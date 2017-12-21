package anatlyzer.testing.atl.mutators.modification.type;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.OCL.VariableDeclaration;
import anatlyzer.testing.mutants.MuMetaModel;

public class VariableModificationMutator extends AbstractTypeModificationMutator {
	
	@Override
	public void generateMutants(ATLModel atlModel, MuMetaModel inputMM, MuMetaModel outputMM) {
		this.genericTypeModification(atlModel, VariableDeclaration.class, "type", new MuMetaModel[] {inputMM, outputMM}, true);
	}
	
	@Override
	public String getDescription() {
		return "Variable Type Modification";
	}
}
