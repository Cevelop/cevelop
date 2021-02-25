package com.cevelop.charwars.checkers;

import org.eclipse.cdt.codan.core.CodanRuntime;
import org.eclipse.cdt.codan.core.model.IProblemLocation;
import org.eclipse.cdt.codan.core.model.IProblemLocationFactory;
import org.eclipse.cdt.core.dom.ast.IASTMacroExpansionLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.core.resources.IFile;

import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;

import com.cevelop.charwars.constants.ProblemId;


public class ProblemReport {

    private ProblemId        problemID;
    private IProblemLocation problemLocation;
    private MarkerInfo<?>    info;

    private ProblemReport(ProblemId problemID, IProblemLocation problemLocation, MarkerInfo<?> info) {
        this.problemID = problemID;
        this.problemLocation = problemLocation;
        this.info = info;
    }

    public static ProblemReport create(IFile file, ProblemId problemID, IASTNode markedNode, MarkerInfo<?> info) {
        if (!isValidMarkedNode(markedNode)) {
            return null;
        }

        return new ProblemReport(problemID, createProblemLocation(file, markedNode), info);
    }

    private static boolean isValidMarkedNode(IASTNode markedNode) {
        if (markedNode == null || markedNode.getNodeLocations().length == 0) {
            return false;
        }

        IASTNodeLocation[] nodeLocations = markedNode.getNodeLocations();
        return !(nodeLocations[0] instanceof IASTMacroExpansionLocation);
    }

    private static IProblemLocation createProblemLocation(IFile file, IASTNode markedNode) {
        IASTNodeLocation[] nodeLocations = markedNode.getNodeLocations();
        IASTNodeLocation firstLoc = nodeLocations[0];
        int start = firstLoc.getNodeOffset();
        int end = firstLoc.getNodeOffset() + markedNode.getRawSignature().length();
        int line = firstLoc.asFileLocation().getStartingLineNumber();

        IProblemLocationFactory problemLocationFactory = CodanRuntime.getInstance().getProblemLocationFactory();
        return problemLocationFactory.createProblemLocation(file, start, end, line);
    }

    public ProblemId getProblemID() {
        return problemID;
    }

    public IProblemLocation getProblemLocation() {
        return problemLocation;
    }

    public MarkerInfo<?> getInfo() {
        return info;
    }
}
