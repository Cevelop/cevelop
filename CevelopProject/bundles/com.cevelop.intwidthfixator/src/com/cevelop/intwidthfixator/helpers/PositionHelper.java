package com.cevelop.intwidthfixator.helpers;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.rewrite.astwriter.ASTWriter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;


@SuppressWarnings("restriction")
public class PositionHelper {

    private static final int NOT_FOUND = -1;

    public static IRegion getRegionOfType(final ICPPASTSimpleDeclSpecifier declSpecifier) {
        final String signature = declSpecifier.getRawSignature();
        final String plainSignature = getPlainType(declSpecifier);

        final int innerOffset = signature.indexOf(plainSignature);

        if (innerOffset == NOT_FOUND) {
            return new Region(0, signature.length());
        } else {
            return new Region(innerOffset, plainSignature.length());
        }

    }

    public static IRegion getRegionOfTypeInFile(final ICPPASTSimpleDeclSpecifier declSpecifier) {
        final IASTFileLocation location = declSpecifier.getFileLocation();

        final String signature = declSpecifier.getRawSignature();
        final String plainSignature = getPlainType(declSpecifier);

        final int nodeOffset = location.getNodeOffset();
        final int innerOffset = signature.indexOf(plainSignature);

        if (innerOffset == NOT_FOUND) {
            final int nodeLength = location.getNodeLength();
            return new Region(nodeOffset, nodeLength);
        } else {
            return new Region(nodeOffset + innerOffset, plainSignature.length());
        }
    }

    private static String getPlainType(final ICPPASTSimpleDeclSpecifier declSpecifier) {
        final ICPPASTSimpleDeclSpecifier copy = declSpecifier.copy(CopyStyle.withoutLocations);
        copy.setConst(false);
        copy.setConstexpr(false);
        copy.setFriend(false);
        copy.setStorageClass(IASTDeclSpecifier.sc_unspecified);
        return new ASTWriter().write(copy).trim();
    }
}
