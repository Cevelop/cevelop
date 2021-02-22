/******************************************************************************
 * Copyright (c) 2015 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Peter Sommerlad peter.sommerlad@hsr.ch
 ******************************************************************************/
package com.cevelop.namespactor.refactoring.itda;

import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.cdt.core.dom.ast.ASTNodeFactoryFactory;
import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTAttributeOwner;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTNodeSelector;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTAliasDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTAlignmentSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTReferenceOperator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.util.StringUtil;
import org.eclipse.cdt.internal.core.dom.parser.ASTInternal;
import org.eclipse.cdt.internal.core.dom.parser.ASTQueries;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDefinition;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamedTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTParameterDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTQualifiedName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPTypedef;
import org.eclipse.cdt.internal.core.model.ASTCache.ASTRunnable;
import org.eclipse.cdt.internal.ui.refactoring.Container;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.namespactor.astutil.ASTNodeFactory;
import com.cevelop.namespactor.astutil.DeclarationHelper;
import com.cevelop.namespactor.astutil.NSNameHelper;
import com.cevelop.namespactor.astutil.NSSelectionHelper;
import com.cevelop.namespactor.refactoring.RefactoringBase;
import com.cevelop.namespactor.refactoring.rewrite.ASTRewriteStore;
import com.cevelop.namespactor.resources.Labels;


/**
 * @author peter.sommerlad@hsr.ch
 */
@SuppressWarnings("restriction")
public class ITDARefactoring extends RefactoringBase {

    public static class FindTypeAliasName implements ASTRunnable {

        private final Region textSelection;
        private Region       selectedTypeDefRegion;
        private IType        theUnderlyingType;           // bad approach, better obtain the declarator and decl-specifier and construct a new one
        private IASTNode     theNodeToReplace;
        public IBinding      theBindingOfTheNodeToReplace;

        public IType getTheType() {
            return theUnderlyingType;
        }

        public FindTypeAliasName(Region textSelection) {
            this.textSelection = textSelection;
            this.theNodeToReplace = null;
            this.theUnderlyingType = null;
        }

        public Region getSelectedTypeDefRegion() {
            return selectedTypeDefRegion;
        }

        @Override
        public IStatus runOnAST(ILanguage lang, IASTTranslationUnit astRoot) throws CoreException {
            IASTNodeSelector selector = astRoot.getNodeSelector(null);
            IASTName name = selector.findEnclosingName(textSelection.getOffset(), textSelection.getLength());
            if (name == null) {
                return Status.CANCEL_STATUS;
            }
            // find enclosing declaration and if it is a typedef, adjust region.
            IBinding thebinding = ((ICPPASTName) name).resolveBinding();

            if (thebinding instanceof CPPTypedef) {
                theBindingOfTheNodeToReplace = thebinding;

                theUnderlyingType = ((CPPTypedef) thebinding).getType(); // to be removed
                theNodeToReplace = name;
                return Status.OK_STATUS;
            } else {
                return Status.CANCEL_STATUS;
            }

        }

        public IASTNode getTheNodeToReplace() {
            return theNodeToReplace;
        }

    }

    private IType             theUnderlyingType;
    private IASTNode          theNodeToReplace;
    private FindTypeAliasName runner;

    public ITDARefactoring(ICElement element, Optional<ITextSelection> selection) {
        super(element, selection);
        selection.filter(s -> s.getOffset() < 0).ifPresent(i -> System.err.println("TD2ARefactoring text selection invalid from marker resolution"));
        factory = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();
    }

    public IASTDeclSpecifier      originspecifier;
    public IASTDeclarator         origindeclarator;
    public IASTDeclarator         originoutermostdeclarator;   // only relevant if different from origindeclarator
    public IBinding               theBindingOfTheNodeToReplace;
    public IASTName               theNameOfTheAlias;           // this is the node of the alias definition, it allows to check the binding
    private final ICPPNodeFactory factory;

