package anatlyzer.testing.atl.typing.mutators;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.testing.atl.mutators.deletion.BindingDeletionMutator;

public class RemoveBindingOfCompulsoryFeatureMutator extends BindingDeletionMutator {
	@Override
	public String getDescription() {
		return "Remove Binding Of Compulsory Feature Mutator (RBCF)";
	}
	
	@Override
	protected boolean confirmDeletion (LocatedElement element) {
		if (!(element instanceof Binding)) return false;
		Binding b = (Binding) element;
		EStructuralFeature esf = (EStructuralFeature) b.getWrittenFeature();
		return (esf.getLowerBound()!=0 && esf.getDefaultValue()==null);
	}
}
