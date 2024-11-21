package ru.sweetbun.benchmark.kafka;

import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.openjdk.jmh.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.sweetbun.config.KafkaConfig;

import java.io.FileWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 2, time = 3)
@Fork(1)
public class ThreeToThreeKafka {

    private static final Logger log = LoggerFactory.getLogger(ThreeToThreeKafka.class);

    private final String TOPIC = "testTopic";
    private final int PRODUCER_COUNT = 3;
    private final int CONSUMER_COUNT = 3;

    private List<KafkaProducer<String, String>> producers = new ArrayList<>();
    private List<KafkaConsumer<String, String>> consumers = new ArrayList<>();

    private long totalTimeDelivery = 0;
    private long totalTimeProcessing = 0;
    private int messageCount = 0;

    @Param({"standard", "replication", "largeMessage"})
    private String mode;

    private String message;

    private AnnotationConfigApplicationContext context;

    @Setup(Level.Trial)
    public void setup() throws InterruptedException {
        context = new AnnotationConfigApplicationContext(KafkaConfig.class);

        Properties producerProperties = context.getBean("producerProperties", Properties.class);
        if ("replication".equals(mode)) {
            producerProperties.put("acks", "all");
        } else {
            producerProperties.put("acks", "1");
        }
        for (int i = 0; i < PRODUCER_COUNT; i++) {
            producers.add(new KafkaProducer<>(producerProperties));
        }

        Properties consumerProperties = context.getBean("consumerProperties", Properties.class);
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProperties);
            consumer.subscribe(Collections.singletonList(TOPIC));
            consumers.add(consumer);
        }

        switch (mode) {
            case "standard":
            case "replication":
                message = "Hello, Kafka!";
                break;
            case "largeMessage":
                message = "A".repeat(1024);
                break;
        }

        Thread.sleep(2000);
    }

    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        for (KafkaProducer<String, String> producer : producers) {
            producer.close();
        }
        for (KafkaConsumer<String, String> consumer : consumers) {
            consumer.close();
        }

        if (context != null) {
            context.close();
        }

        if (messageCount > 0) {
            long avgTimeDelivery = totalTimeDelivery / messageCount;
            long avgTimeProcessing = totalTimeProcessing / messageCount;

            try (FileWriter writer = new FileWriter("Kafka.txt", true)) {
                writer.write("Mode: " + mode + "\n");
                writer.write("[3 to 3 Kafka] Avg Time Delivery (ns): " + avgTimeDelivery + "\n");
                writer.write("[3 to 3 Kafka] Avg Time Processing (ns): " + avgTimeProcessing + "\n");
                writer.write("-------------------------------------------------------------------------------\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Benchmark
    public void sendAndConsumeMessages() {
        long startDelivery = System.nanoTime();

        producers.forEach(producer -> {
            producer.send(new ProducerRecord<>(TOPIC, "key", message), (metadata, exception) -> {
                if (exception != null) {
                    log.error("Error sending message", exception);
                }
            });
        });

        long timeDelivery = System.nanoTime() - startDelivery;
        totalTimeDelivery += timeDelivery;

        long startProcessing = System.nanoTime();
        consumers.forEach(consumer -> {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            records.forEach(record -> {
                log.info("Received message: key={}, value={}, partition={}, offset={}",
                        record.key(), record.value(), record.partition(), record.offset());
            });
            messageCount += records.count();
            try {
                consumer.commitSync();
            } catch (CommitFailedException e) {
                log.error("Failed to commit offsets", e);
            }
        });
        long timeProcessing = System.nanoTime() - startProcessing;
        totalTimeProcessing += timeProcessing;
    }
}
