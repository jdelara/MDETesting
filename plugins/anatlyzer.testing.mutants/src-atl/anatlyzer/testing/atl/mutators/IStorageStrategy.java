package anatlyzer.testing.atl.mutators;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atl.util.ATLSerializer;

public interface IStorageStrategy {

	void save(ATLModel atlModel, MutationInfo info);

	public static final IStorageStrategy NULL = new IStorageStrategy() {		
		@Override
		public void save(ATLModel atlModel, MutationInfo info) {
			System.err.println("No storage strategy.");
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
		public void save(ATLModel atlModel, MutationInfo info) {
			maxMap.putIfAbsent(info.getMutatorName(), 0);
			int num = maxMap.get(info.getMutatorName());;
			if ( num < max ) {
				maxMap.put(info.getMutatorName(), num + 1);				
				delegate.save(atlModel, info);
			}
		}
	}
	
	public class FileBasedStartegy implements IStorageStrategy {

		private String folder;
		private int count;
		
		public FileBasedStartegy(String folder) {
			this.folder = folder;
		}
		
		@Override
		public void save(ATLModel atlModel, MutationInfo info) {
			count++;
			
			String path = folder + File.separator + info.getMutatorName() + "_" + count + ".atl";
			try {
				ATLSerializer.serialize(atlModel, path);
			} catch (IOException e) {
				// TODO: Do this checked?
				throw new RuntimeException(e);
			}
		}
		

		// /home/jesus/mutation-cache/class2table/mutants/HelperReturnModificationMutator_541.atl
		public static String getMutantName(String path) {
			String name = new File(path).getName();
			Pattern pattern = Pattern.compile("([A-Za-z])+_[0-9]+\\.atl");
			
			Matcher m = pattern.matcher(name);
			if ( m.find() ) {
				return m.group(1);
			}
			throw new IllegalStateException("Can't recognize " + path);
		}
		
	}
	
	
}