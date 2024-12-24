package com.consentframework.consenthistory.consentingestor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.consentframework.consenthistory.consentingestor.domain.constants.HttpStatusCode;
import com.consentframework.consenthistory.consentingestor.domain.constants.ResponseParameterName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

class ConsentStreamIngestorTest {
    private ConsentStreamIngestor ingestor;
    private Context context;

    @BeforeEach
    void setUp() {
        ingestor = new ConsentStreamIngestor();
        context = Mockito.mock(Context.class);
    }

    @Test
    void testHandleRequest_noConsentChanges() {
        final DynamodbEvent event = mock(DynamodbEvent.class);

        final Map<String, Object> result = ingestor.handleRequest(event, context);
        assertEquals(HttpStatusCode.SUCCESS.getValue(), result.get(ResponseParameterName.STATUS_CODE.getValue()));
        assertNull(result.get(ResponseParameterName.BODY.getValue()));
    }

    @Test
    void testHandleRequest_multipleConsentChanges() {
        final DynamodbEvent event = mock(DynamodbEvent.class);
        final DynamodbStreamRecord record1 = mock(DynamodbStreamRecord.class);
        final DynamodbStreamRecord record2 = mock(DynamodbStreamRecord.class);

        Mockito.when(event.getRecords()).thenReturn(List.of(record1, record2));

        final Map<String, Object> result = ingestor.handleRequest(event, context);
        assertEquals(HttpStatusCode.SUCCESS.getValue(), result.get(ResponseParameterName.STATUS_CODE.getValue()));
        assertNull(result.get(ResponseParameterName.BODY.getValue()));
    }

    @Test
    void testHandleRequest_nullRequest() {
        assertNotNull(ingestor.handleRequest(null, null), "should return a non-null map");
    }

    @Test
    void testHandleRequest_unexpectedException() {
        final DynamodbEvent event = mock(DynamodbEvent.class);
        doThrow(new RuntimeException("Test error")).when(event).getRecords();

        final Map<String, Object> result = ingestor.handleRequest(event, context);
        assertTrue(result.get(ResponseParameterName.BODY.getValue()).toString().contains("Error processing event"));
    }
}
