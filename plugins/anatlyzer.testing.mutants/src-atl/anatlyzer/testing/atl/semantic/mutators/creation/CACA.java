/**
 * Classes' association creation addition (CACA): This operator adds a useless creation
 * of a relation between two classes of the output model, when the metamodel allows it.
 */

package anatlyzer.testing.atl.semantic.mutators.creation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.ATLFactory;
import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.ATL.OutPatternElement;
import anatlyzer.atlext.ATL.Rule;
import anatlyzer.atlext.OCL.CollectionExp;
import anatlyzer.atlext.OCL.OCLFactory;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.atlext.OCL.VariableDeclaration;
import anatlyzer.atlext.OCL.VariableExp;
import anatlyzer.testing.atl.mutators.creation.BindingCreationMutator;
import anatlyzer.testing.mutants.MuMetaModel;

public class CACA extends BindingCreationMutator {

	@Override
	public String getDescription() {
		return "Classes' association creation addition (CACA)";
	}

	@Override
	protected List<Binding> newbindings(ATLModel wrapper, OutPatternElement outputElement, EClass outputElementType, MuMetaModel outputMM, List<? extends VariableDeclaration> ivariables) {		
		List<Binding> newbindings = new ArrayList<>();
		for (EStructuralFeature feature : outputElementType.getEAllReferences()) {
			// binding for every reference of the output type, no matter whether a binding for the same feature exists
			List<OclExpression> values = getCompatibleValues(wrapper, feature, ivariables);
			for (OclExpression value : values) {
				String propertyName = feature.getName();
				Binding binding     = ATLFactory.eINSTANCE.createBinding();
				binding.setPropertyName(propertyName);
				binding.setValue(value);
				newbindings.add(binding);
			}
		}
		return newbindings;
	}
	
	protected List<OclExpression> getCompatibleValues (ATLModel wrapper, EStructuralFeature feature, List<? extends VariableDeclaration> variables) {
		List<OclExpression> values = new ArrayList<>();
		String featureType = feature.getEType().getName();

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
					values.add(expression);
					break;
				}
			}
		}
		return values;
	}
}
