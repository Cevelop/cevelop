/******************************************************************************
 * Copyright (c) 2012 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ueli Kunz <kunz@ideadapt.net>, Jules Weder <julesweder@gmail.com> - initial API and implementation
 ******************************************************************************/
package com.cevelop.namespactor.astutil;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTMacroExpansionLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDirective;
import org.eclipse.cdt.internal.ui.refactoring.Container;
import org.eclipse.cdt.internal.ui.refactoring.utils.SelectionHelper;
import org.eclipse.jface.text.Region;


/**
 * @author Ueli Kunz, Jules Weder
 */
@SuppressWarnings("restriction")
public class NSSelectionHelper extends SelectionHelper {

    /**
     * Gets the innermost equivalent <code>ICPPASTQualifiedName</code> of expression by the textSelection. This method is an extended version of
     * <code>SelectionHelper.isSelectionOnExpression(Region textSelection, IASTNode expression)</code> and also uses it.
     *
     * Example for a template: selection is on {@code A::B::S2}, expression is {@code A::B::S1<A::B::S2>}
     * {@code SelectionHelper.isSelectionOnExpression} returns {@code true} because {@code A::B::S2}
     * is inside the qualified name {@code A::B::S1<A::B::S2>}. {@code NSSelectionHelper.getInnerMostSelectedNameInExpression} returns the exact match
     * of the selection: {@code A::B::S2}
     *
     * @param textSelection The {@link Region} selected
     * @param expression The expression as a {@link ICPPASTQualifiedName}
     * @return exact qualified name inside a qualifiedName with templateId
     */
    public static ICPPASTQualifiedName getInnerMostSelectedNameInExpression(final Region textSelection, ICPPASTQualifiedName expression) {
        final Container<ICPPASTQualifiedName> container = new Container<>();
        container.setObject(expression);
        expression.accept(new ASTVisitor() {

            {
                shouldVisitNames = true;
            }

            @Override
            public int visit(IASTName name) {
                if (name instanceof ICPPASTQualifiedName && isSelectionOnExpression(textSelection, name)) {
                    container.setObject((ICPPASTQualifiedName) name);
                }
                return PROCESS_CONTINUE;
            }
        });
        return container.getObject();

    }

    public static Region getNodeSpan(IASTNode region) {
        int start = Integer.MAX_VALUE;
        int nodeLength = 0;
        IASTNodeLocation[] nodeLocations = region.getNodeLocations();
        if (nodeLocations.length != 1) {
            for (IASTNodeLocation location : nodeLocations) {
                if (location instanceof IASTMacroExpansionLocation) {
                    IASTMacroExpansionLocation macroLoc = (IASTMacroExpansionLocation) location;
                    int nodeOffset = macroLoc.asFileLocation().getNodeOffset();
                    if (nodeOffset < start) {
                        start = nodeOffset;
                    }
                    nodeLength += macroLoc.asFileLocation().getNodeLength();
                } else {
                    IASTFileLocation loc = region.getFileLocation();
                    int nodeOffset = loc.getNodeOffset();
                    if (nodeOffset < start) {
                        start = nodeOffset;
                    }
                    nodeLength = loc.getNodeLength();
                }
            }
        } else {
            if (nodeLocations[0] instanceof IASTMacroExpansionLocation) {
                IASTMacroExpansionLocation macroLoc = (IASTMacroExpansionLocation) nodeLocations[0];
                start = macroLoc.asFileLocation().getNodeOffset();
                nodeLength = macroLoc.asFileLocation().getNodeLength();
            } else {
                IASTFileLocation loc = region.getFileLocation();
                start = loc.getNodeOffset();
                nodeLength = loc.getNodeLength();
            }
        }
        return new Region(start, nodeLength);
    }

    public static boolean isSelectionOnExpression(Region textSelection, IASTNode directive) {
        Region exprPos = getNodeSpan(directive);
        int selStart = textSelection.getOffset();
        int selEnd = textSelection.getLength() + selStart;
        return exprPos.getOffset() + exprPos.getLength() >= selStart && exprPos.getOffset() <= selEnd;
    }

    public static ICPPASTUsingDeclaration getSelectedUsingDeclaration(final Region textSelection, IASTTranslationUnit tu) {

        final Container<ICPPASTUsingDeclaration> container = new Container<>();

        tu.accept(new ASTVisitor() {

            {
                shouldVisitDeclarations = true;
            }

            @Override
            public int visit(IASTDeclaration declaration) {
                if (declaration instanceof ICPPASTUsingDeclaration && isSelectionOnExpression(textSelection, declaration)) {
                    container.setObject((ICPPASTUsingDeclaration) declaration);
                }
                return super.visit(declaration);
            }
        });

        return container.getObject();
    }

    public static ICPPASTUsingDirective getSelectedUsingDirective(final Region textSelection, IASTTranslationUnit tu) {

        final Container<ICPPASTUsingDirective> container = new Container<>();

        tu.accept(new ASTVisitor() {

            {
                shouldVisitDeclarations = true;
            }

            @Override
            public int visit(IASTDeclaration declaration) {
                if (declaration instanceof ICPPASTUsingDirective && isSelectionOnExpression(textSelection, declaration)) {
                    container.setObject((ICPPASTUsingDirective) declaration);
                }
                return super.visit(declaration);
            }
        });

        return container.getObject();
    }

    public static IASTName getSelectedName(final Region textSelection, IASTTranslationUnit tu) {

        final Container<IASTName> container = new Container<>();

        tu.accept(new ASTVisitor() {

            {
                shouldVisitNames = true;
            }

            @Override
            public int leave(IASTName name) {
                if (container.getObject() == null && isSelectionOnExpression(textSelection, name)) {
                    container.setObject(name);
                }
                return super.leave(name);
            }

            @Override
            public int visit(IASTName name) {
                return super.visit(name);
            }
        });

        return container.getObject();
    }

    public static boolean isSelectionCandidate(IASTName name) {
        return NSNodeHelper.hasAncestor(name, ICPPASTFunctionCallExpression.class) || NSNodeHelper.hasAncestor(name, IASTIdExpression.class) ||
               NSNodeHelper.hasAncestor(name, ICPPASTParameterDeclaration.class) || NSNodeHelper.hasAncestor(name, ICPPASTBaseSpecifier.class) ||
               NSNodeHelper.hasAncestor(name, ICPPASTNamedTypeSpecifier.class) || NSNodeHelper.hasAncestor(name,
                       ICPPASTCompositeTypeSpecifier.class) || NSNodeHelper.hasAncestor(name, ICPPASTElaboratedTypeSpecifier.class) || name
                               .getParent().getParent() instanceof ICPPASTFunctionDefinition;
    }
}
