package anatlyzer.testing.atl.typing.mutators;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import anatlyzer.testing.atl.mutators.modification.feature.NavigationModificationMutator;
import anatlyzer.testing.mutants.MuMetaModel;

public class ReplaceFeatureAccessBySubtypeFeatureMutator extends NavigationModificationMutator{
	protected List<Object> featureReplacements (String type, String feature, MuMetaModel metamodel) {
		List<Object> replacements = new ArrayList<Object>();
		
		EClass mmtype = (EClass)metamodel.getEClassifier(type);
		if (mmtype!=null) {
			
			EStructuralFeature mmfeature = mmtype.getEStructuralFeature(feature);
			if (mmfeature!=null) {
			
				List<EStructuralFeature> options = new ArrayList<EStructuralFeature>();
			
				// search classes to use as replacement
				options.add( getCompatibleFeature2   (mmtype, mmfeature, metamodel) ); // compatible type and cardinality, but defined in a subclass
				
				// create list of replacements (name of selected features)  
				for (EStructuralFeature option : options) 
					if (option!=null) 
						replacements.add(option.getName());
			}
		}
		
		return replacements;	
	}
}
