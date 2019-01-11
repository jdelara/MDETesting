package anatlyzer.testing.atl.mutators;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.NavigationFilter;

import anatlyzer.testing.atl.mutators.creation.BindingCreationMutator;
import anatlyzer.testing.atl.mutators.creation.InElementCreationMutator;
import anatlyzer.testing.atl.mutators.creation.OutElementCreationMutator;
import anatlyzer.testing.atl.mutators.deletion.ArgumentDeletionMutator;
import anatlyzer.testing.atl.mutators.deletion.BindingDeletionMutator;
import anatlyzer.testing.atl.mutators.deletion.FilterDeletionMutator;
import anatlyzer.testing.atl.mutators.deletion.HelperContextDeletionMutator;
import anatlyzer.testing.atl.mutators.deletion.HelperDeletionMutator;
import anatlyzer.testing.atl.mutators.deletion.ParameterDeletionMutator;
import anatlyzer.testing.atl.mutators.deletion.ParentRuleDeletionMutator;
import anatlyzer.testing.atl.mutators.deletion.RuleDeletionMutator;
import anatlyzer.testing.atl.mutators.deletion.VariableDeletionMutator;
import anatlyzer.testing.atl.mutators.modification.ParentRuleModificationMutator;
import anatlyzer.testing.atl.mutators.modification.PrimitiveValueModificationMutator;
import anatlyzer.testing.atl.mutators.modification.feature.BindingModificationMutator;
import anatlyzer.testing.atl.mutators.modification.feature.NavigationModificationMutator;
import anatlyzer.testing.atl.mutators.modification.invocation.CollectionOperationModificationMutator;
import anatlyzer.testing.atl.mutators.modification.invocation.HelperOperationModificationMutator;
import anatlyzer.testing.atl.mutators.modification.invocation.IteratorModificationMutator;
import anatlyzer.testing.atl.mutators.modification.invocation.OperatorModificationMutator;
import anatlyzer.testing.atl.mutators.modification.invocation.PredefinedOperationModificationMutator;
import anatlyzer.testing.atl.mutators.modification.type.ArgumentModificationMutator;
import anatlyzer.testing.atl.mutators.modification.type.CollectionModificationMutator;
import anatlyzer.testing.atl.mutators.modification.type.HelperContextModificationMutator;
import anatlyzer.testing.atl.mutators.modification.type.HelperReturnModificationMutator;
import anatlyzer.testing.atl.mutators.modification.type.InElementModificationMutator;
import anatlyzer.testing.atl.mutators.modification.type.OutElementModificationMutator;
import anatlyzer.testing.atl.mutators.modification.type.ParameterModificationMutator;
import anatlyzer.testing.atl.mutators.modification.type.VariableModificationMutator;
import anatlyzer.testing.ocl.mutators.InvariantMutatorStrategy;

/**
 * A registry for mutators
 * 
 * @author jesus
 *
 */
public interface IMutatorRegistry {

	public IMutatorRegistry addMutator(AbstractMutator mutator);
	
	List<? extends AbstractMutator> getMutators();
	
	public static class Base implements IMutatorRegistry {
		private List<AbstractMutator> mutators = new ArrayList<AbstractMutator>();
		
		@Override
		public IMutatorRegistry addMutator(AbstractMutator mutator) {
			mutators.add(mutator);
			return this;
		}
		
		@Override
		public List<? extends AbstractMutator> getMutators() {
			return mutators;
		}

	}
	
	public static class AllMutators extends Base {

		public AllMutators() {
			// creation
			addMutator(new BindingCreationMutator());
			addMutator(new InElementCreationMutator());
			addMutator(new OutElementCreationMutator());
			// deletion
			addMutator(new ArgumentDeletionMutator());
			addMutator(new BindingDeletionMutator());
			addMutator(new FilterDeletionMutator());
			addMutator(new HelperDeletionMutator());
			addMutator(new InElementCreationMutator());
			addMutator(new OutElementCreationMutator());
			addMutator(new ParameterDeletionMutator());
			addMutator(new ParentRuleDeletionMutator());
			addMutator(new RuleDeletionMutator());
			addMutator(new VariableDeletionMutator());
			// modification
			addMutator(new ParentRuleModificationMutator());
			addMutator(new PrimitiveValueModificationMutator());
			// modification.feature
			addMutator(new BindingModificationMutator());
			addMutator(new NavigationModificationMutator());
			// modification.invocation
			addMutator(new CollectionOperationModificationMutator());
			addMutator(new HelperOperationModificationMutator());
			addMutator(new IteratorModificationMutator());
			addMutator(new OperatorModificationMutator());
			addMutator(new PredefinedOperationModificationMutator());
			// type
			addMutator(new ArgumentModificationMutator());
			addMutator(new CollectionModificationMutator());
			addMutator(new HelperContextModificationMutator());
			addMutator(new HelperReturnModificationMutator());
			addMutator(new InElementModificationMutator());
			addMutator(new OutElementModificationMutator());
			addMutator(new ParameterModificationMutator());
			addMutator(new VariableModificationMutator());
		}
	}
	
	// TODO: Create a registry of mutators that preserve typing
}
