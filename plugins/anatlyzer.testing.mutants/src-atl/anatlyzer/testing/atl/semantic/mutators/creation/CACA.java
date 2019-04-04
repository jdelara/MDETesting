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
import anatlyzer.atlext.OCL.OCLFactory;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.atlext.OCL.SequenceExp;
import anatlyzer.atlext.OCL.SetExp;
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
			OclExpression value = getCompatibleValue(wrapper, feature, ivariables);
			if (value==null) continue;
			String propertyName = feature.getName();
			Binding binding     = ATLFactory.eINSTANCE.createBinding();
			binding.setPropertyName( propertyName );
			binding.setValue( getCompatibleValue(wrapper, feature, ivariables) );
			newbindings.add(binding);
		}
		return newbindings;
	}
	
	// TO-DO: return list of compatible values, not just the first one
	protected OclExpression getCompatibleValue (ATLModel wrapper, EStructuralFeature feature, List<? extends VariableDeclaration> variables) {
		OclExpression expression = null;
		String        type       = feature.getEType().getName();

		// for each input variable
		for (int i=0; i<variables.size(); i++) {
			
			// obtain rule with input=variable.type and output=type
			for (Rule rule : (List<Rule>)wrapper.allObjectsOf(Rule.class)) {
				List<? extends VariableDeclaration> othervariables = getVariableDeclarations(rule);
				if (!rule.getOutPattern().getElements().isEmpty() &&
					!othervariables.isEmpty() &&						
					rule.getOutPattern().getElements().get(0).getType().getName().equals(type) &&
					othervariables.get(0).getType().getName().equals(variables.get(i).getType().getName())) {
					
					// compatible value found: create variable expression
					System.out.println("-------");
					expression = OCLFactory.eINSTANCE.createVariableExp();
					((VariableExp)expression).setReferredVariable(variables.get(i));
					
					// multivalued feature: add variable to collection
					if (feature.getUpperBound()!=1) {
						VariableExp aux = (VariableExp)expression;
						if (feature.isOrdered()) {
							expression = OCLFactory.eINSTANCE.createSequenceExp();
							((SequenceExp)expression).getElements().add(aux);
						}
						else {
							expression = OCLFactory.eINSTANCE.createSetExp();
							((SetExp)expression).getElements().add(aux);
						}
					}
					return expression;
				}
			}
		}
		return expression;
	}
}
