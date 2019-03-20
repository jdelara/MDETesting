package anatlyzer.testing.difftesting;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import anatlyzer.testing.common.IModel;
import anatlyzer.testing.common.ITransformation;
import anatlyzer.testing.common.report.AbstractReport;
import anatlyzer.testing.difftesting.export.ReportModel;
import anatlyzer.testing.difftesting.export.ReportModel.ReportRecord;

public class DifferentialTestingReport extends AbstractReport {

	private List<Record> records = new ArrayList<>();
	
	public static abstract class Record extends AbstractReport.Record {
		private @NonNull ITransformation t1;
		private @NonNull ITransformation t2;
		private @NonNull IModel model;
		
		public Record(@NonNull ITransformation t1, @NonNull ITransformation t2, @NonNull IModel model) {
			this.t1 = t1;
			this.t2 = t2;
			this.model = model;
		}
		
		public ITransformation getTransformation1() {
			return t1;
		}
		
		public ITransformation getTransformation2() {
			return t2;
		}
		
		public IModel getModel() {
			return model;
		}

		@ElementList(name="trafo1")
		protected String getTransformationPath() {
			return t1.toString();
		}

		public @NonNull ReportRecord toExportable() {
			ReportRecord r = new ReportRecord()
					.withTransformation1(t1.toString())
					.withTransformation2(t2.toString())
					.withInput(model.toString());
			return r;
		}
		
	}

	public static class RecordOk extends Record {

		public RecordOk(@NonNull ITransformation t1, @NonNull ITransformation t2, @NonNull IModel model) {
			super(t1, t2, model);
		}
		
		@Override
		public @NonNull ReportRecord toExportable() {
			return super.toExportable()
					.withStatus("ok");
		}
	}
	
	public static class RecordError extends Record {
		private @NonNull Exception exception;

		public RecordError(@NonNull ITransformation t1, @NonNull ITransformation t2, @NonNull IModel model, @NonNull Exception e) {
			super(t1, t2, model);
			this.exception = e;
		}
		
		@Override
		public @NonNull ReportRecord toExportable() {
			return super.toExportable()
					.withException(exception);
		}
	}

	public static class RecordMismatch extends Record {
		private @NonNull Exception exception;
		private @NonNull IModel target1;
		private @NonNull IModel target2;

		public RecordMismatch(@NonNull ITransformation t1, @NonNull ITransformation t2, @NonNull IModel model, @NonNull IModel target1, @NonNull IModel target2) {
			super(t1, t2, model);
			this.target1 = target1;
			this.target2 = target2;
		}
		
		@Override
		public @NonNull ReportRecord toExportable() {
			return super.toExportable()
					.withMismatch("unknown");
		}
	}
	
	public void addError(@NonNull ITransformation t1, @NonNull ITransformation t2, @NonNull IModel model, @NonNull Exception e) {
		RecordError error = new RecordError(t1, t2, model, e);
		this.records.add(error);
	}

	public void addComparisonMismatch(@NonNull ITransformation t1, @NonNull ITransformation t2, IModel source, IModel target1, IModel target2) {
		RecordMismatch error = new RecordMismatch(t1, t2, source, target1, target2);
		this.records.add(error);		
	}

	public void addTestOk(@NonNull ITransformation t1, @NonNull ITransformation t2, IModel model) {
		RecordOk error = new RecordOk(t1, t2, model);
		this.records.add(error);				
	}

	@NonNull
	public List<RecordMismatch> getMismatches() {
		return this.records.stream()
				.filter(p -> p instanceof RecordMismatch)
				.map(p -> (RecordMismatch) p)
				.collect(Collectors.toList());
	}

	@NonNull
	public List<RecordError> getErrors() {
		return this.records.stream()
				.filter(p -> p instanceof RecordError)
				.map(p -> (RecordError) p)
				.collect(Collectors.toList());
	}

	public void merge(DifferentialTestingReport report) {
		this.records.addAll(report.records);
	}


	public void reportIssues(PrintStream out) {
		List<RecordMismatch> mismatches = getMismatches();
		List<RecordError> errors = getErrors();
		
		Map<ITransformation, List<RecordMismatch>> mismatchesByTransformation = mismatches.stream().collect(Collectors.groupingBy(m -> m.getTransformation2(), Collectors.mapping(m -> m, Collectors.toList())));
		Map<ITransformation, List<RecordError>> errorsByTransformation = errors.stream().collect(Collectors.groupingBy(m -> m.getTransformation2(), Collectors.mapping(m -> m, Collectors.toList())));
		
		out.println("Mismatches:");
		mismatchesByTransformation.forEach((t, l) -> {
			out.println(t);
			for (RecordMismatch m : l) {
				out.println("  - Input model " + m.getModel());		
			}
		});
		
		out.println("Errors:");
		errorsByTransformation.forEach((t, l) -> {
			out.println(t);
			for (RecordError m : l) {
				out.println("  - Input model " + m.getModel());
				out.println("   " + m.exception.getMessage());
			}
		});
		
	}

	@NonNull
	public ReportModel toExportable() {
		ReportModel model = new ReportModel();
		for (Record record : records) {
			model.addRecord(record.toExportable());
		}
		return model;
	}
	
}
