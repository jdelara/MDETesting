package anatlyzer.testing.modelgen;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import anatlyzer.testing.common.IModel;
import anatlyzer.testing.common.Metamodel;

public interface IGeneratedModelReference extends IModel {

	@Override
	Resource getResource();

	public static class FileModelReference implements IGeneratedModelReference {

		private String fileName;
		private Metamodel metamodel;

		public FileModelReference(String fileName, Metamodel metamodel) {
			this.fileName = fileName;
			this.metamodel = metamodel;
		}
		
		@Override
		public Resource getResource() {
			ResourceSet rs = new ResourceSetImpl();
			metamodel.registerIn(rs.getPackageRegistry());
			return rs.getResource(URI.createFileURI(fileName), true);			
		}
		
	}
}