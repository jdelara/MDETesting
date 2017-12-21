package anatlyzer.testing.ocl.mutators;

import org.eclipse.emf.ecore.EObject;

import anatlyzer.atlext.ATL.Helper;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.OCL.Attribute;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.atlext.OCL.Operation;

public interface IMutationSelectorStrategy {
	
	/**
	 * Checks if the given expression needs to be mutated. If this is the
	 * case, it returns an object which will be annotated with the mutation
	 * (i.e., the context of the mutation, like a helper or a rule)
	 * 
	 * @param exp
	 * @return null if the expression must not be mutated
	 */
	public LocatedElement needsToBeMutated(EObject exp);

	String createComment(LocatedElement context, LocatedElement element);
	

}
