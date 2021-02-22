package com.cevelop.ctylechecker.infos;

import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.MessageInfoArgument;


public class CtyleCheckerInfo extends MarkerInfo<CtyleCheckerInfo> {

    @MessageInfoArgument(0)
    public String headerName;

    public CtyleCheckerInfo() {}

    public CtyleCheckerInfo(String headerName) {
        this.headerName = headerName;
    }
}
