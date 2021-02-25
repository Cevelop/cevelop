package com.cevelop.tdd.infos;

import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.InfoArgument;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.MessageInfoArgument;


public final class MemberOperatorInfo extends MarkerInfo<MemberOperatorInfo> {

    @MessageInfoArgument(0)
    public String operatorName = "";

    @MessageInfoArgument(1)
    public String typeName = "";

    @InfoArgument
    public boolean isFreeOperator = false;
    @InfoArgument
    public int     hostTypeStart  = -1;
    @InfoArgument
    public int     hostTypeLength = -1;

}
