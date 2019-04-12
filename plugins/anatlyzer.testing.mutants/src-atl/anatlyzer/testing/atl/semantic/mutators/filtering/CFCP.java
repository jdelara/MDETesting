/**
 * Collection filtering change with perturbation (CFCP): this operator modifies an existing
 * filter by influencing its parameters. One criterion could be a property of a class or the
 * type of a class.
 */

package anatlyzer.testing.atl.semantic.mutators.filtering;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atl.types.impl.MetaclassImpl;
import anatlyzer.atlext.ATL.InPattern;
import anatlyzer.atlext.ATL.InPatternElement;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.OCL.BooleanExp;
import anatlyzer.atlext.OCL.Iterator;
import anatlyzer.atlext.OCL.IteratorExp;
import anatlyzer.atlext.OCL.NavigationOrAttributeCallExp;
import anatlyzer.atlext.OCL.OCLFactory;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.atlext.OCL.OclModel;
import anatlyzer.atlext.OCL.OclModelElement;
import anatlyzer.atlext.OCL.OperationCallExp;
import anatlyzer.atlext.OCL.OperatorCallExp;
import anatlyzer.atlext.OCL.VariableDeclaration;
import anatlyzer.atlext.OCL.VariableExp;
import anatlyzer.atlext.OCL.impl.BooleanExpImpl;
import anatlyzer.atlext.OCL.impl.StringExpImpl;
import anatlyzer.testing.atl.mutators.AbstractMutator;
import anatlyzer.testing.mutants.EMFUtils;
import anatlyzer.testing.mutants.MuMetaModel;

public class CFCP extends AbstractMutator {

	@Override
	public void generateMutants(ATLModel wrapper, MuMetaModel inputMM, MuMetaModel outputMM) {
		
		// we will add a comment to the module, documenting the mutation 
		Module module = wrapper.getModule();
		EDataTypeEList<String> comments = null;
		if (module!=null) {
			EStructuralFeature f = module.eClass().getEStructuralFeature("commentsBefore");	
			comments = (EDataTypeEList<String>)module.eGet(f);
		}
		
		// ---------------------------------------------------------------------------
		// RULE FILTERS: a) filter by property; b) filter by subclass	
		// ---------------------------------------------------------------------------
		List<InPattern> inpatterns = (List<InPattern>)wrapper.allObjectsOf(InPattern.class);
		
		for (InPattern inpattern : inpatterns) {
			for (InPatternElement variable : inpattern.getElements()) {
				if (variable.getInferredType() instanceof MetaclassImpl) {				
					EClassifier itClass = inputMM.getEClassifier(((MetaclassImpl)variable.getInferredType()).getName());
					if (itClass instanceof EClass) {		
						OclExpression oldFilter = inpattern.getFilter();					
					
						// a) filter by subclass
						for (EClass subclass : this.subclasses((EClass)itClass, inputMM)) {
							mutate(andVariableIsTypeOf(variable, subclass), oldFilter, inpattern);
							document(comments, "subtype "+subclass.getName(), toString(inpattern)+" rule", ((LocatedElement)inpattern).getLocation());
							undo(wrapper, comments, inpattern, oldFilter);
						}
				
						// b) filter by property
						for (EAttribute att : ((EClass)itClass).getEAllAttributes()) {
							mutate(andVariablePropertyHasValue(variable, att), oldFilter, inpattern);
							document(comments, "property "+att.getName(), toString(inpattern)+" rule", ((LocatedElement)inpattern).getLocation());
							undo(wrapper, comments, inpattern, oldFilter);
						}
					}
				}					
			}
		}
		
		// ---------------------------------------------------------------------------
		// SELECT/REJECT OPERATOR FILTERS: a) filter by property; b) filter by subclass	
		// ---------------------------------------------------------------------------
		List<IteratorExp> iterators = (List<IteratorExp>)wrapper.allObjectsOf(IteratorExp.class);
		
		for (IteratorExp iterator : iterators) {
			if (iterator.getName().equals("select") || iterator.getName().equals("reject")) {
				for (Iterator variable : iterator.getIterators()) {
					if (variable.getInferredType() instanceof MetaclassImpl) {				
						EClassifier itClass = inputMM.getEClassifier(((MetaclassImpl)variable.getInferredType()).getName());
						if (itClass instanceof EClass) {							
							OclExpression oldFilter = iterator.getBody(); 
						
							// a) filter by subclass
							for (EClass subclass : this.subclasses((EClass)itClass, inputMM)) {
								mutate(andVariableIsTypeOf(variable, subclass), oldFilter, iterator);
								document(comments, "subtype "+subclass.getName(), iterator.getName()+" iterator", ((LocatedElement)iterator).getLocation());
								undo(wrapper, comments, iterator, oldFilter);
							}
					
							// b) filter by property
							for (EAttribute att : ((EClass)itClass).getEAllAttributes()) {
								mutate(andVariablePropertyHasValue(variable, att), oldFilter, iterator);
								document(comments, "property "+att.getName(), iterator.getName()+" iterator", ((LocatedElement)iterator).getLocation());
								undo(wrapper, comments, iterator, oldFilter);
							}
						}
					}					
				}
			}
		}			
	}

	@Override
	public String getDescription() {
		return "Collection filtering change with perturbation (CFCP)";
	}
	
