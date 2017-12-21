package anatlyzer.testing.ocl.mutators;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.m2m.atl.core.emf.EMFModel;

import witness.generator.MetaModel;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.Helper;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.OCL.VariableDeclaration;
import anatlyzer.testing.mutants.MuMetaModel;

// Changes the Attribute data type in the constraint (i.e., the type of a variable)
public class WCO2 extends AbstractMutator {
	
	@Override
	public void generateMutants(ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {
		EDataTypeEList<String>    comments   = getModuleComments(wrapper);
		List<VariableDeclaration> modifiable = (List<VariableDeclaration>)wrapper.allObjectsOf(VariableDeclaration.class);
		filterSubtypes(modifiable, VariableDeclaration.class);
		
		// generate mutants
		for (VariableDeclaration object2modify : modifiable) {
			// if it appears in the body of a target invariant
			LocatedElement exp;
			if ( (exp = needsToBeMutated(object2modify)) != null ) {
				EStructuralFeature featureDefinition = wrapper.source(object2modify).eClass().getEStructuralFeature("type");

				// CASE 1: monovalued feature .........................................................

				if (featureDefinition!=null && featureDefinition.getUpperBound() == 1) {
					EObject object2modify_src = wrapper.source(object2modify);			
					EObject oldFeatureValue   = (EObject)object2modify_src.eGet(featureDefinition); 

					if (oldFeatureValue!=null) {
						List<EObject> replacements = this.replacements(wrapper, (EObject)oldFeatureValue, outputMM);
						for (EObject replacement : replacements) {

							// copy features from original object to replacement
							copyFeatures(oldFeatureValue, replacement);

							// modify original object by replacement
							object2modify_src.eSet(featureDefinition, replacement);

							// save mutant
							if (comments!=null) comments.add(createComment((Helper)exp, object2modify));
							//this.save(atlModel, outputFolder, (Helper)exp);
							
							final EDataTypeEList<String> fComments = comments;
							registerUndo(wrapper, info(object2modify), () -> {
								if (fComments!=null) fComments.remove(fComments.size()-1);

								// copy features from replacement to original object
								copyFeatures(replacement, oldFeatureValue);

								// restore original value
								object2modify_src.eSet(featureDefinition, oldFeatureValue);					
							});			
						}

					}
				}

				// CASE 2: multivalued feature ........................................................

				else if (featureDefinition!=null) {
					List<EObject> value = (List<EObject>)wrapper.source(object2modify).eGet(featureDefinition);

					for (int i=0; i<value.size(); i++) {

						EObject oldFeatureValue = value.get(i);

						List<EObject> replacements = this.replacements(wrapper, (EObject)oldFeatureValue, outputMM);
						for (EObject replacement : replacements) {

							// copy features from original object to replacement
							copyFeatures(oldFeatureValue, replacement);

							// modify original object by replacement
							value.set(i, replacement);

							// save mutant
							if (comments!=null) comments.add(createComment((Helper)exp, object2modify));

							final int idx = i;
							final EDataTypeEList<String> fComments = comments;
							registerUndo(wrapper, info(object2modify), () -> {
								if (fComments!=null) fComments.remove(fComments.size()-1);

								// copy features from replacement to original object
								copyFeatures(replacement, oldFeatureValue);

								// restore original value
								value.set(idx, oldFeatureValue);						
							});			
							
						}
					}
				}
			}
		}
	}
	
	@Override
	public String getDescription() {
		return "WCO2";
	}

	/**
	 * It filters a list of objects to retain only those with the received type.
	 * @param objects list of objects
	 * @param type type of the objects in the resulting list
	 */
	private <Type> void filterSubtypes (List<Type> objects, Class<Type> type) {
		List<Type> subtypes = new ArrayList<Type>();
		for (Type container : objects) {
			boolean isSubtype = true;
			for (Class<?> n : container.getClass().getInterfaces())
				if (type.getName().equals(n.getName()))
					isSubtype = false;
			if (isSubtype) subtypes.add(container);
		}
		objects.removeAll(subtypes);
	}	

	/**
	 * It returns the list of compatible classes that replace a given one. 
	 * @param toReplace
	 * @param metamodel
	 * @return
	 */
	private List<EObject> replacements(ATLModel wrapper, EObject toReplace, MuMetaModel metamodel) {
		List<EObject> replacements = new ArrayList<EObject>();
		EPackage      pack         = toReplace.eClass().getEPackage();
		EClass        mmtype       = (EClass)pack.getEClassifier("OclModelElement");
		
		if (mmtype.isInstance(toReplace)) {
			// search class to replace
			EStructuralFeature featureName  = toReplace.eClass().getEStructuralFeature("name");
			String             featureValue = toReplace.eGet(featureName).toString();
			EClassifier      classToReplace = metamodel.getEClassifier(featureValue); 
		
			if (classToReplace != null && classToReplace instanceof EClass) {
				EClass       replace = (EClass)classToReplace;
				List<EClass> options = new ArrayList<EClass>();

				// search classes to use as replacement
				for (EClassifier classifier : metamodel.getEClassifiers())
					if (classifier instanceof EClass && classifier != replace && isCompatibleWith(replace, (EClass)classifier))
						options.add((EClass)classifier);
				
				// create an OclModelElement for each found replacement class  
				for (EClass option : options) {
					if (option!=null) {
						EObject object = (EObject) newElement(mmtype);
						object.eSet(mmtype.getEStructuralFeature("name"), option.getName());
						replacements.add(object);
					}
				}
			}
		}
		
		return replacements;	
	}

	/**
	 * It checks whether a class c1 is compatible with (i.e. it can substitute safely) another class c2.  
	 * c1 is compatible with c2 if c1 defines at least all features that c2 defines (it can define more).
	 * @param c1 class
	 * @param c2 class
	 * @return
	 */
	private boolean isCompatibleWith (EClass c1, EClass c2) {
		boolean compatible = true;

		for (int i=0; i<c2.getEAllStructuralFeatures().size() && compatible; i++) {
			EStructuralFeature feature2 = c2.getEAllStructuralFeatures().get(i);
			EStructuralFeature feature1 = c1.getEStructuralFeature(feature2.getName());
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
		
		return compatible;
	}

	/**
	 * It copies all features (except name) "from" an object "to" another object.
	 * @param from
	 * @param to
	 * @param features
	 */
	private void copyFeatures (EObject from, EObject to) {
		for (EStructuralFeature feature : from.eClass().getEAllStructuralFeatures()) {
			if (!feature.getName().equals("name") && to.eClass().getEAllStructuralFeatures().contains(feature))
				to.eSet(feature, from.eGet(feature));  
		}
	}	
}
