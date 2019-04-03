/**
 */
package anatlyzer.testing.common.validation;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see anatlyzer.testing.common.validation.ValidationResultFactory
 * @model kind="package"
 * @generated
 */
public interface ValidationResultPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "validation";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://anatlyzer/testing/validation_result";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "validation";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ValidationResultPackage eINSTANCE = anatlyzer.testing.common.validation.impl.ValidationResultPackageImpl.init();

	/**
	 * The meta object id for the '{@link anatlyzer.testing.common.validation.impl.ValidationModelImpl <em>Validation Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see anatlyzer.testing.common.validation.impl.ValidationModelImpl
	 * @see anatlyzer.testing.common.validation.impl.ValidationResultPackageImpl#getValidationModel()
	 * @generated
	 */
	int VALIDATION_MODEL = 0;

	/**
	 * The feature id for the '<em><b>Models</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATION_MODEL__MODELS = 0;

	/**
	 * The feature id for the '<em><b>Validations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATION_MODEL__VALIDATIONS = 1;

	/**
	 * The number of structural features of the '<em>Validation Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATION_MODEL_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Validation Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATION_MODEL_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link anatlyzer.testing.common.validation.impl.InputModelImpl <em>Input Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see anatlyzer.testing.common.validation.impl.InputModelImpl
	 * @see anatlyzer.testing.common.validation.impl.ValidationResultPackageImpl#getInputModel()
	 * @generated
	 */
	int INPUT_MODEL = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INPUT_MODEL__NAME = 0;

	/**
	 * The feature id for the '<em><b>Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INPUT_MODEL__PATH = 1;

	/**
	 * The number of structural features of the '<em>Input Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INPUT_MODEL_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Input Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INPUT_MODEL_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link anatlyzer.testing.common.validation.impl.ValidationRuleImpl <em>Validation Rule</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see anatlyzer.testing.common.validation.impl.ValidationRuleImpl
	 * @see anatlyzer.testing.common.validation.impl.ValidationResultPackageImpl#getValidationRule()
	 * @generated
	 */
	int VALIDATION_RULE = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATION_RULE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATION_RULE__DESCRIPTION = 1;

	/**
	 * The feature id for the '<em><b>Objects</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATION_RULE__OBJECTS = 2;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATION_RULE__RESULT = 3;

	/**
	 * The number of structural features of the '<em>Validation Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATION_RULE_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Validation Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATION_RULE_OPERATION_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link anatlyzer.testing.common.validation.ValidationModel <em>Validation Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Validation Model</em>'.
	 * @see anatlyzer.testing.common.validation.ValidationModel
	 * @generated
	 */
	EClass getValidationModel();

	/**
	 * Returns the meta object for the containment reference list '{@link anatlyzer.testing.common.validation.ValidationModel#getModels <em>Models</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Models</em>'.
	 * @see anatlyzer.testing.common.validation.ValidationModel#getModels()
	 * @see #getValidationModel()
	 * @generated
	 */
	EReference getValidationModel_Models();

	/**
	 * Returns the meta object for the containment reference list '{@link anatlyzer.testing.common.validation.ValidationModel#getValidations <em>Validations</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Validations</em>'.
	 * @see anatlyzer.testing.common.validation.ValidationModel#getValidations()
	 * @see #getValidationModel()
	 * @generated
	 */
	EReference getValidationModel_Validations();

	/**
	 * Returns the meta object for class '{@link anatlyzer.testing.common.validation.InputModel <em>Input Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Input Model</em>'.
	 * @see anatlyzer.testing.common.validation.InputModel
	 * @generated
	 */
	EClass getInputModel();

	/**
	 * Returns the meta object for the attribute '{@link anatlyzer.testing.common.validation.InputModel#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see anatlyzer.testing.common.validation.InputModel#getName()
	 * @see #getInputModel()
	 * @generated
	 */
	EAttribute getInputModel_Name();

	/**
	 * Returns the meta object for the attribute '{@link anatlyzer.testing.common.validation.InputModel#getPath <em>Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Path</em>'.
	 * @see anatlyzer.testing.common.validation.InputModel#getPath()
	 * @see #getInputModel()
	 * @generated
	 */
	EAttribute getInputModel_Path();

	/**
	 * Returns the meta object for class '{@link anatlyzer.testing.common.validation.ValidationRule <em>Validation Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Validation Rule</em>'.
	 * @see anatlyzer.testing.common.validation.ValidationRule
	 * @generated
	 */
	EClass getValidationRule();

	/**
	 * Returns the meta object for the attribute '{@link anatlyzer.testing.common.validation.ValidationRule#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see anatlyzer.testing.common.validation.ValidationRule#getName()
	 * @see #getValidationRule()
	 * @generated
	 */
	EAttribute getValidationRule_Name();

	/**
	 * Returns the meta object for the attribute '{@link anatlyzer.testing.common.validation.ValidationRule#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see anatlyzer.testing.common.validation.ValidationRule#getDescription()
	 * @see #getValidationRule()
	 * @generated
	 */
	EAttribute getValidationRule_Description();

	/**
	 * Returns the meta object for the reference list '{@link anatlyzer.testing.common.validation.ValidationRule#getObjects <em>Objects</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Objects</em>'.
	 * @see anatlyzer.testing.common.validation.ValidationRule#getObjects()
	 * @see #getValidationRule()
	 * @generated
	 */
	EReference getValidationRule_Objects();

	/**
	 * Returns the meta object for the attribute '{@link anatlyzer.testing.common.validation.ValidationRule#getResult <em>Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Result</em>'.
	 * @see anatlyzer.testing.common.validation.ValidationRule#getResult()
	 * @see #getValidationRule()
	 * @generated
	 */
	EAttribute getValidationRule_Result();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ValidationResultFactory getValidationResultFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link anatlyzer.testing.common.validation.impl.ValidationModelImpl <em>Validation Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see anatlyzer.testing.common.validation.impl.ValidationModelImpl
		 * @see anatlyzer.testing.common.validation.impl.ValidationResultPackageImpl#getValidationModel()
		 * @generated
		 */
		EClass VALIDATION_MODEL = eINSTANCE.getValidationModel();

		/**
		 * The meta object literal for the '<em><b>Models</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VALIDATION_MODEL__MODELS = eINSTANCE.getValidationModel_Models();

		/**
		 * The meta object literal for the '<em><b>Validations</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VALIDATION_MODEL__VALIDATIONS = eINSTANCE.getValidationModel_Validations();

		/**
		 * The meta object literal for the '{@link anatlyzer.testing.common.validation.impl.InputModelImpl <em>Input Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see anatlyzer.testing.common.validation.impl.InputModelImpl
		 * @see anatlyzer.testing.common.validation.impl.ValidationResultPackageImpl#getInputModel()
		 * @generated
		 */
		EClass INPUT_MODEL = eINSTANCE.getInputModel();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INPUT_MODEL__NAME = eINSTANCE.getInputModel_Name();

		/**
		 * The meta object literal for the '<em><b>Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INPUT_MODEL__PATH = eINSTANCE.getInputModel_Path();

		/**
		 * The meta object literal for the '{@link anatlyzer.testing.common.validation.impl.ValidationRuleImpl <em>Validation Rule</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see anatlyzer.testing.common.validation.impl.ValidationRuleImpl
		 * @see anatlyzer.testing.common.validation.impl.ValidationResultPackageImpl#getValidationRule()
		 * @generated
		 */
		EClass VALIDATION_RULE = eINSTANCE.getValidationRule();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALIDATION_RULE__NAME = eINSTANCE.getValidationRule_Name();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALIDATION_RULE__DESCRIPTION = eINSTANCE.getValidationRule_Description();

		/**
		 * The meta object literal for the '<em><b>Objects</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VALIDATION_RULE__OBJECTS = eINSTANCE.getValidationRule_Objects();

		/**
		 * The meta object literal for the '<em><b>Result</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALIDATION_RULE__RESULT = eINSTANCE.getValidationRule_Result();

	}

} //ValidationResultPackage
