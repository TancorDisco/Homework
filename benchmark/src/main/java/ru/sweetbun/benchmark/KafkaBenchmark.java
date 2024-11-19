/*
package ru.sweetbun.benchmark;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.openjdk.jmh.annotations.*;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS) // 1 итерация разогрева по 1 секунде
@Measurement(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS) // 2 итерации по 1 секунде
@Fork(1) // 1 запуск
public class KafkaBenchmark {

    private static final String TOPIC = "benchmark-topic";
    private Producer<String, String> producer;
    private Consumer<String, String> consumer;

    @Param({"1", "3", "10"}) // Количество продюсеров
    private int producers;

    @Param({"1", "3", "10"}) // Количество консюмеров
    private int consumers;

    @Setup(Level.Trial)
    public void setup() {
        // Kafka producer config
        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", "localhost:9092");
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(producerProps);

        // Kafka consumer config
        Properties consumerProps = new Properties();
        consumerProps.put("bootstrap.servers", "localhost:9092");
        consumerProps.put("group.id", "benchmark-group");
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(TOPIC));
    }

    @Benchmark
    public void testKafkaProducer() {
        for (int i = 0; i < producers; i++) {
            long start = System.nanoTime();
            producer.send(new ProducerRecord<>(TOPIC, "key", "value"));
            long latency = System.nanoTime() - start;
        }
    }

    @Benchmark
    public void testKafkaConsumer() {
        for (int i = 0; i < consumers; i++) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {

            }
        }
    }

    @TearDown(Level.Trial)
    public void teardown() {
        producer.close();
        consumer.close();
    }
}
*/
