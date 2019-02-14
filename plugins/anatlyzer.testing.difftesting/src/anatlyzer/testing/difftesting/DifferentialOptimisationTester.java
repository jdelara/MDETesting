package anatlyzer.testing.difftesting;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import anatlyzer.testing.common.IComparator;
import anatlyzer.testing.common.IModel;
import anatlyzer.testing.common.IProgressMonitor;
import anatlyzer.testing.common.ITransformation;
import anatlyzer.testing.common.ITransformation.ModelSpec;
import anatlyzer.testing.common.ITransformationConfigurator;
import anatlyzer.testing.common.ITransformationLauncher;
import anatlyzer.testing.common.ITransformationLauncher.TransformationExecutionError;
import anatlyzer.testing.common.Metamodel;
import anatlyzer.testing.modelgen.IGeneratedModelReference;
import anatlyzer.testing.modelgen.IModelGenerator;

public class DifferentialOptimisationTester<T extends ITransformation, L extends ITransformationLauncher> {

	
	private final @NonNull T transformation;
	private final @NonNull IOptimiser<T> optimiser;
	private final @NonNull IModelGenerator modelGenerator;
	private final @NonNull ITransformationConfigurator<T, L> configurator;
	private final @NonNull IComparator comparator;

	public DifferentialOptimisationTester(@NonNull T trafo, @NonNull IOptimiser<T> optimiser, @NonNull IModelGenerator modelGenerator, @NonNull ITransformationConfigurator<T, L> configurator, @NonNull IComparator comparator) {
		this.transformation = trafo;
		this.optimiser = optimiser;
		this.modelGenerator = modelGenerator;
		this.configurator = configurator;
		this.comparator = comparator;
	}
	
	/**
	 * Executes the tester
	 */
	public void test(@NonNull IProgressMonitor monitor) {
		
		// 1. Mutate the original transformation 
		monitor.beginWork("Optimise", 1);
		T optimised = optimiser.optimise(this.transformation);
		monitor.workDone("Optimised", 1);
		
		// 2. Generate models
		monitor.beginWork("Model generation", 1);
		List<IGeneratedModelReference> generated = modelGenerator.generateModels(monitor);
		monitor.workDone("Model generation", 1);
		
		// 3. Get all generated models 
		for (IGeneratedModelReference model : generated) {
			//
			// 3.1 Execute the original and the other
			ITransformationLauncher launcher1 = configurator.configure(this.transformation, model);
			ITransformationLauncher launcher2 = configurator.configure(optimised, model);
			
			try {
				launcher1.exec();
				launcher2.exec();
			} catch (TransformationExecutionError e) {
				throw new RuntimeException("TODO: Record this properly", e);
			}
		

			List<? extends ModelSpec> tgts = this.transformation.getTargets();
			for (ModelSpec tgt : tgts) {
				String tgtModelName = tgt.getModelName();		
				
				IModel r0 = launcher1.getOutput(tgtModelName);
				IModel r1 = launcher2.getOutput(tgtModelName);
				
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
