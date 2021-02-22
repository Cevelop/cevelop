package com.cevelop.aliextor.ast;

import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTReferenceOperator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeId;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamedTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTPointer;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTReferenceOperator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTypeId;

import com.cevelop.aliextor.ast.exceptions.ASTHelperIsTypeException;
import com.cevelop.aliextor.ast.selection.IRefactorSelection;


@SuppressWarnings("restriction")
public class ASTHelper {

    private ASTHelper() {}

   // @formatter:off
	public enum Type {
		IASTDeclSpecifier,
		IASTDeclaration,
		IASTSimpleDeclaration,
		ICPPASTSimpleDeclSpecifier,
		ICPPASTNamedTypeSpecifier,
		ICPPASTTypeId,
		CPPASTSimpleDeclaration,
		IASTPointer,
		ICPPASTReferenceOperator,
		ICPPASTDeclSpecifier,
		ICPPASTParameterDeclaration,
		ICPPASTTranslationUnit,
		ICPPASTCompoundStatement,
		ICPPASTFunctionDefinition,
		ICPPASTFunctionDeclarator,
		CPPASTName,
		ICPPASTTemplateId,
		ICPPASTName
	}
	// @formatter:on

    public static <T extends IASTNode> boolean isSameIASTDeclSpecifier(T node, T selectedNode) {
        if (ASTHelper.areSameType(node, selectedNode, Type.ICPPASTSimpleDeclSpecifier)) {
            return ASTHelper.hasSameSimpleDeclSpecSignature((ICPPASTSimpleDeclSpecifier) node, (ICPPASTSimpleDeclSpecifier) selectedNode);
        } else if (ASTHelper.areSameType(node, selectedNode, Type.ICPPASTNamedTypeSpecifier)) {
            return hasSameNamedTypedSpecSignature((ICPPASTNamedTypeSpecifier) node, (ICPPASTNamedTypeSpecifier) selectedNode);
        }
        return false;
    }

    private static boolean hasSameSimpleDeclSpecSignature(ICPPASTSimpleDeclSpecifier node, ICPPASTSimpleDeclSpecifier selectedNode) {
        return node.getType() == selectedNode.getType() && node.isVolatile() == selectedNode.isVolatile() && node.isConst() == selectedNode.isConst();
    }

    private static boolean hasSameNamedTypedSpecSignature(ICPPASTNamedTypeSpecifier node, ICPPASTNamedTypeSpecifier selectedNode) {
        return node.getName().resolveBinding().equals(selectedNode.getName().resolveBinding());
    }

    public static <T extends IASTNode> boolean hasSameCPPASTDeclaration(T node, T selectedNode) {
        if (areSameType(node, selectedNode, Type.CPPASTSimpleDeclaration)) {
            return isSameSimpleDeclarationSignature((IASTSimpleDeclaration) node, (IASTSimpleDeclaration) selectedNode);
        } else if (areSameType(node, selectedNode, Type.ICPPASTTypeId)) {
            return areSameTypeId((IASTTypeId) node, (IASTTypeId) selectedNode);
        } else if (areSameType(node, selectedNode, Type.ICPPASTFunctionDeclarator)) {
            return hasSameFunctionDeclarators((ICPPASTFunctionDeclarator) node, (ICPPASTFunctionDeclarator) selectedNode);
        } else {
            return false;
        }
    }

    private static boolean isSameSimpleDeclarationSignature(IASTSimpleDeclaration declaration, IASTSimpleDeclaration selectedNode) {
        return isSameIASTDeclSpecifier(declaration.getDeclSpecifier(), selectedNode.getDeclSpecifier()) ? areSameDeclaratorSignature(declaration
                .getDeclarators(), selectedNode.getDeclarators()) : false;
    }

    public static boolean areSameTypeId(IASTTypeId node, IASTTypeId selectedNode) {
        return isSameIASTDeclSpecifier(node.getDeclSpecifier(), selectedNode.getDeclSpecifier()) && areSameDeclaratorSignature(new IASTDeclarator[] {
                                                                                                                                                      node.getAbstractDeclarator() },
                new IASTDeclarator[] { selectedNode.getAbstractDeclarator() });
    }

