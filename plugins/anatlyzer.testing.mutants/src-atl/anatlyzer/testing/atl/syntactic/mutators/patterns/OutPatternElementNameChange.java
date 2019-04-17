package anatlyzer.testing.atl.syntactic.mutators.patterns;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.OutPatternElement;
import anatlyzer.testing.atl.mutators.modification.AbstractModificationMutator;
import anatlyzer.testing.mutants.MuMetaModel;

public class OutPatternElementNameChange extends AbstractModificationMutator {
	@Override
	public void generateMutants(ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {
		this.genericAttributeModification(wrapper, OutPatternElement.class, "varName", null);
	}

	@Override
	protected List<Object> replacements(EObject object2modify, String currentAttributeValue, MuMetaModel metamodel) {
		return Collections.singletonList(currentAttributeValue+"_MUTATED");
	}

	@Override
	public String getDescription() {
		return "Out Pattern Element Name Change";
	}
}
