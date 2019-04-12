package anatlyzer.testing.atl.typing.mutators;

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
import anatlyzer.atlext.OCL.BooleanExp;
import anatlyzer.atlext.OCL.Iterator;
import anatlyzer.atlext.OCL.IteratorExp;
import anatlyzer.atlext.OCL.NavigationOrAttributeCallExp;
import anatlyzer.atlext.OCL.OCLFactory;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.atlext.OCL.OclModel;
import anatlyzer.atlext.OCL.OclModelElement;
import anatlyzer.atlext.OCL.OperationCallExp;
import anatlyzer.atlext.OCL.OperatorCallExp;
import anatlyzer.atlext.OCL.VariableDeclaration;
import anatlyzer.atlext.OCL.VariableExp;
import anatlyzer.atlext.OCL.impl.BooleanExpImpl;
import anatlyzer.atlext.OCL.impl.StringExpImpl;
import anatlyzer.testing.atl.mutators.AbstractMutator;
import anatlyzer.testing.atl.semantic.mutators.filtering.CFCP;
import anatlyzer.testing.mutants.EMFUtils;
import anatlyzer.testing.mutants.MuMetaModel;

public class RestrictRuleFilterMutator extends CFCP {
	@Override
	public void generateMutants(ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {
		
		// we will add a comment to the module, documenting the mutation 
		Module module = wrapper.getModule();
		EDataTypeEList<String> comments = null;
		if (module!=null) {
			EStructuralFeature f = module.eClass().getEStructuralFeature("commentsBefore");	
			comments = (EDataTypeEList<String>)module.eGet(f);
		}
		
		// ---------------------------------------------------------------------------
		// RULE FILTERS: a) filter by property; b) filter by subclass	
		// ---------------------------------------------------------------------------
		List<InPattern> inpatterns = (List<InPattern>)wrapper.allObjectsOf(InPattern.class);
		
		for (InPattern inpattern : inpatterns) {
			for (InPatternElement variable : inpattern.getElements()) {
				if (variable.getInferredType() instanceof MetaclassImpl) {				
					EClassifier itClass = inputMM.getEClassifier(((MetaclassImpl)variable.getInferredType()).getName());
					if (itClass instanceof EClass) {		
						OclExpression oldFilter = inpattern.getFilter();					
					
						// a) filter by subclass
						for (EClass subclass : this.subclasses((EClass)itClass, inputMM)) {
							mutate(andVariableIsTypeOf(variable, subclass), oldFilter, inpattern);
							document(comments, "subtype "+subclass.getName(), toString(inpattern)+" rule", ((LocatedElement)inpattern).getLocation());
							undo(wrapper, comments, inpattern, oldFilter);
						}
				
						// b) filter by property
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

	@Override
	public String getDescription() {
		return "Restrict Rule Filter Mutator (RRF)";
	}
}
