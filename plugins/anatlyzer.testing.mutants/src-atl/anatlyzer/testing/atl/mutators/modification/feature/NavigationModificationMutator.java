package anatlyzer.testing.atl.mutators.modification.feature;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.ForEachOutPatternElement;
import anatlyzer.atlext.ATL.Helper;
import anatlyzer.atlext.ATL.InPatternElement;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.ATL.OutPatternElement;
import anatlyzer.atlext.OCL.CollectionType;
import anatlyzer.atlext.OCL.Iterator;
import anatlyzer.atlext.OCL.LoopExp;
import anatlyzer.atlext.OCL.NavigationOrAttributeCallExp;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.atlext.OCL.OclModelElement;
import anatlyzer.atlext.OCL.PropertyCallExp;
import anatlyzer.atlext.OCL.VariableDeclaration;
import anatlyzer.atlext.OCL.VariableExp;
import anatlyzer.testing.mutants.MuMetaModel;

public class NavigationModificationMutator extends AbstractFeatureModificationMutator {

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
			
			EObject navigationExpression = variable.eContainer();
			while  (navigationExpression != null) {

				if (navigationExpression instanceof NavigationOrAttributeCallExp) {

					EStructuralFeature featureDefinition = navigationExpression.eClass().getEStructuralFeature("name");

					// obtain list of replacements
					String type       = getType(navigationExpression, variable, inputMM, outputMM); 
					String navigation = ((NavigationOrAttributeCallExp)navigationExpression).getName();
					List<Object> replacements = this.featureReplacements(type, navigation, inputMM);

					for (Object replacement : replacements) {
						EObject fNavigationExpression = navigationExpression;
						
						fNavigationExpression.eSet(featureDefinition, replacement);

						// mutation: documentation
						if (comments!=null) comments.add("\n-- MUTATION \"" + this.getDescription() + "\" from " + toString(navigationExpression, variable) + navigation + " to " + toString(navigationExpression, variable) + replacement  + " (line " + ((LocatedElement)navigationExpression).getLocation() + " of original transformation)\n");

						// restore original value
						final EDataTypeEList<String> fComments = comments;
						registerUndo(wrapper, change((LocatedElement) fNavigationExpression), () -> {
							// remove comment
							if (fComments!=null) fComments.remove(fComments.size()-1);
							fNavigationExpression.eSet(featureDefinition, navigation);					
						});
						
					}

					// restore original value
					// Done within the loop to have only one restoration point 
					// wrapper.source(navigationExpression).eSet(featureDefinition, navigation);					
				}
				
				// continue navigation in current expression (e.g. object.feature1.feature2)
				if (navigationExpression instanceof NavigationOrAttributeCallExp && navigationExpression.eContainer() instanceof NavigationOrAttributeCallExp) {
					navigationExpression = navigationExpression.eContainer();
				}
				else navigationExpression = null;
			}
		}
	}

	@Override
	public String getDescription() {
		return "Navigation Modification";
	}

	/**
	 * @param containee
	 * @param container
	 */
	protected String toString (EObject container, EObject containee) {
		EObject next   = containee;
		String  string = "";
		do {
			string += toString(next) + ".";
			next    = next.eContainer(); 
		} while (next!=container && next!=null);
		return string;
	}

	/**
	 * It navigates from the variable "containee" to the navigation expression "container", and returns the type of "container".
	 * @param container
	 * @param containee
	 * @param inputMM
	 * @param outputMM
	 * @return
	 */
	protected String getType (EObject container, VariableExp containee, MuMetaModel inputMM, MuMetaModel outputMM) {
		EClassifier         c   = null;
		VariableDeclaration def = containee.getReferredVariable();
		
		// obtain type (classifier) of variable expression ..............................
		// case 1 -> in pattern element
		if (def instanceof InPatternElement) { 
			c = inputMM.getEClassifier(def.getType().getName());
		}
		// case 2 -> for each out pattern element
		else if (def instanceof ForEachOutPatternElement) {
			def = ((ForEachOutPatternElement)def).getIterator();
			if (def.eContainer() instanceof OutPatternElement) {
				OutPatternElement element = (OutPatternElement)def.eContainer();
				if (element.getType() instanceof OclModelElement)
					c = outputMM.getEClassifier(((OclModelElement)element.getType()).getName());
			}
		}
		// case 3 -> iterator
		else if (def instanceof Iterator) {
			if (def.eContainer() instanceof LoopExp) {
				LoopExp  iterator = (LoopExp)def.eContainer();
				OclExpression exp = iterator.getSource();
				while (c==null && exp!=null) {
					if (exp instanceof OclModelElement)  {
						c   = inputMM.getEClassifier(((OclModelElement)exp).getName());
						exp = null;
					}
					else if (exp instanceof PropertyCallExp) {
						exp = ((PropertyCallExp)exp).getSource();
					}
					else if (exp instanceof VariableExp) {
						c = inputMM.getEClassifier(getType(container, (VariableExp)exp, inputMM, outputMM));
						exp = null;
					}
					else exp = null;
				}
			}
		}
		// case 4 -> variable declaration
		else {
			if (toString(def).equals("self")) {
				EObject helper = containee;
				while (helper!=null && !(helper instanceof Helper)) helper = helper.eContainer();
				if (helper instanceof Helper) {
					if (((Helper)helper).getDefinition().getContext_()!=null &&
						((Helper)helper).getDefinition().getContext_().getContext_()!=null &&
					    ((Helper)helper).getDefinition().getContext_().getContext_() instanceof OclModelElement)
						c = inputMM.getEClassifier(((OclModelElement)((Helper)helper).getDefinition().getContext_().getContext_()).getName());
				}
			}
			else if (((VariableDeclaration)def).getType() instanceof OclModelElement) {
				c = inputMM.getEClassifier(((VariableDeclaration)def).getType().getName());
			}
			else if (((VariableDeclaration)def).getType() instanceof CollectionType) {
				c = inputMM.getEClassifier( ((CollectionType)((VariableDeclaration)def).getType()).getElementType().getName());
			}
		}
			
		// obtain type (classifier) of container ........................................
		EObject  next = containee.eContainer();
		while (c!=null && next!=null && next!=container) {
			if (c instanceof EClass) {
				EStructuralFeature name      = next.eClass().getEStructuralFeature("name");
				EStructuralFeature feature   = null;
				if ( name != null ) {
					String             nameValue = next.eGet(name).toString();
					feature = ((EClass)c).getEStructuralFeature(nameValue);
				} else {
					System.out.println("Warning: " + next.eClass().getName() + " " + "with null name feature ");
				}
				if (feature!=null) {
					c    = feature.getEType();
					next = next.eContainer();
				}
				else next=null;
			}
		}
		
		return c!=null? c.getName() : null;
	}

	@Override
	protected List<Object> replacements(EObject object2modify, String currentAttributeValue, MuMetaModel metamodel) {
		return new ArrayList<Object>();
	}
}
