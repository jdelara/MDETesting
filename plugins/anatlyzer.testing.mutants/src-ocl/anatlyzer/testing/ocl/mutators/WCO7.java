package anatlyzer.testing.ocl.mutators;


// WCO7: Changes the constraint by deleting the conditional operator "not"
public class WCO7 extends AbstractDeleteUnaryOperator {

	@Override
	public String getDescription() {
		return "WCO7";
	}
	
	@Override
	public String getUnaryOperator() {
		return "not";
	}
}
