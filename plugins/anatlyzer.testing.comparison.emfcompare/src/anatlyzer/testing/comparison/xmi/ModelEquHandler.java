package anatlyzer.testing.comparison.xmi;

import java.util.*;

public class ModelEquHandler {
	private List<Set<Integer>> equal = new ArrayList<>();
	
	public void addEqual(int i, int j) {
		for (Set<Integer> equiv : equal) {
			if (equiv.contains(i) || equiv.contains(j)) {
				equiv.add(i);
				equiv.add(j);
				return;
			}
		}
		// A new set should be added
		Set<Integer> set = new LinkedHashSet<>();
		set.add(i);
		set.add(j);
		equal.add(set);
	}
	
	@Override
	public String toString() {
		return this.equal.toString();
	}

	public List<Set<Integer>> getClusters() {
		return this.equal;
	}

	public boolean contains(int i) {
		for (Set<Integer> st : this.equal) {
			if (st.contains(i)) return true;
		}
		return false;
	}

	public void addNew(int i) {
		Set<Integer> newSet = new LinkedHashSet<>();
		newSet.add(i);
		equal.add(newSet);
	}
}
