package anatlyzer.testing.atl.mutators.modification;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;

import anatlyzer.atlext.ATL.MatchedRule;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.ATL.RuleWithPattern;
import anatlyzer.testing.atl.mutators.AbstractMutator;
import anatlyzer.testing.mutants.MuMetaModel;

public class ParentRuleModificationMutator extends AbstractMutator {

	@Override
	public void generateMutants(anatlyzer.atl.model.ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {
		// we will add a comment to the module, documenting the mutation 
		Module module = wrapper.getModule();
		EDataTypeEList<String> comments = null;
		if (module!=null) {
			EStructuralFeature feature = wrapper.source(module).eClass().getEStructuralFeature("commentsBefore");	
			comments = (EDataTypeEList<String>)wrapper.source(module).eGet(feature);
		}
		
		// for each matched rule
		for (MatchedRule rule : (List<MatchedRule>)wrapper.allObjectsOf(MatchedRule.class)) {
			
			// obtain current parent rule 
			EStructuralFeature feature   = wrapper.source(rule).eClass().getEStructuralFeature("superRule");
			Object             superrule = wrapper.source(rule).eGet(feature);
					
			// matched rules
			List<MatchedRule> parents = (List<MatchedRule>)wrapper.allObjectsOf(MatchedRule.class);
			
			// exclude itself, and other rules that would make an inheritance cycle
			List<MatchedRule> remove = new ArrayList<MatchedRule>();
			for (MatchedRule parent : parents) {
				RuleWithPattern r = parent.getSuperRule();
				while (r!=null && r!=rule) r = r.getSuperRule();
				if (r==rule) remove.add(parent);
			}
			parents.remove(rule);
			parents.removeAll(remove);
					
			// for each matched rule 
			for (MatchedRule parent : parents) {
					
				// mutation: modify parent rule
				wrapper.source(rule).eSet(feature, wrapper.source(parent));

				// mutation: documentation
				if (comments!=null) comments.add("\n-- MUTATION \"" + this.getDescription() + "\" " + toString(parent) + " in " + toString(rule) + " (line " + rule.getLocation() + " of original transformation)\n");


				// restore: remove added comment
				// restore original parent rule
				final EDataTypeEList<String> fComments = comments;
				registerUndo(wrapper, () -> {
					wrapper.source(rule).eSet(feature, superrule);
					if (fComments!=null) fComments.remove(fComments.size()-1);
				});
			}

		}
	}
	
	@Override
	public String getDescription() {
		return "Modification of Parent Rule";
	}
}
