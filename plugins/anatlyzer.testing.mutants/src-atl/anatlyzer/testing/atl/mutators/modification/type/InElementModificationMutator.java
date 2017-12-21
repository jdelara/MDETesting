package anatlyzer.testing.atl.mutators.modification.type;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.InPatternElement;
import anatlyzer.testing.mutants.MuMetaModel;

public class InElementModificationMutator extends AbstractTypeModificationMutator {
	
	@Override
	public void generateMutants(ATLModel atlModel, MuMetaModel inputMM, MuMetaModel outputMM) {
		this.genericTypeModification(atlModel, InPatternElement.class, "type", new MuMetaModel[] {inputMM});
}
	
	@Override
	public String getDescription() {
		return "InPattern Element Modification";
	}
}
