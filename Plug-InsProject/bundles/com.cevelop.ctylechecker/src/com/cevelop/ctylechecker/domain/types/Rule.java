package com.cevelop.ctylechecker.domain.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;

import com.google.gson.annotations.Expose;

import com.cevelop.ctylechecker.common.ConceptNames;
import com.cevelop.ctylechecker.domain.ExpressionType;
import com.cevelop.ctylechecker.domain.IConcept;
import com.cevelop.ctylechecker.domain.IExpression;
import com.cevelop.ctylechecker.domain.IGroupExpression;
import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.domain.ISingleExpression;
import com.cevelop.ctylechecker.domain.types.util.Concepts;
import com.cevelop.ctylechecker.domain.types.util.Qualifiers;
import com.cevelop.ctylechecker.domain.types.util.ReservedNamesChecker;
import com.cevelop.ctylechecker.domain.types.util.TypeChecker;


public class Rule extends AbstractCtyleElement implements IRule {

    @Expose
    private List<IConcept>    checkedConcepts;
    @Expose
    private List<IExpression> predefinedExpressions;
    @Expose
    private List<IExpression> customExpressions;
    @Expose
    private String            message;
    private List<IExpression> reportingExpressions;
    private TypeChecker       typeChecker;

    public Rule(String pRuleName) {
        this(pRuleName, true);
    }

    public Rule(String pRuleName, Boolean isEnabled) {
        super(pRuleName, isEnabled);
        setMessage("");
        setPredefinedExpressions(new ArrayList<>());
        setCustomExpressions(new ArrayList<>());
        setCheckedConcepts(new ArrayList<>());
    }

    /*
     * (non-Javadoc)
     * @see com.cevelop.ctylechecker.domain.IRule#getPredefinedExpressions()
     */
    @Override
    public List<IExpression> getPredefinedExpressions() {
        return predefinedExpressions;
    }

    @Override
    public void setPredefinedExpressions(List<IExpression> pPredefinedExpressions) {
        predefinedExpressions = pPredefinedExpressions;
    }

    /*
     * (non-Javadoc)
     * @see com.cevelop.ctylechecker.domain.IRule#getCustomExpressions()
     */
    @Override
    public List<IExpression> getCustomExpressions() {
        return customExpressions;
    }

    @Override
    public void setCustomExpressions(List<IExpression> pCustomExpressions) {
        customExpressions = pCustomExpressions;
    }

    /*
     * (non-Javadoc)
     * @see com.cevelop.ctylechecker.domain.IRule#getMessage()
     */
    @Override
    public String getMessage() {
        return reportingExpressions != null ? (!reportingExpressions.isEmpty() ? message + prepareExpressionMessage() : message) : message;
    }

    private String prepareExpressionMessage() {
        String msg = ".\r\n" + "Failed to fulfill Expression(s): ";
        for (IExpression expression : reportingExpressions) {
            msg = populateMessageWithExpression(msg, expression);
        }
        reportingExpressions = new ArrayList<>();
        return msg;
    }

    private String populateMessageWithExpression(String msg, IExpression expression) {
        if (expression.getType().equals(ExpressionType.SINGLE)) {
            ISingleExpression lExpression = (ISingleExpression) expression;
            msg += ".\r\n" + (lExpression.shouldMatch() ? "Matches " : "Doesn't match ") + lExpression.getName();
        }
        if (expression.getType().equals(ExpressionType.GROUP)) {
            IGroupExpression group = (IGroupExpression) expression;
            for (IExpression lExpression : group.getExpressions()) {
                msg = populateMessageWithExpression(msg, lExpression);
                msg += ", Group: " + group.getName();
            }
        }
        return msg;
    }

    @Override
    public void setMessage(String pMessage) {
        this.message = pMessage;
    }

    /*
     * (non-Javadoc)
     * @see com.cevelop.ctylechecker.domain.IRule#matches(org.eclipse.cdt.core.dom.ast.IASTName)
     */
    @Override
    public Boolean matches(IASTName pName) {
        if (ReservedNamesChecker.check(pName.toString())) {
            return true;
        }
        Boolean matched = matchesExpressions(pName.toString(), predefinedExpressions);
        if (matched) {
            matched = matchesExpressions(pName.toString(), customExpressions);
        }
        return matched;
    }

