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
package com.cevelop.namespactor.refactoring.qun;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPSpecialization;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.namespactor.astutil.ASTNodeFactory;
import com.cevelop.namespactor.astutil.NSNameHelper;
import com.cevelop.namespactor.astutil.NSSelectionHelper;
import com.cevelop.namespactor.refactoring.TemplateIdFactory;
import com.cevelop.namespactor.refactoring.iu.InlineRefactoringBase;
import com.cevelop.namespactor.refactoring.iu.InlineRefactoringContext;
import com.cevelop.namespactor.resources.Labels;


/**
 * @author kunz@ideadapt.net
 */
public class QUNRefactoring extends InlineRefactoringBase {

    public QUNRefactoring(ICElement element, Optional<ITextSelection> selection) {
        super(element, selection);
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        SubMonitor sm = SubMonitor.convert(pm, 10);

        super.checkInitialConditions(sm.newChild(6));

        if (initStatus.hasFatalError()) {
            sm.done();
            return initStatus;
        }

        IASTName selectedName = NSSelectionHelper.getSelectedName(selectedRegion, getAST(tu, pm));

        if (selectedName == null) {
            initStatus.addFatalError(Labels.QUN_NoNameSelected);
        } else if (selectedName.getParent() instanceof ICPPASTDeclarator) {
            initStatus.addFatalError(Labels.QUN_DeclaratorNameSelected);
        } else if (selectedName.getParent() instanceof ICPPASTQualifiedName || selectedName instanceof ICPPASTQualifiedName) {
            initStatus.addFatalError(Labels.QUN_SelectedNameAlreadyQualified);
        } else {

            IBinding selectedNameBinding = selectedName.resolveBinding();
            if (selectedNameBinding instanceof ICPPSpecialization) {
                selectedNameBinding = ((ICPPSpecialization) selectedNameBinding).getSpecializedBinding();
            }
            addReplacement(selectedName, ASTNodeFactory.getDefault().newQualifiedNameNode(NSNameHelper.getQualifiedName(selectedNameBinding)));

        }

        sm.done();
        return initStatus;
    }

    @Override
    protected TemplateIdFactory getTemplateIdFactory(ICPPASTTemplateId templateId, InlineRefactoringContext ctx) {
        return new QUNTemplateIdFactory(templateId, ctx);
    }

}
