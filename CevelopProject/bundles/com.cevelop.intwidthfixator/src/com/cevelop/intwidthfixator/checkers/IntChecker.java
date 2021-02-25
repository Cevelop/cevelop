package com.cevelop.intwidthfixator.checkers;

import org.eclipse.cdt.codan.core.model.IProblemLocation;
import org.eclipse.cdt.codan.core.model.IProblemLocationFactory;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.jface.text.IRegion;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.SimpleChecker;

import com.cevelop.intwidthfixator.helpers.IdHelper.ProblemId;
import com.cevelop.intwidthfixator.helpers.PositionHelper;
import com.cevelop.intwidthfixator.visitors.VisitorArgs;
import com.cevelop.intwidthfixator.visitors.intvisitors.IntVisitor;


public class IntChecker extends SimpleChecker<ProblemId> {

    @Override
    protected IntVisitor createVisitor() {
        return new IntVisitor(this, VisitorArgs.REPORT_CASTS, VisitorArgs.REPORT_FUNCTIONS, VisitorArgs.REPORT_TEMPLATES, VisitorArgs.REPORT_TYPEDEFS,
                VisitorArgs.REPORT_VARIABLES);
    }

    @Override
    protected IProblemLocation locationHook(final IASTNode node) {
        final IProblemLocationFactory factory = getRuntime().getProblemLocationFactory();
        final IRegion region = PositionHelper.getRegionOfTypeInFile((ICPPASTSimpleDeclSpecifier) node);
        return factory.createProblemLocation(getFile(), region.getOffset(), region.getOffset() + region.getLength(),
                ((ICPPASTSimpleDeclSpecifier) node).getFileLocation().getStartingLineNumber());
    }

}
