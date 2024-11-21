package ru.sweetbun;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import ru.sweetbun.benchmark.rabbit.BenchmarkRabbit;
import ru.sweetbun.benchmark.kafka.BenchmarkKafka;

public class BenchmarkRunner {

    public static void main(String[] args) throws Exception {

        Options options = new OptionsBuilder()
                .include(BenchmarkRabbit.class.getSimpleName())
                .include(BenchmarkKafka.class.getSimpleName())
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
