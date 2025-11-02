package com.streamkafka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import lombok.*;

// To get rid of some boilerplate code, Lombok creates code like getter, setter methods
// and update the class during compilation and resulting bytecode that will be run by JVM will
// actually include these methods
@Data
// @Configuration is used to make it a bean
@Configuration
@ConfigurationProperties(prefix = "stream-kafka-service")
public class StreamKafkaServiceConfigData {
    private List<String> impKeywords;
    private Boolean enableMockTweets;
    private Long mockSleepMs;
    private Integer mockMinTweetLength;
    private Integer mockMaxTweetLength;
}
