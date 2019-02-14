package anatlyzer.testing.atl.coverage;

import org.eclipse.emf.ecore.util.EcoreUtil;

import anatlyzer.atl.model.ATLModel;
import anatlyzer.atl.util.ATLUtils;
import anatlyzer.atlext.ATL.ATLFactory;
import anatlyzer.atlext.ATL.ActionBlock;
import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.ATL.CalledRule;
import anatlyzer.atlext.ATL.ContextHelper;
import anatlyzer.atlext.ATL.ExpressionStat;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.ATL.OutPattern;
import anatlyzer.atlext.ATL.SimpleOutPatternElement;
import anatlyzer.atlext.OCL.IfExp;
import anatlyzer.atlext.OCL.LetExp;
import anatlyzer.atlext.OCL.OCLFactory;
import anatlyzer.atlext.OCL.OclContextDefinition;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.atlext.OCL.OclFeatureDefinition;
import anatlyzer.atlext.OCL.OclModel;
import anatlyzer.atlext.OCL.OclModelElement;
import anatlyzer.atlext.OCL.Operation;
import anatlyzer.atlext.OCL.OperationCallExp;
import anatlyzer.atlext.OCL.Parameter;
import anatlyzer.atlext.OCL.StringExp;
import anatlyzer.atlext.OCL.VariableDeclaration;
import anatlyzer.atlext.OCL.VariableExp;
import anatlyzer.atlext.processing.AbstractVisitor;

/**
 * Extend a transformation to compute a code coverage.
 * 
 * It adds an additional output model conforming to a coverage meta-model
 * (this can be parameterised): COV : COVERAGE
 * 
 * @author jesus
 *
 */
public class CoverageTransformer extends AbstractVisitor {

	private CoverageTransformerConfiguration configuration;
	private ATLModel model;
	private OclModel coverageModel;
	private CalledRule recordPathRule;
	private ContextHelper recordPathHelper;

	public CoverageTransformer(CoverageTransformerConfiguration configuration, ATLModel model) {
		this.configuration = configuration;
		this.model = model;
	}
	
	public CoverageTransformer(ATLModel model) {
		this(new CoverageTransformerConfiguration(), model);
	}
	
	public static class CoverageTransformerConfiguration {

		private String nsURI;
		private String recordTypeName = "Record";

		public CoverageTransformerConfiguration(String nsURI) {
			this.nsURI = nsURI;
		}
		
		public CoverageTransformerConfiguration() {
			this("http://anatlyzer/testing/coverage");
		}
		
		public CoverageTransformerConfiguration withRecordTypeName(String recordTypeName) {
			this.recordTypeName = recordTypeName;
			return this;
		}
		
		public String getNsURI() {
			return nsURI;
		}

		public String getRecordType() {
			return recordTypeName;
		}
		
	}
	
	public void transform() {
		startVisiting(this.model.getRoot());
	}
	
