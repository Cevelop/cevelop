package com.cevelop.clonewar.transformation.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.cevelop.clonewar.transformation.action.TransformAction;
import com.cevelop.clonewar.transformation.util.TypeInformation;
import com.cevelop.clonewar.transformation.util.namelookup.ReturnNameLookupStrategy;


/**
 * Configuration for the refactoring/transformation process. Contains a mapping
 * of {@link TypeInformation} to multiple {@link TransformAction}.
 *
 * @author ythrier(at)hsr.ch
 */
public class TransformConfiguration {

    private Map<TypeInformation, List<TransformAction>> actions_;

    /**
     * Create the configuration with the given action/type mapping.
     *
     * @param actions
     * type to action mapping.
     */
    public TransformConfiguration(Map<TypeInformation, List<TransformAction>> actions) {
        this.actions_ = actions;
    }

    /**
     * Check if this type has at least one action which is enabled for perform.
     *
     * @param type
     * Type.
     * @return True if one action has the perform flag set to true for this
     * type, otherwise false.
     */
    public boolean hasPerformableAction(TypeInformation type) {
        for (TransformAction action : getActionsOf(type))
            if (action.shouldPerform()) return true;
        return false;
    }

    /**
     * Return a list of all actions.
     *
     * @return List of all actions.
     */
    public List<TransformAction> getAllActions() {
        List<TransformAction> actions = new ArrayList<>();
        for (List<TransformAction> actionList : actions_.values())
            actions.addAll(actionList);
        return actions;
    }

    /**
     * Return an ordered list of all types.
     *
     * @return List of types.
     */
    public List<TypeInformation> getAllTypesOrdered() {
        List<TypeInformation> allTypes = getAllTypes();
        Collections.sort(allTypes);
        return allTypes;
    }

    /**
     * Return a list of all types.
     *
     * @return Types.
     */
    public List<TypeInformation> getAllTypes() {
        return new ArrayList<>(actions_.keySet());
    }

    /**
     * Add an entry to the configuration.
     *
     * @param type
     * Type.
     * @param actions
     * Actions.
     */
    public void add(TypeInformation type, List<TransformAction> actions) {
        actions_.put(type, actions);
    }

    /**
     * Remove an entry from the configuration.
     *
     * @param type
     * Type.
     * @return List of actions removed.
     */
    public List<TransformAction> remove(TypeInformation type) {
        return actions_.remove(type);
    }

    /**
     * Returns a list of all actions of a type.
     *
     * @param type
     * Type.
     * @return List of actions of the type.
     */
    public List<TransformAction> getActionsOf(TypeInformation type) {
        return actions_.get(type);
    }

    /**
     * Return the return type action of the given type information.
     *
     * @param type
     * Type information.
     * @return Return type transform action.
     */
    public TransformAction getReturnTypeAction(TypeInformation type) {
        if (!hasReturnTypeAction(type)) return null;
        for (TransformAction action : getActionsOf(type)) {
            if (action.getVariableName().equals(ReturnNameLookupStrategy.RETURN_SPECIFIER)) return action;
        }
        return null;
    }

    /**
     * Check if the type information has an action which is applied on a return
     * type.
     *
     * @param type
     * Type information.
     * @return True if the actions of the type information contains a return
     * type action, otherwise false.
     */
    public boolean hasReturnTypeAction(TypeInformation type) {
        for (TransformAction action : getActionsOf(type)) {
            if (action.getVariableName().equals(ReturnNameLookupStrategy.RETURN_SPECIFIER)) return true;
        }
        return false;
    }
}
