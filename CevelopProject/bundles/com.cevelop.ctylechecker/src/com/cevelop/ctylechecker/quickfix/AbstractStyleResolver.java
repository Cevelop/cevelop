package com.cevelop.ctylechecker.quickfix;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.codan.ui.AbstractCodanCMarkerResolution;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import ch.hsr.ifs.iltis.cpp.core.codan.marker.IInfoMarkerResolution;

import com.cevelop.ctylechecker.infos.DynamicStyleInfo;


public abstract class AbstractStyleResolver extends AbstractCodanCMarkerResolution implements IInfoMarkerResolution<DynamicStyleInfo> {

    private DynamicStyleInfo info;

    protected static List<IASTName> namesOf(IASTNode node) {
        final List<IASTName> allNames = new ArrayList<>();
        node.accept(new ASTVisitor() {

            {
                shouldVisitNames = true;
            }

            @Override
            public int visit(IASTName name) {
                allNames.add(name);
                return PROCESS_CONTINUE;
            }
        });
        return allNames;
    }

    @Override
    public DynamicStyleInfo getInfo() {
        return info;
    }

    @Override
    public void configure(DynamicStyleInfo info) {
        this.info = info;
    }
}
