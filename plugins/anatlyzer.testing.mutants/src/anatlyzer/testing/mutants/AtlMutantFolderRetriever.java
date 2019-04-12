package anatlyzer.testing.mutants;

import java.io.File;

import anatlyzer.testing.atl.mutators.IStorageStrategy;

public class AtlMutantFolderRetriever extends FolderBasedMutantRetriever<AtlMutantReference>{

	public AtlMutantFolderRetriever(File file) {
		super(file, "atl");
	}

	public AtlMutantFolderRetriever(String path) {
		this(new File(path));
	}
	
	@Override
	protected AtlMutantReference getMutantFromFile(File file) {
		String mutantName = IStorageStrategy.FileBasedStartegy.getMutantName(file.getAbsolutePath());		
		return new AtlMutantReference(file, mutantName);
	}


}
