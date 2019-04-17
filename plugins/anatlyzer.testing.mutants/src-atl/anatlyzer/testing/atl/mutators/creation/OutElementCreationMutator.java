package anatlyzer.testing.atl.mutators.creation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EStructuralFeature;

import anatlyzer.atlext.ATL.ATLFactory;
import anatlyzer.atlext.ATL.MatchedRule;
import anatlyzer.atlext.ATL.PatternElement;
import anatlyzer.testing.mutants.MuMetaModel;

public class OutElementCreationMutator extends AbstractElementCreationMutator {
	
	protected List<PatternElement> getCurrentPatternElements (MatchedRule rule) {
		EStructuralFeature feature = rule.getOutPattern().eClass().getEStructuralFeature("elements");
		return (List<PatternElement>)rule.getOutPattern().eGet(feature);		
	}
	
	protected List<PatternElement> getNewPatternElements (MuMetaModel inputMM, MuMetaModel outputMM) {
		List<PatternElement> newelements = new ArrayList<PatternElement>();
		newelements.addAll(this.getElement(outputMM)); // out-pattern element for each meta-model class
		return newelements;
	}
	
	@Override
	protected PatternElement createPatternElement() {
		return ATLFactory.eINSTANCE.createSimpleOutPatternElement();
	}
	
	@Override
	public String getDescription() {
		return "Out Pattern Element Addition";
	}	
}
