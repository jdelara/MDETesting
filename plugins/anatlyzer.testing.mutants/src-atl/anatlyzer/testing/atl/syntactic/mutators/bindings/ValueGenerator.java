package anatlyzer.testing.atl.syntactic.mutators.bindings;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.Rule;
import anatlyzer.atlext.ATL.RuleWithPattern;
import anatlyzer.atlext.OCL.BooleanExp;
import anatlyzer.atlext.OCL.CollectionExp;
import anatlyzer.atlext.OCL.IntegerExp;
import anatlyzer.atlext.OCL.OCLFactory;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.atlext.OCL.RealExp;
import anatlyzer.atlext.OCL.StringExp;
import anatlyzer.atlext.OCL.VariableDeclaration;
import anatlyzer.atlext.OCL.VariableExp;
import anatlyzer.testing.mutants.EMFUtils;

public class ValueGenerator {
	
	/**
	 * It returns a compatible ocl expression for the received feature.
	 * @param ATLModel wrapper
	 * @param feature
	 * @param variables (used when the type is not primitive)
	 */
	public static OclExpression getCompatibleValue (ATLModel wrapper, EStructuralFeature feature, List<? extends VariableDeclaration> variables) {
		String featureType = feature.getEType().getName();
		
		// binding of reference (non-primitive type)
		if (feature instanceof EReference) 
		{

			// for each input variable:
			for (VariableDeclaration variable : variables) {
			
				// if there is a rule with from=variable.type and to=feature.type...
				for (Rule rule : (List<Rule>)wrapper.allObjectsOf(Rule.class)) {
					List<? extends VariableDeclaration> ruleInputTypes = getVariableDeclarations(rule);
					String ruleInputType  = ruleInputTypes.isEmpty()? "" : ruleInputTypes.get(0).getType().getName();
					String ruleOutputType = rule.getOutPattern().getElements().isEmpty()? "" : rule.getOutPattern().getElements().get(0).getType().getName();				
					if (ruleInputType.equals(variable.getType().getName()) && ruleOutputType.equals(featureType)) {
					
						// ...then the variable is compatible with the feature
						OclExpression expression = OCLFactory.eINSTANCE.createVariableExp();
						((VariableExp)expression).setReferredVariable(variable);
						if (feature.getUpperBound()!=1) {
							VariableExp aux = (VariableExp)expression;
							expression = feature.isOrdered()? OCLFactory.eINSTANCE.createSequenceExp() : OCLFactory.eINSTANCE.createSetExp();
							((CollectionExp)expression).getElements().add(aux);
						}
						return expression; 
					}
				}
			}	
		}

		// binding of attribute (primitive type)
		OclExpression expression = null;
		if (feature.getUpperBound()==1) {
			if (EMFUtils.isString(featureType)) {
				expression = OCLFactory.eINSTANCE.createStringExp();
				((StringExp)expression).setStringSymbol("dummy");
			}
			else if (EMFUtils.isInteger(featureType)) {
				expression = OCLFactory.eINSTANCE.createIntegerExp();
				((IntegerExp)expression).setIntegerSymbol(0);
			}
			else if (EMFUtils.isBoolean(featureType)) {
				expression = OCLFactory.eINSTANCE.createBooleanExp();
				((BooleanExp)expression).setBooleanSymbol(false);
			}
			else if (EMFUtils.isFloating(featureType)) {
				expression = OCLFactory.eINSTANCE.createRealExp();
				((RealExp)expression).setRealSymbol(0);
			}
			else {
				expression = OCLFactory.eINSTANCE.createVariableExp();
				if (variables.size()>0) {
					((VariableExp)expression).setReferredVariable(variables.get(0));
				} else {
					throw new IllegalStateException();
				}
			}
		}		
		else expression = feature.isOrdered()? OCLFactory.eINSTANCE.createSequenceExp() : OCLFactory.eINSTANCE.createSetExp();						
		return expression;
	}
	
	/**
	 * It returns the list of avriable declarations in a rule.
	 * @param rule
	 */
	public static List<? extends VariableDeclaration> getVariableDeclarations (Rule rule) {
		if (rule instanceof RuleWithPattern && ((RuleWithPattern)rule).getInPattern()!=null) 
			return ((RuleWithPattern)rule).getInPattern().getElements();
		if (rule.getVariables() != null) 
			return rule.getVariables();
		return new ArrayList<>();                 
	}
}
