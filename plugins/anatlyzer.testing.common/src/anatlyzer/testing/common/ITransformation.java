package anatlyzer.testing.common;

import java.util.List;

public interface ITransformation {

	public List<? extends ModelSpec> getSources();
	
	public List<? extends ModelSpec> getTargets();
	
	public static class ModelSpec {
		private String modelName;
		private Metamodel metamodel;
		private String metamodelName;
		
		public ModelSpec(String modelName, String metamodelName, Metamodel metamodel) {
			this.modelName = modelName;
			this.metamodelName = metamodelName;
			this.metamodel = metamodel;
		}
		
		public String getMetamodelName() {
			return metamodelName;
		}
		
		public Metamodel getMetamodel() {
			return metamodel;
		}
		
		public String getModelName() {
			return modelName;
		}
	}
	
}
