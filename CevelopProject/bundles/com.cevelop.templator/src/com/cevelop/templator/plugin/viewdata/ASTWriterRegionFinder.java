package com.cevelop.templator.plugin.viewdata;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.internal.core.dom.rewrite.astwriter.ASTWriter;
import org.eclipse.cdt.internal.core.dom.rewrite.astwriter.ProblemRuntimeException;
import org.eclipse.cdt.internal.core.dom.rewrite.commenthandler.NodeCommentMap;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

import com.cevelop.templator.plugin.asttools.data.AbstractResolvedNameInfo;
import com.cevelop.templator.plugin.asttools.data.ResolvedName;


@SuppressWarnings("restriction")
public class ASTWriterRegionFinder extends ASTWriter {

    private FindNodeRegionsVisitor writer = null;

    private Map<Integer, IRegion> foundRegions;
    private String                sourceString;

    public ASTWriterRegionFinder(AbstractResolvedNameInfo resolvedName) {
        writer = new FindNodeRegionsVisitor(resolvedName.getSubNames());
        sourceString = write(resolvedName.getFormattedDefinition());
        foundRegions = removeUnfoundRegions(resolvedName);

        removeLeadingWhitespaces();
    }

    private void removeLeadingWhitespaces() {

        for (Entry<Integer, IRegion> regionEntry : foundRegions.entrySet()) {

            IRegion region = regionEntry.getValue();
            int whitespaceCount = 0;

            while (Character.isWhitespace(sourceString.charAt(region.getOffset() + whitespaceCount))) {
                whitespaceCount++;
            }

            IRegion correctedRegion = new Region(region.getOffset() + whitespaceCount, region.getLength());
            regionEntry.setValue(correctedRegion);
        }
    }

    private Map<Integer, IRegion> removeUnfoundRegions(AbstractResolvedNameInfo resolvedName) {

        Map<ResolvedName, IRegion> tempRegionMapping = new HashMap<>();
        for (Entry<Integer, IRegion> region : writer.getNodeRegions().entrySet()) {
            tempRegionMapping.put(resolvedName.getSubNames().get(region.getKey()), region.getValue());
        }

        for (int i = resolvedName.getSubNames().size() - 1; i >= 0; i--) {
            if (!writer.getNodeRegions().containsKey(i)) {
                resolvedName.getSubNames().remove(i);
            }
        }

        Map<Integer, IRegion> resultMap = new HashMap<>();
        for (int i = 0; i < resolvedName.getSubNames().size(); i++) {
            IRegion region = tempRegionMapping.get(resolvedName.getSubNames().get(i));
            resultMap.put(i, region);
        }

        return resultMap;
    }

    @Override
    public String write(IASTNode rootNode, NodeCommentMap commentMap) throws ProblemRuntimeException {
        if (rootNode != null) {
            rootNode.accept(writer);
        }
        return writer.toString();
    }

    public Map<Integer, IRegion> getFoundRegions() {
        return foundRegions;
    }

    public String getSourceString() {
        return sourceString;
    }
}
