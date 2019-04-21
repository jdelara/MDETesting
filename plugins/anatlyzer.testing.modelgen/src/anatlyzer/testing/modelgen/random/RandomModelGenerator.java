package anatlyzer.testing.modelgen.random;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Range;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import anatlyzer.atl.witness.IWitnessFinder;
import anatlyzer.testing.common.IProgressMonitor;
import anatlyzer.testing.common.Metamodel;
import anatlyzer.testing.modelgen.AbstractModelGenerator;
import anatlyzer.testing.modelgen.IGeneratedModelReference;
import anatlyzer.testing.modelgen.IModelGenerator;
import anatlyzer.testing.modelgen.IStorageStrategy;
import fr.inria.atlanmod.instantiator.GenerationException;
import fr.inria.atlanmod.instantiator.GenericMetamodelConfig;
import fr.inria.atlanmod.instantiator.GenericMetamodelGenerator;

public class RandomModelGenerator extends AbstractModelGenerator implements IModelGenerator {

	private static final int DEFAULT_MODEL_SIZE = 30;
	private static final int DEFAULT_NUM_MODELS = 100;
	private Metamodel 	metamodel;
	private long 		seed = -1;
	private float 		sizeVariation = 0.1f;			// allowed variation in model size
	private int			size = DEFAULT_MODEL_SIZE;		// model size
	private int			numberOfModels = DEFAULT_NUM_MODELS;
	private String cacheDir;		
	
	public RandomModelGenerator(IStorageStrategy strategy, String cacheDir, IWitnessFinder wf, Metamodel m) {
		super(strategy, wf);
		this.cacheDir = cacheDir;
		this.metamodel = m;
	}

	@Override
	public List<IGeneratedModelReference> generateModels(IProgressMonitor monitor) {
		
		long seed = this.seed == -1 ? System.currentTimeMillis() : this.seed;
		
		Range<Integer> range = Range.between(
				Math.round(size * (1 - sizeVariation)), 
				Math.round(size * (1 + sizeVariation)));
				
		GenericMetamodelConfig config = new GenericMetamodelConfig(metamodel.getResource(), range, seed);
		GenericMetamodelGenerator modelGen = new GenericMetamodelGenerator(config);
		
		Path folder = Paths.get(cacheDir);
		modelGen.setSamplesPath(folder);
		
		ResourceSetImpl resourceSet = new ResourceSetImpl();	
		try {
			modelGen.runGeneration(resourceSet, numberOfModels, size, this.sizeVariation);
		} catch (GenerationException e) {
			e.printStackTrace();
			return null;
		}
		
		return this.getGeneratedModels(modelGen);
	}
	
	private List<IGeneratedModelReference> getGeneratedModels(GenericMetamodelGenerator mg) {
		Path folder = Paths.get(cacheDir, mg.getMetaModelResourceName());
		File [] content = folder.toFile().listFiles();
		return Arrays.asList(content).stream().
					map(p -> new IGeneratedModelReference.FileModelReference(p.getAbsolutePath(), metamodel)).
					collect(Collectors.toList());
	}

	public RandomModelGenerator withSeed(long seed) {
		this.seed = seed;
		return this;
	}
		
	public RandomModelGenerator withSize(int size, double v) {
		this.size = size;
		this.sizeVariation = (float)v;
		return this;
	}
	
	public RandomModelGenerator withNumberOfModels(int num) {
		this.numberOfModels = num;
		return this;
	}

}
