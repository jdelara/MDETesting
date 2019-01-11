package anatlyzer.testing.ocl.mutators;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.m2m.atl.core.emf.EMFModel;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.Helper;
import anatlyzer.atlext.ATL.LocatedElement;

public abstract class AbstractChangeStringValue extends AbstractMutator {

	protected <ToModify extends LocatedElement> void generateMutants(ATLModel wrapper, Class<ToModify> ToModifyClass, String featureName) { generateMutants(wrapper, ToModifyClass, featureName, false); }
	protected <ToModify extends LocatedElement> void generateMutants(ATLModel wrapper, Class<ToModify> ToModifyClass, String featureName, boolean ensureBinary) {
		// Changes the value of the received feature, assigning another compatible value.
		// It expect a mono-valued string feature.

		EDataTypeEList<String> comments   = getModuleComments(wrapper);
		List<ToModify>         modifiable = (List<ToModify>)wrapper.allObjectsOf(ToModifyClass);
		List<String>           values     = getValues(); 
		
		for (ToModify element : modifiable) {
			EObject            original_element = element;
			EStructuralFeature feature          = original_element.eClass().getEStructuralFeature(featureName);
			EStructuralFeature feature_args     = original_element.eClass().getEStructuralFeature("arguments");
			String             original_value   = original_element.eGet(feature).toString(); 
			// if the element has an admissible value
			if (values.contains(original_value)) {
				// ...and it appears in the body of a target invariant...
				LocatedElement exp;
				if ( (exp = needsToBeMutated(element)) != null ) {
					// if necessary, ensure that it is a binary operation
					if (!ensureBinary || feature_args==null || !((List<EObject>)original_element.eGet(feature_args)).isEmpty()) {
						for (String new_value : values) {
							if (!new_value.equals(original_value)) {
								// change value
								original_element.eSet(feature, new_value);
				
								if (comments!=null) comments.add(createComment(exp, element));
					
								// save mutant
								// this.save(atlModel, outputFolder, (Helper)exp);
								
								final EDataTypeEList<String> fComments = comments;
								registerUndo(wrapper, info(element), () -> {
									// restore: remove added binding and comment
									original_element.eSet(feature, original_value);
									if (fComments!=null) fComments.remove(fComments.size()-1);
								});

							}
						}
					}
					// restore operator
					// original_element.eSet(feature, original_value);
				}
			}
		}
	}
	
	/**
	 * List of admissible values for the feature.
	 */
	public abstract List<String> getValues();
}
