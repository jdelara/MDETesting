package anatlyzer.testing.difftesting;

import org.eclipse.jdt.annotation.NonNull;

import anatlyzer.testing.common.ITransformation;

public interface IOptimiser<T extends ITransformation> {

	@NonNull
	T optimise(@NonNull T transformation);

}
