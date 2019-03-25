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

	public FolderBasedModelRetriever(File file, Metamodel metamodel) {
		this.folder = file;
		this.metamodel = metamodel;
	}
	
	public FolderBasedModelRetriever(String fileName, Metamodel metamodel) {
		this(new File(fileName), metamodel);
	}


	@Override
	public List<IGeneratedModelReference> generateModels(IProgressMonitor monitor) {
		try {
			return Files.list(folder.toPath())
				.filter(p -> p.toString().endsWith(".xmi"))
				.map(p -> p.toFile())
				.filter(fileFilter)
				.map(f -> new IGeneratedModelReference.FileModelReference(f.getAbsolutePath(), metamodel))
				.collect(Collectors.toList());
		} catch (IOException e) {
			throw new IStorageStrategy.StorageException(e);
		}
	}

	public FolderBasedModelRetriever withFileFilter(Predicate<File> f) {
		this.fileFilter  = f;
		return this;
	}

}
