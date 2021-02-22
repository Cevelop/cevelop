package com.cevelop.templator.plugin.view.components;

public class PortalConnection extends Connection implements Comparable<PortalConnection> {

    private Portal portal;

    public PortalConnection(ConnectionStart start, ConnectionEnd end) {
        super(start, end);
    }

    public void setPortal(Portal portal) {
        this.portal = portal;
    }

    public Portal getPortal() {
        return portal;
    }

    public int getPortalNumber() {
        return end.getEntry().getPortalNumber();
    }

    private int getIndex() {
        return start.getNameIndex();
    }

    public int getYPos() {
        return portal.pos.y;
    }

    public int getXPos() {
        return portal.pos.x;
    }

    @Override
    public boolean isPortal() {
        return true;
    }

    @Override
    public boolean isReversed() {
        return false;
    }

    @Override
    // this < o negative
    // this o e2 0
    // this > o positive
    public int compareTo(PortalConnection other) {
        if (getXPos() < other.getXPos()) {
            return -1;
        } else if (getXPos() > other.getXPos()) {
            return 1;
        } else if (getYPos() == other.getYPos()) {
            return getIndex() - other.getIndex();
        } else {
            return getYPos() - other.getYPos();
        }
    }
}
