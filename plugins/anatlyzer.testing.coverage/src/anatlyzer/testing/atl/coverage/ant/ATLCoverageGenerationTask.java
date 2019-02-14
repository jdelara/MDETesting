package anatlyzer.testing.atl.coverage.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.eclipse.core.runtime.CoreException;

import anatlyzer.atl.editor.builder.AnalyserExecutor;
import anatlyzer.atl.editor.builder.AnalyserExecutor.AnalyserData;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atl.tests.api.AnalysisLoader;
import anatlyzer.atl.util.ATLSerializer;
import anatlyzer.atl.util.AnalyserUtils.CannotLoadMetamodel;
import anatlyzer.atl.util.AnalyserUtils.PreconditionParseError;
import anatlyzer.testing.atl.coverage.CoverageTransformer;

public class ATLCoverageGenerationTask extends Task {

	private String inputFolder;
	private String outputFolder;
	
	public String getInputFolder() {
		return inputFolder;
	}
	
	public void setInputFolder(String folder) {
		this.inputFolder = folder;
	}
	
	
	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}
	
	public String getOutputFolder() {
		return outputFolder;
	}
	
	@Override
	public void execute() throws BuildException {
		File folder = new File(inputFolder);
		
		File[] files = folder.listFiles(f -> f.getName().endsWith(".atl"));
		for (File atl : files) {
			try {
				AnalyserData exec = new AnalyserExecutor().exec(new FileInputStream(atl));
				ATLModel model = exec.getATLModel();
				CoverageTransformer transformer = new CoverageTransformer(model);
				transformer.transform();
				
				ATLSerializer.serialize(
						model, 
						outputFolder + File.separator + atl.getName().replace("atl$", "_coverage.atl"));
			} catch (IOException | CoreException | CannotLoadMetamodel | PreconditionParseError e) {
				throw new BuildException(e);
			}
			
		}
		
		
	}
	
}
