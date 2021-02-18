package com.cevelop.templator.plugin.view.components;

public abstract class Connection {

    protected ConnectionStart start;
    protected ConnectionEnd   end;

    public Connection(ConnectionStart start, ConnectionEnd end) {
        this.start = start;
        this.end = end;
    }

    public abstract boolean isPortal();

    public abstract boolean isReversed();

    public ConnectionStart getStart() {
        return start;
    }

    public ConnectionEnd getEnd() {
        return end;
    }

    public int getColorId() {
        return end.getEntry().getColorId();
    }

    /** For debug purposes only. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Connection\n");
        sb.append("\t start: " + start.getEntry().getViewData().getTitle() + ", " + start.getNameIndex() + '\n');
        sb.append("\t end " + end.getEntry().getViewData().getTitle() + '\n');
        //sb.append("\t isPortal: " + isPortal());
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        Connection otherCon = (Connection) obj;
        return start == otherCon.start && end == otherCon.end;
    }
}
