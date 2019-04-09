/**
 * Collection filtering change with deletion (CFCD): this operator deletes a filter 
 * on a collection; the mutant returns the collection it was supposed to filter.
 */

package anatlyzer.testing.atl.semantic.mutators.filtering;

import java.util.List;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;

import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.OCL.BooleanExp;
import anatlyzer.atlext.OCL.IteratorExp;
import anatlyzer.atlext.OCL.OCLFactory;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.testing.atl.mutators.deletion.FilterDeletionMutator;
import anatlyzer.testing.mutants.MuMetaModel;

public class CFCD extends FilterDeletionMutator {

	@Override
	public void generateMutants(anatlyzer.atl.model.ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {

		// ---------------------------------------------------------------------------
		// DELETION OF RULE FILTERS	
		// ---------------------------------------------------------------------------
		super.generateMutants(wrapper, inputMM, outputMM);
		
		// ---------------------------------------------------------------------------
		// DELETION OF FILTER IN SELECT/REJECT OPERATORS	
		// ---------------------------------------------------------------------------		
		List<IteratorExp> iterators = (List<IteratorExp>)wrapper.allObjectsOf(IteratorExp.class);
		
		// we will add a comment to the module, documenting the mutation 
		Module module = wrapper.getModule();
		EDataTypeEList<String> comments = null;
		if (module!=null) {
			EStructuralFeature f = module.eClass().getEStructuralFeature("commentsBefore");	
			comments = (EDataTypeEList<String>)module.eGet(f);
		}
		
		for (IteratorExp iterator : iterators) {
			if (iterator.getName().equals("select") || iterator.getName().equals("reject")) {
				
				// set iterator filter to true
				BooleanExp    trueValue = OCLFactory.eINSTANCE.createBooleanExp();
				OclExpression oldFilter = iterator.getBody(); 
				trueValue.setBooleanSymbol(iterator.getName().equals("select")? true : false);
				iterator.setBody(trueValue);
				
				// mutation: documentation
				if (comments!=null) comments.add("\n-- MUTATION \"" + this.getDescription() + "\" deleted filter in " + iterator.getName() + " iterator (line " + ((LocatedElement)iterator).getLocation() + " of original transformation)\n");				

				// restore original value
				final EDataTypeEList<String> fComments = comments;
				registerUndo(wrapper, () -> {
					if (fComments!=null) fComments.remove(fComments.size()-1);
					iterator.setBody(oldFilter);
				});
			}
		}			
	}
	
	@Override
	public String getDescription() {
		return "Collection filtering change with deletion (CFCD)";
	}
}
