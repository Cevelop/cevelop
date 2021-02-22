/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.helpers;

import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeConstructorExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTBaseDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTConstructorInitializer;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTReturnStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleTypeConstructorExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTVisibilityLabel;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPNodeFactory;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringContext;
import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;

import com.cevelop.tdd.Activator;
import com.cevelop.tdd.helpers.visitors.VisibilityLabelFinder;


public class TddHelper {

    public static IASTFunctionDefinition getOuterFunctionDeclaration(IASTTranslationUnit localunit, ITextSelection selection) {
        IASTNode declaratorName = localunit.getNodeSelector(null).findEnclosingName(selection.getOffset(), selection.getLength());
        if (declaratorName == null) {
            declaratorName = localunit.getNodeSelector(null).findEnclosingNodeInExpansion(selection.getOffset(), selection.getLength());
        }
        IASTFunctionDefinition result = TddHelper.getAncestorOfType(declaratorName, IASTFunctionDefinition.class);
        return result;
    }

    public static String extractMissingFunctionName(IMarker marker, IDocument document) throws CoreException {
        int start = marker.getAttribute(IMarker.CHAR_START, 0);
        int end = marker.getAttribute(IMarker.CHAR_END, 0);
        try {
            return document.get(start, end - start);
        } catch (BadLocationException e) {
            Activator.log("Exception while extracting missing function name", e);
            return null;
        }
    }

    public static CPPASTReturnStatement getDefaultReturnValue(CPPASTBaseDeclSpecifier spec) {
        if (TypeVoid(spec)) {
            return null;
        } else if (TypeAuto(spec)) {
            ICPPASTFunctionDefinition function = (ICPPASTFunctionDefinition) spec.getParent();
            ICPPASTFunctionDeclarator declarator = (ICPPASTFunctionDeclarator) function.getDeclarator();
            final ICPPASTParameterDeclaration[] parameters = declarator.getParameters();
            if (parameters.length > 0) {
                ICPPASTParameterDeclaration parameterDeclaration = parameters[0];
                ICPPASTDeclarator parameter = parameterDeclaration.getDeclarator();
                IASTName parameterName = parameter.getName();
                CPPNodeFactory nodeFactory = CPPNodeFactory.getDefault();
                IASTIdExpression idExpression = nodeFactory.newIdExpression(parameterName);
                return (CPPASTReturnStatement) nodeFactory.newReturnStatement(idExpression);
            }
        } else {
            CPPASTReturnStatement returnstmt = new CPPASTReturnStatement();
            ICPPASTDeclSpecifier returndeclspec = spec.copy(CopyStyle.withLocations);
            returndeclspec.setStorageClass(IASTDeclSpecifier.sc_unspecified);
            ICPPASTSimpleTypeConstructorExpression returntype = new CPPASTSimpleTypeConstructorExpression(returndeclspec,
                    new CPPASTConstructorInitializer());
            returnstmt.setReturnValue(returntype);
            return returnstmt;
        }
        return null;
    }

    private static boolean TypeVoid(CPPASTBaseDeclSpecifier spec) {
        return (spec instanceof IASTSimpleDeclSpecifier) && ((IASTSimpleDeclSpecifier) spec).getType() == IASTSimpleDeclSpecifier.t_void;
    }

    private static boolean TypeAuto(CPPASTBaseDeclSpecifier spec) {
        return (spec instanceof IASTSimpleDeclSpecifier) && ((IASTSimpleDeclSpecifier) spec).getType() == IASTSimpleDeclSpecifier.t_auto;
    }

    public static void insertMember(IASTNode member, ICPPASTCompositeTypeSpecifier type, ASTRewrite rewrite) {
        IASTNode publiclabel = findVisibilityLabel(type, ICPPASTVisibilityLabel.v_public);
        if (type.getKey() == IASTCompositeTypeSpecifier.k_struct) {
            if (publiclabel == null) {
                insertBeforeAnyLabel(member, type, rewrite);
            } else {
                insertAtVisibilityLabel(member, type, rewrite, publiclabel);
            }
        } else if (type.getKey() == ICPPASTCompositeTypeSpecifier.k_class) {
            if (publiclabel == null) {
                publiclabel = createAndInsertVisibilityLabel(type, rewrite, ICPPASTVisibilityLabel.v_public);
            }
            insertAtVisibilityLabel(member, type, rewrite, publiclabel);
        }
    }

