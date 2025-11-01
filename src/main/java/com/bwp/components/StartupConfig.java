package com.bwp.components;

import com.bwp.Main;
import com.bwp.utils.Utils;
import com.bwp.utils.queue.QueueManager;
import com.quiptmc.core.utils.TaskScheduler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.util.concurrent.TimeUnit;


/**
 * Application startup configuration that initializes background services after
 * the Spring context is ready.
 * <p>
 * Responsibilities:
 * - Initialize the QueueManager tied to the Quipt integration
 * - Schedule Utils.init after a short delay to register configs and bootstrap data
 */
@Configuration
public class StartupConfig {

    /**
     * Invoked when the Spring application is fully started. Safely initializes
     * background components and schedules deferred initialization work.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            QueueManager.initialize(Main.INTEGRATION);
        } catch (Throwable t) {
            Main.LOGGER.error("Failed to initialize QueueManager; continuing startup.", t);
        }

        try {
            TaskScheduler.scheduleAsyncTask(() -> {
                try {
                    Utils.init(Main.INTEGRATION);
                } catch (Throwable t) {
                    Main.LOGGER.error("Failed to run Utils.init; app will continue but some features may be unavailable.", t);
                }
            }, 2, TimeUnit.SECONDS);
        } catch (Throwable t) {
            Main.LOGGER.error("Failed to schedule Utils.init task; continuing startup.", t);
        }
    }
}
