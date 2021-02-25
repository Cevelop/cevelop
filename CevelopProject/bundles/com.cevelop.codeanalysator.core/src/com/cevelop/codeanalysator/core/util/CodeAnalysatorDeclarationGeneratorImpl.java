/*******************************************************************************
 * Copyright (c) 2010, 2017 Tomasz Wesolowski
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Tomasz Wesolowski - initial API and implementation
 * Thomas Corbat (IFS)
 *******************************************************************************/
package com.cevelop.codeanalysator.core.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTStandardFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IArrayType;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunctionType;
import org.eclipse.cdt.core.dom.ast.INodeFactory;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IQualifierType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IValue;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTPointerToMember;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTReferenceOperator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPPointerToMemberType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateArgument;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateInstance;
import org.eclipse.cdt.core.dom.rewrite.DeclarationGenerator;
import org.eclipse.cdt.core.parser.util.CharArrayUtils;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor;

import ch.hsr.ifs.iltis.cpp.core.util.constants.CommonCPPConstants;


/**
 * @author Tomasz Wesolowski
 */
public class CodeAnalysatorDeclarationGeneratorImpl extends DeclarationGenerator {

    private INodeFactory factory;

    /**
     * Creates a new generator using the given factory.
     *
     * @param factory
     * The factory to use. If a C++ type is requested, it has to be an instance of
     * {@link ICPPNodeFactory}.
     */
    public CodeAnalysatorDeclarationGeneratorImpl(INodeFactory factory) {
        this.factory = factory;
    }

    @Override
    public IASTDeclSpecifier createDeclSpecFromType(IType type) {
        IASTDeclSpecifier returnedDeclSpec = null;

        if (type instanceof IPointerType) {
            IType inner = ((IPointerType) type).getType();
            returnedDeclSpec = createDeclSpecFromType(inner);
        } else if (type instanceof ICPPReferenceType) {
            IType inner = ((ICPPReferenceType) type).getType();
            returnedDeclSpec = createDeclSpecFromType(inner);
        } else if (type instanceof IArrayType) {
            IType inner = ((IArrayType) type).getType();
            returnedDeclSpec = createDeclSpecFromType(inner);
        } else if (type instanceof IFunctionType) {
            IType inner = ((IFunctionType) type).getReturnType();
            returnedDeclSpec = createDeclSpecFromType(inner);
        } else if (type instanceof IQualifierType) {
            IType inner = ((IQualifierType) type).getType();
            returnedDeclSpec = createDeclSpecFromType(inner);
            if (((IQualifierType) type).isConst()) {
                returnedDeclSpec.setConst(true);
            }
            if (((IQualifierType) type).isVolatile()) {
                returnedDeclSpec.setVolatile(true);
            }
        } else if (type instanceof IBasicType) {
            IBasicType basicType = (IBasicType) type;
            if (basicType.getKind() == Kind.eNullPtr) {
                returnedDeclSpec = createDeclSpecForNullptr();
            } else {
                IASTSimpleDeclSpecifier declSpec = factory.newSimpleDeclSpecifier();
                declSpec.setType(basicType.getKind());
                declSpec.setComplex(basicType.isComplex());
                declSpec.setImaginary(basicType.isImaginary());
                declSpec.setShort(basicType.isShort());
                declSpec.setLong(basicType.isLong());
                declSpec.setLongLong(basicType.isLongLong());
                declSpec.setSigned(basicType.isSigned());
                declSpec.setUnsigned(basicType.isUnsigned());
                returnedDeclSpec = declSpec;
            }
        } else if (type instanceof ICPPTemplateInstance) {
            returnedDeclSpec = getDeclSpecForTemplate((ICPPTemplateInstance) type);
        } else if (type instanceof IBinding) { /* ITypedef, ICompositeType... */
            // BTW - we need to distinguish (and fail explicitly) on literal composites like:
            // struct { } aSingleInstance;
            returnedDeclSpec = getDeclSpecForBinding((IBinding) type);
        }

        // TODO(sprigogin): Be honest and return null instead of void.
        // Fallback...
        if (returnedDeclSpec == null) {
            IASTSimpleDeclSpecifier specifier = factory.newSimpleDeclSpecifier();
            specifier.setType(Kind.eVoid);
            returnedDeclSpec = specifier;
        }
        return returnedDeclSpec;
    }

    @Override
    public IASTDeclarator createDeclaratorFromType(IType type, char[] name) {
        return createDeclaratorFromType(type, name, true);
    }

