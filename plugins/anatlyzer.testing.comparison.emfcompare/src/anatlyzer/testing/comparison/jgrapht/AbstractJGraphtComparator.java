package anatlyzer.testing.comparison.jgrapht;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jdt.annotation.NonNull;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public abstract class AbstractJGraphtComparator {

	protected static class IntHolder {
		int value;
	}
	
	protected Graph<Node, Edge> createGraph(@NonNull Resource r, int otherSize, IntHolder size) {
		int nodeSize = 0;
		Graph<Node, Edge> g = new DefaultDirectedGraph<>(Edge.class);
		
		TreeIterator<EObject> it = r.getAllContents();
		while ( it.hasNext() ) {
			nodeSize++;
			if ( otherSize > 0 && nodeSize > otherSize  ) {
				// We know the graphs are going to be different because the current graph
				// is larger than the other, so we abort here
				size.value = nodeSize;
				return g;
			}
			
			EObject obj = it.next();
			Node n1 = new Node(obj);
			g.addVertex(n1);
			
			for (EStructuralFeature f : obj.eClass().getEStructuralFeatures()) {
				if ( f.isDerived() ) 
					continue;
				if ( f instanceof EAttribute )
					continue;
				
				if ( f.isMany() ) {
					Collection<EObject> elements = (Collection<EObject>) obj.eGet(f);
					for (EObject e : elements) {
						Node n2 = new Node(e);
						g.addVertex(n2);
						g.addEdge(n1, n2);						
					}
				} else {
					EObject e = (EObject) obj.eGet(f);
					if ( e != null ) {
						Node n2 = new Node(e);
						g.addVertex(n2);
						g.addEdge(n1, n2);
					}
				}
			}
		}

		size.value = nodeSize;
		return g;
	}
	
	public static class Node {
		protected EObject element;

		public Node(EObject obj) {
			this.element = obj;
		}

		public String getId() {
			return EcoreUtil.getIdentification(element);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((element == null) ? 0 : element.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			if (element == null) {
				if (other.element != null)
					return false;
			} else if (!element.equals(other.element))
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return element.eClass().getName();
		}
	}
	
	@SuppressWarnings("serial")
	public static class Edge extends DefaultEdge {
		
		@Override
		public Node getSource() {
			return (Node) super.getSource();
		}
		
		@Override
		public Node getTarget() {
			return (Node) super.getTarget();
		}
		
	}

	
}
