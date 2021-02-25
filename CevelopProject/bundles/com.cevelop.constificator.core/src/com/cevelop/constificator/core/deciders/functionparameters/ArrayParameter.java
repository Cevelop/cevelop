package com.cevelop.constificator.core.deciders.functionparameters;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPParameter;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

import com.cevelop.constificator.core.deciders.common.Array;
import com.cevelop.constificator.core.deciders.decision.Decision;
import com.cevelop.constificator.core.deciders.decision.FunctionParameterDecision;
import com.cevelop.constificator.core.deciders.decision.NullDecision;
import com.cevelop.constificator.core.util.ast.ASTRewriteCache;
import com.cevelop.constificator.core.util.structural.Relation;
import com.cevelop.constificator.core.util.type.Cast;


@SuppressWarnings("restriction")
public class ArrayParameter {

    public static Decision canConstify(ICPPASTDeclarator declarator, ASTRewriteCache cache) {
        if (declarator == null || !Relation.isDescendendOf(ICPPASTParameterDeclaration.class, declarator)) {
            return new NullDecision();
        }

        CPPParameter parameter;
        if ((parameter = Cast.as(CPPParameter.class, declarator.getName().resolveBinding())) == null) {
            return new NullDecision();
        }

        final IASTDeclarator nestedDeclarator = declarator.getNestedDeclarator();
        final ICPPASTName name = nestedDeclarator != null ? (ICPPASTName) nestedDeclarator.getName() : (ICPPASTName) declarator.getName();
        final IType type = SemanticUtil.getSimplifiedType(parameter.getType());
        return Array.decide(Cast.as(ICPPASTDeclarator.class, nestedDeclarator != null ? nestedDeclarator : declarator), name, type,
                FunctionParameterDecision.class, cache);
    }

}
