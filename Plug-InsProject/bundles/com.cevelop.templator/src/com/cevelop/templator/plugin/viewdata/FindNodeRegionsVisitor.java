package com.cevelop.templator.plugin.viewdata;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTImplicitName;
import org.eclipse.cdt.core.dom.ast.IASTImplicitNameOwner;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTArraySubscriptExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTBinaryExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFieldReference;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionCallExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTUnaryExpression;
import org.eclipse.cdt.internal.core.dom.rewrite.ASTModificationStore;
import org.eclipse.cdt.internal.core.dom.rewrite.astwriter.ASTWriter;
import org.eclipse.cdt.internal.core.dom.rewrite.astwriter.Scribe;
import org.eclipse.cdt.internal.core.dom.rewrite.changegenerator.ChangeGeneratorWriterVisitor;
import org.eclipse.cdt.internal.core.dom.rewrite.commenthandler.NodeCommentMap;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

import com.cevelop.templator.plugin.asttools.data.ResolvedName;
import com.cevelop.templator.plugin.util.ReflectionMethodHelper;


public class FindNodeRegionsVisitor extends ChangeGeneratorWriterVisitor {

    private List<ResolvedName> subNames;

    private Map<Integer, IRegion> foundRegions = new HashMap<>();

    private ASTWriter writer = new ASTWriter();

    public FindNodeRegionsVisitor(List<ResolvedName> subNames) {
        super(new ASTModificationStore(), null, new NodeCommentMap());
        this.subNames = subNames;
        shouldVisitDeclSpecifiers = true;
    }

    @Override
    public int visit(IASTName name) {
        processNode(name);
        return super.visit(name);
    }

    @Override
    public int visit(IASTDeclSpecifier declSpec) {
        processNode(declSpec);
        return super.visit(declSpec);
    }

    @Override
    public int visit(IASTExpression expression) {
        if (expression instanceof IASTImplicitNameOwner) {
            processExpression((IASTImplicitNameOwner) expression);
        }
        return super.visit(expression);
    }

    private void processExpression(IASTImplicitNameOwner nameOwner) {
        // TODO handle CPPASTExpressionList that has more than one implicitName in it
        IASTImplicitName implicitName = getImplicitName(nameOwner);
        int namePosition = getNamePosition(implicitName);
        if (namePosition >= 0) {
            int offset = scribe.toString().length() + getIdentation();
            int length = implicitName.getFileLocation().getNodeLength();
            if (nameOwner instanceof CPPASTBinaryExpression) {
                offset += writePartBeforeImplicitName((CPPASTBinaryExpression) nameOwner);
            } else if (nameOwner instanceof CPPASTUnaryExpression) {
                offset += writePartBeforeImplicitName((CPPASTUnaryExpression) nameOwner);
            } else if (nameOwner instanceof CPPASTArraySubscriptExpression) {
                offset += writePartBeforeImplicitName((CPPASTArraySubscriptExpression) nameOwner);
                // not only box [] but also what`s inside
                length += writeArraySubscriptExpr((CPPASTArraySubscriptExpression) nameOwner);
            } else if (nameOwner instanceof CPPASTFunctionCallExpression) {
                offset += writePartBeforeImplicitName((CPPASTFunctionCallExpression) nameOwner);
                // not only box () but also what`s inside
                length += writeFunctionCallExpr((CPPASTFunctionCallExpression) nameOwner);
            } else if (nameOwner instanceof CPPASTFieldReference) {
                offset += writePartBeforeImplicitName((CPPASTFieldReference) nameOwner);
            }
            IRegion region = new Region(offset, length);
            foundRegions.put(namePosition, region);
        }
    }

    private int writePartBeforeImplicitName(CPPASTBinaryExpression binaryExpr) {
        return writer.write(binaryExpr.getOperand1()).length();
    }

    private int writePartBeforeImplicitName(CPPASTUnaryExpression unaryExpr) {
        if (unaryExpr.isPostfixOperator()) {
            return writer.write(unaryExpr.getOperand()).length();
        }
        return 0;
    }

    private int writePartBeforeImplicitName(CPPASTArraySubscriptExpression arrSubExpr) {
        return writer.write(arrSubExpr.getArrayExpression()).length();
    }

    private int writeArraySubscriptExpr(CPPASTArraySubscriptExpression arrSubExpr) {
        // +1 because of closing ]
        return writer.write(arrSubExpr.getArgument()).length() + 1;
    }

    private int writePartBeforeImplicitName(CPPASTFunctionCallExpression funCallExpr) {
        return writer.write(funCallExpr.getFunctionNameExpression()).length();
    }

    private int writeFunctionCallExpr(CPPASTFunctionCallExpression funCallExpr) {
        // -1 because of ;
        return writer.write(funCallExpr).length() - writer.write(funCallExpr.getFunctionNameExpression()).length() - 1;
    }

    private int writePartBeforeImplicitName(CPPASTFieldReference fieldRef) {
        return writer.write(fieldRef.getFieldOwner()).length();
    }

    private IASTImplicitName[] getImplicitNames(IASTImplicitNameOwner nameOwner) {
        IASTImplicitNameOwner originalNameOwner = (IASTImplicitNameOwner) nameOwner.getOriginalNode();
        return originalNameOwner.getImplicitNames();
    }

    private IASTImplicitName getImplicitName(IASTImplicitNameOwner nameOwner) {
        IASTImplicitName[] implicitNames = getImplicitNames(nameOwner);
        if (implicitNames.length == 0) {
            return null;
        }
        return implicitNames[0];
    }

    private int getNamePosition(IASTName name) {
        if (name != null) {
            for (int i = 0; i < subNames.size(); i++) {
                IASTNode originalNode = subNames.get(i).getOriginalNode();
                if (!(originalNode instanceof IASTName)) {
                    continue;
                }
                IASTName nameToTest = (IASTName) originalNode;
                if (name.equals(nameToTest)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int getIdentation() {
        if (scribe.isAtBeginningOfLine()) {
            try {
                Field indent = ReflectionMethodHelper.getNonAccessibleField(Scribe.class, "INDENTATION_SIZE");
                Field iLevel = ReflectionMethodHelper.getNonAccessibleField(Scribe.class, "indentationLevel");
                return iLevel.getInt(scribe) * indent.getInt(scribe);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private void processNode(IASTNode node) {
        for (int i = 0; i < subNames.size(); i++) {
            IASTNode originalNode = subNames.get(i).getOriginalNode();
            if (node.getOriginalNode().equals(originalNode)) {
                String nameString = writer.write(originalNode);
                int offset = scribe.toString().length();
                int length = nameString.length();
                IRegion region = new Region(offset, length);
                foundRegions.put(i, region);
                break;
            }
        }
    }

    public Map<Integer, IRegion> getNodeRegions() {
        return foundRegions;
    }
}
