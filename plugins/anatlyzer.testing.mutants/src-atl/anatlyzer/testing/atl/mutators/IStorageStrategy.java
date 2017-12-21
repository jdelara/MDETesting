package anatlyzer.testing.atl.mutators;

import anatlyzer.atl.model.ATLModel;

public interface IStorageStrategy {

	void save(ATLModel atlModel, MutationInfo info);

}