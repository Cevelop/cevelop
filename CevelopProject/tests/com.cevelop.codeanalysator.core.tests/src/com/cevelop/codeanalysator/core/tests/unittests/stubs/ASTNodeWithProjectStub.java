package com.cevelop.codeanalysator.core.tests.unittests.stubs;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.dom.ILinkage;
import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.ExpansionOverlapsBoundaryException;
import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTCompletionNode;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.cdt.core.dom.ast.IASTNodeSelector;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroExpansion;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.INodeFactory;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFileSet;
import org.eclipse.cdt.core.model.BufferChangedEvent;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.IBuffer;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICElementVisitor;
import org.eclipse.cdt.core.model.ICModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.IInclude;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.model.INamespace;
import org.eclipse.cdt.core.model.IProblemRequestor;
import org.eclipse.cdt.core.model.ISourceRange;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.model.IUsing;
import org.eclipse.cdt.core.model.IWorkingCopy;
import org.eclipse.cdt.core.parser.CodeReader;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.ISignificantMacros;
import org.eclipse.cdt.core.parser.IToken;
import org.eclipse.cdt.core.parser.ParserLanguage;
import org.eclipse.cdt.internal.core.model.IBufferFactory;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.jobs.ISchedulingRule;


@SuppressWarnings("restriction")
public class ASTNodeWithProjectStub implements IASTNode {

   private final boolean  nullResource;
   private final IProject project;

   private ASTNodeWithProjectStub(boolean nullResource, IProject project) {
      this.nullResource = nullResource;
      this.project = project;
   }

   public static ASTNodeWithProjectStub nodeWithNullResource() {
      return new ASTNodeWithProjectStub(true, null);
   }

   public static ASTNodeWithProjectStub nodeWithProject(IProject project) {
      return new ASTNodeWithProjectStub(false, project);
   }

   @Override
   public IASTTranslationUnit getTranslationUnit() {
      return new ASTTranslationUnitWithProjectStub(); // stub
   }

   @Override
   public IASTNodeLocation[] getNodeLocations() {
      return null;
   }

   @Override
   public IASTFileLocation getFileLocation() {
      return null;
   }

   @Override
   public String getContainingFilename() {
      return null;
   }

   @Override
   public boolean isPartOfTranslationUnitFile() {
      return false;
   }

   @Override
   public IASTNode getParent() {
      return null;
   }

   @Override
   public IASTNode[] getChildren() {
      return null;
   }

   @Override
   public void setParent(IASTNode node) {}

   @Override
   public ASTNodeProperty getPropertyInParent() {
      return null;
   }

   @Override
   public void setPropertyInParent(ASTNodeProperty property) {}

   @Override
   public boolean accept(ASTVisitor visitor) {
      return false;
   }

   @Override
   public String getRawSignature() {
      return null;
   }

   @Override
   public boolean contains(IASTNode node) {
      return false;
   }

   @Override
   public IToken getLeadingSyntax() throws ExpansionOverlapsBoundaryException, UnsupportedOperationException {
      return null;
   }

   @Override
   public IToken getTrailingSyntax() throws ExpansionOverlapsBoundaryException, UnsupportedOperationException {
      return null;
   }

   @Override
   public IToken getSyntax() throws ExpansionOverlapsBoundaryException {
      return null;
   }

   @Override
   public boolean isFrozen() {
      return false;
   }

   @Override
   public boolean isActive() {
      return false;
   }

   @Override
   public IASTNode copy() {
      return null;
   }

   @Override
   public IASTNode copy(CopyStyle style) {
      return null;
   }

   @Override
   public IASTNode getOriginalNode() {
      return this;
   }

   private class ASTTranslationUnitWithProjectStub implements IASTTranslationUnit {

      @Override
      public IASTDeclaration[] getDeclarations(boolean includeInactive) {
         return null;
      }

      @Override
      public IASTTranslationUnit getTranslationUnit() {
         return null;
      }

      @Override
      public IASTNodeLocation[] getNodeLocations() {
         return null;
      }

      @Override
      public IASTFileLocation getFileLocation() {
         return null;
      }

      @Override
      public String getContainingFilename() {
         return null;
      }

      @Override
      public boolean isPartOfTranslationUnitFile() {
         return false;
      }

      @Override
      public IASTNode getParent() {
         return null;
      }

      @Override
      public IASTNode[] getChildren() {
         return null;
      }

      @Override
      public void setParent(IASTNode node) {}