    private static boolean hasSameFunctionDeclarators(ICPPASTFunctionDeclarator node, ICPPASTFunctionDeclarator selectedNode) {
        boolean hasSameDeclaration = hasSameCPPASTDeclaration(node.getNestedDeclarator(), selectedNode.getNestedDeclarator());
        if (!hasSameDeclaration) {
            IASTDeclarator nodeDecl = node.getNestedDeclarator();
            IASTPointerOperator nodePtr[] = nodeDecl != null ? nodeDecl.getPointerOperators() : null;
            IASTPointerOperator selectedPtr[] = selectedNode.getPointerOperators();

            if (nodePtr != null && selectedPtr != null) {
                return areSamePointerOperators(nodePtr, selectedPtr);
            } else {
                return false;
            }
        }
        ICPPASTParameterDeclaration[] nodeParameters = node.getParameters();
        ICPPASTParameterDeclaration[] selectedNodeParameters = selectedNode.getParameters();

        if (hasSameDeclaration && nodeParameters.length == selectedNodeParameters.length) {
            for (int i = nodeParameters.length; i < nodeParameters.length; i++) {
                if (areSameType(nodeParameters[i], selectedNodeParameters[i], Type.ICPPASTParameterDeclaration)) {
                    ICPPASTParameterDeclaration newParameterDeclaration = nodeParameters[i];
                    ICPPASTParameterDeclaration newSelectedNode = selectedNodeParameters[i];
                    if (!(isSameIASTDeclSpecifier(newParameterDeclaration.getDeclSpecifier(), newSelectedNode.getDeclSpecifier()) &&
                          hasSameCPPASTDeclaration(newParameterDeclaration, newSelectedNode))) {
                        return false;
                    }
                }
            }
        }

        return false;
    }

    private static boolean areSameDeclaratorSignature(IASTDeclarator declarators1[], IASTDeclarator declarators2[]) {
        if (declarators1.length == declarators2.length) {
            boolean same = true;
            for (int i = 0; i < declarators1.length; i++) {
                if (!areSamePointerOperators(declarators1[i].getPointerOperators(), declarators2[i].getPointerOperators())) {
                    same = false;
                }
            }
            return same;
        } else {
            return false;
        }
    }

    private static boolean areSamePointerOperators(IASTPointerOperator pointOps1[], IASTPointerOperator pointOps2[]) {
        if (pointOps1 == null && pointOps2 == null) {
            return true;
        } else if (pointOps1 != null && pointOps2 != null && pointOps1.length == pointOps2.length) {
            if (pointOps1.length != 0) {
                boolean ok = true;
                for (int i = 0; i < pointOps1.length; i++) {
                    // Last pointerOperator?
                    if (i == pointOps1.length - 1) {
                        if (!isSameRefOperator(pointOps1[i], pointOps2[i]) != areSameType(pointOps1[i], pointOps2[i], Type.IASTPointer)
                                                                                                                                        ? isSamePointer(
                                                                                                                                                pointOps1[i],
                                                                                                                                                pointOps2[i])
                                                                                                                                        : false) {
                            ok = false;
                        }
                    } else {
                        // check if const or volatile
                        if (!isSamePointer(pointOps1[i], pointOps2[i])) {
                            ok = false;
                        }
                    }
                }
                return ok;
            } else {
                return true;
            }
        }
        return false;
    }

    public static boolean isSamePointer(IASTPointerOperator pointer1, IASTPointerOperator pointer2) {
        if (pointer1 instanceof IASTPointer && pointer2 instanceof IASTPointer) {
            return ((IASTPointer) pointer1).isConst() == ((IASTPointer) pointer1).isConst() && ((IASTPointer) pointer1)
                    .isVolatile() == ((IASTPointer) pointer1).isVolatile();
        } else if (pointer1 instanceof ICPPASTReferenceOperator && pointer2 instanceof ICPPASTReferenceOperator) {
            return isSameRefOperator(pointer1, pointer2);
        } else {
            return false;
        }

    }

    private static boolean isSameRefOperator(IASTPointerOperator refOp1, IASTPointerOperator refOp2) {
        return areSameType(refOp1, refOp2, Type.ICPPASTReferenceOperator);
    }

    public static <T extends IASTNode> boolean isSameIASTParamDeclaration(T parameterDeclaration, T selectedNode) {
        if (areSameType(parameterDeclaration, selectedNode, Type.ICPPASTParameterDeclaration)) {
            ICPPASTParameterDeclaration newParameterDeclaration = (ICPPASTParameterDeclaration) parameterDeclaration;
            ICPPASTParameterDeclaration newSelectedNode = (ICPPASTParameterDeclaration) selectedNode;
            return isSameIASTDeclSpecifier(newParameterDeclaration.getDeclSpecifier(), newSelectedNode.getDeclSpecifier()) &&
                   hasSameCPPASTDeclaration(newParameterDeclaration.getDeclarator(), newSelectedNode.getDeclarator());
        }
        return false;
    }

    public static IASTNode findEnclosingScope(IASTNode node) {
        IASTNode enclosingScope = node;
        while (!(enclosingScope instanceof IASTCompositeTypeSpecifier || enclosingScope instanceof IASTCompoundStatement ||
                 enclosingScope instanceof IASTTranslationUnit)) {
            enclosingScope = enclosingScope.getParent();
        }
        return enclosingScope;
    }

    public static IASTNode findEnclosingTranslationUnit(IASTNode node) {
        while (!(node instanceof IASTTranslationUnit)) {
            node = node.getParent();
        }
        return node;
    }

