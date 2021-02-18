package com.cevelop.gslator.charwarsstub.stubadapter;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;

import com.cevelop.gslator.charwarsstub.constants.PreferenceDefaults;
import com.cevelop.gslator.charwarsstub.constants.PreferenceKeys;


public class CharWarsPreferenceStoreAdapter implements IPreferenceStore {

    Map<String, Object> settings = new HashMap<>();

    public CharWarsPreferenceStoreAdapter() {
        settings.put(PreferenceKeys.GENERATE_GSL_PROJECT, PreferenceDefaults.GENERATE_GSL_PROJECT);
        settings.put(PreferenceKeys.GSL_PROJECT_NAME, PreferenceDefaults.GSL_PROJECT_NAME);
        settings.put(PreferenceKeys.GENERATE_GSL_INCLUDE, PreferenceDefaults.GENERATE_GSL_INCLUDE);
    }

    @Override
    public void addPropertyChangeListener(IPropertyChangeListener listener) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public boolean contains(String name) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public void firePropertyChangeEvent(String name, Object oldValue, Object newValue) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public boolean getBoolean(String name) {
        return (boolean) settings.get(name);
    }

    @Override
    public boolean getDefaultBoolean(String name) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public double getDefaultDouble(String name) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public float getDefaultFloat(String name) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public int getDefaultInt(String name) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public long getDefaultLong(String name) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public String getDefaultString(String name) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public double getDouble(String name) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public float getFloat(String name) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public int getInt(String name) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public long getLong(String name) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public String getString(String name) {
        return (String) settings.get(name);
    }

    @Override
    public boolean isDefault(String name) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public boolean needsSaving() {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public void putValue(String name, String value) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public void removePropertyChangeListener(IPropertyChangeListener listener) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public void setDefault(String name, double value) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public void setDefault(String name, float value) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public void setDefault(String name, int value) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public void setDefault(String name, long value) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public void setDefault(String name, String defaultObject) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public void setDefault(String name, boolean value) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public void setToDefault(String name) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public void setValue(String name, double value) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public void setValue(String name, float value) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public void setValue(String name, int value) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public void setValue(String name, long value) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public void setValue(String name, String value) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }

    @Override
    public void setValue(String name, boolean value) {
        throw new RuntimeException("Not implemented in CharWarsPreferenceStoreAdapter");
    }
}
