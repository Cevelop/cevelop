package com.cevelop.intwidthfixator.visitors.cstdintvisitors;

import java.util.EnumSet;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTAliasDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCastExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConversionName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNewExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeConstructorExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBasicType;

import com.cevelop.intwidthfixator.helpers.IdHelper.ProblemId;
import com.cevelop.intwidthfixator.helpers.InversionHelper;
import com.cevelop.intwidthfixator.visitors.AbstractVisitor;
import com.cevelop.intwidthfixator.visitors.VisitorArgs;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;
import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.ISimpleReporter;


public class CstdintVisitor extends AbstractVisitor {

    public CstdintVisitor(final ISimpleReporter<ProblemId> reporter, final VisitorArgs... args) {
        super(reporter, args);
    }

    /* VISITORS */

    @Override
    public int visit(final IASTExpression expression) {
        if (expression instanceof ICPPASTCastExpression) {
            /* report cast expressions */
            reportIfCstdintAndNotNull(((ICPPASTCastExpression) expression).getTypeId(), ProblemId.CASTS);
        } else if (expression instanceof ICPPASTSimpleTypeConstructorExpression) {
            /* report variable expressions */
            reportIfCstdintAndNotNull(((ICPPASTSimpleTypeConstructorExpression) expression).getDeclSpecifier(), ProblemId.VARIABLES);
        } else if (expression instanceof ICPPASTNewExpression) {
            /* report variable expressions */
            reportIfCstdintAndNotNull(((ICPPASTNewExpression) expression).getTypeId(), ProblemId.VARIABLES);
        } else if (expression instanceof ICPPASTFunctionCallExpression) {
            /* report function-style cast expression */
            reportIfCstdintAndNotNull(((ICPPASTFunctionCallExpression) expression).getFunctionNameExpression(), ProblemId.CASTS);
        }
        return super.visit(expression);
    }

    @Override
    public int visit(final IASTDeclSpecifier declSpec) {
        if (declSpec instanceof IASTNamedTypeSpecifier) {
            final IASTNamedTypeSpecifier namedTypeSpecifier = (IASTNamedTypeSpecifier) declSpec;
            if (isTypedefDeclaration(namedTypeSpecifier)) {
                /* report typedef storage class */
                reportIfCstdintAndNotNull(namedTypeSpecifier, ProblemId.TYPEDEF);
            }
        }
        return super.visit(declSpec);
    }

    public boolean isTypedefDeclaration(final IASTNamedTypeSpecifier namedTypeSpecifier) {
        return namedTypeSpecifier.getStorageClass() == IASTDeclSpecifier.sc_typedef;
    }

    @Override
    public int visit(final IASTDeclaration declaration) {
        if (declaration instanceof IASTSimpleDeclaration) {
            /* report variable declarations */
            reportIfIsVariableDeclaration((IASTSimpleDeclaration) declaration);
            /* report function declaration return type */
            reportIfIsFunctionDeclaration((IASTSimpleDeclaration) declaration);

        } else if (declaration instanceof ICPPASTFunctionDefinition) {
            /* report function return type */
            reportIfIsFunctionReturnType((ICPPASTFunctionDefinition) declaration);

        } else if (declaration instanceof ICPPASTAliasDeclaration) {
            /* report using declarations */
            reportIfCstdintAndNotNull(((ICPPASTAliasDeclaration) declaration).getMappingTypeId().getDeclSpecifier(), ProblemId.TYPEDEF);
        }

        return super.visit(declaration);
    }

    public void reportIfIsVariableDeclaration(final IASTSimpleDeclaration declaration) {
        if (!isFunctionDeclarator(declaration.getDeclarators()) && declaration.getDeclSpecifier() instanceof IASTNamedTypeSpecifier &&
            !isTypedefDeclaration((IASTNamedTypeSpecifier) declaration.getDeclSpecifier())) {
            /* report variable declaration */
            reportIfCstdintAndNotNull(declaration.getDeclSpecifier(), ProblemId.VARIABLES);
        }
    }

    private boolean isFunctionDeclarator(final IASTDeclarator[] declarators) {
        for (final IASTDeclarator declarator : declarators) {
            if (isFunctionDeclarator(declarator)) {
                return true;
            }
        }
        return false;
    }

    private boolean isFunctionDeclarator(final IASTDeclarator declarator) {
        if (declarator instanceof IASTFunctionDeclarator) {
            return true;
        }

        if (declarator.getNestedDeclarator() != null && isFunctionDeclarator(declarator.getNestedDeclarator())) {
            return true;
        }

        return false;
    }

    private void reportIfIsFunctionDeclaration(final IASTSimpleDeclaration declaration) {
        if (isFunctionDeclarator(declaration.getDeclarators())) {
            /* report function declaration return type */
            reportIfCstdintAndNotNull(declaration.getDeclSpecifier(), ProblemId.FUNCTION);
        }
    }

    private void reportIfIsFunctionReturnType(final ICPPASTFunctionDefinition definition) {
        if (!isMainFunction(definition.getDeclarator()) && definition.getDeclSpecifier() instanceof IASTDeclSpecifier) {
            /* report function return type */
            reportIfCstdintAndNotNull(definition.getDeclSpecifier(), ProblemId.FUNCTION);
        }
    }

