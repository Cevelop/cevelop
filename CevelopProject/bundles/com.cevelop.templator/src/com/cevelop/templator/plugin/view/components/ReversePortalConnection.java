package com.cevelop.templator.plugin.view.components;

public class ReversePortalConnection extends Connection {

    public ReversePortalConnection(ConnectionStart start, ConnectionEnd end) {
        super(start, end);
    }

    @Override
    public boolean isPortal() {
        return true;
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}
