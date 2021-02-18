package com.cevelop.templator.plugin.asttools.resolving;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExplicitTemplateInstantiation;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPAliasTemplate;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPAliasTemplateInstance;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassTemplate;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassTemplatePartialSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFieldTemplate;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPPartialSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPPartiallySpecializable;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateArgument;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateInstance;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameterMap;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPVariableTemplate;
import org.eclipse.cdt.internal.core.dom.parser.ASTInternal;
import org.eclipse.cdt.internal.core.dom.parser.ProblemBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPClassTemplatePartialSpecialization;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPFieldTemplatePartialSpecialization;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPTemplateNonTypeArgument;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPTemplateTypeArgument;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPUnknownClassInstance;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPVariableTemplate;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPVariableTemplatePartialSpecialization;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPDeferredClassInstance;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPInternalBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPInternalClassTemplate;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPUnknownMemberClass;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPSemantics;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPTemplates;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

import com.cevelop.templator.plugin.asttools.data.AbstractResolvedNameInfo;
import com.cevelop.templator.plugin.asttools.resolving.nametype.TypeNameToType;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.plugin.logger.TemplatorLogger;
import com.cevelop.templator.plugin.util.ReflectionMethodHelper;


public final class ClassTemplateResolver {

    private static Method isClassTemplateMethod;
    private static Method addDefaultArgumentsMethod;
    private static Method createAliasTemplaceInstanceMethod;
    private static Method argsAreTrivialMethod;
    private static Method findPartialSpecializationMethod;
    private static Method instantiateMethod;
    private static Method postResolution;
    private static Method createParameterMap;

    private ClassTemplateResolver() {}

    static {
        try {
            isClassTemplateMethod = ReflectionMethodHelper.getNonAccessibleMethod(CPPTemplates.class, "isClassTemplate", ICPPASTTemplateId.class);
            addDefaultArgumentsMethod = ReflectionMethodHelper.getNonAccessibleMethod(CPPTemplates.class, "addDefaultArguments",
                    ICPPTemplateDefinition.class, ICPPTemplateArgument[].class);
            createAliasTemplaceInstanceMethod = ReflectionMethodHelper.getNonAccessibleMethod(CPPTemplates.class, "createAliasTemplateInstance",
                    ICPPAliasTemplate.class, ICPPTemplateArgument[].class, ICPPTemplateParameterMap.class, IType.class, IBinding.class);
            argsAreTrivialMethod = ReflectionMethodHelper.getNonAccessibleMethod(CPPTemplates.class, "argsAreTrivial", ICPPTemplateParameter[].class,
                    ICPPTemplateArgument[].class);
            findPartialSpecializationMethod = ReflectionMethodHelper.getNonAccessibleMethod(CPPTemplates.class, "findPartialSpecialization",
                    ICPPPartiallySpecializable.class, ICPPTemplateArgument[].class);
            instantiateMethod = ReflectionMethodHelper.getNonAccessibleMethod(CPPTemplates.class, "instantiate", ICPPPartiallySpecializable.class,
                    ICPPTemplateArgument[].class, boolean.class, boolean.class);
            createParameterMap = ReflectionMethodHelper.getNonAccessibleMethod(CPPTemplates.class, "createParameterMap", ICPPTemplateDefinition.class,
                    ICPPTemplateArgument[].class);
            postResolution = ReflectionMethodHelper.getNonAccessibleMethod(CPPSemantics.class, "postResolution", IBinding.class, IASTName.class);
        } catch (final Exception e) {
            TemplatorLogger.errorDialogWithStackTrace("Class Templates that depend on other template arguments cannot be deduced.",
                    "Methods in CPPTemplates are called via reflection to resolve class templates and their definition changed.", e);
        }

    }

