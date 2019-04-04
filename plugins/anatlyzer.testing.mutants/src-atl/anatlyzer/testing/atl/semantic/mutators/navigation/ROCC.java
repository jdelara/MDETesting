/**
 * Relation to another class change (ROCC): This operator replaces the navigation of an association  
 * towards a class with the navigation of another association to another class.
 */
package anatlyzer.testing.atl.semantic.mutators.navigation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import anatlyzer.testing.atl.mutators.modification.feature.NavigationModificationMutator;
import anatlyzer.testing.mutants.MuMetaModel;

public class ROCC extends NavigationModificationMutator {

	@Override
	public String getDescription() {
		return "Relation to another class change (ROCC)";
	}
	
	@Override
	protected List<Object> featureReplacements (String type, String feature, MuMetaModel metamodel) {
		List<Object> replacements = new ArrayList<Object>();
		EClass mmsource;
		EStructuralFeature mmfeature;		
		if ((mmsource = (EClass)metamodel.getEClassifier(type))!=null &&
			(mmfeature = mmsource.getEStructuralFeature(feature))!=null &&
			mmfeature instanceof EReference) 		
			for (EReference option : mmsource.getEAllReferences())
				if (option!=mmfeature && option.getEType()!=mmfeature.getEType())
					replacements.add(option.getName());
		return replacements;	
	}
}
