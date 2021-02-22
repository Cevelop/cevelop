package com.cevelop.ctylechecker.service.types;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.cevelop.ctylechecker.Activator;
import com.cevelop.ctylechecker.ids.IdHelper;
import com.cevelop.ctylechecker.service.ICtylecheckerRegistry;


public class CtylecheckerRuntime {

    private static CtylecheckerRuntime instance = new CtylecheckerRuntime();
    private ICtylecheckerRegistry      registry;

    public CtylecheckerRuntime() {
        registry = new CtylecheckerRegistry();
    }

    public ICtylecheckerRegistry getRegistry() {
        return registry;
    }

    public static CtylecheckerRuntime getInstance() {
        return instance;
    }

    public static void showMessage(String title, String message) {
        MessageBox messageBox = new MessageBox(new Shell());
        messageBox.setText(title);
        messageBox.setMessage(message);
        messageBox.open();
    }

    public static ImageDescriptor imageDescriptorFromPlugin(String path) {
        return AbstractUIPlugin.imageDescriptorFromPlugin(IdHelper.PLUGIN_ID, path);
    }

    public static IPreferenceStore getPreferenceStore() {
        return Activator.getDefault().getPreferenceStore();
    }

    public static String getPluginId() {
        return IdHelper.PLUGIN_ID;
    }

    public static void log(Exception e) {
        Activator.log(e);
    }

    public static void log(String e) {
        Activator.log(e);
    }
}
