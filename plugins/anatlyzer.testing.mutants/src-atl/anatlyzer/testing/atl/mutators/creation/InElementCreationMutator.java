package anatlyzer.testing.atl.mutators.creation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EStructuralFeature;

import anatlyzer.atlext.ATL.ATLFactory;
import anatlyzer.atlext.ATL.MatchedRule;
import anatlyzer.atlext.ATL.PatternElement;
import anatlyzer.testing.mutants.MuMetaModel;

public class InElementCreationMutator extends AbstractElementCreationMutator {
	
	protected List<PatternElement> getCurrentPatternElements (MatchedRule rule) {
		EStructuralFeature feature = rule.getInPattern().eClass().getEStructuralFeature("elements");
		return (List<PatternElement>)rule.getInPattern().eGet(feature);		
	}
	
	protected List<PatternElement> getNewPatternElements (MuMetaModel inputMM, MuMetaModel outputMM) {
		List<PatternElement> newelements = new ArrayList<PatternElement>();
		newelements.addAll(this.getElement(inputMM)); // in-pattern element for each meta-model class
		return newelements;
	}
	
	protected PatternElement createPatternElement() {
		return ATLFactory.eINSTANCE.createSimpleInPatternElement();
	}
	
	@Override
	public String getDescription() {
		return "In Pattern Element Addition";
	}
}
