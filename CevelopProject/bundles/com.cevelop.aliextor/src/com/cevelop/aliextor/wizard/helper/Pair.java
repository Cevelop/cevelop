package com.cevelop.aliextor.wizard.helper;

import ch.hsr.ifs.iltis.core.core.data.AbstractPair;


public class Pair<L, R> extends AbstractPair<L, R> {

    private L first;
    private R second;

    private Pair(L first, R second) {
        super(first, second);
        this.first = first;
        this.second = second;
    }

    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }

    public L getLeft() {
        return first;
    }

    public R getRight() {
        return second;
    }

}