      @Override
      public ASTNodeProperty getPropertyInParent() {
         return null;
      }

      @Override
      public void setPropertyInParent(ASTNodeProperty property) {}

      @Override
      public boolean accept(ASTVisitor visitor) {
         return false;
      }

      @Override
      public String getRawSignature() {
         return null;
      }

      @Override
      public boolean contains(IASTNode node) {
         return false;
      }

      @Override
      public IToken getLeadingSyntax() throws ExpansionOverlapsBoundaryException, UnsupportedOperationException {
         return null;
      }

      @Override
      public IToken getTrailingSyntax() throws ExpansionOverlapsBoundaryException, UnsupportedOperationException {
         return null;
      }

      @Override
      public IToken getSyntax() throws ExpansionOverlapsBoundaryException {
         return null;
      }

      @Override
      public boolean isFrozen() {
         return false;
      }

      @Override
      public boolean isActive() {
         return false;
      }

      @Override
      public IASTNode getOriginalNode() {
         return null;
      }

      @Override
      public ISignificantMacros getSignificantMacros() throws CoreException {
         return null;
      }

      @Override
      public boolean hasPragmaOnceSemantics() throws CoreException {
         return false;
      }

      @Override
      public <T> T getAdapter(Class<T> adapter) {
         return null;
      }

      @Override
      public IASTDeclaration[] getDeclarations() {
         return null;
      }

      @Override
      public void addDeclaration(IASTDeclaration declaration) {}

      @Override
      public IScope getScope() {
         return null;
      }

      @Override
      public IName[] getDeclarations(IBinding binding) {
         return null;
      }

      @Override
      public IASTName[] getDeclarationsInAST(IBinding binding) {
         return null;
      }

      @Override
      public IName[] getDefinitions(IBinding binding) {
         return null;
      }

      @Override
      public IASTName[] getDefinitionsInAST(IBinding binding) {
         return null;
      }

      @Override
      public IASTName[] getDefinitionsInAST(IBinding binding, boolean permissive) {
         return null;
      }

      @Override
      public IASTName[] getReferences(IBinding binding) {
         return null;
      }

      @Override
      public IASTNodeSelector getNodeSelector(String filePath) {
         return null;
      }

      @Override
      public IASTNode selectNodeForLocation(String path, int offset, int length) {
         return null;
      }

      @Override
      public IASTPreprocessorMacroDefinition[] getMacroDefinitions() {
         return null;
      }

      @Override
      public IASTPreprocessorMacroDefinition[] getBuiltinMacroDefinitions() {
         return null;
      }

      @Override
      public IASTPreprocessorIncludeStatement[] getIncludeDirectives() {
         return null;
      }

      @Override
      public IASTPreprocessorStatement[] getAllPreprocessorStatements() {
         return null;
      }

      @Override
      public IASTPreprocessorMacroExpansion[] getMacroExpansions() {
         return null;
      }

      @Override
      public IASTProblem[] getPreprocessorProblems() {
         return null;
      }

      @Override
      public int getPreprocessorProblemsCount() {
         return 0;
      }

      @Override
      public String getFilePath() {
         return null;
      }

      @Override
      public IASTFileLocation flattenLocationsToFile(IASTNodeLocation[] nodeLocations) {
         return null;
      }

      @Override
      public IDependencyTree getDependencyTree() {
         return null;
      }

      @Override
      public String getContainingFilename(int offset) {
         return null;
      }

      @Override
      public ParserLanguage getParserLanguage() {
         return null;
      }

      @Override
      public IIndex getIndex() {
         return null;
      }

      @Override
      public IIndexFileSet getIndexFileSet() {
         return null;
      }

      @Override
      public IIndexFileSet getASTFileSet() {
         return null;
      }

      @Override
      public IASTComment[] getComments() {
         return null;
      }

      @Override
      public ILinkage getLinkage() {
         return null;
      }

      @Override
      public boolean isHeaderUnit() {
         return false;
      }

      @Override
      public INodeFactory getASTNodeFactory() {
         return null;
      }

      @Override
      public void setIndex(IIndex index) {}

      @Override
      public void setIsHeaderUnit(boolean headerUnit) {}

      @Override
      public void freeze() {}

      @Override
      public IASTTranslationUnit copy() {
         return null;
      }

      @Override
      public IASTTranslationUnit copy(CopyStyle style) {
         return null;
      }

