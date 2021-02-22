package com.cevelop.codeanalysator.cppcore.util;

public class CppCoreIdHelper {

    public static final String DEFAULT_QUALIFIER = "com.cevelop.codeanalysator.cppcore";

    public static final String GuidelineId = DEFAULT_QUALIFIER;

    public static final String AvoidConversionOperatorsProblemId           = DEFAULT_QUALIFIER + ".problem.avoidconversationoperators";
    public static final String DeclareLoopVariableInTheIntializerProblemId = DEFAULT_QUALIFIER + ".problem.declareloopvariableintheintializer";
    public static final String RedundantOperationsProblemId                = DEFAULT_QUALIFIER + ".problem.redundantoperations";
    public static final String MissingSpecialMemberFunctionsProblemId      = DEFAULT_QUALIFIER + ".problem.missingspecialmemberfunctions";
    public static final String AlwaysInitializeAnObjectProblemId           = DEFAULT_QUALIFIER + ".problem.alwaysinitializeanobject";
    public static final String AvoidLossyConversionsProblemId              = DEFAULT_QUALIFIER + ".problem.avoidlossyconversions";

    public static final String NoDestructorProblemId                           = DEFAULT_QUALIFIER + ".problem.nodestructor";
    public static final String DestructorHasNoBodyProblemId                    = DEFAULT_QUALIFIER + ".problem.destructorhasnobody";
    public static final String DestructorWithMissingDeleteStatementsProblemId  = DEFAULT_QUALIFIER + ".problem.destructorwithmissingdeletestatements";
    public static final String DontUseVariableForTwoUnrelatedPurposesProblemId = DEFAULT_QUALIFIER +
                                                                                 ".problem.dontusevariablefortwounrelatedpurposes";

}
