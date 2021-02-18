package com.cevelop.includator.optimizer.includestofwd;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.ui.refactoring.CTextFileChange;
import org.eclipse.core.resources.IMarker;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.cxxelement.ClassDeclarationReference;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.cxxelement.FunctionDeclarationReference;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorStatus;
import com.cevelop.includator.helpers.NodeHelper;
import com.cevelop.includator.helpers.offsetprovider.InsertFwdOffsetProvider;
import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.optimizer.IncludatorQuickFix;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.SuppressSuggestionQuickFix;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.ui.Markers;
import com.cevelop.includator.ui.helpers.PositionTrackingChange;


public class ReplaceIncludeWithFwdSuggestion extends Suggestion<FwdInitializationData> {

    private final String           includeString;
    private List<String>           fwdDeclRefDependencies;
    private PositionTrackingChange addFwdChange;
    private final String           includeName;
    private int                    initialStartOffset;
    private int                    initialEndOffset;

    public ReplaceIncludeWithFwdSuggestion(IncludatorFile file, IASTPreprocessorIncludeStatement includeToRemove,
                                           List<DeclarationReferenceDependency> insertFwdCandidates, Class<? extends Algorithm> originAlgorithm) {
        super(new FwdInitializationData(file, includeToRemove, insertFwdCandidates), originAlgorithm);
        includeString = includeToRemove.getRawSignature();
        includeName = includeToRemove.getName().toString();
        initOffsets(includeToRemove);
    }

    private void initOffsets(IASTPreprocessorIncludeStatement includeToRemove) {
        IASTFileLocation includeToRemoveLocation = includeToRemove.getFileLocation();
        initialStartOffset = includeToRemoveLocation.getNodeOffset();
        initialEndOffset = initialStartOffset + includeToRemoveLocation.getNodeLength();
    }

    @Override
    protected void init() {
        init(initData.insertFwdCandidates, initData.getFile().getTranslationUnit(), initData.includeToRemove);
    }

    private void init(List<DeclarationReferenceDependency> insertFwdCandidates, IASTTranslationUnit tu,
            IASTPreprocessorIncludeStatement fallbackInclude) {
        initFwdDependencies(insertFwdCandidates);
        ASTRewrite rewrite = ASTRewrite.create(tu);
        InsertFwdOffsetProvider offsetProvider = new InsertFwdOffsetProvider(tu, fallbackInclude);
        IASTNode fwdDeclsNode = FwdNodeHelper.createCompositeForwardDeclarationFor(insertFwdCandidates);
        rewrite.insertBefore(tu, NodeHelper.findFollowingNonPreprocessorNodeByPosition(offsetProvider.getInsertAfterNode()), fwdDeclsNode, null);
        Change rewriteChange = rewrite.rewriteAST();
        if (!removeSuperfluousDeleteEdit(rewriteChange)) {
            String path = initData.getFile().getSmartPath();
            IncludatorPlugin.logStatus(new IncludatorStatus(
                    "Failed to remove superfluous delete edit from AST-rewrite. Applying changes might thus fail."), path);
        }
        addFwdChange = new PositionTrackingChange(rewriteChange, getIFile());
    }

    /**
     * this method is required because of the rather suboptimal way of the CDT AST-Rewriter which causes overlapping text edits when inserting several
     * nodes in the same place in different rewrites. Also, it removes a possible unwanted newline at the begin of the insert edit.
     */
    private boolean removeSuperfluousDeleteEdit(Change rewriteChange) {
        if (rewriteChange instanceof CompositeChange) {
            CompositeChange compositeChange = (CompositeChange) rewriteChange;
            Change[] changeChildren = compositeChange.getChildren();
            if (changeChildren.length == 1) {
                if (changeChildren[0] instanceof CTextFileChange) {
                    CTextFileChange cTextFileChange = (CTextFileChange) changeChildren[0];
                    TextEdit edit = cTextFileChange.getEdit();
                    return removeSuperfluousDeleteEdit(edit);
                }
            }
        }
        return false;
    }

