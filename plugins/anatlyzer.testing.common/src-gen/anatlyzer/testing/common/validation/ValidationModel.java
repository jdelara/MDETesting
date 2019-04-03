/**
 */
package anatlyzer.testing.common.validation;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Validation Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link anatlyzer.testing.common.validation.ValidationModel#getModels <em>Models</em>}</li>
 *   <li>{@link anatlyzer.testing.common.validation.ValidationModel#getValidations <em>Validations</em>}</li>
 * </ul>
 *
 * @see anatlyzer.testing.common.validation.ValidationResultPackage#getValidationModel()
 * @model
 * @generated
 */
public interface ValidationModel extends EObject {
	/**
	 * Returns the value of the '<em><b>Models</b></em>' containment reference list.
	 * The list contents are of type {@link anatlyzer.testing.common.validation.InputModel}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Models</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Models</em>' containment reference list.
	 * @see anatlyzer.testing.common.validation.ValidationResultPackage#getValidationModel_Models()
	 * @model containment="true"
	 * @generated
	 */
	EList<InputModel> getModels();

	/**
	 * Returns the value of the '<em><b>Validations</b></em>' containment reference list.
	 * The list contents are of type {@link anatlyzer.testing.common.validation.ValidationRule}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Validations</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Validations</em>' containment reference list.
	 * @see anatlyzer.testing.common.validation.ValidationResultPackage#getValidationModel_Validations()
	 * @model containment="true"
	 * @generated
	 */
	EList<ValidationRule> getValidations();

} // ValidationModel
