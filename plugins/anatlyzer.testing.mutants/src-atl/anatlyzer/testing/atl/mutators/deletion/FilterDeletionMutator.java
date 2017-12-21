package anatlyzer.testing.atl.mutators.deletion;

import anatlyzer.atlext.ATL.InPattern;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.testing.mutants.MuMetaModel;

public class FilterDeletionMutator extends AbstractDeletionMutator {

	@Override
	public void generateMutants(anatlyzer.atl.model.ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {		
		this.genericDeletion(wrapper, InPattern.class, OclExpression.class, "filter");
	}

	@Override
	public String getDescription() {
		return "Deletion of Filter";
	}	
}
