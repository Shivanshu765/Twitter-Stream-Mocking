package com.streamkafka.project.runner.impl;

import com.streamkafka.project.config.StreamKafkaServiceConfigData;
import com.streamkafka.project.listener.StreamKafkaStatusListener;
import com.streamkafka.project.runner.StreamRunner;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.FilterQuery;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import java.util.Arrays;

@Component
public class TwitterKafkaStreamRunner implements StreamRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStreamRunner.class);

    private final StreamKafkaServiceConfigData streamKafkaServiceConfigData;

    private final StreamKafkaStatusListener streamKafkaStatusListener;

    private TwitterStream twitterStream;

    public TwitterKafkaStreamRunner(StreamKafkaServiceConfigData configData,
                                    StreamKafkaStatusListener statusListener)
    {
        this.streamKafkaStatusListener = statusListener;
        this.streamKafkaServiceConfigData = configData;
    }

    private void addFilter(){
        String[] keywords = streamKafkaServiceConfigData.getImpKeywords().toArray(new String[0]);
        FilterQuery filterQuery = new FilterQuery(keywords);
        twitterStream.filter(filterQuery);
        LOG.info("Started filtering twitter stream for keywords{}", Arrays.toString(keywords));
    }


    //Spring beans are created with singleton scope by default
    // means same instance is shared each time the object is injected
    // If we define spring bean with prototype scope, then each injection of bean will create new instance(not singleton)

    //If bean is under prototype scope, spring will not call methods with pre destroy annotation

    // Used for clean up before application shutdown
    // method will be called before application shutdown
    // this will make sure stream connection will be closed prior to application close
    @PreDestroy
    public void shutdown(){
        if (twitterStream != null){
            LOG.info("Closing twitter Stream!");
            twitterStream.shutdown();
        }
    }

    @Override
    public void start() throws TwitterException {
        twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(streamKafkaStatusListener);
        addFilter();
    }
}
