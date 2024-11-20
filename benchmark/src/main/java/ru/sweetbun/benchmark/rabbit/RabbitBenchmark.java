/*
package ru.sweetbun.benchmark.rabbit;

import org.openjdk.jmh.annotations.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.sweetbun.config.RabbitConfig;

import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = RabbitConfig.class) // Указываем конфигурацию Spring
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark) // Убедитесь, что бенчмарки выполняются после полной инициализации контекста
public class RabbitBenchmark {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Queue queue;

    private String message = "Hello, RabbitMQ!";

    @Setup(Level.Trial)
    public void setup() {
        // Этот метод будет выполняться перед началом тестирования
        System.out.println("Настройка перед тестами");
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        // Этот метод будет выполняться после завершения тестирования
        System.out.println("Завершение тестов");
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testSendMessages() {
        // Проверьте, что очередь корректно получена
        if (queue == null) {
            throw new IllegalStateException("Queue не инициализирована!");
        }
        rabbitTemplate.convertAndSend(queue.getName(), message);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testConsumeMessages() throws Exception {
        // Проверьте, что очередь корректно получена
        if (queue == null) {
            throw new IllegalStateException("Queue не инициализирована!");
        }
        // Используем слушателя RabbitMQ для получения сообщений
        String receivedMessage = (String) rabbitTemplate.receiveAndConvert(queue.getName());
        assert receivedMessage != null;
    }
}
*/
