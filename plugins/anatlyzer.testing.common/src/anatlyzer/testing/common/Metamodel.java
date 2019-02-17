package anatlyzer.testing.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Wraps a set of EPackage's into a meta-model.
 * @author jesus
 *
 */
public class Metamodel extends IModel.AbstractModel implements IModel {

	private List<EPackage> packages;
	private List<EClassifier> classifiers;
	private Resource resource;

	public Metamodel(Resource r) {
		resource = r;
		packages = extractEPackages(r);
	}

	@Override
	public @Nullable Metamodel getMetamodel() {
		return null;
	}
	
	public Collection<? extends EPackage> getPackages() {
		return packages;
	}
	
	public Collection<? extends EClassifier> getEClassifiers() {
		if ( classifiers == null ) {
			classifiers = new ArrayList<EClassifier>();
			for (EPackage pkg : packages) {
				classifiers.addAll(pkg.getEClassifiers());
			}
		}
		return classifiers;
	}

	
	private static List<EPackage> extractEPackages(Resource r) {
		ArrayList<EPackage> packages = new ArrayList<>();
		r.getAllContents().forEachRemaining(o -> {
			if ( o instanceof EPackage ) {
				packages.add((EPackage) o);
			}
		});
		return packages;
	}

	@Override
	public Resource getResource() {
		return resource;
	}

	public void registerIn(Registry packageRegistry) {
		for (EPackage pkg : getPackages()) {
			packageRegistry.put(pkg.getNsURI(), pkg);
		}
	}

	@Override
	public void save() throws IOException {
		resource.save(null);
	}
	
}
