package anatlyzer.testing.comparison.emfcompare;

import java.util.regex.Pattern;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.diff.DefaultDiffEngine;
import org.eclipse.emf.compare.diff.DiffBuilder;
import org.eclipse.emf.compare.diff.FeatureFilter;
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.eobject.CachingDistance;
import org.eclipse.emf.compare.match.eobject.DefaultWeightProvider;
import org.eclipse.emf.compare.match.eobject.EcoreWeightProvider;
import org.eclipse.emf.compare.match.eobject.EditionDistance;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.eobject.IdentifierEObjectMatcher;
import org.eclipse.emf.compare.match.eobject.ProximityEObjectMatcher;
import org.eclipse.emf.compare.match.eobject.WeightProviderDescriptorRegistryImpl;
import org.eclipse.emf.compare.match.eobject.internal.WeightProviderDescriptorImpl;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import anatlyzer.testing.common.IComparator;
import anatlyzer.testing.common.IModel;

public class EMFCompareComparator implements IComparator {

	@Override
	public boolean compare(IModel r0, IModel r1) {		
		// Configure EMF Compare
		
		// IEObjectMatcher matcher = DefaultMatchEngine.createDefaultEObjectMatcher(UseIdentifiers.WHEN_AVAILABLE);
		
		// This is for both
//		
//		final WeightProviderDescriptorRegistryImpl registry = new WeightProviderDescriptorRegistryImpl();
//
//		final DefaultWeightProvider defaultWeightProvider = new DefaultWeightProvider();
//		
//		final WeightProviderDescriptorImpl dwpDescriptor = new WeightProviderDescriptorImpl(
//				defaultWeightProvider, 100, Pattern.compile(".*")); //$NON-NLS-1$
//		final EcoreWeightProvider ecoreWeightProvider = new EcoreWeightProvider();
//		
//		final WeightProviderDescriptorImpl ewpDescriptor = new WeightProviderDescriptorImpl(
//				ecoreWeightProvider, 101, Pattern.compile("http://www.eclipse.org/emf/2002/Ecore")); //$NON-NLS-1$
//
//		registry.put(defaultWeightProvider.getClass().getName(), dwpDescriptor);
//		registry.put(ecoreWeightProvider.getClass().getName(), ewpDescriptor);
//
//		
//		
		/*
		final EditionDistance editionDistance = new EditionDistance() {
			@Override
			public double distance(Comparison inProgress, EObject a, EObject b) {
				return 1;
			}
		};
		final CachingDistance cachedDistance = new CachingDistance(editionDistance);
		
		final IEObjectMatcher contentMatcher = new ProximityEObjectMatcher(cachedDistance);
		IdentifierEObjectMatcher matcher = new IdentifierEObjectMatcher(contentMatcher);
		
		
		IComparisonFactory comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory());
		
		IMatchEngine.Factory matchEngineFactory = new MatchEngineFactoryImpl(matcher, comparisonFactory) {
			@Override
			public IMatchEngine getMatchEngine() {
				matchEngine = new DefaultMatchEngine(matcher, comparisonFactory) {
					
					@Override
					protected void match(Comparison comparison, IComparisonScope scope, EObject left, EObject right,
							EObject origin, Monitor monitor) {
						
//						// They are root elements, we want to match them properly
//						if ( left.eContainer() == null && right.eContainer() == null ) {
//							
//						}
//						
						super.match(comparison, scope, left, right, origin, monitor);
					}
				};
				
				return super.getMatchEngine();
			}	
			
		};
    
		matchEngineFactory.setRanking(20);
		IMatchEngine.Factory.Registry matchEngineRegistry = new MatchEngineFactoryRegistryImpl();
        matchEngineRegistry.add(matchEngineFactory);
    
	    DiffBuilder diffProcessor = new DiffBuilder();
	    DefaultDiffEngine diffEngine = new DefaultDiffEngine(diffProcessor) {
	    	@Override
	    	protected FeatureFilter createFeatureFilter() {
	    		
	    		return new FeatureFilter() {
	    			@Override
	    			public boolean checkForOrderingChanges(EStructuralFeature feature) {
	    				return super.checkForOrderingChanges(feature);
	    			}
	    		};
	    	}
	    	
	    };
		
		EMFCompare comparator = EMFCompare.builder().setMatchEngineFactoryRegistry(matchEngineRegistry).
				setDiffEngine(diffEngine).
				build();

		// Compare the two models
		IComparisonScope scope = new DefaultComparisonScope(r0.getResource(), r1.getResource(), null);
		
		Comparison comparison = comparator.compare(scope);
		
		EList<Diff> diffs = comparison.getDifferences();
		for (Diff diff : diffs) {
			System.out.println(diff);
		}
		return diffs.size() == 0;
    */
	
		

		IEObjectMatcher matcher = DefaultMatchEngine.createDefaultEObjectMatcher(UseIdentifiers.WHEN_AVAILABLE);
		IComparisonFactory comparisonFactory = new DefaultComparisonFactory(
				new DefaultEqualityHelperFactory());
		IMatchEngine.Factory matchEngineFactory = new MatchEngineFactoryImpl(matcher, comparisonFactory);
		matchEngineFactory.setRanking(20);
		IMatchEngine.Factory.Registry matchEngineRegistry = new MatchEngineFactoryRegistryImpl();
		matchEngineRegistry.add(matchEngineFactory);
				
		EMFCompare comparator = EMFCompare.builder()
				.setMatchEngineFactoryRegistry(matchEngineRegistry)
				.build();//setDiffEngine(diffEngine).build();

		
		// final IComparisonScope scope = EMFCompare.createDefaultScope(r0.getResource(), r1.getResource());
		IComparisonScope scope = new DefaultComparisonScope(r0.getResource(), r1.getResource(), null);
		Comparison comparison = comparator.compare(scope);
		
		EList<Diff> diffs = comparison.getDifferences();
		for (Diff diff : diffs) {
			System.out.println(diff);
		}
		return diffs.size() == 0;
	}

}
