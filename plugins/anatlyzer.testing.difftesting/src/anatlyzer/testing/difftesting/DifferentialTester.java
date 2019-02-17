package anatlyzer.testing.difftesting;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import anatlyzer.testing.common.FileUtils;
import anatlyzer.testing.common.IComparator;
import anatlyzer.testing.common.IModel;
import anatlyzer.testing.common.IProgressMonitor;
import anatlyzer.testing.common.ITransformation;
import anatlyzer.testing.common.ITransformation.ModelSpec;
import anatlyzer.testing.common.ITransformationConfigurator;
import anatlyzer.testing.common.ITransformationLauncher;
import anatlyzer.testing.common.ITransformationLauncher.TransformationExecutionError;
import anatlyzer.testing.modelgen.IGeneratedModelReference;
import anatlyzer.testing.modelgen.IModelGenerator;

public class DifferentialTester<
	T1 extends ITransformation, 
	T2 extends ITransformation, 
	L1 extends ITransformationLauncher, 
	L2 extends ITransformationLauncher> {

	
	private final @NonNull T1 transformation1;
	private final @NonNull T2 transformation2;
	private final @NonNull IModelGenerator modelGenerator;
	private final @NonNull ITransformationConfigurator<T1, L1> configurator1;
	private final @NonNull ITransformationConfigurator<T2, L2> configurator2;
	private final @NonNull IComparator comparator;
	private boolean saveModels;
	
	public DifferentialTester(@NonNull T1 trafo1, @NonNull T2 trafo2,
			@NonNull ITransformationConfigurator<T1, L1> configurator1,
			@NonNull ITransformationConfigurator<T2, L2> configurator2,
			@NonNull IModelGenerator modelGenerator,
			@NonNull IComparator comparator) {
		this.transformation1 = trafo1;
		this.transformation2 = trafo2;
		this.modelGenerator = modelGenerator;
		this.configurator1 = configurator1;
		this.configurator2 = configurator2;		
		this.comparator = comparator;
	}
	
	public DifferentialTester<T1, T2, L1, L2> withSaveModels(boolean save) {
		this.saveModels = save;
		return this;
	}
	
	/**
	 * Executes the tester
	 */
	public void test(@NonNull IProgressMonitor monitor) {
		DifferentialTestingReport report = new DifferentialTestingReport();
		
		// 2. Generate models
		monitor.beginWork("Model generation", 1);
		List<IGeneratedModelReference> generated = modelGenerator.generateModels(monitor);
		monitor.workDone("Model generation", 1);
		
		// 3. Get all generated models 
		for (IGeneratedModelReference model : generated) {
			//
			// 3.1 Execute the original and the other
			ITransformationLauncher launcher1 = configurator1.configure(this.transformation1, model);
			ITransformationLauncher launcher2 = configurator2.configure(this.transformation2, model);
			
			try {
				launcher1.exec();
				launcher2.exec();
			} catch (TransformationExecutionError e) {
				throw new RuntimeException("TODO: Record this properly", e);
			}
		
			List<? extends ModelSpec> tgts = this.transformation2.getTargets();
			for (ModelSpec tgt : tgts) {
				String tgtModelName = tgt.getModelName();		
				
				IModel r0 = launcher1.getOutput(tgtModelName);
				IModel r1 = launcher2.getOutput(tgtModelName);
				
				if ( saveModels ) {
					try {
						File f0 = r0.getAttribute(File.class);
						File f1 = r1.getAttribute(File.class);
						FileUtils.mkFolderForFile(f0);
						FileUtils.mkFolderForFile(f1);
						
						r0.save();
						r1.save();
					} catch (IOException e) {
						// Report and continue?
						// Throw a proper test exception?
						throw new RuntimeException(e);
					}
				}
				
				// 3.2 Compare each one
				boolean equals = comparator.compare(r0, r1);
				System.out.println("Comparing : " + equals);
				if ( ! equals ) {
					
					System.out.println("Failed optimisation!");
					throw new UnsupportedOperationException("Record this and continue");
				}
			}
			
			// addToReport
		}
	}
	
}
