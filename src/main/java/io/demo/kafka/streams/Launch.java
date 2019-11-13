package io.demo.kafka.streams;

import io.demo.kafka.streams.model.AggregationResult;
import io.demo.kafka.streams.model.Device;
import io.demo.kafka.streams.model.UsageRecord;
import io.demo.kafka.streams.serialization.JsonPOJODeserializer;
import io.demo.kafka.streams.serialization.JsonPOJOSerializer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.DefaultProductionExceptionHandler;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.processor.StateRestoreListener;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class Launch {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "bzhezlo-streams-homework-4");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, LogAndContinueExceptionHandler.class);
        props.put(StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG, DefaultProductionExceptionHandler.class);
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 1);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 5000);
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);

        final Serde<Device> deviceSerde =
                Serdes.serdeFrom(new JsonPOJOSerializer<>(), new JsonPOJODeserializer<>(Device.class));

        final Serde<UsageRecord> usageRecordSerde =
                Serdes.serdeFrom(new JsonPOJOSerializer<>(), new JsonPOJODeserializer<>(UsageRecord.class));

        final Serde<AggregationResult> aggResultSerde =
                Serdes.serdeFrom(new JsonPOJOSerializer<>(), new JsonPOJODeserializer<>(AggregationResult.class));

        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Device> iotDevicesStream = builder
                .stream("iot_devices", Consumed.with(Serdes.String(), deviceSerde))
                .selectKey((k, v) -> v.getDeviceSerialNumber());

        KStream<String, UsageRecord> usageStreamStream = builder
                .stream("iot_usage_records", Consumed.with(Serdes.String(), usageRecordSerde))
                .selectKey((k, v) -> v.getDeviceSerialNumber());

        KStream<String, UsageRecord> usageStreamAggregated = usageStreamStream
                .groupByKey(Grouped.with(Serdes.String(), usageRecordSerde))
                .aggregate(
                        UsageRecord::new,
                        (key, value, aggr) -> {
                            aggr.setDeviceSerialNumber(key);
                            aggr.setDownloadBytes(aggr.getDownloadBytes() + value.getDownloadBytes());
                            aggr.setUploadBytes(aggr.getUploadBytes() + value.getUploadBytes());
                            return aggr;
                        },
                        Materialized.with(Serdes.String(), usageRecordSerde)
                ).toStream();

        KTable<Windowed<String>, AggregationResult> result =
                iotDevicesStream
                        .join(usageStreamAggregated, (device, usage) -> {
                                    AggregationResult res = new AggregationResult();
                                    res.setAccountNumber(device.getAccountNumber());
                                    res.setDownloadBytes(usage.getDownloadBytes());
                                    res.setUploadBytes(usage.getUploadBytes());
                                    return res;
                                },
                                JoinWindows.of(Duration.ofMinutes(1)),
                                Joined.with(Serdes.String(), deviceSerde, usageRecordSerde)
                        )
                        .groupBy((k, v) -> v.getAccountNumber(), Grouped.with(Serdes.String(), aggResultSerde))
                        .windowedBy(TimeWindows.of(Duration.ofMinutes(1)))
                        .aggregate(AggregationResult::new,
                                (key, value, aggr) -> {
                                    aggr.setAccountNumber(value.getAccountNumber());
                                    aggr.setDownloadBytes(aggr.getDownloadBytes() + value.getDownloadBytes());
                                    aggr.setUploadBytes(aggr.getUploadBytes() + value.getUploadBytes());
                                    return aggr;
                                },
                                Materialized.with(Serdes.String(), aggResultSerde)
                        )
                        .mapValues((k, v) -> {
                                    v.setPeriodStart(k.window().start());
                                    v.setPeriodEnd(k.window().end());
                                    return v;
                                }
                        );

        KStream<Windowed<String>, AggregationResult> resultStream = result.toStream();

        resultStream.foreach((k, v) -> {
            System.out.println("KEY: " + k + " VALUE: " + v);
        });

        //resultStream.to("iot_aggregated_usage"); //TODO: it fails due to serialization error

        final Topology topology = builder.build();

        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);

        streams.setStateListener((newState, oldState) -> System.out.println("Changing state from " + oldState + " to " + newState));
        streams.setGlobalStateRestoreListener(new StateRestoreListener() {
            @Override
            public void onRestoreStart(TopicPartition topicPartition, String storeName, long startingOffset, long endingOffset) {
            }

            @Override
            public void onBatchRestored(TopicPartition topicPartition, String storeName, long batchEndOffset, long numRestored) {
            }

            @Override
            public void onRestoreEnd(TopicPartition topicPartition, String storeName, long totalRestored) {
            }
        });

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                System.out.println("Shutting down streams...");
                streams.close();
                System.out.println("Streams shut down!");
                latch.countDown();
            }
        });

        try {
            System.out.println("Starting streams...");
            streams.start();
            System.out.println("Streams started!");
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.out.println("Exiting...");
        System.exit(0);
    }
}
