package anatlyzer.testing.mutants;

import java.util.List;

import anatlyzer.testing.common.IProgressMonitor;
import anatlyzer.testing.mutants.IMutantGenerator.IMutantReference;

public interface IMutantGenerator<T extends IMutantReference> {

	public interface IMutantReference {
		/**
		 * @return the name of the applied mutation
		 */
		public String getMutantKind();
	}

	List<T> generateMutants(IProgressMonitor monitor) throws MutantGeneratorException;
	
	@SuppressWarnings("serial")
	public static class MutantGeneratorException extends Exception {
		public MutantGeneratorException(Throwable t) {
			super(t);
		}
	}

}
