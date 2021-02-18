package com.cevelop.constificator.core.deciders.rules;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFile;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.index.IIndexInclude;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPSemantics;
import org.eclipse.cdt.internal.ui.refactoring.includes.IncludeUtil;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.constificator.core.Activator;
import com.cevelop.constificator.core.util.ast.ASTRewriteCache;
import com.cevelop.constificator.core.util.semantic.Function;
import com.cevelop.constificator.core.util.semantic.Function.MatchStyle;
import com.cevelop.constificator.core.util.semantic.Type;
import com.cevelop.constificator.core.util.structural.Relation;
import com.cevelop.constificator.core.util.type.Cast;


@SuppressWarnings("restriction")
public class Functions {

    public static Set<ICPPASTFunctionDeclarator> declarationsFor(ICPPASTName name, boolean includeOverloads, ASTRewriteCache cache) {
        Set<ICPPASTFunctionDeclarator> decls = new HashSet<>();

        if (name == null) {
            return decls;
        }

        ICPPFunction called;
        if ((called = Cast.as(ICPPFunction.class, name.resolveBinding())) == null) {
            return decls;
        }

        String sourceFile = name.getContainingFilename();
        IIndex sourceIndex = name.getTranslationUnit().getIndex();
        IIndexInclude[] sourceIncludes = new IIndexInclude[0];

        try {
            sourceIndex.acquireReadLock();
            IIndexFile[] indexFiles = sourceIndex.getAllFiles();

            for (IIndexFile file : indexFiles) {
                String filePath = IncludeUtil.getPath(file);
                if (filePath.equals(sourceFile)) {
                    sourceIncludes = sourceIndex.findIncludes(file, IIndex.DEPTH_INFINITE);
                }
            }
        } catch (CoreException | InterruptedException e) {
            Activator.getDefault().logException("Failed to collect includes!", e);
        } finally {
            sourceIndex.releaseReadLock();
        }

        IBinding[] bindings = CPPSemantics.findBindingsForContentAssist(name, false, null);

        final MatchStyle matchStyle = includeOverloads ? MatchStyle.IgnoreConstQualification : MatchStyle.Exact;

        for (IBinding current : bindings) {
            if (current instanceof ICPPFunction) {
                if (Function.haveSameType(called, (ICPPFunction) current, matchStyle)) {
                    IIndex index = name.getTranslationUnit().getIndex();
                    ICProject project = name.getTranslationUnit().getOriginatingTranslationUnit().getCProject();

                    try {
                        index.acquireReadLock();
                        IIndexName[] declarations = index.findNames(current, IIndex.FIND_DECLARATIONS_DEFINITIONS);

                        for (IIndexName declaration : declarations) {
                            IIndexFileLocation file = declaration.getFile().getLocation();
                            String fileLocation = declaration.getFileLocation().getFileName();
                            if (fileLocation.equals(sourceFile)) {
                                Functions.getDeclarations(cache, decls, project, declaration, file);
                            } else {
                                for (IIndexInclude include : sourceIncludes) {
                                    if (include.getIncludesLocation() == file) {
                                        Functions.getDeclarations(cache, decls, project, declaration, file);
                                    }
                                }
                            }
                        }
                    } catch (CoreException | InterruptedException e) {
                        Activator.getDefault().logException("Failed to get declarations for " + current.getName(), e);
                    } finally {
                        index.releaseReadLock();
                    }

                }
            }
        }

        return decls;
    }

    private static void getDeclarations(ASTRewriteCache cache, Set<ICPPASTFunctionDeclarator> decls, ICProject project, IIndexName declaration,
            IIndexFileLocation file) throws CModelException {
        ITranslationUnit tu = CoreModelUtil.findTranslationUnitForLocation(file, project);
        IASTTranslationUnit ast = cache.getASTTranslationUnit(tu);
        IASTName currentName = ast.getNodeSelector(null).findName(declaration.getNodeOffset(), declaration.getNodeLength());
        ICPPASTFunctionDeclarator node;
        if ((node = Relation.getAncestorOf(ICPPASTFunctionDeclarator.class, currentName)) != null) {
            decls.add(node);
        }
    }

    public static boolean hasConstOverload(ICPPASTName name, int parameterIndex, int pointerLevel, ASTRewriteCache cache) {
        Set<ICPPASTFunctionDeclarator> declarations = Functions.declarationsFor(name, true, cache);

        if (name == null) {
            return false;
        }

        ICPPFunction called;
        if ((called = Cast.as(ICPPFunction.class, name.resolveBinding())) == null) {
            return false;
        }

        if (called.getParameters().length <= parameterIndex) {
            return false;
        }

        ICPPParameter calledParameter = called.getParameters()[parameterIndex];
        for (ICPPASTFunctionDeclarator decl : declarations) {
            ICPPParameter currentParameter = Cast.as(ICPPParameter.class, decl.getParameters()[parameterIndex].getDeclarator().getName()
                    .resolveBinding());

            if (Type.isMoreConst(currentParameter.getType(), calledParameter.getType())) {
                return true;
            }
        }

        return false;
    }

}
