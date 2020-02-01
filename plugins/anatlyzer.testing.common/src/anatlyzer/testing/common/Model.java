package anatlyzer.testing.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.jdt.annotation.NonNull;

import com.google.common.base.Preconditions;

/**
 * Represents an in-memory model
 * @author jesus
 *
 */
public class Model extends IModel.AbstractModel implements IModel {

	private final Metamodel metamodel;
	private final Resource resource;

	public Model(@NonNull Resource resource, @NonNull Metamodel metamodel) {	
		this.metamodel = Preconditions.checkNotNull(metamodel);
		this.resource  = Preconditions.checkNotNull(resource);
	}
	
	public Metamodel getMetamodel() {
		return metamodel;
	}
	
	@Override
	public Resource getResource() {
		return resource;
	}

	@Override
	public void save() throws IOException {
		File f = getAttributeOrNull(File.class);
		HashMap<Object, Object> options = new HashMap<Object, Object>();
		
//		String schemaLocation = "";
//		for (EPackage pkg : metamodel.getPackages()) {
//			String pair = pkg.getNsURI() + " " + pkg.eResource().getURI().toString();
//			schemaLocation += " " + pair;
//		}
//		schemaLocation = schemaLocation.trim();
//		
//		if (schemaLocation != null) {
//			options.put(XMIResource.SCHEMA_LOCATION, schemaLocation);
//		}
		if ( f != null ) {
			resource.save(new FileOutputStream(f), options);
		} else {
			resource.save(options);
		}
	}
	
}
