package com.cevelop.tdd.infos;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeTemplateParameter;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleTypeTemplateParameter;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.InfoArgument;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.MessageInfoArgument;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.NonPersistentCopyArgument;


public final class TypeInfo extends MarkerInfo<TypeInfo> {

    @MessageInfoArgument(0)
    public String                                           typeName = "";
    @InfoArgument
    public String                                           templateArgumentsString;
    @NonPersistentCopyArgument
    private MutableList<ICPPASTSimpleTypeTemplateParameter> templateArguments;

    public MutableList<ICPPASTSimpleTypeTemplateParameter> getTemplateArguments() {
        if (templateArguments == null) {
            templateArguments = createTemplateArguments();
        }
        return templateArguments;
    }

    public boolean isTemplateSituation() {
        return templateArgumentsString != null && !templateArgumentsString.isEmpty();
    }

    private MutableList<ICPPASTSimpleTypeTemplateParameter> createTemplateArguments() {
        final String templateStart = "T";
        MutableList<ICPPASTSimpleTypeTemplateParameter> result = Lists.mutable.empty();
        if (isTemplateSituation()) {
            int templateCount = templateArgumentsString.split(",").length;
            for (int i = 0; i < templateCount; i++) {
                CPPASTSimpleTypeTemplateParameter templparam = new CPPASTSimpleTypeTemplateParameter();
                templparam.setDefaultType(null);
                templparam.setName(new CPPASTName((templateStart + i).toCharArray()));
                templparam.setParameterType(ICPPASTSimpleTypeTemplateParameter.st_typename);
                result.add(templparam);
            }
        }
        return result;
    }
}
