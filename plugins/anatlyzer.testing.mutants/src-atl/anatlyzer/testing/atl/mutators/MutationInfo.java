package anatlyzer.testing.atl.mutators;

import anatlyzer.atlext.ATL.LocatedElement;

/**
 * This class gather information about a mutation
 * @author jesus
 *
 */
public class MutationInfo {

	private ATLAbstractMutator mutator;
	private LocatedElement mutatedElement;

	public MutationInfo(ATLAbstractMutator atlAbstractMutator, LocatedElement elem) {
		this.mutator = atlAbstractMutator;
		this.mutatedElement = elem;
	}

	public MutationInfo(ATLAbstractMutator atlAbstractMutator) {
		this(atlAbstractMutator, null);
	}

	public ATLAbstractMutator getMutator() {
		return mutator;
	}
	
	public LocatedElement getMutatedElement() {
		return mutatedElement;
	}
	
	public String getMutatorName() {
		return mutator.getClass().getSimpleName();
	}
	
}
