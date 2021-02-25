package com.cevelop.templator.plugin.asttools.data;

import java.util.Objects;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPSpecialization;
import org.eclipse.cdt.internal.core.dom.rewrite.astwriter.ASTWriter;

import com.cevelop.templator.plugin.asttools.type.finding.RelevantNameType;


public class UnresolvedNameInfo {

    private IASTName         originalName;
    private IASTName         resolvingName;
    private IBinding         binding;
    private NameTypeKind     type;
    private RelevantNameType nameType;

    public UnresolvedNameInfo(IASTName originalName) {
        this.originalName = originalName;
    }

    public IASTName getOriginalName() {
        return originalName;
    }

    public IASTName getResolvingName() {
        return resolvingName;
    }

    public void setResolvingName(IASTName resolvingName) {
        this.resolvingName = resolvingName;
    }

    public IBinding getBinding() {
        return binding;
    }

    public void setBinding(IBinding binding) {
        setBinding(binding, false);
    }

    public void setBinding(IBinding binding, boolean alsoSetType) {
        this.binding = binding;
        if (alsoSetType) {
            setType(binding);
        }
    }

    public void setType(NameTypeKind type) {
        this.type = type;
    }

    public void setType(IBinding binding) {
        setType(NameTypeKind.getType(binding));
    }

    public NameTypeKind getType() {
        return type;
    }

    public boolean isRelevant() {
        return type != null;
    }

    public RelevantNameType getNameType() {
        return nameType;
    }

    public void setNameType(RelevantNameType nameType) {
        this.nameType = nameType;
    }

    /** For debug purposes only. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UnresolvedNameInfo ( " + getClass().getSimpleName() + " ) [" + hashCode() + "] \n");
        sb.append("\t originalName: " + originalName + " [" + originalName.hashCode() + "] \n");
        sb.append("\t resolvingName: " + Objects.toString(resolvingName) + " [" + resolvingName.hashCode() + "] \n");
        sb.append("\t binding: " + Objects.toString(binding) + " [" + binding.hashCode() + "] \n");
        sb.append("\t argument map: ");
        if (binding instanceof ICPPSpecialization) {
            sb.append(((ICPPSpecialization) binding).getTemplateParameterMap());
        } else {
            sb.append('-');
        }
        sb.append('\n');
        sb.append("\t type: " + type + " [" + type.hashCode() + "] \n");
        sb.append("\t nameType: " + nameType.getClass().getSimpleName() + " [" + nameType.hashCode() + "] \n");
        sb.append("\t grandparent: \n");
        sb.append(new ASTWriter().write(resolvingName.getParent().getParent()).replaceAll("(?m)^", "\t\t") + '\n');
        return sb.toString();
    }
}
