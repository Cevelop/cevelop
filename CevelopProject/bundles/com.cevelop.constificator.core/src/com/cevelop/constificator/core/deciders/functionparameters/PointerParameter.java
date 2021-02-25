package com.cevelop.constificator.core.deciders.functionparameters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;

import com.cevelop.constificator.core.deciders.common.Pointer;
import com.cevelop.constificator.core.deciders.decision.Decision;
import com.cevelop.constificator.core.deciders.decision.FunctionParameterDecision;
import com.cevelop.constificator.core.util.ast.ASTRewriteCache;
import com.cevelop.constificator.core.util.structural.Relation;
import com.cevelop.constificator.core.util.type.Cast;


public class PointerParameter {

    public static List<Decision> canConstify(ICPPASTDeclarator declarator, ASTRewriteCache cache) {
        if (declarator == null || !Relation.isDescendendOf(ICPPASTParameterDeclaration.class, declarator)) {
            return new ArrayList<>();
        }

        ICPPParameter parameter;
        if ((parameter = Cast.as(ICPPParameter.class, declarator.getName().resolveBinding())) == null) {
            return new ArrayList<>();
        }

        return Pointer.decide(declarator, (ICPPASTName) declarator.getName(), parameter.getType(), FunctionParameterDecision.class, cache);
    }

}
