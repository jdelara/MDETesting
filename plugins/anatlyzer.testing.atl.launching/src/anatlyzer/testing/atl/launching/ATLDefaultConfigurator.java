package anatlyzer.testing.atl.launching;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import anatlyzer.testing.atl.common.AtlLauncher;
import anatlyzer.testing.atl.common.AtlTransformation;
import anatlyzer.testing.common.IModel;
import anatlyzer.testing.common.ITransformation.ModelSpec;
import anatlyzer.testing.common.ITransformationConfigurator;

public class ATLDefaultConfigurator implements ITransformationConfigurator<AtlTransformation, AtlLauncher> {
	protected Map<String, BiFunction<AtlTransformation, IModel[], File>> outputPaths = new HashMap<>();	
	
	public ATLDefaultConfigurator withOutputModel(String name, BiFunction<AtlTransformation, IModel[], File> outputMapper) {
		outputPaths.put(name, outputMapper);
		return this;
	}
	
	@Override
	public AtlLauncher configure(AtlTransformation t, IModel... inputs) {
		List<ATLExecutor.ModelData> models = new ArrayList<>();
		
		int i = 0;
		for (ModelSpec m : t.getSources()) {
			models.add( ATLExecutor.inModel(m.getModelName(), inputs[i].getResource(), 
					m.getMetamodelName(), 
					inputs[i].getMetamodel().getResource()) );			
			i++;
		}
		
		for (ModelSpec m : t.getTargets()) {
			if ( ! outputPaths.containsKey(m.getModelName()) ) {
				throw new IllegalArgumentException();
			}
			
			String path = outputPaths.get(m.getModelName()).apply(t, inputs).getAbsolutePath();
			models.add( ATLExecutor.outModel(m.getModelName(), path, m.getMetamodelName(), m.getMetamodel().getResource()));
		}
		
		ATLExecutor executor = new ATLExecutor().withModels(models);
		
		return new AtlLauncher(executor, t);
	}

}
