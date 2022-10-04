package com.cevelop.codeanalysator.core.tests.unittests.stubs;

import java.net.URI;
import java.util.Map;

import org.eclipse.core.resources.FileInfoMatcherDescription;
import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceFilterDescription;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentTypeMatcher;
import org.eclipse.core.runtime.jobs.ISchedulingRule;


public class ProjectStub implements IProject {

   @Override
   public boolean isConflicting(ISchedulingRule rule) {
      return false;
   }

   @Override
   public boolean contains(ISchedulingRule rule) {
      return false;
   }

   @Override
   public <T> T getAdapter(Class<T> adapter) {
      return null;
   }

   @Override
   public void touch(IProgressMonitor monitor) throws CoreException {}

   @Override
   public void setTeamPrivateMember(boolean isTeamPrivate) throws CoreException {}

   @Override
   public void setSessionProperty(QualifiedName key, Object value) throws CoreException {}

   @Override
   public void setResourceAttributes(ResourceAttributes attributes) throws CoreException {}

   @Override
   public void setReadOnly(boolean readOnly) {}

   @Override
   public void setPersistentProperty(QualifiedName key, String value) throws CoreException {}

   @Override
   public long setLocalTimeStamp(long value) throws CoreException {
      return 0;
   }

   @Override
   public void setLocal(boolean flag, int depth, IProgressMonitor monitor) throws CoreException {}

   @Override
   public void setHidden(boolean isHidden) throws CoreException {}

   @Override
   public void setDerived(boolean isDerived, IProgressMonitor monitor) throws CoreException {}

   @Override
   public void setDerived(boolean isDerived) throws CoreException {}

   @Override
   public void revertModificationStamp(long value) throws CoreException {}

   @Override
   public void refreshLocal(int depth, IProgressMonitor monitor) throws CoreException {}

