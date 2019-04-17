package anatlyzer.testing.atl.syntactic.mutators.bindings;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atl.types.Metaclass;
import anatlyzer.atl.types.Type;
import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.ATL.Rule;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.testing.atl.mutators.AbstractMutator;
import anatlyzer.testing.mutants.MuMetaModel;

public class BindingValueChange extends AbstractMutator {

	@Override
	public void generateMutants(ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {
		// we will add a comment to the module, documenting the mutation 
		Module module = wrapper.getModule();
		EDataTypeEList<String> comments = null;
		if (module!=null) {
			EStructuralFeature f = module.eClass().getEStructuralFeature("commentsBefore");	
			comments = (EDataTypeEList<String>)module.eGet(f);
		}
		
		for (Binding binding : (List<Binding>)wrapper.allObjectsOf(Binding.class)) {			
			Type targetType = binding.getOutPatternElement().getInferredType();
			if (targetType instanceof Metaclass) {				
				EClassifier targetClass = outputMM.getEClassifier(((Metaclass)targetType).getName());
				if (targetClass!=null && targetClass instanceof EClass) {
					OclExpression oldvalue = binding.getValue();
					Rule          rule     = binding.getOutPatternElement().getOutPattern().getRule();
					EStructuralFeature feature = ((EClass)targetClass).getEStructuralFeature(binding.getPropertyName());
					if (feature!=null) {
						OclExpression newvalue = ValueGenerator.getCompatibleValue(wrapper, feature, ValueGenerator.getVariableDeclarations(rule));
						if (newvalue!=null) {
							// mutate binding expression
							binding.setValue(newvalue);
							// document mutation
							if (comments!=null) comments.add("\n-- MUTATION \"" + this.getDescription() + "\" in binding " + toString(binding) + " of rule " + toString(rule) + " (line " + oldvalue.getLocation() + " of original transformation)\n");
							// restore original value					
							final EDataTypeEList<String> fComments = comments;
							registerUndo(wrapper, change(binding), () -> {
								if (fComments!=null) fComments.remove(fComments.size()-1);
								binding.setValue(oldvalue);
							});
						}
					}
				}
			}
		}

	}
	
	@Override
	public String getDescription() {
		return "Binding Value Change";
	}
}
