package anatlyzer.testing.comparison.emfcompare;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.utils.UseIdentifiers;

import anatlyzer.testing.common.IComparator;
import anatlyzer.testing.common.IModel;

public class EMFCompareComparator implements IComparator {

	@Override
	public boolean compare(IModel r0, IModel r1) {		
		// Configure EMF Compare
		IEObjectMatcher matcher = DefaultMatchEngine.createDefaultEObjectMatcher(UseIdentifiers.WHEN_AVAILABLE);
		IComparisonFactory comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory());
		IMatchEngine.Factory matchEngineFactory = new MatchEngineFactoryImpl(matcher, comparisonFactory);
	        matchEngineFactory.setRanking(20);
	        IMatchEngine.Factory.Registry matchEngineRegistry = new MatchEngineFactoryRegistryImpl();
	        matchEngineRegistry.add(matchEngineFactory);
		EMFCompare comparator = EMFCompare.builder().setMatchEngineFactoryRegistry(matchEngineRegistry).build();

		// Compare the two models
		IComparisonScope scope = new DefaultComparisonScope(r0.getResource(), r1.getResource(), null);
		Comparison comparison = comparator.compare(scope);
		
		EList<Diff> diffs = comparison.getDifferences();
		for (Diff diff : diffs) {
			System.out.println(diff);
		}
		return diffs.size() == 0;
		
/*		
		IEObjectMatcher matcher = DefaultMatchEngine.createDefaultEObjectMatcher(UseIdentifiers.WHEN_AVAILABLE);
		IComparisonFactory comparisonFactory = new DefaultComparisonFactory(
				new DefaultEqualityHelperFactory());
//		IMatchEngine.Factory matchEngineFactory = new MatchEngineFactoryImpl(matcher, comparisonFactory);
//		matchEngineFactory.setRanking(20);
//		IMatchEngine.Factory.Registry matchEngineRegistry = new MatchEngineFactoryRegistryImpl();
//		matchEngineRegistry.add(matchEngineFactory);
				
		EMFCompare comparator = EMFCompare.builder()
				// .setMatchEngineFactoryRegistry(matchEngineRegistry)
				.build();//setDiffEngine(diffEngine).build();

		
		// final IComparisonScope scope = EMFCompare.createDefaultScope(r0.getResource(), r1.getResource());
		IComparisonScope scope = new DefaultComparisonScope(r0.getResource(), r1.getResource(), null);
		Comparison comparison = comparator.compare(scope);
		
		EList<Diff> diffs = comparison.getDifferences();
		for (Diff diff : diffs) {
			System.out.println(diff);
		}
		return diffs.size() == 0;
*/		
	}

}
