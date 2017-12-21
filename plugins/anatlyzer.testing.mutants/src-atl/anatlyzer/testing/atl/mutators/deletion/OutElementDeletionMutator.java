package anatlyzer.testing.atl.mutators.deletion;

import anatlyzer.atlext.ATL.OutPattern;
import anatlyzer.atlext.ATL.PatternElement;
import anatlyzer.testing.mutants.MuMetaModel;

public class OutElementDeletionMutator extends AbstractDeletionMutator {

	@Override
	public void generateMutants(anatlyzer.atl.model.ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {		
		this.genericDeletion(wrapper, OutPattern.class, PatternElement.class, "elements");
	}

	@Override
	public String getDescription() {
		return "Deletion of OutPattern Element";
	}	
}