    public static ICPPASTDeclSpecifier getDeclSpecWithoutTypeQualifiersAndStorageClass(ICPPASTDeclSpecifier declSpec) {
        ICPPASTDeclSpecifier copy = declSpec.copy(CopyStyle.withLocations);
        copy.setConst(false);
        copy.setConstexpr(false);
        copy.setExplicit(false);
        copy.setFriend(false);
        copy.setInline(false);
        copy.setRestrict(false);
        copy.setThreadLocal(false);
        copy.setVirtual(false);
        copy.setVolatile(false);
        copy.setStorageClass(IASTDeclSpecifier.sc_unspecified);
        return copy;
    }

    public static boolean selectedNodeHasTemplateId(IRefactorSelection selection) {
        return getNamedTypeSpecifierWithTemplateId(selection) != null;
    }

    public static ICPPASTNamedTypeSpecifier getNamedTypeSpecifierWithTemplateId(IRefactorSelection selection) {
        IASTNode selectedNode = selection.getSelectedNode();
        if (ASTHelper.isType(selectedNode, Type.ICPPASTNamedTypeSpecifier)) {
            // Get Qualified Name
            ICPPASTNamedTypeSpecifier newSelectedNode = (ICPPASTNamedTypeSpecifier) selectedNode;
            IASTNode childrenOfNamedTypeSpec[] = newSelectedNode.getChildren();
            if (childrenOfNamedTypeSpec.length == 1 && childrenOfNamedTypeSpec[0] instanceof ICPPASTQualifiedName) {
                // Get TemplateId
                ICPPASTQualifiedName qualifiedNameOfNamedTypeSpec = (ICPPASTQualifiedName) childrenOfNamedTypeSpec[0];
                IASTNode childrenOfQualifiedName[] = qualifiedNameOfNamedTypeSpec.getChildren();
                for (int i = 1; i < childrenOfQualifiedName.length; i++) {
                    if (childrenOfQualifiedName[i] instanceof ICPPASTTemplateId) {
                        return newSelectedNode;
                    }
                }
            }
        }
        return null;
    }

    public static IASTPointerOperator[] findPointerOperators(IASTDeclSpecifier declSpec) {
        if (declSpec.getParent() instanceof ICPPASTTypeId) {
            return ((ICPPASTTypeId) declSpec.getParent()).getAbstractDeclarator().getPointerOperators();
        } else if (declSpec.getParent() instanceof IASTSimpleDeclaration) {
            return ((IASTSimpleDeclaration) declSpec.getParent()).getDeclarators()[0].getPointerOperators();
        } else if (declSpec.getParent().getParent() instanceof IASTSimpleDeclaration) {
            return ((IASTSimpleDeclaration) declSpec.getParent().getParent()).getDeclarators()[0].getPointerOperators();
        }
        return null;
    }

    private static <T extends IASTNode> boolean areSameType(T node1, T node2, Type type) {
        return isType(node1, type) && isType(node2, type);
    }

    public static <T extends IASTNode> boolean isType(IASTNode node, Type type) {
        switch (type) {
        case IASTDeclSpecifier:
            return node instanceof IASTDeclSpecifier;
        case IASTSimpleDeclaration:
            return node instanceof IASTSimpleDeclaration;
        case ICPPASTSimpleDeclSpecifier:
            return node instanceof CPPASTSimpleDeclSpecifier;
        case ICPPASTNamedTypeSpecifier:
            return node instanceof CPPASTNamedTypeSpecifier;
        case ICPPASTTypeId:
            return node instanceof CPPASTTypeId;
        case CPPASTSimpleDeclaration:
            return node instanceof CPPASTSimpleDeclaration;
        case IASTPointer:
            return node instanceof CPPASTPointer;
        case ICPPASTReferenceOperator:
            return node instanceof CPPASTReferenceOperator;
        case ICPPASTDeclSpecifier:
            return node instanceof ICPPASTDeclSpecifier;
        case ICPPASTParameterDeclaration:
            return node instanceof ICPPASTParameterDeclaration;
        case ICPPASTTranslationUnit:
            return node instanceof ICPPASTTranslationUnit;
        case ICPPASTCompoundStatement:
            return node instanceof ICPPASTCompoundStatement;
        case ICPPASTFunctionDefinition:
            return node instanceof ICPPASTFunctionDefinition;
        case ICPPASTFunctionDeclarator:
            return node instanceof ICPPASTFunctionDeclarator;
        case IASTDeclaration:
            return node instanceof IASTDeclaration;
        case CPPASTName:
            return node instanceof CPPASTName;
        case ICPPASTTemplateId:
            return node instanceof ICPPASTTemplateId;
        case ICPPASTName:
            return node instanceof ICPPASTName;
        default:
            throw new ASTHelperIsTypeException("Walked in default case");
        }
    }

}
