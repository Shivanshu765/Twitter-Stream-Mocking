package com.streamkafka.project;

import com.streamkafka.project.config.StreamKafkaServiceConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// In modern java , we use Slf4j instead of loggerFactory and using constructor /field (@Autowired)injection
@SpringBootApplication
public class StreamKafkaServiceApplication implements CommandLineRunner {

    // Here we are using LoggerFactory as it will be used to use application.properties configData
    private static final Logger LOG = LoggerFactory.getLogger(StreamKafkaServiceApplication.class);


    // Constructor injection
    private final StreamKafkaServiceConfigData streamKafkaServiceConfigData;
    public StreamKafkaServiceApplication(StreamKafkaServiceConfigData configData) {
        this.streamKafkaServiceConfigData = configData;
    }

    /*
    Demonstrates the use of constructor-based dependency injection in Spring.

    Constructor injection has several advantages:

    1. Immutability: Dependencies can be defined as final, ensuring they are set only once.
    2. Thread safety: Immutable objects are safer to use in multi-threaded environments.
    3. Required dependencies: The constructor enforces that all mandatory dependencies
      are provided when the object is created.
    4. Better testability: Makes it easier to create instances manually during testing.
    5. No reflection overhead: Unlike field injection, Spring does not use reflection
      with constructor injection, resulting in better performance.

    Overall, constructor injection helps create robust, maintainable, and efficient applications.

    Field injection example:

    @Autowired
    private StreamKafkaServiceConfigData streamKafkaServiceConfigData;
*/











    // executes after dependency injection to perform initialization
    // Called once after spring being created
    // by default spring beans are created once because they are created a singleton
    // we can change this using @Scope(str) annotation
    // if we set "request" as parameter, it will create a new bean for each request
    // This is not a good option for application initialization job
    // Better Option: Use ApplicationListener/ CommandLinerRunner
    // Initialization logic : PostConstruct/ApplicationListener/ CommandLinerRunner

    @Override
    public void run(String... args) throws Exception {
        //System.out.println("App Starts...");
        LOG.info("App Starts.....");
        LOG.info(streamKafkaServiceConfigData.getImpKeywords().toString());
    }
}