    public static IBinding instantiateClassTemplate(final ICPPASTTemplateId id, final AbstractResolvedNameInfo parent) throws TemplatorException {
        IBinding instantiatedClassTemplate = null;
        CPPSemantics.pushLookupPoint(id);
        try {
            final Boolean isClassTemplate = ReflectionMethodHelper.<Boolean>invokeStaticMethod(isClassTemplateMethod, id);
            if (!isClassTemplate) {
                // Functions are instantiated as part of the resolution process.
                final IBinding result = CPPVisitor.createBinding(id);
                final IASTName templateName = id.getTemplateName();
                if (result instanceof ICPPClassTemplate || result instanceof ICPPAliasTemplate || result instanceof ICPPVariableTemplate) {
                    templateName.setBinding(result);
                    id.setBinding(null);
                } else {
                    if (result instanceof ICPPTemplateInstance) {
                        templateName.setBinding(((ICPPTemplateInstance) result).getTemplateDefinition());
                    } else {
                        templateName.setBinding(result);
                    }
                    // do not return the variable template, continue with instantiating it
                    if (!(templateName.getBinding() instanceof ICPPVariableTemplate)) {
                        return result;
                    }
                }
            }

            IASTNode parentOfName = id.getParent();
            boolean isLastName = true;
            if (parentOfName instanceof ICPPASTQualifiedName) {
                isLastName = ((ICPPASTQualifiedName) parentOfName).getLastName() == id;
                parentOfName = parentOfName.getParent();
            }

            boolean isDeclaration = false;
            boolean isDefinition = false;
            boolean isExplicitSpecialization = false;
            if (isLastName && parentOfName != null) {
                final IASTNode declaration = parentOfName.getParent();
                if (declaration instanceof IASTSimpleDeclaration) {
                    if (parentOfName instanceof ICPPASTElaboratedTypeSpecifier) {
                        isDeclaration = true;
                    } else if (parentOfName instanceof ICPPASTCompositeTypeSpecifier) {
                        isDefinition = true;
                    }
                    if (isDeclaration || isDefinition) {
                        final IASTNode parentOfDeclaration = declaration.getParent();
                        if (parentOfDeclaration instanceof ICPPASTExplicitTemplateInstantiation) {
                            isDeclaration = false;
                        } else if (parentOfDeclaration instanceof ICPPASTTemplateSpecialization) {
                            isExplicitSpecialization = true;
                        }
                    }
                }
            }

            IBinding result = null;
            final IASTName templateName = id.getTemplateName();
            IBinding template = templateName.resolvePreBinding();

            template = extractTemplateTemplateParameter(parent, template);

            // Alias template.
            if (template instanceof ICPPAliasTemplate) {
                final ICPPAliasTemplate aliasTemplate = (ICPPAliasTemplate) template;
                ICPPTemplateArgument[] args = CPPTemplates.createTemplateArgumentArray(id);
                replaceDependentTemplateArguments(id, parent, args);
                args = addDefaultArguments(aliasTemplate, args);
                if (args == null) {
                    return new ProblemBinding(id, IProblemBinding.SEMANTIC_INVALID_TEMPLATE_ARGUMENTS, templateName.toCharArray());
                }
                final ICPPTemplateParameterMap parameterMap = ReflectionMethodHelper.<ICPPTemplateParameterMap>invokeStaticMethod(createParameterMap,
                        aliasTemplate, args);
                final IType aliasedType = aliasTemplate.getType();
                final IBinding owner = template.getOwner();

                return ReflectionMethodHelper.<IBinding>invokeStaticMethod(createAliasTemplaceInstanceMethod, aliasTemplate, args, parameterMap,
                        aliasedType, owner);
            }

            // Alias template instance.
            if (template instanceof ICPPAliasTemplateInstance) {
                final ICPPAliasTemplateInstance aliasTemplateInstance = (ICPPAliasTemplateInstance) template;
                ICPPTemplateArgument[] args = CPPTemplates.createTemplateArgumentArray(id);
                final ICPPAliasTemplate aliasTemplate = aliasTemplateInstance.getTemplateDefinition();
                replaceDependentTemplateArguments(id, parent, args);
                args = addDefaultArguments(aliasTemplate, args);
                if (args == null) {
                    return new ProblemBinding(id, IProblemBinding.SEMANTIC_INVALID_TEMPLATE_ARGUMENTS, templateName.toCharArray());
                }
                final ICPPTemplateParameterMap parameterMap = ReflectionMethodHelper.<ICPPTemplateParameterMap>invokeStaticMethod(createParameterMap,
                        aliasTemplate, args);

                final IType aliasedType = aliasTemplateInstance.getType();
                final IBinding owner = aliasTemplateInstance.getOwner();
                return ReflectionMethodHelper.<IBinding>invokeStaticMethod(createAliasTemplaceInstanceMethod, aliasTemplate, args, parameterMap,
                        aliasedType, owner, id);
            }

            // Class or variable template.
            if (template instanceof ICPPConstructor) {
                template = template.getOwner();
            }

            if (template instanceof ICPPUnknownMemberClass) {
                final IType owner = ((ICPPUnknownMemberClass) template).getOwnerType();
                ICPPTemplateArgument[] args = CPPTemplates.createTemplateArgumentArray(id);
                args = SemanticUtil.getSimplifiedArguments(args);
                return new CPPUnknownClassInstance(owner, id.getSimpleID(), args);
            }

            if (!(template instanceof ICPPPartiallySpecializable) || template instanceof ICPPClassTemplatePartialSpecialization)
                return new ProblemBinding(id, IProblemBinding.SEMANTIC_INVALID_TYPE, templateName.toCharArray());

            final ICPPPartiallySpecializable classTemplate = (ICPPPartiallySpecializable) template;
            ICPPTemplateArgument[] args = CPPTemplates.createTemplateArgumentArray(id);
            if (CPPTemplates.hasDependentArgument(args)) {
                ICPPASTTemplateDeclaration tdecl = CPPTemplates.getTemplateDeclaration(id);
                if (tdecl != null) {
                    final Boolean argsAreTrivial = ReflectionMethodHelper.<Boolean>invokeStaticMethod(argsAreTrivialMethod, classTemplate
                            .getTemplateParameters(), args);
                    if (argsAreTrivial) {
                        result = classTemplate;
                    } else {
                        args = addDefaultArguments(classTemplate, args);
                        if (args == null) {
                            return new ProblemBinding(id, IProblemBinding.SEMANTIC_INVALID_TEMPLATE_ARGUMENTS, templateName.toCharArray());
                        }
                        ICPPPartialSpecialization partialSpec = ReflectionMethodHelper.<ICPPPartialSpecialization>invokeStaticMethod(
                                findPartialSpecializationMethod, classTemplate, args);
                        ICPPClassTemplatePartialSpecialization indexSpec = null;
                        if ((isDeclaration || isDefinition) && (partialSpec instanceof ICPPClassTemplatePartialSpecialization)) {
                            indexSpec = (ICPPClassTemplatePartialSpecialization) partialSpec;
                            partialSpec = null;
                        }
                        if (partialSpec == null) {
                            if (isDeclaration || isDefinition) {
                                if (template instanceof ICPPClassTemplate) {
                                    partialSpec = new CPPClassTemplatePartialSpecialization(id, args);
                                    if (indexSpec != null) {
                                        SemanticUtil.recordPartialSpecialization(indexSpec, (ICPPClassTemplatePartialSpecialization) partialSpec, id);
                                    } else if (template instanceof ICPPInternalClassTemplate) {
                                        ((ICPPInternalClassTemplate) template).addPartialSpecialization(
                                                (ICPPClassTemplatePartialSpecialization) partialSpec);
                                    }
                                } else if (template instanceof ICPPVariableTemplate) {
                                    if (template instanceof ICPPFieldTemplate) {
                                        partialSpec = new CPPFieldTemplatePartialSpecialization(id, args);
                                    } else {
                                        partialSpec = new CPPVariableTemplatePartialSpecialization(id, args);
                                    }
                                    if (template instanceof CPPVariableTemplate) ((CPPVariableTemplate) template).addPartialSpecialization(
                                            partialSpec);
                                }
                                return partialSpec;
                            }
                            return new ProblemBinding(id, IProblemBinding.SEMANTIC_INVALID_TYPE, templateName.toCharArray());
                        }
                        result = partialSpec;
                    }
                } else {
                    replaceDependentTemplateArguments(id, parent, args);
                }
            }
            if (result == null) {
                result = ReflectionMethodHelper.<IBinding>invokeStaticMethod(instantiateMethod, classTemplate, args, isDefinition,
                        isExplicitSpecialization);
                if (result instanceof ICPPInternalBinding) {
                    if (isDeclaration) {
                        ASTInternal.addDeclaration(result, id);
                    } else if (isDefinition) {
                        ASTInternal.addDefinition(result, id);
                    }
                }
            }
            instantiatedClassTemplate = ReflectionMethodHelper.<IBinding>invokeStaticMethod(postResolution, result, id);
        } catch (DOMException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassCastException e) {
            return null;
        } finally {
            CPPSemantics.popLookupPoint();
        }
        return instantiatedClassTemplate;
    }

