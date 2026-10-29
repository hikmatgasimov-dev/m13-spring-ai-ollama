package com.accenture.springai_bootcamp_demo.coach.workflow;


public class TopicNotCoveredException extends RuntimeException {

    public TopicNotCoveredException(String message) {
        super(message);
    }
}