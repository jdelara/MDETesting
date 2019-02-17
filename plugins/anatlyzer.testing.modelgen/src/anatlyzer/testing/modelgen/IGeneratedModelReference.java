package anatlyzer.testing.modelgen;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import anatlyzer.testing.common.IModel;
import anatlyzer.testing.common.Metamodel;

public interface IGeneratedModelReference extends IModel {
	
	@Override
	Resource getResource();

	public static class FileModelReference extends IModel.AbstractModel implements IGeneratedModelReference {

		private static Log LOG = LogFactory.getLog(FileModelReference.class);
		
		private String fileName;
		private Metamodel metamodel;

		public FileModelReference(String fileName, Metamodel metamodel) {
			this.fileName = fileName;
			this.metamodel = metamodel;
			addAttribute(File.class, new File(fileName));
		}
		
		@Override
		public Resource getResource() {
			ResourceSet rs = new ResourceSetImpl();
			metamodel.registerIn(rs.getPackageRegistry());
			return rs.getResource(URI.createFileURI(fileName), true);			
		}

		@Override
		public Metamodel getMetamodel() {
			return metamodel;
		}
		
		@Override
		public void save() throws IOException {
			// Do nothing, it is already on disc
			LOG.warn("Calling save on a model reference: " + fileName);
		}
		
	}
}