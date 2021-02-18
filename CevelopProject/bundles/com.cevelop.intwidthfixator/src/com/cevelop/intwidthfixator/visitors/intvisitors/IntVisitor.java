package com.cevelop.intwidthfixator.visitors.intvisitors;

import java.util.EnumSet;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTAliasDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCastExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConversionName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNewExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeConstructorExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeId;

import com.cevelop.intwidthfixator.helpers.IdHelper.ProblemId;
import com.cevelop.intwidthfixator.quickfixes.ProblemToLabelMapper;
import com.cevelop.intwidthfixator.refactorings.conversion.ConversionInfo;
import com.cevelop.intwidthfixator.visitors.AbstractVisitor;
import com.cevelop.intwidthfixator.visitors.VisitorArgs;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;
import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.ISimpleReporter;


public class IntVisitor extends AbstractVisitor {

    public IntVisitor(final ISimpleReporter<ProblemId> reporter, final VisitorArgs... args) {
        super(reporter, args);
    }

    /* VISITORS */

    @Override
    public int visit(final IASTExpression expression) {
        if (expression instanceof ICPPASTCastExpression) {
            /* report cast expressions */
            reportIfIntAndNotNull(((ICPPASTCastExpression) expression).getTypeId(), ProblemId.CASTS);
        } else if (expression instanceof ICPPASTSimpleTypeConstructorExpression) {
            /* report variable expressions */
            reportIfIntAndNotNull(((ICPPASTSimpleTypeConstructorExpression) expression).getDeclSpecifier(), ProblemId.VARIABLES);

        } else if (expression instanceof ICPPASTNewExpression) {
            /* report variable expressions */
            reportIfIntAndNotNull(((ICPPASTNewExpression) expression).getTypeId(), ProblemId.VARIABLES);
        }
        return super.visit(expression);
    }

    @Override
    public int visit(final IASTDeclSpecifier declSpec) {
        if (declSpec instanceof IASTSimpleDeclSpecifier) {
            final IASTSimpleDeclSpecifier simpleDeclSpecifier = (IASTSimpleDeclSpecifier) declSpec;
            if (isTypedefDeclaration(simpleDeclSpecifier)) {
                /* report typedef storage class */
                reportIfIntAndNotNull(simpleDeclSpecifier, ProblemId.TYPEDEF);
            }
        }
        return super.visit(declSpec);
    }

    public boolean isTypedefDeclaration(final IASTSimpleDeclSpecifier simpleDeclSpecifier) {
        return simpleDeclSpecifier.getStorageClass() == IASTDeclSpecifier.sc_typedef;
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
            reportIfIntAndNotNull(((ICPPASTAliasDeclaration) declaration).getMappingTypeId().getDeclSpecifier(), ProblemId.TYPEDEF);
        }

