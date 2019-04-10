/**
 * Collection filtering change with addition (CFCA): this operator processes a useless filtering
 * in a collection. As this could return an infinite number of mutants, we choose to filter a 
 * collection without filters, by returning one of its elements arbitrarily chosen.
 */

package anatlyzer.testing.atl.semantic.mutators.filtering;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atl.types.impl.MetaclassImpl;
import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.OCL.NavigationOrAttributeCallExp;
import anatlyzer.atlext.OCL.OCLFactory;
import anatlyzer.atlext.OCL.OperationCallExp;
import anatlyzer.atlext.OCL.impl.IntegerExpImpl;
import anatlyzer.testing.atl.mutators.modification.feature.NavigationModificationMutator;
import anatlyzer.testing.mutants.MuMetaModel;

public class CFCA extends NavigationModificationMutator {

	@Override
	public void generateMutants(ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {
		List<NavigationOrAttributeCallExp> properties = (List<NavigationOrAttributeCallExp>)wrapper.allObjectsOf(NavigationOrAttributeCallExp.class);
		
		// we will add a comment to the module, documenting the mutation 
		Module module = wrapper.getModule();
		EDataTypeEList<String> comments = null;
		if (module!=null) {
			EStructuralFeature f = module.eClass().getEStructuralFeature("commentsBefore");	
			comments = (EDataTypeEList<String>)module.eGet(f);
		}
		
		for (NavigationOrAttributeCallExp property : properties) {
			EObject receptorType = property.getReceptorType();
			if (receptorType instanceof MetaclassImpl) {
				final EObject root = property.eContainer();
				if (root instanceof OperationCallExp || root instanceof Binding) {				
					EClass             mmsource  = ((MetaclassImpl)receptorType).getKlass();
					EStructuralFeature mmfeature = mmsource.getEStructuralFeature(property.getName());
					if (mmfeature!=null && mmfeature.getUpperBound()!=1) { // collection
						if ( root instanceof Binding || 
							((OperationCallExp)root).getSource()==property ||
							(((OperationCallExp)root).getArguments().size()>0 && ((OperationCallExp)root).getArguments().get(0)==property)) {
							
							// create filter expression ".asSequence().subSequence(1,1)"
							OperationCallExp asSequence  = asSequenceSubSequence();
							OperationCallExp subSequence = subSequence();
					    	
					    	// add filter expression
							if (root instanceof Binding) 
								((Binding)root).setValue(subSequence);
							else if (((OperationCallExp)root).getSource()==property)
								((OperationCallExp)root).setSource(subSequence);
							else if (((OperationCallExp)root).getArguments().size()>0 && ((OperationCallExp)root).getArguments().get(0)==property) 
								((OperationCallExp)root).getArguments().set(0, subSequence);
						
							asSequence.setSource(property);
							
							// mutation: documentation
							if (comments!=null) comments.add("\n-- MUTATION \"" + this.getDescription() + "\" added filter '.asSequence().subSequence(1,1)' after " + toString(property) + " (line " + ((LocatedElement)property).getLocation() + " of original transformation)\n");

							// restore original value
							final EDataTypeEList<String> fComments = comments;
							registerUndo(wrapper, change((LocatedElement) root), () -> {
								// remove comment
								if (fComments!=null) fComments.remove(fComments.size()-1);
								// undo changes
								if (root instanceof Binding)
									((Binding)root).setValue(property);
								else if (((OperationCallExp)root).getSource()==subSequence)
									((OperationCallExp)root).setSource(property);
								else if (((OperationCallExp)root).getArguments().size()>0 && ((OperationCallExp)root).getArguments().get(0)==subSequence)
									((OperationCallExp)root).getArguments().set(0, property);
								asSequence.setSource(null);
							});
						}
					}
				}
			}
		}
	}

	@Override
	public String getDescription() {
		return "Collection filtering change with addition (CFCA)";
	}
	
	//
	private   OperationCallExp asSequenceSubSequence = null;
	private   OperationCallExp subSequence           = null;
	protected OperationCallExp asSequenceSubSequence () {
		if (asSequenceSubSequence==null) {
			asSequenceSubSequence = OCLFactory.eINSTANCE.createOperationCallExp();
			subSequence           = OCLFactory.eINSTANCE.createOperationCallExp();
			asSequenceSubSequence.setOperationName ("asSequence");
			subSequence.setOperationName("subSequence");
			subSequence.setSource(asSequenceSubSequence);
			IntegerExpImpl index1 = (IntegerExpImpl)OCLFactory.eINSTANCE.createIntegerExp();
			IntegerExpImpl index2 = (IntegerExpImpl)OCLFactory.eINSTANCE.createIntegerExp();
			index1.setIntegerSymbol(1);
			index2.setIntegerSymbol(1);
			subSequence.getArguments().add(index1);
			subSequence.getArguments().add(index2);
		}
		return asSequenceSubSequence;
	}
	
	protected OperationCallExp subSequence() {
		if (subSequence==null) asSequenceSubSequence();
		return subSequence;
	}
}
