/*
package ru.sweetbun.benchmark.rabbit;

import com.rabbitmq.client.*;
import org.openjdk.jmh.annotations.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 2, time = 1)
@Fork(1)
public class OneToOneRabbit {

    private static final String QUEUE_NAME = "testQueue";
    private static final String HOST = "localhost";

    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;

    private long totalTimeDelivery = 0;
    private long totalTimeProcessing = 0;
    private int messageCount = 0;

    @Param({"standard", "manualAck", "largeMessage"})
    private String mode;

    private String message;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        factory = new ConnectionFactory();
        factory.setHost(HOST);
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        switch (mode) {
            case "standard":
            case "manualAck":
                message = "Hello, RabbitMQ!";
                break;
            case "largeMessage":
                message = "A".repeat(1024 * 512); // 1/2 MB сообщение
                break;
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        channel.close();
        connection.close();

        if (messageCount > 0) {
            long avgTimeDelivery = totalTimeDelivery / messageCount;
            long avgTimeProcessing = totalTimeProcessing / messageCount;

            try (FileWriter writer = new FileWriter("Rabbit.txt", true)) {
                writer.write("Mode: " + mode + "\n");
                writer.write("[1 to 1 Rabbit] Avg Time Delivery (ns): " + avgTimeDelivery + "\n");
                writer.write("[1 to 1 Rabbit] Avg Time Processing (ns): " + avgTimeProcessing + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Benchmark // Пропускная способность с учетом режима
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void sendAndConsumeMessages() throws Exception {
        long startDelivery = System.nanoTime();
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            long timeDelivery = System.nanoTime() - startDelivery;
            totalTimeDelivery += timeDelivery;

            long startProcessing = System.nanoTime();
            try {
                String receivedMessage = new String(delivery.getBody(), StandardCharsets.UTF_8);
                long timeProcessing = System.nanoTime() - startProcessing;
                totalTimeProcessing += timeProcessing;
                messageCount++;

                if ("manualAck".equals(mode)) {
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            } catch (Exception e) {
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
            }
        };
        boolean autoAck = !"manualAck".equals(mode);
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> {});
    }

    @Benchmark // Latency продюсера
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void sendMessages() throws IOException {
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
    }

    @Benchmark // Latency консюмера
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void consumeMessages() throws Exception {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String receivedMessage = new String(delivery.getBody(), StandardCharsets.UTF_8);
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    }
}*/
