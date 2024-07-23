package com.kpabr.backrooms.util;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Server animation - SA
 */
public class SACallbackManager {
    static final Timer callbacks = new Timer();

    public static void addNewCallback(ServerAnimationCallback callback, long lengthInMilliseconds) {
        callbacks.schedule(new TimerTask() {
            @Override
            public void run() {
                callback.sync();
            }
        }, lengthInMilliseconds);
    }
}
