package com.cevelop.codeanalysator.autosar.quickfix;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCapture;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression.CaptureDefault;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPVariable;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.codeanalysator.autosar.util.LambdaHelper;
import com.cevelop.codeanalysator.core.quickfix.BaseQuickFix;


public class DoNotImplicitlyCaptureVariablesQuickFix extends BaseQuickFix {

    public DoNotImplicitlyCaptureVariablesQuickFix(String label) {
        super(label);
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        if (!(markedNode instanceof ICPPASTLambdaExpression)) return;
        ICPPASTLambdaExpression lambdaExpression = (ICPPASTLambdaExpression) markedNode;

        ICPPASTLambdaExpression newLambdaExpression = (ICPPASTLambdaExpression) lambdaExpression.copy(CopyStyle.withLocations);
        newLambdaExpression.setCaptureDefault(CaptureDefault.UNSPECIFIED);

        Set<ICPPVariable> implicitlyCapturedVariables = LambdaHelper.getImplicitlyCapturedVariables(lambdaExpression);
        if (implicitlyCapturedVariables.remove(null)) {
            boolean isThisCaptured = false;
            for (ICPPASTCapture capture : lambdaExpression.getCaptures()) {
                if (capture.getIdentifier() == null) {
                    isThisCaptured = true;
                    break;
                }
            }
            if (!isThisCaptured) {
                ICPPASTCapture capture = factory.newCapture();
                capture.setIsByReference(true);
                capture.setIdentifier(null);
                newLambdaExpression.addCapture(capture);
            }
        }
        List<ICPPVariable> lexographiclySortedImplicitlyCapturedVariables = new ArrayList<>(implicitlyCapturedVariables);
        lexographiclySortedImplicitlyCapturedVariables.sort((x, y) -> x.getName().compareTo(y.getName()));
        boolean isByReferenceCaptureDefault = lambdaExpression.getCaptureDefault() == CaptureDefault.BY_REFERENCE;
        for (ICPPVariable variable : lexographiclySortedImplicitlyCapturedVariables) {
            ICPPASTCapture capture = factory.newCapture();
            capture.setIsByReference(isByReferenceCaptureDefault);
            IASTName variableName = factory.newName(variable.getName());
            capture.setIdentifier(variableName);
            newLambdaExpression.addCapture(capture);
        }

        hRewrite.replace(lambdaExpression, newLambdaExpression, null);
    }
}
