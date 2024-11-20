package ru.sweetbun;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sweetbun.benchmark.rabbit.OneToOneRabbit;

public class BenchmarkRunner {

    public static final Logger log = LoggerFactory.getLogger(BenchmarkRunner.class);

    public static void main(String[] args) throws Exception {
        log.info("Начало тестирования");

        Options options = new OptionsBuilder()
                .include(OneToOneRabbit.class.getSimpleName()) // Указываем имя вашего теста
                .forks(1)
                .resultFormat(ResultFormatType.TEXT)
                .build();

        try {
            new Runner(options).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
