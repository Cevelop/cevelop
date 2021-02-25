package com.cevelop.includator.ui.helpers;

import org.eclipse.jface.text.Position;

import com.cevelop.includator.helpers.FileHelper;


public class PositionTrackInfo {

    private Position currentPosition;
    private Position originalPosition;

    public PositionTrackInfo(Position pos) {
        this.currentPosition = pos;
        this.originalPosition = new Position(pos.offset, pos.length);
    }

    public PositionTrackInfo() {}

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Position pos) {
        this.currentPosition = pos;
    }

    @Override
    public String toString() {
        return "current pos: " + makePosString(currentPosition) + FileHelper.NL + "original pos: " + makePosString(originalPosition);
    }

    private String makePosString(Position pos) {
        return (pos != null) ? pos.toString() : "[null position]";
    }

    public Position getOriginalPosition() {
        return originalPosition;
    }

    public void setOriginalPosition(Position originalPosition) {
        this.originalPosition = originalPosition;
    }
}
