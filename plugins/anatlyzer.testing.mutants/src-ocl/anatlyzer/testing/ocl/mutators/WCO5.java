package anatlyzer.testing.ocl.mutators;

import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.util.EcoreUtil;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.Helper;
import anatlyzer.atlext.OCL.Attribute;
import anatlyzer.atlext.OCL.OclFeature;
import anatlyzer.atlext.OCL.Operation;
import anatlyzer.testing.mutants.MuMetaModel;

// Changes the constraint by adding the conditional operator "not"
public class WCO5 extends AbstractMutator {

	@Override
	public void generateMutants(ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {
		EDataTypeEList<String> comments = getModuleComments(wrapper);
		List<Helper>           helpers  = (List<Helper>)wrapper.allObjectsOf(Helper.class);
		
		for (Helper helper : helpers) {
			// if the helper is a target invariant...
			if ( needsToBeMutated(helper) != null ) {
				
				// add "not" to constraint
				EClass             clazz               = getOperatorCallExp(wrapper.source(helper).eClass().eResource());
				OclFeature         oclfeature          = helper.getDefinition().getFeature();
				EObject            original_oclfeature = wrapper.source(oclfeature);
				EStructuralFeature feature             = null;
				EObject            original_constraint = null;
				if (oclfeature instanceof Operation) {
					original_constraint = wrapper.source( ((Operation)oclfeature).getBody() );
					feature             = original_oclfeature.eClass().getEStructuralFeature("body");
				}
				else if (oclfeature instanceof Attribute) {
					original_constraint = wrapper.source( ((Attribute)oclfeature).getInitExpression() );
					feature             = original_oclfeature.eClass().getEStructuralFeature("initExpression");
				}
				if (original_constraint != null) {
					EObject not = EcoreUtil.create(clazz);
					not.eSet(clazz.getEStructuralFeature("operationName"), "not");
					not.eSet(clazz.getEStructuralFeature("source"), original_constraint);				
					original_oclfeature.eSet(feature, not);
				
					// save mutant
					if (comments!=null) comments.add(createComment(helper, helper));
					// this.save(atlModel, outputFolder, helper);
					
					final EStructuralFeature fFeature = feature;
					final EDataTypeEList<String> fComments = comments;
					registerUndo(wrapper, info(helper), () -> {
						if (fComments!=null) fComments.remove(fComments.size()-1);
						// remove "not" from constraint
						original_oclfeature.eSet(fFeature, not.eGet(clazz.getEStructuralFeature("source")));
					});				
					
				
				}
				else System.err.println("--> KO "+getDescription()+": "+helper);
			}	
		}
	}
	
	@Override
	public String getDescription() {
		return "WCO5";
	}
	
	/**
	 * It returns the class OperatorCallExp stored in the received resource.
	 */
	private EClass getOperatorCallExp (Resource resource) {
		EClass clazz = null;
		TreeIterator<Object> contents = EcoreUtil.getAllContents(resource, true);
		while (contents.hasNext() && clazz==null) {
			Object next = contents.next();
			if (next instanceof EClass && ((EClass)next).getName().equals("OperatorCallExp")) {
				clazz = (EClass)next;
			}
		}
		return clazz;
	}
}
