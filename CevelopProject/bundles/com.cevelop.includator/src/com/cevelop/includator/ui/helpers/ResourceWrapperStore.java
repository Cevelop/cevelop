package com.cevelop.includator.ui.helpers;

import java.util.Properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;


public class ResourceWrapperStore implements IPreferenceStore {

    private IResource resource;

    private Properties defaultProperties;

    public ResourceWrapperStore() {
        defaultProperties = new Properties();
    }

    @Override
    public void addPropertyChangeListener(IPropertyChangeListener listener) {}

    @Override
    public boolean contains(String name) {
        try {
            return (resource.getPersistentProperties().containsKey(getQualifiedName(name)) || defaultProperties.containsKey(name));
        } catch (CoreException e) {
            return false;
        }
    }

    /**
     * Compared to <i>getString</i>, <i>internalGetString</i> may also return <b>null</b>.
     *
     * @param name
     * @return
     * @throws CoreException
     */
    private String internalGetString(String name) {
        try {
            String value = resource.getPersistentProperty(getQualifiedName(name));
            if (value == null) {
                value = defaultProperties.getProperty(name);
            }
            return value;
        } catch (CoreException e) {
            return null;
        }
    }

    private String internalGetDefaultString(String name) {
        return defaultProperties.getProperty(name);
    }

    private QualifiedName getQualifiedName(String name) {
        int pos = name.lastIndexOf('.');
        String namePart = (pos == -1) ? name : name.substring(pos + 1);
        String qualifierPart = (pos == -1) ? "" : name.substring(0, pos);
        return new QualifiedName(qualifierPart, namePart);
    }

    private boolean parseBoolean(String value) {
        return Boolean.parseBoolean(value);
    }

    private double parseDouble(String value) {
        return Double.parseDouble(value);
    }

    private float parseFloat(String value) {
        return Float.parseFloat(value);
    }

    private int parseInt(String value) {
        return Integer.parseInt(value);
    }

    private long parseLong(String value) {
        return Long.parseLong(value);
    }

    @Override
    public boolean getBoolean(String name) {
        return parseBoolean(internalGetString(name)); // return false when parsing null.
    }

    @Override
    public boolean getDefaultBoolean(String name) {
        return parseBoolean(internalGetDefaultString(name));
    }

    @Override
    public double getDefaultDouble(String name) {
        return parseDouble(internalGetDefaultString(name));
    }

    @Override
    public float getDefaultFloat(String name) {
        return parseFloat(internalGetDefaultString(name));
    }

    @Override
    public int getDefaultInt(String name) {
        return parseInt(internalGetDefaultString(name));
    }

    @Override
    public long getDefaultLong(String name) {
        return parseLong(internalGetDefaultString(name));
    }

    @Override
    public String getDefaultString(String name) {
        String value = internalGetDefaultString(name);
        return value == null ? STRING_DEFAULT_DEFAULT : value;
    }

    @Override
    public double getDouble(String name) {
        return parseDouble(internalGetString(name));
    }

    @Override
    public float getFloat(String name) {
        return parseFloat(internalGetString(name));
    }

    @Override
    public int getInt(String name) {
        return parseInt(internalGetString(name));
    }

    @Override
    public long getLong(String name) {
        return parseLong(internalGetString(name));
    }

    @Override
    public String getString(String name) {
        String value = internalGetString(name);
        return value == null ? STRING_DEFAULT_DEFAULT : value;
    }

    @Override
    public boolean isDefault(String name) {
        try {
            return (!resource.getPersistentProperties().containsKey(getQualifiedName(name)) && defaultProperties.containsKey(name));
        } catch (CoreException e) {
            return false;
        }
    }

    @Override
    public boolean needsSaving() {
        return false;
    }

    @Override
    public void putValue(String name, String value) {
        try {
            resource.setPersistentProperty(getQualifiedName(name), value);
        } catch (CoreException e) {
            // do nothing
        }
    }

    @Override
    public void removePropertyChangeListener(IPropertyChangeListener listener) {
        // do nothing
    }

    @Override
    public void setDefault(String name, double value) {
        setDefault(name, Double.toString(value));
    }

    @Override
    public void setDefault(String name, float value) {
        setDefault(name, Float.toString(value));
    }

    @Override
    public void setDefault(String name, int value) {
        setDefault(name, Integer.toString(value));
    }

    @Override
    public void setDefault(String name, long value) {
        setDefault(name, Long.toString(value));
    }

    @Override
    public void setDefault(String name, String value) {
        defaultProperties.put(name, value);
    }

    @Override
    public void setDefault(String name, boolean value) {
        setDefault(name, Boolean.toString(value));
    }

    @Override
    public void setToDefault(String name) {
        try {
            resource.getPersistentProperties().remove(getQualifiedName(name));
        } catch (CoreException e) {
            // do nothing
        }
    }

    @Override
    public void setValue(String name, double value) {
        setValue(name, Double.toString(value));
    }

    @Override
    public void setValue(String name, float value) {
        setValue(name, Float.toString(value));
    }

    @Override
    public void setValue(String name, int value) {
        setValue(name, Integer.toString(value));
    }

    @Override
    public void setValue(String name, long value) {
        setValue(name, Long.toString(value));
    }

    @Override
    public void setValue(String name, String value) {
        try {
            resource.setPersistentProperty(getQualifiedName(name), value);
        } catch (CoreException e) {
            // do nothing
        }
    }

    @Override
    public void setValue(String name, boolean value) {
        setValue(name, Boolean.toString(value));
    }

    @Override
    public void firePropertyChangeEvent(String name, Object oldValue, Object newValue) {
        // do nothing
    }

    public void setResource(IResource resource) {
        this.resource = resource;
    }
}
