package com.kpabr.backrooms.util;

/**
 * Functional interface for syncing animations on server with client,
 * will run when animation is finished
 */
@FunctionalInterface
public interface ServerAnimationCallback {
    void sync();
}