    /*
     * (non-Javadoc)
     * @see com.cevelop.ctylechecker.domain.IRule#matches(java.lang.String)
     */
    @Override
    public Boolean matches(String pName) {
        Boolean matched = matchesExpressions(pName, predefinedExpressions);
        if (matched) {
            matched = matchesExpressions(pName, customExpressions);
        }
        return matched;
    }

    private Boolean matchesExpressions(String pName, List<IExpression> pExpressions) {
        reportingExpressions = new ArrayList<>();
        for (IExpression expression : pExpressions) {
            checkExpression(pName, expression);
        }
        return reportingExpressions.isEmpty();
    }

    private void checkExpression(String pName, IExpression expression) {
        if (!expression.check(pName)) {
            reportingExpressions.add(expression);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.cevelop.ctylechecker.domain.IRule#isApplicableToFile(java.lang.Boolean)
     */
    @Override
    public Boolean isApplicableToFile(Boolean pIsHeaderUnit) {
        Optional<IConcept> oConcept = Optional.empty();
        if (pIsHeaderUnit) {
            oConcept = Concepts.getConcept(ConceptNames.NAME_HEADER_FILE);
        } else {
            oConcept = Concepts.getConcept(ConceptNames.NAME_SOURCE_FILE);
        }
        if (oConcept.isPresent()) {
            if (checkedConcepts.contains(oConcept.get())) {
                int index = checkedConcepts.indexOf(new Concept(oConcept.get().getType()));
                IConcept concept = checkedConcepts.get(index);
                return concept.getQualifiers().isEmpty() || concept.getQualifiers().contains(Qualifiers.FILE_BODY);
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.cevelop.ctylechecker.domain.IRule#isApplicableToFileEnding(java.lang.Boolean)
     */
    @Override
    public Boolean isApplicableToFileEnding(Boolean pIsHeaderUnit) {
        Optional<IConcept> oConcept = Optional.empty();
        if (pIsHeaderUnit) {
            oConcept = Concepts.getConcept(ConceptNames.NAME_HEADER_FILE);
        } else {
            oConcept = Concepts.getConcept(ConceptNames.NAME_SOURCE_FILE);
        }
        if (oConcept.isPresent()) {
            if (checkedConcepts.contains(oConcept.get())) {
                IConcept concept = checkedConcepts.get(checkedConcepts.indexOf(new Concept(oConcept.get().getType())));
                return concept.getQualifiers().isEmpty() || concept.getQualifiers().contains(Qualifiers.FILE_ENDING);
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.cevelop.ctylechecker.domain.IRule#isApplicable(org.eclipse.cdt.core.dom.ast.IASTName)
     */
    @Override
    public Boolean isApplicable(IASTName pName) {
        IBinding resolvedBinding = pName.resolveBinding();
        if (resolvedBinding != null) {
            if (checkedConcepts == null) {
                checkedConcepts = new ArrayList<>();
            }
            if (checkedConcepts.contains(new Concept(resolvedBinding.getClass().getSimpleName()))) {
                IConcept binding = checkedConcepts.get(checkedConcepts.indexOf(new Concept(resolvedBinding.getClass().getSimpleName())));
                return getTypeChecker().isTypeApplicable(resolvedBinding, binding);
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.cevelop.ctylechecker.domain.IRule#getCheckedConcepts()
     */
    @Override
    public List<IConcept> getCheckedConcepts() {
        return checkedConcepts;
    }

    @Override
    public void setCheckedConcepts(List<IConcept> checkedBindings) {
        this.checkedConcepts = checkedBindings;
    }

    @Override
    public String toString() {
        return "id: " + getId().toString() + "\n" + "name: " + getName() + "\n" + "predefinedExpressions: " + predefinedExpressions + "\n" + "\n" +
               "customExpressions: " + customExpressions + "\n" + "message: " + message + "\n" + "enabled: " + isEnabled().toString() + "\n" +
               checkedConcepts;
    }

    public TypeChecker getTypeChecker() {
        if (typeChecker == null) {
            typeChecker = new TypeChecker();
        }
        return typeChecker;
    }
}
