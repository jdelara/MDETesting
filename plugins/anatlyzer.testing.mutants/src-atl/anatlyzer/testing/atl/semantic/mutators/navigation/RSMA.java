/**
 * Relation sequence modification with addition (RSMA): This operator adds a last step to a navigation.
 */ 
package anatlyzer.testing.atl.semantic.mutators.navigation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
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
import anatlyzer.atlext.OCL.impl.NavigationOrAttributeCallExpImpl;
import anatlyzer.testing.atl.mutators.modification.feature.NavigationModificationMutator;
import anatlyzer.testing.mutants.MuMetaModel;

public class RSMA extends NavigationModificationMutator {

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
			EObject lastNavigation = variable.eContainer();
			while (lastNavigation instanceof NavigationOrAttributeCallExp && lastNavigation.eContainer() instanceof NavigationOrAttributeCallExp) 
				lastNavigation = lastNavigation.eContainer();
			
			// obtain type of last expression in navigation		
			String type = getType(lastNavigation, variable, inputMM, outputMM); 			
			EClass mmsource;
			EStructuralFeature mmfeature;
			if ((mmsource = (EClass)inputMM.getEClassifier(type))!=null &&
				(mmfeature = mmsource.getEStructuralFeature(toString(lastNavigation)))!=null &&
				mmfeature instanceof EReference) {
				mmsource = (EClass)mmfeature.getEType();
			
				final EObject root = lastNavigation.eContainer();
				if (root instanceof OperationCallExp || root instanceof Binding || root instanceof LoopExp || root instanceof InPattern) {
					
				    // for each reference in type of last expression, add extra navigation 
				    for (EReference option : mmsource.getEAllReferences()) {
				    	boolean proceed = true;

						// create additional navigation expression 
						NavigationOrAttributeCallExp extraNavigation = new NavigationOrAttributeCallExpImpl() {};
				    	extraNavigation.setName(option.getName());
						if (root instanceof Binding) 
							((Binding)root).setValue(extraNavigation);
						else if (root instanceof LoopExp) 
							((LoopExp)root).setSource(extraNavigation);
						else if (root instanceof InPattern) 
							((InPattern)root).setFilter(extraNavigation);
						else if (((OperationCallExp)root).getSource()==lastNavigation)
							((OperationCallExp)root).setSource(extraNavigation);
						else if (((OperationCallExp)root).getArguments().size()>0 && ((OperationCallExp)root).getArguments().get(0)==lastNavigation) 
							((OperationCallExp)root).getArguments().set(0, extraNavigation);
						else proceed = false;

						if (proceed) {
							extraNavigation.setSource((NavigationOrAttributeCallExp)lastNavigation);
							
							// mutation: documentation
							if (comments!=null) comments.add("\n-- MUTATION \"" + this.getDescription() + "\" added " + option.getName() + " after " + toString(lastNavigation) + " (line " + ((LocatedElement)lastNavigation).getLocation() + " of original transformation)\n");

							// restore original value
							final EDataTypeEList<String>       fComments        = comments;
							final NavigationOrAttributeCallExp fLastNavigation  = (NavigationOrAttributeCallExp)lastNavigation;
							final NavigationOrAttributeCallExp fExtraNavigation = extraNavigation;
							registerUndo(wrapper, change(extraNavigation), () -> {
								// remove comment
								if (fComments!=null) fComments.remove(fComments.size()-1);
								// undo changes
								if (root instanceof Binding)
									((Binding)root).setValue(fLastNavigation);
								else if (root instanceof LoopExp)                           
									((LoopExp)root).setSource(fLastNavigation);
								else if (root instanceof InPattern) 
									((InPattern)root).setFilter(fLastNavigation);
								else if (((OperationCallExp)root).getSource()==fExtraNavigation)
									((OperationCallExp)root).setSource(fLastNavigation);
								else if (((OperationCallExp)root).getArguments().size()>0 && ((OperationCallExp)root).getArguments().get(0)==fExtraNavigation)
									((OperationCallExp)root).getArguments().set(0, fLastNavigation);
								fExtraNavigation.setSource(null);
							});
						}
					}
				}
			}
		}
	}

	@Override
	public String getDescription() {
		return "Relation sequence modification with addition (RSMA)";
	}

	@Override
	protected List<Object> replacements(EObject object2modify, String currentAttributeValue, MuMetaModel metamodel) {
		return new ArrayList<Object>();
	}
}
