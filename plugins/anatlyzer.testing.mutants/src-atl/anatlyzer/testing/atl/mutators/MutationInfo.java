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

	public void setMutator(ATLAbstractMutator atlAbstractMutator) {
		this.mutator = atlAbstractMutator;
	}

	public void setMutatedElement(LocatedElement elem) {
		this.mutatedElement = elem;
	}

}
