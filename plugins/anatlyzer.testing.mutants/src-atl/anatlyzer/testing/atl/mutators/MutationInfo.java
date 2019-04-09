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
	private ChangeKind kind;

	public enum ChangeKind {
		ADD,
		CHANGE,
		REMOVE,
	}
	
	public MutationInfo(ATLAbstractMutator atlAbstractMutator, LocatedElement elem, ChangeKind kind) {
		this.mutator = atlAbstractMutator;
		this.mutatedElement = elem;
		this.kind = kind;
	}

	public ATLAbstractMutator getMutator() {
		return mutator;
	}
	
	public ChangeKind getKind() {
		return kind;
	}
	
	public LocatedElement getMutatedElement() {
		return mutatedElement;
	}
	
	public String getMutatorName() {
		return mutator.getClass().getSimpleName();
	}
	
}
