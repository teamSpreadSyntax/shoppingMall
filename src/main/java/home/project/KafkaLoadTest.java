package home.project;

import org.apache.kafka.clients.producer.*;
import java.util.Properties;
import java.util.concurrent.*;

public class KafkaLoadTest {
    public static void main(String[] args) throws Exception {
        int numMessages = 100000;  // 총 메시지 수
        int batchSize = 1000;      // 배치당 메시지 수
        int numThreads = 10;       // 스레드 수
        String topic = "default-logs";

        Properties props = new Properties();
        props.put("bootstrap.servers", "kafka:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("acks", "1");
        props.put("batch.size", 16384 * 4);
        props.put("linger.ms", 5);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        long startTime = System.currentTimeMillis();

        for (int t = 0; t < numThreads; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try (Producer<String, String> producer = new KafkaProducer<>(props)) {
                    for (int i = 0; i < numMessages / numThreads; i++) {
                        String message = "{\"message\":\"test log " + threadId + "-" + i + "\",\"timestamp\":" + System.currentTimeMillis() + "}";
                        producer.send(new ProducerRecord<>(topic, message));

                        if (i % batchSize == 0) {
                            producer.flush();
                            System.out.println("Thread " + threadId + " sent " + i + " messages");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Sent " + numMessages + " messages in " + duration + "ms");
        System.out.println("Rate: " + (numMessages * 1000L / duration) + " messages/second");
    }
}