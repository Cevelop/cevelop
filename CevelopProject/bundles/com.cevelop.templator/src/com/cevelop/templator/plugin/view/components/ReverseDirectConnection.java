package com.cevelop.templator.plugin.view.components;

public class ReverseDirectConnection extends Connection {

    public ReverseDirectConnection(ConnectionStart start, ConnectionEnd end) {
        super(start, end);
    }

    @Override
    public boolean isPortal() {
        return false;
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}
