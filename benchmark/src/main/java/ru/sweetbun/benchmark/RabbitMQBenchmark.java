/*
package ru.sweetbun.benchmark;

import com.rabbitmq.client.*;
import org.openjdk.jmh.annotations.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class RabbitMQBenchmark {

    private static final String QUEUE_NAME = "benchmark-queue";
    private Connection connection;
    private Channel channel;

    private List<Long> producerLatencies = new ArrayList<>();
    private List<Long> consumerLatencies = new ArrayList<>();
    private List<Long> deliveryLatencies = new ArrayList<>();
    private List<Long> processingLatencies = new ArrayList<>();

    @Param({"1", "3", "10"})
    private int producers;

    @Param({"1", "3", "10"})
    private int consumers;

    private BufferedWriter writer;

    @Setup(Level.Trial)
    public void setup() throws IOException, TimeoutException {
        // Открытие файла для перезаписи (старая информация не сохраняется)
        writer = new BufferedWriter(new FileWriter("benchmark-results.txt", true)); // true - добавление строк

        // Запись заголовков, если файл пустой
        writer.write("Producers\tConsumers\tProd. Latency (ns)\tCons. Latency (ns)\tDelivery Latency (ns)\tProcessing Latency (ns)\n");

        // Подключение к RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    }

    @Benchmark
    public void testRabbitMQ() throws IOException {
        // Тестирование продюсеров
        for (int i = 0; i < producers; i++) {
            long startTime = System.nanoTime();
            String message = "Hello, RabbitMQ!";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            long endTime = System.nanoTime();
            long latency = endTime - startTime;
            producerLatencies.add(latency); // Сохраняем время отклика продюсера
        }

        // Тестирование консюмеров
        for (int i = 0; i < consumers; i++) {
            long consumerStartTime = System.nanoTime(); // Начало времени для вычисления consumer latency
            long deliveryStartTime = System.nanoTime();

            // Получение сообщения с ожиданием
            GetResponse response = waitForMessage();  // Новый метод для ожидания сообщения
            long deliveryEndTime = System.nanoTime();

            if (response != null) {
                long processingStartTime = System.nanoTime();
                simulateMessageProcessing(); // Симуляция обработки
                long processingEndTime = System.nanoTime();

                deliveryLatencies.add(deliveryEndTime - deliveryStartTime); // Задержка доставки
                processingLatencies.add(processingEndTime - processingStartTime); // Время обработки
                consumerLatencies.add(System.nanoTime() - consumerStartTime); // Время отклика консюмера
            }
        }
    }

    private GetResponse waitForMessage() throws IOException {
        GetResponse response = null;
        int retries = 10;  // Максимум 10 попыток получить сообщение
        while (response == null && retries > 0) {
            response = channel.basicGet(QUEUE_NAME, true); // Попытка получить сообщение
            if (response == null) {
                try {
                    Thread.sleep(50); // Ожидание перед новой попыткой
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            retries--;
        }
        return response;  // Либо сообщение, либо null после 10 попыток
    }

    private void simulateMessageProcessing() {
        try {
            Thread.sleep(50); // Имитация задержки обработки
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @TearDown(Level.Trial)
    public void teardown() throws IOException, TimeoutException {
        // Вычисление средних значений
        long avgProducerLatency = (long) producerLatencies.stream().mapToLong(Long::longValue).average().orElse(0);
        long avgConsumerLatency = (long) consumerLatencies.stream().mapToLong(Long::longValue).average().orElse(0);
        long avgDeliveryLatency = (long) deliveryLatencies.stream().mapToLong(Long::longValue).average().orElse(0);
        long avgProcessingLatency = (long) processingLatencies.stream().mapToLong(Long::longValue).average().orElse(0);

        // Запись результатов в файл
        writer.write(String.format("%d\t%d\t%d\t%d\t%d\t%d\n",
                producers, consumers, avgProducerLatency, avgConsumerLatency, avgDeliveryLatency, avgProcessingLatency));

        // Очистка данных
        producerLatencies.clear();
        consumerLatencies.clear();
        deliveryLatencies.clear();
        processingLatencies.clear();

        // Закрытие соединений
        channel.close();
        connection.close();

        // Закрытие файла
        writer.close();
    }
}
*/
