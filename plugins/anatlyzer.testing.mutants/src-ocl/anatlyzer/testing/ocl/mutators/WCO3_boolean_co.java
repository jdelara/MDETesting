package anatlyzer.testing.ocl.mutators;

import java.util.Arrays;
import java.util.List;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.OCL.OperationCallExp;
import anatlyzer.testing.mutants.MuMetaModel;

// WCO3: Changes a collection operator by another compatible
public class WCO3_boolean_co extends AbstractChangeStringValue {

	@Override
	public void generateMutants(ATLModel atlModel, MuMetaModel inputMM, MuMetaModel outputMM) {
		generateMutants(atlModel, OperationCallExp.class, "operationName");
	}

	@Override
	public List<String> getValues() {
		return Arrays.asList ( new String[]{"isEmpty", "notEmpty", "includes", "excludes", "includesAll", "excludesAll"} ); 
	}

	@Override
	public String getDescription() {
		return "WCO3BooleanCo";
	}
}