    private boolean isMainFunction(final IASTFunctionDeclarator declarator) {
        return declarator.getName() != null && declarator.getName().toString().equals("main");
    }

    @Override
    public int visit(final IASTDeclarator declarator) {
        if (declarator instanceof ICPPASTFunctionDeclarator) {
            /* report function trailing return type */
            reportIfCstdintAndNotNull(((ICPPASTFunctionDeclarator) declarator).getTrailingReturnType(), ProblemId.FUNCTION);
        }
        return super.visit(declarator);
    }

    @Override
    public int visit(final IASTParameterDeclaration parameterDeclaration) {
        if (parameterDeclaration.getDeclSpecifier() instanceof ICPPASTNamedTypeSpecifier) {
            final IASTNode parent = parameterDeclaration.getParent();
            if (parent instanceof IASTFunctionDeclarator && !isMainFunction((IASTFunctionDeclarator) parent)) {
                /* report function parameter declarations */
                reportIfCstdintAndNotNull(parameterDeclaration, ProblemId.FUNCTION);
            } else if (parent instanceof ICPPASTTemplateDeclaration) {
                /* report template declarations */
                reportIfCstdintAndNotNull(parameterDeclaration, ProblemId.TEMPLATE);
            }
        }
        return super.visit(parameterDeclaration);
    }

    @Override
    public int visit(final ICPPASTTemplateParameter templateParam) {
        /* report template parameters */
        templateParam.accept(new NamedTypeSpecVisitor((declSpec) -> reportIfCstdintAndNotNull(declSpec, ProblemId.TEMPLATE)));
        return super.visit(templateParam);
    }

    @Override
    public int visit(final IASTName name) {
        if (name instanceof ICPPASTTemplateId) {
            final ICPPASTTemplateId tempId = (ICPPASTTemplateId) name;
            for (final IASTNode argument : tempId.getTemplateArguments()) {
                if (argument instanceof ICPPASTTypeId) {
                    /* Reports template invocations (constructors, functions) */
                    reportIfCstdintAndNotNull(((ICPPASTTypeId) argument), ProblemId.TEMPLATE);
                }
            }
        } else if (name instanceof ICPPASTConversionName) {
            /* report function operator int */
            reportIfCstdintAndNotNull(((ICPPASTConversionName) name).getTypeId(), ProblemId.FUNCTION);
        }
        return super.visit(name);
    }

    /* HELPERS */

    protected void reportIfCstdintAndNotNull(final ICPPASTNamedTypeSpecifier node, final ProblemId id) {
        if (node != null) {
            if (isCstdintType(node.getName())) {
                reporter.addNodeForReporting(id, node);
            }
        }
    }

    protected void reportIfCstdintAndNotNull(final ICPPASTName node, final ProblemId id) {
        if (node != null) {
            if (isCstdintType(node)) {
                reporter.addNodeForReporting(id, node);
            }
        }
    }

    protected void reportIfCstdintAndNotNull(final IASTTypeId node, final ProblemId id) {
        if (node != null && node.getDeclSpecifier() instanceof ICPPASTNamedTypeSpecifier) {
            reportIfCstdintAndNotNull((ICPPASTNamedTypeSpecifier) node.getDeclSpecifier(), id);
        }
    }

    protected void reportIfCstdintAndNotNull(final IASTDeclSpecifier node, final ProblemId id) {
        if (node != null && node instanceof ICPPASTNamedTypeSpecifier) {
            reportIfCstdintAndNotNull((ICPPASTNamedTypeSpecifier) node, id);
        }
    }

    protected void reportIfCstdintAndNotNull(final IASTParameterDeclaration node, final ProblemId id) {
        if (node != null && node instanceof ICPPASTParameterDeclaration) {
            reportIfCstdintAndNotNull(node.getDeclSpecifier(), id);
        }
    }

    protected void reportIfCstdintAndNotNull(final IASTExpression node, final ProblemId id) {
        if (node != null && node instanceof IASTIdExpression) {
            if (((IASTIdExpression) node).getName() instanceof ICPPASTName) {
                reportIfCstdintAndNotNull((ICPPASTName) ((IASTIdExpression) node).getName(), id);
            }
        }
    }

    protected boolean isCstdintType(final IASTName name) {
        if (name instanceof ICPPASTQualifiedName) {
            final ICPPASTQualifiedName qualifiedName = (ICPPASTQualifiedName) name;
            final ICPPASTNameSpecifier[] qualifiers = qualifiedName.getQualifier();
            if (qualifiers.length == 1) {
                return InversionHelper.isCstdint(qualifiedName.toString());
            }
        } else {
            final ICPPASTName cppName = (ICPPASTName) name;
            final IBinding binding = cppName.resolveBinding();
            if (binding instanceof ITypedef) {
                final IType type = ((ITypedef) binding).getType();
                if (type instanceof ICPPBasicType) {
                    final ICPPBasicType basicType = (ICPPBasicType) type;
                    final Kind kind = basicType.getKind();
                    return ((kind == Kind.eChar || kind == Kind.eInt) && InversionHelper.isCstdint(name));
                }
            }
        }
        return false;
    }

    @Override
    public Set<? extends IProblemId<?>> getProblemIds() {
        return EnumSet.of(ProblemId.CASTS, ProblemId.FUNCTION, ProblemId.TEMPLATE, ProblemId.TYPEDEF, ProblemId.VARIABLES);
    }

}