      @Override
      public ITranslationUnit getOriginatingTranslationUnit() {
         return new TranslationUnitWithProjectStub(); // stub
      }

      @Override
      public boolean isBasedOnIncompleteIndex() {
         return false;
      }

      @Override
      public void setSignificantMacros(ISignificantMacros sigMacros) {}

      @Override
      public void setPragmaOnceSemantics(boolean value) {}

      @Override
      public boolean hasNodesOmitted() {
         return false;
      }

      @Override
      public void setHasNodesOmitted(boolean nodesOmitted) {}
   }

   private class TranslationUnitWithProjectStub implements ITranslationUnit {

      @Override
      public boolean exists() {
         return false;
      }

      @Override
      public ICElement getAncestor(int ancestorType) {
         return null;
      }

      @Override
      public String getElementName() {
         return null;
      }

      @Override
      public int getElementType() {
         return 0;
      }

      @Override
      public ICModel getCModel() {
         return null;
      }

      @Override
      public ICProject getCProject() {
         return null;
      }

      @Override
      public ICElement getParent() {
         return null;
      }

      @Override
      public IPath getPath() {
         return null;
      }

      @Override
      public URI getLocationURI() {
         return null;
      }

      @Override
      public IResource getUnderlyingResource() {
         return nullResource ? null : new ResourceWithProjectStub(); // stub
      }

      @Override
      public IResource getResource() {
         return null;
      }

      @Override
      public boolean isReadOnly() {
         return false;
      }

      @Override
      public boolean isStructureKnown() throws CModelException {
         return false;
      }

      @Override
      public void accept(ICElementVisitor visitor) throws CoreException {}

      @Override
      public String getHandleIdentifier() {
         return null;
      }

      @Override
      public <T> T getAdapter(Class<T> adapter) {
         return null;
      }

      @Override
      public ICElement[] getChildren() throws CModelException {
         return null;
      }

      @Override
      public List<ICElement> getChildrenOfType(int type) throws CModelException {
         return null;
      }

      @Override
      public boolean hasChildren() {
         return false;
      }

      @Override
      public void close() throws CModelException {}

      @Override
      public IBuffer getBuffer() throws CModelException {
         return null;
      }

      @Override
      public boolean hasUnsavedChanges() throws CModelException {
         return false;
      }

      @Override
      public boolean isConsistent() throws CModelException {
         return false;
      }

      @Override
      public boolean isOpen() {
         return false;
      }

      @Override
      public void makeConsistent(IProgressMonitor progress) throws CModelException {}

      @Override
      public void makeConsistent(IProgressMonitor progress, boolean forced) throws CModelException {}

      @Override
      public void open(IProgressMonitor progress) throws CModelException {}

      @Override
      public void save(IProgressMonitor progress, boolean force) throws CModelException {}

      @Override
      public void bufferChanged(BufferChangedEvent event) {}

      @Override
      public String getSource() throws CModelException {
         return null;
      }

      @Override
      public ISourceRange getSourceRange() throws CModelException {
         return null;
      }

      @Override
      public ITranslationUnit getTranslationUnit() {
         return null;
      }

      @Override
      public boolean isActive() {
         return false;
      }

      @Override
      public int getIndex() {
         return 0;
      }

      @Override
      public void copy(ICElement container, ICElement sibling, String rename, boolean replace, IProgressMonitor monitor) throws CModelException {}

      @Override
      public void delete(boolean force, IProgressMonitor monitor) throws CModelException {}

      @Override
      public void move(ICElement container, ICElement sibling, String rename, boolean replace, IProgressMonitor monitor) throws CModelException {}

      @Override
      public void rename(String name, boolean replace, IProgressMonitor monitor) throws CModelException {}

      @Override
      public IInclude createInclude(String name, boolean isStd, ICElement sibling, IProgressMonitor monitor) throws CModelException {
         return null;
      }

      @Override
      public IUsing createUsing(String name, boolean isDirective, ICElement sibling, IProgressMonitor monitor) throws CModelException {
         return null;
      }

      @Override
      public INamespace createNamespace(String namespace, ICElement sibling, IProgressMonitor monitor) throws CModelException {
         return null;
      }

      @Override
      public IWorkingCopy findSharedWorkingCopy() {
         return null;
      }

      @Override
      public char[] getContents() {
         return null;
      }

      @Override
      public ICElement getElementAtLine(int line) throws CModelException {
         return null;
      }

      @Override
      public ICElement getElementAtOffset(int offset) throws CModelException {
         return null;
      }

