package com.cevelop.namespactor.refactoring.rewrite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;


/**
 * Rewrite store to register changes for an AST. The class stores all rewrites created during a refactoring process and selects the appropriate one
 * for the current change, by considering the rewrite
 * root of a change. If a change creates a new rewrite, it is saved inside the store for further usage.
 *
 * @author ythrier(at)hsr.ch
 */
public class ASTRewriteStore {

    private final ModificationCollector     collector;
    private final Map<IASTNode, ASTRewrite> rewrites = new HashMap<>();
    private final List<IASTChange>          changes  = new ArrayList<>();

    /**
     * Create the rewrite store.
     *
     * @param collector
     * Modification collector.
     */
    public ASTRewriteStore(ModificationCollector collector) {
        this.collector = collector;
    }

    /**
     * Add an insert change to the store.
     *
     * @param root
     * Root node for the insertion.
     * @param newNode
     * New node to insert.
     * @param insertionPoint
     * Insertion point.
     */
    public void addInsertChange(IASTNode root, IASTNode newNode, IASTNode insertionPoint) {
        changes.add(new ASTInsertChange(root, newNode, insertionPoint));
    }

    /**
     * Add a remove change to the store.
     *
     * @param node
     * Node to remove.
     */
    public void addRemoveChange(IASTNode node) {
        changes.add(new ASTRemoveChange(node));
    }

    /**
     * Add a replace change to the store.
     *
     * @param node
     * Node to replace.
     * @param replacement
     * Replacement node.
     */
    public void addReplaceChange(IASTNode node, IASTNode replacement) {
        changes.add(new ASTReplaceChange(node, replacement));
    }

    /**
     * Perform all registered changes.
     */
    public void performChanges() {
        for (IASTChange change : changes) {
            ASTRewrite newRewrite = change.apply(getRewrite(change));
            if (newRewrite != null) {
                register(change.getChangeRoot(), newRewrite);
            }
        }
    }

    private void register(IASTNode node, ASTRewrite rewrite) {
        rewrites.put(node, rewrite);
    }

    private ASTRewrite getRewrite(IASTChange change) {
        enforceRewriteExistance(change);
        return findRewrite(change);
    }

    private ASTRewrite findRewrite(IASTChange change) {
        return rewrites.get(lookupNodeInRewrites(change.getRewriteRoot()));
    }

    private IASTNode lookupNodeInRewrites(IASTNode node) {
        IASTNode lookup = node;
        while (!(lookup == null) && !hasFirsthandRewriteFor(lookup)) {
            lookup = lookup.getParent();
        }
        return lookup;
    }

    private void enforceRewriteExistance(IASTChange change) {
        IASTNode root = change.getRewriteRoot();
        if (!hasFirsthandRewriteFor(root) && !hasIndirectRewriteFor(root)) {
            register(root.getTranslationUnit(), createRewrite(change));
        }
    }

    private boolean hasIndirectRewriteFor(IASTNode node) {
        return (lookupNodeInRewrites(node) != null);
    }

    private ASTRewrite createRewrite(IASTChange change) {
        return collector.rewriterForTranslationUnit(change.getRewriteRoot().getTranslationUnit());
    }

    private boolean hasFirsthandRewriteFor(IASTNode node) {
        return rewrites.containsKey(node);
    }
}
