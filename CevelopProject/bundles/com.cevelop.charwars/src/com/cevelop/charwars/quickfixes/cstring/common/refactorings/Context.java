package com.cevelop.charwars.quickfixes.cstring.common.refactorings;

import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;

import com.cevelop.charwars.asttools.ASTAnalyzer;
import com.cevelop.charwars.asttools.ASTModifier;
import com.cevelop.charwars.constants.StringType;


public class Context {

    public enum Kind {
        Unmodified_String, Modified_String, Modified_Alias
    }

    private Kind          kind;
    private String        stringVarName;
    private String        offsetVarName;
    private IASTStatement firstAffectedStatement;
    private StringType    stringType;

    public static Context newUnmodifiedStringContext(StringType stringType, String stringVarName) {
        return new Context(Kind.Unmodified_String, stringType, stringVarName, null, null);
    }

    public static Context newModifiedStringContext(StringType stringType, String stringVarName, String offsetVarName,
            IASTStatement firstAffectedStatement) {
        return new Context(Kind.Modified_String, stringType, stringVarName, offsetVarName, firstAffectedStatement);
    }

    public static Context newModifiedAliasContext(StringType stringType, String stringVarName, String offsetVarName) {
        return new Context(Kind.Modified_Alias, stringType, stringVarName, offsetVarName, null);
    }

    public Context(Kind kind, StringType stringType, String stringVarName, String offsetVarName, IASTStatement firstAffectedStatement) {
        this.kind = kind;
        this.stringVarName = stringVarName;
        this.offsetVarName = offsetVarName;
        this.firstAffectedStatement = firstAffectedStatement;
        this.stringType = stringType;
    }

    public boolean isOffset(IASTIdExpression idExpression) {
        switch (kind) {
        case Unmodified_String:
            return false;
        case Modified_String:
            IASTStatement topLevelStatement = ASTAnalyzer.getTopLevelParentStatement(idExpression.getOriginalNode());
            IASTCompoundStatement compoundStatement = (IASTCompoundStatement) topLevelStatement.getParent();
            List<IASTStatement> statements = Arrays.asList(compoundStatement.getStatements());
            int indexOfTopLevelStatement = statements.indexOf(topLevelStatement);
            int indexOfFirstAffectedStatement = statements.indexOf(firstAffectedStatement.getOriginalNode());
            return indexOfTopLevelStatement >= indexOfFirstAffectedStatement;
        case Modified_Alias:
            return true;
        default:
            return false;
        }
    }

    public Kind getKind() {
        return kind;
    }

    public IASTName createStringVarName() {
        return ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newName(stringVarName);
    }

    public IASTIdExpression createStringVarIdExpression() {
        return ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newIdExpression(stringVarName);
    }

    public IASTIdExpression createOffsetVarIdExpression() {
        return ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newIdExpression(offsetVarName);
    }

    public StringType getStringType() {
        return stringType;
    }

    public IASTNode getOffset(IASTIdExpression idExpression) {
        IASTNode offset = ASTModifier.transformToPointerOffset(idExpression);

        if (offset == null) {
            if (isOffset(idExpression)) {
                offset = createOffsetVarIdExpression();
            } else {
                offset = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newIntegerLiteral(0);
            }
        } else if (isOffset(idExpression)) {
            offset = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newPlusExpression(createOffsetVarIdExpression(), (IASTExpression) offset);
        }

        return offset;
    }
}
