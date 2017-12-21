package anatlyzer.testing.ocl.mutators;

import java.util.Arrays;
import java.util.List;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.OCL.IteratorExp;
import anatlyzer.testing.mutants.MuMetaModel;

// WCO3: Changes an iterator by another compatible
public class WCO3_boolean_it extends AbstractChangeStringValue {

	@Override
	public void generateMutants(ATLModel atlModel, MuMetaModel inputMM, MuMetaModel outputMM) {
		generateMutants(atlModel, IteratorExp.class, "name");
	}
	
	@Override
	public List<String> getValues() {
		return Arrays.asList ( new String[]{"exists","forAll","one"} ); // "isUnique"
	}

	@Override
	public String getDescription() {
		return "WCO3BooleanIt";
	}
}