    public IASTNode getNodeToReplace(final Region textSelection, IASTTranslationUnit tu) {

        runner = new FindTypeAliasName(textSelection);
        try {
            runner.runOnAST(null, tu);
        } catch (CoreException e) {}
        if (runner.getTheNodeToReplace() != null) {
            theBindingOfTheNodeToReplace = runner.theBindingOfTheNodeToReplace;
            theUnderlyingType = runner.getTheType();
            determineOriginDeclaratorAndDeclSpecifier(theBindingOfTheNodeToReplace);

            return runner.getTheNodeToReplace();
        }
        // now search using a visitor, if shortcut above doesn't succeed.
        final Container<IASTNode> container = new Container<>();
        final Region selection = runner.getSelectedTypeDefRegion() != null ? runner.getSelectedTypeDefRegion() : textSelection;

        tu.accept(new ASTVisitor() {

            {
                shouldVisitNames = true;
            }

            @Override
            public int leave(IASTName name) {
                // find enclosing simple declaration and then check if selection is here
                // and find the typedef SimpleDeclaration if it actually is one
                IASTNode node = name;
                if (container.getObject() == null && NSSelectionHelper.isSelectionOnExpression(selection, name)) {
                    IBinding thebinding = ((ICPPASTName) name).resolveBinding();

                    if (thebinding instanceof CPPTypedef) {

                        container.setObject(node);
                        return ASTVisitor.PROCESS_ABORT; // done container is filled
                    }
                }
                return super.leave(name);
            }

            @Override
            public int visit(IASTName name) {
                return super.visit(name);
            }
        });
        if (container.getObject() != null) {
            IBinding thebinding = ((ICPPASTName) container.getObject()).resolveBinding();
            if (thebinding instanceof CPPTypedef) {
                theBindingOfTheNodeToReplace = thebinding;
                theUnderlyingType = ((CPPTypedef) thebinding).getType();
                determineOriginDeclaratorAndDeclSpecifier(theBindingOfTheNodeToReplace);

            }
        }
        return container.getObject();
    }

    public void determineOriginDeclaratorAndDeclSpecifier(IBinding thebinding) {
        IASTNode[] declarationsOfBinding = ASTInternal.getDeclarationsOfBinding(thebinding);
        if (declarationsOfBinding.length > 0) {
            IASTNode declaration = declarationsOfBinding[0];
            IASTNode parent = declaration.getParent();
            // 2 cases, alias or typedef
            if (parent instanceof ICPPASTDeclarator) { // typedef
                origindeclarator = (ICPPASTDeclarator) parent;
                origindeclarator = ASTQueries.findOutermostDeclarator(origindeclarator);
                //				IASTDeclarator definingdeclarator = ASTQueries.findTypeRelevantDeclarator(origindeclarator);
                // need to find simpledeclaration around me...
                if (origindeclarator.getParent() instanceof IASTSimpleDeclaration) {
                    originspecifier = ((IASTSimpleDeclaration) (origindeclarator.getParent())).getDeclSpecifier();
                }
                theNameOfTheAlias = ASTQueries.findInnermostDeclarator(origindeclarator).getName();
            } else if (parent instanceof ICPPASTAliasDeclaration) {
                ICPPASTAliasDeclaration alias = (ICPPASTAliasDeclaration) parent;
                ICPPASTTypeId typeid = alias.getMappingTypeId();
                originspecifier = typeid.getDeclSpecifier();
                origindeclarator = typeid.getAbstractDeclarator();
                theNameOfTheAlias = alias.getAlias();
            } else {
                // oops, unexpected.
            }
        }
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        SubMonitor sm = SubMonitor.convert(pm, 10);

        super.checkInitialConditions(sm.newChild(6));

        if (initStatus.hasFatalError()) {
            sm.done();
            return initStatus;
        }

        theNodeToReplace = getNodeToReplace(selectedRegion, getAST(tu, pm));

        if (theNodeToReplace == null) {
            initStatus.addFatalError(Labels.TD2A_NoNameSelected);
        }

        sm.done();
        return initStatus;
    }

