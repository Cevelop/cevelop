package com.cevelop.charwars.quickfixes.cstring.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.charwars.constants.StringType;


public class BlockRefactoringConfiguration {

    private IASTNode            block;
    private ASTRewrite          rewrite;
    private IASTName            strName;
    private IASTName            varName;                //could be same as strName or Alias
    private String              newVarNameString = null;
    private List<IASTStatement> statementsToSkip;
    private StringType          stringType;

    public BlockRefactoringConfiguration() {
        this.statementsToSkip = new ArrayList<>();
    }

    public void setBlock(IASTNode block) {
        this.block = block;
    }

    public IASTNode getBlock() {
        return block;
    }

    public void setASTRewrite(ASTRewrite rewrite) {
        this.rewrite = rewrite;
    }

    public ASTRewrite getASTRewrite() {
        return rewrite;
    }

    public void setStringType(StringType stringType) {
        this.stringType = stringType;
    }

    public StringType getStringType() {
        return stringType;
    }

    public void setStrName(IASTName strName) {
        this.strName = strName;
    }

    public IASTName getStrName() {
        return strName;
    }

    public String getStrNameString() {
        return strName.toString();
    }

    public void setVarName(IASTName varName) {
        this.varName = varName;
    }

    public IASTName getVarName() {
        return varName;
    }

    public String getVarNameString() {
        return varName.toString();
    }

    public void setNewVarNameString(String newVarNameString) {
        this.newVarNameString = newVarNameString;
    }

    public String getNewVarNameString() {
        return (newVarNameString == null) ? getVarNameString() : newVarNameString;
    }

    public void skipStatement(IASTStatement statement) {
        this.statementsToSkip.add(statement);
    }

    public boolean shouldSkipStatement(IASTStatement statement) {
        return this.statementsToSkip.contains(statement);
    }

    public boolean isAlias() {
        return this.strName != this.varName;
    }
}
