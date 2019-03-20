package anatlyzer.testing.common.export;

import java.io.File;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public abstract class AbstractReportModel {

	public void toXML(String fname) {
		Serializer serializer = new Persister();
        File result = new File(fname);
        try {
			serializer.write(this, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
