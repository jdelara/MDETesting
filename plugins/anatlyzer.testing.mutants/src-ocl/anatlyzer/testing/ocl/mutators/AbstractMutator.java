package anatlyzer.testing.ocl.mutators;

import java.io.File;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import org.eclipse.m2m.atl.engine.parser.AtlParser;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.ATL.Helper;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.ATL.PatternElement;
import anatlyzer.atlext.ATL.Rule;
import anatlyzer.atlext.OCL.Attribute;
import anatlyzer.atlext.OCL.CollectionType;
import anatlyzer.atlext.OCL.OclContextDefinition;
import anatlyzer.atlext.OCL.OclFeatureDefinition;
import anatlyzer.atlext.OCL.OclModelElement;
import anatlyzer.atlext.OCL.OclType;
import anatlyzer.atlext.OCL.Operation;
import anatlyzer.atlext.OCL.OperationCallExp;
import anatlyzer.atlext.OCL.StringExp;
import anatlyzer.atlext.OCL.VariableDeclaration;
import anatlyzer.atlext.OCL.VariableExp;
import anatlyzer.testing.atl.mutators.ATLAbstractMutator;
import anatlyzer.testing.mutants.MuMetaModel;

public abstract class AbstractMutator extends ATLAbstractMutator {
	/**
	 * It generates all possible mutants of a certain kind. To be implemented by concrete mutation operators. 
	 * @param atlModel original transformation (name of the atl file)
	 * @param inputMM input metamodel of the transformation
	 * @param outputMM output metamodel of the transformation
	 * @param outputFolder folder where the mutants will be generated
	 */
	public abstract void generateMutants(ATLModel atlModel, MuMetaModel inputMM, MuMetaModel outputMM);
	
	protected IMutationSelectorStrategy selectorStrategy;
	
	public void setSelectorStrategy(IMutationSelectorStrategy selectorStrategy) {
		this.selectorStrategy = selectorStrategy;
	}
	
	protected LocatedElement needsToBeMutated(EObject exp) {
		return selectorStrategy.needsToBeMutated(exp);
	}
	
	protected String createComment(LocatedElement context, LocatedElement element) {
		return selectorStrategy.createComment(context, element);
	}
	
	
	/**
	 * Description of mutator, used to document the generated mutant.
	 */
	public abstract String getDescription();

//	/**
//	 * It saves the received atl model in the specified folder, only if it has no compilation errors.
//	 * @param atlModel
//	 * @param outputFolder
//	 * @param helper (OPTIONAL) name of mutated helper
//	 * @return true if the atl file and its asm compilation were generated; false otherwise.
//	 */
//	protected boolean save (EMFModel atlModel, String outputFolder ) { return save(atlModel, outputFolder, null); }
//	protected boolean save (EMFModel atlModel, String outputFolder, Helper helper) {
//		try {
//			// save atl file
//			String atl_transformation = this.getValidNameOfFile(outputFolder, helper);
//			AtlParser atlParser       = new AtlParser();
//			atlParser.extract(atlModel, atl_transformation);
//			
//			// compile transformation
//			String asm_transformation = atl_transformation.replace(".atl", ".asm");
//			if (! new File(asm_transformation).exists() ) {
//				Atl2006Compiler compiler  = new Atl2006Compiler();
//				FileInputStream trafoFile;
//				File atl_file = new File(atl_transformation);
//				trafoFile     = new FileInputStream(atl_file);
//				CompileTimeError[] errors = compiler.compile(trafoFile, asm_transformation);
//				trafoFile.close();
//					
//			// delete transformation if it has compilation errors, the compilation does not produce an asm file, or contains errors confirmed statically (mutants should compile)
//				boolean fatalErrors = false;
//				for (CompileTimeError error : errors) fatalErrors = fatalErrors || !error.getSeverity().equals("warning");
//				if  (fatalErrors || !new File(asm_transformation).exists()) {
//					System.out.println( fatalErrors? "---> [" + errors[0].getLocation() + "] " + errors[0].getDescription() : "---> no asm file could be generated or some error was detected statically");
//					atl_file.delete();
//					return false;
//				}
//				else if (!isStaticallyCorrect(atl_transformation)) {
//					System.out.println( "---> an error was detected statically");
//					atl_file.delete();
//					new File(asm_transformation).delete();
//					return false;
//				}
//				
//				return true;
//			}
//		} 
//		catch (ATLCoreException e) {}
//		catch (FileNotFoundException e) {} 
//		catch (IOException e) {}
//		
//		return false;
//	}
	
