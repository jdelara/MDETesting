package anatlyzer.testing.atl.mutators;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.testing.atl.mutators.MutationInfo.ChangeKind;
import anatlyzer.testing.mutants.IMutantGenerator.IMutantReference;

public class ATLAbstractMutator {
	protected IStorageStrategy storage = IStorageStrategy.NULL;
	
	public List<IMutantReference> generatedMutants = new ArrayList<>();
	
	public List<? extends IMutantReference> getGeneratedMutants() {
		return generatedMutants;
	}
	
	public void setStorageStrategy(IStorageStrategy strategy) {
		this.storage = strategy;
	}
	
//	protected void registerUndo(ATLModel mutatedModel, LocatedElement elem, Runnable undo) {
//		registerUndo(mutatedModel, new MutationInfo(this, elem), undo);
//	}

//	protected void registerUndo(ATLModel mutatedModel, Runnable undo) {
//		registerUndo(mutatedModel, new MutationInfo(this), undo);
//	}
	
	protected void registerUndo(ATLModel mutatedModel, MutationInfo info, Runnable undo) {
		IMutantReference ref = storage.save(mutatedModel, info);
		if ( ref != null )
			generatedMutants.add(ref);
		undo.run();
		storage.onRestoredTransformation(mutatedModel, info, ref);
	}
	
	public MutationInfo remove(LocatedElement elem) {
		return new MutationInfo(this, elem, ChangeKind.REMOVE);
	}
	
	public MutationInfo add(LocatedElement elem) {
		return new MutationInfo(this, elem, ChangeKind.ADD);
	}
	
	public MutationInfo change(LocatedElement elem) {
		return new MutationInfo(this, elem, ChangeKind.CHANGE);
	}
	
	public MutationInfo replace(LocatedElement elem, LocatedElement replacement) {
		return new MutationInfo(this, elem, replacement, ChangeKind.REPLACE);
	}
	
	//public MutationInfo info(LocatedElement elem) {
	//	return new MutationInfo(this, elem);
	//}
	
	// Copied from EMFModel#newElement
	protected EObject newElement(EClass ec) {
//		Resource mainResource = getResource();
//		if (mainResource == null) {
//			mainResource = modelFactory.getResourceSet().createResource(URI.createURI("new-model")); //$NON-NLS-1$
//			// TODO [Resource.Factory issues] use the correct factory
//			// MAIN ISSUE HERE...
//			// resource must be created within the model creation
//			setResource(mainResource);
//		}
		EObject ret = null;
		ret = ec.getEPackage().getEFactoryInstance().create(ec);
//		mainResource.getContents().add(ret);
		return ret;
	}
}
