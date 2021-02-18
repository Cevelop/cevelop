package com.cevelop.templator.tests;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateArgument;
import org.junit.Assert;

import com.cevelop.templator.plugin.asttools.ASTTools;
import com.cevelop.templator.plugin.asttools.FindAllRelevantNodesVisitor;
import com.cevelop.templator.plugin.asttools.data.AbstractResolvedNameInfo;
import com.cevelop.templator.plugin.asttools.data.RelevantNameCache;
import com.cevelop.templator.plugin.asttools.data.UnresolvedNameInfo;
import com.cevelop.templator.plugin.asttools.resolving.NameDeduction;
import com.cevelop.templator.plugin.logger.TemplatorException;


public class TemplatorResolutionTest extends TemplatorProjectTest {

    protected AbstractResolvedNameInfo firstStatementInMain;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        resolveFirstFoundCallInMain();
    }

    protected AbstractResolvedNameInfo resolveCallInMain(int index) throws TemplatorException {
        List<UnresolvedNameInfo> subCalls = getSubStatements(getMain());
        return AbstractResolvedNameInfo.create(subCalls.get(index), null, analyzer);
    }

    protected List<UnresolvedNameInfo> getSubStatements(IASTDeclaration definition) throws TemplatorException {
        List<UnresolvedNameInfo> preStatements = new ArrayList<>();

        IASTNode body = ASTTools.getBody(definition);
        if (body != null) {
            FindAllRelevantNodesVisitor visitor = new FindAllRelevantNodesVisitor();
            body.accept(visitor);

            for (IASTNode node : visitor.getAllRelevantNodes()) {
                if (node instanceof IASTName) {
                    IASTName name = (IASTName) node;
                    UnresolvedNameInfo subStatement = NameDeduction.deduceName(name, false, true, analyzer, new RelevantNameCache());
                    if (subStatement != null) {
                        preStatements.add(subStatement);
                    }
                }
            }
        }
        return preStatements;
    }

    private void resolveFirstFoundCallInMain() throws TemplatorException {
        firstStatementInMain = resolveCallInMain(0);
    }

    protected void testArgumentMap(ICPPTemplateArgument... expectedArguments) {
        assertEquals(firstStatementInMain.getTemplateArgumentMap(), expectedArguments);
    }

    // statement 0 -> definition 0
    protected void firstStatementInMainResolvesToFirstDefinition() {
        firstStatementInMainResolvesToDefinition(definitions.get(0));
    }

    // statement 0 -> definition x
    protected void firstStatementInMainResolvesToDefinition(IASTDeclaration definition) {
        Assert.assertEquals(definition, firstStatementInMain.getDefinition());
    }

    // statement 0 -> definition x (x from config in rts file)
    protected void firstStatementInMainMatchesExpectedDefinition() {
        statementMatchesExpectedDefinition(firstStatementInMain.getDefinition());
    }
}
