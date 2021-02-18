package com.cevelop.ctylechecker.service.types;

import org.eclipse.cdt.codan.core.model.IProblemLocation;
import org.eclipse.cdt.codan.core.model.IProblemLocationFactory;
import org.eclipse.cdt.codan.internal.core.model.ProblemLocationFactory;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor;
import org.eclipse.core.resources.IFile;


@SuppressWarnings("restriction")
public class InfrastructureService {

    private InfrastructureService() {}

    public static <T extends IASTNode> T findAncestorWithType(IASTNode node, Class<T> type) {
        return CPPVisitor.findAncestorWithType(node, type);
    }

    @SuppressWarnings("unchecked")
    public static <T> T as(Object o, Class<T> type) {
        if (type.isInstance(o)) {
            return (T) o;
        }
        return null;
    }

    public static IProblemLocation createProblemLocation(IASTNodeLocation location, IFile file) {
        IASTFileLocation fileLocation = location.asFileLocation();
        IProblemLocationFactory plf = new ProblemLocationFactory();
        int nodeOffset = fileLocation.getNodeOffset();
        int nodeEnd = nodeOffset + fileLocation.getNodeLength();
        int lineNumber = fileLocation.getStartingLineNumber();
        return plf.createProblemLocation(file, nodeOffset, nodeEnd, lineNumber);
    }
}
