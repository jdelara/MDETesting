package anatlyzer.testing.difftesting.export;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import anatlyzer.testing.common.export.AbstractReportModel;

@Root(name="diff-report")
public class ReportModel extends AbstractReportModel {

	@ElementList(name="records")
	private List<ReportRecord> records = new ArrayList<>();
	
	public void addRecord(@NonNull ReportRecord r) {
		this.records.add(r);
	}
	
	@NonNull
	public List<? extends ReportRecord> getRecords() {
		return records;
	}
	
	public static class ReportRecord {
		@Element(name="transformation1")
		private String transformation1;
		
		@Element(name="transformation2")
		private String transformation2;
		
		@Element(name="input")
		private String input;

		@Attribute(name="status")
		private String status;

		@Element(name="exception", required=false)
		private String exception;

		@Element(name="mismatch", required=false)
		private @NonNull String mismatch;
		
		@NonNull
		public ReportRecord withTransformation1(String v) {
			this.transformation1 = v;
			return this;
		}
		
		@NonNull
		public ReportRecord withTransformation2(String v) {
			this.transformation2 = v;
			return this;
		}
		
		@NonNull
		public ReportRecord withInput(String v) {
			this.input = v;
			return this;
		}

		@NonNull 
		public ReportRecord withStatus(String status) {
			this.status = status;
			return this;
		}

		@NonNull 
		public ReportRecord withException(@NonNull Exception exception) {
			this.exception = exception.getMessage();
			for (StackTraceElement e : exception.getStackTrace()) {
				this.exception += "\n" + e.getClassName() + ":" + e.getLineNumber();
			}
			return withStatus("exception");
		}

		public @NonNull ReportRecord withMismatch(@NonNull String explanation) {
			this.mismatch = explanation;
			return withStatus("mismatch");
		}
		
		public String getTransformation1() {
			return transformation1;
		}
		
		public String getTransformation2() {
			return transformation2;
		}
		
		public String getException() {
			return exception;
		}
		
		public String getInput() {
			return input;
		}
		
		public String getStatus() {
			return status;
		}
		
		public String getMismatch() {
			return mismatch;
		}
	}
}
