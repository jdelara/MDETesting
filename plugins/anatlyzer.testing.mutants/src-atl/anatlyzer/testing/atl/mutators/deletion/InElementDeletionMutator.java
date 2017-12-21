package anatlyzer.testing.atl.mutators.deletion;

import anatlyzer.atlext.ATL.InPattern;
import anatlyzer.atlext.ATL.PatternElement;
import anatlyzer.testing.mutants.MuMetaModel;

public class InElementDeletionMutator extends AbstractDeletionMutator {

	@Override
	public void generateMutants(anatlyzer.atl.model.ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {		
		this.genericDeletion(wrapper, InPattern.class, PatternElement.class, "elements");
	}

	@Override
	public String getDescription() {
		return "Deletion of InPattern Element";
	}	
}

