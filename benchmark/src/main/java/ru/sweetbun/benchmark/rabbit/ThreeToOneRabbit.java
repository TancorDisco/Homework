/*
package ru.sweetbun.benchmark.rabbit;

import com.rabbitmq.client.*;
import org.openjdk.jmh.annotations.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 2, time = 1)
@Fork(1)
public class ThreeToOneRabbit {

    private static final String QUEUE_NAME = "testQueue";
    private static final String HOST = "localhost";

    private ConnectionFactory factory;
    private Connection consumerConnection;
    private Channel consumerChannel;

    private long totalTimeDelivery = 0;
    private long totalTimeProcessing = 0;
    private int messageCount = 0;

    @Param({"standard", "manualAck", "largeMessage"})
    private String mode;

    private String message;
    private ExecutorService producerPool;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        factory = new ConnectionFactory();
        factory.setHost(HOST);

        // Настройка консюмера
        consumerConnection = factory.newConnection();
        consumerChannel = consumerConnection.createChannel();
        consumerChannel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // Сообщение для тестов
        switch (mode) {
            case "standard":
            case "manualAck":
                message = "Hello, RabbitMQ!";
                break;
            case "largeMessage":
                message = "A".repeat(1024 * 50); // 50 KB сообщение
                break;
        }

        // Создаем пул потоков для 3 продюсеров
        producerPool = Executors.newFixedThreadPool(3);
    }

    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        producerPool.shutdown();
        producerPool.awaitTermination(1, TimeUnit.MINUTES);

        consumerChannel.close();
        consumerConnection.close();

        if (messageCount > 0) {
            long avgTimeDelivery = totalTimeDelivery / messageCount;
            long avgTimeProcessing = totalTimeProcessing / messageCount;

            try (FileWriter writer = new FileWriter("Rabbit.txt", true)) {
                writer.write("Mode: " + mode + "\n");
                writer.write("[3 producers, 1 consumer] Avg Time Delivery (ns): " + avgTimeDelivery + "\n");
                writer.write("[3 producers, 1 consumer] Avg Time Processing (ns): " + avgTimeProcessing + "\n");
                writer.write("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Benchmark // Пропускная способность (Load Balancing)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void loadBalancingTest() throws Exception {
        // Запуск 3 продюсеров
        for (int i = 0; i < 3; i++) {
            producerPool.submit(() -> {
                try {
                    sendMessages(100); // Ограничиваем количество сообщений
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // Консюминг в основном потоке
        consumeMessages(300); // Ожидаем обработку 300 сообщений (3 продюсера * 100 сообщений)
    }

    private void sendMessages(int messageCount) throws Exception {
        try (Connection producerConnection = factory.newConnection();
             Channel producerChannel = producerConnection.createChannel()) {

            for (int i = 0; i < messageCount; i++) {
                long startDelivery = System.nanoTime();
                producerChannel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
                totalTimeDelivery += (System.nanoTime() - startDelivery);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void consumeMessages(int expectedMessageCount) throws Exception {
        try (Connection consumerConnection = factory.newConnection();
             Channel consumerChannel = consumerConnection.createChannel()) {

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                try {
                    long startProcessing = System.nanoTime();
                    String receivedMessage = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    long timeProcessing = System.nanoTime() - startProcessing;

                    totalTimeProcessing += timeProcessing;
                    messageCount++;

                    if ("manualAck".equals(mode)) {
                        consumerChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    }

                    // Завершение консюмера при достижении ожидаемого количества сообщений
                    if (messageCount >= expectedMessageCount) {
                        try {
                            consumerChannel.basicCancel(consumerTag);
                        } catch (IOException e) {
                            if (e.getMessage().contains("Unknown consumerTag")) {
                                System.out.println("ConsumerTag already canceled or does not exist: " + consumerTag);
                            } else {
                                throw e;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if ("manualAck".equals(mode)) {
                        consumerChannel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
                    }
                }
            };

            boolean autoAck = !"manualAck".equals(mode);
            String consumerTag = consumerChannel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
*/
