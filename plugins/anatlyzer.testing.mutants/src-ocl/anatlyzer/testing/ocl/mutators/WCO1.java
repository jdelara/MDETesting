package anatlyzer.testing.ocl.mutators;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;

import witness.generator.MetaModel;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.Helper;
import anatlyzer.atlext.ATL.LocatedElement;
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

// WCO1: Changes the constraint by MODIFYING (instead of deleting) the references to a class Attribute
public class WCO1 extends AbstractMutator {

	@Override
	public void generateMutants(ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {
		EDataTypeEList<String> comments    = getModuleComments(wrapper);
		List<NavigationOrAttributeCallExp> navigations = (List<NavigationOrAttributeCallExp>)wrapper.allObjectsOf(NavigationOrAttributeCallExp.class);
		
		for (NavigationOrAttributeCallExp navigation : navigations) {
			EObject            original_navigation = navigation;
			String             original_value   = navigation.getName();
			EStructuralFeature feature          = original_navigation.eClass().getEStructuralFeature("name");
			OclExpression      variable         = navigation.getSource();
			if (variable instanceof VariableExp) {
				// if it appears in the body of a target invariant
				LocatedElement exp;
				if ( (exp = needsToBeMutated(navigation)) != null ) {
					// obtain list of replacements
					String       type   = getType(navigation, (VariableExp)variable, outputMM); 
					List<String> values = featureReplacements(type, navigation.getName(), outputMM);
					for (String new_value : values) {						
						// change value
						original_navigation.eSet(feature, new_value);
				
						// save mutant
						if (comments!=null) comments.add(createComment((Helper)exp, navigation));
						//this.save(atlModel, outputFolder, (Helper)exp);
						//if (comments!=null) comments.remove(comments.size()-1);
						
						final EDataTypeEList<String> fComments = comments;
						registerUndo(wrapper, info(navigation), () -> {
							// restore feature
							original_navigation.eSet(feature, original_value);
							if (fComments!=null) fComments.remove(fComments.size()-1);
						});
						
					}
					// restore feature
					// original_navigation.eSet(feature, original_value);
				}
			}
		}
	}	

	@Override
	public String getDescription() {
		return "WCO1";
	}

	/**
	 * It navigates from the variable "containee" to the navigation expression "container", and returns the type of "container".
	 * @param container
	 * @param containee
	 * @param inputMM
	 * @param outputMM
	 * @return
	 */
	private String getType (EObject container, VariableExp containee, MuMetaModel outputMM) {
		EClassifier         c   = null;
		VariableDeclaration def = containee.getReferredVariable();
		
		// obtain type (classifier) of variable expression ..............................
		// case 1 -> in pattern element (omitted)
		// case 2 -> for each out pattern element (omitted)
		// case 3 -> iterator
		if (def instanceof Iterator) {
			if (def.eContainer() instanceof LoopExp) {
				LoopExp  iterator = (LoopExp)def.eContainer();
				OclExpression exp = iterator.getSource();
				while (c==null && exp!=null) {
					if (exp instanceof OclModelElement)  {
						c = outputMM.getEClassifier(((OclModelElement)exp).getName());
						exp = null;
					}
					else if (exp instanceof PropertyCallExp) {
						exp = ((PropertyCallExp)exp).getSource();
					}
					else if (exp instanceof VariableExp) {
						String name = getType(container, (VariableExp)exp, outputMM);
						c = outputMM.getEClassifier(name);
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
						c = outputMM.getEClassifier(((OclModelElement)((Helper)helper).getDefinition().getContext_().getContext_()).getName());
				}
			}
			else if (((VariableDeclaration)def).getType() instanceof OclModelElement) {
				c = outputMM.getEClassifier(((VariableDeclaration)def).getType().getName());
			}
			else if (((VariableDeclaration)def).getType() instanceof CollectionType) {
				c = outputMM.getEClassifier( ((CollectionType)((VariableDeclaration)def).getType()).getElementType().getName());
			}
		}
			
		// obtain type (classifier) of container ........................................
		EObject next = containee.eContainer();
		while (c!=null && next!=null && next!=container) {
			if (c instanceof EClass) {
				EStructuralFeature name    = next.eClass().getEStructuralFeature("name");
				EStructuralFeature feature = null;
				if (name != null) {
					String nameValue = next.eGet(name).toString();
					feature = ((EClass)c).getEStructuralFeature(nameValue);
					if (feature!=null) {
						c    = feature.getEType();
						next = next.eContainer();
					}
					else next=null;
				}
				else next=null;
			}
		}
		
		return c!=null? c.getName() : null;
	}

	/**
	 * It returns the list of compatible features that replace a given one (features with compatible type and cardinality).   
	 */
	private List<String> featureReplacements (String type, String feature, MuMetaModel metamodel) {
		List<String> replacements = new ArrayList<String>();
		
		EClass mmtype = (EClass)metamodel.getEClassifier(type);
		
		if (mmtype!=null) {
			EStructuralFeature mmfeature = mmtype.getEStructuralFeature(feature);
			
			if (mmfeature!=null) {
				
				// search features to use as replacement
				List<EStructuralFeature> candidates = mmtype.getEAllStructuralFeatures();
				for (int i=0; i<candidates.size(); i++) {
					EStructuralFeature feature2 = candidates.get(i);
					if (mmfeature != feature2 && 
						// compatible type	
						isCompatibleWith(feature2.getEType(), mmfeature.getEType()) &&
						// compatible lower cardinality (both 0, or both bigger than 0)
						((mmfeature.getLowerBound() == 0 && feature2.getLowerBound() == 0) || 
						 (mmfeature.getLowerBound() >  0 && feature2.getLowerBound() >  0)) &&
						// compatible upper cardinality (both 1, or both different from 1) 
						((mmfeature.getUpperBound() == 1 && feature2.getUpperBound() == 1) ||
						 (mmfeature.getUpperBound() != 1 && feature2.getUpperBound() != 1))) {
						replacements.add(feature2.getName());
					}
				}
			}
		}
		
		return replacements;	
	}

	/**
	 * It checks whether a classifier c1 is compatible with (i.e. it can substitute safely) another classifier c2.  
	 * If c1 and c2 are classes, then c1 is compatible with c2 if c1 defines at least all features that c2 defines 
	 * (it can define more). Two primitive types are compatible only if they are the same. 
	 * @param c1 class
	 * @param c2 class
	 * @return
	 */
	private boolean isCompatibleWith (EClassifier c1, EClassifier c2) {
		boolean compatible = true;

		// c1 and c2 are classes
		if (c1 instanceof EClass && c2 instanceof EClass) {
			for (int i=0; i<((EClass)c2).getEAllStructuralFeatures().size() && compatible; i++) {
				EStructuralFeature feature2 = ((EClass)c2).getEAllStructuralFeatures().get(i);
				EStructuralFeature feature1 = ((EClass)c1).getEStructuralFeature(feature2.getName());
				// c1 cannot substitute c2 if:
				// - c1 lacks one of the features of c2
				// - c1 has a feature with same name but different type
				// - c1 has a feature with same name but it is monovalued, while the one in c1 is multivalued (or viceversa)
				if (feature1 == null ||
					feature1.getEType() != feature2.getEType() ||
					(feature1.getUpperBound()==1 && feature2.getUpperBound()!=1) ||
					(feature1.getUpperBound()!=1 && feature2.getUpperBound()==1) ) 
					compatible = false;
			}
		}
		
		// only one of them is a class
		else if ((c1 instanceof EClass && !(c2 instanceof EClass)) ||
				 (c2 instanceof EClass && !(c1 instanceof EClass)))
			compatible = false;
		
		// c1 and c2 are primitive types
		else compatible = c1.getName().equals(c2.getName());
		
		return compatible;
	}
}