	/**
	 * It returns the list of comments of a module.
	 * @param atlModel
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected EDataTypeEList<String> getModuleComments(ATLModel atlModel) {
		// we will add a comment to the module, documenting the mutation 
		Module module = atlModel.getModule();
		EDataTypeEList<String> comments = null;
		if (module!=null) {
			EStructuralFeature f = module.eClass().getEStructuralFeature("commentsBefore");	
			comments = (EDataTypeEList<String>)module.eGet(f);
		}
		return comments;
	}
	
	
	
//	/**
//	 * It returns the next valid name for a mutant: mutant<available-i-in-directory>.model
//	 * @param outputFolder folder where the mutants will be generated
//	 * @param helper (optional) name of mutated helper
//	 */
//	private /*static*/ long index = 1;
//	private String getValidNameOfFile (String outputFolder) { return getValidNameOfFile(outputFolder, null); }
//	private String getValidNameOfFile (String outputFolder, Helper helper) {
//		String outputfile = null;
//		String aux        = null;
//		for (long i=index; outputfile==null; i++) {
//			aux = File.separatorChar + getDescription().replaceAll("\\s+","") + "_" + name(helper) + "_mutant" + i + ".atl";
//			if (!new File(outputFolder, aux).exists()) { 
//				outputfile = outputFolder + aux;
//				index = i;
//			}
//			else index = i;
//		}
//		return outputfile;
//	}	
	
	
//	/**
//	 * It checks whether the static analysis of the transformation detects errors.
//	 * It performs a very basic coarse-grained analysis (without rule conflicts or 
//	 * witness confirmation). 
//	 */
//	private boolean isStaticallyCorrect (String transformation) {		
//		boolean correct = true;
//		
//		try {
//			// analyse the transformation
//			ModelFactory      modelFactory = new EMFModelFactory();
//			EMFReferenceModel atlMetamodel = (EMFReferenceModel)modelFactory.getBuiltInResource("ATL.ecore");
//			AtlParser         atlParser    = new AtlParser();		
//			EMFModel          atlModel     = (EMFModel)modelFactory.newModel(atlMetamodel);
//			atlParser.inject (atlModel, transformation);	
//			atlModel.setIsTarget(true);	
//			ATLModel tmpAtlModel = new ATLModel(atlModel.getResource());
//			anatlyzer.atl.model.ATLModel  atlTransformation = new anatlyzer.atl.model.ATLModel(atlModel.getResource());
//			GlobalNamespace namespace = AnalyserUtils.prepare(tmpAtlModel, new IAtlFileLoader() {			
//				@Override
//				public Resource load(IFile f) {
//					EMFModel libModel = AtlEngineUtils.loadATLFile(f);
//					return libModel.getResource();
//				}
//
//				@Override
//				public Resource load(String text) {
//					EMFModel libModel = AtlEngineUtils.loadATLText(text);
//					return libModel.getResource();
//				}
//			});
//			Analyser analyser = new Analyser(namespace, atlTransformation);
//			analyser.perform();
//			
//			// identify errors of interest
//			correct = analyser.getErrors().getAnalysis().getProblems().stream().noneMatch(problem -> 
//						problem instanceof InvalidOperator || 
//						problem instanceof IteratorBodyWrongType ||
//						problem instanceof OperationCallInvalidNumberOfParameters
//						);
//		}
//		catch (Exception e) {}
//		
//		return correct;
//	}	

	/**
	 * Custom "toString" for ATL model elements
	 * @param element
	 * @return
	 */
	protected String toString (LocatedElement element) {
		String toString = "";
		if (element instanceof OclModelElement) {
			toString = ((OclModelElement)element).getName(); 
		}
		else if (element instanceof Binding) {
			toString = ((Binding)element).getPropertyName(); 
		}
		else if (element instanceof Rule) {
			toString = ((Rule)element).getName(); 
		}
		else if (element instanceof Helper) {
			toString = toString(((Helper)element).getDefinition().getFeature());
		}
		else if (element instanceof OclFeatureDefinition) {
			toString = toString(((OclFeatureDefinition)element).getFeature());
		}
		else if (element instanceof Attribute) {
			toString = ((Attribute)element).getName(); 
		}
		else if (element instanceof Operation) {
			toString = ((Operation)element).getName(); 
		}
		else if (element instanceof VariableExp) {
			toString = toString(((VariableExp)element).getReferredVariable());
		}
		else if (element instanceof VariableDeclaration) {
			toString = ((VariableDeclaration)element).getVarName();
		}
		else if (element instanceof PatternElement) {
			toString = ((PatternElement)element).getVarName();
		}
		else if (element instanceof OperationCallExp) {
			toString = ((OperationCallExp)element).getOperationName();
		}
		else if (element instanceof OclType) {
			toString = element.eClass().getName();
		}
		else if (element instanceof CollectionType) {
			toString = element.eClass().getName();
		}
		else if (element instanceof StringExp) {
			toString = "'" + ((StringExp)element).getStringSymbol() + "'";
		}
		else if (element instanceof OclContextDefinition) {
			toString = toString(((OclContextDefinition)element).getContext_());
		}
		else if (element instanceof Module) {
			toString = ((Module)element).getName(); 
		}
		//else System.out.println(element.eClass().getName());
		return toString;
	}
	
}
