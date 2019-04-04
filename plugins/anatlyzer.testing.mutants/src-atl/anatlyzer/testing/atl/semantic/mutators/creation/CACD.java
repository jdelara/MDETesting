/**
 * Classes' association creation deletion (CACD): This operator deletes the creation of
 * an association between two instances.
 */

package anatlyzer.testing.atl.semantic.mutators.creation;

import org.eclipse.emf.ecore.EReference;

import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.testing.atl.mutators.deletion.BindingDeletionMutator;

public class CACD extends BindingDeletionMutator {
	
	@Override
	public String getDescription() {
		return "Classes' association creation deletion (CACD)";
	}
	
	@Override
	protected boolean confirmDeletion (LocatedElement element) {
		// delete only bindings of references
		return element instanceof Binding && ((Binding)element).getWrittenFeature() instanceof EReference; 
	}

}
