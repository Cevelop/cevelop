package com.cevelop.constificator.core.deciders.localvariables;

import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPVariable;

import com.cevelop.constificator.core.deciders.common.NonPointer;
import com.cevelop.constificator.core.deciders.decision.Decision;
import com.cevelop.constificator.core.deciders.decision.NullDecision;
import com.cevelop.constificator.core.util.ast.ASTRewriteCache;
import com.cevelop.constificator.core.util.structural.Relation;
import com.cevelop.constificator.core.util.type.Cast;


@SuppressWarnings("restriction")
public class NonPointerVariable {

    public static Decision canConstify(ICPPASTDeclarator declarator, ASTRewriteCache cache, Class<? extends Decision> decisionType) {
        if (declarator == null || !Relation.isDescendendOf(IASTSimpleDeclaration.class, declarator)) {
            return new NullDecision();
        }

        CPPVariable variable;
        if ((variable = Cast.as(CPPVariable.class, declarator.getName().resolveBinding())) == null) {
            return new NullDecision();
        }

        return NonPointer.decide(declarator, (ICPPASTName) declarator.getName(), variable.getType(), decisionType, cache);
    }
}
