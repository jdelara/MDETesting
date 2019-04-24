package anatlyzer.testing.atl.mutators;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atl.util.ATLSerializer;
import anatlyzer.testing.mutants.AtlMutantReference;
import anatlyzer.testing.mutants.IMutantGenerator.IMutantReference;

public interface IStorageStrategy {

	@Nullable
	IMutantReference save(ATLModel atlModel, MutationInfo info);

	default void onRestoredTransformation(ATLModel mutatedModel, MutationInfo info, IMutantReference ref) {
		// Let specific strategies to implement this
	}

	public static final IStorageStrategy NULL = new IStorageStrategy() {		
		@Override
		public IMutantReference save(ATLModel atlModel, MutationInfo info) {
			System.err.println("No storage strategy.");
			return null;
		}
	};
	
	public class MaxMutantKindStrategy implements IStorageStrategy {
		private IStorageStrategy delegate;
		private Map<String, Integer> maxMap = new HashMap<>();
		private int max; 
		
		public MaxMutantKindStrategy(int max, IStorageStrategy delegate) {
			this.delegate = delegate;
			this.max = max;
		}

		@Override
		public IMutantReference save(ATLModel atlModel, MutationInfo info) {
			maxMap.putIfAbsent(info.getMutatorName(), 0);
			int num = maxMap.get(info.getMutatorName());;
			if ( num < max ) {
				maxMap.put(info.getMutatorName(), num + 1);				
				return delegate.save(atlModel, info);
			}
			return null;
		}
	}
	
	public class FileBasedStartegy implements IStorageStrategy {

		private String folder;
		private int count;
		
		public FileBasedStartegy(String folder) {
			this.folder = folder;
		}
		
		@Override
		public IMutantReference save(ATLModel atlModel, MutationInfo info) {
			count++;
			
			String path = folder + File.separator + info.getMutatorName() + "_" + count + ".atl";
			try {
				ATLSerializer.serialize(atlModel, path);
			} catch (IOException e) {
				// TODO: Do this checked?
				throw new RuntimeException(e);
			}
			
			return new AtlMutantReference(new File(path), info.getMutatorName());
		}
		

		// /home/jesus/mutation-cache/class2table/mutants/HelperReturnModificationMutator_541.atl
		public static String getMutantName(String path) {
			String name = new File(path).getName();
			
			String mutant = name.substring(0, name.indexOf("_"));	// assuming no "_" in mutant name
			if ( mutant.contains("\\") ) {
				int idx = mutant.lastIndexOf("\\");
				mutant = mutant.substring(idx + 1);
			}
			
			return mutant;
			
			// Changed by JL: for some reason the following code does not work for me (returns "r")
			/*Pattern pattern = Pattern.compile("([A-Za-z])+_[0-9]+\\.atl");
			
			Matcher m = pattern.matcher(name);
			if ( m.find() ) {
				return m.group(1);
			}
			throw new IllegalStateException("Can't recognize " + path);*/
		}
	}
	
	public static abstract class DelegatedStrategy implements IStorageStrategy {

		protected IStorageStrategy strategy;

		public DelegatedStrategy(IStorageStrategy strategy) {
			this.strategy = strategy;
		}
		
	}

	
	
}