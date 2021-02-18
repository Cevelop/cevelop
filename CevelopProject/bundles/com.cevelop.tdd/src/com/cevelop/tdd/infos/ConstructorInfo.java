package com.cevelop.tdd.infos;

import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.MessageInfoArgument;


public final class ConstructorInfo extends MarkerInfo<ConstructorInfo> {

    @MessageInfoArgument(0)
    public String typeName = "";

}