    private static IBinding extractTemplateTemplateParameter(final AbstractResolvedNameInfo parent, IBinding template) {
        if (template instanceof ICPPTemplateTemplateParameter) {
            ICPPTemplateTemplateParameter templateTemplateParam = (ICPPTemplateTemplateParameter) template;
            ICPPTemplateArgument arg = parent.getArgument(templateTemplateParam);
            IType type = arg.getTypeValue();
            if (type instanceof ICPPClassTemplate || type instanceof ICPPAliasTemplate) {
                template = (IBinding) type;
            }
        }
        return template;
    }

    private static void replaceDependentTemplateArguments(final ICPPASTTemplateId id, final AbstractResolvedNameInfo parent,
            final ICPPTemplateArgument[] args) throws TemplatorException {
        for (int i = 0; i < args.length; i++) {
            final ICPPTemplateArgument arg = args[i];
            /* final */ IType typeValue = arg.getTypeValue();
            //
            //			if (typeValue instanceof ICPPParameterPackType) {
            //				ICPPParameterPackType packType = (ICPPParameterPackType) typeValue;
            //				typeValue = packType.getType();
            //			}
            //
            if (arg.isNonTypeValue()) {
                args[i] = TemplateNonTypeArgumentResolver.getEvaluatedArgument((CPPTemplateNonTypeArgument) arg, parent.getTemplateArgumentMap());
            } else if (typeValue instanceof ICPPTemplateParameter) {
                args[i] = parent.getArgument((ICPPTemplateParameter) typeValue);
            } else if (typeValue instanceof ICPPDeferredClassInstance || typeValue instanceof ICPPUnknownMemberClass) {
                final IASTNode templateArgument = id.getTemplateArguments()[i];
                if (templateArgument instanceof IASTTypeId) {
                    final IASTDeclSpecifier declSpecifier = ((IASTTypeId) templateArgument).getDeclSpecifier();
                    if (declSpecifier instanceof IASTNamedTypeSpecifier) {
                        final IASTName typeName = ((IASTNamedTypeSpecifier) declSpecifier).getName();
                        if (typeName instanceof ICPPASTTemplateId) {
                            final IBinding nestedInstance = instantiateClassTemplate((ICPPASTTemplateId) typeName, parent);
                            if (nestedInstance instanceof IType) {
                                args[i] = new CPPTemplateTypeArgument((IType) nestedInstance);
                            }
                        } else {
                            final TypeNameToType nestedType = parent.getAnalyzer().getType(typeName, parent);
                            if (nestedType == null || nestedType.getType() == null) {
                                throw new TemplatorException("Could not determine argument for " + args[i] + " in " + id + ".");
                            }
                            args[i] = new CPPTemplateTypeArgument(nestedType.getType());
                        }
                    }
                }
            }
        }
    }

    public static ICPPTemplateArgument[] addDefaultArguments(final ICPPTemplateDefinition template, final ICPPTemplateArgument[] arguments)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassCastException {
        return ReflectionMethodHelper.<ICPPTemplateArgument[]>invokeStaticMethod(addDefaultArgumentsMethod, template, arguments);
    }
}
