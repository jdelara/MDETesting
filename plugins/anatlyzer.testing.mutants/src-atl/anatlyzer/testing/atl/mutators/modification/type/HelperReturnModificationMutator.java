package anatlyzer.testing.atl.mutators.modification.type;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.OCL.Attribute;
import anatlyzer.atlext.OCL.Operation;
import anatlyzer.testing.mutants.MuMetaModel;

public class HelperReturnModificationMutator extends AbstractTypeModificationMutator {
	
	@Override
	public void generateMutants(ATLModel atlModel, MuMetaModel inputMM, MuMetaModel outputMM) {
		this.genericTypeModification(atlModel, Attribute.class, "type",       new MuMetaModel[] {inputMM, outputMM});
		this.genericTypeModification(atlModel, Operation.class, "returnType", new MuMetaModel[] {inputMM, outputMM});
}
	
	@Override
	public String getDescription() {
		return "Helper Return Type Modification";
	}
}