   @Override
   public void move(IProjectDescription description, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {}

   @Override
   public void move(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {}

   @Override
   public void move(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {}

   @Override
   public void move(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {}

   @Override
   public boolean isVirtual() {
      return false;
   }

   @Override
   public boolean isTeamPrivateMember(int options) {
      return false;
   }

   @Override
   public boolean isTeamPrivateMember() {
      return false;
   }

   @Override
   public boolean isSynchronized(int depth) {
      return false;
   }

   @Override
   public boolean isReadOnly() {
      return false;
   }

   @Override
   public boolean isPhantom() {
      return false;
   }

   @Override
   public boolean isLocal(int depth) {
      return false;
   }

   @Override
   public boolean isLinked(int options) {
      return false;
   }

   @Override
   public boolean isLinked() {
      return false;
   }

   @Override
   public boolean isHidden(int options) {
      return false;
   }

   @Override
   public boolean isHidden() {
      return false;
   }

   @Override
   public boolean isDerived(int options) {
      return false;
   }

   @Override
   public boolean isDerived() {
      return false;
   }

   @Override
   public boolean isAccessible() {
      return false;
   }

   @Override
   public IWorkspace getWorkspace() {
      return null;
   }

   @Override
   public int getType() {
      return 0;
   }

   @Override
   public Object getSessionProperty(QualifiedName key) throws CoreException {
      return null;
   }

   @Override
   public Map<QualifiedName, Object> getSessionProperties() throws CoreException {
      return null;
   }

   @Override
   public ResourceAttributes getResourceAttributes() {
      return null;
   }

   @Override
   public URI getRawLocationURI() {
      return null;
   }

   @Override
   public IPath getRawLocation() {
      return null;
   }

   @Override
   public IPath getProjectRelativePath() {
      return null;
   }

   @Override
   public IProject getProject() {
      return null;
   }

   @Override
   public String getPersistentProperty(QualifiedName key) throws CoreException {
      return null;
   }

   @Override
   public Map<QualifiedName, String> getPersistentProperties() throws CoreException {
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
   public String getName() {
      return null;
   }

   @Override
   public long getModificationStamp() {
      return 0;
   }

   @Override
   public IMarker getMarker(long id) {
      return null;
   }

   @Override
   public URI getLocationURI() {
      return null;
   }

   @Override
   public IPath getLocation() {
      return null;
   }

   @Override
   public long getLocalTimeStamp() {
      return 0;
   }

   @Override
   public IPath getFullPath() {
      return null;
   }

   @Override
   public String getFileExtension() {
      return null;
   }

   @Override
   public int findMaxProblemSeverity(String type, boolean includeSubtypes, int depth) throws CoreException {
      return 0;
   }

   @Override
   public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {
      return null;
   }

   @Override
   public IMarker findMarker(long id) throws CoreException {
      return null;
   }

   @Override
   public boolean exists() {
      return false;
   }

   @Override
   public void deleteMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {}

   @Override
   public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException {}

   @Override
   public void delete(boolean force, IProgressMonitor monitor) throws CoreException {}

   @Override
   public IResourceProxy createProxy() {
      return null;
   }

   @Override
   public IMarker createMarker(String type) throws CoreException {
      return null;
   }

   @Override
   public void copy(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {}

   @Override
   public void copy(IProjectDescription description, boolean force, IProgressMonitor monitor) throws CoreException {}

   @Override
   public void copy(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {}

   @Override
   public void copy(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {}

   @Override
   public void clearHistory(IProgressMonitor monitor) throws CoreException {}

   @Override
   public void accept(IResourceVisitor visitor, int depth, int memberFlags) throws CoreException {}

   @Override
   public void accept(IResourceVisitor visitor, int depth, boolean includePhantoms) throws CoreException {}

   @Override
   public void accept(IResourceProxyVisitor visitor, int depth, int memberFlags) throws CoreException {}

   @Override
   public void accept(IResourceProxyVisitor visitor, int memberFlags) throws CoreException {}

   @Override
   public void accept(IResourceVisitor visitor) throws CoreException {}

   @Override
   public void setDefaultCharset(String charset, IProgressMonitor monitor) throws CoreException {}

   @Override
   public void setDefaultCharset(String charset) throws CoreException {}

   @Override
   public IResource[] members(int memberFlags) throws CoreException {
      return null;
   }

   @Override
   public IResource[] members(boolean includePhantoms) throws CoreException {
      return null;
   }

   @Override
   public IResource[] members() throws CoreException {
      return null;
   }

   @Override
   public IFolder getFolder(IPath path) {
      return null;
   }

   @Override
   public IResourceFilterDescription[] getFilters() throws CoreException {
      return null;
   }

   @Override
   public IFile getFile(IPath path) {
      return null;
   }

   @Override
   public String getDefaultCharset(boolean checkImplicit) throws CoreException {
      return null;
   }

   @Override
   public String getDefaultCharset() throws CoreException {
      return null;
   }

   @Override
   public IResource findMember(IPath path, boolean includePhantoms) {
      return null;
   }

   @Override
   public IResource findMember(String path, boolean includePhantoms) {
      return null;
   }

   @Override
   public IResource findMember(IPath path) {
      return null;
   }

   @Override
   public IResource findMember(String path) {
      return null;
   }

   @Override
   public IFile[] findDeletedMembersWithHistory(int depth, IProgressMonitor monitor) throws CoreException {
      return null;
   }

   @Override
   public boolean exists(IPath path) {
      return false;
   }

   @Override
   public IResourceFilterDescription createFilter(int type, FileInfoMatcherDescription matcherDescription, int updateFlags, IProgressMonitor monitor)
         throws CoreException {
      return null;
   }

   @Override
   public void setDescription(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {}

   @Override
   public void setDescription(IProjectDescription description, IProgressMonitor monitor) throws CoreException {}

   @Override
   public void saveSnapshot(int options, URI snapshotLocation, IProgressMonitor monitor) throws CoreException {}

   @Override
   public void open(int updateFlags, IProgressMonitor monitor) throws CoreException {}

   @Override
   public void open(IProgressMonitor monitor) throws CoreException {}

   @Override
   public void move(IProjectDescription description, boolean force, IProgressMonitor monitor) throws CoreException {}

   @Override
   public void loadSnapshot(int options, URI snapshotLocation, IProgressMonitor monitor) throws CoreException {}

   @Override
   public boolean isOpen() {
      return false;
   }

   @Override
   public boolean isNatureEnabled(String natureId) throws CoreException {
      return false;
   }

   @Override
   public boolean hasNature(String natureId) throws CoreException {
      return false;
   }

   @Override
   public boolean hasBuildConfig(String configName) throws CoreException {
      return false;
   }

   @Override
   public IPath getWorkingLocation(String id) {
      return null;
   }

   @Override
   public IProject[] getReferencingProjects() {
      return null;
   }

   @Override
   public IProject[] getReferencedProjects() throws CoreException {
      return null;
   }

   @Override
   public IBuildConfiguration[] getReferencedBuildConfigs(String configName, boolean includeMissing) throws CoreException {
      return null;
   }

   @Override
   public IProjectNature getNature(String natureId) throws CoreException {
      return null;
   }

   @Override
   public IFolder getFolder(String name) {
      return null;
   }

   @Override
   public IFile getFile(String name) {
      return null;
   }

   @Override
   public IProjectDescription getDescription() throws CoreException {
      return null;
   }

   @Override
   public IContentTypeMatcher getContentTypeMatcher() throws CoreException {
      return null;
   }

   @Override
   public IBuildConfiguration[] getBuildConfigs() throws CoreException {
      return null;
   }

   @Override
   public IBuildConfiguration getBuildConfig(String configName) throws CoreException {
      return null;
   }

   @Override
   public IBuildConfiguration getActiveBuildConfig() throws CoreException {
      return null;
   }

   @Override
   public void delete(boolean deleteContent, boolean force, IProgressMonitor monitor) throws CoreException {}

   @Override
   public void create(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {}

   @Override
   public void create(IProjectDescription description, IProgressMonitor monitor) throws CoreException {}

   @Override
   public void create(IProgressMonitor monitor) throws CoreException {}

   @Override
   public void close(IProgressMonitor monitor) throws CoreException {}

   @Override
   public void clearCachedDynamicReferences() {}

   @Override
   public void build(int kind, String builderName, Map<String, String> args, IProgressMonitor monitor) throws CoreException {}

   @Override
   public void build(IBuildConfiguration config, int kind, IProgressMonitor monitor) throws CoreException {}

   @Override
   public void build(int kind, IProgressMonitor monitor) throws CoreException {}

@Override
public String getDefaultLineSeparator() throws CoreException {
    // TODO(sop) Auto-generated method stub
    return null;
}
}
