package anatlyzer.testing.atl.mutators.modification.type;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.OCL.OperationCallExp;
import anatlyzer.testing.mutants.MuMetaModel;

public class ArgumentModificationMutator extends AbstractTypeModificationMutator {
	
	@Override
	public void generateMutants(ATLModel atlModel, MuMetaModel inputMM, MuMetaModel outputMM) {
		this.genericTypeModification(atlModel, OperationCallExp.class, "source",    new MuMetaModel[] {inputMM, outputMM});
		this.genericTypeModification(atlModel, OperationCallExp.class, "arguments", new MuMetaModel[] {inputMM, outputMM});
		this.genericTypeModification(atlModel, Binding.class,          "value",     new MuMetaModel[] {});
	}
	
	@Override
	public String getDescription() {
		return "Argument Type Modification";
	}
}
