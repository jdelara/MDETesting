package anatlyzer.testing.modelgen;

import org.eclipse.jdt.annotation.NonNull;

import anatlyzer.atl.witness.IWitnessFinder;

public abstract class AbstractModelGenerator implements IModelGenerator {

	@NonNull
	protected IStorageStrategy storageStrategy;
	@NonNull 
	protected IWitnessFinder wf;
	
	protected int limit = -1;
	
	public AbstractModelGenerator(IStorageStrategy strategy, IWitnessFinder wf) {
		this.storageStrategy = strategy;
		this.wf = wf;
	}
	
	public AbstractModelGenerator withLimit(int limit) {
		if ( limit <= 0 )
			limit = -1;
		this.limit = limit;
		return this;
	}
	
}
