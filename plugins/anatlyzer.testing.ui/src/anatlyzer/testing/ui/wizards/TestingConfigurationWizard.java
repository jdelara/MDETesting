package anatlyzer.testing.ui.wizards;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

public class TestingConfigurationWizard extends Wizard {

	private ModelGenerationMode page1 = new ModelGenerationMode("Model generation mode");

	private ModelGenerationKind modelGenerationKind = ModelGenerationKind.RANDOM;
	
	@Override
	public void addPages() {
		addPage(page1);
	}
	
	@Override
	public boolean performFinish() {
		return true;
	}
	
	private class ModelGenerationMode extends WizardPage {

		protected ModelGenerationMode(String pageName) {
			super(pageName);
		}

		@Override
		public void createControl(Composite parent) {
			Group genderGroup = new Group(parent, SWT.NONE);
			genderGroup.setLayout(new RowLayout(SWT.HORIZONTAL));
			 
			 
			Button buttonRandom = new Button(genderGroup, SWT.RADIO);
			buttonRandom.setText("Random");
			buttonRandom.addSelectionListener(new GenerationModeSelectionListener(ModelGenerationKind.RANDOM));
			 
			Button buttonMetamodel = new Button(genderGroup, SWT.RADIO);
			buttonMetamodel.setText("Metamodel");			
			buttonMetamodel.addSelectionListener(new GenerationModeSelectionListener(ModelGenerationKind.METAMODEL));
			
			Button pathBased = new Button(genderGroup, SWT.RADIO);
			pathBased.setText("Path-based");			
			pathBased.addSelectionListener(new GenerationModeSelectionListener(ModelGenerationKind.PATH));
			
			setControl(genderGroup);
		}
		
	}
	
	private class GenerationModeSelectionListener implements SelectionListener {

		private ModelGenerationKind kind;

		public GenerationModeSelectionListener(ModelGenerationKind kind) {
			this.kind = kind;
		}
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			TestingConfigurationWizard.this.modelGenerationKind = kind;
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) { }
		
	}
	
	public ModelGenerationKind getModelGenerationKind() {
		return modelGenerationKind;
	}
	
	public static enum ModelGenerationKind {
		RANDOM,
		METAMODEL,
		PATH
	}
}
