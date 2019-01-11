package anatlyzer.testing.modelgen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.xmi.XMIResource;
import anatlyzer.atl.witness.IWitnessModel;
import anatlyzer.testing.common.Metamodel;

public class FolderBasedStorageStrategy implements IStorageStrategy {

	private File folder;
	private int count;
	
	public FolderBasedStorageStrategy(File f) {
		if ( ! f.exists() ) {
			f.mkdirs();
		}
		
		this.folder = f;
	}
	
	public FolderBasedStorageStrategy(String fileName) {
		this(new File(fileName));
	}

	@Override
	public IGeneratedModelReference save(IWitnessModel model, Metamodel metamodel) {
		String modelFile = folder.getAbsolutePath() + File.separator + "mutant_" + count + ".xmi";
		count++;
		try {
			Map<Object, Object> options = new HashMap<>();
			options.put(XMIResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
			model.getModelAsOriginal().save(new FileOutputStream(modelFile), options);
			return new IGeneratedModelReference.FileModelReference(modelFile, metamodel);
		} catch (IOException e) {
			throw new StorageException(e);
		}
	}

}
