package com.cevelop.intwidthfixator.visitors;

import java.util.Arrays;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.ISimpleReporter;
import ch.hsr.ifs.iltis.cpp.core.ast.visitor.SimpleVisitor;
// import ch.hsr.ifs.iltis.cpp.core.ast.visitor.helper.IVisitorArgument;

import com.cevelop.intwidthfixator.helpers.IdHelper.ProblemId;


public abstract class AbstractVisitor extends SimpleVisitor<ProblemId, VisitorArgs> {

    protected boolean reportCasts;
    protected boolean reportFunctions;
    protected boolean reportTemplates;
    protected boolean reportTypedefs;
    protected boolean reportVariables;

    public AbstractVisitor(ISimpleReporter<ProblemId> reporter, final VisitorArgs... args) {
        super(reporter, args);

        reportCasts = Arrays.asList(args).contains(VisitorArgs.REPORT_CASTS);
        reportFunctions = Arrays.asList(args).contains(VisitorArgs.REPORT_FUNCTIONS);
        reportTemplates = Arrays.asList(args).contains(VisitorArgs.REPORT_TEMPLATES);
        reportTypedefs = Arrays.asList(args).contains(VisitorArgs.REPORT_TYPEDEFS);
        reportVariables = Arrays.asList(args).contains(VisitorArgs.REPORT_VARIABLES);

        setReportCasts();
        setReportFunctions();
        setReportTemplates();
        setReportTypedefs();
        setReportVariables();
    }

    {
        /* Are set in setters for reporting */
        shouldVisitExpressions = false;
        shouldVisitNames = false;
        shouldVisitDeclarations = false;
        shouldVisitDeclarators = false;
        shouldVisitParameterDeclarations = false;
        shouldVisitTemplateParameters = false;
        shouldVisitDeclSpecifiers = false;
    }

    public void setReportCasts() {
        shouldVisitExpressions = reportCasts || reportVariables;
    }

    public void setReportFunctions() {
        shouldVisitNames = reportFunctions || reportTemplates;
        shouldVisitParameterDeclarations = reportFunctions || reportTemplates;
        shouldVisitDeclarators = reportFunctions;
        shouldVisitDeclarations = reportFunctions || reportTypedefs || reportVariables;
    }

    public void setReportTemplates() {
        shouldVisitNames = reportFunctions || reportTemplates;
        shouldVisitTemplateParameters = reportTemplates;
        shouldVisitParameterDeclarations = reportFunctions || reportTemplates;
    }

    public void setReportTypedefs() {
        shouldVisitDeclSpecifiers = reportTypedefs;
        shouldVisitDeclarations = reportFunctions || reportTypedefs || reportVariables;
    }

    public void setReportVariables() {
        shouldVisitExpressions = reportCasts || reportVariables;
        shouldVisitDeclarations = reportFunctions || reportTypedefs || reportVariables;
    }

}
