package anatlyzer.testing.atl.syntactic.mutators.bindings;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.ATLFactory;
import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.ATL.OutPatternElement;
import anatlyzer.atlext.OCL.VariableDeclaration;
import anatlyzer.testing.atl.mutators.creation.BindingCreationMutator;
import anatlyzer.testing.mutants.MuMetaModel;

public class BindingAddition extends BindingCreationMutator {
	
	// all possible non-duplicate bindings with a correct value
	protected List<Binding> newbindings(ATLModel wrapper, OutPatternElement outputElement, EClass outputElementType, MuMetaModel outputMM, List<? extends VariableDeclaration> ivariables) {		
		List<Binding> newbindings = new ArrayList<Binding>();
		for (EStructuralFeature feature : outputElementType.getEAllStructuralFeatures()) {
			if ( !outputElement.getBindings().stream().anyMatch( b -> b.getPropertyName().equals(feature.getName())) ) {
				String propertyName = feature.getName();
				Binding binding     = ATLFactory.eINSTANCE.createBinding();
				binding.setPropertyName( propertyName );
				binding.setValue( ValueGenerator.getCompatibleValue(wrapper, feature, ivariables) );
				newbindings.add(binding);
			}
		}
		return newbindings;
	}
	
	@Override
	public String getDescription() {
		return "Binding Addition";
	}
}
