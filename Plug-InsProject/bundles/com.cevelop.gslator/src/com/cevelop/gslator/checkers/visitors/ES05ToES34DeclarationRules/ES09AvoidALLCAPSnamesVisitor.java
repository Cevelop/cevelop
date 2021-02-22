package com.cevelop.gslator.checkers.visitors.ES05ToES34DeclarationRules;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.core.resources.IResource;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.ES05ToES34DeclarationRules.ES09AvoidALLCAPSnamesChecker;
import com.cevelop.gslator.checkers.visitors.BaseVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.infos.GslatorInfo;


public class ES09AvoidALLCAPSnamesVisitor extends BaseVisitor {

    public ES09AvoidALLCAPSnamesVisitor(BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitNames = true;
        shouldVisitTranslationUnit = true;
    }

    @Override
    public int visit(IASTTranslationUnit tu) {
        if (!isMacroCheckEnabled(tu)) {
            return PROCESS_CONTINUE;
        }

        IASTPreprocessorMacroDefinition[] list = tu.getMacroDefinitions();
        for (IASTPreprocessorMacroDefinition macro : list) {
            if (!isALLCAPS(macro.getName())) { //TODO Would some Ignore functionality make sense? How?
                checker.reportProblem(ProblemId.P_ES09, macro, new GslatorInfo("Macros have to be ALL_CAPS"));
            }
        }

        return super.visit(tu);
    }

    @Override
    public int visit(IASTName name) {
        if (name.isDeclaration() && isALLCAPS(name) && nodeHasNoIgnoreAttribute(this, name)) {
            if (!isSingleCharTemplateParam(name) && !isSpecialMember(name)) {
                checker.reportProblem(ProblemId.P_ES09, name, new GslatorInfo("Avoid ALL_CAPS names"));
            }
        }
        return super.visit(name);
    }

    private static boolean isSingleCharTemplateParam(final IASTName astName) {
        return astName.getParent() instanceof ICPPASTTemplateParameter && astName.toString().length() == 1;
    }

    private static boolean isSpecialMember(final IASTName astName) {
        IBinding nameBinding = astName.resolveBinding();

        return nameBinding instanceof ICPPConstructor || nameBinding instanceof ICPPMethod && nameBinding.getName().startsWith("~");
    }

    private static boolean isALLCAPS(IASTName astName) {
        String name = astName.toString();
        return name.chars().anyMatch(Character::isLetter) && name.equals(name.toUpperCase());
    }

    private boolean isMacroCheckEnabled(IASTTranslationUnit ast) {
        IResource res = ast.getOriginatingTranslationUnit().getResource();
        return (boolean) checker.getPreference(res, ES09AvoidALLCAPSnamesChecker.PREF_MARK_MACROS);
    }
}
