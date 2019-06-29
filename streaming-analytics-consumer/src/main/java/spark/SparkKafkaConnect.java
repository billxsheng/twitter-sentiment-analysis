package spark;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import models.Tweet;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import twitter4j.Status;

public class SparkKafkaConnect {
    public static void connectToTopic() throws InterruptedException {
        SparkConf sparkConf = new SparkConf().setAppName("spark-streaming").setMaster("local[2]").set("spark.executor.memory","1g");
        sparkConf.setAppName("WordCountingApp");
        sparkConf.set("spark.cassandra.connection.host", "127.0.0.1");
        JavaStreamingContext streamingContext = new JavaStreamingContext(sparkConf, Durations.seconds(1));

        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", "localhost:9092");
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "use_a_separate_group_id_for_each_stream");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);
        Collection<String> topics = Arrays.asList("bill-test");

        JavaInputDStream<ConsumerRecord<String, String>> tweetStream =
                KafkaUtils.createDirectStream(
                        streamingContext,
                        LocationStrategies.PreferConsistent(),
                        ConsumerStrategies.<String, String> Subscribe(topics, kafkaParams));

//        JavaDStream<String> tweets = tweetStream.map(tweet -> tweet.value());

        JavaDStream<Tweet> tweets = tweetStream.map(status -> buildNewTweet(status));
//        tweets.foreachRDD(rdd -> {
//            for(String r: rdd.collect()) {
//                System.out.println("*" + r);
//            }
//        });

        streamingContext.start();
        streamingContext.awaitTermination();
    }

    public static Tweet buildNewTweet(Status status) {

    }
}