    @Override
    protected void collectModifications(ASTRewriteStore store) {
        IASTNode replacement = null;
        if (originspecifier != null && origindeclarator != null) {
            IASTDeclSpecifier specifierFromAlias = null;
            if (originspecifier instanceof ICPPASTCompositeTypeSpecifier) {
                final IASTName nameForType = ((ICPPASTCompositeTypeSpecifier) originspecifier).getName();
                if (nameForType != null && nameForType.toString().length() > 0) {
                    specifierFromAlias = factory.newTypedefNameSpecifier(nameForType.copy()); // need to see if this copies qualifiers
                }
            } else if (originspecifier instanceof ICPPASTEnumerationSpecifier) {
                IASTName nameForType = ((ICPPASTEnumerationSpecifier) originspecifier).getName();
                if (nameForType != null && nameForType.toString().length() > 0) {
                    specifierFromAlias = factory.newTypedefNameSpecifier(nameForType.copy());
                }
            } else if (originspecifier instanceof ICPPASTNamedTypeSpecifier) {
                ICPPASTNamedTypeSpecifier ntspec = ((ICPPASTNamedTypeSpecifier) originspecifier).copy();
                specifierFromAlias = ntspec;
                IASTName orignalname = ((ICPPASTNamedTypeSpecifier) originspecifier).getName();
                // check if the originalname has a binding in the scope of the alias definition. This means, we need to add the qualifiers of the alias' scope.
                if (aliasTypeIsFromSameScopeAsAliasDefinitionAndReplacementIsQualified(orignalname, theNameOfTheAlias, theNodeToReplace)) {
                    // need to add scope's qualifiers to specifierFromAlias
                    IBinding binding = theNameOfTheAlias.resolveBinding();
                    binding = binding != null ? binding.getOwner() : null;
                    if (binding != null) { // only when alias is actually from a a scope
                        String[] qnameparts = NSNameHelper.getQualifiedName(binding);
                        if (ntspec.getName() instanceof ICPPASTQualifiedName) {
                            ICPPASTQualifiedName oriqname = (ICPPASTQualifiedName) ntspec.getName();
                            ICPPASTQualifiedName qname = factory.newQualifiedName(null);
                            ICPPASTNameSpecifier[] existingqualifiers = oriqname.getQualifier();
                            for (String qn : qnameparts) {
                                qname.addNameSpecifier(factory.newName(qn.toCharArray())); // might be reversed, we will see
                            }
                            for (ICPPASTNameSpecifier qal : existingqualifiers) {
                                qname.addNameSpecifier(qal);
                            }
                            qname.addName(oriqname.getLastName());
                            ntspec.setName(qname);
                        } else {
                            ICPPASTQualifiedName qname = ASTNodeFactory.getDefault().newQualifiedNameNode(qnameparts);
                            qname.addName(ntspec.getName());
                            ntspec.setName(qname);
                        }
                    }
                }

                // the following attempt does break with tempalte-ids and other problems, so just copying what we've got at the place of definition is not too bad.
                // unless... we refer to a local name from a scope.

                //				IASTName nameForType = ((ICPPASTNamedTypeSpecifier) originspecifier).getName();
                //				// use namespactor helpers to construct template ID etc.
                //
                //				// need to locate the name to replace, especially if it was a template id, fortunately namespactor has the infrastructure for it
                //				IBinding binding = nameForType.resolveBinding();
                //				String[] qnameparts = NSNameHelper.getQualifiedName(binding);
                //				ICPPASTQualifiedName qname = ASTNodeFactory.getDefault().newQualifiedNameNode(qnameparts);
                //				specifierFromAlias = factory.newTypedefNameSpecifier(qname);
            } else { // normal case, we just take what we've got at the alias definition
                specifierFromAlias = originspecifier.copy();
            }
            specifierFromAlias.setStorageClass(IASTDeclSpecifier.sc_unspecified); // remove potential typedef
            IASTDeclarator declaratorfromAlias = origindeclarator.copy();
            DeclarationHelper.makeDeclaratorAbstract(declaratorfromAlias, factory); // make abstract
            declaratorfromAlias = DeclarationHelper.optimizeDeclaratorNesting(declaratorfromAlias);
            // switch for different node types. here we could keep track of the
            if (theNodeToReplace.getParent() instanceof CPPASTQualifiedName) {
                // need to re-scope target, if from same scope as alias
                theNodeToReplace = theNodeToReplace.getParent();
                Assert.isTrue(!(theNodeToReplace.getParent() instanceof CPPASTQualifiedName));
            } // if a qualified name, replace the whole name
            if ((theNodeToReplace.getParent() instanceof CPPASTNamedTypeSpecifier)) {
                theNodeToReplace = theNodeToReplace.getParent().getParent();
                if (theNodeToReplace instanceof ICPPASTTypeId) {
                    // create new ICPPASTTypeID from declspecifier, declarator and nest CPPASTDeclarator
                    // too cheap at the moment also needs to take parameters from replaced specifier and potential abstract declarator!
                    replacement = createTypeId((ICPPASTTypeId) theNodeToReplace, specifierFromAlias, declaratorfromAlias);
                } else if (theNodeToReplace instanceof CPPASTSimpleDeclaration) {
                    replacement = createSimpleDeclaration((CPPASTSimpleDeclaration) theNodeToReplace, specifierFromAlias, declaratorfromAlias);
                } else if (theNodeToReplace instanceof CPPASTParameterDeclaration) {
                    replacement = createParameterDeclaration((CPPASTParameterDeclaration) theNodeToReplace, specifierFromAlias, declaratorfromAlias);
                } else if (theNodeToReplace instanceof CPPASTFunctionDefinition) {// also CPPASTFunctionWithTryBlock
                    replacement = createFunctionDefinition((CPPASTFunctionDefinition) theNodeToReplace, specifierFromAlias, declaratorfromAlias);
                } else {
                    // what did i forget...?

                }
            } else {
                // ?? anything else
            }
        }
        if (replacement == null) { // try hard with brain dead and often wrong approach

            // original simple implementation not using correct nodes, but string transform.
            String newTypeName = ASTTypeUtil.getType(theUnderlyingType, false);
            if (theUnderlyingType instanceof ICPPBinding) {
                try {
                    String[] qName = ((ICPPBinding) theUnderlyingType).getQualifiedName();
                    newTypeName = StringUtil.join(qName, "::"); // this loses template ids
                } catch (DOMException e) {}
            }
            replacement = factory.newName(newTypeName.toCharArray());
        }
        if (replacement != null) {
            store.addReplaceChange(theNodeToReplace, replacement);
        }
        super.collectModifications(store);
    }

