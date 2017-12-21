package anatlyzer.testing.atl.mutators.modification.type;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.OutPatternElement;
import anatlyzer.testing.mutants.MuMetaModel;

public class OutElementModificationMutator extends AbstractTypeModificationMutator {
	
	@Override
	public void generateMutants(ATLModel atlModel, MuMetaModel inputMM, MuMetaModel outputMM) {
		this.genericTypeModification(atlModel, OutPatternElement.class, "type", new MuMetaModel[] {outputMM});
	}
	
	@Override
	public String getDescription() {
		return "OutPattern Element Modification";
	}
}