      @Override
      public ICElement[] getElementsAtOffset(int offset) throws CModelException {
         return null;
      }

      @Override
      public ICElement getElement(String name) throws CModelException {
         return null;
      }

      @Override
      public IInclude getInclude(String name) {
         return null;
      }

      @Override
      public IInclude[] getIncludes() throws CModelException {
         return null;
      }

      @Override
      public IWorkingCopy getSharedWorkingCopy(IProgressMonitor monitor, IProblemRequestor requestor) throws CModelException {
         return null;
      }

      @Override
      public IUsing getUsing(String name) {
         return null;
      }

      @Override
      public IUsing[] getUsings() throws CModelException {
         return null;
      }

      @Override
      public INamespace getNamespace(String name) {
         return null;
      }

      @Override
      public INamespace[] getNamespaces() throws CModelException {
         return null;
      }

      @Override
      public boolean isHeaderUnit() {
         return false;
      }

      @Override
      public boolean isSourceUnit() {
         return false;
      }

      @Override
      public boolean isCLanguage() {
         return false;
      }

      @Override
      public boolean isCXXLanguage() {
         return false;
      }

      @Override
      public boolean isASMLanguage() {
         return false;
      }

      @Override
      public IWorkingCopy getWorkingCopy() throws CModelException {
         return null;
      }

      @Override
      public IWorkingCopy getWorkingCopy(IProgressMonitor monitor) throws CModelException {
         return null;
      }

      @Override
      public String getContentTypeId() {
         return null;
      }

      @Override
      public boolean isWorkingCopy() {
         return false;
      }

      @Override
      public ILanguage getLanguage() throws CoreException {
         return null;
      }

      @Override
      public void setIsStructureKnown(boolean wasSuccessful) {}

      @Override
      public IPath getLocation() {
         return null;
      }

      @Override
      public IFile getFile() {
         return null;
      }

      @Override
      public IScannerInfo getScannerInfo(boolean force) {
         return null;
      }

      @Override
      public IASTTranslationUnit getAST() throws CoreException {
         return null;
      }

      @Override
      public IASTTranslationUnit getAST(IIndex index, int style) throws CoreException {
         return null;
      }

      @Override
      public IASTCompletionNode getCompletionNode(IIndex index, int style, int offset) throws CoreException {
         return null;
      }

      @Override
      public IWorkingCopy getSharedWorkingCopy(IProgressMonitor monitor, IBufferFactory factory) throws CModelException {
         return null;
      }

      @Override
      public IWorkingCopy getSharedWorkingCopy(IProgressMonitor monitor, IBufferFactory factory, IProblemRequestor requestor) throws CModelException {
         return null;
      }

      @Override
      public IWorkingCopy findSharedWorkingCopy(IBufferFactory bufferFactory) {
         return null;
      }

      @Override
      public IWorkingCopy getWorkingCopy(IProgressMonitor monitor, IBufferFactory factory) throws CModelException {
         return null;
      }

      @Override
      public Map<?, ?> parse() {
         return null;
      }

      @Override
      public CodeReader getCodeReader() {
         return null;
      }
   }

   private class ResourceWithProjectStub implements IResource {

      @Override
      public <T> T getAdapter(Class<T> adapter) {
         return null;
      }

      @Override
      public boolean contains(ISchedulingRule rule) {
         return false;
      }

      @Override
      public boolean isConflicting(ISchedulingRule rule) {
         return false;
      }

      @Override
      public void accept(IResourceProxyVisitor visitor, int memberFlags) throws CoreException {}

      @Override
      public void accept(IResourceProxyVisitor visitor, int depth, int memberFlags) throws CoreException {}

      @Override
      public void accept(IResourceVisitor visitor) throws CoreException {}

      @Override
      public void accept(IResourceVisitor visitor, int depth, boolean includePhantoms) throws CoreException {}

      @Override
      public void accept(IResourceVisitor visitor, int depth, int memberFlags) throws CoreException {}

      @Override
      public void clearHistory(IProgressMonitor monitor) throws CoreException {}

