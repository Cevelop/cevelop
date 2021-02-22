package com.cevelop.intwidthfixator.helpers;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.core.resources.IProject;

import ch.hsr.ifs.iltis.cpp.core.resources.CProjectUtil;


/**
 * @author tstauber
 */
public class ConversionHelper extends AbstractHelper {

    public static ICPPASTNamedTypeSpecifier convertToNamedTypeSpecifier(final ICPPASTSimpleDeclSpecifier sourceNode) {

        final int type = sourceNode.getType();

        if (type == IASTSimpleDeclSpecifier.t_char) {
            return createNode(sourceNode, IdHelper.P_CHAR_MAPPING);
        } else if (type == IASTSimpleDeclSpecifier.t_int || type == IASTSimpleDeclSpecifier.t_unspecified) {
            if (sourceNode.isShort()) {
                return createNode(sourceNode, IdHelper.P_SHORT_MAPPING);
            } else if (sourceNode.isLong()) {
                return createNode(sourceNode, IdHelper.P_LONG_MAPPING);
            } else if (sourceNode.isLongLong()) {
                return createNode(sourceNode, IdHelper.P_LONGLONG_MAPPING);
            } else if (type == IASTSimpleDeclSpecifier.t_int) {
                return createNode(sourceNode, IdHelper.P_INT_MAPPING);
            }
        }
        return null;
    }

    /**
     * Creates an {@link ICPPASTNamedTypeSpecifier} as the fixed-width equivalent
     * of the passed {@code sourceNode}.
     *
     * @param sourceNode
     * The source node from which to create the {@link ICPPASTNamedTypeSpecifier}
     *
     * @param sizePref
     * The size preference id
     *
     * @return The newly created {@link ICPPASTNamedTypeSpecifier}
     */
    private static ICPPASTNamedTypeSpecifier createNode(final ICPPASTSimpleDeclSpecifier sourceNode, final String sizePref) {
        final IProject project = CProjectUtil.getProject(sourceNode);

        final boolean isUnsigned = (sourceNode.getType() == IASTSimpleDeclSpecifier.t_char && !(sourceNode.isUnsigned() || sourceNode.isSigned()))
                                                                                                                                                   ? isDefaultUnsigned(
                                                                                                                                                           project)
                                                                                                                                                   : sourceNode
                                                                                                                                                           .isUnsigned();

        final ICPPNodeFactory factory = (ICPPNodeFactory) sourceNode.getTranslationUnit().getASTNodeFactory();
        final int width = getWidthIdFromPreferenceId(sizePref, project).width;
        final ICPPASTName name = factory.newName(String.format("std::%sint%s_t", (isUnsigned ? "u" : ""), width)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        final ICPPASTNamedTypeSpecifier node = factory.newNamedTypeSpecifier(name);
        setPreferences(sourceNode, node);
        return node;
    }

    /**
     * Creates an {@link ASTRewrite} from to replace the passed {@code IASTNode}
     * with an fixed-width {@code ICPPASTNamedTypeSpecifier}. If the node somehow
     * could not be converted, this method will return {@code null}.
     *
     * @param node
     * The source node to be replaced
     *
     * @return A new {@link ASTRewrite}
     */
    public static ASTRewrite createASTRewrite(final IASTNode node) {
        final IASTNode replacementNode = convertToNamedTypeSpecifier((ICPPASTSimpleDeclSpecifier) node);
        final ASTRewrite rewrite = ASTRewrite.create(node.getTranslationUnit());
        if (replacementNode == null) {
            return null;
        }
        rewrite.replace(node, replacementNode, null);
        return rewrite;
    }

    /**
     * Determines if @{code char} is by default configured as signed or unsigned
     *
     * @param project
     * The project for which the unsigned preference needs to be checked
     *
     * @return {@code true} iff the default for this project is set to unsigned
     */
    private static boolean isDefaultUnsigned(final IProject project) {
        return getStringPreference(IdHelper.P_CHAR_PLATFORM_SIGNED_UNSIGNED, project).equals(IdHelper.V_CHAR_PLATFORM_UNSIGNED);
    }

}
