package com.cevelop.tdd.infos;

import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.InfoArgument;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.MessageInfoArgument;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.NonPersistentCopyArgument;


public final class ArgumentMismatchInfo extends MarkerInfo<ArgumentMismatchInfo> {

    @MessageInfoArgument(0)
    public String argumentName = "";

    @NonPersistentCopyArgument
    public int    candidateNr;

    @InfoArgument
    public String candidates = "";
}
