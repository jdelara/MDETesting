package anatlyzer.testing.mutants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import anatlyzer.testing.common.FileUtils;
import anatlyzer.testing.common.IProgressMonitor;
import anatlyzer.testing.mutants.IMutantGenerator.IMutantReference;

public abstract class FolderBasedMutantRetriever<T extends IMutantReference> implements IMutantGenerator<T> {

	private File folder;
	private String extension;
	private Predicate<File> fileFilter = (f) -> true;
	private int limitPerMutant = -1;

	public FolderBasedMutantRetriever(File file, String extension) {
		this.folder = file;
		this.extension = extension;
	}
	
	public FolderBasedMutantRetriever(String fileName, String extension) {
		this(new File(fileName), extension);		
	}

	/**
	 * Established a limit, per kind of mutant.
	 */
	public FolderBasedMutantRetriever<T> withLimitPerMutant(int limit) {
		this.limitPerMutant = limit;
		return this;
	}
	
	@Override
	public List<T> generateMutants(IProgressMonitor monitor) throws MutantGeneratorException {
		try {
			List<T> allFiles = FileUtils.getFiles(folder, extension, fileFilter, (file) -> getMutantFromFile(file));
			if (limitPerMutant == -1)
				return allFiles;
			else {
				Map<String, Integer> counter = new HashMap<>();
				
				List<T> filtered = new ArrayList<>();
				for(T m : allFiles) {
					counter.putIfAbsent(m.getMutantKind(), 0);
					if (counter.get(m.getMutantKind()) <= limitPerMutant) {
						filtered.add(m);
						counter.compute(m.getMutantKind(), (k, v) -> v + 1);
					}
				}
				return filtered;
			}
		} catch (IOException e) {
			throw new MutantGeneratorException(e);
		}
	}


	public void withFileFilter(Predicate<File> predicate) {
		this.fileFilter = predicate;
	}
	
	protected abstract T getMutantFromFile(File file);
	
}
