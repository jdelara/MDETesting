/**
 */
package anatlyzer.testing.common.validation;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see anatlyzer.testing.common.validation.ValidationResultPackage
 * @generated
 */
public interface ValidationResultFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ValidationResultFactory eINSTANCE = anatlyzer.testing.common.validation.impl.ValidationResultFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Validation Model</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Validation Model</em>'.
	 * @generated
	 */
	ValidationModel createValidationModel();

	/**
	 * Returns a new object of class '<em>Input Model</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Input Model</em>'.
	 * @generated
	 */
	InputModel createInputModel();

	/**
	 * Returns a new object of class '<em>Validation Rule</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Validation Rule</em>'.
	 * @generated
	 */
	ValidationRule createValidationRule();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	ValidationResultPackage getValidationResultPackage();

} //ValidationResultFactory
