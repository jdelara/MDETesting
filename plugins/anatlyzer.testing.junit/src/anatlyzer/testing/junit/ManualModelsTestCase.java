package anatlyzer.testing.junit;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import anatlyzer.atl.tests.api.StandaloneUSEWitnessFinder;
import anatlyzer.atl.tests.api.AtlLoader.LoadException;
import anatlyzer.atl.witness.IWitnessFinder;
import anatlyzer.atl.witness.WitnessUtil;
import anatlyzer.testing.atl.common.AtlLauncher;
import anatlyzer.testing.atl.common.AtlTransformation;
import anatlyzer.testing.atl.launching.ATLExecutor;
import anatlyzer.testing.atl.launching.ATLExecutor.ModelData;
import anatlyzer.testing.common.IComparator;
import anatlyzer.testing.common.IModel;
import anatlyzer.testing.common.IProgressMonitor;
import anatlyzer.testing.common.Model;
import anatlyzer.testing.comparison.CompositeComparator;
import anatlyzer.testing.comparison.emfcompare.EMFCompareComparator;
import anatlyzer.testing.comparison.jgrapht.JGraphtModelComparator;
import anatlyzer.testing.comparison.xmi.XMIComparator;
import anatlyzer.testing.modelgen.AbstractModelGenerator;
import anatlyzer.testing.modelgen.FolderBasedStorageStrategy;
import anatlyzer.testing.modelgen.IGeneratedModelReference;
import anatlyzer.testing.modelgen.atl.PathBasedModelGenerator;

public class ManualModelsTestCase {

	public static class ModelInfo implements Cloneable {
		private String modelName;
		private String metamodelName;
		private String metamodel;
		private String modelPath;

		public ModelInfo(String modelName, String metamodelName, String metamodel) {
			this.modelName = modelName;
			this.metamodelName = metamodelName;
			this.metamodel = metamodel;
		}
		
		public String getModelName() {
			return modelName;
		}
		
		public String getMetamodel() {
			return metamodel;
		}
		
		public String getMetamodelName() {
			return metamodelName;
		}
		
		public void setModelPath(String modelPath) {
			this.modelPath = modelPath;
		}
		
		public String getModelPath() {
			return modelPath;
		}
		
		@Override
		protected ModelInfo clone() throws CloneNotSupportedException {
			return (ModelInfo) super.clone();
		}
	}

//	public static interface ModelConfiguration {
//		public void withInputModel(String modelName, String path);
//			
//		public void withExpectedOutput(String name, String path);
//	}
//	
	
	public static class Metadata implements Cloneable {
	
		private List<ModelInfo> inputModels = new ArrayList<>();
		private List<ModelInfo> outputModels = new ArrayList<>();
		private String outputFolder;
		
		private File trafo;

		public Metadata(String trafo) {
			this.trafo = new File(trafo);
		}
		
		public Metadata configureInModel(String modelName, String metamodelName, String metamodel) {
			inputModels.add(new ModelInfo(modelName, metamodelName, metamodel));
			return this;
		}

		public Metadata configureOutModel(String modelName, String metamodelName, String metamodel) {
			outputModels.add(new ModelInfo(modelName, metamodelName, metamodel));
			return this;
		}
		
		public Metadata configureOutputFolder(String path) {
			this.outputFolder = path;
			return this;
		}
		
		public List<? extends ModelInfo> getInputModels() {
			return inputModels;
		}
		
		public List<? extends ModelInfo> getOutputModels() {
			return outputModels;
		}
		
		public String getOutputFolder() {
			return outputFolder;
		}
		
		public void execute() throws Exception {			
			AtlTransformation atlTrafo = getTrafo();

			List<ModelData> models = new ArrayList<ModelData>();

			ATLExecutor executor = new ATLExecutor();		
			for(ModelInfo m : inputModels) {
				ModelData md = ATLExecutor.inModel(m.modelName, m.modelPath, m.metamodelName, m.metamodel);				
				models.add(md);
			}
			for(ModelInfo m : outputModels) {
				ModelData md = ATLExecutor.outModel(m.modelName, m.modelPath, m.metamodelName, m.metamodel);				
				models.add(md);
			}
			
			executor.withModels(models);
			
			// executor.perform(trafo.getAbsolutePath(), models.toArray(new ModelData[0]));
		

			AtlLauncher launcher = new AtlLauncher(executor, atlTrafo);
			launcher.exec();

			File outputFolder = new File("outputs/manual");
			if (! outputFolder.exists()) {
				outputFolder.mkdirs();
			}				

			
			for(ModelInfo m : outputModels) {
				String mName = m.modelName;
				
				IModel outputModel = launcher.getOutput(mName);
				outputModel.save();		
				
				ResourceSet rs = new ResourceSetImpl();	
				outputModel.getMetamodel().getPackages().forEach(p -> {
					rs.getPackageRegistry().put(p.getNsURI(), p);
				});
				
				String expectedModelPath = expectedOutputModels.get(mName);
				if (expectedModelPath == null)
					continue;
				
				File file = new File(expectedModelPath);
				Resource r = rs.getResource(URI.createFileURI(file.getAbsolutePath()), true);
				Model expectedOutModel = new Model(r, outputModel.getMetamodel());
				expectedOutModel.addAttribute(File.class, file);
				
				IComparator comparator = getComparator();
				boolean result = comparator.compare(expectedOutModel, outputModel);
				assertTrue("Different models", result);				
			}
		}
		

