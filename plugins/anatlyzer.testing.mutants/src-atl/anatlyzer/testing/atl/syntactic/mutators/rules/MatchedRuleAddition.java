package anatlyzer.testing.atl.syntactic.mutators.rules;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.ATLFactory;
import anatlyzer.atlext.ATL.InPattern;
import anatlyzer.atlext.ATL.InPatternElement;
import anatlyzer.atlext.ATL.MatchedRule;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.ATL.OutPattern;
import anatlyzer.atlext.ATL.OutPatternElement;
import anatlyzer.atlext.ATL.Rule;
import anatlyzer.atlext.OCL.OCLFactory;
import anatlyzer.atlext.OCL.OclModel;
import anatlyzer.atlext.OCL.OclModelElement;
import anatlyzer.testing.atl.mutators.AbstractMutator;
import anatlyzer.testing.mutants.MuMetaModel;

public class MatchedRuleAddition extends AbstractMutator {

	@Override
	public void generateMutants(ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {
		// we will add a comment to the module, documenting the mutation 
		Module module = wrapper.getModule();
		EDataTypeEList<String> comments = null;
		if (module!=null) {
			EStructuralFeature feature = module.eClass().getEStructuralFeature("commentsBefore");	
			comments = (EDataTypeEList<String>)module.eGet(feature);
		}
		else return;
		
		EStructuralFeature feature = module.eClass().getEStructuralFeature("elements");
		List<Rule>         rules   = (List<Rule>)module.eGet(feature);
		for (EClassifier sourceclass : inputMM.getEClassifiers()) {
			if (sourceclass instanceof EClass) {
				for (EClassifier targetclass : outputMM.getEClassifiers()) {
					if (targetclass instanceof EClass && !((EClass)targetclass).isAbstract()) {
						// mutation: add rule
						Rule rule = createRule((EClass)sourceclass, (EClass)targetclass, inputMM, outputMM);
						rules.add(rule);
						
						// mutation: documentation
						if (comments!=null) comments.add("\n-- MUTATION \"" + this.getDescription() + "\" " + toString(rule) + "\n");
						
						final EDataTypeEList<String> fComments = comments;
						registerUndo(wrapper, add(module), () -> {
							rules.remove(rule);
							if (fComments!=null) fComments.remove(fComments.size()-1);
						});
					}
				}
			}
		}
	}
	
	private Rule createRule (EClass source, EClass target, MuMetaModel inputMM, MuMetaModel outputMM) {
		InPattern        ipattern = ATLFactory.eINSTANCE.createInPattern();
		InPatternElement ielement = ATLFactory.eINSTANCE.createSimpleInPatternElement();
		OclModelElement  iobject  = createModelElement(source.getName(), inputMM.getName());
		ielement.setVarName("i" + iobject.getName());	
		ielement.setType(iobject);
		ipattern.getElements().add (ielement);
		
		OutPattern        opattern = ATLFactory.eINSTANCE.createOutPattern();
		OutPatternElement oelement = ATLFactory.eINSTANCE.createSimpleOutPatternElement();
		OclModelElement   oobject  = createModelElement(target.getName(), outputMM.getName());
		oelement.setVarName("o" + oobject.getName());		
		oelement.setType(oobject);
		opattern.getElements().add(oelement);
		
		MatchedRule rule     = ATLFactory.eINSTANCE.createMatchedRule();
		rule.setName("new_rule_" + source.getName() + "2" + target.getName());
		rule.setInPattern(ipattern);
		rule.setOutPattern(opattern);
		
		return rule;
	}
	
	private OclModelElement createModelElement (String elementName, String elementType) {
		OclModelElement  object = OCLFactory.eINSTANCE.createOclModelElement();
		OclModel         model  = OCLFactory.eINSTANCE.createOclModel();
		model.setName (elementType);
		object.setName(elementName);
		object.setModel(model);
		return object;
	}

	@Override
	public String getDescription() {
		return "Matched Rule Addition";
	}
}
