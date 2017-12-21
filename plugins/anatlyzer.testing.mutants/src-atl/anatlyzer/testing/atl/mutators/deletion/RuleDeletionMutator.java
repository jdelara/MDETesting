package anatlyzer.testing.atl.mutators.deletion;

import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.ATL.Rule;
import anatlyzer.testing.mutants.MuMetaModel;

public class RuleDeletionMutator extends AbstractDeletionMutator {

	@Override
	public void generateMutants(anatlyzer.atl.model.ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {		
		this.genericDeletion(wrapper, Module.class, Rule.class, "elements");
	}

	@Override
	public String getDescription() {
		return "Deletion of Rule";
	}	
}
