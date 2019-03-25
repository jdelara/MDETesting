package anatlyzer.testing.comparison.xmi;

import java.io.File;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.ocl.ecore.delegate.OCLDelegateDomain;
import org.eclipse.ocl.ecore.delegate.OCLInvocationDelegateFactory;
import org.eclipse.ocl.ecore.delegate.OCLSettingDelegateFactory;
import org.eclipse.ocl.ecore.delegate.OCLValidationDelegateFactory;

public class EMFHandler {
	private ResourceSet rs;

	public EMFHandler() {
		String oclDelegateURI = OCLDelegateDomain.OCL_DELEGATE_URI+"/Pivot";
		EOperation.Internal.InvocationDelegate.Factory.Registry.INSTANCE.put(
				oclDelegateURI, new OCLInvocationDelegateFactory.Global());
		EStructuralFeature.Internal.SettingDelegate.Factory.Registry.INSTANCE
				.put(oclDelegateURI, new OCLSettingDelegateFactory.Global());
		EValidator.ValidationDelegate.Registry.INSTANCE.put(oclDelegateURI,
				new OCLValidationDelegateFactory.Global());
//		EValidator.Registry.INSTANCE.put(MyModelPackage.eINSTANCE, new MyModelValidator());
	}
	
	public Resource loadModel(File model, EPackage p) {		
		URI uri = URI.createFileURI(model.getPath());
		rs = new ResourceSetImpl();
		if (p!=null) {
			rs.getPackageRegistry().put(p.getNsURI(), p);
		}
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,new XMIResourceFactoryImpl());
		Resource resource = rs.getResource(uri, true);
		return resource;
	}
	
	public EPackage getEPackage(Resource ecoreModel) {
		return (EPackage) ecoreModel.getContents().get(0);
	}
	
	public File getFileWithExtension(File folder, String ext) {
		File[] ecores = folder.listFiles((current, name) -> name.endsWith(ext));
		return ecores.length==0 ? null : ecores[0]; 
	}
	
	public ResourceSet getModelAt(File folder) {
		File ecore = this.getFileWithExtension(folder, ".ecore");
		Resource ecoreModel = loadModel(ecore, null);
		File xmi = this.getFileWithExtension(folder, ".xmi");
		if (xmi==null) {
			System.err.println("Found no xmi file at "+folder);
			return null;
		} 
		return loadModel(xmi, this.getEPackage(ecoreModel)).getResourceSet();
	}
	
	public int getModelSize(Resource m) {
		List<EObject> content = m.getContents();
		return content.size();
	}

	public int getModelSizeInFolder(File folder) {
		File ecore = this.getFileWithExtension(folder, ".ecore");
		Resource ecoreModel = loadModel(ecore, null);
		File xmi = this.getFileWithExtension(folder, ".xmi");
		if (xmi==null) {
			System.err.println("Found no xmi file at "+folder);
			return 0;
		} 
		Resource r = loadModel(xmi, this.getEPackage(ecoreModel));
		return this.getModelSize(r);
	}
}
