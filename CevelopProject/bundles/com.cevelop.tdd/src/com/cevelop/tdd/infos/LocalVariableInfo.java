package com.cevelop.tdd.infos;

import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.MessageInfoArgument;


public final class LocalVariableInfo extends MarkerInfo<LocalVariableInfo> {

    @MessageInfoArgument(0)
    public String name = "";
}
