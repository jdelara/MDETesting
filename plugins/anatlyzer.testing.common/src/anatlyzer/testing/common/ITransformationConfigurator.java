package anatlyzer.testing.common;

/**
 * For the moment this works only for one input model...
 * 
 * @author jesus
 *
 */
public interface ITransformationConfigurator<T extends ITransformation, L extends ITransformationLauncher> {

	L configure(T t, IModel... inputs);
	
}
