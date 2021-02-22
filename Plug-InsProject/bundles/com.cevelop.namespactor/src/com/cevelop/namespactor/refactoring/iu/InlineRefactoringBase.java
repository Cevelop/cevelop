/******************************************************************************
 * Copyright (c) 2012 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ueli Kunz <kunz@ideadapt.net>, Jules Weder <julesweder@gmail.com> - initial API and implementation
 ******************************************************************************/
package com.cevelop.namespactor.refactoring.iu;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.ASTNodeFactoryFactory;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.ITextSelection;

import com.cevelop.namespactor.refactoring.RefactoringBase;
import com.cevelop.namespactor.refactoring.TemplateIdFactory;
import com.cevelop.namespactor.refactoring.rewrite.ASTRewriteStore;


/**
 * @author kunz@ideadapt.net
 */
public abstract class InlineRefactoringBase extends RefactoringBase {

    protected Map<IASTName, IASTName>   nodesToReplace     = new HashMap<>();
    protected InlineRefactoringContext  ctx                = new InlineRefactoringContext();
    protected NullProgressMonitor       npm                = new NullProgressMonitor();
    protected IncludeDependencyAnalyser includeDepAnalyser = null;

    public InlineRefactoringBase(ICElement element, Optional<ITextSelection> selection) {
        super(element, selection);
    }

    protected abstract TemplateIdFactory getTemplateIdFactory(ICPPASTTemplateId templateId, InlineRefactoringContext ctx);

    protected void addReplacement(IASTName nodeToReplace, IASTName newNameNode) {
        if (nodeToReplace != null) {
            nodesToReplace.put(nodeToReplace, newNameNode);
        }
    }

    @Override
    protected void collectModifications(ASTRewriteStore store) {
        for (IASTName nodeToReplace : nodesToReplace.keySet()) {
            store.addReplaceChange(nodeToReplace, nodesToReplace.get(nodeToReplace));
        }
        super.collectModifications(store);
    }

    protected IASTName createQualifiedName(IASTName name, IASTName selectedName) {
        ICPPNodeFactory factory = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();
        return factory.newQualifiedName(new String[] { selectedName.toString() }, name.toString());
    }

    protected boolean isFirstSpecifier(IASTName name) {
        IASTName target = name;
        IASTNode parent = name.getParent();
        if (parent instanceof ICPPASTTemplateId) {
            target = (IASTName) parent;
            parent = parent.getParent();
        }
        if (parent instanceof ICPPASTQualifiedName) {
            return ((ICPPASTQualifiedName) parent).getAllSegments()[0] == target;
        }
        return true;
    }
}
