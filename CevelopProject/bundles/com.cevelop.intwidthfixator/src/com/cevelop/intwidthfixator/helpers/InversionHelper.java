package com.cevelop.intwidthfixator.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.core.resources.IProject;
import org.eclipse.osgi.util.NLS;

import com.cevelop.intwidthfixator.helpers.IdHelper.WidthId;

import ch.hsr.ifs.iltis.cpp.core.resources.CProjectUtil;


/**
 * @author tstauber
 */
public class InversionHelper extends AbstractHelper {

    private static final String RX_WIDTHS           = "(8|16|32|64)";                            //$NON-NLS-1$
    private static final String RX_SIGNED_CSTDINT   = "(.*\\s)?(std::)?int" + RX_WIDTHS + "_t";  //$NON-NLS-1$ //$NON-NLS-2$
    private static final String RX_UNSIGNED_CSTDINT = "(.*\\s)?(std::)?uint" + RX_WIDTHS + "_t"; //$NON-NLS-1$ //$NON-NLS-2$

    public static boolean isCstdint(final String target) {
        return target.matches(RX_SIGNED_CSTDINT) || target.matches(RX_UNSIGNED_CSTDINT);
    }

    public static boolean isCstdint(final IName name) {
        return isCstdint(name.toString());
    }

    public static boolean isSigned(final String target) {
        return target.matches(RX_SIGNED_CSTDINT);
    }

    public static boolean isUnsigned(final String target) {
        return target.matches(RX_UNSIGNED_CSTDINT);
    }

    /**
     * Converts fixed width integer types to its unfixed width integer type
     * equivalent.
     *
     * @param node
     *        A node of unfixed width integer type that shall be converted to
     *        its unfixed width integer type equivalent.
     *
     * @return ASTRewrite The {@code ICPPASTSimpleDeclSpecifier} able to replace
     *         the provided {@code node}, or Null if the {@code node} could not be
     *         converted to a unfixed width integer type.
     */
    public static ICPPASTSimpleDeclSpecifier convertToSimpleDeclSpecifier(final IASTNode node) {
        if (node == null || !(node instanceof ICPPASTName || node instanceof ICPPASTNamedTypeSpecifier)) { return null; }

        final IProject project = CProjectUtil.getProject(node);

        final IASTName name = node instanceof IASTName ? (IASTName) node : ((ICPPASTNamedTypeSpecifier) node).getName();
        final String nameString = name.getLastName().toString();

        final WidthId typeWidth = getWidthIdFromCstdint(nameString);

        if (typeWidth == getWidthIdFromPreferenceId(IdHelper.P_CHAR_MAPPING, project)) {
            return createNode(node, IASTSimpleDeclSpecifier.t_char, isUnsigned(nameString), false, false, false);
        } else {
            if (typeWidth == getWidthIdFromPreferenceId(IdHelper.P_SHORT_MAPPING, project)) {
                return createNode(node, IASTSimpleDeclSpecifier.t_unspecified, isUnsigned(nameString), true, false, false);
            } else if (typeWidth == getWidthIdFromPreferenceId(IdHelper.P_INT_MAPPING, project)) {
                return createNode(node, IASTSimpleDeclSpecifier.t_int, isUnsigned(nameString), false, false, false);
            } else if (typeWidth == getWidthIdFromPreferenceId(IdHelper.P_LONG_MAPPING, project)) {
                return createNode(node, IASTSimpleDeclSpecifier.t_unspecified, isUnsigned(nameString), false, true, false);
            } else if (typeWidth == getWidthIdFromPreferenceId(IdHelper.P_LONGLONG_MAPPING, project)) {
                return createNode(node, IASTSimpleDeclSpecifier.t_unspecified, isUnsigned(nameString), false, false, true);
            } else {
                return null;
            }
        }
    }

    /**
     * Returns an {@code int} representing the width of the cstdint represented by
     * typeName.
     *
     * @param typeName
     *        Has to be a cstdint type.
     * @throws IllegalArgumentException
     *         If not an cstdint type was passed.
     * @return The types width
     */
    public static int getWidthFromCstdint(final String typeName) throws IllegalArgumentException {
        if (!isCstdint(typeName)) { throw new IllegalArgumentException(NLS.bind(Messages.InversionHelper_Exception, typeName)); }
        final Matcher matcher = getWidthMatcher(typeName);
        matcher.find();
        return Integer.parseUnsignedInt(typeName.substring(matcher.start(), matcher.end()));
    }

    public static WidthId getWidthIdFromCstdint(final String typeName) throws IllegalArgumentException {
        return WidthId.valueOf(getWidthFromCstdint(typeName));
    }

