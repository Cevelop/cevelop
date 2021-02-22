package com.cevelop.aliextor.refactoring.strategy;

import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeId;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.aliextor.ast.ASTHelper;
import com.cevelop.aliextor.ast.ASTHelper.Type;
import com.cevelop.aliextor.ast.SimpleVisitor;
import com.cevelop.aliextor.ast.TemplateAliasVisitor;
import com.cevelop.aliextor.refactoring.AliExtorRefactoring;
import com.cevelop.aliextor.wizard.helper.Pair;


public class TemplateAliasRefactoringConcreteStrategy extends SimpleRefactoringConcreteStrategy {

    private final static String templateParameterDefaultName = "T";

    private ICPPASTNamedTypeSpecifier        selectedNode;
    private ArrayList<IASTNode>              names;
    private ArrayList<Pair<String, Integer>> selectedNames;
    private ArrayList<IASTDeclSpecifier>     selectedNodes;
    private ArrayList<Pair<String, Integer>> helperList;

    private int count;
    private int indexNames;

    public TemplateAliasRefactoringConcreteStrategy(AliExtorRefactoring proxy) {
        super(proxy);
    }

    @Override
    public RefactoringStatus checkTheInitialConditions(RefactoringStatus status) {

        selectedNode = ASTHelper.getNamedTypeSpecifierWithTemplateId(refactorSelection);

        if (selectedNode != null) {
            proxy.setPotentialForTemplateAlias(true);

            // Find the names or the simpleDeclSpecs and save em in names
            TemplateAliasVisitor visitor = new TemplateAliasVisitor(refactorSelection);
            refactorSelection.getSelectedNode().accept(visitor);
            names = visitor.getOccurrences();
        }

        return super.checkTheInitialConditions(status);
    }

    @Override
    protected IASTNode getScope() {
        if (checkSelectedNames()) {
            return ASTHelper.findEnclosingTranslationUnit(refactorSelection.getSelectedNode());
        }
        return super.getScope();
    }

    @Override
    public void collectModifications(ASTRewrite rewrite) {
        if (proxy.hasPotentialForTemplateAlias() && checkSelectedNames()) {
            if (!proxy.shouldJustRefactorSelected()) {
                SimpleVisitor visitor = new SimpleVisitor(refactorSelection);
                IASTNode searchScope = getScope();
                searchScope.accept(visitor);
            }
            count = 1;
            selectedNodes = new ArrayList<>();
            helperList = new ArrayList<>();
        }
        super.collectModifications(rewrite);
    }

    private boolean checkSelectedNames() {
        return selectedNames != null && selectedNames.size() != 0;
    }

    @Override
    protected IASTNode createAliasStatement(char[] aliasName) {
        if (checkSelectedNames()) {
            indexNames = 0;

            ICPPASTTemplateDeclaration templateAlias = factory.newTemplateDeclaration(factory.newAliasDeclaration(factory.newName(proxy
                    .getTheUserInput().toCharArray()), factory.newTypeId(factory.newTypedefNameSpecifier(createQualifiedNameWithTemplateParameter()),
                            createEmptyDeclarator())));
            // template<typename T>
            if (count > 1) {
                for (int i = 1; i < count; i++) {
                    templateAlias.addTemplateParameter(factory.newSimpleTypeTemplateParameter(ICPPASTSimpleTypeTemplateParameter.st_typename, factory
                            .newName((templateParameterDefaultName + i).toCharArray()), null));
                }
            } else {
                templateAlias.addTemplateParameter(factory.newSimpleTypeTemplateParameter(ICPPASTSimpleTypeTemplateParameter.st_typename, factory
                        .newName(templateParameterDefaultName.toCharArray()), null));
            }
            return templateAlias;
        } else {
            return super.createAliasStatement(aliasName);
        }
    }

    private ICPPASTQualifiedName createQualifiedNameWithTemplateParameter() {
        if (names.get(indexNames) != null) {
            ICPPASTQualifiedName newQualifiedName = createQualifiedName();

            if (indexNames >= names.size()) {
                return newQualifiedName;
            }
            IASTNode name = names.get(indexNames++);
            if (name == null) {
                return newQualifiedName;
            }

            ICPPASTTemplateId templateId = factory.newTemplateId((IASTName) name.copy(CopyStyle.withLocations));
            if (shouldAddTemplateIdName(newQualifiedName)) {
                newQualifiedName.addName(templateId);
            }
            indexNames++;

            ICPPASTTemplateId sameTemplateId = getTemplateId(name);
            IASTTypeId typeId = createTypeId();

            if (isLastArgument(typeId)) {
                if (shouldAddTemplateIdName(newQualifiedName)) {
                    templateId.addTemplateArgument(typeId);
                }
                return newQualifiedName;
            }
            // add arguments
            typeId = addArguments(newQualifiedName, templateId, sameTemplateId, typeId);
            if (typeId != null) {
                if (shouldAddTemplateIdName(newQualifiedName)) {
                    templateId.addTemplateArgument(typeId);
                }
            }
            return newQualifiedName;
        }
        return null;
    }

    private IASTTypeId addArguments(ICPPASTQualifiedName newQualifiedName, ICPPASTTemplateId templateId, ICPPASTTemplateId sameTemplateId,
            IASTTypeId typeId) {
        while (indexNames < names.size() && sameTemplateId.getRawSignature().contentEquals(getTemplateId(names.get(indexNames) != null ? names.get(
                indexNames) : names.get(++indexNames)).getRawSignature())) {
            if (shouldAddTemplateIdName(newQualifiedName)) {
                templateId.addTemplateArgument(typeId);
            }
            typeId = createTypeId();
            if (typeId == null) {
                indexNames++;
                typeId = createTypeId();
            }
        }
        return typeId;
    }

