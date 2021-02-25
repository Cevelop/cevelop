package com.cevelop.includator.helpers.comparators;

import java.util.Comparator;

import org.eclipse.cdt.core.dom.ast.IASTNode;


/**
 * Note that this comparator will fail if any of the compared nodes does not yields a file location.
 *
 * @author felu
 *
 */
public class AstNodePositionOnlyComparator implements Comparator<IASTNode> {

    @Override
    public int compare(IASTNode arg0, IASTNode arg1) {
        return arg0.getFileLocation().getNodeOffset() - arg1.getFileLocation().getNodeOffset();
    }

}
