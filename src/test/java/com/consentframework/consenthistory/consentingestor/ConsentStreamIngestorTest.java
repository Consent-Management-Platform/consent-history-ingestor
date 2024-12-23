package com.consentframework.consenthistory.consentingestor;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ConsentStreamIngestorTest {
    @Test
    void testHandleRequest() {
        final ConsentStreamIngestor ingestor = new ConsentStreamIngestor();
        assertNotNull(ingestor.handleRequest(), "should return a non-null map");
    }
}
