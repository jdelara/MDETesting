/**
 */
package anatlyzer.testing.coverage;

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
 * @see anatlyzer.testing.coverage.CoverageFactory
 * @model kind="package"
 * @generated
 */
public interface CoveragePackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "coverage";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://anatlyzer/testing/coverage";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "coverage";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	CoveragePackage eINSTANCE = anatlyzer.testing.coverage.impl.CoveragePackageImpl.init();

	/**
	 * The meta object id for the '{@link anatlyzer.testing.coverage.impl.CoverageModelImpl <em>Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see anatlyzer.testing.coverage.impl.CoverageModelImpl
	 * @see anatlyzer.testing.coverage.impl.CoveragePackageImpl#getCoverageModel()
	 * @generated
	 */
	int COVERAGE_MODEL = 0;

	/**
	 * The feature id for the '<em><b>Records</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COVERAGE_MODEL__RECORDS = 0;

	/**
	 * The number of structural features of the '<em>Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COVERAGE_MODEL_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COVERAGE_MODEL_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link anatlyzer.testing.coverage.impl.RecordImpl <em>Record</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see anatlyzer.testing.coverage.impl.RecordImpl
	 * @see anatlyzer.testing.coverage.impl.CoveragePackageImpl#getRecord()
	 * @generated
	 */
	int RECORD = 1;

	/**
	 * The feature id for the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RECORD__LOCATION = 0;

	/**
	 * The feature id for the '<em><b>Parent Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RECORD__PARENT_LOCATION = 1;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RECORD__KIND = 2;

	/**
	 * The number of structural features of the '<em>Record</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RECORD_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Record</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RECORD_OPERATION_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link anatlyzer.testing.coverage.CoverageModel <em>Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Model</em>'.
	 * @see anatlyzer.testing.coverage.CoverageModel
	 * @generated
	 */
	EClass getCoverageModel();

	/**
	 * Returns the meta object for the containment reference list '{@link anatlyzer.testing.coverage.CoverageModel#getRecords <em>Records</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Records</em>'.
	 * @see anatlyzer.testing.coverage.CoverageModel#getRecords()
	 * @see #getCoverageModel()
	 * @generated
	 */
	EReference getCoverageModel_Records();

	/**
	 * Returns the meta object for class '{@link anatlyzer.testing.coverage.Record <em>Record</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Record</em>'.
	 * @see anatlyzer.testing.coverage.Record
	 * @generated
	 */
	EClass getRecord();

	/**
	 * Returns the meta object for the attribute '{@link anatlyzer.testing.coverage.Record#getLocation <em>Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Location</em>'.
	 * @see anatlyzer.testing.coverage.Record#getLocation()
	 * @see #getRecord()
	 * @generated
	 */
	EAttribute getRecord_Location();

	/**
	 * Returns the meta object for the attribute '{@link anatlyzer.testing.coverage.Record#getParentLocation <em>Parent Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Parent Location</em>'.
	 * @see anatlyzer.testing.coverage.Record#getParentLocation()
	 * @see #getRecord()
	 * @generated
	 */
	EAttribute getRecord_ParentLocation();

	/**
	 * Returns the meta object for the attribute '{@link anatlyzer.testing.coverage.Record#getKind <em>Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Kind</em>'.
	 * @see anatlyzer.testing.coverage.Record#getKind()
	 * @see #getRecord()
	 * @generated
	 */
	EAttribute getRecord_Kind();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	CoverageFactory getCoverageFactory();

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
		 * The meta object literal for the '{@link anatlyzer.testing.coverage.impl.CoverageModelImpl <em>Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see anatlyzer.testing.coverage.impl.CoverageModelImpl
		 * @see anatlyzer.testing.coverage.impl.CoveragePackageImpl#getCoverageModel()
		 * @generated
		 */
		EClass COVERAGE_MODEL = eINSTANCE.getCoverageModel();

		/**
		 * The meta object literal for the '<em><b>Records</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COVERAGE_MODEL__RECORDS = eINSTANCE.getCoverageModel_Records();

		/**
		 * The meta object literal for the '{@link anatlyzer.testing.coverage.impl.RecordImpl <em>Record</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see anatlyzer.testing.coverage.impl.RecordImpl
		 * @see anatlyzer.testing.coverage.impl.CoveragePackageImpl#getRecord()
		 * @generated
		 */
		EClass RECORD = eINSTANCE.getRecord();

		/**
		 * The meta object literal for the '<em><b>Location</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RECORD__LOCATION = eINSTANCE.getRecord_Location();

		/**
		 * The meta object literal for the '<em><b>Parent Location</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RECORD__PARENT_LOCATION = eINSTANCE.getRecord_ParentLocation();

		/**
		 * The meta object literal for the '<em><b>Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RECORD__KIND = eINSTANCE.getRecord_Kind();

	}

} //CoveragePackage
