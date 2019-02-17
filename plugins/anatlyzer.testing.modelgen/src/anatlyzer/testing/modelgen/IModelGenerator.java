package anatlyzer.testing.modelgen;

import java.util.List;

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
