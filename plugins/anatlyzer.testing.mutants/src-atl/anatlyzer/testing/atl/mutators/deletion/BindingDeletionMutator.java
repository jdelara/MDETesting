package anatlyzer.testing.atl.mutators.deletion;

import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.ATL.OutPatternElement;
import anatlyzer.testing.mutants.MuMetaModel;

public class BindingDeletionMutator extends AbstractDeletionMutator {

	@Override
	public void generateMutants(anatlyzer.atl.model.ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {		
		this.genericDeletion(wrapper, OutPatternElement.class, Binding.class, "bindings");
	}

	@Override
	public String getDescription() {
		return "Deletion of Binding";
	}
}
