package anatlyzer.testing.atl.mutators.modification.type;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.OCL.CollectionType;
import anatlyzer.testing.mutants.MuMetaModel;

public class CollectionModificationMutator extends AbstractTypeModificationMutator {
	
	@Override
	public void generateMutants(ATLModel atlModel, MuMetaModel inputMM, MuMetaModel outputMM) {
		this.genericTypeModification(atlModel, CollectionType.class, "elementType", new MuMetaModel[] {inputMM, outputMM});
	}
	
	@Override
	public String getDescription() {
		return "Collection Type Modification";
	}
}
