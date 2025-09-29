package com.bwp.tests.queue;

import com.bwp.tests.factories.TestFactory;
import com.bwp.utils.queue.QueueManager;
import com.quiptmc.core.QuiptIntegration;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

public class QueueTests {

    @Test
    public void testQueue() {
        QuiptIntegration integration = TestFactory.integration();
        QueueManager.initialize(integration);

        QueueManager.INSTANCE.add(new Supplier<>() {

            int i = 0;

            @Override
            public Boolean get() {
                System.out.println("Hello World");
                i = i + 1;
                return i == 5;
            }
        });
    }
}