    private static void insertPrivateMember(IASTDeclaration member, ICPPASTCompositeTypeSpecifier type, ASTRewrite rewrite) {
        IASTNode label = findVisibilityLabel(type, ICPPASTVisibilityLabel.v_private);
        if (type.getKey() == IASTCompositeTypeSpecifier.k_struct) {
            if (label == null) {
                label = createAndInsertVisibilityLabel(type, rewrite, ICPPASTVisibilityLabel.v_private);
            }
            insertAtVisibilityLabel(member, type, rewrite, label);
        } else if (type.getKey() == ICPPASTCompositeTypeSpecifier.k_class) {
            if (label == null) {
                insertBeforeAnyLabel(member, type, rewrite);
            } else {
                insertAtVisibilityLabel(member, type, rewrite, label);
            }
        }
    }

    private static void insertNamespaceMember(IASTNode partToInsert, IASTNode owningType, ASTRewrite rewrite) {
        rewrite.insertBefore(owningType, null, partToInsert, null);
    }

    private static void insertAtVisibilityLabel(IASTNode node, final ICPPASTCompositeTypeSpecifier typespec, ASTRewrite rewrite, IASTNode label) {
        IASTNode insertedNode = node.copy(CopyStyle.withLocations);
        insertedNode.setParent(label);
        IASTNode otherlabel = findDeclarationAfterLabel(typespec, label);
        rewrite.insertBefore(typespec, otherlabel, insertedNode, null);
    }

    private static void insertBeforeAnyLabel(IASTNode function, final ICPPASTCompositeTypeSpecifier typespec, ASTRewrite rewrite) {
        IASTNode firstFunction = null;
        if (typespec.getDeclarations(true).length > 0) {
            firstFunction = typespec.getDeclarations(true)[0];
        }
        rewrite.insertBefore(typespec, firstFunction, function.copy(CopyStyle.withLocations), null);
    }

    // TODO: tcorbat - cannot imagine that this method is correct
    private static IASTNode findDeclarationAfterLabel(ICPPASTCompositeTypeSpecifier typespec, IASTNode label) {
        boolean found = false;
        for (IASTDeclaration dec : typespec.getDeclarations(true)) {
            // 🤔: ??
            if (found) {
                return dec;
            }
            if (dec.equals(label)) {
                found = true;
            }
        }
        return null;
    }

    public static ICPPASTVisibilityLabel createAndInsertVisibilityLabel(final ICPPASTCompositeTypeSpecifier typespec, ASTRewrite rewrite,
            int visibility) {
        ICPPASTVisibilityLabel label = new CPPASTVisibilityLabel(visibility);
        label.setParent(typespec);
        rewrite.insertBefore(typespec, null, label, null);
        return label;
    }

    public static ICPPASTVisibilityLabel findVisibilityLabel(ICPPASTCompositeTypeSpecifier typespec, int visibility) {
        VisibilityLabelFinder labelfinder = new VisibilityLabelFinder(visibility);
        typespec.accept(labelfinder);
        return labelfinder.getFoundLabel();
    }

    public static void showMessageOnStatusLine(String message) {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorPart editor = page.getActiveEditor();
        IActionBars bars = editor.getEditorSite().getActionBars();
        bars.getStatusLineManager().setMessage(message);
        editor.getEditorSite().getActionBarContributor().init(bars, page);
    }

    public static void showErrorOnStatusLine(String message) {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorPart editor = page.getActiveEditor();
        IActionBars bars = editor.getEditorSite().getActionBars();
        bars.getStatusLineManager().setErrorMessage(message);
        editor.getEditorSite().getActionBarContributor().init(bars, page);
    }

    public static boolean nameNotFoundProblem(IProblemBinding problemBinding) {
        return problemBinding.getID() == IProblemBinding.SEMANTIC_NAME_NOT_FOUND;
    }

    public static boolean isInvalidType(IProblemBinding problemBinding) {
        return problemBinding.getID() == IProblemBinding.SEMANTIC_INVALID_TYPE;
    }