    public IASTDeclarator createDeclaratorFromType(IType type, char[] name, boolean changeInitialFunctionToFuncPtr) {
        IASTDeclarator returnedDeclarator = null;

        IASTName newName = name != null ? factory.newName(name) : factory.newName();
        try {
            // Addition of pointer operators has to be in reverse order, so it's deferred until
            // the end.
            Map<IASTDeclarator, LinkedList<IASTPointerOperator>> pointerOperatorMap = new HashMap<>();

            // If the type is an array of something, create a declaration of a pointer to something
            // instead (to allow assignment, etc).

            boolean replaceInitialArrayWithPointer = true;

            // If the type is a function, create a declaration of a pointer to this function
            // (shorthand notation for function address).

            boolean changeNextFunctionToFuncPtr = changeInitialFunctionToFuncPtr;

            while (typeNeedsNontrivialDeclarator(type)) {
                if (replaceInitialArrayWithPointer && type instanceof IArrayType) {
                    returnedDeclarator = factory.newDeclarator(newName);
                    returnedDeclarator.addPointerOperator(factory.newPointer());
                    type = ((IArrayType) type).getType();
                } else if (changeNextFunctionToFuncPtr && type instanceof IFunctionType) {
                    returnedDeclarator = factory.newDeclarator(newName);
                    returnedDeclarator.addPointerOperator(factory.newPointer());
                    // Leave type as it is, next iteration will handle the function.
                } else if (type instanceof IArrayType) {
                    IArrayType arrayType = (IArrayType) type;
                    IASTArrayDeclarator arrayDeclarator = factory.newArrayDeclarator(null);
                    if (returnedDeclarator == null) {
                        arrayDeclarator.setName(newName);
                    } else {
                        arrayDeclarator.setNestedDeclarator(returnedDeclarator);
                        arrayDeclarator.setName(factory.newName());
                    }
                    // Consume all immediately following array expressions.
                    while (type instanceof IArrayType) {
                        arrayType = (IArrayType) type;
                        IASTExpression arraySizeExpression = arrayType.getArraySizeExpression();
                        arrayDeclarator.addArrayModifier(factory.newArrayModifier(arraySizeExpression == null ? null : arraySizeExpression.copy(
                                CopyStyle.withLocations)));
                        type = arrayType.getType();
                    }
                    returnedDeclarator = arrayDeclarator;
                } else if (isPtrOrRefType(type)) {
                    if (returnedDeclarator == null) {
                        returnedDeclarator = factory.newDeclarator(newName);
                    }
                    IASTPointerOperator ptrOp = createPointerOperator(type);
                    addPointerOperatorDeferred(pointerOperatorMap, returnedDeclarator, ptrOp);
                    type = getPtrOrRefSubtype(type);
                } else if (type instanceof IFunctionType) {
                    IFunctionType funcType = (IFunctionType) type;
                    IASTStandardFunctionDeclarator func = factory.newFunctionDeclarator(null);
                    for (IType paramType : funcType.getParameterTypes()) {
                        IASTDeclSpecifier declspec = createDeclSpecFromType(paramType);
                        IASTDeclarator declarator = null;
                        if (typeNeedsNontrivialDeclarator(paramType)) {
                            declarator = createDeclaratorFromType(paramType, null);
                        } else {
                            declarator = factory.newDeclarator(factory.newName());
                        }
                        IASTParameterDeclaration parameterDeclaration = factory.newParameterDeclaration(declspec, declarator);
                        func.addParameterDeclaration(parameterDeclaration);
                    }
                    if (returnedDeclarator == null) {
                        func.setName(newName);
                    } else {
                        func.setNestedDeclarator(returnedDeclarator);
                        func.setName(factory.newName());
                    }
                    returnedDeclarator = func;
                    type = funcType.getReturnType();
                }
                replaceInitialArrayWithPointer = false;
                changeNextFunctionToFuncPtr = false;
            }

            finalizePointerOperators(pointerOperatorMap);
        } catch (DOMException e) {
            CCorePlugin.log(e);
        }

        // Fallback
        if (returnedDeclarator == null) {
            returnedDeclarator = factory.newDeclarator(newName);
        }

        return returnedDeclarator;
    }

    private void finalizePointerOperators(Map<IASTDeclarator, LinkedList<IASTPointerOperator>> pointerOperatorMap) {
        for (IASTDeclarator declarator : pointerOperatorMap.keySet()) {
            LinkedList<IASTPointerOperator> list = pointerOperatorMap.get(declarator);
            for (IASTPointerOperator op : list) {
                declarator.addPointerOperator(op);
            }
        }
    }

