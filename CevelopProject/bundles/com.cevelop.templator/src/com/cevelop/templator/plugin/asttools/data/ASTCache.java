package com.cevelop.templator.plugin.asttools.data;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.templator.plugin.logger.TemplatorException;


public class ASTCache {

    private IIndex                           index;
    private Map<String, IASTTranslationUnit> astStore;

    public ASTCache(IIndex index) {
        this.index = index;
        astStore = new HashMap<>();
    }

    public IASTTranslationUnit getAST(IIndexFileLocation fileLocation) throws TemplatorException {
        IASTTranslationUnit ast;

        if (astStore.containsKey(fileLocation.toString())) {
            ast = astStore.get(fileLocation.toString());
        } else {
            try {
                ITranslationUnit tu = CoreModelUtil.findTranslationUnitForLocation(fileLocation, null);
                ast = tu.getAST(index, 0);

                astStore.put(fileLocation.toString(), ast);
            } catch (CoreException e) {
                throw new TemplatorException("Could not parse file " + fileLocation + ".", e);
            }
        }
        return ast;
    }

}
