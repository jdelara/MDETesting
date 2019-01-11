package anatlyzer.testing.modelgen;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import anatlyzer.testing.common.IProgressMonitor;

/**
 * Represents a mechanism to generate models for a metamodel.
 * 
 * @author jesus
 *
 */
public interface IModelGenerator {

	List<IGeneratedModelReference> generateModels(IProgressMonitor monitor);
	
}
