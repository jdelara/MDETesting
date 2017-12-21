package anatlyzer.testing.ocl.mutators;

import java.util.Arrays;
import java.util.List;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.OCL.IteratorExp;
import anatlyzer.testing.mutants.MuMetaModel;

// WCO3: Changes an iterator by another compatible
public class WCO3_collection_it extends AbstractChangeStringValue {

	@Override
	public void generateMutants(ATLModel atlModel, MuMetaModel inputMM, MuMetaModel outputMM) {
		generateMutants(atlModel, IteratorExp.class, "name");
	}
	
	@Override
	public List<String> getValues() {
		return Arrays.asList ( new String[]{"collect","select","reject","sortedBy"} );
	}

	@Override
	public String getDescription() {
		return "WCO3CollectionIt";
	}	
}
