package anatlyzer.testing.coverage;

import static org.junit.Assert.assertNotEquals;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Test;

import anatlyzer.atl.analyser.AnalysisResult;
import anatlyzer.atl.tests.api.AnalysisLoader;
import anatlyzer.atl.util.ATLSerializer;
import anatlyzer.testing.atl.coverage.CoverageTransformer;

public class TestCoverateAdapter {

	@Test
	public void test() {
		ResourceSet rs = new ResourceSetImpl();
		Resource r = rs.getResource(URI.createFileURI("resources/transformations/class2table.atl"), true);
		
		AnalysisLoader loader = AnalysisLoader.fromResource(r, new String[] { "resources/metamodels/ClassDiagram.ecore", "resources/metamodels/Relational.ecore"} , new String[] { "Class", "Relational" });
		AnalysisResult result = loader.analyse();
		
		String original = ATLSerializer.serialize(result.getATLModel());
		
		CoverageTransformer transformer = new CoverageTransformer(result.getATLModel());
		transformer.transform();
		
		String instrumented = ATLSerializer.serialize(result.getATLModel());
		
		System.out.println(original);
		System.out.println("Instrumented:");
		System.out.println(instrumented);
		
		assertNotEquals(original, instrumented);
		
		
	}

}
