package com.cevelop.templator.plugin.view.components;

public class DirectConnection extends Connection {

    private BezierCurve bezierCurve;
    private boolean     active;

    public DirectConnection(ConnectionStart start, ConnectionEnd end) {
        super(start, end);
    }

    public void setBezierCurve(BezierCurve bezierCurve) {
        this.bezierCurve = bezierCurve;
    }

    public BezierCurve getBezierCurve() {
        return bezierCurve;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean isPortal() {
        return false;
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}
