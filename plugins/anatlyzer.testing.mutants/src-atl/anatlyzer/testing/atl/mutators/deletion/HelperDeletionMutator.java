package anatlyzer.testing.atl.mutators.deletion;

import anatlyzer.atlext.ATL.Helper;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.testing.mutants.MuMetaModel;

public class HelperDeletionMutator extends AbstractDeletionMutator {

	@Override
	public void generateMutants(anatlyzer.atl.model.ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {		
		this.genericDeletion(wrapper, Module.class, Helper.class, "elements");
	}

	@Override
	public String getDescription() {
		return "Deletion of Helper";
	}	
}