        return super.visit(declaration);
    }

    public void reportIfIsVariableDeclaration(final IASTSimpleDeclaration declaration) {
        if (!isFunctionDeclarator(declaration.getDeclarators()) && declaration.getDeclSpecifier() instanceof IASTSimpleDeclSpecifier &&
            !isTypedefDeclaration((IASTSimpleDeclSpecifier) declaration.getDeclSpecifier())) {
            /* report variable declaration */
            reportIfIntAndNotNull(declaration.getDeclSpecifier(), ProblemId.VARIABLES);
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
            reportIfIntAndNotNull(declaration.getDeclSpecifier(), ProblemId.FUNCTION);
        }
    }

    private void reportIfIsFunctionReturnType(final ICPPASTFunctionDefinition definition) {
        if (!isMainFunction(definition.getDeclarator()) && definition.getDeclSpecifier() instanceof IASTDeclSpecifier) {
            /* report function return type */
            reportIfIntAndNotNull(definition.getDeclSpecifier(), ProblemId.FUNCTION);
        }
    }

    private boolean isMainFunction(final IASTFunctionDeclarator declarator) {
        return declarator.getName() != null && declarator.getName().toString().equals("main");
    }

    @Override
    public int visit(final IASTDeclarator declarator) {
        if (declarator instanceof ICPPASTFunctionDeclarator) {
            /* report function trailing return type */
            reportIfIntAndNotNull(((ICPPASTFunctionDeclarator) declarator).getTrailingReturnType(), ProblemId.FUNCTION);
        }
        return super.visit(declarator);
    }

    @Override
    public int visit(final IASTParameterDeclaration parameterDeclaration) {
        if (parameterDeclaration.getDeclSpecifier() instanceof IASTSimpleDeclSpecifier) {
            final IASTNode parent = parameterDeclaration.getParent();
            if (parent instanceof IASTFunctionDeclarator && !isMainFunction((IASTFunctionDeclarator) parent)) {
                /* report function parameter declarations */
                reportIfIntAndNotNull(parameterDeclaration, ProblemId.FUNCTION);
            } else if (parent instanceof ICPPASTTemplateDeclaration) {
                /* report template declarations */
                reportIfIntAndNotNull(parameterDeclaration, ProblemId.TEMPLATE);
            }
        }
        return super.visit(parameterDeclaration);
    }

    @Override
    public int visit(final ICPPASTTemplateParameter templateParam) {
        /* report template parameters */
        templateParam.accept(new SimpleDeclSpecVisitor((declSpec) -> reportIfIntAndNotNull(declSpec, ProblemId.TEMPLATE)));
        return super.visit(templateParam);
    }

    @Override
    public int visit(final IASTName name) {
        if (name instanceof ICPPASTTemplateId) {
            final ICPPASTTemplateId tempId = (ICPPASTTemplateId) name;
            for (final IASTNode argument : tempId.getTemplateArguments()) {
                if (argument instanceof ICPPASTTypeId) {
                    /* Reports template invocations (constructors, functions) */
                    reportIfIntAndNotNull(((ICPPASTTypeId) argument), ProblemId.TEMPLATE);
                }
            }
        } else if (name instanceof ICPPASTConversionName) {
            /* report function operator int */
            reportIfIntAndNotNull(((ICPPASTConversionName) name).getTypeId(), ProblemId.FUNCTION);
        }
        return super.visit(name);
    }

    protected void reportChecked(final ProblemId id, final IASTSimpleDeclSpecifier node) {
        switch (id) {
        case CASTS:
            if (!reportCasts) return;
            break;
        case FUNCTION:
            if (!reportFunctions) return;
            break;
        case TEMPLATE:
            if (!reportTemplates) return;
            break;
        case TYPEDEF:
            if (!reportTypedefs) return;
            break;
        case VARIABLES:
            if (!reportVariables) return;
        }
        reporter.addNodeForReporting(id, node, new ConversionInfo().also(i -> i.mrLabel = ProblemToLabelMapper.getLabel(id)));
    }

    /* HELPERS */

    protected boolean reportIfIntAndNotNull(final IASTDeclSpecifier declSpecifier, final ProblemId id) {
        if (declSpecifier != null && isIntType(declSpecifier)) {
            reportChecked(id, (IASTSimpleDeclSpecifier) declSpecifier);
            return true;
        }
        return false;
    }

    protected boolean reportIfIntAndNotNull(final IASTParameterDeclaration paramDecl, final ProblemId id) {
        if (paramDecl != null) {
            return reportIfIntAndNotNull(paramDecl.getDeclSpecifier(), id);
        }
        return false;
    }

    protected boolean reportIfIntAndNotNull(final ICPPASTSimpleTypeConstructorExpression constructorInit, final ProblemId id) {
        if (constructorInit != null) {
            return reportIfIntAndNotNull(constructorInit.getDeclSpecifier(), id);
        }
        return false;
    }

    protected boolean reportIfIntAndNotNull(final IASTTypeId typeId, final ProblemId id) {
        if (typeId != null) {
            return reportIfIntAndNotNull(typeId.getDeclSpecifier(), id);
        }
        return false;
    }

    protected boolean isIntType(final IASTDeclSpecifier declSpecifier) {
        if (declSpecifier instanceof ICPPASTSimpleDeclSpecifier) {
            final ICPPASTSimpleDeclSpecifier simpleDeclSpec = (ICPPASTSimpleDeclSpecifier) declSpecifier;
            return simpleDeclSpec.getType() == IASTSimpleDeclSpecifier.t_int || simpleDeclSpec.getType() == IASTSimpleDeclSpecifier.t_char ||
                   (simpleDeclSpec.getType() == IASTSimpleDeclSpecifier.t_unspecified && (simpleDeclSpec.isShort() || simpleDeclSpec.isLong() ||
                                                                                          simpleDeclSpec.isLongLong()));
        }
        return false;
    }

    @Override
    public Set<? extends IProblemId<?>> getProblemIds() {
        return EnumSet.of(ProblemId.CASTS, ProblemId.FUNCTION, ProblemId.TEMPLATE, ProblemId.TYPEDEF, ProblemId.VARIABLES);
    }

}
