package anatlyzer.testing.atl.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.annotation.NonNull;

import anatlyzer.atl.analyser.IAnalyserResult;
import anatlyzer.atl.analyser.namespaces.MetamodelNamespace;
import anatlyzer.atl.tests.api.AnalysisLoader;
import anatlyzer.atl.tests.api.AtlLoader;
import anatlyzer.atl.tests.api.AtlLoader.LoadException;
import anatlyzer.atl.util.ATLUtils;
import anatlyzer.atl.util.ATLUtils.ModelInfo;
import anatlyzer.testing.common.ITransformation;
import anatlyzer.testing.common.Metamodel;

public class AtlTransformation implements ITransformation {

	private IAnalyserResult analysis;
	private List<ModelSpec> sources;
	private List<ModelSpec> targets;
	private String fileName;
	
	private AtlTransformation() { }
	
	public AtlTransformation(String fileName, @NonNull IAnalyserResult analysis) {
		this.fileName = fileName;
		this.analysis = analysis;
		this.sources = new ArrayList<ITransformation.ModelSpec>();
		this.targets = new ArrayList<ITransformation.ModelSpec>();
		
		for (ModelInfo m : ATLUtils.getModelInfo(analysis.getATLModel())) {
			MetamodelNamespace ns = analysis.getNamespaces().getNamespace(m.getMetamodelName());
			if ( m.isInput() ) {
				sources.add(new ModelSpec(m.getModelName(), m.getMetamodelName(), new Metamodel(ns.getResource())));
			} else if ( m.isOutput() ) {
				targets.add(new ModelSpec(m.getModelName(), m.getMetamodelName(), new Metamodel(ns.getResource())));
			}
		}
	}

	@NonNull 
	public IAnalyserResult getAnalysis() {
		return analysis;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getUnitName() {
		return this.analysis.getATLModel().getRoot().getName();
	}
	
	@Override
	public List<? extends ModelSpec> getSources() {		
		return sources;
	}

	@Override
	public List<? extends ModelSpec> getTargets() {
		return targets;
	}

	public static AtlTransformation fromFile(String fileName,  Object[] metamodels, String[] names) throws LoadException {
		Resource r = AtlLoader.load(fileName);
		AnalysisLoader loader = AnalysisLoader.fromResource(r, metamodels, names);
		IAnalyserResult analysis = loader.analyse().getAnalyser();
		
		return new AtlTransformation(fileName, analysis);
	}

	public AtlTransformation copyAs(String resultFile) {
		AtlTransformation trafo = new AtlTransformation();
		trafo.fileName = resultFile;
		trafo.analysis = analysis; // Not sure about this
		trafo.sources = new ArrayList<ITransformation.ModelSpec>(sources);
		trafo.targets = new ArrayList<ITransformation.ModelSpec>(targets);
		return trafo;
	}
	
	

}
