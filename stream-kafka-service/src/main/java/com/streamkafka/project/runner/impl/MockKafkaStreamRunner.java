package com.streamkafka.project.runner.impl;

import com.streamkafka.config.StreamKafkaServiceConfigData;
import com.streamkafka.project.exceptions.StreamKafkaServiceException;
import com.streamkafka.project.listener.StreamKafkaStatusListener;
import com.streamkafka.project.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;


// Conditionally create a spring bean with a config variable
@Component
@ConditionalOnProperty(name = "stream-kafka-service.enable-mock-tweets", havingValue = "true")
public class MockKafkaStreamRunner implements StreamRunner {

    private static final Logger LOG = LoggerFactory.getLogger(MockKafkaStreamRunner.class);

    private final StreamKafkaServiceConfigData streamKafkaServiceConfigData;
    private final StreamKafkaStatusListener streamKafkaStatusListener;

    public MockKafkaStreamRunner(StreamKafkaServiceConfigData configData,
                                 StreamKafkaStatusListener statusListener) {
        this.streamKafkaServiceConfigData = configData;
        this.streamKafkaStatusListener = statusListener;
    }

    private static final Random RANDOM = new Random();

    private static final String[] WORDS = new String[]{
            "Loremi",
            "psum",
            "dolor",
            "sit",
            "amet",
            "consectetur",
            "adipiscing",
            "elit",
            "sed",
            "do",
            "eiusmod",
            "tempor",
            "incididunt",
            "ut",
            "labore",
            "et",
            "dolore",
            "magna",
            "aliqua"
    };

    private static final String tweetAsRawJson = "{" +
            "\"created_at\":\"{0}\"," +
            "\"id\":\"{1}\"," +
            "\"text\":\"{2}\"," +
            "\"user\":{\"id\":\"{3}\"}" +
            "}";

    private static final String TWITTER_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";


    @Override
    public void start() throws TwitterException {
        String[] keywords = streamKafkaServiceConfigData.getImpKeywords().toArray(new String[0]);
        int minTweetLength = streamKafkaServiceConfigData.getMockMinTweetLength();
        int maxTweetLength = streamKafkaServiceConfigData.getMockMaxTweetLength();
        long sleepTimeMs = streamKafkaServiceConfigData.getMockSleepMs();
        LOG.info("Starting mock filtering twitter stream for keywords {}", Arrays.toString(keywords));
        simulateTwitterStream(keywords, minTweetLength, maxTweetLength, sleepTimeMs);
    }

    private void simulateTwitterStream(String[] keywords, int minTweetLength, int maxTweetLength, long sleepTimeMs)
    {
        Executors.newSingleThreadExecutor().submit(() ->{
            try {
                while (true) {
                    String formattedTweetAsRawJson = getFormattedTweet(keywords, minTweetLength, maxTweetLength);
                    Status status = TwitterObjectFactory.createStatus(formattedTweetAsRawJson);
                    streamKafkaStatusListener.onStatus(status);
                    sleep(sleepTimeMs);
                }
            }
            catch (TwitterException e){
                LOG.error("Error creating twitter status!", e);
            }
        });
    }

    private void sleep(long sleepTimeMs) {
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException e) {
            throw new StreamKafkaServiceException("Error while sleeping for waiting new status to create!");
        }
    }


    // Use of **Long with upper bound** to create random LOng number ThreadLocalRandom.current().nextLong(UPPER_BOUND)
    private String getFormattedTweet(String[] keywords, int minTweetLength, int maxTweetLength) {
       String[] params = new String[] {
               ZonedDateTime.now().format(DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT, Locale.ENGLISH)),
               String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
               getRandomTweetContent(keywords, minTweetLength, maxTweetLength),
               String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE))

       };
       String tweet = tweetAsRawJson;

       for(int i = 0; i < params.length; i++)
       {
           tweet = tweet.replace("{" + i + "}", params[i]);
       }
       return tweet;
    }

    private String getRandomTweetContent(String[] keywords, int minTweetLength, int maxTweetLength){
        StringBuilder tweet = new StringBuilder();
        int tweetLength = RANDOM.nextInt(maxTweetLength - minTweetLength +1) + minTweetLength;
        for (int i=0; i< tweetLength; i++)
        {
            tweet.append(WORDS[RANDOM.nextInt(WORDS.length)]).append(" ");
            if(i == tweetLength / 2){
                tweet.append(keywords[RANDOM.nextInt(keywords.length)]).append(" ");
            }

        }
        return tweet.toString().trim();
    }

}
