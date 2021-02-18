package com.cevelop.templator.plugin.viewdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.IParameter;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPAliasTemplateInstance;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassTemplate;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunctionSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateArgument;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateInstance;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPVariable;
import org.eclipse.cdt.core.parser.util.StringUtil;
import org.eclipse.cdt.internal.core.dom.rewrite.astwriter.ASTWriter;
import org.eclipse.jface.text.IRegion;

import com.cevelop.templator.plugin.asttools.data.AbstractResolvedNameInfo;
import com.cevelop.templator.plugin.asttools.data.AbstractTemplateInstance;
import com.cevelop.templator.plugin.asttools.data.NameTypeKind;
import com.cevelop.templator.plugin.asttools.data.ResolvedName;
import com.cevelop.templator.plugin.asttools.data.SubNameErrorCollection;
import com.cevelop.templator.plugin.asttools.formatting.ASTTemplateFormatter;
import com.cevelop.templator.plugin.asttools.templatearguments.TemplateArgumentMap;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.plugin.util.ILoadingProgress;
import com.cevelop.templator.plugin.util.ImageCache.ImageID;


public class ViewData {

    private AbstractResolvedNameInfo resolvedNameInfo;
    private ResolvedName             resolvedName;
    private Map<Integer, IRegion>    subSegments;
    private String                   dataText;
    private String                   title  = null;
    private ASTWriter                writer = new ASTWriter();

    public ViewData(ResolvedName resolvedName) {
        this.resolvedName = resolvedName;
        resolvedNameInfo = resolvedName.getInfo();
    }

    public void prepareForView(ILoadingProgress loadingProgress) throws TemplatorException {

        // searching for sub templates
        resolvedNameInfo.searchSubNames(loadingProgress);

        // formatting the copied AST
        ASTTemplateFormatter.format(resolvedNameInfo);

        // write the create the data text and generate clickable regions
        loadingProgress.setStatus("Rewriting Code...");
        ASTWriterRegionFinder regionFindWriter = new ASTWriterRegionFinder(resolvedNameInfo);

        // populate fields for data access later
        this.dataText = regionFindWriter.getSourceString();
        this.subSegments = regionFindWriter.getFoundRegions();
    }

    public String getTitle() {
        if (title != null) {
            return title;
        }
        IBinding binding = resolvedNameInfo.getBinding();
        if (binding instanceof ICPPTemplateInstance) {
            ICPPTemplateInstance templateInstance = (ICPPTemplateInstance) binding;
            return createTitle(templateInstance.getTemplateDefinition());
        } else if (binding instanceof ICPPAliasTemplateInstance) {
            ICPPAliasTemplateInstance aliasTemplate = (ICPPAliasTemplateInstance) binding;
            return createTitle(aliasTemplate.getTemplateDefinition());
        } else if (binding instanceof ICPPFunctionSpecialization) {
            ICPPFunctionSpecialization functionSpec = (ICPPFunctionSpecialization) binding;
            return createTitle(functionSpec);
        } else if (binding instanceof ICPPFunction) {
            title = binding.toString();
            title = title.replace("&", "&&"); // & is not shown
            return title;
        } else if (binding instanceof ICPPClassType || binding instanceof ICPPVariable) {
            title = binding.toString();
            return title;
        }
        // fallback
        return resolvedName.getOriginalNode().toString();
    }

    private String createTitle(ICPPTemplateDefinition templateDef) {
        if (templateDef instanceof ICPPClassTemplate) {
            return createTitleFromDeferredString((ICPPClassTemplate) templateDef);
        }
        StringBuilder sb = new StringBuilder(templateDef.getName());
        sb.append('<');
        ICPPTemplateParameter[] params = templateDef.getTemplateParameters();
        for (int i = 0; i < params.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(params[i].toString());
            if (params[i].isParameterPack()) {
                sb.append("...");
            }
        }
        sb.append('>');
        title = sb.toString();
        return title;
    }

