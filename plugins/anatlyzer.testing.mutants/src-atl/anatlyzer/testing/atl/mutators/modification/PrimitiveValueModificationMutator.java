package anatlyzer.testing.atl.mutators.modification;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import anatlyzer.atlext.OCL.BooleanExp;
import anatlyzer.atlext.OCL.IntegerExp;
import anatlyzer.atlext.OCL.RealExp;
import anatlyzer.atlext.OCL.StringExp;
import anatlyzer.testing.mutants.MuMetaModel;

public class PrimitiveValueModificationMutator extends AbstractModificationMutator {

	@Override
	public void generateMutants(anatlyzer.atl.model.ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {
		this.genericAttributeModification(wrapper, BooleanExp.class, "booleanSymbol", outputMM);
		this.genericAttributeModification(wrapper, StringExp.class,  "stringSymbol",  outputMM);
		this.genericAttributeModification(wrapper, IntegerExp.class, "integerSymbol", outputMM);
		this.genericAttributeModification(wrapper, RealExp.class,    "realSymbol",    outputMM);
	}

	@Override
	public String getDescription() {
		return "Primitive Value Modification";
	}

	@Override
	protected List<Object> replacements(EObject object2modify, String currentAttributeValue, MuMetaModel metamodel) {
		List<Object> replacements = new ArrayList<Object>();
		if      (object2modify instanceof BooleanExp) replacements.add( !((BooleanExp)object2modify).isBooleanSymbol() );
		else if (object2modify instanceof StringExp)  replacements.add( ((StringExp)  object2modify).getStringSymbol().isEmpty()? "dummy" : "" );
		else if (object2modify instanceof IntegerExp) replacements.add( ((IntegerExp) object2modify).getIntegerSymbol()+1 );
		else if (object2modify instanceof RealExp)    replacements.add( ((RealExp)    object2modify).getRealSymbol()+1 );
		return replacements;
	}
}
