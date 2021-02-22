package com.cevelop.templator.plugin.asttools.type.finding;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPAliasTemplateInstance;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPVariableInstance;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPFieldSpecialization;

import com.cevelop.templator.plugin.logger.TemplatorException;


public abstract class RelevantNameType {

    protected IASTName definitionName;
    protected IASTName typeName;
    protected IBinding originalBinding;
    protected IBinding typeBinding;

    protected RelevantNameType(IASTName definitionName) {
        this.definitionName = definitionName;
        originalBinding = this.definitionName.resolveBinding();
    }

    protected abstract IASTName getTypeFromDefinition() throws TemplatorException;

    public static RelevantNameType create(IASTName definitionName) throws TemplatorException {
        RelevantNameType type = null;
        IBinding definitionBinding = definitionName.resolveBinding();
        if (definitionBinding instanceof ICPPParameter) {
            type = new Parameter(definitionName);
        } else if (definitionBinding instanceof IVariable && !(definitionBinding instanceof ICPPVariableInstance) &&
                   !(definitionBinding instanceof CPPFieldSpecialization)) {
                       type = new Variable(definitionName);
                   } else if (definitionBinding instanceof ITypedef && !(definitionBinding instanceof ICPPAliasTemplateInstance)) {
                       type = new Alias(definitionName);
                   } else {
                       type = new AlreadyRelevantType(definitionName);
                   }
        type.typeName = type.getTypeFromDefinition();
        if (type.typeName != null) {
            type.typeBinding = type.typeName.resolveBinding();
        }
        return type;
    }

    // von NameTypeCache
    // private Map<IASTName, NameToType> nameCache = new HashMap<>();
    // private Map<IBinding, NameToType> bindingCache = new HashMap<>();
    //
    // public NameToType getFor(unresolvedNameInfo statement) {
    // NameToType result = getFor(statement.getResolvingName());
    // if (result == null) {
    // result = getFor(statement.getOriginalName().resolveBinding());
    // if (result == null) {
    // result = getFor(statement.getBinding());
    // }
    // }
    //
    // return result;
    // }

    public IASTName getDefinitionName() {
        return definitionName;
    }

    public IASTName getTypeName() {
        return typeName;
    }

    public IBinding getOriginalBinding() {
        return originalBinding;
    }

    public IBinding getTypeBinding() {
        return typeBinding;
    }

    /** For debug purposes only. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RelevantNameType ( " + getClass().getSimpleName() + " ) [" + hashCode() + "] \n");
        sb.append("\t definitionName: " + definitionName + " [" + definitionName.hashCode() + "] \n");
        sb.append("\t originalBinding: " + originalBinding + " [" + originalBinding.hashCode() + "] \n");
        sb.append("\t typeName: " + typeName + " [" + typeName.hashCode() + "] \n");
        sb.append("\t typeBinding: " + typeBinding + " [" + typeBinding.hashCode() + "] \n");
        return sb.toString();
    }

}
