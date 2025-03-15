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
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.StreamRecord;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.StreamViewType;
import com.consentframework.consenthistory.consentingestor.domain.constants.ConsentTableAttributeName;
import com.consentframework.consenthistory.consentingestor.domain.constants.HttpStatusCode;
import com.consentframework.consenthistory.consentingestor.domain.constants.ResponseParameterName;
import com.consentframework.consenthistory.consentingestor.infrastructure.entities.DynamoDbConsentHistory;
import com.consentframework.consenthistory.consentingestor.infrastructure.repositories.DynamoDbConsentHistoryRepository;
import com.consentframework.consenthistory.consentingestor.testcommon.constants.TestConstants;
import com.consentframework.consenthistory.consentingestor.usecases.activities.IngestConsentChangeActivity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Map;

class ConsentStreamIngestorTest {
    private DynamoDbTable<DynamoDbConsentHistory> consentHistoryTable;
    private DynamoDbConsentHistoryRepository dynamoDbConsentHistoryRepository;
    private IngestConsentChangeActivity<Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue>>
        ingestConsentChangeActivity;
    private ConsentStreamIngestor ingestor;
    private Context context;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        consentHistoryTable = (DynamoDbTable<DynamoDbConsentHistory>) mock(DynamoDbTable.class);;
        dynamoDbConsentHistoryRepository = new DynamoDbConsentHistoryRepository(consentHistoryTable);
        ingestConsentChangeActivity = new IngestConsentChangeActivity<Map<String,
            software.amazon.awssdk.services.dynamodb.model.AttributeValue>>(dynamoDbConsentHistoryRepository);
        ingestor = new ConsentStreamIngestor(consentHistoryTable, dynamoDbConsentHistoryRepository, ingestConsentChangeActivity);
        context = Mockito.mock(Context.class);
        MockitoAnnotations.openMocks(this);
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

        final DynamodbStreamRecord record1 = createDynamoDbStreamModifyRecord("oldVal1", "newVal1");
        final DynamodbStreamRecord record2 = createDynamoDbStreamCreateRecord();
        Mockito.when(event.getRecords()).thenReturn(List.of(record1, record2));

        final Map<String, Object> result = ingestor.handleRequest(event, context);
        assertEquals(HttpStatusCode.SUCCESS.getValue(), result.get(ResponseParameterName.STATUS_CODE.getValue()));
        assertNull(result.get(ResponseParameterName.BODY.getValue()));
    }

    private DynamodbStreamRecord createDynamoDbStreamModifyRecord(final String oldTestAttributeValue, final String newTestAttributeValue) {
        final Map<String, AttributeValue> keys = Map.of(ConsentTableAttributeName.ID.getValue(),
            new AttributeValue().withS(TestConstants.TEST_CONSENT_PARTITION_KEY));
        final String testAttributeName = "testAttribute";
        final Map<String, AttributeValue> oldImage = Map.of(
            ConsentTableAttributeName.ID.getValue(), new AttributeValue().withS(TestConstants.TEST_CONSENT_PARTITION_KEY),
            ConsentTableAttributeName.CONSENT_VERSION.getValue(), new AttributeValue().withN("1"),
            ConsentTableAttributeName.CONSENT_DATA.getValue(), new AttributeValue().withM(Map.of(
                testAttributeName, new AttributeValue().withS(oldTestAttributeValue)
            ))
        );
        final Map<String, AttributeValue> newImage = Map.of(
            ConsentTableAttributeName.ID.getValue(), new AttributeValue().withS(TestConstants.TEST_CONSENT_PARTITION_KEY),
            ConsentTableAttributeName.CONSENT_VERSION.getValue(), new AttributeValue().withN("2"),
            ConsentTableAttributeName.CONSENT_DATA.getValue(), new AttributeValue().withM(Map.of(
                testAttributeName, new AttributeValue().withS(newTestAttributeValue)
            ))
        );
        final StreamRecord streamRecord = new StreamRecord()
            .withKeys(keys)
            .withApproximateCreationDateTime(Date.from(Instant.now()))
            .withOldImage(oldImage)
            .withNewImage(newImage)
            .withStreamViewType(StreamViewType.NEW_AND_OLD_IMAGES);

        final DynamodbStreamRecord dynamodbStreamRecord = mock(DynamodbStreamRecord.class);
        Mockito.when(dynamodbStreamRecord.getEventID()).thenReturn("1");
        Mockito.when(dynamodbStreamRecord.getEventName()).thenReturn("MODIFY");
        Mockito.when(dynamodbStreamRecord.getDynamodb()).thenReturn(streamRecord);

        return dynamodbStreamRecord;
    }

    private DynamodbStreamRecord createDynamoDbStreamCreateRecord() {
        final Map<String, AttributeValue> keys = Map.of(ConsentTableAttributeName.ID.getValue(),
            new AttributeValue().withS(TestConstants.TEST_CONSENT_PARTITION_KEY));
        final Map<String, AttributeValue> newImage = Map.of(
            ConsentTableAttributeName.ID.getValue(), new AttributeValue().withS(TestConstants.TEST_CONSENT_PARTITION_KEY),
            ConsentTableAttributeName.CONSENT_VERSION.getValue(), new AttributeValue().withN("1")
        );
        final StreamRecord streamRecord = new StreamRecord()
            .withKeys(keys)
            .withApproximateCreationDateTime(Date.from(Instant.now()))
            .withNewImage(newImage)
            .withStreamViewType(StreamViewType.NEW_AND_OLD_IMAGES);

        final DynamodbStreamRecord dynamodbStreamRecord = mock(DynamodbStreamRecord.class);
        Mockito.when(dynamodbStreamRecord.getEventID()).thenReturn("1");
        Mockito.when(dynamodbStreamRecord.getEventName()).thenReturn("INSERT");
        Mockito.when(dynamodbStreamRecord.getDynamodb()).thenReturn(streamRecord);

        return dynamodbStreamRecord;
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