    private void addPointerOperatorDeferred(Map<IASTDeclarator, LinkedList<IASTPointerOperator>> pointerOperatorMap,
            IASTDeclarator returnedDeclarator, IASTPointerOperator ptrOp) {
        LinkedList<IASTPointerOperator> list;
        if (!pointerOperatorMap.containsKey(returnedDeclarator)) {
            list = new LinkedList<>();
            pointerOperatorMap.put(returnedDeclarator, list);
        } else {
            list = pointerOperatorMap.get(returnedDeclarator);
        }
        list.addFirst(ptrOp);
    }

    private IType getPtrOrRefSubtype(IType type) {
        if (type instanceof IPointerType) {
            return ((IPointerType) type).getType();
        } else {
            return ((ICPPReferenceType) type).getType();
        }
    }

    private IASTPointerOperator createPointerOperator(IType type) {
        if (type instanceof ICPPPointerToMemberType) {
            String classStr = ASTTypeUtil.getType(((ICPPPointerToMemberType) type).getMemberOfClass());
            IASTName newName = factory.newName((classStr + "::").toCharArray()); //$NON-NLS-1$
            // Any better way of getting class name from ICPPPointerToMemberType?

            ICPPASTPointerToMember member = ((ICPPNodeFactory) factory).newPointerToMember(newName);
            member.setConst(((ICPPPointerToMemberType) type).isConst());
            member.setVolatile(((ICPPPointerToMemberType) type).isVolatile());
            return member;
        } else if (type instanceof IPointerType) {
            IASTPointer pointer = factory.newPointer();
            pointer.setConst(((IPointerType) type).isConst());
            pointer.setVolatile(((IPointerType) type).isVolatile());
            return pointer;
        } else {
            ICPPReferenceType refType = (ICPPReferenceType) type;
            ICPPASTReferenceOperator op = ((ICPPNodeFactory) factory).newReferenceOperator(refType.isRValueReference());
            return op;
        }
    }

    private boolean isPtrOrRefType(IType type) {
        return type instanceof IPointerType || type instanceof ICPPReferenceType;
    }

    private boolean typeNeedsNontrivialDeclarator(IType type) {
        return isPtrOrRefType(type) || type instanceof IArrayType || type instanceof IFunctionType;
    }

    private IASTDeclSpecifier createDeclSpecForNullptr() {
        ICPPNodeFactory cppFactory = (ICPPNodeFactory) factory;
        IASTName nullptrName = cppFactory.newQualifiedName(new String[] { "std" }, "nullptr_t");
        return cppFactory.newNamedTypeSpecifier(nullptrName);
    }

    private IASTDeclSpecifier getDeclSpecForTemplate(ICPPTemplateInstance type) {
        IASTName name = getName(type);
        if (factory instanceof ICPPNodeFactory) {
            if (name instanceof ICPPASTQualifiedName) {
                ICPPASTQualifiedName fullQualifiedName = (ICPPASTQualifiedName) name;
                IASTName templateName = fullQualifiedName.getLastName();
                ICPPASTTemplateId tempId = getTemplateId(type, templateName);

                ICPPASTQualifiedName newQualifiedName = ((ICPPNodeFactory) factory).newQualifiedName(tempId);
                ICPPASTNameSpecifier[] qualifier = fullQualifiedName.getQualifier();
                int nbQualifiedNames = qualifier.length;
                for (int i = 0; i < nbQualifiedNames; i++) {
                    newQualifiedName.addNameSpecifier(qualifier[i].copy(CopyStyle.withLocations));
                }

                return factory.newTypedefNameSpecifier(newQualifiedName);
            } else {
                IASTName templateName = getName(type);
                ICPPASTTemplateId tempId = getTemplateId(type, templateName);
                return factory.newTypedefNameSpecifier(tempId);
            }
        }

        return factory.newTypedefNameSpecifier(name);
    }

    private ICPPASTTemplateId getTemplateId(ICPPTemplateInstance type, IASTName templateName) {
        ICPPNodeFactory cppFactory = (ICPPNodeFactory) factory;
        ICPPASTTemplateId tempId = cppFactory.newTemplateId(templateName.copy(CopyStyle.withLocations));
        for (ICPPTemplateArgument arg : type.getTemplateArguments()) {
            addTemplateArgument(tempId, arg);
        }
        return tempId;
    }

