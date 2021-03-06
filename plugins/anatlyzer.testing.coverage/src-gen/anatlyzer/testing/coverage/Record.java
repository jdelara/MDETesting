/**
 */
package anatlyzer.testing.coverage;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Record</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link anatlyzer.testing.coverage.Record#getLocation <em>Location</em>}</li>
 *   <li>{@link anatlyzer.testing.coverage.Record#getParentLocation <em>Parent Location</em>}</li>
 *   <li>{@link anatlyzer.testing.coverage.Record#getKind <em>Kind</em>}</li>
 * </ul>
 *
 * @see anatlyzer.testing.coverage.CoveragePackage#getRecord()
 * @model
 * @generated
 */
public interface Record extends EObject {
	/**
	 * Returns the value of the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Location</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Location</em>' attribute.
	 * @see #setLocation(String)
	 * @see anatlyzer.testing.coverage.CoveragePackage#getRecord_Location()
	 * @model required="true"
	 * @generated
	 */
	String getLocation();

	/**
	 * Sets the value of the '{@link anatlyzer.testing.coverage.Record#getLocation <em>Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Location</em>' attribute.
	 * @see #getLocation()
	 * @generated
	 */
	void setLocation(String value);

	/**
	 * Returns the value of the '<em><b>Parent Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent Location</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent Location</em>' attribute.
	 * @see #setParentLocation(String)
	 * @see anatlyzer.testing.coverage.CoveragePackage#getRecord_ParentLocation()
	 * @model
	 * @generated
	 */
	String getParentLocation();

	/**
	 * Sets the value of the '{@link anatlyzer.testing.coverage.Record#getParentLocation <em>Parent Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent Location</em>' attribute.
	 * @see #getParentLocation()
	 * @generated
	 */
	void setParentLocation(String value);

	/**
	 * Returns the value of the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Kind</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Kind</em>' attribute.
	 * @see #setKind(String)
	 * @see anatlyzer.testing.coverage.CoveragePackage#getRecord_Kind()
	 * @model required="true"
	 * @generated
	 */
	String getKind();

	/**
	 * Sets the value of the '{@link anatlyzer.testing.coverage.Record#getKind <em>Kind</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Kind</em>' attribute.
	 * @see #getKind()
	 * @generated
	 */
	void setKind(String value);

} // Record
