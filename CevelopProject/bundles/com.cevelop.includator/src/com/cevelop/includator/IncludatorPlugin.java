/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.helpers.IncludatorStatus;
import com.cevelop.includator.helpers.IncludatorStatusCollector;
import com.cevelop.includator.helpers.MarkerHelper;
import com.cevelop.includator.helpers.ResourceChangeListener;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.resources.IncludatorWorkspace;
import com.cevelop.includator.stores.DeclarationStore;
import com.cevelop.includator.stores.FirstLastElementIncludePathStore;
import com.cevelop.includator.stores.IncludePathStore;
import com.cevelop.includator.stores.IncludeSubstitutionStore;
import com.cevelop.includator.stores.IndexIncludeStore;
import com.cevelop.includator.stores.RecursiveIndexIncludeStore;
import com.cevelop.includator.stores.SuggestionStore;
import com.cevelop.includator.ui.helpers.DocumentPositionManager;


/**
 * The activator class controls the plug-in life cycle
 */
public class IncludatorPlugin extends AbstractUIPlugin {

    public static String PLUGIN_ID = "com.cevelop.includator";

    private static IncludatorPlugin plugin;

    private final ThreadLocal<IncludatorWorkspace> workspace;

    private final ThreadLocal<IWorkbenchWindow>                 workbenchWindow;
    private final ThreadLocal<FirstLastElementIncludePathStore> firstLastElementIncludePathStore;
    private static final SuggestionStore                        SUGGESTION_STORE = new SuggestionStore();
    private final IncludePathStore                              includePathStore;
    private final ThreadLocal<DeclarationStore>                 declarationStore;
    private final ThreadLocal<Integer>                          preferredLinkageID;
    private final IndexIncludeStore                             indexIncludeStore;
    private final RecursiveIndexIncludeStore                    recursiveIndexIncludeStore;
    private final IncludeSubstitutionStore                      includeSubstitutionStore;
    private final IncludatorStatusCollector                     statusCollector;

    private BundleContext bundleContext;

