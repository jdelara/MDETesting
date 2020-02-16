package anatlyzer.testing.comparison.jgrapht;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jdt.annotation.NonNull;
import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphExporter;

import anatlyzer.testing.common.IComparator;
import anatlyzer.testing.common.IModel;

public class JGraphtModelComparator extends AbstractJGraphtComparator implements IComparator {
	
	@Override
	public boolean compare(IModel r0, IModel r1) {
		@NonNull
		Resource left = r0.getResource();
		@NonNull
		Resource right = r1.getResource();
		
		IntHolder sizeG1 = new IntHolder();
		IntHolder sizeG2 = new IntHolder();
		
		Graph<Node, Edge> g1 = createGraph(left, -1, sizeG1);
		Graph<Node, Edge> g2 = createGraph(right, sizeG1.value, sizeG2);
		
		if ( sizeG1.value != sizeG2.value )
			return false;
		
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
		
		
        // export(g1, g2);
		
        VF2GraphIsomorphismInspector<Node, Edge> vf2 =
            new VF2GraphIsomorphismInspector<>(g1, g2, vertexComparator, edgeComparator);

// This doesn't seem to solve the problem, but we just count elements to make sure that we don't create veeery large models for nothing (see above)        
//        ThreadedExecution threadedExecution = new ThreadedExecution(vf2);
//        try {
//        	threadedExecution.start();
//        	threadedExecution.join();
//        } catch ( OutOfMemoryError e ) {
//        	return false; // we don't know, report somehow 
//        } catch (InterruptedException e) {
//        	return false; // we don't know, report somehow 
//        }
//        return threadedExecution.isIsomorphism();        		
        
        return vf2.isomorphismExists();
	}

//	private static class ThreadedExecution extends Thread {
//		private VF2GraphIsomorphismInspector<Node, Edge> vf2;
//		private boolean exists;
//
//		public ThreadedExecution(VF2GraphIsomorphismInspector<Node, Edge> vf2) {
//			this.vf2 = vf2;
//		}
//
//		@Override
//		public void run() {
//	        this.exists = vf2.isomorphismExists();
//		}
//		
//		public boolean isIsomorphism() {
//			return exists;
//		}
//	}

	protected boolean equalsAttributes(EObject e1, EObject e2) {
		// TODO: Use qualified names, etc. The metamodel uris must be the same, but the actual ecore instances may not be
		if ( ! e1.eClass().getName().equals(e2.eClass().getName() )) 
			return false;
		
		for (EAttribute att : e1.eClass().getEAllAttributes()) {
			if ( att.isMany() ) {
				Collection<Object> values1 = (List<Object>) e1.eGet(att);
				Collection<Object> values2 = (List<Object>) e2.eGet(e2.eClass().getEStructuralFeature(att.getName()));
				// Check this properly
				boolean b = values1.containsAll(values2) && values2.containsAll(values1);
				if ( ! b )
					return false;
			} else {
				Object value1 = e1.eGet(att);
				Object value2 = e2.eGet(e2.eClass().getEStructuralFeature(att.getName()));
				if ( value1 == null && value2 == null ) {
					// equals
				} else {
					if ( (value1 == null && value2 != null) || (value1 != null && value2 == null))
						return false;
					
					boolean b = value1.equals(value2);
					if ( ! b )
						return false;
				}
			}
		}
		
		return true;
	}
	
	private void export(Graph<Node, Edge> g1, Graph<Node, Edge> g2) {
        GraphExporter<Node, Edge> exporter =
        		new DOTExporter<>(v -> v.getId().
        				replaceAll("\\{|\\}|/|#|-|@|:", "").
        				replaceAll("org.eclipse.emf.ecore.impl.DynamicEObjectImpl", "").
        				replaceAll("\\.", ""), v -> v.toString(), null);
		try {
			exporter.exportGraph(g1, new FileOutputStream("/tmp/g1.dot"));
			exporter.exportGraph(g2, new FileOutputStream("/tmp/g2.dot"));
		} catch (FileNotFoundException | ExportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		System.out.println(g1);
		System.out.println(g2);
	}
	
}
