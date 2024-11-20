package ru.sweetbun;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import ru.sweetbun.benchmark.kafka.OneToOneKafka;
import ru.sweetbun.benchmark.rabbit.*;

public class BenchmarkRunner {

    public static void main(String[] args) throws Exception {

        Options options = new OptionsBuilder()
                /*.include(OneToOneRabbit.class.getSimpleName())
                .include(ThreeToOneRabbit.class.getSimpleName())
                .include(OneToThreeRabbit.class.getSimpleName())
                .include(ThreeToThreeRabbit.class.getSimpleName())
                .include(TenToTenRabbit.class.getSimpleName())*/
                .include(OneToOneKafka.class.getSimpleName())
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