	@Override
	public void beforeModule(Module self) {
		OclModel model = OCLFactory.eINSTANCE.createOclModel();
		model.setName("COV");
		OclModel metamodel = OCLFactory.eINSTANCE.createOclModel();
		metamodel.setName("COVERAGE");
		model.setMetamodel(metamodel);
		this.coverageModel = model;
		
		self.getOutModels().add(model);
		
		String uri = "@nsURI COVERAGE=" + configuration.getNsURI();		
		self.getCommentsBefore().add(0, uri);
	
		// Record path rule	
		recordPathRule = ATLFactory.eINSTANCE.createCalledRule();
		recordPathRule.setName("recordPath");
		Parameter paramLocation = OCLFactory.eINSTANCE.createParameter();
		paramLocation.setVarName("location");
		paramLocation.setType(OCLFactory.eINSTANCE.createStringType());
		
		Parameter parentLocation = OCLFactory.eINSTANCE.createParameter();
		parentLocation.setVarName("parentLocation");
		parentLocation.setType(OCLFactory.eINSTANCE.createStringType());
		
		Parameter paramKind = OCLFactory.eINSTANCE.createParameter();
		paramKind.setVarName("kind");
		paramKind.setType(OCLFactory.eINSTANCE.createStringType());		
		
		recordPathRule.getParameters().add(paramLocation);
		recordPathRule.getParameters().add(paramKind);
		
		OutPattern outPattern = ATLFactory.eINSTANCE.createOutPattern();
		SimpleOutPatternElement sop = ATLFactory.eINSTANCE.createSimpleOutPatternElement();
		outPattern.getElements().add(sop);
		sop.setVarName("tgt");
		OclModelElement me = OCLFactory.eINSTANCE.createOclModelElement();
		me.setModel(this.coverageModel.getMetamodel());
		me.setName(configuration.getRecordType());
		sop.setType(me);
		recordPathRule.setOutPattern(outPattern);
		
		Binding location = ATLFactory.eINSTANCE.createBinding();
		location.setPropertyName("location");
		location.setValue(createVarRef(paramLocation));
		sop.getBindings().add(location);

		Binding parent = ATLFactory.eINSTANCE.createBinding();
		parent.setPropertyName("parentLocation");
		parent.setValue(createVarRef(parentLocation));
		sop.getBindings().add(parent);


		Binding kind = ATLFactory.eINSTANCE.createBinding();
		kind.setPropertyName("kind");
		kind.setValue(createVarRef(paramKind));
		sop.getBindings().add(kind);
		
		ActionBlock action = ATLFactory.eINSTANCE.createActionBlock();
		ExpressionStat returnStat = ATLFactory.eINSTANCE.createExpressionStat();
		returnStat.setExpression(createVarRef(sop));
		action.getStatements().add(returnStat);
		recordPathRule.setActionBlock(action);
		
		self.getElements().add(recordPathRule);
			
		
		
		// Record path helper
		
		recordPathHelper = ATLFactory.eINSTANCE.createContextHelper();		
		OclFeatureDefinition definition = OCLFactory.eINSTANCE.createOclFeatureDefinition();		
		OclContextDefinition ctx = OCLFactory.eINSTANCE.createOclContextDefinition();
		ctx.setContext_(OCLFactory.eINSTANCE.createOclAnyType());
		definition.setContext_(ctx);
		recordPathHelper.setDefinition(definition);
		Operation operation = OCLFactory.eINSTANCE.createOperation();
		operation.setName("recordPathH");
		operation.setReturnType(OCLFactory.eINSTANCE.createOclAnyType());
		Parameter paramLocationH = OCLFactory.eINSTANCE.createParameter();
		paramLocationH.setVarName("location");
		paramLocationH.setType(OCLFactory.eINSTANCE.createStringType());

		Parameter paramParentH = OCLFactory.eINSTANCE.createParameter();
		paramParentH.setVarName("parentLocation");
		paramParentH.setType(OCLFactory.eINSTANCE.createStringType());

		Parameter paramKindH = OCLFactory.eINSTANCE.createParameter();
		paramKindH.setVarName("kind");
		paramKindH.setType(OCLFactory.eINSTANCE.createStringType());		
		operation.getParameters().add(paramLocationH);
		operation.getParameters().add(paramParentH);		
		operation.getParameters().add(paramKindH);
		
		definition.setFeature(operation);
		
		LetExp body = OCLFactory.eINSTANCE.createLetExp();
		
		// thisModule.recordPath(arg, arg, arg)
		OperationCallExp invoke = OCLFactory.eINSTANCE.createOperationCallExp();
		VariableDeclaration thisModule = OCLFactory.eINSTANCE.createVariableDeclaration();
		thisModule.setVarName("thisModule");
		VariableExp varExp = OCLFactory.eINSTANCE.createVariableExp();
		varExp.setReferredVariable(thisModule);
		invoke.setSource(varExp);
		invoke.setOperationName(recordPathRule.getName());
		invoke.getArguments().add(createVarRef(paramLocationH));
		invoke.getArguments().add(createVarRef(paramParentH));
		invoke.getArguments().add(createVarRef(paramKindH));
		
		VariableDeclaration letVarDcl = OCLFactory.eINSTANCE.createVariableDeclaration();
		letVarDcl.setType(OCLFactory.eINSTANCE.createOclAnyType());
		letVarDcl.setVarName("dummy_tgt");
		letVarDcl.setInitExpression(invoke);
		
		VariableExp returnSelf = OCLFactory.eINSTANCE.createVariableExp();
		VariableDeclaration selfVarDcl = OCLFactory.eINSTANCE.createVariableDeclaration();
		selfVarDcl.setVarName("self");
		returnSelf.setReferredVariable(selfVarDcl);
		
		body.setVariable(letVarDcl);
		body.setIn_(returnSelf);
		
		operation.setBody(body);
		
		// expr.recordPath('x', expr)->xxx
	
		self.getElements().add(recordPathHelper);
	}
	
	private VariableExp createVarRef(VariableDeclaration varDcl) {
		VariableExp varRef = OCLFactory.eINSTANCE.createVariableExp();
		varRef.setReferredVariable(varDcl);
		return varRef;
	}
	
	
	
	@Override
	public void inIfExp(IfExp self) {
		doRecord(self.getThenExpression(), self.getLocation(), self.getThenExpression().getLocation(), "then");
		doRecord(self.getElseExpression(), self.getLocation(), self.getElseExpression().getLocation(), "else");		
	}

	private void doRecord(OclExpression exp, String parentLocation, String location, String kind) {
		StringExp locationString = OCLFactory.eINSTANCE.createStringExp();
		StringExp kindString = OCLFactory.eINSTANCE.createStringExp();
		locationString.setStringSymbol(location);
		kindString.setStringSymbol(kind);
		
		OclExpression parent;
		if ( parentLocation != null ) {
			StringExp loc = OCLFactory.eINSTANCE.createStringExp();
			loc.setStringSymbol(parentLocation);
			parent = loc;
		} else {
			parent = OCLFactory.eINSTANCE.createOclUndefinedExp();
		}
		
		OperationCallExp opcall = OCLFactory.eINSTANCE.createOperationCallExp();
		opcall.setOperationName(ATLUtils.getHelperName(this.recordPathHelper));		
		opcall.getArguments().add(locationString);
		opcall.getArguments().add(parent);
		opcall.getArguments().add(kindString);
		
		EcoreUtil.replace(exp,  opcall);		
		opcall.setSource(exp);		
	}
}
