package anatlyzer.testing.common;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.annotation.NonNull;

/**
 * Represents an in-memory model
 * @author jesus
 *
 */
public class Model implements IModel {

	private final Metamodel metamodel;
	private final Resource resource;

	public Model(@NonNull Resource resource, @NonNull Metamodel metamodel) {
		this.metamodel = metamodel;
		this.resource  = resource;
	}
	
	public Metamodel getMetamodel() {
		return metamodel;
	}
	
	@Override
	public Resource getResource() {
		return resource;
	}
	
}
