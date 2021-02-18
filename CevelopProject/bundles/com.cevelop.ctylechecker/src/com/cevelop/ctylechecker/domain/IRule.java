package com.cevelop.ctylechecker.domain;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTName;


public interface IRule extends ICtyleElement {

    List<IExpression> getPredefinedExpressions();

    void setPredefinedExpressions(List<IExpression> pExpressions);

    List<IExpression> getCustomExpressions();

    void setCustomExpressions(List<IExpression> pExpressions);

    String getMessage();

    void setMessage(String pMessage);

    Boolean matches(IASTName pName);

    Boolean matches(String pName);

    Boolean isApplicableToFile(Boolean pIsHeaderUnit);

    Boolean isApplicableToFileEnding(Boolean pIsHeaderUnit);

    Boolean isApplicable(IASTName pName);

    List<IConcept> getCheckedConcepts();

    void setCheckedConcepts(List<IConcept> pConcepts);

}
