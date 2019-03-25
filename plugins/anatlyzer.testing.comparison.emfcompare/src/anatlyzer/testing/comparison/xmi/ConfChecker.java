package anatlyzer.testing.comparison.xmi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.xml.XMLLayout;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.Query;
import org.eclipse.ocl.common.OCLConstants;
import org.eclipse.ocl.ecore.OCL;
import org.eclipse.ocl.ecore.OCLExpression;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class ConfChecker {

	private String basePath;
	private List<String> paths = new ArrayList<>();
	private ExcelSaver es;
	private ModelEquHandler meh = new ModelEquHandler();
	private EMFHandler emfh;
	
	public ConfChecker(String basePath, String...paths) {
		this.basePath = basePath;
		for ( String p : paths) {
			this.paths.add(p);
		}
		this.emfh = new EMFHandler();
	}		

	private void doCheck() {
		for (String p : this.paths) {
			this.checkStrategy(p, "max_strategy");
			this.checkStrategy(p, "min_strategy");
		}
	}

	private void calculateDiffs() {
		for (String p : this.paths) {
			this.meh = new ModelEquHandler();
			this.calculateSyntacticDiffs(p, "max_strategy");
			this.meh = new ModelEquHandler();
			this.calculateSyntacticDiffs(p, "min_strategy");
		}
	}

	
	private void calculateDiffs(String path, String strategy) {
		File[] subFolders = initAndGetSubFolders(path, strategy);
		
		int[][] diffs = new int[subFolders.length][subFolders.length];
		
		for (int i = 0; i < subFolders.length; i++ ) {
			File folder = subFolders[i];
			ResourceSet model1 = this.emfh.getModelAt(folder);
			
			for (int j = i + 1; j < subFolders.length; j++ ) {
				File folder2 = subFolders[j];
				ResourceSet model2 = this.emfh.getModelAt(folder2);
				
				IComparisonScope scope = new DefaultComparisonScope(model1, model2, null);
				Comparison comparison = EMFCompare.builder().build().compare(scope);
				
				List<Diff> differences = comparison.getDifferences();
				if (differences.size()>0) {
					System.out.println("=== Differences:"+folder+" , "+folder2);
					for (Diff d : differences) {
						System.out.println(d);
					}
				}
				diffs[i][j] = differences.size();
				System.out.println("Diff "+folder+" , "+folder2+" = "+diffs[i][j]);
			}
		}
	}
	
	private void calculateSyntacticDiffs(String path, String strategy) {
		File[] subFolders = initAndGetSubFolders(path, strategy);
		
		this.es = new ExcelSaver(subFolders.length, basePath+File.separator+path, "r"+strategy+".xlsx");
		
		//int[][] diffs = new int[subFolders.length][subFolders.length];
		 
		int maxSize = 0;
		int minSize = Integer.MAX_VALUE;
		long totalSize = 0;
		for (int i = 0; i < subFolders.length; i++ ) {
			Row rabs = this.es.getAbsolute().getRow(i+1);
			File folder = subFolders[i];
			File xmi1 = this.emfh.getFileWithExtension(folder, ".xmi");
			int modelSize = this.emfh.getModelSizeInFolder(folder);
			totalSize += modelSize;
			
			List<String> lines1 = this.fileToLines(xmi1);
			lines1.remove(0);
			lines1.remove(0);
			if (modelSize>maxSize) maxSize = modelSize;
			if (modelSize<minSize) minSize = modelSize;
			
			for (int j = i + 1; j < subFolders.length; j++ ) {
				Cell cabs = rabs.createCell(j+1);
				File folder2 = subFolders[j];
				File xmi2 = this.emfh.getFileWithExtension(folder2, ".xmi");
				
				List<String> lines2 = this.fileToLines(xmi2);
				lines2.remove(0);
				lines2.remove(0);
								
				Patch patch = DiffUtils.diff(lines1, lines2);
				if (patch.getDeltas().size()>0) 
					System.out.println("=== Differences:"+folder+" , "+folder2);
				else
					this.meh.addEqual(i, j);
				int affected = 0;
		        for (Delta delta: patch.getDeltas()) {		        	
		            System.out.println(delta);
		            int linesAffected = this.getNumLinesAffected(delta);
		            System.out.println("  lines affected "+linesAffected);
		            affected += linesAffected;
		        }
		        cabs.setCellValue(affected);
		        if (affected!=0)
				  System.out.println("Diff "+folder+" , "+folder2+" = "+affected);
			}
			if (! this.meh.contains(i)) this.meh.addNew(i);
			Cell csize = rabs.createCell(subFolders.length+1);
			csize.setCellValue(modelSize);
		}
		this.es.writeSummary(this.meh.getClusters(), minSize, maxSize, totalSize*1.0/subFolders.length);
		this.es.closeExcelWorkbook();
		System.out.println(this.meh);
	}
	
	private int getNumLinesAffected(Delta delta) {		
		int sourceLines = delta.getOriginal().getLines().size();
		int targetLines = delta.getRevised().getLines().size();
		return Math.max(sourceLines, targetLines);
	}
	
	private List<String> fileToLines(File filename) {
	    List<String> lines = new LinkedList<String>();
	    String line = "";
	    try {
	        BufferedReader in = new BufferedReader(new FileReader(filename));
	        while ((line = in.readLine()) != null) {
	            lines.add(line.trim());
	        }
	        in.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return lines;
	}
	
	private void checkStrategy(String path, String strategy) {
		File[] subFolders = initAndGetSubFolders(path, strategy);
		
		int noXMI = 0;
		
		for (File folder : subFolders) {
			File ecore = this.emfh.getFileWithExtension(folder, ".ecore");
			Resource ecoreModel = this.emfh.loadModel(ecore, null);
			//System.out.println("Loaded ecore resource from "+ecore);
			
			File xmi = this.emfh.getFileWithExtension(folder, ".xmi");
			if (xmi==null) {
				System.err.println("Found no xmi file at "+folder);
				noXMI++;
			} else {
				Resource model = this.emfh.loadModel(xmi, this.emfh.getEPackage(ecoreModel));
				//org.eclipse.ocl.ecore.OCL.initialize(this.rs);
				//org.eclipse.ocl.ecore.delegate.OCLDelegateDomain.initialize(this.rs, OCLDelegateDomain.OCL_DELEGATE_URI+"/Pivot");
				if (!this.validateModel(model)) {
					System.err.println("Model "+xmi+" is malformed");
				}
				if (!this.validateOCLModel(model)) {
					System.err.println("Model "+xmi+" has OCL errors");
				}
			}
		}
		
		System.out.println("Found "+noXMI+" folders without model");
	}

	private File[] initAndGetSubFolders(String path, String strategy) {
		File rootFolder = new File(basePath+File.separator+path+File.separator+strategy);		
		File[] subFolders = rootFolder.listFiles((current, name) -> new File(current, name).isDirectory());
		System.out.println("Analysing "+ subFolders.length+" subfolders of: "+rootFolder.getPath());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xml", new XMLResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		return subFolders;
	}

	private boolean validateModel(Resource model) {
		for (EObject o : model.getContents()) {
			Diagnostic diagnostic = Diagnostician.INSTANCE.validate(o);
			if (showError(diagnostic)) return false; 
		}
		return true;
	}
	
	private boolean validateOCLModel(Resource model0) {
		for (EObject eObject : model0.getContents()) {

			// get metaclasses of object (metaclass and ancestors)
			Set<EClass> metaclasses = new HashSet<EClass>();
			EClass metaclass = eObject.eClass();
			metaclasses.add(metaclass);
			metaclasses.addAll(metaclass.getEAllSuperTypes());

			// for each ocl invariant in the metaclasses...
			for (EClass cl : metaclasses) {
				for (EAnnotation an : cl.getEAnnotations()) {
					if (an.getSource().equals(OCLConstants.OCL_DELEGATE_URI+"/Pivot")) {
						for (String key : an.getDetails().keySet()) {

							// ...evaluate invariant in the object
							Object context   = eObject;
							String invariant = an.getDetails().get(key);
							OCL ocl = OCL.newInstance(org.eclipse.ocl.ecore.EcoreEnvironmentFactory.INSTANCE);
							OCL.Helper helper = ocl.createOCLHelper();
							helper.setInstanceContext(context);
							try {
								OCLExpression  exp   = helper.createQuery(invariant);
								Query<?, ?, ?> query = OCL.newInstance().createQuery(exp);
								Object eval = query.evaluate(context);

								// check if the constraint failed
								if (eval instanceof Boolean && ((Boolean)eval).booleanValue()==false) {
									//System.out.println( ">>> ERROR: constraint " + key + " does not hold");
									return false;
								}
							}
							catch (ParserException e) { e.printStackTrace(); }
							ocl.dispose();
						}
					}
				}
			}
		} 
		return true;
	}

	private boolean showError(Diagnostic diagnostic) {
		boolean hasError = false;
		if (diagnostic.getSeverity() == Diagnostic.ERROR || diagnostic.getSeverity() == Diagnostic.WARNING)
		{
			//System.err.println(diagnostic.getMessage());
			for (Iterator i=diagnostic.getChildren().iterator(); i.hasNext();) {
				Diagnostic childDiagnostic = (Diagnostic)i.next();
				switch (childDiagnostic.getSeverity())
				{
				case Diagnostic.ERROR:
				case Diagnostic.WARNING:
					if (! childDiagnostic.getMessage().startsWith("An exception occurred while delegating evaluation of the"))	{ // OCL delegate problem 
						//System.err.println("\t" + childDiagnostic.getMessage());
						hasError = true;
					}
				}
			}
		}
		return hasError;
	}

	public static void main(String[] args) throws IOException {
		BasicConfigurator.configure(new FileAppender(new XMLLayout(), "log.xml"));
		ConfChecker cc = new ConfChecker("C:\\research\\merlin_ws\\modelsTSE\\_models_", "GPL");
		cc.doCheck();
		cc.calculateDiffs();
	}

}
