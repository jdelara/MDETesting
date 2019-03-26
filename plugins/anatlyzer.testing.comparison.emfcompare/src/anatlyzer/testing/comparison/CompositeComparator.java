package anatlyzer.testing.comparison;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;

import anatlyzer.testing.common.IComparator;
import anatlyzer.testing.common.IModel;

public class CompositeComparator implements IComparator {

	private List<IComparator> comparators;

	public CompositeComparator(IComparator... comparators) {
		Preconditions.checkArgument(comparators.length > 0);
		this.comparators = Arrays.asList(comparators);
	}

	@Override
	public boolean compare(IModel r0, IModel r1) {
		List<Boolean> values = new ArrayList<Boolean>();
		int counter = 0;
		for(int i = 0, len = comparators.size(); i < len; i++) {
			IComparator c = comparators.get(i);			
			boolean b = c.compare(r0, r1);
			values.add(b);
			counter = counter + (b ? +1 : -1);
			
			// We already have a decision
			if (Math.abs(counter) > len / 2.0d) {
				break;
			}
		}
		
		if ( counter < 0 ) 
			return false;
		else if ( counter > 0 )
			return true;
		else
			return values.get(0);					
	}
	
}
