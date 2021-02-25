package com.cevelop.ctylechecker;

import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.cdt.codan.core.model.IProblemLocation;
import org.eclipse.cdt.codan.core.model.IProblemLocationFactory;
import org.eclipse.cdt.codan.internal.core.model.ProblemLocationFactory;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.cdt.internal.core.dom.parser.ASTQueries;
import org.eclipse.core.resources.IFile;


@SuppressWarnings("restriction")
public class Infrastructure {

    private Infrastructure() {}

    public static <T extends IASTNode> T findAncestorWithType(IASTNode node, Class<T> type) {
        return ASTQueries.findAncestorWithType(node, type);
    }

    @SuppressWarnings("unchecked")
    public static <T> T as(Object o, Class<T> type) {
        if (type.isInstance(o)) {
            return (T) o;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> asOpt(Object o, Class<T> type) {
        if (type.isInstance(o)) {
            return Optional.of((T) o);
        }
        return Optional.empty();
    }

    public static <T> Stream<T> optToStream(Optional<T> optional) {
        if (optional.isPresent()) {
            return Stream.of(optional.get());
        } else {
            return Stream.empty();
        }
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
