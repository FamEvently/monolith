package com.famevently.monolith.userfollow;

public enum FollowChange {
    INCREMENT(1),
    DECREMENT(-1),
    NONE(0);

    private final int delta;

    FollowChange(final int delta) {
        this.delta = delta;
    }

    public int getDelta() {
        return delta;
    }
}

