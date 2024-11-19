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
    private static final String HOST = "localhost";  // Адрес вашего RabbitMQ сервера

    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;

    private long totalTimeDelivery = 0;
    private long totalTimeProcessing = 0;
    private int messageCount = 0;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        factory = new ConnectionFactory();
        factory.setHost(HOST);
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    }

    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        channel.close();
        connection.close();

        if (messageCount > 0) {
            long avgTimeDelivery = totalTimeDelivery / messageCount;
            long avgTimeProcessing = totalTimeProcessing / messageCount;

            try (FileWriter writer = new FileWriter("benchmark_results.txt")) {
                writer.write("Средняя задержка доставки (ns): " + avgTimeDelivery + "\n");
                writer.write("Среднее время обработки (ns): " + avgTimeProcessing + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Benchmark //Пропускную способность
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void sendAndConsumeMessages() throws Exception {
        long startDelivery = System.nanoTime();
        String message = "Hello, RabbitMQ!";
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

                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (Exception e) {
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
            }
        };
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {});
    }

    @Benchmark //Latency producer
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void sendMessages() throws IOException {
        String message = "Hello, RabbitMQ!";
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
    }

    @Benchmark //Latency consumer
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void consumeMessages() throws Exception {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    }
}