    public IncludatorPlugin() {
        workbenchWindow = new ThreadLocal<>();

        workspace = new ThreadLocal<>();
        statusCollector = new IncludatorStatusCollector();
        includePathStore = new IncludePathStore();
        firstLastElementIncludePathStore = new ThreadLocal<>();
        indexIncludeStore = new IndexIncludeStore();
        recursiveIndexIncludeStore = new RecursiveIndexIncludeStore();
        includeSubstitutionStore = new IncludeSubstitutionStore();
        declarationStore = new ThreadLocal<>();
        preferredLinkageID = new ThreadLocal<>();

        new DocumentPositionManager();
        new ResourceChangeListener();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        bundleContext = context;
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    public static IncludatorPlugin getDefault() {
        return plugin;
    }

    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    public static SuggestionStore getSuggestionStore() {
        return SUGGESTION_STORE;
    }

    public static IncludatorWorkspace getWorkspace() {
        return getDefault().workspace.get();
    }

    public static void log(String message, Exception exception) {
        CCorePlugin.log(message, exception);
    }

    public static void log(Throwable exception) {
        CCorePlugin.log(exception);
    }

    public static void log(String message) {
        CCorePlugin.log(message);
    }

    public static void log(IStatus status) {
        CCorePlugin.log(status);
    }

    public void clean() {
        clean(new NullProgressMonitor());
    }

    public void clean(IProgressMonitor monitor) {
        String msg = "Cleaning Includator environment";
        monitor.beginTask(msg, 100);
        monitor.setTaskName(msg);
        cleanWorkspace(SubMonitor.convert(monitor, 70));
        monitor.setTaskName("Cleaning cached marker data");
        SUGGESTION_STORE.clear();
        monitor.worked(15);
        monitor.setTaskName("Removing markers from user interface");
        MarkerHelper.removeAllIncludatorMarkers(workspace.get());
        includeSubstitutionStore.clear();
        monitor.worked(15);
        monitor.done();
    }

    public void cleanWorkspace() {
        cleanWorkspace(new NullProgressMonitor());
    }

    public void cleanWorkspace(IProgressMonitor monitor) {
        String msg = "Cleaning data structure";
        monitor.setTaskName(msg);
        workspace.get().clear();
        monitor.worked(3);
        statusCollector.clear();
        monitor.setTaskName("Cleaning cached include relations");
        includePathStore.clear();
        firstLastElementIncludePathStore.get().clear();
        indexIncludeStore.clear();
        recursiveIndexIncludeStore.clear();
        monitor.worked(3);
        monitor.setTaskName("Cleaning cached declaration information");
        declarationStore.get().clear();
        FileHelper.clean();
        monitor.worked(1);
        SubMonitor.done(monitor);
    }

    public static void collectStatus(MultiStatus status) {
        getDefault().statusCollector.collectStatus(status);
    }

    public static void logStatus(IStatus status, String affectedPath) {
        getDefault().statusCollector.logStatus(status, affectedPath);
    }

    public static void logStatus(IStatus status, IncludatorFile affectedFile) {
        logStatus(status, affectedFile.getSmartPath());
    }

    public static IncludePathStore getIncludePathStore() {
        return getDefault().includePathStore;
    }

    public static FirstLastElementIncludePathStore getFirstLastElementIncludePathStore() {
        return getDefault().firstLastElementIncludePathStore.get();
    }

    public static IndexIncludeStore getIndexIncludeStore() {
        return getDefault().indexIncludeStore;
    }

    public static RecursiveIndexIncludeStore getRecursiveIndexIncludeStore() {
        return getDefault().recursiveIndexIncludeStore;
    }

    public static IncludeSubstitutionStore getIncludeSubstitutionStore() {
        return getDefault().includeSubstitutionStore;
    }

    public static DeclarationStore getDeclarationStore() {
        return getDefault().declarationStore.get();
    }

    public static boolean initActiveWorkbenchWindow(IWorkbenchWindow window) {
        if (getDefault().workbenchWindow.get() != null) {
            return true;
        }
        getDefault().workbenchWindow.set(window);
        return false;
    }

    public static void resetActiveWorkbenchWindow() {
        getDefault().workbenchWindow.remove();
    }

    public static IWorkbenchWindow getActiveWorkbenchWindow() {
        IWorkbenchWindow activeThreadWindow = getDefault().workbenchWindow.get();
        return activeThreadWindow != null ? activeThreadWindow : PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    }

    public static void initActiveIncludatorWorkspace() {
        getDefault().workspace.set(new IncludatorWorkspace(ResourcesPlugin.getWorkspace()));
        getDefault().declarationStore.set(new DeclarationStore());
        getDefault().firstLastElementIncludePathStore.set(new FirstLastElementIncludePathStore());
    }

    public static void resetActiveIncludatorWorkspace() {
        getDefault().firstLastElementIncludePathStore.remove();
        getDefault().declarationStore.remove();
        getDefault().preferredLinkageID.remove();
        getDefault().workspace.remove();
    }

    public static void releaseCDTResources() {
        getIncludePathStore().clear();
        getFirstLastElementIncludePathStore().clear();
        getIndexIncludeStore().clear();
        getRecursiveIndexIncludeStore().clear();
        getDeclarationStore().clear();
        getWorkspace().purge();
    }

    public static int getPreferredLinkageID() {
        Integer integer = getDefault().preferredLinkageID.get();
        if (integer == null) {
            logStatus(new IncludatorStatus(IStatus.WARNING, new IncludatorException(
                    "Accessing preferred-linkage-id while not set. Falling back to C++ linkage.")), (String) null);
            getDefault().preferredLinkageID.set(GPPLanguage.getDefault().getLinkageID());
        }
        return getDefault().preferredLinkageID.get();
    }

    public static void initPreferredLinkageID(int preferredLinkageID) {
        getDefault().preferredLinkageID.set(preferredLinkageID);
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }
}
