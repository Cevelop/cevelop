package com.cevelop.tdd.infos;

import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.InfoArgument;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.MessageInfoArgument;


public final class FreeOperatorInfo extends MarkerInfo<FreeOperatorInfo> {

    @MessageInfoArgument(0)
    public String operatorName = "";

    @MessageInfoArgument(1)
    public String typeName = "";

    @InfoArgument
    boolean isFreeOperator = false;

}
