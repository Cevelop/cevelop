package com.cevelop.includator.helpers.tuprovider;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.corext.util.CModelUtil;
import org.eclipse.cdt.internal.ui.editor.ASTProvider;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;

import com.cevelop.includator.helpers.IncludatorException;


@SuppressWarnings("restriction")
public class UnsavedEditorTranslationUnitProvider extends TranslationUnitProvider {

    public UnsavedEditorTranslationUnitProvider(IFile file) {
        super(file);
    }

    @Override
    public IASTTranslationUnit getASTTranslationUnit(IIndex index) {
        try {
            initTranslationUnit();
            if (translationUnit == null) {
                return null;
            }
            translationUnit = CModelUtil.toWorkingCopy(translationUnit);
            // Try to get a shared AST before creating our own.
            final IASTTranslationUnit[] astHolder = new IASTTranslationUnit[1];
            ASTProvider.getASTProvider().runOnAST(translationUnit, ASTProvider.WAIT_IF_OPEN, new NullProgressMonitor(), (lang, ast) -> {
                astHolder[0] = ast;
                return Status.OK_STATUS;
            });
            IASTTranslationUnit ast = astHolder[0];
            if (ast == null) {
                int options = ITranslationUnit.AST_CONFIGURE_USING_SOURCE_CONTEXT | ITranslationUnit.AST_SKIP_INDEXED_HEADERS;
                ast = translationUnit.getAST(index, options);
            }
            return ast;
        } catch (CoreException e) {
            throw new IncludatorException(e);
        }
    }
}
