package anatlyzer.testing.atl.syntactic.mutators.bindings;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.OCL.OclType;
import anatlyzer.testing.atl.mutators.modification.feature.BindingModificationMutator;
import anatlyzer.testing.mutants.MuMetaModel;

public class BindingFeatureChange extends BindingModificationMutator {

	@Override
	public String getDescription() {
		return "Binding Feature Change";
	}
	
	@Override
	protected List<Object> replacements(EObject object2modify, String currentAttributeValue, MuMetaModel metamodel) {
		List<Object> replacements = new ArrayList<>();
		if (object2modify instanceof Binding) {
			// replace given feature by every other feature defined by the owner type
			OclType     oclType = ((Binding)object2modify).getOutPatternElement().getType();
			EClassifier classifier = metamodel.getEClassifier(oclType.getName());
			if (classifier!=null && classifier instanceof EClass) {
				for (EStructuralFeature feature : ((EClass)classifier).getEAllStructuralFeatures()) {
					if (!feature.getName().equals(((Binding)object2modify).getPropertyName())) {
						replacements.add(feature.getName());
					}
				}
			}
		}
		return replacements;
	}
}
