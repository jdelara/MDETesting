package anatlyzer.testing.modelgen;

import java.util.Properties;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;

import anatlyzer.atl.witness.IMetamodelRewrite;
import anatlyzer.atl.witness.IScopeCalculator;

public class GenStrategyScope implements IScopeCalculator {

	private Properties properties;
	private int minDefault = 0;
	private int maxDefault = 0;

	public GenStrategyScope(Properties propertiesUse) {
		this.properties = propertiesUse;
	}

	@Override
	public Interval getScope(EClass klass) {
		// int min = Integer.parseInt(properties.getProperty(klass.getName() + "_min", "" + minDefault));
		// int max = Integer.parseInt(properties.getProperty(klass.getName() + "_max", "" + maxDefault));
		if ( klass.getName().equals("AuxiliaryClass4USE") || klass.getName().equals("ThisModule") ) {
			return new Interval(1, 1);
		}
		
		int min = Integer.parseInt(properties.getProperty("solver.scope." + klass.getName(), "" + minDefault));
		int max = Integer.parseInt(properties.getProperty("solver.scope." + klass.getName(), "" + maxDefault));
		return new Interval(min, max);
	}

	@Override
	public Interval getScope(EReference feature) {
		//int min = Integer.parseInt(properties.getProperty("solver.scope." + feature.getEContainingClass().getName() + "_" + feature.getName(), "" + minDefault));
		//int max = Integer.parseInt(properties.getProperty("solver.scope." + feature.getEContainingClass().getName() + "_" + feature.getName(), "" + maxDefault));
		//return new Interval(min, max);
		return new Interval(0, 10);
	}

	@Override
	public int getDefaultMaxScope() {
		return 5;
	}
	
	@Override
	public boolean incrementScope() {
		return false;
	}

	@Override
	public void setMetamodelRewrite(IMetamodelRewrite rewrite) {
		System.out.println("Ignoring rewriter " + rewrite);
	}

}
