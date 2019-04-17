package anatlyzer.testing.atl.syntactic.mutators.patterns;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.InPatternElement;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.testing.atl.mutators.AbstractMutator;
import anatlyzer.testing.mutants.MuMetaModel;

public class InPatternElementClassChange extends AbstractMutator {

	@Override
	public void generateMutants(ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {
		
		// we will add a comment to the module, documenting the mutation 
		Module module = wrapper.getModule();
		EDataTypeEList<String> comments = null;
		if (module!=null) {
			EStructuralFeature f = module.eClass().getEStructuralFeature("commentsBefore");	
			comments = (EDataTypeEList<String>)module.eGet(f);
		}

		for (InPatternElement patternElement : (List<InPatternElement>)wrapper.allObjectsOf(InPatternElement.class)) {
			String oldtype = patternElement.getType().getName();
			for (EClassifier newtype : inputMM.getEClassifiers()) {								
				if (newtype instanceof EClass && !oldtype.equals(newtype.getName())) {
					// change type
					patternElement.getType().setName(newtype.getName());
					
					// mutation: documentation
					if (comments!=null) comments.add("\n-- MUTATION \"" + this.getDescription() + "\" in rule " + toString(patternElement.getInPattern().eContainer()) +" from " + oldtype + " to " + newtype.getName() + " (line " + patternElement.getType().getLocation() + " of original transformation)\n");

					// undo mutation
					final EDataTypeEList<String> fComments = comments;
					registerUndo(wrapper, change((LocatedElement) patternElement), () -> {
						if (fComments!=null) fComments.remove(fComments.size()-1);
						patternElement.getType().setName(oldtype);					
					});

				}
			}
		}		
	}

	@Override
	public String getDescription() {
		return "In Pattern Element Class Change";
	}
}
