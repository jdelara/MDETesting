package anatlyzer.testing.ocl.mutators;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.m2m.atl.core.emf.EMFModel;

import witness.generator.MetaModel;
import anatlyzer.atlext.ATL.Helper;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.testing.mutants.MuMetaModel;

/**
 * Deletes a constraint represented as an ATL helper, probably with some kind of annotation  
 */
public class MCO extends AbstractMutator {

	@Override
	public void generateMutants(anatlyzer.atl.model.ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {

		EDataTypeEList<String> comments = getModuleComments(wrapper);
		List<Helper>           helpers  = (List<Helper>)wrapper.allObjectsOf(Helper.class);
		
		for (Helper helper : helpers) {
			// if the helper is a target invariant...
			if ( needsToBeMutated(helper) != null ) {				
				EObject original_helper = helper;
				EObject container       = helper.eContainer();
				EStructuralFeature feature_container = null;
				if (helper.eContainer() instanceof Module && ((Module)helper.eContainer()).getElements().contains(helper)) {
					feature_container = container.eClass().getEStructuralFeature("elements");
				}
				if (feature_container!=null) {
					// remove helper
					List<EObject> elements = (List<EObject>) container.eGet(feature_container);
					int index = elements.indexOf(original_helper);
					elements.remove(original_helper);
				
					// save mutant
					if (comments!=null) comments.add(createComment(helper, helper));
					
					//this.save(atlModel, outputFolder, helper);
					// if (comments!=null) comments.remove(comments.size()-1);
				
					final EDataTypeEList<String> fComments = comments;
					registerUndo(wrapper, remove(helper), () -> {
						if (fComments!=null) fComments.remove(fComments.size()-1);
						// restore helper
						elements.add(index, original_helper);
					});				
					
				}
				else System.err.println("--> KO "+getDescription()+": "+helper);
			}	
		}
	}
	
	@Override
	public String getDescription() {
		return "MCO";
	}	
}
