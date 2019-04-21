package anatlyzer.testing.atl.typing.mutators;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;

import anatlyzer.testing.atl.semantic.mutators.navigation.RSMA;

public class AddNavigationAfterOptionalFeature extends RSMA {

	@Override
	public String getDescription() {
		return "AddNavigationAfterOptionalFeature";
	}
	
	@Override
	protected boolean confirmAdditionTo (EReference reference) {
		// perform mutation only if the feature (to which a navigation is to be added) is optional 
		return reference.getLowerBound()==0 && reference.getUpperBound()==1; 
	}
	
	protected List<EReference> replacements (EClass sourceclass) {
		// generate a single mutant per navigation expression
		List<EReference> replacements = super.replacements(sourceclass);
		return replacements.isEmpty()? replacements : replacements.subList(0, 1);
	}
}
