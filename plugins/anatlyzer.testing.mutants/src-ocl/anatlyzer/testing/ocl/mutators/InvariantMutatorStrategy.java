package anatlyzer.testing.ocl.mutators;

import org.eclipse.emf.ecore.EObject;

import anatlyzer.atlext.ATL.Helper;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.OCL.Attribute;
import anatlyzer.atlext.OCL.Operation;

public class InvariantMutatorStrategy implements IMutationSelectorStrategy {

	@Override
	public LocatedElement needsToBeMutated(EObject element) {
		EObject exp = element;
		while (exp != null && !(exp instanceof Helper)) exp = exp.eContainer();			
		if (exp instanceof Helper && isTargetInvariant((Helper)exp)) {
			return (LocatedElement) exp;
		}
		return null;
	}


	/**
	 * It returns true if the helper represent a target meta-model invariant (i.e., it is annotated as "target_invariant").
	 * @param helper
	 * @return
	 */
	protected boolean isTargetInvariant (Helper helper) {
		return helper.getCommentsBefore().stream().anyMatch(comment -> comment.startsWith("--") && comment.endsWith("@target_invariant"));
	}

	/**
	 * It creates a string explaining the location of the performed mutation. 
	 */
	@Override
	public String createComment(LocatedElement context, LocatedElement element) {
		return  "-- @mutated_target_invariant " + name((Helper) context) +" (line " + element.getLocation() + " of original transformation)\n";
	}
	
	/**
	 * It returns the name of a helper.
	 * @param helper
	 * @return
	 */
	private String name (Helper helper) {
		String helperName = ""; 
		if (helper!=null) {
			if      (helper.getDefinition() != null && helper.getDefinition().getFeature() instanceof Operation) helperName = ((Operation)(helper.getDefinition().getFeature())).getName();
			else if (helper.getDefinition() != null && helper.getDefinition().getFeature() instanceof Attribute) helperName = ((Attribute)(helper.getDefinition().getFeature())).getName();
		}
		return helperName;
	}
}
