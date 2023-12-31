package com.hhoussem.springboot;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@Service
public class WikimediaChangesProducer {

    public static final String URL_STREAM_RECENTCHANGE = "https://stream.wikimedia.org/v2/stream/recentchange";

    private final KafkaTemplate<String, String> kafkaTemplate;
    @Value("${spring.kafka.topic.name}")
    private String topicName;

    public WikimediaChangesProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage() throws InterruptedException {
        // to read real time stream data from wikimedia, we use event  source
        EventHandler eventHandler = new WikimediaChangesHandler(kafkaTemplate, topicName);
        EventSource eventSource = new EventSource.Builder(eventHandler, URI.create(URL_STREAM_RECENTCHANGE)).build();
        eventSource.start();
        TimeUnit.SECONDS.sleep(30);
    }


}
