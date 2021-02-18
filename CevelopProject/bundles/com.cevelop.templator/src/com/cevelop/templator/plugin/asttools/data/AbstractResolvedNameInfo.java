package com.cevelop.templator.plugin.asttools.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateArgument;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameter;
import org.eclipse.cdt.internal.core.dom.rewrite.astwriter.ASTWriter;

import com.cevelop.templator.plugin.asttools.ASTAnalyzer;
import com.cevelop.templator.plugin.asttools.ASTTools;
import com.cevelop.templator.plugin.asttools.FindAllRelevantNodesVisitor;
import com.cevelop.templator.plugin.asttools.resolving.AutoCache;
import com.cevelop.templator.plugin.asttools.resolving.AutoResolver;
import com.cevelop.templator.plugin.asttools.resolving.NameDeduction;
import com.cevelop.templator.plugin.asttools.resolving.PostResolver;
import com.cevelop.templator.plugin.asttools.resolving.nametype.TypeNameToType;
import com.cevelop.templator.plugin.asttools.templatearguments.TemplateArgumentMap;
import com.cevelop.templator.plugin.asttools.type.finding.RelevantNameType;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.plugin.util.ILoadingProgress;
import com.cevelop.templator.plugin.util.SettingsCache;


public abstract class AbstractResolvedNameInfo {

    protected ASTAnalyzer analyzer;

    protected IASTName resolvingName;
    protected IBinding binding;

    protected AbstractResolvedNameInfo                        parent;
    protected List<ResolvedName>                              subNames;
    protected Map<RelevantNameType, AbstractResolvedNameInfo> subNameCache;
    private AutoCache                                         autoCache;
    private AutoResolver                                      autoResolver;

    protected boolean      subNamesSearched = false;
    protected NameTypeKind type;

    protected IASTDeclaration   definition;
    protected IASTDeclaration   formattedDefinition;
    protected RelevantNameCache relevantNameCache;

    private SubNameErrorCollection subNameErrors;

    protected AbstractResolvedNameInfo(IASTName resolvingName, IBinding binding, NameTypeKind type, IASTDeclaration definition,
                                       AbstractResolvedNameInfo parent, ASTAnalyzer analyzer) {
        this.resolvingName = resolvingName;
        this.binding = binding;
        this.type = type;
        this.definition = definition;
        this.parent = parent;
        this.analyzer = analyzer;
        autoCache = new AutoCache();
        autoResolver = new AutoResolver(analyzer);
        subNames = new ArrayList<>();
        subNameCache = new HashMap<>();
        relevantNameCache = new RelevantNameCache();
    }

    protected AbstractResolvedNameInfo(UnresolvedNameInfo unresolvedName, IASTDeclaration definition, AbstractResolvedNameInfo parent,
                                       ASTAnalyzer analyzer) {
        this(unresolvedName.getResolvingName(), unresolvedName.getBinding(), unresolvedName.getType(), definition, parent, analyzer);
    }

    public static AbstractResolvedNameInfo create(IASTName originalName, boolean acceptUnknownBindings, boolean isStartingPoint, ASTAnalyzer analyzer)
            throws TemplatorException {
        // get the template-id for the original name if necessary, else deduceStatement
        // will return null
        IASTName resolvingOriginalName = ASTTools.extractTemplateInstanceName(originalName);
        UnresolvedNameInfo unresolvedName = NameDeduction.deduceName(resolvingOriginalName, acceptUnknownBindings, isStartingPoint, analyzer,
                RelevantNameCache.EMPTY_CACHE);
        return create(unresolvedName, null, analyzer);
    }

    public static AbstractResolvedNameInfo create(UnresolvedNameInfo unresolvedName, AbstractResolvedNameInfo parent, ASTAnalyzer analyzer)
            throws TemplatorException {
        return create(unresolvedName, parent, analyzer, true);
    }

    public static AbstractResolvedNameInfo create(UnresolvedNameInfo unresolvedName, AbstractResolvedNameInfo parent, ASTAnalyzer analyzer,
            boolean resolve) throws TemplatorException {
        if (unresolvedName != null) {
            if (resolve) {
                PostResolver.resolveToFinalBinding(unresolvedName, parent, analyzer);
            }
            if (unresolvedName.getType() == NameTypeKind.CLASS) {
                return ClassType.__create(unresolvedName, parent, analyzer);
            } else if (unresolvedName.getType() == NameTypeKind.LAMBDA) {
                return LambdaExpression.__create(unresolvedName, parent, analyzer);
            } else if (unresolvedName.getType() == NameTypeKind.FUNCTION || unresolvedName.getType() == NameTypeKind.FUNCTION_SPECIALIZATION) {
                return FunctionCall.__create(unresolvedName, parent, analyzer);
            } else {
                return AbstractTemplateInstance.__create(unresolvedName, parent, analyzer);
            }
        }

        return null;
    }

