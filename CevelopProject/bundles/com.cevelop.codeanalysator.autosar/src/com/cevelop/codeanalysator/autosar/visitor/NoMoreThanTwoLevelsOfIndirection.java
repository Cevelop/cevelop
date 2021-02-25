package com.cevelop.codeanalysator.autosar.visitor;

import java.util.Arrays;
import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStandardFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


@SuppressWarnings("restriction")
public class NoMoreThanTwoLevelsOfIndirection extends CodeAnalysatorVisitor {

    public NoMoreThanTwoLevelsOfIndirection(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarations = true;
    }

    @Override
    public int visit(IASTDeclaration declaration) {
        if (declaration instanceof IASTSimpleDeclaration) {
            checkSimpleDeclaration((IASTSimpleDeclaration) declaration);
        } else if (declaration instanceof IASTFunctionDefinition) {
            checkFunctionDefinition((IASTFunctionDefinition) declaration);
        }
        return PROCESS_CONTINUE;
    }

    private void checkSimpleDeclaration(IASTSimpleDeclaration simpleDeclaration) {
        long levelsOfIndirectIndirection = levelsOfIndirectionOverTypeAlias(simpleDeclaration.getDeclSpecifier());
        Arrays.stream(simpleDeclaration.getDeclarators()) //
                .forEach(declarator -> checkDeclarator(declarator, levelsOfIndirectIndirection, IndirectionMode.STANDARD));
    }

    private void checkFunctionDefinition(IASTFunctionDefinition functionDefinition) {
        IASTDeclarator declarator = functionDefinition.getDeclarator();
        if (declarator instanceof IASTStandardFunctionDeclarator) {
            checkFunctionDeclarator((IASTStandardFunctionDeclarator) declarator);
        }
    }

    private void checkDeclarator(IASTDeclarator declarator, long levelsOfIndirectIndirection, IndirectionMode indirectionMode) {
        long levelsOfIndirection = levelsOfIndirectIndirection + levelsOfIndirection(declarator, indirectionMode);
        if (levelsOfIndirection > 2) {
            reportRuleForNode(declarator);
        }
        if (declarator instanceof IASTStandardFunctionDeclarator) {
            checkFunctionDeclarator((IASTStandardFunctionDeclarator) declarator);
        }
    }

    private void checkFunctionDeclarator(IASTStandardFunctionDeclarator functionDeclarator) {
        Arrays.stream(functionDeclarator.getParameters()) //
                .forEach(this::checkParameterDeclaration);
    }

    private void checkParameterDeclaration(IASTParameterDeclaration parameterDeclaration) {
        IASTDeclarator declarator = parameterDeclaration.getDeclarator();
        long levelsOfIndirectIndirection = levelsOfIndirectionOverTypeAlias(parameterDeclaration.getDeclSpecifier());
        checkDeclarator(declarator, levelsOfIndirectIndirection, IndirectionMode.COUNT_ARRAY_AS_INDIRECTION);
    }

    private long levelsOfIndirectionOverTypeAlias(IASTDeclSpecifier declSpecifier) {
        if (declSpecifier instanceof IASTNamedTypeSpecifier) {
            IASTNamedTypeSpecifier namedTypeSpecifier = (IASTNamedTypeSpecifier) declSpecifier;
            IBinding binding = namedTypeSpecifier.getName().resolveBinding();
            if (binding instanceof IType) {
                return levelsOfIndirectionOverTypeAlias((IType) binding);
            }
        }
        return 0;
    }

    private long levelsOfIndirectionOverTypeAlias(IType type) {
        type = SemanticUtil.getNestedType(type, SemanticUtil.TDEF);
        if (type instanceof IPointerType) {
            IPointerType pointerType = (IPointerType) type;
            return 1 + levelsOfIndirectionOverTypeAlias(pointerType.getType());
        } else {
            return 0;
        }
    }

    private long levelsOfIndirection(IASTDeclarator declarator, IndirectionMode indirectionMode) {
        long numberOfPointerOperators = Arrays.stream(declarator.getPointerOperators()) //
                .filter(IASTPointer.class::isInstance) //
                .count();
        if (indirectionMode == IndirectionMode.COUNT_ARRAY_AS_INDIRECTION && declarator instanceof IASTArrayDeclarator) {
            numberOfPointerOperators++;
        }
        numberOfPointerOperators += Optional.ofNullable(declarator.getNestedDeclarator()) //
                .map(nestedDeclarator -> levelsOfIndirection(nestedDeclarator, indirectionMode)) //
                .orElse((long) 0);
        return numberOfPointerOperators;
    }

    private static enum IndirectionMode {
        STANDARD, COUNT_ARRAY_AS_INDIRECTION
    }
}
