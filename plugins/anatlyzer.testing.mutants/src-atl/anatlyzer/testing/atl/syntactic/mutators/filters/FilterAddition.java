package anatlyzer.testing.atl.syntactic.mutators.filters;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atl.types.impl.MetaclassImpl;
import anatlyzer.atlext.ATL.InPattern;
import anatlyzer.atlext.ATL.InPatternElement;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.testing.atl.semantic.mutators.filtering.CFCP;
import anatlyzer.testing.mutants.MuMetaModel;

public class FilterAddition extends CFCP {

	@Override
	public void generateMutants(ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {
		
		// we will add a comment to the module, documenting the mutation 
		Module module = wrapper.getModule();
		EDataTypeEList<String> comments = null;
		if (module!=null) {
			EStructuralFeature f = module.eClass().getEStructuralFeature("commentsBefore");	
			comments = (EDataTypeEList<String>)module.eGet(f);
		}
		
		// add filter by property to rules without filter (filter by property)	
		List<InPattern> inpatterns = (List<InPattern>)wrapper.allObjectsOf(InPattern.class);
		for (InPattern inpattern : inpatterns) {
			for (InPatternElement variable : inpattern.getElements()) {
				if (variable.getInferredType() instanceof MetaclassImpl) {				
					EClassifier itClass = inputMM.getEClassifier(((MetaclassImpl)variable.getInferredType()).getName());
					if (itClass instanceof EClass) {		
						OclExpression oldFilter = inpattern.getFilter();
						//
						// This condition is not checked in CFCP
						if (oldFilter==null) { 
						//
						//	
							for (EAttribute att : ((EClass)itClass).getEAllAttributes()) {
								mutate(andVariablePropertyHasValue(variable, att), oldFilter, inpattern);
								document(comments, "property "+att.getName(), toString(inpattern)+" rule", ((LocatedElement)inpattern).getLocation());
								undo(wrapper, comments, inpattern, oldFilter);
							}
						}
					}
				}					
			}
		}
	}
	
	@Override
	public String getDescription() {
		return "Filter Addition";
	}
}
