package com.cevelop.codeanalysator.core;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.cevelop.codeanalysator.core.guideline.GuidelinePreferencesImpl;
import com.cevelop.codeanalysator.core.guideline.GuidelinePriorityResolverImpl;
import com.cevelop.codeanalysator.core.guideline.GuidelineRegistry;
import com.cevelop.codeanalysator.core.guideline.IGuidelinePreferences;
import com.cevelop.codeanalysator.core.guideline.IGuidelinePriorityResolver;
import com.cevelop.codeanalysator.core.guideline.RuleRegistry;
import com.cevelop.codeanalysator.core.preference.PropAndPrefHelper;


/**
 * The activator class controls the plug-in life cycle
 */
public class CodeAnalysatorRuntime extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "com.cevelop.codeanalysator.core"; //$NON-NLS-1$

    // The shared instance
    private static CodeAnalysatorRuntime plugin;

    private PropAndPrefHelper             propAndPrefHelper;
    private GuidelinePreferencesImpl      guidelinePreferences;
    private GuidelinePriorityResolverImpl priorityResolver;
    private RuleRegistry                  ruleRegistry;
    private GuidelineRegistry             guidelineRegistry;

    /**
     * The constructor
     */
    public CodeAnalysatorRuntime() {
        guidelinePreferences = new GuidelinePreferencesImpl();
        priorityResolver = new GuidelinePriorityResolverImpl();
        ruleRegistry = new RuleRegistry(priorityResolver);
        guidelineRegistry = new GuidelineRegistry();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        propAndPrefHelper = new PropAndPrefHelper(getPreferenceStore());
        guidelinePreferences.startListening(propAndPrefHelper);
        priorityResolver.startListening(guidelinePreferences);

        guidelineRegistry.loadExtensions();

        priorityResolver.computePriorityOrderings();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        guidelinePreferences.stopListening();

        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static CodeAnalysatorRuntime getDefault() {
        return plugin;
    }

    public PropAndPrefHelper getPropAndPrefHelper() {
        return propAndPrefHelper;
    }

    public IGuidelinePreferences getGuidelinePreferences() {
        return guidelinePreferences;
    }

    public IGuidelinePriorityResolver getPriorityResolver() {
        return priorityResolver;
    }

    public RuleRegistry getRuleRegistry() {
        return ruleRegistry;
    }

    public GuidelineRegistry getGuidelineRegistry() {
        return guidelineRegistry;
    }
}
