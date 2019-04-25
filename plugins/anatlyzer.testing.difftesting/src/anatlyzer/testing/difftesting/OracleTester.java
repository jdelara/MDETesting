package anatlyzer.testing.difftesting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import anatlyzer.testing.common.FileUtils;
import anatlyzer.testing.common.IModel;
import anatlyzer.testing.common.IPartialOracle;
import anatlyzer.testing.common.IProgressMonitor;
import anatlyzer.testing.common.ITransformation;
import anatlyzer.testing.common.ITransformation.ModelSpec;
import anatlyzer.testing.common.ITransformationConfigurator;
import anatlyzer.testing.common.ITransformationLauncher;
import anatlyzer.testing.common.ITransformationLauncher.IExecutionTimeRecorder;
import anatlyzer.testing.common.ITransformationLauncher.TransformationExecutionError;
import anatlyzer.testing.difftesting.DifferentialTestingReport.RecordOk;
import anatlyzer.testing.difftesting.DifferentialTestingReport.RecordOracleFailure;
import anatlyzer.testing.difftesting.DifferentialTestingReport.RecordSaveError;
import anatlyzer.testing.modelgen.IGeneratedModelReference;
import anatlyzer.testing.modelgen.IModelGenerator;

/**
 * This is similar to DifferentialTester. We could try to factorize some code.
 *  
 * @author jesus
 */
public class OracleTester<
	T1 extends ITransformation, 
	T2 extends ITransformation, 
	L1 extends ITransformationLauncher, 
	L2 extends ITransformationLauncher> extends DifferentialTester<T1, T2, L1, L2>{

		
	private @NonNull IPartialOracle oracle;

	public OracleTester(@NonNull T1 trafo1, @NonNull T2 trafo2,
			@NonNull ITransformationConfigurator<T1, L1> configurator1,
			@NonNull ITransformationConfigurator<T2, L2> configurator2,
			@NonNull IModelGenerator modelGenerator,
			@NonNull IPartialOracle oracle) {
		super(trafo1, trafo2, configurator1, configurator2, modelGenerator, null);
		this.oracle = oracle;
	}
		
	/**
	 * Executes the tester
	 * @return 
	 */
	public DifferentialTestingReport test(@NonNull IProgressMonitor monitor) {
		DifferentialTestingReport report = new DifferentialTestingReport();
		
		// 2. Generate models
		monitor.beginWork("Model generation", 1);
		List<IGeneratedModelReference> generated = modelGenerator.generateModels(monitor);
		monitor.workDone("Model generation", 1);
		
		// 3. Get all generated models 
		TEST_GENERATED_MODEL:
		for (IGeneratedModelReference model : generated) {
			System.out.println("Diff. testing with input model: " + model);
			//
			// 3.1 Execute the original and the other
			ITransformationLauncher launcher1 = configurator1.configure(this.transformation1, model);			
			ITransformationLauncher launcher2 = configurator2.configure(this.transformation2, model);
			
			long time1 = -1;
			long time2 = -1;
			
			boolean isTrafo1Ok = false;
			ITransformation executing = this.transformation1;
			try {
				launcher1.exec();
				if ( launcher1 instanceof IExecutionTimeRecorder) {
					time1 = ((IExecutionTimeRecorder) launcher1).getTime();
				}
				isTrafo1Ok = true;
				executing = this.transformation2;
				launcher2.exec();
				if ( launcher2 instanceof IExecutionTimeRecorder) {
					time2 = ((IExecutionTimeRecorder) launcher2).getTime();
				}				
			} catch (TransformationExecutionError e) {
				if ( ! isTrafo1Ok )
					throw new IllegalStateException("Trafo1 is failing!");
				report.addError(this.transformation1, this.transformation2, executing, model, e);
				if ( ! retryStrategy.continueOnException(e) )
					break;
				continue TEST_GENERATED_MODEL;
			} catch ( Exception e ) {
				e.printStackTrace();
				throw new IllegalStateException("Any transformation error should be wrapped into a TransformationExecutionError");
			}
		
			List<String> nonConformantTargetModels0 = new ArrayList<String>();
			List<String> nonConformantTargetModels1 = new ArrayList<String>();
			
			List<? extends ModelSpec> tgts = this.transformation2.getTargets();
			COMPARE_TARGETS:
			for (ModelSpec tgt : tgts) {
				String tgtModelName = tgt.getModelName();		
				
				IModel r0 = launcher1.getOutput(tgtModelName);
				IModel r1 = launcher2.getOutput(tgtModelName);
				
				boolean valid0 = validate(r0.getResource());
				boolean valid1 = validate(r1.getResource());
				if ( ! valid0 ) {
					nonConformantTargetModels0.add(tgtModelName);
				}
				if ( ! valid1 ) {
					nonConformantTargetModels1.add(tgtModelName);
				}
				
				if ( saveModels ) {
					try {
						File f0 = r0.getAttribute(File.class);
						File f1 = r1.getAttribute(File.class);
						FileUtils.mkFolderForFile(f0);
						FileUtils.mkFolderForFile(f1);
						
						r0.save();
						r1.save();
					} catch (Exception e) {
						// Report and continue?
						// Throw a proper test exception?
						RecordSaveError record = report.addSaveError(transformation1, transformation2, model, e);
						addNonConformant(record, nonConformantTargetModels0, nonConformantTargetModels1);
						continue TEST_GENERATED_MODEL;
					}
				}

				boolean originalResult = oracle.compare(model, r0);
				boolean otherResult = oracle.compare(model, r1);
				
				if ( ! originalResult || ! otherResult ) {
					RecordOracleFailure record = report.addOracleFailure(transformation1, transformation2, model, r0, r1, originalResult, otherResult);
					addNonConformant(record, nonConformantTargetModels0, nonConformantTargetModels1);
					if ( ! retryStrategy.continueOnComparisonMismatch("<unknown-cause>") )
						break TEST_GENERATED_MODEL;
					continue TEST_GENERATED_MODEL;					
				}
				
			}
			
			RecordOk record = report.addTestOk(transformation1, transformation2, model);
			record.withExecutionTime(time1, time2);
			addNonConformant(record, nonConformantTargetModels0, nonConformantTargetModels1);
		}
		
		return report;
	}
}
