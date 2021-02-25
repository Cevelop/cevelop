package com.cevelop.includator.helpers;

import java.util.LinkedHashMap;
import java.util.Map;

import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.ContainerScopedAlgorithm;
import com.cevelop.includator.optimizer.FileScopedAlgorithm;
import com.cevelop.includator.optimizer.ProjectScopedAlgorithm;
import com.cevelop.includator.optimizer.ScopedAlgorithm;
import com.cevelop.includator.startingpoints.AlgorithmStartingPoint;


public class AlgorithmScopeHelper {

    private static Map<AlgorithmScope, ScopedAlgorithm> scopedAlgorithmMap;

    static {
        scopedAlgorithmMap = new LinkedHashMap<>();
        scopedAlgorithmMap.put(AlgorithmScope.EDITOR_SCOPE, new FileScopedAlgorithm());
        scopedAlgorithmMap.put(AlgorithmScope.FILE_SCOPE, new FileScopedAlgorithm());
        scopedAlgorithmMap.put(AlgorithmScope.CONTAINER_SCOPE, new ContainerScopedAlgorithm());
        scopedAlgorithmMap.put(AlgorithmScope.PROJECT_SCOPE, new ProjectScopedAlgorithm());
    }

    public static Algorithm getScopedAlgorithm(AlgorithmStartingPoint startingPoint, Algorithm algorithm) {
        AlgorithmScope startingPointScope = startingPoint.getScope();
        AlgorithmScope algScope = algorithm.getScope();
        if (haveSameScope(startingPointScope, algScope)) { // perfect match, no need of scoped alg
            return algorithm;
        }
        // Alg will use project anyway and ignore active file and affected files (if present at all)
        if (AlgorithmScope.PROJECT_SCOPE.equals(algScope)) {
            return algorithm;
        }
        // should there be any container scoped algorithms in the future (none up to now), enhancement is needed here.

        if (AlgorithmScope.EDITOR_SCOPE.equals(algScope)) {
            return getAlgorithmForScope(startingPointScope, algorithm);
        }
        return null;
    }

    private static boolean haveSameScope(AlgorithmScope startingPointScope, AlgorithmScope algScope) {
        if (algScope.equals(startingPointScope)) {
            return true;
        }
        return isFileOrEditorScope(startingPointScope) && isFileOrEditorScope(algScope);
    }

    private static boolean isFileOrEditorScope(AlgorithmScope scope) {
        return scope == AlgorithmScope.FILE_SCOPE || scope == AlgorithmScope.EDITOR_SCOPE;
    }

    public static Algorithm getAlgorithmForScope(AlgorithmScope targetScope, Algorithm algorithm) {
        ScopedAlgorithm scopedAlg = scopedAlgorithmMap.get(targetScope);
        scopedAlg.setWrappedAlg(algorithm);
        return scopedAlg;
    }
}
