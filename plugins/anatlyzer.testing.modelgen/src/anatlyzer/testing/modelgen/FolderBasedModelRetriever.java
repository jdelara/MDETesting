package anatlyzer.testing.modelgen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import anatlyzer.testing.common.IProgressMonitor;
import anatlyzer.testing.common.Metamodel;

public class FolderBasedModelRetriever implements IModelGenerator {

	private File folder;
	private Metamodel metamodel;
	private Predicate<File> fileFilter = (f) -> true;
	private boolean recursive;
	private int limit = Integer.MAX_VALUE;
	
	public FolderBasedModelRetriever(File file, Metamodel metamodel) {
		this.folder = file;
		this.metamodel = metamodel;
	}
	
	public FolderBasedModelRetriever(String fileName, Metamodel metamodel) {
		this(new File(fileName), metamodel);
	}

	public FolderBasedModelRetriever withRecursive(boolean b) {
		this.recursive = b;
		return this;
	}

	public boolean isRecursive() {
		return recursive;
	}
	
	@Override
	public List<IGeneratedModelReference> generateModels(IProgressMonitor monitor) {
		try {
			return Files.walk(folder.toPath(), recursive ? Integer.MAX_VALUE : 1)
				.filter(p -> p.toString().endsWith(".xmi"))
				.map(p -> p.toFile())
				.filter(fileFilter)
				.map(f -> new IGeneratedModelReference.FileModelReference(f.getAbsolutePath(), metamodel))
				.limit(limit)
				.collect(Collectors.toList());
		} catch (IOException e) {
			throw new IStorageStrategy.StorageException(e);
		}
	}

	public FolderBasedModelRetriever withFileFilter(Predicate<File> f) {
		this.fileFilter  = f;
		return this;
	}
	
	public FolderBasedModelRetriever withLimit(int limit) {
		this.limit = limit;
		return this;
	}

}
