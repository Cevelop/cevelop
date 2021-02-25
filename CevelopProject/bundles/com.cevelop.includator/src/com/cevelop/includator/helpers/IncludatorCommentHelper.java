package com.cevelop.includator.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.internal.core.dom.rewrite.commenthandler.ASTCommenter;
import org.eclipse.cdt.internal.core.dom.rewrite.commenthandler.NodeCommentMap;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.resources.IncludatorProject;


@SuppressWarnings("restriction")
public class IncludatorCommentHelper {

    public static void extendLocationsWithSurroundingCommentsLocation(Collection<Suggestion<?>> suggestions) {
        initCommentMap(SuggestionHelper.groupSuggestionsPerFile(suggestions));
    }

    public static void initCommentMap(Map<IFile, Collection<Suggestion<?>>> suggestionsPerFileMap) {
        if (suggestionsPerFileMap.isEmpty()) {
            return;
        }
        IncludatorProject project = getProject(suggestionsPerFileMap);
        for (Entry<IFile, Collection<Suggestion<?>>> entry : suggestionsPerFileMap.entrySet()) {
            initCommentMap(project, entry.getKey(), entry.getValue());
        }
    }

    private static void initCommentMap(IncludatorProject project, IFile iFile, Collection<Suggestion<?>> suggestions) {
        IncludatorFile file = project.getFile(iFile);
        NodeCommentMap commentMap;
        try {
            commentMap = getCommentedNodeMap(file.getTranslationUnit());
        } catch (Exception e) {
            IncludatorPlugin.logStatus(new IncludatorStatus("Failed to initialize CommentMap becuase of Exception in CDT in file: " + file, e), file);
            commentMap = new NodeCommentMap();
        }
        for (Suggestion<?> curSuggestion : suggestions) {
            curSuggestion.initData.setNodeCommentMap(commentMap);
        }
    }

    private static IncludatorProject getProject(Map<IFile, Collection<Suggestion<?>>> suggestionsPerFileMap) {
        IProject iProject = suggestionsPerFileMap.keySet().iterator().next().getProject();
        ICProject cProject = CoreModel.getDefault().create(iProject);
        return IncludatorPlugin.getWorkspace().getProject(cProject);
    }

    public static NodeCommentMap getCommentedNodeMap(IASTTranslationUnit tu) {
        NodeCommentMap commentMap = ASTCommenter.getCommentedNodeMap(tu);
        adaptCommentMap(commentMap, tu);
        return commentMap;
    }

    private static void adaptCommentMap(NodeCommentMap commentMap, IASTTranslationUnit tu) {
        Map<IASTNode, List<IASTComment>> leadingComments = commentMap.getLeadingMap();
        if (!leadingComments.isEmpty()) {
            List<IASTComment> firstList = leadingComments.entrySet().iterator().next().getValue();
            List<IASTComment> copyrightCommentsList = getLeadingCopyrightComments(firstList);

            firstList.removeAll(copyrightCommentsList);
            for (IASTComment curComment : copyrightCommentsList) {
                commentMap.addFreestandingCommentToNode(tu, curComment);
            }
        }
    }

    private static List<IASTComment> getLeadingCopyrightComments(List<IASTComment> allComments) {
        List<IASTComment> copyrightComments = new ArrayList<>();
        int nextExpectedLineNr = 1;
        boolean containsCopyrightStr = false;
        for (IASTComment curComment : allComments) {
            if (curComment.getFileLocation().getStartingLineNumber() != nextExpectedLineNr++) {
                break;
            }
            copyrightComments.add(curComment);
            containsCopyrightStr |= curComment.getRawSignature().toLowerCase().contains("copyright");
        }
        if (!containsCopyrightStr) {
            copyrightComments.clear();
        }
        return copyrightComments;
    }
}