    private void addTemplateArgument(ICPPASTTemplateId templateId, ICPPTemplateArgument templateArgument) {
        if (templateArgument.isTypeValue()) {
            IType argumentType = templateArgument.getOriginalTypeValue();
            templateId.addTemplateArgument(getTypeIdForTypeTemplateArgument(argumentType));
        } else {
            IASTExpression expression = getExpressionForValueTypeTemplateArgument(templateArgument);
            if (expression != null) {
                templateId.addTemplateArgument(expression);
            } else {
                /* use type of value as fallback */
                IType argumentValueType = templateArgument.getTypeOfNonTypeValue();
                templateId.addTemplateArgument(getTypeIdForTypeTemplateArgument(argumentValueType));
            }
        }
    }

    IASTTypeId getTypeIdForTypeTemplateArgument(IType argumentType) {
        IASTDeclSpecifier typeSpecifier = createDeclSpecFromType(argumentType);
        IASTDeclarator typeDeclarator = createDeclaratorFromType(argumentType, CharArrayUtils.EMPTY, false);
        return factory.newTypeId(typeSpecifier, typeDeclarator);
    }

    IASTExpression getExpressionForValueTypeTemplateArgument(ICPPTemplateArgument templateArgument) {
        IValue value = templateArgument.getNonTypeValue();
        IType valueType = templateArgument.getTypeOfNonTypeValue();
        if (valueType instanceof IBasicType) {
            IBasicType basicType = (IBasicType) valueType;
            Kind kind = basicType.getKind();
            switch (kind) {
            case eNullPtr:
                return factory.newLiteralExpression(IASTLiteralExpression.lk_nullptr, CommonCPPConstants.NULLPTR);
            case eBoolean:
                if (isTrueValue(value)) {
                    return factory.newLiteralExpression(IASTLiteralExpression.lk_true, "true");
                } else {
                    return factory.newLiteralExpression(IASTLiteralExpression.lk_false, "false");
                }
            case eChar:
                return factory.newLiteralExpression(IASTLiteralExpression.lk_char_constant, getCharacterLiteral("", value));
            case eWChar:
                return factory.newLiteralExpression(IASTLiteralExpression.lk_char_constant, getCharacterLiteral("L", value));
            case eInt:
                return factory.newLiteralExpression(IASTLiteralExpression.lk_integer_constant, getIntegerLiteral(value));
            case eChar16: /* CDT does not support UCS-2 literals */
            case eChar32: /* CDT does not support UCS-4 literals */
            case eInt128: /* CDT does not support __int128 as template parameter */
            default:
                return null;
            }
        } else {
            /*
             * CDT does not support lvalue references, pointer to members, pointers
             * or enumeration values as template parameters
             */
            return null;
        }
    }

    private boolean isTrueValue(IValue value) {
        Number numberValue = value.numberValue();
        if (numberValue == null) {
            return false;
        }
        return numberValue.intValue() != 0;
    }

    private String getCharacterLiteral(String prefix, IValue value) {
        Number numberValue = value.numberValue();
        if (numberValue == null) {
            return "";
        }
        int codePoint = numberValue.intValue();

        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append('\'');
        if (Character.isBmpCodePoint(codePoint)) {
            sb.append((char) codePoint);
        } else {
            sb.append(String.format("\\U%08x", codePoint));
        }
        sb.append('\'');
        return sb.toString();
    }

    private String getIntegerLiteral(IValue value) {
        Number numberValue = value.numberValue();
        if (numberValue == null) {
            return "0";
        }
        return String.valueOf(numberValue.longValue());
    }

    private IASTNamedTypeSpecifier getDeclSpecForBinding(IBinding binding) {
        IASTName name = getName(binding);
        return factory.newTypedefNameSpecifier(name);
    }

    private IASTName getName(IBinding binding) {
        char[][] qualifiedNameCharArray = CPPVisitor.getQualifiedNameCharArray(binding);
        IASTName name;
        if (qualifiedNameCharArray.length > 1) {
            char[] cs = qualifiedNameCharArray[qualifiedNameCharArray.length - 1];
            ICPPNodeFactory cppFactory = (ICPPNodeFactory) factory;
            ICPPASTName segment = cppFactory.newName(cs);
            name = cppFactory.newQualifiedName(segment);
            for (int i = 0; i < qualifiedNameCharArray.length - 1; i++) {
                segment = cppFactory.newName(cs);
                ((ICPPASTQualifiedName) name).addNameSpecifier(cppFactory.newName(qualifiedNameCharArray[i]));
            }
        } else if (qualifiedNameCharArray.length == 1) {
            name = factory.newName(qualifiedNameCharArray[0]);
        } else {
            name = factory.newName(binding.getName().toCharArray());
        }
        return name;
    }
}
