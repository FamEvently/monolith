package com.famevently.monolith.postlikes;

public enum LikeChange {
    INCREMENT(1),
    DECREMENT(-1),
    NONE(0);

    private final int delta;

    LikeChange(final int delta) {
        this.delta = delta;
    }

    public int getDelta() {
        return delta;
    }
}

