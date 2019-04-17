package anatlyzer.testing.atl.syntactic.mutators.rules;

import anatlyzer.atlext.ATL.MatchedRule;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.testing.atl.mutators.deletion.AbstractDeletionMutator;
import anatlyzer.testing.mutants.MuMetaModel;

public class MatchedRuleDeletion extends AbstractDeletionMutator {
	@Override
	public void generateMutants(anatlyzer.atl.model.ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {		
		this.genericDeletion(wrapper, Module.class, MatchedRule.class, "elements");
	}

	@Override
	public String getDescription() {
		return "Matched Rule Deletion";
	}	
}
