package com.cevelop.codeanalysator.core.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTAttribute;
import org.eclipse.cdt.core.dom.ast.IASTAttributeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTImplicitDestructorName;
import org.eclipse.cdt.core.dom.ast.IASTImplicitName;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTToken;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.c.ICASTDesignator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCapture;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTClassVirtSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDecltypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDesignator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVirtSpecifier;

import ch.hsr.ifs.iltis.core.functional.functions.Function2;


public class VisitorComposite extends ASTVisitor {

    private List<CodeAnalysatorVisitor>                visitors         = new ArrayList<>();
    private List<CodeAnalysatorVisitor>                visitorsToRemove = new ArrayList<>();
    private Map<IASTNode, List<CodeAnalysatorVisitor>> skippedVisitors  = new HashMap<>();
    private VisitorCache                               cache            = new VisitorCache();

    public VisitorComposite() {
        super(true);
    }

    public List<CodeAnalysatorVisitor> getVisitors() {
        return this.visitors;
    }

    public boolean containsVisitors() {
        return !visitors.isEmpty();
    }

    public void add(CodeAnalysatorVisitor visitor) {
        this.visitors.add(visitor);

        if (visitor.shouldVisitAmbiguousNodes) {
            this.shouldVisitAmbiguousNodes = true;
            cache.addCache(IASTTranslationUnit.class, visitor);
        }
        if (visitor.shouldVisitArrayModifiers) {
            this.shouldVisitArrayModifiers = true;
            cache.addCache(IASTArrayModifier.class, visitor);
        }
        if (visitor.shouldVisitAttributes) {
            this.shouldVisitAttributes = true;
            cache.addCache(IASTAttribute.class, visitor);
        }
        if (visitor.shouldVisitBaseSpecifiers) {
            this.shouldVisitBaseSpecifiers = true;
            cache.addCache(ICPPASTBaseSpecifier.class, visitor);
        }
        if (visitor.shouldVisitCaptures) {
            this.shouldVisitCaptures = true;
            cache.addCache(ICPPASTCapture.class, visitor);
        }
        if (visitor.shouldVisitDeclarations) {
            this.shouldVisitDeclarations = true;
            cache.addCache(IASTDeclaration.class, visitor);
        }
        if (visitor.shouldVisitDeclarators) {
            this.shouldVisitDeclarators = true;
            cache.addCache(IASTDeclarator.class, visitor);
        }
        if (visitor.shouldVisitDeclSpecifiers) {
            this.shouldVisitDeclSpecifiers = true;
            cache.addCache(IASTDeclSpecifier.class, visitor);
        }
        if (visitor.shouldVisitDecltypeSpecifiers) {
            this.shouldVisitDecltypeSpecifiers = true;
            cache.addCache(ICPPASTDecltypeSpecifier.class, visitor);
        }
        if (visitor.shouldVisitDesignators) {
            this.shouldVisitDesignators = true;
            cache.addCache(ICPPASTDesignator.class, visitor);
        }
        if (visitor.shouldVisitEnumerators) {
            this.shouldVisitEnumerators = true;
            cache.addCache(IASTEnumerator.class, visitor);
        }
        if (visitor.shouldVisitExpressions) {
            this.shouldVisitExpressions = true;
            cache.addCache(IASTExpression.class, visitor);
        }
        if (visitor.shouldVisitImplicitDestructorNames) {
            this.shouldVisitImplicitDestructorNames = true;
            cache.addCache(IASTImplicitDestructorName.class, visitor);
        }
        if (visitor.shouldVisitImplicitNameAlternates) {
            this.shouldVisitImplicitNameAlternates = true;
            cache.addCache(IASTImplicitName.class, visitor);
        }
        if (visitor.shouldVisitImplicitNames) {
            this.shouldVisitImplicitNames = true;
            cache.addCache(IASTImplicitName.class, visitor);
        }
        if (visitor.shouldVisitInitializers) {
            this.shouldVisitInitializers = true;
            cache.addCache(IASTInitializer.class, visitor);
        }
        if (visitor.shouldVisitNames) {
            this.shouldVisitNames = true;
            cache.addCache(IASTName.class, visitor);
        }
        if (visitor.shouldVisitNamespaces) {
            this.shouldVisitNamespaces = true;
            cache.addCache(ICPPASTNamespaceDefinition.class, visitor);
        }
        if (visitor.shouldVisitParameterDeclarations) {
            this.shouldVisitParameterDeclarations = true;
            cache.addCache(IASTParameterDeclaration.class, visitor);
        }
        if (visitor.shouldVisitPointerOperators) {
            this.shouldVisitPointerOperators = true;
            cache.addCache(IASTPointerOperator.class, visitor);
        }
        if (visitor.shouldVisitProblems) {
            this.shouldVisitProblems = true;
            cache.addCache(IASTProblem.class, visitor);
        }
        if (visitor.shouldVisitStatements) {
            this.shouldVisitStatements = true;
            cache.addCache(IASTStatement.class, visitor);
        }
        if (visitor.shouldVisitTemplateParameters) {
            this.shouldVisitTemplateParameters = true;
            cache.addCache(ICPPASTTemplateParameter.class, visitor);
        }
        if (visitor.shouldVisitTokens) {
            this.shouldVisitTokens = true;
            cache.addCache(IASTToken.class, visitor);
        }
        if (visitor.shouldVisitTranslationUnit) {
            this.shouldVisitTranslationUnit = true;
            cache.addCache(IASTTranslationUnit.class, visitor);
        }
        if (visitor.shouldVisitTypeIds) {
            this.shouldVisitTypeIds = true;
            cache.addCache(IASTTypeId.class, visitor);
        }
        if (visitor.shouldVisitVirtSpecifiers) {
            this.shouldVisitVirtSpecifiers = true;
            cache.addCache(ICPPASTVirtSpecifier.class, visitor);
        }
    }

