package ru.sweetbun.benchmark.rabbit;

import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import ru.sweetbun.consumer.RabbitConsumer;
import ru.sweetbun.producer.RabbitProducer;

@Component
public class RabbitRunner {

    private final RabbitProducer producer;
    private final RabbitConsumer consumer;
    private final TaskExecutor taskExecutor;

    public RabbitRunner(RabbitProducer producer, RabbitConsumer consumer, TaskExecutor taskExecutor) {
        this.producer = producer;
        this.consumer = consumer;
        this.taskExecutor = taskExecutor;
    }

    public void runTest(int producerCount, int consumerCount) {
        for (int i = 0; i < producerCount; i++) {
            taskExecutor.execute(() -> {
                producer.sendMessage("Hello from producer " + Thread.currentThread().getName());
            });
        }

        for (int i = 0; i < consumerCount; i++) {
            taskExecutor.execute(() -> {
                consumer.receiveMessage("Test message from consumer " + Thread.currentThread().getName());
            });
        }
    }
}
