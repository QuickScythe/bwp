package com.bwp.utils.queue;

import com.quiptmc.core.QuiptIntegration;
import com.quiptmc.core.heartbeat.HeartbeatUtils;
import com.quiptmc.core.heartbeat.runnable.Heartbeat;

/**
 * Manager for the application-wide Queue executed on the Quipt Heartbeat.
 * <p>
 * Provides a singleton Queue instance and wires it into the integration-specific
 * Heartbeat so queued tasks run periodically.
 */
public class QueueManager {

    public static final Queue INSTANCE = new Queue();
    private static Heartbeat.FlutterTask task = null;

    /**
     * Initializes the Queue on the given Quipt integration's Heartbeat, creating
     * one if necessary, and schedules the Queue to be fluttered each tick.
     *
     * @param integration the integration providing the Heartbeat context
     */
    public static void initialize(QuiptIntegration integration) {
        Heartbeat heartbeat = HeartbeatUtils.heartbeat(integration) == null ? HeartbeatUtils.init(integration) : HeartbeatUtils.heartbeat(integration);
        task = heartbeat.flutter(INSTANCE);
    }

    /**
     * Returns the scheduled Heartbeat task controlling the Queue, if initialized.
     *
     * @return the Heartbeat.FlutterTask or null if initialize(...) has not been called
     */
    public Heartbeat.FlutterTask task(){
        return task;
    }
}
