package ru.sweetbun.benchmark.rabbit;

import org.openjdk.jmh.annotations.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.sweetbun.config.RabbitConfig;

import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

@SpringBootTest
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Warmup(iterations = 2, time = 3)
@Measurement(iterations = 3, time = 5)
@Fork(3)
public class BenchmarkRabbit {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Queue queue;

    @Param({"1", "3", "10"})
    private int PRODUCER_COUNT;
    @Param({"1", "3", "10"})
    private int CONSUMER_COUNT;

    private SimpleMessageListenerContainer listenerContainer;

    private long totalTimeDelivery = 0;
    private long totalTimeProcessing = 0;
    private int messageCount = 0;

    @Param({"standard", "manualAck", "largeMessage"})
    private String mode;

    private String message;

    private AnnotationConfigApplicationContext context;
    private ExecutorService producerExecutor;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        context = new AnnotationConfigApplicationContext(RabbitConfig.class);
        rabbitTemplate = context.getBean(RabbitTemplate.class);
        queue = context.getBean(Queue.class);
        listenerContainer = context.getBean(SimpleMessageListenerContainer.class);

        switch (mode) {
            case "standard":
            case "manualAck":
                message = "Hello, RabbitMQ!";
                break;
            case "largeMessage":
                message = "A".repeat(1024);
                break;
        }

        listenerContainer.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
            long startProcessing = System.nanoTime();
            try {
                String receivedMessage = new String(message.getBody(), StandardCharsets.UTF_8);
                long timeProcessing = System.nanoTime() - startProcessing;
                totalTimeProcessing += timeProcessing;
                messageCount++;

                if ("manualAck".equals(mode)) {
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                }
            } catch (Exception e) {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }
        });

        listenerContainer.setAcknowledgeMode("manualAck".equals(mode)
                ? org.springframework.amqp.core.AcknowledgeMode.MANUAL
                : org.springframework.amqp.core.AcknowledgeMode.AUTO);

        listenerContainer.setConcurrentConsumers(CONSUMER_COUNT);
        listenerContainer.start();

        producerExecutor = Executors.newFixedThreadPool(PRODUCER_COUNT);
    }

    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        if (producerExecutor != null) {
            producerExecutor.shutdown();
            if (!producerExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
                producerExecutor.shutdownNow();
            }
        }
        if (context != null) {
            context.close();
        }
        if (rabbitTemplate.getConnectionFactory() instanceof CachingConnectionFactory) {
            ((CachingConnectionFactory) rabbitTemplate.getConnectionFactory()).destroy();
        }
        listenerContainer.stop();

        if (messageCount > 0) {
            long avgTimeDelivery = totalTimeDelivery / messageCount;
            long avgTimeProcessing = totalTimeProcessing / messageCount;

            try (FileWriter writer = new FileWriter("Rabbit.txt", true)) {
                writer.write("Mode: " + mode + "\n");
                writer.write("[" + PRODUCER_COUNT +" to "+ CONSUMER_COUNT +" Rabbit] Avg Time Delivery (ns): " + avgTimeDelivery + "\n");
                writer.write("[" + PRODUCER_COUNT +" to "+ CONSUMER_COUNT +" Rabbit] Avg Time Processing (ns): " + avgTimeProcessing + "\n");
                writer.write("-------------------------------------------------------------------------------\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Benchmark
    public void sendAndConsumeMessages() throws InterruptedException {
        for (int i = 0; i < PRODUCER_COUNT; i++) {
            producerExecutor.submit(() -> {
                long startDelivery = System.nanoTime();
                rabbitTemplate.convertAndSend(queue.getName(), message);
                long timeDelivery = System.nanoTime() - startDelivery;
                totalTimeDelivery += timeDelivery;
            });
        }
    }
}
