/**
 * Replaces an argument in a helper call, by a variable with a different type.
 */

package anatlyzer.testing.atl.typing.mutators;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atl.types.Metaclass;
import anatlyzer.atlext.ATL.InPattern;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.ATL.OutPattern;
import anatlyzer.atlext.ATL.PatternElement;
import anatlyzer.atlext.OCL.Iterator;
import anatlyzer.atlext.OCL.LoopExp;
import anatlyzer.atlext.OCL.OCLFactory;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.atlext.OCL.OperationCallExp;
import anatlyzer.atlext.OCL.VariableDeclaration;
import anatlyzer.atlext.OCL.VariableExp;
import anatlyzer.testing.atl.mutators.AbstractMutator;
import anatlyzer.testing.mutants.MuMetaModel;

public class ReplaceHelperCallParameter extends AbstractMutator {

	@Override
	public void generateMutants(ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {
		// we will add a comment to the module, documenting the mutation 
		Module module = wrapper.getModule();
		EDataTypeEList<String> comments = null;
		if (module!=null) {
			EStructuralFeature f = module.eClass().getEStructuralFeature("commentsBefore");	
			comments = (EDataTypeEList<String>)module.eGet(f);
		}
		
		for (OperationCallExp call : (List<OperationCallExp>)wrapper.allObjectsOf(OperationCallExp.class)) {
			if ( (!((OperationCallExp)call).getDynamicResolvers().isEmpty() ||  
				   ((OperationCallExp)call).isIsStaticCall()) &&       // helper invocation
				 !((OperationCallExp)call).getArguments().isEmpty()) { // with arguments

				List<OclExpression> arguments = ((OperationCallExp)call).getArguments();
				for (int i=0; i<arguments.size(); i++) {
					OclExpression argument = ((OperationCallExp)call).getArguments().get(i);
					List<VariableExp> replacements = this.replacements(argument);
					for (VariableExp replacement : replacements) {
						
						if (!(argument instanceof VariableExp) || 	// variable expression with a different type 
							validReplacement(((VariableExp)argument).getReferredVariable(), replacement.getReferredVariable())) {	
							
							// mutation: replace argument of helper call
							arguments.set(i, replacement);
							
							// documentation
							if (comments!=null) comments.add("\n-- MUTATION \"" + this.getDescription() + "\" in parameter number " + (i+1) + " of call to helper " + toString(call) + " (line " + call.getLocation() + " of original transformation)\n");
	
							// undo mutation
							final EDataTypeEList<String> fComments = comments;
							final int fi = i;
							registerUndo(wrapper, change((LocatedElement) call), () -> {
								if (fComments!=null) fComments.remove(fComments.size()-1);
								arguments.set(fi, argument);					
							});
						}
					}							
				}
			}
		}
	}
	
	// look for variables that can be used in the context of the expression
	protected List<VariableExp> replacements (EObject expression) {
		List<VariableExp> replacements = new ArrayList<VariableExp>();
		if (expression!=null) {
			
			if (expression instanceof LoopExp)
				for (Iterator iterator : ((LoopExp)expression).getIterators())
					replacements.add( createVariableExp(iterator) );
			
			else if (expression instanceof InPattern)
				for (PatternElement element : ((InPattern)expression).getElements())
					replacements.add( createVariableExp(element) );
			
			else if (expression instanceof OutPattern)
				for (PatternElement element : ((OutPattern)expression).getElements())
					replacements.add( createVariableExp(element) );
			
			replacements.addAll(replacements(expression.eContainer()));
		}
		return replacements;
	}
	
	// 
	protected VariableExp createVariableExp (VariableDeclaration vd) { VariableExp ve = OCLFactory.eINSTANCE.createVariableExp(); ve.setReferredVariable(vd); return ve; }
	
	//
	protected boolean validReplacement (VariableDeclaration vd1, VariableDeclaration vd2) {
		return !(vd1.getInferredType() instanceof Metaclass) ||
			   !(vd2.getInferredType() instanceof Metaclass) ||
			   !((Metaclass)vd1.getInferredType()).getName().equals(((Metaclass)vd2.getInferredType()).getName());
	}

	@Override
	public String getDescription() {
		return "ReplaceHelperCallParameter";
	}
}
