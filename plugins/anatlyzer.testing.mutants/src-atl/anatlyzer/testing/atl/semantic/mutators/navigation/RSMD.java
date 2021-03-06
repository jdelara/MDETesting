/**
 * Relation sequence modification with deletion (RSMD): This operator removes the last step off from a composed navigation.
 */
package anatlyzer.testing.atl.semantic.mutators.navigation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.ATL.InPattern;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.OCL.LoopExp;
import anatlyzer.atlext.OCL.NavigationOrAttributeCallExp;
import anatlyzer.atlext.OCL.OperationCallExp;
import anatlyzer.atlext.OCL.VariableExp;
import anatlyzer.testing.atl.mutators.modification.AbstractModificationMutator;
import anatlyzer.testing.mutants.MuMetaModel;

public class RSMD extends AbstractModificationMutator {

	@Override
	public void generateMutants(ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {
		List<VariableExp> variables = (List<VariableExp>)wrapper.allObjectsOf(VariableExp.class);
		
		// we will add a comment to the module, documenting the mutation 
		Module module = wrapper.getModule();
		EDataTypeEList<String> comments = null;
		if (module!=null) {
			EStructuralFeature f = module.eClass().getEStructuralFeature("commentsBefore");	
			comments = (EDataTypeEList<String>)module.eGet(f);
		}
		
		// navigate navigation expressions starting from each variable
		for (VariableExp variable : variables) {
			
			// obtain last expression in navigation
			EObject lastNavigation     = variable.eContainer();
			EObject previousNavigation = variable;			
			while (lastNavigation instanceof NavigationOrAttributeCallExp && lastNavigation.eContainer() instanceof NavigationOrAttributeCallExp) {
				previousNavigation = lastNavigation;
				lastNavigation     = lastNavigation.eContainer();
			}
			
			if (variable.eContainer() != lastNavigation) {
				final EObject root = lastNavigation.eContainer();
				if (root instanceof OperationCallExp || root instanceof Binding || root instanceof LoopExp || root instanceof InPattern) {
					
					// remove last navigation step
					if (root instanceof Binding) 
						 ((Binding)root).setValue((NavigationOrAttributeCallExp)previousNavigation);
					else if (root instanceof LoopExp) 
						 ((LoopExp)root).setSource((NavigationOrAttributeCallExp)previousNavigation);
					else if (root instanceof InPattern) 
						 ((InPattern)root).setFilter((NavigationOrAttributeCallExp)previousNavigation);
					else if (((OperationCallExp)root).getSource()==lastNavigation)
						 ((OperationCallExp)root).setSource((NavigationOrAttributeCallExp)previousNavigation);
					else if (((OperationCallExp)root).getArguments().size()>0 && ((OperationCallExp)root).getArguments().get(0)==lastNavigation) 
						((OperationCallExp)root).getArguments().set(0, (NavigationOrAttributeCallExp)previousNavigation);
					
					// mutation: documentation
					if (comments!=null) comments.add("\n-- MUTATION \"" + this.getDescription() + "\" deleted " + toString(lastNavigation) + " after " + toString(previousNavigation) + " (line " + ((LocatedElement)lastNavigation).getLocation() + " of original transformation)\n");
					
					// restore original value
					final EDataTypeEList<String>       fComments           = comments;
					final NavigationOrAttributeCallExp fLastNavigation     = (NavigationOrAttributeCallExp)lastNavigation;
					final NavigationOrAttributeCallExp fPreviousNavigation = (NavigationOrAttributeCallExp)previousNavigation;
					registerUndo(wrapper, change((LocatedElement) root), () -> {
						// remove comment
						if (fComments!=null) fComments.remove(fComments.size()-1);
						if (root instanceof Binding)                           
							((Binding)root).setValue(fLastNavigation);
						else if (root instanceof LoopExp)                           
							((LoopExp)root).setSource(fLastNavigation);
						else if (root instanceof InPattern) 
							 ((InPattern)root).setFilter(fLastNavigation);
						else if (((OperationCallExp)root).getSource()==fPreviousNavigation)
							((OperationCallExp)root).setSource(fLastNavigation);
						else if (((OperationCallExp)root).getArguments().size()>0 && ((OperationCallExp)root).getArguments().get(0)==fPreviousNavigation)
							((OperationCallExp)root).getArguments().set(0, fLastNavigation);
						fLastNavigation.setSource(fPreviousNavigation);
					});
				}
			}
		}
	}

	@Override
	public String getDescription() {
		return "Relation sequence modification with deletion (RSMD)";
	}

	@Override
	protected List<Object> replacements(EObject object2modify, String currentAttributeValue, MuMetaModel metamodel) {
		return new ArrayList<Object>();
	}

}