    private void processResult(int result, IASTNode node, CodeAnalysatorVisitor visitor) {
        if (result == PROCESS_SKIP) {
            this.visitorsToRemove.add(visitor);
            if (this.skippedVisitors.containsKey(node)) {
                this.skippedVisitors.get(node).add(visitor);
            } else {
                List<CodeAnalysatorVisitor> visitors = new ArrayList<>();
                visitors.add(visitor);
                this.skippedVisitors.put(node, visitors);
            }
        } else if (result == PROCESS_ABORT) {
            this.visitorsToRemove.add(visitor);
        }
    }

    private int processLeave(IASTNode node) {
        List<CodeAnalysatorVisitor> skippedVisitors = this.skippedVisitors.get(node);
        if (skippedVisitors == null) {
            return PROCESS_CONTINUE;
        }
        for (CodeAnalysatorVisitor visitor : skippedVisitors) {
            this.visitors.add(visitor);
        }
        return PROCESS_CONTINUE;
    }

    private int getCurrentResult() {
        for (CodeAnalysatorVisitor visitor : this.visitorsToRemove) {
            visitors.remove(visitor);
            cache.remove(visitor);
        }

        if (visitors.isEmpty()) {
            if (skippedVisitors.isEmpty()) {
                return PROCESS_ABORT;
            } else {
                return PROCESS_SKIP;
            }
        }

        return PROCESS_CONTINUE;
    }

    public <Node extends IASTNode> int doVisitForNode(Class<?> nodeClass, Node node, Function2<ASTVisitor, Node, Integer> function) {
        List<CodeAnalysatorVisitor> visitors = cache.getCache(nodeClass);
        for (CodeAnalysatorVisitor visitor : visitors) {
            processResult(function.apply(visitor, node), node, visitor);
        }
        return getCurrentResult();
    }

    @Override
    public int visit(IASTTranslationUnit tu) {
        return doVisitForNode(IASTTranslationUnit.class, tu, ASTVisitor::visit);
    }

    @Override
    public int visit(IASTName name) {
        return doVisitForNode(IASTName.class, name, ASTVisitor::visit);
    }

    @Override
    public int visit(IASTDeclaration declaration) {
        return doVisitForNode(IASTDeclaration.class, declaration, ASTVisitor::visit);
    }

    @Override
    public int visit(IASTInitializer initializer) {
        return doVisitForNode(IASTInitializer.class, initializer, ASTVisitor::visit);
    }

    @Override
    public int visit(IASTParameterDeclaration parameterDeclaration) {
        return doVisitForNode(IASTParameterDeclaration.class, parameterDeclaration, ASTVisitor::visit);
    }

    @Override
    public int visit(IASTDeclarator declarator) {
        return doVisitForNode(IASTDeclarator.class, declarator, ASTVisitor::visit);
    }

    @Override
    public int visit(IASTDeclSpecifier declSpec) {
        return doVisitForNode(IASTDeclSpecifier.class, declSpec, ASTVisitor::visit);
    }

    @Override
    public int visit(IASTArrayModifier arrayModifier) {
        return doVisitForNode(IASTArrayModifier.class, arrayModifier, ASTVisitor::visit);
    }

    @Override
    public int visit(IASTPointerOperator ptrOperator) {
        return doVisitForNode(IASTPointerOperator.class, ptrOperator, ASTVisitor::visit);
    }

    @Override
    public int visit(IASTAttribute attribute) {
        return doVisitForNode(IASTAttribute.class, attribute, ASTVisitor::visit);
    }

    @Override
    public int visit(IASTAttributeSpecifier specifier) {
        return doVisitForNode(IASTAttributeSpecifier.class, specifier, ASTVisitor::visit);
    }

    @Override
    public int visit(IASTToken token) {
        return doVisitForNode(IASTToken.class, token, ASTVisitor::visit);
    }

    @Override
    public int visit(IASTExpression expression) {
        return doVisitForNode(IASTExpression.class, expression, ASTVisitor::visit);
    }

    @Override
    public int visit(IASTStatement statement) {
        return doVisitForNode(IASTStatement.class, statement, ASTVisitor::visit);
    }

    @Override
    public int visit(IASTTypeId typeId) {
        return doVisitForNode(IASTTypeId.class, typeId, ASTVisitor::visit);
    }

