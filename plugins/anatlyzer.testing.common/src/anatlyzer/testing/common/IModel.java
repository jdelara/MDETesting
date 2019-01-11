package anatlyzer.testing.common;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.annotation.NonNull;

public interface IModel {

	@NonNull
	Resource getResource();

}
