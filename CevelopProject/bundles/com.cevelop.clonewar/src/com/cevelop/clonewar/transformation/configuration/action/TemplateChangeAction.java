package com.cevelop.clonewar.transformation.configuration.action;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.clonewar.transformation.action.TransformAction;
import com.cevelop.clonewar.transformation.configuration.TransformConfiguration;
import com.cevelop.clonewar.transformation.util.TypeInformation;


public abstract class TemplateChangeAction implements IConfigChangeAction {

    private static final String BASE_TEMPLATE_NAME = "T";

    /**
     * Propose unique names for the names.
     *
     * @param config The transform configuration {@link TransformConfiguration}
     */
    protected void proposeUniqueNames(TransformConfiguration config) {
        List<TypeInformation> allTypes = config.getAllTypes();
        Set<String> usedNames = findUsedNames(allTypes);
        int i = 1;
        for (TypeInformation type : allTypes) {
            if (type.getTemplateName().isEmpty()) {
                while (usedNames.contains(BASE_TEMPLATE_NAME + i))
                    i++;
                updateName(type, config, BASE_TEMPLATE_NAME + i);
                usedNames.add(BASE_TEMPLATE_NAME + i);
            }
        }
    }

    /**
     * Find used template names.
     *
     * @param allTypes
     * List of all types.
     * @return Set of used template names.
     */
    private Set<String> findUsedNames(List<TypeInformation> allTypes) {
        Set<String> usedNames = new HashSet<>();
        for (TypeInformation type : allTypes) {
            if (!type.getTemplateName().isEmpty()) usedNames.add(type.getTemplateName());
        }
        return usedNames;
    }

    /**
     * Update a name.
     *
     * @param type
     * Type.
     * @param config
     * Config.
     * @param name
     * Name.
     */
    protected void updateName(TypeInformation type, TransformConfiguration config, String name) {
        List<TransformAction> actions = config.remove(type);
        type.setTemplateName(name);
        config.add(type, actions);
    }

    /**
     * Get the default type from an action of the type.
     *
     * @param type
     * Type information.
     * @param config
     * Configuration.
     * @param status
     * Status.
     * @return Default type.
     */
    protected IASTDeclSpecifier getDefaultTypeOf(TypeInformation type, TransformConfiguration config, RefactoringStatus status) {
        if (config.getActionsOf(type).isEmpty()) {
            status.addError("Can not resolve default type of " + type.getTemplateName() + ".");
            return null;
        }
        return (IASTDeclSpecifier) config.getActionsOf(type).get(0).getNode();
    }
}
