package anatlyzer.testing.ocl.mutators;

import java.util.Arrays;
import java.util.List;

import org.eclipse.m2m.atl.core.emf.EMFModel;

import witness.generator.MetaModel;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.OCL.OperatorCallExp;
import anatlyzer.testing.mutants.MuMetaModel;

// WCO4: Changes an arithmetic operator for another and supports binary operators: +, -, *, /
public class WCO4 extends AbstractChangeStringValue { 

	@Override
	public void generateMutants(ATLModel atlModel, MuMetaModel inputMM, MuMetaModel outputMM) {
		generateMutants(atlModel, OperatorCallExp.class, "operationName", true);
	}
	
	@Override
	public List<String> getValues() {
		return Arrays.asList ( new String[]{"+", "-", "*", "/"} );
	}

	@Override
	public String getDescription() {
		return "WCO4";
	}
}
