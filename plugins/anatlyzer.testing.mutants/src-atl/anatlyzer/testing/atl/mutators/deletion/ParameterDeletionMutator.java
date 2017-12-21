package anatlyzer.testing.atl.mutators.deletion;

import anatlyzer.atlext.ATL.CalledRule;
import anatlyzer.atlext.OCL.Operation;
import anatlyzer.atlext.OCL.Parameter;
import anatlyzer.testing.mutants.MuMetaModel;

public class ParameterDeletionMutator extends AbstractDeletionMutator {

	@Override
	public void generateMutants(anatlyzer.atl.model.ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {		
		// deletion of parameters in helpers
		this.genericDeletion(wrapper, Operation.class, Parameter.class, "parameters");
		// deletion of parameters in called rules
		this.genericDeletion(wrapper, CalledRule.class, Parameter.class, "parameters");
	}

	@Override
	public String getDescription() {
		return "Deletion of Parameter";
	}	
}