    private boolean aliasTypeIsFromSameScopeAsAliasDefinitionAndReplacementIsQualified(IASTName orignalname, IASTName theNameOfTheAlias2,
            IASTNode theNodeToReplace2) {
        boolean result = (theNodeToReplace2.getParent() instanceof ICPPASTQualifiedName);
        if (result && orignalname instanceof ICPPASTQualifiedName) {
            ICPPASTNameSpecifier firstname = ((ICPPASTQualifiedName) orignalname).getAllSegments()[0];
            IBinding aliasbinding = theNameOfTheAlias2.resolveBinding();
            IScope aliasscope = null;
            try {
                aliasscope = aliasbinding == null ? null : aliasbinding.getScope();
                result &= aliasscope.getBinding((IASTName) firstname, true) != null;
            } catch (DOMException e) {}
        } else if (result) {
            IBinding aliasbinding = theNameOfTheAlias2.resolveBinding();
            IScope aliasscope = null;
            try {
                aliasscope = aliasbinding == null ? null : aliasbinding.getScope();
                result &= aliasscope.getBinding(orignalname, true) != null;
            } catch (DOMException e) {}

        }
        return result;
    }

    private ICPPASTFunctionDefinition createFunctionDefinition(CPPASTFunctionDefinition toReplace, IASTDeclSpecifier specifierFromAlias,
            IASTDeclarator declaratorFromAlias) {
        IASTDeclSpecifier replacedDeclspec = toReplace.getDeclSpecifier();
        declaratorFromAlias = cloneSpecifierInfo(specifierFromAlias, replacedDeclspec, declaratorFromAlias);
        IASTFunctionDeclarator olddeclar = toReplace.getDeclarator();
        IASTFunctionDeclarator replacementdeclarator = (IASTFunctionDeclarator) createDeclarator(declaratorFromAlias, olddeclar);

        CPPASTFunctionDefinition result = toReplace.copy(CopyStyle.withLocations); // clone to handle subtype as well
        result.setDeclarator(replacementdeclarator);
        result.setDeclSpecifier(specifierFromAlias);
        return result;
    }