    private String createTitleFromDeferredString(ICPPClassTemplate classTemplate) {
        String deferredString = getDeferredString(classTemplate);
        return replaceDeferred(deferredString, classTemplate.getTemplateParameters());
    }

    private String getDeferredString(ICPPClassTemplate classTemplate) {
        return classTemplate.asDeferredInstance().toString();
    }

    private String replaceDeferred(String deferredString, ICPPTemplateParameter[] params) {
        for (int i = 0; i < params.length; i++) {
            deferredString = deferredString.replace("#" + i, params[i].toString());
        }
        deferredString = deferredString.replace("(...)", "");
        return deferredString.replace(" ", "");
    }

    private String createTitle(ICPPFunctionSpecialization functionSpec) {
        StringBuilder sb = new StringBuilder(functionSpec.getName());
        sb.append('(');
        IType[] params = functionSpec.getDeclaredType().getParameterTypes();
        String[] typeStrings = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            typeStrings[i] = params[i].toString();
        }
        sb.append(StringUtil.join(typeStrings, ","));
        sb.append(')');
        title = sb.toString();
        title = title.replace("&", "&&"); // & is not shown
        return title;
    }

    public String getDataText() {
        return dataText;
    }

    public List<String> getDescription() {
        List<String> descriptionStrings = new ArrayList<>();
        if (resolvedNameInfo instanceof AbstractTemplateInstance) {
            IBinding binding = resolvedNameInfo.getBinding();
            ICPPTemplateParameter[] templateParameters = null;
            if (binding instanceof ICPPTemplateInstance) {
                ICPPTemplateInstance templateInstance = (ICPPTemplateInstance) binding;
                templateParameters = templateInstance.getTemplateDefinition().getTemplateParameters();
            } else if (binding instanceof ICPPAliasTemplateInstance) {
                ICPPAliasTemplateInstance aliasTemplate = (ICPPAliasTemplateInstance) binding;
                templateParameters = aliasTemplate.getTemplateDefinition().getTemplateParameters();
            }

            if (templateParameters == null || templateParameters.length == 0) {
                return descriptionStrings;
            }

            for (ICPPTemplateParameter templateParameter : templateParameters) {
                if (!templateParameter.isParameterPack()) {
                    ICPPTemplateArgument argument = resolvedNameInfo.getArgument(templateParameter);
                    String typeString = "";
                    if (argument.isNonTypeValue()) {
                        typeString += argument.getTypeOfNonTypeValue();
                    } else if (templateParameter instanceof ICPPTemplateTemplateParameter) {
                        typeString += "template<";
                        ICPPTemplateTemplateParameter templateTemplateParameter = (ICPPTemplateTemplateParameter) templateParameter;
                        for (int i = 0; i < templateTemplateParameter.getTemplateParameters().length; i++) {
                            if (i > 0) {
                                typeString += ',';
                            }
                            typeString += "typename";
                        }
                        typeString += ">";
                    } else {
                        typeString += "typename ";
                    }
                    typeString += " " + templateParameter.toString();
                    String argumentString = TemplateArgumentMap.getArgumentString(argument);
                    descriptionStrings.add(typeString + " = " + argumentString);
                } else {
                    ICPPTemplateArgument[] packExpansion = resolvedNameInfo.getTemplateArgumentMap().getPackExpansion(templateParameter);
                    String[] argumentStrings = new String[packExpansion.length];
                    for (int i = 0; i < packExpansion.length; i++) {
                        argumentStrings[i] = TemplateArgumentMap.getArgumentString(packExpansion[i]);
                    }
                    String allArguments = StringUtil.join(argumentStrings, ", ");
                    descriptionStrings.add("typename... " + templateParameter.toString() + " = " + allArguments);
                }
            }
        }

        return descriptionStrings;
    }

    public Map<Integer, IRegion> getSubSegments() {
        return subSegments;
    }

    public ViewData getSubNameData(int subNameIndex) {
        ResolvedName subInstance = resolvedNameInfo.getSubNames().get(subNameIndex);
        return new ViewData(subInstance);
    }

    public ImageID getTypeIcon() {
        NameTypeKind type = resolvedNameInfo.getType();
        switch (type) {
        case CLASS:
            return ImageID.TYPE_CLASS;
        case FUNCTION:
            if (resolvedNameInfo.getBinding() instanceof ICPPMethod) return ImageID.TYPE_MEMBER_FUNCTION;
            return ImageID.TYPE_FUNCTION;
        case METHOD:
            return ImageID.TYPE_MEMBER_FUNCTION;
        case FUNCTION_TEMPLATE:
            return ImageID.TYPE_FUNCTION_TEMPLATE;
        case CLASS_TEMPLATE:
            return ImageID.TYPE_CLASS_TEMPLATE;
        case VARIABLE_TEMPLATE:
            return ImageID.TYPE_VARIABLE_TEMPALTE;
        case ALIAS_TEMPLATE:
            return ImageID.TYPE_ALIAS_TEMPLATE;
        case LAMBDA:
            return ImageID.TYPE_LAMBDA;
        default:
            return ImageID.TYPE_UNKNOWN;
        }
    }

    public SubNameErrorCollection getSubNameErrors() {
        return resolvedNameInfo.getSubNameErrors();
    }

    public void navigateToName() {
        resolvedNameInfo.navigateTo();
    }

    public void navigateToSubName(int subNameIndex) {
        AbstractResolvedNameInfo subNameInfo = resolvedNameInfo.getSubNames().get(subNameIndex).getInfo();
        subNameInfo.navigateTo();
    }

    @Override
    public boolean equals(Object obj) {
        ViewData viewData = (ViewData) obj;
        IBinding thisBinding = resolvedNameInfo.getBinding();
        IBinding otherBinding = viewData.resolvedNameInfo.getBinding();

        // if the names are not the same it cannot be equal
        if (!(thisBinding.getName().equals(otherBinding.getName()))) {
            return false;
        }

        // if the NameTypeKind is not the same it cannot be equal
        if (resolvedNameInfo.getType() != viewData.resolvedNameInfo.getType()) {
            return false;
        }

        // if the template arguments are not the same it cannot be equal
        if (thisBinding instanceof ICPPTemplateInstance && otherBinding instanceof ICPPTemplateInstance) {
            ICPPTemplateInstance thisInstance = (ICPPTemplateInstance) thisBinding;
            ICPPTemplateInstance otherInstance = (ICPPTemplateInstance) otherBinding;
            if (!haveSameTemplateArguments(thisInstance, otherInstance)) {
                return false;
            }
        }

        if (thisBinding instanceof IFunction & otherBinding instanceof IFunction) {
            IFunction thisFunction = (IFunction) thisBinding;
            IFunction otherFunction = (IFunction) otherBinding;

            // if the parameters are not the same it cannot be the same function
            IParameter[] thisParameters = thisFunction.getParameters();
            IParameter[] otherParameters = otherFunction.getParameters();
            if (thisParameters.length != otherParameters.length) {
                return false;
            }
            for (int i = 0; i < thisParameters.length; i++) {
                if (!thisParameters[i].equals(otherParameters[i])) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    private boolean haveSameTemplateArguments(ICPPTemplateInstance thisInstance, ICPPTemplateInstance otherInstance) {
        ICPPTemplateArgument[] thisArguments = thisInstance.getTemplateArguments();
        ICPPTemplateArgument[] otherArguments = otherInstance.getTemplateArguments();
        if (thisArguments.length != otherArguments.length) {
            return false;
        }
        for (int i = 0; i < thisArguments.length; i++) {
            ICPPTemplateArgument thisArgument = thisArguments[i];
            ICPPTemplateArgument otherArgument = otherArguments[i];
            if (!(thisArgument.toString().equals(otherArgument.toString()))) {
                return false;
            }
        }
        return true;
    }
}
