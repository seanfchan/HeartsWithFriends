package org.bitcoma.heartserver.netty.task;

import java.util.concurrent.TimeUnit;

import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;

public class TimeOutTaskCreator {

    private static HashedWheelTimer instance;

    private static synchronized HashedWheelTimer getInstance() {
        if (instance == null) {
            instance = new HashedWheelTimer();
        }

        return instance;
    }

    public static Timeout createTask(TimerTask task, long delay, TimeUnit unit) {
        return getInstance().newTimeout(task, delay, unit);
    }

    public static void stop() {
        if (instance != null)
            instance.stop();
    }
}
