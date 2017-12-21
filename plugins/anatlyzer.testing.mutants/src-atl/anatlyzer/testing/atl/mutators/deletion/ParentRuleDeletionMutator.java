package anatlyzer.testing.atl.mutators.deletion;

import anatlyzer.atlext.ATL.MatchedRule;
import anatlyzer.testing.mutants.MuMetaModel;

public class ParentRuleDeletionMutator extends AbstractDeletionMutator {

	@Override
	public void generateMutants(anatlyzer.atl.model.ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {		
		this.genericDeletion(wrapper, MatchedRule.class, MatchedRule.class, "children");
	}

	@Override
	public String getDescription() {
		return "Deletion of Parent Rule";
	}	
}