    private boolean isLastArgument(IASTTypeId typeId) {
        return indexNames >= names.size() && typeId != null;
    }

    private boolean shouldAddTemplateIdName(ICPPASTQualifiedName newQualifiedName) {
        return !newQualifiedName.toString().contentEquals(getCurrentTemplateParameterName());
    }

    private ICPPASTQualifiedName createQualifiedName() {
        ICPPASTQualifiedName newQualifiedName = null;
        // To make sure that the index is shifted
        ICPPASTQualifiedName tempQualifiedName = null;
        if (nameIsSelected()) {
            saveSelectedType();
            newQualifiedName = factory.newQualifiedName(factory.newName(getTemplateParameterName()));
        }
        tempQualifiedName = factory.newQualifiedName((ICPPASTName) names.get(indexNames++).copy(CopyStyle.withLocations));
        for (; indexInRange() && names.get(indexNames) != null && !(names.get(indexNames).getParent() instanceof ICPPASTTemplateId); indexNames++) {
            tempQualifiedName.addName((IASTName) names.get(indexNames).copy(CopyStyle.withLocations));
        }
        return newQualifiedName != null ? newQualifiedName : tempQualifiedName;
    }

    private boolean nameIsSelected() {
        if (indexNames == 0) {
            return false;
        }
        StringBuilder name = new StringBuilder();
        name.append(names.get(indexNames));
        IASTNode node = indexInRange(indexNames + 1) ? names.get(indexNames + 1) : null;
        for (int index = indexNames + 1; node != null; index++, node = names.get(index)) {
            name.append("::");
            name.append(node.toString());
            if (!indexInRange(index + 1)) {
                break;
            }
        }
        String nameAsString = name.toString();
        Pair<String, Integer> pair = Pair.of(nameAsString, countOccurrence(nameAsString));
        helperList.add(pair);
        return selectedNames.contains(pair);
    }

    private int countOccurrence(String name) {
        int i;
        for (i = 0; helperList.contains(Pair.of(name, i)); i++) {
            ;
        }
        return i;
    }

    private void saveSelectedType() {
        IASTNode name = names.get(indexNames);
        if (ASTHelper.isType(name, Type.ICPPASTSimpleDeclSpecifier)) {
            selectedNodes.add((IASTDeclSpecifier) name);
        } else if (ASTHelper.isType(name, Type.ICPPASTName)) {
            selectedNodes.add((IASTDeclSpecifier) name.getParent().getParent());
        }
    }

    private ICPPASTTypeId createTypeId() {
        IASTNode node = names.get(indexNames);
        if (node != null) {
            if (node instanceof ICPPASTSimpleDeclSpecifier) {
                if (nameIsSelected()) {
                    saveSelectedType();
                    indexNames++;
                    return factory.newTypeId(factory.newTypedefNameSpecifier(factory.newName(getTemplateParameterName())), createEmptyDeclarator());
                }
                indexNames++;
                return factory.newTypeId((IASTDeclSpecifier) node.copy(CopyStyle.withLocations), createEmptyDeclarator());
            } else if (node instanceof ICPPASTName) {
                ICPPASTQualifiedName qualiName = createQualifiedNameWithTemplateParameter();
                if (qualiName != null) {
                    return factory.newTypeId(factory.newTypedefNameSpecifier(qualiName), createEmptyDeclarator());
                }
            }
        }
        return null;
    }

    private ICPPASTTemplateId getTemplateId(IASTNode node) {
        if (node != null) {
            while (node != null && !(node instanceof ICPPASTTemplateId)) {
                node = node.getParent();
            }
            return node != null ? (ICPPASTTemplateId) node : null;
        } else {
            return null;
        }
    }

    @Override
    protected void doDeclSpecifierChange(ASTRewrite rewrite, char[] aliasName, IASTNode oldNode) {
        if (checkSelectedNames()) {
            ICPPASTTemplateId name = factory.newTemplateId(factory.newName(aliasName));
            ICPPASTNamedTypeSpecifier replacement = factory.newTypedefNameSpecifier(name);

            for (IASTNode node : selectedNodes) {
                name.addTemplateArgument(factory.newTypeId((IASTDeclSpecifier) node.copy(CopyStyle.withLocations), createEmptyDeclarator()));
            }

            replacement.setStorageClass(((ICPPASTDeclSpecifier) oldNode).getStorageClass());

            rewrite.replace(oldNode, replacement, null);
        } else {
            super.doDeclSpecifierChange(rewrite, aliasName, oldNode);
        }
    }

    private boolean indexInRange() {
        return indexNames < names.size();
    }

    private boolean indexInRange(int index) {
        return index < names.size();
    }

    private char[] getTemplateParameterName() {
        return (templateParameterDefaultName + (selectedNames.size() > 1 ? count++ : "")).toCharArray();
    }

    private String getCurrentTemplateParameterName() {
        return templateParameterDefaultName + (selectedNames.size() > 1 ? count > 1 ? count - 1 : count : "");
    }

    private ICPPASTDeclarator createEmptyDeclarator() {
        return factory.newDeclarator(factory.newName());
    }

    public ICPPASTNamedTypeSpecifier getSelectedNode() {
        return selectedNode;
    }

    public ArrayList<IASTNode> getNames() {
        return names;
    }

    public void setSelectedNames(ArrayList<Pair<String, Integer>> selectedNames) {
        this.selectedNames = selectedNames;
    }

}
