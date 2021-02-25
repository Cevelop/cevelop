package com.cevelop.ctylechecker.checker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDirective;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNamespace;

import ch.hsr.ifs.iltis.cpp.core.wrappers.AbstractIndexAstChecker;


public abstract class AbstractStyleChecker extends AbstractIndexAstChecker {

    protected static List<IASTName> namesOf(IASTNode node) {
        final List<IASTName> names = new ArrayList<>();
        node.accept(new ASTVisitor() {

            {
                shouldVisitNames = true;
            }

            @Override
            public int visit(IASTName name) {
                names.add(name);
                return PROCESS_CONTINUE;
            }
        });
        return names;
    }

    protected List<ICPPASTUsingDirective> usingDirectivesOf(IASTNode node) {
        final List<ICPPASTUsingDirective> allUsingDeclarations = new ArrayList<>();
        node.accept(new ASTVisitor() {

            {
                shouldVisitDeclarations = true;
            }

            @Override
            public int visit(IASTDeclaration declaration) {
                if (declaration instanceof ICPPASTUsingDirective) {
                    allUsingDeclarations.add((ICPPASTUsingDirective) declaration);
                }
                return PROCESS_CONTINUE;
            }
        });
        return allUsingDeclarations;
    }

    protected List<ICPPASTUsingDeclaration> usingDeclarationsOf(IASTNode node) {
        final List<ICPPASTUsingDeclaration> allUsingDeclarations = new ArrayList<>();
        node.accept(new ASTVisitor() {

            {
                shouldVisitDeclarations = true;
            }

            @Override
            public int visit(IASTDeclaration declaration) {
                if (declaration instanceof ICPPASTUsingDeclaration) {
                    allUsingDeclarations.add((ICPPASTUsingDeclaration) declaration);
                }
                return PROCESS_CONTINUE;
            }
        });
        return allUsingDeclarations;
    }

    protected static boolean equalsAny(Object o, Object... options) {
        if (o == null) {
            return false;
        }
        return Arrays.stream(options).anyMatch(o::equals);
    }

    protected static String toQualifiedName(IASTName name) {
        IBinding binding = name.resolveBinding();
        if (binding instanceof ICPPBinding) {
            ICPPBinding cppBinding = (ICPPBinding) binding;
            try {
                String[] filteredNameParts = filterInlineNamespaces(cppBinding);
                return String.join("::", filteredNameParts);
            } catch (DOMException e) {}
        }
        String strippedTemplateArgument = name.toString().replaceFirst("<.*>", "");
        return strippedTemplateArgument;
    }

    private static String[] filterInlineNamespaces(ICPPBinding cppBinding) throws DOMException {
        List<String> qualifiedNameParts = new ArrayList<>(Arrays.asList(cppBinding.getQualifiedName()));
        if (qualifiedNameParts.size() < 2) {
            return cppBinding.getQualifiedName();
        }
        IBinding owner = cppBinding.getOwner();
        for (int i = qualifiedNameParts.size() - 2; i >= 0; i--) {
            if (!owner.getName().equals(qualifiedNameParts.get(i))) {
                return cppBinding.getQualifiedName();
            }
            if (owner instanceof ICPPNamespace && ((ICPPNamespace) owner).isInline()) {
                qualifiedNameParts.remove(i);
            }
            owner = owner.getOwner();
        }
        return qualifiedNameParts.toArray(new String[qualifiedNameParts.size()]);
    }
}
