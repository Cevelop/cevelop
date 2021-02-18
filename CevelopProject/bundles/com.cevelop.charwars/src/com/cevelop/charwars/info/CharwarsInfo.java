package com.cevelop.charwars.info;

import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.MessageInfoArgument;


public class CharwarsInfo extends MarkerInfo<CharwarsInfo> {

    @MessageInfoArgument(0)
    public String nodeName;

}
