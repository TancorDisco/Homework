/*
package ru.sweetbun.benchmark;

import org.openjdk.jmh.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.sweetbun.producer.RabbitMQProducer;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class RabbitMQBenchmark {

    @Autowired
    private RabbitMQProducer producer;

    @Benchmark
    public void sendRabbitMQMessage() {
        producer.sendMessage("Test message");
    }
}
*/
