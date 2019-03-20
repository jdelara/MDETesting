package anatlyzer.testing.common.export;

import java.io.File;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public abstract class AbstractReportModel {

	
	
	public void toXML(String fname) {
		toXML(new File(fname));
	}
	
	public void toXML(File result) {
		Serializer serializer = new Persister();
        try {
			serializer.write(this, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
