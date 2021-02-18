package com.cevelop.templator.plugin.asttools.resolving.nametype;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPSpecialization;

import com.cevelop.templator.plugin.asttools.data.AbstractResolvedNameInfo;


public class TypeNameToType {

    protected IASTName                 typeName;
    protected ICPPSpecialization       specialization;
    protected IType                    type;
    protected AbstractResolvedNameInfo currentContext;
    protected boolean                  completelyResolved;

    public TypeNameToType(IASTName resolvingName) {
        this.typeName = resolvingName;
    }

    public TypeNameToType(IASTName resolvingName, ICPPSpecialization specialization) {
        this.typeName = resolvingName;
        setSpecialization(specialization);
    }

    public TypeNameToType(IASTName typeName, AbstractResolvedNameInfo currentContext) {
        this.typeName = typeName;
        this.currentContext = currentContext;
    }

    public TypeNameToType(IASTName typeName, ICPPSpecialization specialization, AbstractResolvedNameInfo currentContext) {
        super();
        this.typeName = typeName;
        setSpecialization(specialization);
        this.currentContext = currentContext;
    }

    public TypeNameToType(TypeNameToType other) {
        setAll(other);
    }

    public IType getType() {
        return type;
    }

    public void setType(IType type) {
        this.type = type;
    }

    public void setTypeName(IASTName typeName) {
        this.typeName = typeName;
    }

    public IASTName getTypeName() {
        return typeName;
    }

    public void setSpecialization(ICPPSpecialization specialization) {
        this.specialization = specialization;
        if (specialization instanceof IType) {
            type = (IType) specialization;
        }
    }

    public ICPPSpecialization getSpecialization() {
        return specialization;
    }

    public AbstractResolvedNameInfo getCurrentContext() {
        return currentContext;
    }

    public void setCurrentContext(AbstractResolvedNameInfo currentContext) {
        this.currentContext = currentContext;
    }

    public boolean isCompletelyResolved() {
        return completelyResolved;
    }

    public void setCompletelyResolved(boolean completelyResolved) {
        this.completelyResolved = completelyResolved;
    }

    public void setAll(TypeNameToType other) {
        this.typeName = other.typeName;
        this.specialization = other.specialization;
        this.type = other.type;
        this.currentContext = other.currentContext;
        this.completelyResolved = other.completelyResolved;
    }

    @Override
    public String toString() {
        return "typeName=" + typeName + ", specialization=" + specialization + ", type=" + type + ", currentContext=" + currentContext;
    }

}
