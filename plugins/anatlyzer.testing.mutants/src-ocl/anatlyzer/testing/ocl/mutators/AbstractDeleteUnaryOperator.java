package anatlyzer.testing.ocl.mutators;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.m2m.atl.core.emf.EMFModel;

import witness.generator.MetaModel;
import anatlyzer.atlext.ATL.Helper;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.OCL.LoopExp;
import anatlyzer.atlext.OCL.Operation;
import anatlyzer.atlext.OCL.OperationCallExp;
import anatlyzer.atlext.OCL.OperatorCallExp;
import anatlyzer.atlext.OCL.PropertyCallExp;
import anatlyzer.testing.mutants.MuMetaModel;

public abstract class AbstractDeleteUnaryOperator extends AbstractMutator {

	@Override
	public void generateMutants(anatlyzer.atl.model.ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {

		EDataTypeEList<String> comments  = getModuleComments(wrapper);
		List<OperatorCallExp>  operators = (List<OperatorCallExp>)wrapper.allObjectsOf(OperatorCallExp.class);
		
		for (OperatorCallExp operator : operators) {
			// if the operator is the unary targeted operated...
			if (operator.getOperationName().equals(getUnaryOperator()) && operator.getArguments().isEmpty()) {
				// ...and it appears in the body of a target invariant...
				LocatedElement exp;
				if ( (exp = needsToBeMutated(operator)) != null ) {
					EObject not       = operator;
					EObject container = operator.eContainer();
					EObject content   = operator.getSource();
					EStructuralFeature feature_not       = not.eClass().getEStructuralFeature("source");
					EStructuralFeature feature_container = null;
					if ((operator.eContainer() instanceof Operation && ((Operation)operator.eContainer()).getBody() == operator) || 
						(operator.eContainer() instanceof LoopExp   && ((LoopExp)  operator.eContainer()).getBody() == operator)) {
						feature_container = container.eClass().getEStructuralFeature("body");
					}
					else if (operator.eContainer() instanceof OperationCallExp && ((OperationCallExp)operator.eContainer()).getArguments().contains(operator)) {
						feature_container = container.eClass().getEStructuralFeature("arguments");
					}
					else if (operator.eContainer() instanceof PropertyCallExp && ((PropertyCallExp)operator.eContainer()).getSource() == operator) {
						feature_container = feature_not;
					}					
					if (feature_container!=null) {
						EStructuralFeature fFeature_container = feature_container;
						
						// remove "not"
						set(container, fFeature_container, content);
				
						// annotate mutant
						if (comments!=null) comments.add(createComment((Helper)exp, operator));
						
						//this.save(atlModel, outputFolder, (Helper)exp);
						
						final EDataTypeEList<String> fComments = comments;
						registerUndo(wrapper, info(operator), () -> {
							// restore "not"
							set(container, fFeature_container, not);
							set(not, feature_not, content);
							if (fComments!=null) fComments.remove(fComments.size()-1);
						});				
					}
					else System.err.println("--> KO "+getDescription()+": "+operator.eContainer());
				}
			}
		}
	}
	
	// set feature
	@SuppressWarnings("unchecked")
	private void set (EObject object, EStructuralFeature feature, EObject value) {
		if (feature.getUpperBound()==1) 
			object.eSet(feature, value);
		else {
			List<EObject> list = (List<EObject>)object.eGet(feature);
			list.clear();
			list.add(value);
		}
	}
	
	/**
	 * Operator to be deleted.
	 */
	public abstract String getUnaryOperator();	
}
