package com.cevelop.templator.plugin.asttools.templatearguments;

import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IValue;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplatedTypeTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateArgument;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameterMap;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPTemplateParameterMap;

import com.cevelop.templator.plugin.logger.TemplatorLogger;


public class TemplateArgumentMap extends CPPTemplateParameterMap {

    public TemplateArgumentMap(CPPTemplateParameterMap other) {
        super(other);
    }

    public TemplateArgumentMap(int initialSize) {
        super(initialSize);
    }

    public TemplateArgumentMap() {
        super(0);
    }

    public static TemplateArgumentMap copy(ICPPTemplateParameterMap templateParameterMap) {
        if (templateParameterMap instanceof TemplateArgumentMap) {
            return (TemplateArgumentMap) templateParameterMap;
        } else {
            return new TemplateArgumentMap((CPPTemplateParameterMap) templateParameterMap);
        }
    }

    private static ICPPTemplateParameter getParameterBinding(ICPPASTTemplateParameter astParameter) {
        IASTName paramName = null;

        if (astParameter instanceof ICPPASTParameterDeclaration) {
            ICPPASTParameterDeclaration parameterDeclaration = (ICPPASTParameterDeclaration) astParameter.getOriginalNode();
            ICPPASTDeclarator declarator = parameterDeclaration.getDeclarator();
            paramName = declarator.getName();
        } else if (astParameter instanceof ICPPASTSimpleTypeTemplateParameter) {
            ICPPASTSimpleTypeTemplateParameter typeParameter = (ICPPASTSimpleTypeTemplateParameter) astParameter.getOriginalNode();
            paramName = typeParameter.getName();
        } else if (astParameter instanceof ICPPASTTemplatedTypeTemplateParameter) {
            ICPPASTTemplatedTypeTemplateParameter typeParameter = (ICPPASTTemplatedTypeTemplateParameter) astParameter.getOriginalNode();
            paramName = typeParameter.getName();
        } else {
            TemplatorLogger.logError("ICPPASTTemplateParameter was of unexpected type " + astParameter.getClass().toString(), null);
            return null;
        }

        IBinding binding = paramName.resolveBinding();
        if (binding instanceof ICPPTemplateParameter) {
            return (ICPPTemplateParameter) binding;
        }

        return null;
    }

    public ICPPTemplateArgument getArgument(ICPPASTTemplateParameter astParameter) {
        return getArgument(getParameterBinding(astParameter));
    }

    public ICPPTemplateArgument[] getPackExpansion(ICPPASTTemplateParameter astParameter) {
        return getPackExpansion(getParameterBinding(astParameter));
    }

    public String getArgumentString(ICPPASTTemplateParameter astParameter) {
        return getArgumentString(getArgument(astParameter));
    }

    /*
     * The default ASTTypeUtil.getArgumentString writes the data type + value for non-type values. So for 3 it writes
     * int3 which we do not want.
     */
    public static String getArgumentString(ICPPTemplateArgument argument) {
        String argumentString = null;
        if (argument != null) {
            IValue val = argument.getNonTypeValue();
            if (val != null) {
                argumentString = String.valueOf(val.getSignature());
            } else {
                argumentString = ASTTypeUtil.getArgumentString(argument, true);
            }
        }
        return argumentString;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return getAllParameterPositions().length;
    }

    @Override
    public boolean equals(Object obj) {
        TemplateArgumentMap other = (TemplateArgumentMap) obj;
        if (size() != other.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            ICPPTemplateArgument thisArg = getArgument(i);
            ICPPTemplateArgument otherArg = other.getArgument(i);
            if (!thisArg.equals(otherArg)) {
                return false;
            }
        }
        return true;
    }
}
