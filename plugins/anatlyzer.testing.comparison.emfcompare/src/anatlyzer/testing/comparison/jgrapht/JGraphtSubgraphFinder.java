package anatlyzer.testing.comparison.jgrapht;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.annotation.NonNull;
import org.jgrapht.Graph;
import org.jgrapht.alg.isomorphism.VF2SubgraphIsomorphismInspector;

public class JGraphtSubgraphFinder extends AbstractJGraphtComparator {

	// TODO: Consider two signatures: one with IModel for uses within the framework and using resources for external users
	//       who wants less coupling
	public boolean findSubgraph(@NonNull Resource graph, @NonNull Resource subgraph) {
		IntHolder sizeG1 = new IntHolder();
		IntHolder sizeG2 = new IntHolder();
		
		Graph<Node, Edge> g1 = createGraph(graph, -1, sizeG1);
		Graph<Node, Edge> g2 = createGraph(subgraph, sizeG1.value, sizeG2);

		Comparator<Node> vertexComparator = (n1, n2) -> {
			if ( equalsAttributes(n1.element, n2.element) )
				return 0;
			//if ( n1.element.eClass().getName().equals(n2.element.eClass().getName()) )
			//	return 0;
					
			return -1;
		};
		
		Comparator<Edge> edgeComparator = (e1, e2) -> {
//			if ( e1.getSource().equals(e2.getSource()) && 
//					e1.getTarget().equals(e2.getTarget()) ) return 0;
			if ( vertexComparator.compare(e1.getSource(), e2.getSource()) == 0 &&
					vertexComparator.compare(e1.getTarget(), e2.getTarget()) == 0 ) {
				return 0;
			}
				
			return -1;
		};

		VF2SubgraphIsomorphismInspector<Node, Edge> inspector = new VF2SubgraphIsomorphismInspector<>(g1, g2, vertexComparator, edgeComparator);

		return inspector.isomorphismExists();
	}
	
	protected boolean equalsAttributes(EObject e1, EObject e2) {
		// TODO: Use qualified names, etc. The metamodel uris must be the same, but the actual ecore instances may not be
		if ( ! e1.eClass().getName().equals(e2.eClass().getName() )) 
			return false;
		
		for (EAttribute att : e1.eClass().getEAllAttributes()) {
			EStructuralFeature att2 = e2.eClass().getEStructuralFeature(att.getName());
			if ( att.isMany() ) {
				Collection<Object> values1 = (List<Object>) e1.eGet(att);
				Collection<Object> values2 = (List<Object>) e2.eGet(att2);
				// Check this properly
				boolean b = values1.containsAll(values2); // But not the other way around && values2.containsAll(values1);
				if ( ! b )
					return false;
			} else {
				if (e1.eIsSet(att) && !(e2.eIsSet(att2)))
					continue;
				
				Object value1 = e1.eGet(att);
				Object value2 = e2.eGet(att2);
				if ( value1 == null && value2 == null ) {
					// equals
				} else {
					if ( (value1 == null && value2 != null) ) // But not the other way around || (value1 != null && value2 == null))
						return false;
					
					boolean b = value1.equals(value2);
					if ( ! b )
						return false;
				}
			}
		}
		
		return true;
	}
	
}
