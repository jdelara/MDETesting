package anatlyzer.testing.atl.mutators.deletion;

import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.atlext.OCL.OperationCallExp;
import anatlyzer.testing.mutants.MuMetaModel;

public class ArgumentDeletionMutator extends AbstractDeletionMutator {

	@Override
	public void generateMutants(anatlyzer.atl.model.ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {		
		// deletion of arguments in calls to called rules and helpers
		this.genericDeletion(wrapper, OperationCallExp.class, OclExpression.class, "arguments", true);
	}

	@Override
	public String getDescription() {
		return "Deletion of Argument";
	}	
}
