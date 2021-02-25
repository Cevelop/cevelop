package com.cevelop.intwidthfixator.refactorings.inversion;

import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.InfoArgument;


/**
 * @author tstauber
 */
public final class InversionRefactoringInfo extends MarkerInfo<InversionRefactoringInfo> {

    @InfoArgument
    public boolean refactor_int8;
    @InfoArgument
    public boolean refactor_int16;
    @InfoArgument
    public boolean refactor_int32;
    @InfoArgument
    public boolean refactor_int64;

    @InfoArgument
    public boolean refactor_unsigned;
    @InfoArgument
    public boolean refactor_signed;

    public boolean isTypeSelectedForRefactoring(final int width) {
        switch (width) {
        case 8:
            return refactor_int8;
        case 16:
            return refactor_int16;
        case 32:
            return refactor_int32;
        case 64:
            return refactor_int64;
        default:
            return false;
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("InversionRefactoringInfo [ refactor_int8 = ");
        builder.append(refactor_int8);
        builder.append(" | refactor_int16 = ");
        builder.append(refactor_int16);
        builder.append(" | refactor_int32 = ");
        builder.append(refactor_int32);
        builder.append(" | refactor_int64 = ");
        builder.append(refactor_int64);
        builder.append(" | refactor_unsigned = ");
        builder.append(refactor_unsigned);
        builder.append(" | refactor_signed = ");
        builder.append(refactor_signed);
        builder.append(" | use_selection = ");
        builder.append(usesSelection());
        builder.append("]  ");
        return builder.toString();
    }

}
