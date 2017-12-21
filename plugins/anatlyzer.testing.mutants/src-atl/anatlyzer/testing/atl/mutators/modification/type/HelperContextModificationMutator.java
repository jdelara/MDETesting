package anatlyzer.testing.atl.mutators.modification.type;

import org.eclipse.m2m.atl.core.emf.EMFModel;

import witness.generator.MetaModel;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.OCL.OclContextDefinition;
import anatlyzer.testing.mutants.MuMetaModel;

public class HelperContextModificationMutator extends AbstractTypeModificationMutator {
	
	@Override
	public void generateMutants(ATLModel atlModel, MuMetaModel inputMM, MuMetaModel outputMM) {
		this.genericTypeModification(atlModel, OclContextDefinition.class, "context_", new MuMetaModel[] {inputMM, outputMM});

	}
	
	@Override
	public String getDescription() {
		return "Helper Context Type Modification";
	}
}
