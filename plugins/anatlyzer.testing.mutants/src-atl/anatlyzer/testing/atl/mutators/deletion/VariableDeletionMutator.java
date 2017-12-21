package anatlyzer.testing.atl.mutators.deletion;

import anatlyzer.atlext.ATL.Rule;
import anatlyzer.atlext.ATL.RuleVariableDeclaration;
import anatlyzer.testing.mutants.MuMetaModel;

public class VariableDeletionMutator extends AbstractDeletionMutator {

	@Override
	public void generateMutants(anatlyzer.atl.model.ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {		
		this.genericDeletion(wrapper, Rule.class, RuleVariableDeclaration.class, "variables");
	}

	@Override
	public String getDescription() {
		return "Deletion of Variable";
	}	
}