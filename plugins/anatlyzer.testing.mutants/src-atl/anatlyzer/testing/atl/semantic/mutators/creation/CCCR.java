/**
 * Class' compatible creation replacement (CCCR): This operator replaces the creation
 * of an object by the creation of an object of a compatible type. It could be an
 * instance of a child class, of a parent class, or of a class with a common parent.
 */

package anatlyzer.testing.atl.semantic.mutators.creation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.testing.atl.mutators.modification.type.OutElementModificationMutator;
import anatlyzer.testing.mutants.MuMetaModel;

public class CCCR extends OutElementModificationMutator {

	@Override
	public String getDescription() {
		return "Class' compatible creation replacement (CCCR)";
	}

	@Override
	protected List<EObject> replacements(ATLModel atlModel, EObject toReplace, MuMetaModel[] metamodels) {
		List<EObject> replacements = new ArrayList<EObject>();
		
		EPackage pack   = toReplace.eClass().getEPackage();
		EClass   mmtype = (EClass)pack.getEClassifier("OclModelElement");
		
		// OCL MODEL ELEMENT .......................................................................
		
		if (mmtype.isInstance(toReplace)) {
			for (MuMetaModel metamodel : metamodels) {
				// search class to replace
				EStructuralFeature featureName    = toReplace.eClass().getEStructuralFeature("name");
				String             featureValue   = toReplace.eGet(featureName).toString();
				EClassifier        classToReplace = metamodel.getEClassifier(featureValue); 
		
				if (classToReplace != null && classToReplace instanceof EClass) {
					EClass       replace = (EClass)classToReplace;
					List<EClass> options = new ArrayList<EClass>();

					// search classes to use as replacement
					for (EClassifier replacement : metamodel.getEClassifiers()) {
						if (replacement instanceof EClass && replacement != replace && (
							// subclass
							replace.isSuperTypeOf(((EClass)replacement)) ||
							// ancestor
							((EClass)replacement).isSuperTypeOf(replace) ||
							// class with a common ancestor
							replace.getESuperTypes().stream().anyMatch(sup -> sup.isSuperTypeOf((EClass)replacement)))) {
							options.add((EClass)replacement);
						}
					}
					
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
		}		
		
		return replacements;	
	}
}