    public void searchSubNames(ILoadingProgress loadingProgress) throws TemplatorException {
        if (subNamesSearched) {
            return;
        }
        subNameErrors = new SubNameErrorCollection();
        List<IASTNode> allNodes = findAllRelevantNodes(loadingProgress);
        _createSubNames(loadingProgress, allNodes, subNameErrors);
        subNamesSearched = true;
    }

    private List<IASTNode> findAllRelevantNodes(ILoadingProgress loadingProgress) {
        loadingProgress.setStatus("Listing all Nodes...");
        FindAllRelevantNodesVisitor searchVisitor = new FindAllRelevantNodesVisitor();
        definition.accept(searchVisitor);
        loadingProgress.setProgress(0.1);
        return searchVisitor.getAllRelevantNodes();
    }

    private void _createSubNames(ILoadingProgress loadingProgress, List<IASTNode> allNames, SubNameErrorCollection errors) throws TemplatorException {

        loadingProgress.setStatus("Deducing Template Relevant Names...");
        double loadingBarIncrement = 0.99 / (allNames.size() + 1);
        double currentLoadingProgress = 0.1;

        for (IASTNode node : allNames) {
            UnresolvedNameInfo unresolvedName = null;
            IASTName name = null;
            IASTDeclSpecifier declSpec = null;
            ResolvedName subName = null;
            if (node instanceof IASTName) {
                name = (IASTName) node;
                unresolvedName = deduceSubName(name, errors);
                if (unresolvedName != null) {
                    subName = createSubName(unresolvedName, errors);
                    addSubName(subName);
                } else if (SettingsCache.isTracingAuto()) { // handle names that could not be resolved because of auto specifier
                    IASTDeclSpecifier autoDeclSpec = autoResolver.getAutoDeclSpec(name);
                    if (autoDeclSpec != null) {
                        AbstractResolvedNameInfo info = autoCache.get(autoDeclSpec);
                        if (info != null) {
                            subName = new ResolvedName(name, info);
                            addSubName(subName);
                        } else {
                            subName = autoResolver.createResolvedNameFromAuto(autoDeclSpec, this);
                            if (subNameIsRelevant(subName)) {
                                addToAutoCache(autoDeclSpec, subName);
                                subName = new ResolvedName(name, subName.getInfo());
                                addSubName(subName);
                            }
                        }
                    }
                }
            } else if (SettingsCache.isTracingAuto() && node instanceof IASTDeclSpecifier) { // handle auto specifier
                declSpec = (IASTDeclSpecifier) node;
                subName = autoResolver.createResolvedNameFromAuto(declSpec, this);
                addToAutoCache(declSpec, subName);
                addSubName(subName);
            }
            currentLoadingProgress += loadingBarIncrement;
            loadingProgress.setProgress(currentLoadingProgress);
        }
    }

    private void addToAutoCache(IASTDeclSpecifier declSpec, ResolvedName subName) {
        if (subNameIsRelevant(subName)) {
            autoCache.put(declSpec, subName.getInfo());
        }
    }

    private void addSubName(ResolvedName subName) {
        if (!subNameIsRelevant(subName)) {
            return;
        }
        if (!doesReferenceItself(subName)) {
            subNames.add(subName);
        }
    }

    private boolean doesReferenceItself(ResolvedName subName) {
        return subName.getInfo().getBinding() == getBinding() && subName.getInfo().getTemplateArgumentMap().equals(getTemplateArgumentMap());
    }

    private boolean subNameIsRelevant(ResolvedName subName) {
        return !(subName == null || subName.getInfo() == null);
    }

    private UnresolvedNameInfo deduceSubName(IASTName name, SubNameErrorCollection errors) {
        try {
            UnresolvedNameInfo unresolvedSubName = NameDeduction.deduceName(name, true, false, this, relevantNameCache);
            return unresolvedSubName;
        } catch (TemplatorException e) {
            errors.addDeductionError(name, e);
        }
        return null;
    }

    private ResolvedName createSubName(UnresolvedNameInfo unresolvedName, SubNameErrorCollection errors) {
        try {
            // first check cache for same occurrence of variables etc. that have already been resolved
            AbstractResolvedNameInfo resolvedSubName = subNameCache.get(unresolvedName.getNameType());
            if (resolvedSubName == null) {
                resolvedSubName = create(unresolvedName, this, analyzer);
                if (resolvedSubName != null) {
                    doPostResolving(resolvedSubName);
                    subNameCache.put(unresolvedName.getNameType(), resolvedSubName);
                }
            }

            return new ResolvedName(unresolvedName.getOriginalName(), resolvedSubName);
        } catch (TemplatorException e) {
            errors.addResolvingError(unresolvedName.getOriginalName(), e);
        }
        return null;
    }

