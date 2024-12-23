package com.consentframework.consenthistory.consentstreamprocessor;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ConsentStreamProcessorTest {
    @Test
    void testHandleRequest() {
        final ConsentStreamProcessor consentStreamProcessor = new ConsentStreamProcessor();
        assertNotNull(consentStreamProcessor.handleRequest(), "should return a non-null map");
    }
}
