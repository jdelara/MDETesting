package anatlyzer.testing.mutants;

import java.io.File;
import java.io.IOException;
import java.util.List;

import anatlyzer.testing.common.FileUtils;
import anatlyzer.testing.common.IProgressMonitor;
import anatlyzer.testing.mutants.IMutantGenerator.IMutantReference;

public abstract class FolderBasedMutantRetriever<T extends IMutantReference> implements IMutantGenerator<T> {

	private File folder;
	private String extension;

	public FolderBasedMutantRetriever(File file, String extension) {
		this.folder = file;
		this.extension = extension;
	}
	
	public FolderBasedMutantRetriever(String fileName, String extension) {
		this(new File(fileName), extension);		
	}

	@Override
	public List<T> generateMutants(IProgressMonitor monitor) throws MutantGeneratorException {
		try {
			return FileUtils.getFiles(folder, extension, (file) -> getMutantFromFile(file));
		} catch (IOException e) {
			throw new MutantGeneratorException(e);
		}
	}

	protected abstract T getMutantFromFile(File file);
	
}
