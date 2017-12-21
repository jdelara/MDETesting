package anatlyzer.testing.atl.mutators.modification.feature;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.m2m.atl.core.emf.EMFModel;

import witness.generator.MetaModel;
import anatlyzer.atlext.ATL.Binding;
import anatlyzer.testing.mutants.MuMetaModel;

public class BindingModificationMutator extends AbstractFeatureModificationMutator {

	@Override
	public void generateMutants(anatlyzer.atl.model.ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {
		this.genericAttributeModification(wrapper, Binding.class, "propertyName", outputMM);
	}

	@Override
	public String getDescription() {
		return "Binding-target Modification";
	}

	@Override
	protected List<Object> replacements(EObject object2modify, String currentAttributeValue, MuMetaModel metamodel) {
		return object2modify instanceof Binding? 
			   this.featureReplacements(toString(((Binding)object2modify).getOutPatternElement().getType()), currentAttributeValue, metamodel) :
			   new ArrayList<Object>();	   
	}
}
