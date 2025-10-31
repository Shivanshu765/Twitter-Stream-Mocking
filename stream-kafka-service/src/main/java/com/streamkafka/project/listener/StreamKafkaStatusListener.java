package com.streamkafka.project.listener;

/* https://twitter4j.org/code-examples */
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.StatusAdapter;


// All the classes that are annotated with component annotation/ controller/ service/ repository/ configuration
// will be scanned and loaded as spring bean at runtime


// To make this spring managed bean
@Component
public class StreamKafkaStatusListener extends StatusAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(StreamKafkaStatusListener.class);

    @Override
    public void onStatus(Status status){
        LOG.info("Twitter status with text {}", status.getText());
    }
}
