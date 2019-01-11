package anatlyzer.testing.atl.mutators;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.LocatedElement;

public class ATLAbstractMutator {
	protected IStorageStrategy storage = IStorageStrategy.NULL;
	
	public void setStorageStrategy(IStorageStrategy strategy) {
		this.storage = strategy;
	}
	
	protected void registerUndo(ATLModel mutatedModel, Runnable undo) {
		registerUndo(mutatedModel, new MutationInfo(this), undo);
	}
	
	protected void registerUndo(ATLModel mutatedModel, MutationInfo info, Runnable undo) {
		storage.save(mutatedModel, info);
		undo.run();
	}
	
	public MutationInfo info(LocatedElement elem) {
		return new MutationInfo(this, elem);
	}
	
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