    private boolean removeSuperfluousDeleteEdit(TextEdit edit) {
        if (edit instanceof MultiTextEdit) {
            MultiTextEdit multiTextEdit = (MultiTextEdit) edit;
            TextEdit[] editChildren = multiTextEdit.getChildren();
            if (editChildren.length == 2) {
                if (editChildren[1] instanceof DeleteEdit) {
                    DeleteEdit deleteEdit = (DeleteEdit) editChildren[1];
                    if (deleteEdit.getLength() == FileHelper.NL_LENGTH) {
                        multiTextEdit.removeChild(deleteEdit);
                    }
                } else {
                    return false;
                }
            }
            if ((editChildren.length == 1 || editChildren.length == 2) && editChildren[0] instanceof InsertEdit) {
                modifyInsertEdit(multiTextEdit, editChildren);
                return true;
            }
        }
        return false;
    }

    private void modifyInsertEdit(MultiTextEdit multiTextEdit, TextEdit[] editChildren) {
        InsertEdit insertEdit = (InsertEdit) editChildren[0];
        String oldInsertText = insertEdit.getText();
        int beginOffset = 0;
        if (oldInsertText.startsWith(FileHelper.NL)) {
            beginOffset = FileHelper.NL_LENGTH;
        }
        int endIndex = oldInsertText.length() - ((editChildren.length == 2) ? FileHelper.NL_LENGTH : 0);
        InsertEdit newInsertEdit = new InsertEdit(insertEdit.getOffset(), oldInsertText.substring(beginOffset, endIndex));
        multiTextEdit.removeChild(insertEdit);
        multiTextEdit.addChild(newInsertEdit);
    }

    private void initFwdDependencies(List<DeclarationReferenceDependency> insertFwdCandidates) {
        if (fwdDeclRefDependencies == null) {
            fwdDeclRefDependencies = new ArrayList<>();
            for (DeclarationReferenceDependency curDepencdency : insertFwdCandidates) {
                fwdDeclRefDependencies.add("'" + toFwdCandidateString(curDepencdency.getDeclarationReference()) + "'");
            }
        }
    }

    private String toFwdCandidateString(DeclarationReference declRef) {
        String nameString = declRef.getName();
        if (declRef instanceof FunctionDeclarationReference) {
            return "function " + nameString;
        } else if (declRef instanceof ClassDeclarationReference) {
            ClassDeclarationReference classRef = (ClassDeclarationReference) declRef;
            int classTypeKey = classRef.getClassBinding().getKey();
            if (classTypeKey == ICPPClassType.k_class) {
                return "class " + nameString;
            } else if (classTypeKey == ICompositeType.k_struct) {
                return "struct " + nameString;
            }
        }
        return nameString;
    }

    @Override
    public String getDescription() {
        initFwdDependencies(initData.insertFwdCandidates);
        String fwdListString = fwdDeclRefDependencies.toString();
        String fwdListStringTrimmed = fwdListString.substring(1, fwdListString.length() - 1);
        boolean hasSeveralFwd = fwdDeclRefDependencies.size() != 1;
        return includeString + " can be replaced with forward declaration" + ((hasSeveralFwd) ? "s" : "") + " of " + fwdListStringTrimmed + ".";
    }

    @Override
    public int getSeverity() {
        return IMarker.SEVERITY_WARNING;
    }

    @Override
    protected int getInitialEndOffset() {
        return initialEndOffset;
    }

    @Override
    protected int getInitialStartOffset() {
        return initialStartOffset;
    }

    @Override
    protected String getSuggestionId() {
        return "IncludatorReplaceIncludesWithFwdID";
    }

    @Override
    protected IncludatorQuickFix[] createQuickFixes() {
        return new IncludatorQuickFix[] { new ReplaceIncludeWithFwdQuickFix(this, addFwdChange), new SuppressSuggestionQuickFix(this) };
    }

    @Override
    public String getMarkerType() {
        return Markers.REPLACE_INCLUDE_WITH_FWD_MARKER;
    }

    @Override
    public void dispose() {
        addFwdChange.dispose();
        super.dispose();
    }

    @Override
    public PositionTrackingChange getContainedPositionTrackingChange() {
        return addFwdChange;
    }

    @Override
    public String getSuppressSuggestionTargetFileName() {
        return includeName;
    }

}
