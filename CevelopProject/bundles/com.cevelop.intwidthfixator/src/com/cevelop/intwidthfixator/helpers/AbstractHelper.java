package com.cevelop.intwidthfixator.helpers;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.core.resources.IProject;

import com.cevelop.intwidthfixator.helpers.IdHelper.WidthId;
import com.cevelop.intwidthfixator.preferences.PropAndPrefHelper;


/**
 * @author tstauber
 */
public abstract class AbstractHelper {

    /**
     * Gets the {@link WidthId} {@code enum} element for the passed preference id.
     *
     * @param id
     * The preference store id that should be read.
     *
     * @param project
     * The project to get the preferences for
     *
     * @throws IllegalArgumentException
     * If the preference store returned an invalid value.
     *
     * @return The {@link WidthId} for the passed id
     */
    public static WidthId getWidthIdFromPreferenceId(final String id, final IProject project) {
        final String preference = getStringPreference(id, project);
        return WidthId.valueOf(preference);
    }

    /**
     * Gets the string value for the preference constant provided
     *
     * @param key
     * The id of a preference constant
     *
     * @param project
     * The project to get the preferences for
     *
     * @return The string returned by the default {@code PreferenceStore}
     */
    public static String getStringPreference(final String key, final IProject project) {
        return PropAndPrefHelper.getInstance().getString(key, project);
    }

    /**
     * Copies the preferences from a sourceNode to another node.
     *
     * @param sourceNode
     * Source node from which to copy the preferences
     *
     * @param node
     * Target node for which to set the preferences
     */
    protected static void setPreferences(final ICPPASTDeclSpecifier sourceNode, final ICPPASTDeclSpecifier node) {
        node.setStorageClass(sourceNode.getStorageClass());
        node.setFriend(sourceNode.isFriend());
        node.setConstexpr(sourceNode.isConstexpr());
        node.setVolatile(sourceNode.isVolatile());
        node.setConst(sourceNode.isConst());
    }

}
