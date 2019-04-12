package anatlyzer.testing.mutants;

import java.io.File;

import org.eclipse.jdt.annotation.NonNull;

import anatlyzer.testing.mutants.IMutantGenerator.IMutantReference;

public class AtlMutantReference implements IMutantReference {

	private File file;
	private String mutantKindName;

	public AtlMutantReference(@NonNull File file, @NonNull String mutantKindName) {
		this.file = file;
		this.mutantKindName = mutantKindName;
	}

	public File getFile() {
		return file;
	}
	
	@Override
	public String getMutantKind() {
		return mutantKindName;
	}

}
