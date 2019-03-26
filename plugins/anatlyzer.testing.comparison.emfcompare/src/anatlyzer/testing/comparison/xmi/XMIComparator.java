package anatlyzer.testing.comparison.xmi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import anatlyzer.testing.common.IComparator;
import anatlyzer.testing.common.IModel;
import difflib.DiffUtils;
import difflib.Patch;

public class XMIComparator implements IComparator {

	@Override
	public boolean compare(IModel r0, IModel r1) {
		File f0 = r0.getAttribute(File.class);
		File f1 = r1.getAttribute(File.class);

		List<String> lines1 = fileToLines(f0);
		List<String> lines2 = fileToLines(f1);
		
		
		lines1.remove(0);
		lines1.remove(0);
		
		Patch patch = DiffUtils.diff(lines1, lines2);
		if (patch.getDeltas().size()>0) 
			return false;
		else
			return true;
	}
	
	// From ConfChecker
	private List<String> fileToLines(File filename) {
	    List<String> lines = new LinkedList<String>();
	    String line = "";
	    try {
	        BufferedReader in = new BufferedReader(new FileReader(filename));
	        while ((line = in.readLine()) != null) {
	            lines.add(line.trim());
	        }
	        in.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return lines;
	}

}
