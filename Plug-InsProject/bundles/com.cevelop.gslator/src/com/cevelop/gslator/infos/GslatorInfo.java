package com.cevelop.gslator.infos;

import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.MessageInfoArgument;


public class GslatorInfo extends MarkerInfo<GslatorInfo> {

    @MessageInfoArgument(0)
    public String text;

    public GslatorInfo() {}

    public GslatorInfo(String text) {
        this.text = text;
    }
}
