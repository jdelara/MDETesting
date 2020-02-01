package anatlyzer.testing.junit;

import java.util.stream.Collectors;

import anatlyzer.testing.junit.ManualModelsTestCase.Metadata;

public class AnATLyzerTestCase {
	private Metadata metadata;

	public AnATLyzerTestCase(Metadata m) {
		this.metadata = m;
	}
	
	public Metadata getMetadata() {
		return metadata;
	}
	
	public void execute() throws Exception {
		metadata.execute();
	}
	
	@Override
	public String toString() {
		return "Inputs: " +
				metadata.getInputModels().stream().map(m -> m.getModelPath()).collect(Collectors.joining(", "));
		
	}
}
