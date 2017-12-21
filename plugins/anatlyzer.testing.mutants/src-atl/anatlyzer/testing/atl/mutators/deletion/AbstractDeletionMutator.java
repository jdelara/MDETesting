package anatlyzer.testing.atl.mutators.deletion;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.m2m.atl.core.emf.EMFModel;

import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.testing.atl.mutators.AbstractMutator;
import anatlyzer.testing.mutants.MuMetaModel;

public abstract class AbstractDeletionMutator extends AbstractMutator {

	/**
	 * Generic deletion. It allows subtypes of both the container class and the class to delete.
	 * @param atlModel
	 * @param outputFolder
	 * @param ContainerClass container class of the class of objects to delete (example OutPattern)
	 * @param ToDeleteClass class of objects to delete (example Binding)
	 * @param relation containment relation (example bindings)
	 */
	protected <Container extends LocatedElement, ToDelete/* extends LocatedElement*/>	
	void genericDeletion(anatlyzer.atl.model.ATLModel wrapper, Class<Container> ContainerClass, Class<ToDelete> ToDeleteClass, String relation) { 
		genericDeletion(wrapper, ContainerClass, ToDeleteClass, relation, false); 
	}
	
	/**
	 * Generic deletion. It allows subtypes of the class to delete. The parameter 'exactContainerType'
	 * allows configuring whether the type of the container must be exactly the one received, or if
	 * the subtypes should be also considered.  
	 * @param atlModel
	 * @param outputFolder
	 * @param ContainerClass container class of the class of objects to delete (example OutPattern)
	 * @param ToDeleteClass class of objects to delete (example Binding)
	 * @param relation containment relation (example bindings)
	 * @param exactContainerType false to consider also subtypes of the ContainerClass, true to discard subtypes of the ContainerClass  
	 */
	protected <Container extends LocatedElement, ToDelete/* extends LocatedElement*/> 
	void genericDeletion(anatlyzer.atl.model.ATLModel wrapper, Class<Container> ContainerClass, Class<ToDelete> ToDeleteClass, String relation, boolean exactContainerType) {
		List<Container> containers = (List<Container>)wrapper.allObjectsOf(ContainerClass);
		
		// we will add a comment to the module, documenting the mutation 
		Module module = wrapper.getModule();
		EDataTypeEList<String> comments = null;
		if (module!=null) {
			EStructuralFeature feature = wrapper.source(module).eClass().getEStructuralFeature("commentsBefore");	
			comments = (EDataTypeEList<String>)wrapper.source(module).eGet(feature);
		}

		// filter subtypes (only if parameter exactContainerType is true)
		if (exactContainerType) filterSubtypes(containers, ContainerClass);

		for (Container container : containers) {
			EStructuralFeature feature = wrapper.source(container).eClass().getEStructuralFeature(relation);

			if (feature!=null) {

				// CASE 1: monovalued feature .........................................................

				if (feature.getUpperBound() == 1 && feature.getLowerBound() == 0) {
					EObject link = (EObject) wrapper.source(container).eGet(feature);

					// mutation: remove object
					if (link!=null) {
						LocatedElement object = (LocatedElement)wrapper.target(link);
						if (ToDeleteClass.isAssignableFrom(object.getClass())) {
							wrapper.source(container).eSet(feature, null);

							// mutation: documentation
							if (comments!=null) comments.add("\n-- MUTATION \"" + this.getDescription() + "\" " + toString(object) + " in " + toString(container) + " (line " + object.getLocation() + " of original transformation)\n");

							// restore: restore object and remove comment
							final EDataTypeEList<String> fComments = comments;
							registerUndo(wrapper, () -> {
								wrapper.source(container).eSet(feature, link);
								if (fComments!=null) fComments.remove(fComments.size()-1);
							});
							
						}
					}
				}

				// CASE 2: multivalued feature ........................................................

				else {
					List<EObject> link = (List<EObject>)wrapper.source(container).eGet(feature);
					if (feature.getLowerBound() < link.size()) {
						int size = link.size();
						for (int i=0; i<size; i++) { 

							// mutation: remove object
							EObject        eobject = link.get(i);
							LocatedElement object = (LocatedElement)wrapper.target(eobject);
							if (ToDeleteClass.isAssignableFrom(object.getClass())) {
								link.remove(i);

								// mutation: documentation
								if (comments!=null) comments.add("\n-- MUTATION \"" + this.getDescription() + "\" " + toString(object) + " in " + toString(container) + " (line " + object.getLocation() + " of original transformation)\n");

								
								// restore: restore object and remove comment
								final EDataTypeEList<String> fComments = comments;
								final int idx = i;
								registerUndo(wrapper, () -> {
									link.add(idx, eobject);
									if (fComments!=null) fComments.remove(fComments.size()-1);
								});
								
							}
						}
					}
				}
			}
		}
	}
}
