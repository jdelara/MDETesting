package anatlyzer.testing.atl.mutators.modification;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;

import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.testing.atl.mutators.AbstractMutator;
import anatlyzer.testing.mutants.MuMetaModel;

public abstract class AbstractModificationMutator extends AbstractMutator {

	/**
	 * It receives the object to modify, and the value of the attribute to modify.
	 * It returns a set of valid replacements for the attribute value. 
	 * To be implemented only by subclasses that call to genericAttributeModification.
	 * @param object2modify
	 * @param currentAttributeValue
	 * @param metamodel
	 * @return set of valid replacements for the attribute value.
	 */
	protected abstract List<Object> replacements(EObject object2modify, String currentAttributeValue, MuMetaModel metamodel);

	/**
	 * Generic modification. It allows subtypes of the class to modify.
	 * @param atlModel
	 * @param outputFolder
	 * @param ToModifyClass class of objects to modify (example Binding)
	 * @param featureName feature to modify (example propertyName)
	 * @param metamodel metamodel containing the candidate types for the modification   
	 */ 
	protected <ToModify extends LocatedElement> 
	void genericAttributeModification(anatlyzer.atl.model.ATLModel wrapper, Class<ToModify> ToModifyClass, String feature, MuMetaModel metamodel) {

		genericAttributeModification(wrapper, ToModifyClass, feature, metamodel, false);
	}
	
	/**
	 * Generic modification. The parameter 'exactContainerType' allows configuring whether the type of  
	 * the class to modify must be exactly the one received, or if the subtypes should be also considered.  
	 * @param atlModel
	 * @param outputFolder
	 * @param ToModifyClass class of objects to modify (example Binding)
	 * @param featureName feature to modify (example propertyName)
	 * @param metamodel metamodel containing the candidate types for the modification   
	 * @param exactToModifyType false to consider also subtypes of the class ToModify, true to discard subtypes of the class ToModify  
	 */ 
	protected <ToModify extends LocatedElement> 
	void genericAttributeModification(anatlyzer.atl.model.ATLModel wrapper, Class<ToModify> ToModifyClass, String feature, MuMetaModel metamodel, boolean exactToModifyType) {
		List<ToModify> modifiable = (List<ToModify>)wrapper.allObjectsOf(ToModifyClass);

		// we will add a comment to the module, documenting the mutation 
		Module module = wrapper.getModule();
		EDataTypeEList<String> comments = null;
		if (module!=null) {
			EStructuralFeature f = module.eClass().getEStructuralFeature("commentsBefore");	
			comments = (EDataTypeEList<String>)module.eGet(f);
		}
		
		// filter subtypes (only if parameter exactToModifyType is true)
		if (exactToModifyType) filterSubtypes(modifiable, ToModifyClass);
		
		for (ToModify object2modify : modifiable) {
			EStructuralFeature featureDefinition = object2modify.eClass().getEStructuralFeature(feature);
			
			if (featureDefinition!=null && featureDefinition.getUpperBound() == 1) {
				EObject object2modify_src = object2modify;			
				Object oldFeatureValue = object2modify_src.eGet(featureDefinition); 

				List<Object> replacements = this.replacements(object2modify, oldFeatureValue.toString(), metamodel);
				for (Object replacement : replacements) {
					if (replacement!=null) {	
						object2modify.eSet(featureDefinition, replacement);
					
						// mutation: documentation
						if (comments!=null) comments.add("\n-- MUTATION \"" + this.getDescription() + "\" from " + oldFeatureValue.toString() + " to " + replacement  + " (line " + object2modify.getLocation() + " of original transformation)\n");

						// restore original value					
						final EDataTypeEList<String> fComments = comments;
						registerUndo(wrapper, () -> {
							object2modify_src.eSet(featureDefinition, oldFeatureValue);
							if (fComments!=null) fComments.remove(fComments.size()-1);
						});						
					}
				}
				
				// restore original value
				// This is done now within the loop, to have only one restoration point
				// object2modify_src.eSet(featureDefinition, oldFeatureValue);					
			}
		}
	}
}
