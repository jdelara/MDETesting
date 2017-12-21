package anatlyzer.testing.atl.mutators.deletion;

import anatlyzer.atlext.OCL.OclContextDefinition;
import anatlyzer.atlext.OCL.OclFeatureDefinition;
import anatlyzer.testing.mutants.MuMetaModel;

public class HelperContextDeletionMutator extends AbstractDeletionMutator {

	@Override
	public void generateMutants(anatlyzer.atl.model.ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {		
		this.genericDeletion(wrapper, OclFeatureDefinition.class, OclContextDefinition.class, "context_");
	}

	@Override
	public String getDescription() {
		return "Deletion of Context";
	}	
}
