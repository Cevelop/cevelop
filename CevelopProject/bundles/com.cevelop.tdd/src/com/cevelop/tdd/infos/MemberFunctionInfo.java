package com.cevelop.tdd.infos;

import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.InfoArgument;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.MessageInfoArgument;


public final class MemberFunctionInfo extends MarkerInfo<MemberFunctionInfo> {

    @InfoArgument
    public boolean mustBeStatic = false;
    @MessageInfoArgument(0)
    public String  name         = "";

}
