package anatlyzer.testing.ocl.mutators;

import java.util.Arrays;
import java.util.List;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.OCL.OperatorCallExp;
import anatlyzer.testing.mutants.MuMetaModel;

// WCO6: Changes a conditional operator for another and supports operators: or, and
public class WCO6 extends AbstractChangeStringValue { 

	@Override
	public void generateMutants(ATLModel atlModel, MuMetaModel inputMM, MuMetaModel outputMM) {
		generateMutants(atlModel, OperatorCallExp.class, "operationName");
	}

	@Override
	public List<String> getValues() {
		return Arrays.asList ( new String[]{"or", "and"} );
	}

	@Override
	public String getDescription() {
		return "WCO6";
	}
}
