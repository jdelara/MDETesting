package anatlyzer.testing.atl.mutators.modification.type;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.OCL.Parameter;
import anatlyzer.testing.mutants.MuMetaModel;

public class ParameterModificationMutator extends AbstractTypeModificationMutator {
	
	@Override
	public void generateMutants(ATLModel atlModel, MuMetaModel inputMM, MuMetaModel outputMM) {
		this.genericTypeModification(atlModel, Parameter.class, "type", new MuMetaModel[] {inputMM, outputMM});
	}
	
	@Override
	public String getDescription() {
		return "Parameter Type Modification";
	}
}
