/*
package ru.sweetbun.benchmark;

import org.openjdk.jmh.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.sweetbun.producer.KafkaProducer;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class KafkaBenchmark {

    @Autowired
    private KafkaProducer producer;

    @Benchmark
    public void sendKafkaMessage() {
        producer.sendMessage("Test message");
    }
}*/
