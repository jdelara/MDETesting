package anatlyzer.testing.mutants;

import java.io.File;

public class AtlMutantFolderRetriever extends FolderBasedMutantRetriever<AtlMutantReference>{

	public AtlMutantFolderRetriever(File file) {
		super(file, "atl");
	}

	public AtlMutantFolderRetriever(String path) {
		this(new File(path));
	}
	
	@Override
	protected AtlMutantReference getMutantFromFile(File file) {
		return new AtlMutantReference(file);
	}


}
