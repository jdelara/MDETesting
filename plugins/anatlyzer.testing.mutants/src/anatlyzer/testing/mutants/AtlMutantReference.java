package anatlyzer.testing.mutants;

import java.io.File;

import anatlyzer.testing.mutants.IMutantGenerator.IMutantReference;

public class AtlMutantReference implements IMutantReference {

	private File file;

	public AtlMutantReference(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

}
