package com.streamkafka.project.exceptions;


// Create custom exceptions
public class StreamKafkaServiceException extends RuntimeException {

    public StreamKafkaServiceException(){
        super();
    }

    public StreamKafkaServiceException(String message){
        super(message);
    }

    public StreamKafkaServiceException(String message, Throwable cause){
        super(message, cause);
    }

}
