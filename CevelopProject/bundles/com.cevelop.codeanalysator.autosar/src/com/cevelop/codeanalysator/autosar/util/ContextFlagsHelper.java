package com.cevelop.codeanalysator.autosar.util;

public class ContextFlagsHelper {

    public static final int    DoNotIntroduceVirtualFunctionInFinalClassContextFlagsStringIndex       = 0;
    public static final String DoNotIntroduceVirtualFunctionInFinalClassContextFlagPureVirtual        = ":purevirtual";
    public static final String DoNotIntroduceVirtualFunctionInFinalClassContextFlagIntroducingVirtual = ":introducingvirtual";
    public static final int    VirtualFunctionShallHaveExactlyOneSpecifierContextFlagsStringIndex     = 0;
    public static final String VirtualFunctionShallHaveExactlyOneSpecifierContextFlagPureVirtual      = ":purevirtual";
    public static final String VirtualFunctionShallHaveExactlyOneSpecifierContextFlagIntroducingFinal = ":introducingfinal";
    public static final int    SwitchMustHaveAtLeastTwoCasesContextFlagsStringIndex                   = 0;
    public static final String SwitchMustHaveAtLeastTwoCasesContextFlagTrivial                        = ":trivial";
    public static final int    UseAutoSparinglyContextFlagsStringIndex                                = 0;
    public static final String UseAutoSparinglyContextFlagControlDeclaration                          = ":controldeclaration";
    public static final int    DoNotUseTypedefContextFlagsStringIndex                                 = 0;
    public static final String DoNotUseTypedefContextFlagContextFlagStruct                            = ":struct";
}