    @Override
    public int visit(IASTEnumerator enumerator) {
        return doVisitForNode(IASTEnumerator.class, enumerator, ASTVisitor::visit);
    }

    @Override
    public int visit(IASTProblem problem) {
        return doVisitForNode(IASTProblem.class, problem, ASTVisitor::visit);
    }

    @Override
    public int visit(ICPPASTBaseSpecifier baseSpecifier) {
        return doVisitForNode(ICPPASTBaseSpecifier.class, baseSpecifier, ASTVisitor::visit);
    }

    @Override
    public int visit(ICPPASTNamespaceDefinition namespaceDefinition) {
        return doVisitForNode(ICPPASTNamespaceDefinition.class, namespaceDefinition, ASTVisitor::visit);
    }

    @Override
    public int visit(ICPPASTTemplateParameter templateParameter) {
        return doVisitForNode(ICPPASTTemplateParameter.class, templateParameter, ASTVisitor::visit);
    }

    @Override
    public int visit(ICPPASTCapture capture) {
        return doVisitForNode(ICPPASTCapture.class, capture, ASTVisitor::visit);
    }

    @Override
    public int visit(ICASTDesignator designator) {
        return doVisitForNode(ICASTDesignator.class, designator, ASTVisitor::visit);
    }

    @Override
    public int visit(ICPPASTDesignator designator) {
        return doVisitForNode(ICPPASTDesignator.class, designator, ASTVisitor::visit);
    }

    @Override
    public int visit(ICPPASTVirtSpecifier virtSpecifier) {
        return doVisitForNode(ICPPASTVirtSpecifier.class, virtSpecifier, ASTVisitor::visit);
    }

    @Override
    public int visit(ICPPASTClassVirtSpecifier classVirtSpecifier) {
        return doVisitForNode(ICPPASTClassVirtSpecifier.class, classVirtSpecifier, ASTVisitor::visit);
    }

    @Override
    public int visit(ICPPASTDecltypeSpecifier decltypeSpecifier) {
        return doVisitForNode(ICPPASTDecltypeSpecifier.class, decltypeSpecifier, ASTVisitor::visit);
    }

    @Override
    public int leave(IASTTranslationUnit tu) {
        return processLeave(tu);
    }

    @Override
    public int leave(IASTName name) {
        return processLeave(name);
    }

    @Override
    public int leave(IASTDeclaration declaration) {
        return processLeave(declaration);
    }

    @Override
    public int leave(IASTInitializer initializer) {
        return processLeave(initializer);
    }

    @Override
    public int leave(IASTParameterDeclaration parameterDeclaration) {
        return processLeave(parameterDeclaration);
    }

    @Override
    public int leave(IASTDeclarator declarator) {
        return processLeave(declarator);
    }

    @Override
    public int leave(IASTDeclSpecifier declSpec) {
        return processLeave(declSpec);
    }

    @Override
    public int leave(IASTArrayModifier arrayModifier) {
        return processLeave(arrayModifier);
    }

    @Override
    public int leave(IASTPointerOperator ptrOperator) {
        return processLeave(ptrOperator);
    }

    @Override
    public int leave(IASTAttribute attribute) {
        return processLeave(attribute);
    }

    @Override
    public int leave(IASTAttributeSpecifier specifier) {
        return processLeave(specifier);
    }

    @Override
    public int leave(IASTToken token) {
        return processLeave(token);
    }

    @Override
    public int leave(IASTExpression expression) {
        return processLeave(expression);
    }

    @Override
    public int leave(IASTStatement statement) {
        return processLeave(statement);
    }

    @Override
    public int leave(IASTTypeId typeId) {
        return processLeave(typeId);
    }

    @Override
    public int leave(IASTEnumerator enumerator) {
        return processLeave(enumerator);
    }

    @Override
    public int leave(IASTProblem problem) {
        return processLeave(problem);
    }

    @Override
    public int leave(ICPPASTBaseSpecifier baseSpecifier) {
        return processLeave(baseSpecifier);
    }

    @Override
    public int leave(ICPPASTNamespaceDefinition namespaceDefinition) {
        return processLeave(namespaceDefinition);
    }

    @Override
    public int leave(ICPPASTTemplateParameter templateParameter) {
        return processLeave(templateParameter);
    }

    @Override
    public int leave(ICPPASTCapture capture) {
        return processLeave(capture);
    }

    @Override
    public int leave(ICASTDesignator designator) {
        return processLeave(designator);
    }

    @Override
    public int leave(ICPPASTDesignator designator) {
        return processLeave(designator);
    }

    @Override
    public int leave(ICPPASTVirtSpecifier virtSpecifier) {
        return processLeave(virtSpecifier);
    }

    @Override
    public int leave(ICPPASTClassVirtSpecifier virtSpecifier) {
        return processLeave(virtSpecifier);
    }

    @Override
    public int leave(ICPPASTDecltypeSpecifier decltypeSpecifier) {
        return processLeave(decltypeSpecifier);
    }
}
