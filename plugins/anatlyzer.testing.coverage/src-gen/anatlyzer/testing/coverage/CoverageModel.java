/**
 */
package anatlyzer.testing.coverage;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link anatlyzer.testing.coverage.CoverageModel#getRecords <em>Records</em>}</li>
 * </ul>
 *
 * @see anatlyzer.testing.coverage.CoveragePackage#getCoverageModel()
 * @model
 * @generated
 */
public interface CoverageModel extends EObject {
	/**
	 * Returns the value of the '<em><b>Records</b></em>' containment reference list.
	 * The list contents are of type {@link anatlyzer.testing.coverage.Record}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Records</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Records</em>' containment reference list.
	 * @see anatlyzer.testing.coverage.CoveragePackage#getCoverageModel_Records()
	 * @model containment="true"
	 * @generated
	 */
	EList<Record> getRecords();

} // CoverageModel
