package com.cevelop.constificator.core.deciders.localvariables;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPVariable;

import com.cevelop.constificator.core.deciders.common.Pointer;
import com.cevelop.constificator.core.deciders.decision.Decision;
import com.cevelop.constificator.core.util.ast.ASTRewriteCache;
import com.cevelop.constificator.core.util.structural.Relation;
import com.cevelop.constificator.core.util.type.Cast;


@SuppressWarnings("restriction")
public class PointerVariable {

    public static List<Decision> canConstify(ICPPASTDeclarator declarator, ASTRewriteCache cache, Class<? extends Decision> decisionType) {
        if (declarator == null || !Relation.isDescendendOf(IASTSimpleDeclaration.class, declarator)) {
            return new ArrayList<>();
        }

        CPPVariable pointer;
        if ((pointer = Cast.as(CPPVariable.class, declarator.getName().resolveBinding())) == null) {
            return new ArrayList<>();
        }

        return Pointer.decide(declarator, (ICPPASTName) declarator.getName(), pointer.getType(), decisionType, cache);
    }

}
