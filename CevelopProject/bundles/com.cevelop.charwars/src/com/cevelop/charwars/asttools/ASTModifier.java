package com.cevelop.charwars.asttools;

import java.util.Set;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorObjectStyleMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.cevelop.charwars.constants.ErrorMessages;
import com.cevelop.charwars.utils.ErrorLogger;
import com.cevelop.charwars.utils.analyzers.BEAnalyzer;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;


@SuppressWarnings("restriction")
public class ASTModifier {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String INCLUDE_FORMAT = "#include <%s>";

    public static void replaceNode(IASTNode oldNode, IASTNode newNode) {
        //TODO: Hack...
        IASTAmbiguityParent parent = (IASTAmbiguityParent) oldNode.getParent();
        parent.replace(oldNode, newNode);
    }

    public static void includeHeaders(Set<String> headers, CompositeChange change, IFile file, IASTTranslationUnit astTranslationUnit,
            IDocument document) {
        // replaces the existing CompositeChange with a new TextFileChange that
        // contains an additional InsertEdit that inserts the header includes
        TextFileChange textFileChange = getTextFileChangeForFile(change, file);
        change.remove(textFileChange.getParent());

        int insertionPosition = getPositionForIncludeStatements(astTranslationUnit);
        String includeText = getIncludeText(headers, astTranslationUnit, insertionPosition, document);
        InsertEdit insertEdit = new InsertEdit(insertionPosition, includeText);
        TextFileChange newTextFileChange = new TextFileChange(file.getName(), file);
        MultiTextEdit multiTextEdit = new MultiTextEdit();
        multiTextEdit.addChild(textFileChange.getEdit());
        multiTextEdit.addChild(insertEdit);
        newTextFileChange.setEdit(multiTextEdit);
        change.add(newTextFileChange);
    }

    private static TextFileChange getTextFileChangeForFile(Change change, IFile file) {
        if (change instanceof CompositeChange) {
            CompositeChange compositeChange = (CompositeChange) change;
            for (Change c : compositeChange.getChildren()) {
                TextFileChange textFileChange = getTextFileChangeForFile(c, file);
                if (textFileChange != null) {
                    return textFileChange;
                }
            }
        }

        if (change instanceof TextFileChange) {
            TextFileChange textFileChange = (TextFileChange) change;
            if (textFileChange.getFile().equals(file)) {
                return textFileChange;
            }
        }
        return null;
    }

    private static String getIncludeText(Set<String> headers, IASTTranslationUnit ast, int insertionPosition, IDocument document) {
        StringBuffer includeText = new StringBuffer();
        for (String headerName : headers) {
            if (!isHeaderAlreadyIncluded(headerName, ast)) {
                if (includeText.length() != 0 || insertionPosition != 0) {
                    includeText.append(LINE_SEPARATOR);
                }
                includeText.append(String.format(INCLUDE_FORMAT, headerName));
            }
        }

        if (includeText.length() > 0) {
            try {
                String s = document.get(insertionPosition, LINE_SEPARATOR.length());
                if (!s.equals(LINE_SEPARATOR)) {
                    includeText.append(LINE_SEPARATOR);
                }
            } catch (BadLocationException e) {
                ErrorLogger.log(ErrorMessages.UNABLE_TO_ADD_INCLUDE_DIRECTIVE, e);
                throw new RuntimeException(e);
            }
        }
        return includeText.toString();
    }

    private static boolean isHeaderAlreadyIncluded(String headerName, IASTTranslationUnit ast) {
        IASTPreprocessorIncludeStatement[] includeStatements = ast.getTranslationUnit().getIncludeDirectives();
        for (IASTPreprocessorIncludeStatement includeStatement : includeStatements) {
            if (includeStatement.getName().getRawSignature().equals(headerName) && includeStatement.isSystemInclude()) {
                return true;
            }
        }

        return false;
    }

    private static int getPositionForIncludeStatements(IASTTranslationUnit ast) {
        IASTPreprocessorIncludeStatement[] includeStatements = ast.getTranslationUnit().getIncludeDirectives();
        for (int i = includeStatements.length - 1; i >= 0; --i) {
            IASTPreprocessorIncludeStatement includeStatement = includeStatements[i];
            if (includeStatement.isSystemInclude() && includeStatement.isPartOfTranslationUnitFile()) {
                IASTNodeLocation nodeLocation = includeStatement.getNodeLocations()[0];
                return nodeLocation.getNodeOffset() + nodeLocation.getNodeLength();
            }
        }

        IASTPreprocessorStatement[] preprocessorStatements = ast.getAllPreprocessorStatements();
        for (int i = preprocessorStatements.length - 1; i >= 0; --i) {
            IASTPreprocessorStatement preprocessorStatement = preprocessorStatements[i];
            if (preprocessorStatement instanceof IASTPreprocessorObjectStyleMacroDefinition) {
                IASTNodeLocation nodeLocation = preprocessorStatement.getNodeLocations()[0];
                return nodeLocation.getNodeOffset() + nodeLocation.getNodeLength();
            }
        }

        return 0;
    }

    public static void replace(IASTNode node1, IASTNode node2, ASTRewrite rewrite) {
        rewrite.replace(node1, node2, null);
    }

    public static void remove(IASTNode node, ASTRewrite rewrite) {
        rewrite.remove(node, null);
    }

    public static void insertBefore(IASTNode parent, IASTNode insertionPoint, IASTNode node, ASTRewrite rewrite) {
        rewrite.insertBefore(parent, insertionPoint, node, null);
    }

    public static IASTNode transformToPointerOffset(IASTIdExpression idExpression) {
        IASTNode lastNode = idExpression;
        IASTNode currentNode = idExpression.getParent();
        while (BEAnalyzer.isSubtraction(currentNode) || BEAnalyzer.isAddition(currentNode)) {
            lastNode = currentNode;
            currentNode = currentNode.getParent();
        }

        //return null to indicate, that there is no pointer offset
        if (lastNode == idExpression) {
            return null;
        }

        IASTNode parent = idExpression.getParent();
        IASTExpression remainingOperand;
        if (BEAnalyzer.isSubtraction(parent) && BEAnalyzer.isOp1(idExpression)) {
            remainingOperand = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newNegatedExpression(BEAnalyzer.getOperand2(parent));
        } else {
            remainingOperand = BEAnalyzer.getOtherOperand(idExpression);
        }

        replaceNode(parent, remainingOperand);
        return (parent == lastNode) ? remainingOperand : lastNode;
    }
}