    public ICPPASTTypeId createTypeId(ICPPASTTypeId toReplace, IASTDeclSpecifier specifierFromAlias, IASTDeclarator declaratorFromAlias) {
        IASTDeclSpecifier replacedDeclspec = toReplace.getDeclSpecifier();
        declaratorFromAlias = cloneSpecifierInfo(specifierFromAlias, replacedDeclspec, declaratorFromAlias);
        IASTDeclarator olddeclar = toReplace.getAbstractDeclarator();
        return factory.newTypeId(specifierFromAlias, createDeclarator(declaratorFromAlias, olddeclar));
    }

    private ICPPASTParameterDeclaration createParameterDeclaration(CPPASTParameterDeclaration toReplace, IASTDeclSpecifier specifierFromAlias,
            IASTDeclarator declaratorFromAlias) {
        IASTDeclSpecifier replacedDeclspec = toReplace.getDeclSpecifier();
        declaratorFromAlias = cloneSpecifierInfo(specifierFromAlias, replacedDeclspec, declaratorFromAlias);
        ICPPASTDeclarator olddeclar = toReplace.getDeclarator(); // in contrast to a simpleDeclaration there can be only one.
        return factory.newParameterDeclaration(specifierFromAlias, createDeclarator(declaratorFromAlias, olddeclar));
    }

    private IASTSimpleDeclaration createSimpleDeclaration(CPPASTSimpleDeclaration toReplace, IASTDeclSpecifier specifierFromAlias,
            IASTDeclarator declaratorFromAlias) {
        // get all old declarators and use them as subdeclarator of declarator
        // now add flags and storage class from toReplace.getDeclSpecifier
        IASTDeclSpecifier replacedDeclspec = toReplace.getDeclSpecifier();
        declaratorFromAlias = cloneSpecifierInfo(specifierFromAlias, replacedDeclspec, declaratorFromAlias);
        IASTSimpleDeclaration newdeclaration = factory.newSimpleDeclaration(specifierFromAlias);
        IASTDeclarator[] olddeclars = toReplace.getDeclarators();
        if (olddeclars.length > 1) {
            for (IASTDeclarator olddeclar : olddeclars) {
                newdeclaration.addDeclarator(createDeclarator(declaratorFromAlias.copy(), olddeclar));
            }
        } else {
            newdeclaration.addDeclarator(createDeclarator(declaratorFromAlias, olddeclars[0]));
        }
        return newdeclaration;
    }

    private IASTDeclarator cloneSpecifierInfo(IASTDeclSpecifier target, IASTDeclSpecifier origin, IASTDeclarator declarator) {
        declarator = handleConstVolatile(target, origin, declarator);
        if (origin instanceof IASTAttributeOwner && ((IASTAttributeOwner) origin).getAttributeSpecifiers() != null) {
            final ICPPASTAlignmentSpecifier[] allSpecs = Stream.of(((IASTAttributeOwner) origin).getAttributeSpecifiers()).filter(
                    s -> ICPPASTAlignmentSpecifier.class.isInstance(s)).toArray(ICPPASTAlignmentSpecifier[]::new);

            if (target instanceof IASTAttributeOwner) {
                for (ICPPASTAlignmentSpecifier allSpec : allSpecs) {
                    ((IASTAttributeOwner) target).addAttributeSpecifier(allSpec);
                }
            }
        }
        target.setInline(origin.isInline()); // couldn't be already true, because a typedef can not be inline
        target.setRestrict(origin.isRestrict());// could not be already restrict, not really a C++ feature
        target.setStorageClass(origin.getStorageClass());
        if (target instanceof ICPPASTDeclSpecifier && origin instanceof ICPPASTDeclSpecifier) {
            handleCppDeclSpecifierInfo((ICPPASTDeclSpecifier) target, (ICPPASTDeclSpecifier) origin);
        }
        return declarator;
    }

