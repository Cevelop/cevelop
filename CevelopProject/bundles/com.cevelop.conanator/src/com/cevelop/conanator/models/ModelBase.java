package com.cevelop.conanator.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.cevelop.conanator.Activator;


public abstract class ModelBase implements Cloneable {

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    @Override
    public ModelBase clone() {
        ModelBase clone = null;

        try {
            clone = (ModelBase) super.clone();
            clone.propertyChangeSupport = new PropertyChangeSupport(clone);
        } catch (CloneNotSupportedException e) {
            Activator.log(e);
        }

        assert (clone != null);

        return clone;
    }
}
