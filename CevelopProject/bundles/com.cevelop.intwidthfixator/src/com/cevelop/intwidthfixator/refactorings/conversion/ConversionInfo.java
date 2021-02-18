package com.cevelop.intwidthfixator.refactorings.conversion;

import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;

import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.InfoArgument;


/**
 * @author tstauber
 */
public final class ConversionInfo extends MarkerInfo<ConversionInfo> {

    @InfoArgument
    public boolean refactor_char;
    @InfoArgument
    public boolean refactor_short;
    @InfoArgument
    public boolean refactor_int;
    @InfoArgument
    public boolean refactor_long;
    @InfoArgument
    public boolean refactor_longlong;
    @InfoArgument
    public String  mrLabel;

    public ConversionInfo() {}

    public boolean isTypeSelectedForRefactoring(final IASTSimpleDeclSpecifier declSpec) {

        switch (declSpec.getType()) {
        case IASTSimpleDeclSpecifier.t_char:
            return refactor_char;
        case IASTSimpleDeclSpecifier.t_int:
            return (refactor_longlong && declSpec.isLongLong()) //
                   || (refactor_long && declSpec.isLong()) //
                   || (refactor_short && declSpec.isShort()) //
                   || (refactor_int && !(declSpec.isShort() || declSpec.isLong() || declSpec.isLongLong()));
        case IASTSimpleDeclSpecifier.t_unspecified:
            return (refactor_longlong && declSpec.isLongLong()) || (refactor_long && declSpec.isLong()) || (refactor_short && declSpec.isShort());
        default:
            return false;
        }
    }

}
