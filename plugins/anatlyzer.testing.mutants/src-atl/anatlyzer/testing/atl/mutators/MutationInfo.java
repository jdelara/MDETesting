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
	private LocatedElement newTarget;

	public enum ChangeKind {
		ADD,
		CHANGE,
		REPLACE,
		REMOVE,
	}

	public MutationInfo(ATLAbstractMutator atlAbstractMutator, LocatedElement elem, ChangeKind kind) {
		this(atlAbstractMutator, elem, null, kind);
	}
	
	public MutationInfo(ATLAbstractMutator atlAbstractMutator, LocatedElement elem, LocatedElement newTarget, ChangeKind kind) {
		this.mutator = atlAbstractMutator;
		this.mutatedElement = elem;
		this.newTarget = newTarget;
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

	public LocatedElement getNewTarget() {
		return newTarget;
	}
	
}