    private void handleCppDeclSpecifierInfo(ICPPASTDeclSpecifier target, ICPPASTDeclSpecifier origin) {

        target.setConstexpr(origin.isConstexpr());
        target.setFriend(origin.isFriend());
        target.setExplicit(origin.isExplicit());
        target.setVirtual(origin.isVirtual());
        target.setThreadLocal(origin.isThreadLocal());
        // should do something about attributes, but need to know how and what
        //origin.getAttributes()
        //origin.getAttributeSpecifiers()
    }

    private IASTDeclarator handleConstVolatile(IASTDeclSpecifier target, IASTDeclSpecifier origin, IASTDeclarator declarator) {
        // if origin has const and/or volatile add it to declarator (inner), if it has pointer operators, or to target
        if (origin.isConst() || origin.isVolatile()) {
            //declarator=DeclarationHelper.optimizeDeclaratorNesting(declarator);
            //declarator.setParent(null);// Attempt to block double nesting....
            IASTDeclarator inner = ASTQueries.findInnermostDeclarator(declarator);
            if (inner.getPointerOperators() != null && inner.getPointerOperators().length > 0) {
                int i = inner.getPointerOperators().length - 1;
                while (i > 0 && (inner.getPointerOperators()[i] == null || inner.getPointerOperators()[i] instanceof ICPPASTReferenceOperator)) {
                    --i;
                }
                IASTPointerOperator po = inner.getPointerOperators()[i];
                if (po instanceof IASTPointer) {
                    if (origin.isConst()) {
                        ((IASTPointer) po).setConst(true);
                    }
                    if (origin.isVolatile()) {
                        ((IASTPointer) po).setVolatile(true);
                    }
                    return declarator;
                } else {
                    //this might be a problem and shouldn't happen, be optimistic and just add it to the specifier
                }
            }
            target.setConst(target.isConst() || origin.isConst());
            target.setVolatile(target.isVolatile() || origin.isVolatile());
        }
        return declarator;
    }

    private IASTDeclarator createDeclarator(IASTDeclarator theNewDeclarator, IASTDeclarator olddeclar) {
        IASTDeclarator retval = theNewDeclarator;
        if (olddeclar != null) {
            retval = olddeclar.copy(CopyStyle.withLocations);
            IASTInitializer initializer = retval.getInitializer();
            retval.setInitializer(null);
            // the following is wrong, if the olddeclar is a function declarator of a function definition or declaration
            //			if (olddeclar.getParent() instanceof ICPPASTFunctionDefinition){
            //				IASTDeclarator inner = ASTQueries.findInnermostDeclarator(theNewDeclarator);
            // copy pointer operators from inner to retval, if any, need to insert them in the front....
            //			} else{
            ASTQueries.findInnermostDeclarator(theNewDeclarator).setNestedDeclarator(retval); // might add one level of parentheses too much, but should be syntactically OK
            //			}
            retval = DeclarationHelper.optimizeDeclaratorNesting(theNewDeclarator);

            if (initializer != null) {
                ASTQueries.findInnermostDeclarator(retval).setInitializer(initializer);
            }
        } else {
            retval = DeclarationHelper.optimizeDeclaratorNesting(retval);
        }
        retval.setParent(null); // avoid problems with setDeclarator implementations
        return retval;
    }

    public void transferDeclaratorExtras(IASTDeclarator target, IASTDeclarator fromtypealias, IASTDeclarator toreplace) {
        for (IASTPointerOperator po : fromtypealias.getPointerOperators()) {
            target.addPointerOperator(po);
        }
        for (IASTPointerOperator po : toreplace.getPointerOperators()) {
            target.addPointerOperator(po.copy(CopyStyle.withLocations));
        }
        IASTDeclarator nested = toreplace.getNestedDeclarator();
        if (fromtypealias.getNestedDeclarator() != null) {
            if (nested != null) {
                fromtypealias.setNestedDeclarator(nested.copy(CopyStyle.withLocations));
            }
            target.setNestedDeclarator(fromtypealias);
        }
    }

}
