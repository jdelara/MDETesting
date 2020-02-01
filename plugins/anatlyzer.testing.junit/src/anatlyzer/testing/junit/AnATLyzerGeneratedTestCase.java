package anatlyzer.testing.junit;

import java.io.File;
import java.util.stream.Collectors;

import anatlyzer.testing.junit.ManualModelsTestCase.Metadata;
import anatlyzer.testing.junit.ManualModelsTestCase.ModelInfo;
import anatlyzer.testing.modelgen.IGeneratedModelReference;

public class AnATLyzerGeneratedTestCase {
	private Metadata metadata;
	private IGeneratedModelReference model;

	public AnATLyzerGeneratedTestCase(Metadata m, IGeneratedModelReference r) {
		this.metadata = m.clone();
		this.model = r;

		// TODO: This assumes only one input model
		ModelInfo mi = metadata.getInputModels().get(0);
		File file = model.getAttribute(File.class);		
		// Otherwise it doesn't work
		mi.setModelPath("file:/" + file.getAbsolutePath());
		
		for (ModelInfo modelInfo : metadata.getOutputModels()) {
			String folder = metadata.getOutputFolder();			
			modelInfo.setModelPath(folder + File.separator + modelInfo.getModelName().toLowerCase() + "_" + file.getName());
		}		
	}
	
	public Metadata getMetadata() {
		return metadata;
	}
	
	public void execute() throws Exception {
		// TODO: This assumes only one input model
		ModelInfo mi = metadata.getInputModels().get(0);
		File file = model.getAttribute(File.class);		
		// Otherwise it doesn't work
		mi.setModelPath("file:/" + file.getAbsolutePath());
		
		for (ModelInfo modelInfo : metadata.getOutputModels()) {
			String folder = metadata.getOutputFolder();			
			modelInfo.setModelPath(folder + File.separator + modelInfo.getModelName().toLowerCase() + "_" + file.getName());
		}
		
		metadata.execute();
	}
	
	@Override
	public String toString() {
		return "Inputs: " + model.getAttribute(File.class).getName();
		//return "Inputs: " 
		//		metadata.getInputModels().stream().map(m -> m.getModelPath()).collect(Collectors.joining(", "));
		
	}
}
