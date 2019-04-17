package anatlyzer.testing.atl.syntactic.mutators.rules;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.MatchedRule;
import anatlyzer.testing.atl.mutators.modification.AbstractModificationMutator;
import anatlyzer.testing.mutants.MuMetaModel;

public class MatchedRuleNameChange extends AbstractModificationMutator {

	@Override
	public void generateMutants(ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {
		this.genericAttributeModification(wrapper, MatchedRule.class, "name", null);
	}

	@Override
	protected List<Object> replacements(EObject object2modify, String currentAttributeValue, MuMetaModel metamodel) {
		return Collections.singletonList(currentAttributeValue+"_MUTATED");
	}

	@Override
	public String getDescription() {
		return "Matched Rule Name Change";
	}
}
