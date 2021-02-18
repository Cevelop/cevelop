package com.cevelop.conanator.models;

public final class Remote extends ModelBase implements Cloneable {

    private String name;
    private String url;
    private String ssl;

    public Remote() {
        name = "";
        url = "";
        ssl = "True";
    }

    public Remote(String name, String url, String ssl) {
        this.name = name;
        this.url = url;
        setSsl(ssl);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        firePropertyChange("name", this.name, this.name = name);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        firePropertyChange("url", this.url, this.url = url);
    }

    public String getSsl() {
        return ssl;
    }

    public void setSsl(String ssl) {
        String oldValue = this.ssl;

        if (ssl.equalsIgnoreCase("false")) {
            this.ssl = "False";
        } else {
            this.ssl = "True";
        }

        firePropertyChange("ssl", oldValue, this.ssl);
    }

    @Override
    public String toString() {
        return name + " " + url + " " + ssl;
    }

    @Override
    public Remote clone() {
        return (Remote) super.clone();
    }
}
