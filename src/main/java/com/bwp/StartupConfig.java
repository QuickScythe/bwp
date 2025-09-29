package com.bwp;

import com.bwp.utils.Utils;
import com.bwp.utils.queue.QueueManager;
import com.quiptmc.core.utils.TaskScheduler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.util.concurrent.TimeUnit;

@Configuration
public class StartupConfig {

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
