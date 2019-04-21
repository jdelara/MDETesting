package anatlyzer.testing.atl.typing.mutators;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atl.types.Metaclass;
import anatlyzer.atl.types.Type;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.OCL.BooleanExp;
import anatlyzer.atlext.OCL.IfExp;
import anatlyzer.atlext.OCL.NavigationOrAttributeCallExp;
import anatlyzer.atlext.OCL.OCLFactory;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.atlext.OCL.OperationCallExp;
import anatlyzer.atlext.OCL.OperatorCallExp;
import anatlyzer.atlext.OCL.VariableExp;
import anatlyzer.testing.atl.mutators.AbstractMutator;
import anatlyzer.testing.mutants.MuMetaModel;

public class RemoveEnclosingConditional extends AbstractMutator {

	@Override
	public void generateMutants(ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {
		// we will add a comment to the module, documenting the mutation 
		Module module = wrapper.getModule();
		EDataTypeEList<String> comments = null;
		if (module!=null) {
			EStructuralFeature f = module.eClass().getEStructuralFeature("commentsBefore");	
			comments = (EDataTypeEList<String>)module.eGet(f);
		}

		// store mutated ifs to avoid generating duplicate mutants
		List<IfExp> truecondition  = new ArrayList<>();
		List<IfExp> falsecondition = new ArrayList<>();
		OclExpression branch = null;
		
		// look for navigation expression that traverses an optional feature
		for (VariableExp variable : (List<VariableExp>)wrapper.allObjectsOf(VariableExp.class)) {			
			if (applyMutation(inputMM, variable)) {
				if ((branch = getEnclosingConditionalBranch(variable)) != null) {
					// make if-condition=true  if the expression is in the then-branch, or
					// make if-condition=false if the expression is in the else-branch.
					IfExp ifexp = (IfExp)branch.eContainer();
					OclExpression ifcondition  = ifexp.getCondition();
					BooleanExp    newcondition = OCLFactory.eINSTANCE.createBooleanExp();
					newcondition.setBooleanSymbol(branch == ifexp.getThenExpression());
					if ( (newcondition.isBooleanSymbol()  && !truecondition.contains(ifexp)) ||
						 (!newcondition.isBooleanSymbol() && !falsecondition.contains(ifexp)) ) {
						// mutate
						ifexp.setCondition(newcondition);
					
						// document
						if (comments!=null) comments.add("\n-- MUTATION \"" + this.getDescription() + "\" condition of 'if' changed to " + newcondition.isBooleanSymbol() + " (line " + ((LocatedElement)ifexp).getLocation() + " of original transformation)\n");
					
						// undo mutation
						final EDataTypeEList<String> fComments = comments;
						registerUndo(wrapper, change(ifexp), () -> {
							if (fComments!=null) fComments.remove(fComments.size()-1);
							ifexp.setCondition(ifcondition);
						});
					
						// store mutation to avoid duplicate mutants
						if (newcondition.isBooleanSymbol()) 
							truecondition.add(ifexp);
						else falsecondition.add(ifexp);
					}
				}
			}
		}
	}
	
	// the mutation is applied if the navigation expression that starts in the variable 
	// traverses an optional feature, and this optional feature is not the last one in
	// the expression.
	protected boolean applyMutation (MuMetaModel metamodel, VariableExp variable) {
		boolean apply  = false;
		EClass  source = getClass(metamodel, variable.getReferredVariable().getInferredType());
		EObject lastNavigation = variable.eContainer();
		
		// case 1: optional feature in the middle of a navigation expression
		while (!apply && 
			   lastNavigation instanceof NavigationOrAttributeCallExp &&
			   lastNavigation.eContainer() instanceof NavigationOrAttributeCallExp) {
			if (source != null) {
				String property = ((NavigationOrAttributeCallExp)lastNavigation).getName();
				EStructuralFeature sproperty = source.getEStructuralFeature(property);
				if (sproperty != null) {
					apply  = sproperty.getLowerBound()==0 && sproperty.getUpperBound()==1;
					source = sproperty.getEType() instanceof EClass? (EClass)sproperty.getEType() : null; 
				}
			}
			// next step in navigation expression
			if (source == null) source = getClass(metamodel, ((NavigationOrAttributeCallExp)lastNavigation).getInferredType());
			lastNavigation = lastNavigation.eContainer();
		}
		
		// case 2: call to operation on an optional feature
		if (!apply &&
			lastNavigation instanceof NavigationOrAttributeCallExp &&
			lastNavigation.eContainer() instanceof OperationCallExp &&
			!(lastNavigation.eContainer() instanceof OperatorCallExp) &&
			!((OperationCallExp)lastNavigation.eContainer()).getOperationName().equals("oclIsUndefined")) {
			if (source != null) {
				String property = ((NavigationOrAttributeCallExp)lastNavigation).getName();
				EStructuralFeature sproperty = source.getEStructuralFeature(property);
				apply = sproperty != null && sproperty.getLowerBound()==0 && sproperty.getUpperBound()==1;
			}
		}
		
		return apply;
	}
	
	// 
	protected OclExpression getEnclosingConditionalBranch (VariableExp variable) {
		EObject expression = variable.eContainer();
		while (expression != null && !(expression.eContainer() instanceof IfExp))
				expression = expression.eContainer();
		return expression instanceof OclExpression? (OclExpression)expression : null;
	}

	//	
	protected EClass getClass (MuMetaModel metamodel, Type type) {
		if (type instanceof Metaclass) {
			EClassifier classif = metamodel.getEClassifier(((Metaclass)type).getName());
			return classif instanceof EClass? (EClass)classif : null;
		}
		return null;
	}

	@Override
	public String getDescription() {
		return "RemoveEnclosingConditional";
	}
}