    protected abstract void doPostResolving(AbstractResolvedNameInfo subNameInfo) throws TemplatorException;

    public abstract void navigateTo();

    public abstract TemplateArgumentMap getTemplateArgumentMap();

    public ICPPTemplateArgument getArgument(ICPPTemplateParameter param) {
        ICPPTemplateArgument argument = getTemplateArgumentMap().getArgument(param);
        AbstractResolvedNameInfo parentNameInfo = getParent();
        while (argument == null && parentNameInfo != null) {
            argument = parentNameInfo.getTemplateArgumentMap().getArgument(param);
            parentNameInfo = parentNameInfo.getParent();
        }
        return argument;
    }

    public ICPPTemplateArgument getArgument(ICPPASTTemplateParameter astParam) {
        ICPPTemplateArgument argument = getTemplateArgumentMap().getArgument(astParam);
        AbstractResolvedNameInfo parentNameInfo = getParent();
        while (argument == null && parentNameInfo != null) {
            argument = parentNameInfo.getTemplateArgumentMap().getArgument(astParam);
            parentNameInfo = parentNameInfo.getParent();
        }
        return argument;
    }

    public void setMaxAutoResolvingDepth(int maxDepth) {
        autoResolver.setMaxDepth(maxDepth);
    }

    public IASTName getResolvingName() {
        return resolvingName;
    }

    public NameTypeKind getType() {
        return type;
    }

    public AbstractResolvedNameInfo getParent() {
        return parent;
    }

    public IBinding getBinding() {
        return binding;
    }

    public List<ResolvedName> getSubNames() {
        return subNames;
    }

    public IASTDeclaration getFormattedDefinition() {
        return formattedDefinition;
    }

    public void setFormattedDefinition(IASTDeclaration formattedDefinition) {
        this.formattedDefinition = formattedDefinition;
    }

    public IASTDeclaration getDefinition() {
        return definition;
    }

    public ASTAnalyzer getAnalyzer() {
        return analyzer;
    }

    public SubNameErrorCollection getSubNameErrors() {
        return subNameErrors;
    }

    /** For debug purposes only. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AbstractResolvedNameInfo ( " + getClass().getSimpleName() + " ) [" + hashCode() + "] \n");
        sb.append("\t resolvingName: " + Objects.toString(resolvingName) + " [" + resolvingName.hashCode() + "] \n");
        sb.append("\t type: " + Objects.toString(binding) + " [" + type.hashCode() + "] \n");
        sb.append("\t binding: " + Objects.toString(binding) + " [" + binding.hashCode() + "] \n");
        sb.append("\t argument map: ");
        if (binding instanceof ICPPSpecialization) {
            sb.append(((ICPPSpecialization) binding).getTemplateParameterMap());
        } else {
            sb.append('-');
        }
        sb.append('\n');
        sb.append("\t grandparent: \n");
        sb.append(new ASTWriter().write(resolvingName.getParent().getParent()).replaceAll("(?m)^", "\t\t") + '\n');
        sb.append("\t subNames: \n");
        for (ResolvedName resolvedName : getSubNames()) {
            sb.append("\t \t " + resolvedName.getOriginalNode() + " [" + resolvedName.getOriginalNode().hashCode() + "] \n");
        }
        return sb.toString();
    }

    protected static AbstractResolvedNameInfo createParent(UnresolvedNameInfo unresolvedName, AbstractResolvedNameInfo parent, ASTAnalyzer analyzer)
            throws TemplatorException {
        NameTypeKind type = unresolvedName.getType();
        IASTName resolvingName = unresolvedName.getResolvingName();

        ICPPASTTemplateDeclaration classDeclaration = null;
        if (type == NameTypeKind.METHOD_TEMPLATE) {
            classDeclaration = ASTTools.findFirstAncestorByType(resolvingName, ICPPASTTemplateDeclaration.class);
            classDeclaration = ASTTools.findFirstAncestorByType(classDeclaration, ICPPASTTemplateDeclaration.class);
        } else if (type == NameTypeKind.METHOD || type == NameTypeKind.DEFERRED_METHOD) {
            classDeclaration = ASTTools.findFirstAncestorByType(resolvingName, ICPPASTTemplateDeclaration.class);
        }

        AbstractResolvedNameInfo classTemplateInfo = null;
        if (parent != null && parent.getDefinition() == classDeclaration) {
            classTemplateInfo = parent;
        } else {
            IASTName definition = analyzer.getTypeDeducer().getDefinitionForName(resolvingName);
            RelevantNameType extractResolvingName = analyzer.extractResolvingName(definition, true, false);
            if (extractResolvingName != null) {
                TypeNameToType parentClassTemplate = analyzer.getType(extractResolvingName.getTypeName(), parent);
                classTemplateInfo = parentClassTemplate.getCurrentContext();
            }
        }
        return classTemplateInfo;
    }
}