    public static boolean isFunctionCall(IASTNode parentNode) {
        if (parentNode instanceof IASTIdExpression) {
            IASTIdExpression expression = (IASTIdExpression) parentNode;
            IASTNode parentParentNode = expression.getParent();
            if (parentParentNode instanceof IASTFunctionCallExpression && expression.getPropertyInParent().getName().equals(
                    IASTFunctionCallExpression.FUNCTION_NAME.getName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMethod(IASTNode node) {
        IASTNode parent = node.getParent();
        IASTNode grand = parent.getParent();
        return (grand instanceof IASTFunctionCallExpression && grand.getChildren()[0] == parent);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getLastOfSameAncestor(IASTNode node, Class<?> T) {
        T lastNode = null;
        while (node != null) {
            if (T.isInstance(node)) {
                lastNode = (T) node;
            } else {
                return lastNode;
            }
            node = node.getParent();
        }
        return lastNode;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getChildofType(IASTNode node, Class<? extends T> c) {
        if (node != null) {
            if (c.isInstance(node)) {
                return (T) node;
            }
            for (IASTNode child : node.getChildren()) {
                T result = getChildofType(child, c);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public static void writeDefinitionTo(ModificationCollector collector, IASTNode owningType, IASTNode partToInsert) {
        if (owningType != null && owningType.getTranslationUnit() != null) {
            ASTRewrite rewrite = collector.rewriterForTranslationUnit(owningType.getTranslationUnit());
            if (owningType instanceof ICPPASTCompositeTypeSpecifier) {
                insertMember(partToInsert, (ICPPASTCompositeTypeSpecifier) owningType, rewrite);
            } else if (owningType instanceof ICPPASTNamespaceDefinition) {
                insertNamespaceMember(partToInsert, owningType, rewrite);
            } else {
                rewrite.insertBefore(owningType, null, partToInsert, null);
            }
        }
    }

    public static void writePrivateDefinitionTo(ModificationCollector collector, ICPPASTCompositeTypeSpecifier type, IASTDeclaration member) {
        ASTRewrite rewrite = collector.rewriterForTranslationUnit(type.getTranslationUnit());
        insertPrivateMember(member, type, rewrite);
    }

    public static IASTNode getNestedInsertionPoint(IASTTranslationUnit localunit, ICPPASTQualifiedName parent, CRefactoringContext context) {
        ICPPASTQualifiedName qname = parent;
        ICPPASTNameSpecifier lastExisting = null;
        for (ICPPASTNameSpecifier n : qname.getAllSegments()) {
            IBinding b = n.resolveBinding();
            if (!(b instanceof IProblemBinding)) {
                lastExisting = n;
            } else {
                break;
            }
        }
        if (lastExisting != null) {
            return TypeHelper.getTypeDefinitonOfName(localunit, new String(lastExisting.resolveBinding().getName()), context);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getTopAncestorOfType(IASTNode node, Class<?> T) {
        T result = null;
        while (node != null) {
            if (T.isInstance(node)) {
                result = (T) node;
            }
            node = node.getParent();
        }
        return result;
    }

    public static boolean hasPointerOrRefType(IASTDeclarator declarator) {
        if (declarator == null) {
            return false;
        }
        if (declarator instanceof IASTArrayDeclarator) {
            return true;
        }
        IBinding declBinding = declarator.getName().resolveBinding();
        if (declBinding instanceof IVariable) {
            IType type = ((IVariable) declBinding).getType();
            return type instanceof ICPPReferenceType || type instanceof IPointerType;
        }

        return false;
    }

    public static boolean isLastPartOfQualifiedName(IASTName name) {
        IASTNode parent = name.getParent();
        if (parent instanceof ICPPASTQualifiedName) {
            ICPPASTQualifiedName qName = (ICPPASTQualifiedName) parent;
            return name == qName.getLastName();
        }
        return true;
    }

    public static boolean hasUnresolvableNameQualifier(IASTName name) {
        IASTNode parent = name.getParent();
        if (parent instanceof ICPPASTQualifiedName) {
            ICPPASTQualifiedName qName = (ICPPASTQualifiedName) parent;
            for (ICPPASTNameSpecifier part : qName.getAllSegments()) {
                if (part != name && part.resolveBinding() instanceof IProblemBinding) {
                    return true;
                }
            }
        }
        return false;
    }

    public static <T> T getAncestorOfType(IASTNode node, Class<? extends T> c) {
        while (node != null) {
            if (c.isInstance(node)) {
                return c.cast(node);
            }
            node = node.getParent();
        }
        return null;
    }
}