	//
	private   OperatorCallExp andVariableIsTypeOf = null;
	protected OperatorCallExp andVariableIsTypeOf (VariableDeclaration variable, EClass type) {
		OperationCallExp itoOperator = null;
		VariableExp      variableExp = null;
		OclModelElement  elementType = null;
		OclModel         metamodel   = null;
		if (andVariableIsTypeOf==null) {
			andVariableIsTypeOf = OCLFactory.eINSTANCE.createOperatorCallExp();								
			itoOperator = OCLFactory.eINSTANCE.createOperationCallExp();
			variableExp = OCLFactory.eINSTANCE.createVariableExp();
			elementType = OCLFactory.eINSTANCE.createOclModelElement();
			metamodel   = OCLFactory.eINSTANCE.createOclModel();
			andVariableIsTypeOf.setOperationName("and");
			andVariableIsTypeOf.getArguments().add(itoOperator);
			itoOperator.setOperationName("oclIsTypeOf");
			itoOperator.setSource(variableExp);
			itoOperator.getArguments().add(elementType);
			elementType.setModel(metamodel);
		}
		else {
			itoOperator = (OperationCallExp)andVariableIsTypeOf.getArguments().get(0);
			variableExp = (VariableExp)itoOperator.getSource();
			elementType = (OclModelElement)itoOperator.getArguments().get(0);
			metamodel   = elementType.getModel();
		}
		variableExp.setReferredVariable(variable);
		elementType.setName(type.getName());
		metamodel.setName(((MetaclassImpl)variable.getInferredType()).getModel().getName());
		return andVariableIsTypeOf;
	}
	
	//
	private   OperatorCallExp andVariablePropertyHasValue = null;
	protected OperatorCallExp andVariablePropertyHasValue (VariableDeclaration variable, EAttribute att) {
		OperatorCallExp equalsOperator = null;
		VariableExp     variableExp    = null;
		NavigationOrAttributeCallExp attributeExp = null;
		if (andVariablePropertyHasValue==null) {
			andVariablePropertyHasValue = OCLFactory.eINSTANCE.createOperatorCallExp();
			equalsOperator = OCLFactory.eINSTANCE.createOperatorCallExp();
			variableExp    = OCLFactory.eINSTANCE.createVariableExp();
			attributeExp   = OCLFactory.eINSTANCE.createNavigationOrAttributeCallExp();
			attributeExp.setSource(variableExp);
			andVariablePropertyHasValue.setOperationName("and");
			andVariablePropertyHasValue.getArguments().add(equalsOperator);
			equalsOperator.setOperationName("=");
			equalsOperator.setSource(attributeExp); 
		}
		else {
			equalsOperator = (OperatorCallExp)andVariablePropertyHasValue.getArguments().get(0);
			attributeExp   = (NavigationOrAttributeCallExp)equalsOperator.getSource();
			variableExp    = (VariableExp)attributeExp.getSource(); 
		}
		attributeExp.setName(att.getName());
		variableExp.setReferredVariable(variable);
		equalsOperator.getArguments().clear();
		equalsOperator.getArguments().add(value(att));
		return andVariablePropertyHasValue;
	}
	
	//
	protected OclExpression value (EAttribute att) {
		OclExpression value = OCLFactory.eINSTANCE.createOclUndefinedExp();
		if (att.getUpperBound()==1) {
			if      (EMFUtils.isBoolean   (att.getEAttributeType().getName())) { value = OCLFactory.eINSTANCE.createBooleanExp(); }
			else if (EMFUtils.isString    (att.getEAttributeType().getName())) { value = OCLFactory.eINSTANCE.createStringExp(); ((StringExpImpl)value).setStringSymbol(""); }
			else if (EMFUtils.isFloating  (att.getEAttributeType().getName())) { value = OCLFactory.eINSTANCE.createRealExp();    }
			else if (EMFUtils.isInteger   (att.getEAttributeType().getName())) { value = OCLFactory.eINSTANCE.createIntegerExp(); }
			else if (EMFUtils.isBigInteger(att.getEAttributeType().getName())) { value = OCLFactory.eINSTANCE.createIntegerExp(); }
		}
		else { 
			value = att.isOrdered()? OCLFactory.eINSTANCE.createSequenceExp() : OCLFactory.eINSTANCE.createSetExp(); 
		}
		return value;
	}

	//
	protected OclExpression trueExp () {
		BooleanExp trueValue = OCLFactory.eINSTANCE.createBooleanExp();
		((BooleanExpImpl)trueValue).setBooleanSymbol(true);
		return trueValue;
	}
	
	//
	protected void mutate (OperatorCallExp andOperator, OclExpression oldFilter, InPattern inpattern) {
		andOperator.setSource(oldFilter!=null? oldFilter : trueExp());
		inpattern.setFilter(andOperator);
	}
	
	//
	protected void mutate (OperatorCallExp andOperator, OclExpression oldFilter, IteratorExp iterator) {
		andOperator.setSource(oldFilter!=null? oldFilter : trueExp());
		iterator.setBody(andOperator);
	}
	
	// 
	protected void document (EDataTypeEList<String> comments, String filter, String location, String lineNumber) {
		if (comments!=null) comments.add("\n-- MUTATION \"" + this.getDescription() + "\" added filter by " + filter + " in " + location + " (line " + lineNumber + " of original transformation)\n");
	}
	
	//
	protected void undo (ATLModel wrapper, EDataTypeEList<String> comments, InPattern inpattern, OclExpression oldFilter) {
		registerUndo(wrapper, change(inpattern), () -> {
			if (comments!=null) comments.remove(comments.size()-1);
			inpattern.setFilter(oldFilter);
		});		
	}
	
	//
	protected void undo (ATLModel wrapper, EDataTypeEList<String> comments, IteratorExp iterator, OclExpression oldFilter) {
		registerUndo(wrapper, change(iterator), () -> {
			if (comments!=null) comments.remove(comments.size()-1);
			iterator.setBody(oldFilter);
		});		
	}
}
