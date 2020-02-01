package anatlyzer.testing.ui.commands;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import anatlyzer.atl.analyser.AnalysisResult;
import anatlyzer.atl.analyser.namespaces.MetamodelNamespace;
import anatlyzer.atl.editor.AtlEditorExt;
import anatlyzer.atl.index.AnalysisIndex;
import anatlyzer.atl.util.ATLUtils;
import anatlyzer.atl.util.ATLUtils.ModelInfo;
import anatlyzer.atl.witness.IWitnessFinder;
import anatlyzer.atl.witness.WitnessUtil;
import anatlyzer.testing.common.Metamodel;
import anatlyzer.testing.modelgen.FolderBasedStorageStrategy;
import anatlyzer.testing.modelgen.IModelGenerator;
import anatlyzer.testing.modelgen.ModelGenerationStrategy;
import anatlyzer.testing.modelgen.ModelGeneratorWitnessFinder;
import anatlyzer.testing.modelgen.UseModelValidatorModelGenerator;
import anatlyzer.testing.modelgen.atl.PathBasedModelGenerator;
import anatlyzer.testing.modelgen.random.RandomModelGenerator;
import anatlyzer.testing.ui.EclipseTestProgressMonitor;
import anatlyzer.testing.ui.wizards.TestingConfigurationWizard;
import anatlyzer.testing.ui.wizards.TestingConfigurationWizard.ModelGenerationKind;

public class OpenModelGenerationWizardHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if ( editor instanceof AtlEditorExt ) {
			AtlEditorExt atlEditor = (AtlEditorExt) editor;
			IFile file = (IFile) atlEditor.getUnderlyingResource();
			
			AnalysisResult r = AnalysisIndex.getInstance().getAnalysisOrLoad(file);
			if ( r == null )
				return null;
			
			TestingConfigurationWizard wizard = new TestingConfigurationWizard();
			WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
			int code = dialog.open();
			if (code == WizardDialog.OK) {
				ModelGenerationKind kind = wizard.getModelGenerationKind();
				generateModels(file, r, kind);
			}
		}
		
		return null;
	}

	// TODO: Do this with a UI option
	private static int MODEL_GENERATION_LIMIT = 100;
	
	/**
	 * Generate models with a specific generator (parameter kind). 
	 * 
	 * @param file The ATL file for which to generate models. It provides access to the project.
	 * @param result The AnATLyzer analysis
	 * @param kind The kind of model generation
	 */
	private void generateModels(IFile file, AnalysisResult result, ModelGenerationKind kind) {
		ModelGenerationJob job = new ModelGenerationJob(file, result, kind);
		// Refresh, do something...
		// job.addJobChangeListener(listener);
		job.schedule();
	}
		
	private IFolder getOrCreateFolder(IContainer project, String name) {
		IFolder folder = project.getFolder(new Path(name));
		if (! folder.exists()) {
			try {
				folder.create(true, true, null);
			} catch (CoreException e) {
				// TODO: Notify to the user
				e.printStackTrace();
				return null;
			}
		}
		return folder;
	}
	
	private class ModelGenerationJob extends Job {

		private IFile file;
		private AnalysisResult result;
		private ModelGenerationKind kind;

		public ModelGenerationJob(IFile file, AnalysisResult result, ModelGenerationKind kind) {
			super("Model generation");
			this.file = file;
			this.result = result;
			this.kind = kind;
		}

		@Override
		protected IStatus run(org.eclipse.core.runtime.IProgressMonitor monitor) {
			IProject project = file.getProject();
			IFolder testCasesDir = getOrCreateFolder(project, "test-cases");
			if (testCasesDir == null)
				return Status.CANCEL_STATUS;
			
			IFolder trafoDir = getOrCreateFolder(testCasesDir, getFolderName(file, result));
			if (trafoDir == null)
				return Status.CANCEL_STATUS;

			IFolder generationDir = getOrCreateFolder(trafoDir, kind.name().toLowerCase().toString() + "-coverage");
			if (generationDir == null)
				return Status.CANCEL_STATUS;

			
			String cacheDir = generationDir.getLocation().toOSString();
			FolderBasedStorageStrategy storage = new FolderBasedStorageStrategy(cacheDir);
			
			List<ModelInfo> inputs = ATLUtils.getModelInfo(result.getATLModel()).stream().filter(ModelInfo::isInput).collect(Collectors.toList());
			IWitnessFinder wf = WitnessUtil.getFirstWitnessFinder();
			
			for(ModelInfo input : inputs) {
				MetamodelNamespace ns = result.getNamespace().getNamespace(input.getMetamodelName());
				Metamodel mm = new Metamodel(ns.getResource());
				
				IModelGenerator generator;
				switch(kind) {
				case PATH:
					generator = new PathBasedModelGenerator(result.getAnalyser(), storage, wf).
						withLimit(MODEL_GENERATION_LIMIT);
					break;
				case RANDOM:
					generator = new RandomModelGenerator(storage, cacheDir, new ModelGeneratorWitnessFinder(), mm).
						withNumberOfModels(50).		// generate 50 models ...
						withSize(100, 0.2);			// of size 100 +- 20%	
					break;
				case METAMODEL:
					// TODO: We may want to have an option to slice
					generator = new UseModelValidatorModelGenerator(mm, 
							ModelGenerationStrategy.STRATEGY.Lite, storage, wf).
							// withMetamodelViewFilter(footprintMetamodel).
							withLimit(MODEL_GENERATION_LIMIT);	
					break;
				default:
					throw new IllegalStateException();
				}				
	
				EclipseTestProgressMonitor testMonitor = new EclipseTestProgressMonitor(monitor);
				generator.generateModels(testMonitor);
				if (testMonitor.isCancelled()) {
					return Status.CANCEL_STATUS;
				}
			}

			return Status.OK_STATUS;
		}
	}
	

	// TODO: Do this well
	private String getFolderName(IFile file, AnalysisResult result) {
		String name = file.getName();
		return name.replace(".atl", "");
	}

}
