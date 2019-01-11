package anatlyzer.testing.modelgen;

import anatlyzer.atl.witness.IWitnessModel;
import anatlyzer.testing.common.Metamodel;

public interface IStorageStrategy {
	
	public IGeneratedModelReference save(IWitnessModel foundWitnessModel, Metamodel metamodel);

	@SuppressWarnings("serial")
	public static class StorageException extends RuntimeException {
		public StorageException(Throwable t) {
			super(t);
		}
	}
	
}