		private AtlTransformation getTrafo() throws LoadException {
			List<String> metamodelNames = new ArrayList<String>();
			List<String> metamodels = new ArrayList<String>();

			List<ModelInfo> all = new ArrayList<>(inputModels);
			all.addAll(outputModels);
			for(ModelInfo m : all) {
				metamodelNames.add(m.metamodelName);
				metamodels.add(m.metamodel);
			}
			
			String[] names = metamodelNames.toArray(new String[0]);
			String[] metamodelFiles = metamodels.toArray(new String[0]);
			
			return AtlTransformation.fromFile(trafo.getAbsolutePath(), metamodelFiles, names);
		}

		public void withInputModel(String modelName, String path) {
			for (ModelInfo m : inputModels) {
				if (m.modelName.equals(modelName)) {
					m.setModelPath(path);
					return;
				}
			}
		}


		private Map<String, String> expectedOutputModels = new HashMap<>();
		private List<AnATLyzerTestCase> testCases = new ArrayList<AnATLyzerTestCase>();
		
		public void withExpectedOutput(String name, String path) {
			expectedOutputModels.put(name, path);
		}
		
		public void withOutputModel(String modelName, String path) {
			for (ModelInfo m : outputModels) {
				if (m.modelName.equals(modelName)) {
					m.setModelPath(path);
					return;
				}
			}
		}

		public ModelInfo getInputModel(String modelName) {
			for (ModelInfo m : inputModels) {
				if (m.modelName.equals(modelName)) {
					return m;
				}
			}
			return null;
		}
		
		public ModelInfo getOutputModel(String modelName) {
			for (ModelInfo m : outputModels) {
				if (m.modelName.equals(modelName)) {
					return m;
				}
			}
			return null;
		}
				
		public void addTestCase(Object... args) {
			if (args.length % 2 != 0) 
				throw new IllegalArgumentException("Expected a even number of arguments");
			
			if (outputFolder == null)
				throw new IllegalStateException("No output folder has been configured. Use Metadata.configureOutputFolder to do it.");
			
			Metadata m = this.clone();
			for (int i = 0; i < args.length; i += 2) {
				String modelName = (String) args[i];
				String model = (String) args[i + 1];
					
				ModelInfo input = getInputModel(modelName);
				if (input != null) {
					m.withInputModel(modelName, model);
				} else {
					ModelInfo output = getOutputModel(modelName);
					if (output == null)
						throw new IllegalArgumentException("Model " + modelName + " not found");
										
					m.withExpectedOutput(modelName, model);
					
					File expectedFile = new File(model);
					m.withOutputModel(modelName, outputFolder + File.separator + expectedFile.getName());
				}			
			}			
			
			AnATLyzerTestCase testcase = new AnATLyzerTestCase(m);
			testCases.add(testcase);
		}
		
		public Collection<AnATLyzerGeneratedTestCase> generatePathTestCases(String cacheDir, int limit) {
			try {
				// IWitnessFinder wf = WitnessUtil.getFirstWitnessFinder();
				StandaloneUSEWitnessFinder wf = new StandaloneUSEWitnessFinder();				
				
				FolderBasedStorageStrategy storage = new FolderBasedStorageStrategy(cacheDir);
				
				AbstractModelGenerator generator = new PathBasedModelGenerator(getTrafo().getAnalysis(), storage, wf).
						withLimit(limit);
				
				List<IGeneratedModelReference> refs = generator.generateModels(IProgressMonitor.NULL);
				return refs.stream().map(r -> new AnATLyzerGeneratedTestCase(this, r)).collect(Collectors.toList());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		public Collection<AnATLyzerTestCase> getTestCases() {
			return testCases;
		}
		
		
		protected IComparator getComparator() {
			IComparator emf = new EMFCompareComparator();
			IComparator xmi = new XMIComparator();
			IComparator iso = new JGraphtModelComparator();
			
			IComparator comparator = new CompositeComparator(iso, xmi, emf);
			return comparator;
		}

		@Override
		protected Metadata clone() {
			try {
				Metadata m = (Metadata) super.clone();
				m.inputModels = new ArrayList<ModelInfo>();
				m.outputModels = new ArrayList<ModelInfo>();
				m.expectedOutputModels = new HashMap<String, String>();
				
				for(ModelInfo mi : this.inputModels) {
					m.inputModels.add(mi.clone());
				}
				
				for(ModelInfo mi : this.outputModels) {
					m.outputModels.add(mi.clone());
				}
				
				for (Entry<String, String> entry : this.expectedOutputModels.entrySet()) {
					m.expectedOutputModels.put(entry.getKey(), entry.getValue());
				}
				
				return m;
			} catch (CloneNotSupportedException e) {
				throw new IllegalStateException();
			}
		}
		
	}
	
}