    /**
     * Returns a matcher to find the values (8|16|32|64) in the passed
     * {@code String}
     * 
     * @param target
     *        The target {@code String} to search for the width values
     * 
     * @return A matcher to find the values (8|16|32|64) in the passed
     *         {@code String}
     */
    public static Matcher getWidthMatcher(final String target) {
        final Pattern pattern = Pattern.compile(RX_WIDTHS);
        return pattern.matcher(target);
    }

    /**
     * Creates an ASTRewrite that can be executed to replace the provided
     * {@code node} by its respective fixed width integer type node.
     *
     * @param node
     *        A node of fixed width integer type that shall be replaced its
     *        unfixed width integer type equivalent, not Null.
     *
     * @return ASTRewrite The {@code ASTRewrite} able to replace the provided
     *         {@code node} or Null, if the {@code node} could not be converted to
     *         a {@code cstdint} type.
     */
    public static ASTRewrite createASTRewrite(final IASTNode node) {
        final IASTNode replacementNode = convertToSimpleDeclSpecifier(node);
        final ASTRewrite rewrite = ASTRewrite.create(node.getTranslationUnit());
        if (replacementNode == null) { return null; }
        rewrite.replace(node, replacementNode, null);
        return rewrite;
    }

    /**
     * Creates a ICPPASTSimpleDeclSpecifier that can be used to replace an
     * ICPPASTNamedTypeSpecifier
     */
    private static ICPPASTSimpleDeclSpecifier createNode(final IASTNode sourceNode, final int type, //
            final boolean isUnsigned, final boolean isShort, final boolean isLong, final boolean isLongLong) {
        if (sourceNode instanceof IASTName) {
            return createNode((ICPPASTName) sourceNode, type, isUnsigned, isShort, isLong, isLongLong);
        } else if (sourceNode instanceof ICPPASTNamedTypeSpecifier) {
            return createNode((ICPPASTNamedTypeSpecifier) sourceNode, type, isUnsigned, isShort, isLong, isLongLong);
        } else {
            return null;
        }
    }

    private static ICPPASTSimpleDeclSpecifier createNode(final ICPPASTNamedTypeSpecifier sourceNode, final int type, //
            final boolean isUnsigned, final boolean isShort, final boolean isLong, final boolean isLongLong) {
        final ICPPASTSimpleDeclSpecifier replacementNode = createSimpleDeclSpecifier(sourceNode);
        setPreferences(sourceNode, replacementNode, type, isUnsigned, isShort, isLong, isLongLong);
        return replacementNode;
    }

    private static ICPPASTSimpleDeclSpecifier createNode(final ICPPASTName sourceNode, final int type, //
            final boolean isUnsigned, final boolean isShort, final boolean isLong, final boolean isLongLong) {
        /* Treats functional style casts */
        final ICPPASTSimpleDeclSpecifier replacementNode = createSimpleDeclSpecifier(sourceNode);
        setPreferences(replacementNode, type, isUnsigned, isShort, isLong, isLongLong);
        return replacementNode;
    }

    protected static ICPPASTSimpleDeclSpecifier createSimpleDeclSpecifier(final IASTNode sourceNode) {
        final ICPPNodeFactory factory = (ICPPNodeFactory) sourceNode.getTranslationUnit().getASTNodeFactory();
        final ICPPASTSimpleDeclSpecifier replacementNode = factory.newSimpleDeclSpecifier();
        return replacementNode;
    }

    /**
     * Copies the preferences from a sourceNode to another node.
     * 
     * @param sourceNode
     * The node to be copied from
     * @param node
     * The target node
     * @param type
     * The {@link IASTSimpleDeclSpecifier} type
     * @param isUnsigned
     * Set to {@code true} iff the target should be {@code unsigned}
     * @param isShort
     * Set to {@code true} iff the target should be {@code short}
     * @param isLong
     * Set to {@code true} iff the target should be {@code long}
     * @param isLongLong
     * Set to {@code true} iff the target should be {@code long long}
     */
    protected static void setPreferences(final ICPPASTNamedTypeSpecifier sourceNode, //
            final ICPPASTSimpleDeclSpecifier node, final int type, // 
            final boolean isUnsigned, final boolean isShort, final boolean isLong, final boolean isLongLong) {
        setPreferences(sourceNode, node);
        setPreferences(node, type, isUnsigned, isShort, isLong, isLongLong);
    }

    protected static void setPreferences(final ICPPASTSimpleDeclSpecifier node, final int type, final boolean isUnsigned, //
            final boolean isShort, final boolean isLong, final boolean isLongLong) {
        node.setType(type);
        node.setUnsigned(isUnsigned);
        node.setShort(isShort);
        node.setLong(isLong);
        node.setLongLong(isLongLong);
    }

}
