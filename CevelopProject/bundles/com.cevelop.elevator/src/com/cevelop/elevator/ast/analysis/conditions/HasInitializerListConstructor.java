package com.cevelop.elevator.ast.analysis.conditions;

import java.util.Optional;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTImplicitName;
import org.eclipse.cdt.core.dom.ast.IASTImplicitNameOwner;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;

import com.cevelop.elevator.Activator;


/**
 * Checks if type of the supplied node has a constructor with {@link IASTInitializerList} argument.
 */
public class HasInitializerListConstructor extends Condition {
    // TODO: duplicated code with HasDefaultConstructor

    @Override
    public boolean satifies(final IASTNode node) {
        if (node instanceof IASTImplicitNameOwner) {
            return hasInitializerListConstructor((IASTImplicitNameOwner) node);
        }
        return false;
    }

    private boolean hasInitializerListConstructor(IASTImplicitNameOwner implicitNameOwner) {
        IASTImplicitName[] implicitNames = implicitNameOwner.getImplicitNames();
        for (IASTImplicitName name : implicitNames) {
            if (containsInitializerList(name.getBinding())) {
                return true;
            }
        }
        return false;
    }

    private boolean containsInitializerList(IBinding binding) {
        if (binding instanceof ICPPConstructor) {
            ICPPConstructor[] constructors = getAllConstructors((ICPPConstructor) binding);
            for (ICPPConstructor constructor : constructors) {
                if (containsInitializerList(constructor)) {
                    return true;
                }
            }
        }
        return false;
    }

    private ICPPConstructor[] getAllConstructors(ICPPConstructor binding) {
        ICPPClassType classOwner = binding.getClassOwner();
        if (classOwner instanceof ICPPClassType) {
            return classOwner.getConstructors();
        }
        return classOwner.getConstructors();
    }

    private boolean containsInitializerList(ICPPConstructor constructor) {
        if (constructor == null) {
            return false;
        }
        for (ICPPParameter parameter : constructor.getParameters()) {
            if (parameter.getType() instanceof ICPPClassType) {
                ICPPClassType type = (ICPPClassType) parameter.getType();
                if (type == null) {
                    return false;
                }
                Optional<IScope> scope = getScope(type);
                if (!scope.isPresent() || scope.get() == null) {
                    return false;
                }
                String name = type.getName();
                IName scopeName = scope.get().getScopeName();
                return name != null && name.startsWith("initializer_list") && scopeName != null && "std".equals(scopeName.toString());
            }
        }
        return false;
    }

    private Optional<IScope> getScope(ICPPClassType type) {
        try {
            return Optional.ofNullable(type.getScope());
        } catch (DOMException e) {
            Activator.log(e);
        }
        return Optional.empty();
    }
}