      @Override
      public void copy(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {}

      @Override
      public void copy(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {}

      @Override
      public void copy(IProjectDescription description, boolean force, IProgressMonitor monitor) throws CoreException {}

      @Override
      public void copy(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {}

      @Override
      public IMarker createMarker(String type) throws CoreException {
         return null;
      }

      @Override
      public IResourceProxy createProxy() {
         return null;
      }

      @Override
      public void delete(boolean force, IProgressMonitor monitor) throws CoreException {}

      @Override
      public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException {}

      @Override
      public void deleteMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {}

      @Override
      public boolean exists() {
         return false;
      }

      @Override
      public IMarker findMarker(long id) throws CoreException {
         return null;
      }

      @Override
      public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {
         return null;
      }

      @Override
      public int findMaxProblemSeverity(String type, boolean includeSubtypes, int depth) throws CoreException {
         return 0;
      }

      @Override
      public String getFileExtension() {
         return null;
      }

      @Override
      public IPath getFullPath() {
         return null;
      }

      @Override
      public long getLocalTimeStamp() {
         return 0;
      }

      @Override
      public IPath getLocation() {
         return null;
      }

      @Override
      public URI getLocationURI() {
         return null;
      }

      @Override
      public IMarker getMarker(long id) {
         return null;
      }

      @Override
      public long getModificationStamp() {
         return 0;
      }

      @Override
      public String getName() {
         return null;
      }

      @Override
      public IPathVariableManager getPathVariableManager() {
         return null;
      }

      @Override
      public IContainer getParent() {
         return null;
      }

      @Override
      public Map<QualifiedName, String> getPersistentProperties() throws CoreException {
         return null;
      }

      @Override
      public String getPersistentProperty(QualifiedName key) throws CoreException {
         return null;
      }

      @Override
      public IProject getProject() {
         return project; // stub
      }

      @Override
      public IPath getProjectRelativePath() {
         return null;
      }

      @Override
      public IPath getRawLocation() {
         return null;
      }

      @Override
      public URI getRawLocationURI() {
         return null;
      }

      @Override
      public ResourceAttributes getResourceAttributes() {
         return null;
      }

      @Override
      public Map<QualifiedName, Object> getSessionProperties() throws CoreException {
         return null;
      }

      @Override
      public Object getSessionProperty(QualifiedName key) throws CoreException {
         return null;
      }

      @Override
      public int getType() {
         return 0;
      }

      @Override
      public IWorkspace getWorkspace() {
         return null;
      }

      @Override
      public boolean isAccessible() {
         return false;
      }

      @Override
      public boolean isDerived() {
         return false;
      }

      @Override
      public boolean isDerived(int options) {
         return false;
      }

      @Override
      public boolean isHidden() {
         return false;
      }

      @Override
      public boolean isHidden(int options) {
         return false;
      }

      @Override
      public boolean isLinked() {
         return false;
      }

      @Override
      public boolean isVirtual() {
         return false;
      }

      @Override
      public boolean isLinked(int options) {
         return false;
      }

      @Override
      public boolean isLocal(int depth) {
         return false;
      }

      @Override
      public boolean isPhantom() {
         return false;
      }

      @Override
      public boolean isReadOnly() {
         return false;
      }

      @Override
      public boolean isSynchronized(int depth) {
         return false;
      }

      @Override
      public boolean isTeamPrivateMember() {
         return false;
      }

      @Override
      public boolean isTeamPrivateMember(int options) {
         return false;
      }

      @Override
      public void move(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {}

      @Override
      public void move(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {}

      @Override
      public void move(IProjectDescription description, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {}

      @Override
      public void move(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {}

      @Override
      public void refreshLocal(int depth, IProgressMonitor monitor) throws CoreException {}

      @Override
      public void revertModificationStamp(long value) throws CoreException {}

      @Override
      public void setDerived(boolean isDerived) throws CoreException {}

      @Override
      public void setDerived(boolean isDerived, IProgressMonitor monitor) throws CoreException {}

      @Override
      public void setHidden(boolean isHidden) throws CoreException {}

      @Override
      public void setLocal(boolean flag, int depth, IProgressMonitor monitor) throws CoreException {}

      @Override
      public long setLocalTimeStamp(long value) throws CoreException {
         return 0;
      }

      @Override
      public void setPersistentProperty(QualifiedName key, String value) throws CoreException {}

      @Override
      public void setReadOnly(boolean readOnly) {}

      @Override
      public void setResourceAttributes(ResourceAttributes attributes) throws CoreException {}

      @Override
      public void setSessionProperty(QualifiedName key, Object value) throws CoreException {}

      @Override
      public void setTeamPrivateMember(boolean isTeamPrivate) throws CoreException {}

      @Override
      public void touch(IProgressMonitor monitor) throws CoreException {}
   }
}
