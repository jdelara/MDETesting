package anatlyzer.testing.ocl.mutators;


// WCO9: Changes a constraint by deleting a unary arithmetic operator (-)
public class WCO9 extends AbstractDeleteUnaryOperator {

	@Override
	public String getDescription() {
		return "WCO9";
	}

	@Override
	public String getUnaryOperator() {
		return "-";
	}
}
