package com.cevelop.includator.optimizer;

import org.eclipse.cdt.internal.core.dom.rewrite.commenthandler.NodeCommentMap;

import com.cevelop.includator.resources.IncludatorFile;


/**
 * This class is used by each Suggestion. It should contain all arguments which are not supposed to remain in the suggestion. Like this, the
 * includator framework can make sure that (1) the suggestion is only initialized when really necessary and that, when initialized or disposed, all
 * initialization-data members are disposed.
 *
 * When overriding,
 */
@SuppressWarnings("restriction")
public class SuggestionInitializationData {

    IncludatorFile file;

    NodeCommentMap commentMap;

    public SuggestionInitializationData(IncludatorFile file) {
        this.file = file;
    }

    public void dispose() {
        file = null;
        commentMap = null;
    }

    public void setNodeCommentMap(NodeCommentMap commentMap) {
        this.commentMap = commentMap;
    }

    public IncludatorFile getFile() {
        return file;
    }
}
