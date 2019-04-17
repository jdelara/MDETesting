package anatlyzer.testing.atl.mutators.creation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;

import anatlyzer.atlext.ATL.MatchedRule;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.ATL.PatternElement;
import anatlyzer.atlext.OCL.OCLFactory;
import anatlyzer.atlext.OCL.OclModel;
import anatlyzer.atlext.OCL.OclModelElement;
import anatlyzer.testing.atl.mutators.AbstractMutator;
import anatlyzer.testing.mutants.MuMetaModel;

abstract public class AbstractElementCreationMutator extends AbstractMutator {

	@Override
	public void generateMutants(anatlyzer.atl.model.ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {
		
		// we will add a comment to the module, documenting the mutation 
		Module module = wrapper.getModule();
		EDataTypeEList<String> comments = null;
		if (module!=null) {
			EStructuralFeature feature = module.eClass().getEStructuralFeature("commentsBefore");	
			comments = (EDataTypeEList<String>)module.eGet(feature);
		}
		
		// for each matched rule
		for (MatchedRule rule : (List<MatchedRule>)wrapper.allObjectsOf(MatchedRule.class)) {
			
			List<PatternElement> realelements = getCurrentPatternElements(rule);
			List<PatternElement> newelements  = getNewPatternElements(inputMM, outputMM);
					
			// for each new pattern element 
			for (PatternElement element : newelements) {
				if (element!=null) {
						
					// mutation: add pattern element
					realelements.add(element);

					// mutation: documentation
					if (comments!=null) comments.add("\n-- MUTATION \"" + this.getDescription() + "\" " + toString(element) + " in " + toString(rule) + " (line " + rule.getLocation() + " of original transformation)\n");

					// restore: remove added pattern element and comment
					final EDataTypeEList<String> fComments = comments;
					registerUndo(wrapper, add(element), () -> {
						realelements.remove(element);
						if (fComments!=null) fComments.remove(fComments.size()-1);
					});
				}
			}
		}
	}
	
	// pattern elements of a rule	
	abstract protected List<PatternElement> getCurrentPatternElements (MatchedRule rule);
	
	// pattern elements to be added to a rule
	abstract protected List<PatternElement> getNewPatternElements (MuMetaModel inputMM, MuMetaModel outputMM);
	
	// new pattern element
	abstract protected PatternElement createPatternElement (); 
	
	/**
	 * It returns a list of in-pattern elements.
	 * @param metamodel
	 */
	protected List<PatternElement> getElement (MuMetaModel metamodel) {
		List<PatternElement> elements = new ArrayList<PatternElement>();
		for (EClassifier classifier : metamodel.getEClassifiers()) {
			if (classifier instanceof EClass) {
				PatternElement element = createPatternElement();
				OclModelElement ome = OCLFactory.eINSTANCE.createOclModelElement();
				OclModel        mm  = OCLFactory.eINSTANCE.createOclModel();
				ome.setName(classifier.getName());
				mm.setName (metamodel.getName());
				ome.setModel(mm);
				element.setType(ome);
				element.setVarName("dummy" + ome.getName() + elements.size());
				elements.add(element);
			}
		}
		return elements;
	}	
}
