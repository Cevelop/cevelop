package com.cevelop.tdd.checkers.visitors;

import java.util.Arrays;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMember;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTBaseDeclSpecifier;

import com.cevelop.tdd.helpers.IdHelper.ProblemId;
import com.cevelop.tdd.helpers.TddHelper;
import com.cevelop.tdd.infos.VisibilityInfo;

import ch.hsr.ifs.iltis.core.core.functional.functions.Consumer3;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class VisibilityProblemVisitor extends ASTVisitor {

    {
        shouldVisitNames = true;
    }

    private Consumer3<IProblemId<ProblemId>, IASTName, VisibilityInfo> problemReporter;

    public VisibilityProblemVisitor(Consumer3<IProblemId<ProblemId>, IASTName, VisibilityInfo> problemReporter) {
        this.problemReporter = problemReporter;
    }

    @Override
    public int visit(IASTName name) {
        ICPPMember member = getMember(name);
        if (member == null) {
            return PROCESS_CONTINUE;
        }
        CPPASTBaseDeclSpecifier type = TddHelper.getAncestorOfType(name, CPPASTBaseDeclSpecifier.class);
        if (type != null) {
            return PROCESS_CONTINUE;
        }
        final ICPPClassType owner = member.getClassOwner();
        if (owner == null) {
            return PROCESS_CONTINUE;
        }
        final ICPPMember[] methods = owner.getAllDeclaredMethods();
        final ICPPMember[] fields = owner.getDeclaredFields();
        if (ArrayUtil.containsEqual(methods, member) || ArrayUtil.containsEqual(fields, member)) {
            if (member.getVisibility() == ICPPMember.v_private) {
                IBinding surroundingFunction = findAscendingFunctionBinding(name);
                IIndex index = name.getTranslationUnit().getIndex();
                ICPPClassType indexOwnerBinding = (ICPPClassType) index.adaptBinding(owner);
                IBinding indexFunctionBinding = index.adaptBinding(surroundingFunction);
                if (canAccessPrivateMember(indexOwnerBinding, indexFunctionBinding)) {
                    return PROCESS_CONTINUE;
                }
                String memberName = new String(name.getSimpleID());
                VisibilityInfo info = new VisibilityInfo();
                info.memberName = memberName;
                info.message = memberName;
                problemReporter.accept(ProblemId.VISIBILITY, name.getLastName(), info);
            }
        }
        return PROCESS_CONTINUE;
    }

    private boolean canAccessPrivateMember(final ICPPClassType owner, IBinding surroundingFunction) {
        if (owner == null || surroundingFunction == null) return false;
        final IType typeOfContext = findTypeOfContext(surroundingFunction);
        return owner.isSameType(typeOfContext) || isFriendOf(surroundingFunction.getOwner(), owner) || isFriendOf(surroundingFunction, owner);
    }

    private boolean isFriendOf(IBinding possibleFriend, ICPPClassType typeOfTarget) {
        final IBinding[] friends = typeOfTarget.getFriends();
        return Arrays.asList(friends).contains(possibleFriend);
    }

    private IType findTypeOfContext(IBinding surroundingFunction) {
        if (surroundingFunction != null) {
            final IBinding owner = surroundingFunction.getOwner();
            if (owner instanceof IType) {
                return (IType) owner;
            }
        }
        return null;
    }

    IBinding findAscendingFunctionBinding(IASTName name) {
        IASTNode ancestor = name;
        while (ancestor != null) {
            ancestor = ancestor.getParent();
            if (ancestor instanceof ICPPASTFunctionDefinition) {
                final IASTName functionName = ((IASTFunctionDefinition) ancestor).getDeclarator().getName();
                return functionName.resolveBinding();
            }
        }
        return null;
    }

    private ICPPMember getMember(IASTName name) {
        if (!(name.getParent() instanceof ICPPASTFieldReference)) {
            return null;
        }
        final IBinding binding = name.resolveBinding();
        if (binding instanceof ICPPMember) {
            return (ICPPMember) binding;
        }
        return null;
    }
}
