/**
 */
package anatlyzer.testing.common.validation.impl;

import anatlyzer.testing.common.validation.InputModel;
import anatlyzer.testing.common.validation.ValidationModel;
import anatlyzer.testing.common.validation.ValidationResultPackage;
import anatlyzer.testing.common.validation.ValidationRule;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Validation Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link anatlyzer.testing.common.validation.impl.ValidationModelImpl#getModels <em>Models</em>}</li>
 *   <li>{@link anatlyzer.testing.common.validation.impl.ValidationModelImpl#getValidations <em>Validations</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ValidationModelImpl extends MinimalEObjectImpl.Container implements ValidationModel {
	/**
	 * The cached value of the '{@link #getModels() <em>Models</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModels()
	 * @generated
	 * @ordered
	 */
	protected EList<InputModel> models;

	/**
	 * The cached value of the '{@link #getValidations() <em>Validations</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValidations()
	 * @generated
	 * @ordered
	 */
	protected EList<ValidationRule> validations;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ValidationModelImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ValidationResultPackage.Literals.VALIDATION_MODEL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<InputModel> getModels() {
		if (models == null) {
			models = new EObjectContainmentEList<InputModel>(InputModel.class, this, ValidationResultPackage.VALIDATION_MODEL__MODELS);
		}
		return models;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ValidationRule> getValidations() {
		if (validations == null) {
			validations = new EObjectContainmentEList<ValidationRule>(ValidationRule.class, this, ValidationResultPackage.VALIDATION_MODEL__VALIDATIONS);
		}
		return validations;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ValidationResultPackage.VALIDATION_MODEL__MODELS:
				return ((InternalEList<?>)getModels()).basicRemove(otherEnd, msgs);
			case ValidationResultPackage.VALIDATION_MODEL__VALIDATIONS:
				return ((InternalEList<?>)getValidations()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ValidationResultPackage.VALIDATION_MODEL__MODELS:
				return getModels();
			case ValidationResultPackage.VALIDATION_MODEL__VALIDATIONS:
				return getValidations();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ValidationResultPackage.VALIDATION_MODEL__MODELS:
				getModels().clear();
				getModels().addAll((Collection<? extends InputModel>)newValue);
				return;
			case ValidationResultPackage.VALIDATION_MODEL__VALIDATIONS:
				getValidations().clear();
				getValidations().addAll((Collection<? extends ValidationRule>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case ValidationResultPackage.VALIDATION_MODEL__MODELS:
				getModels().clear();
				return;
			case ValidationResultPackage.VALIDATION_MODEL__VALIDATIONS:
				getValidations().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ValidationResultPackage.VALIDATION_MODEL__MODELS:
				return models != null && !models.isEmpty();
			case ValidationResultPackage.VALIDATION_MODEL__VALIDATIONS:
				return validations != null && !validations.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //ValidationModelImpl
