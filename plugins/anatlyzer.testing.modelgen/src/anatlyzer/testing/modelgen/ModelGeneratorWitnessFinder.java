package anatlyzer.testing.modelgen;

import java.io.IOException;
import java.nio.file.Files;

import anatlyzer.atl.witness.IWitnessFinder;
import anatlyzer.atl.witness.UseWitnessFinder;

/* I have no idea why I need to implements IWitnessFinder to use the class as a IWitnessFinder :-( */
public class ModelGeneratorWitnessFinder extends UseWitnessFinder {

	@Override
	protected void onUSEInternalError(Exception e) {
		e.printStackTrace();
	}

	@Override
	protected String getTempDirectory() {
		try {
			return Files.createTempDirectory("anatlyzer").toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
