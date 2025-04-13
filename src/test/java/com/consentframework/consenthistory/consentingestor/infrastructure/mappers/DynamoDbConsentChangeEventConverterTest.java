package com.consentframework.consenthistory.consentingestor.infrastructure.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.StreamRecord;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.StreamViewType;
import com.consentframework.consenthistory.consentingestor.domain.constants.ConsentTableAttributeName;
import com.consentframework.consenthistory.consentingestor.domain.entities.ConsentHistoryRecord;
import com.consentframework.consenthistory.consentingestor.infrastructure.adapters.DynamoDbConsentChangeEvent;
import com.consentframework.consenthistory.consentingestor.testcommon.constants.TestConstants;
import com.consentframework.shared.api.infrastructure.entities.StoredConsentImage;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

class DynamoDbConsentChangeEventConverterTest {
    @Test
    void testToDynamoDbConsentChangeEvent() {
        final String oldConsentVersion = "1";
        final String newConsentVersion = "2";
        final String testAttributeName = "testAttribute";
        final String oldTestAttributeValue = "oldValue";
        final String newTestAttributeValue = "newValue";

        final Map<String, AttributeValue> keys = Map.of(ConsentTableAttributeName.ID.getValue(),
            new AttributeValue().withS(TestConstants.TEST_CONSENT_PARTITION_KEY));
        final Map<String, AttributeValue> oldImage = Map.of(
            ConsentTableAttributeName.ID.getValue(), new AttributeValue().withS(TestConstants.TEST_CONSENT_PARTITION_KEY),
            ConsentTableAttributeName.SERVICE_ID.getValue(), new AttributeValue().withS(TestConstants.TEST_SERVICE_ID),
            ConsentTableAttributeName.USER_ID.getValue(), new AttributeValue().withS(TestConstants.TEST_USER_ID),
            ConsentTableAttributeName.CONSENT_ID.getValue(), new AttributeValue().withS(TestConstants.TEST_CONSENT_ID),
            ConsentTableAttributeName.CONSENT_VERSION.getValue(), new AttributeValue().withN(oldConsentVersion),
            ConsentTableAttributeName.CONSENT_STATUS.getValue(), new AttributeValue().withS(TestConstants.TEST_CONSENT_STATUS),
            ConsentTableAttributeName.CONSENT_DATA.getValue(), new AttributeValue().withM(Map.of(
                testAttributeName, new AttributeValue().withS(oldTestAttributeValue)
            ))
        );
        final Map<String, AttributeValue> newImage = Map.of(
            ConsentTableAttributeName.ID.getValue(), new AttributeValue().withS(TestConstants.TEST_CONSENT_PARTITION_KEY),
            ConsentTableAttributeName.SERVICE_ID.getValue(), new AttributeValue().withS(TestConstants.TEST_SERVICE_ID),
            ConsentTableAttributeName.USER_ID.getValue(), new AttributeValue().withS(TestConstants.TEST_USER_ID),
            ConsentTableAttributeName.CONSENT_ID.getValue(), new AttributeValue().withS(TestConstants.TEST_CONSENT_ID),
            ConsentTableAttributeName.CONSENT_VERSION.getValue(), new AttributeValue().withN(newConsentVersion),
            ConsentTableAttributeName.CONSENT_STATUS.getValue(), new AttributeValue().withS(TestConstants.TEST_CONSENT_STATUS),
            ConsentTableAttributeName.CONSENT_DATA.getValue(), new AttributeValue().withM(Map.of(
                testAttributeName, new AttributeValue().withS(newTestAttributeValue)
            ))
        );
        final Date eventTime = Date.from(Instant.now());
        final StreamRecord streamRecord = new StreamRecord()
            .withKeys(keys)
            .withApproximateCreationDateTime(eventTime)
            .withOldImage(oldImage)
            .withNewImage(newImage)
            .withStreamViewType(StreamViewType.NEW_AND_OLD_IMAGES);

        final String eventId = "abcd-1234";
        final DynamodbStreamRecord dynamodbStreamRecord = mock(DynamodbStreamRecord.class);
        when(dynamodbStreamRecord.getEventID()).thenReturn(eventId);
        when(dynamodbStreamRecord.getEventName()).thenReturn("MODIFY");
        when(dynamodbStreamRecord.getDynamodb()).thenReturn(streamRecord);

        final DynamoDbConsentChangeEvent changeEvent = DynamoDbConsentChangeEventConverter.toDynamoDbConsentChangeEvent(
            dynamodbStreamRecord);
        assertNotNull(changeEvent);

        final ConsentHistoryRecord<StoredConsentImage> consentHistoryRecord =
            changeEvent.toConsentHistoryRecord();
        assertNotNull(consentHistoryRecord);
        assertEquals(TestConstants.TEST_CONSENT_PARTITION_KEY, consentHistoryRecord.id());
        assertEquals(TestConstants.TEST_SERVICE_USER_ID, consentHistoryRecord.serviceUserId());
        assertEquals(eventId, consentHistoryRecord.eventId());

        final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String expectedIso8601EventTime = isoFormat.format(eventTime);
        assertEquals(expectedIso8601EventTime, consentHistoryRecord.eventTime());

        final StoredConsentImage parsedOldImage = consentHistoryRecord.oldConsentData().get();
        assertEquals(TestConstants.TEST_CONSENT_PARTITION_KEY, parsedOldImage.getId());
        assertEquals(oldConsentVersion, parsedOldImage.getConsentVersion().toString());
        assertEquals(oldTestAttributeValue, parseConsentDataAttribute(parsedOldImage, testAttributeName));

        final StoredConsentImage parsedNewImage = consentHistoryRecord.newConsentData().get();
        assertEquals(TestConstants.TEST_CONSENT_PARTITION_KEY, parsedNewImage.getId());
        assertEquals(newConsentVersion, parsedNewImage.getConsentVersion().toString());
        assertEquals(newTestAttributeValue, parseConsentDataAttribute(parsedNewImage, testAttributeName));
    }

    @Test
    void testToDynamoDbConsentChangeEventWhenInvalidConsentImages() {
        final Map<String, AttributeValue> keys = Map.of(ConsentTableAttributeName.ID.getValue(),
            new AttributeValue().withS(TestConstants.TEST_CONSENT_PARTITION_KEY));
        final Map<String, AttributeValue> oldImage = null;
        final Map<String, AttributeValue> newImage = Map.of(
            ConsentTableAttributeName.ID.getValue(), new AttributeValue().withS(TestConstants.TEST_CONSENT_PARTITION_KEY)
        );
        final Date eventTime = Date.from(Instant.now());
        final StreamRecord streamRecord = new StreamRecord()
            .withKeys(keys)
            .withApproximateCreationDateTime(eventTime)
            .withOldImage(oldImage)
            .withNewImage(newImage)
            .withStreamViewType(StreamViewType.NEW_AND_OLD_IMAGES);

        final String eventId = "abcd-1234";
        final DynamodbStreamRecord dynamodbStreamRecord = mock(DynamodbStreamRecord.class);
        when(dynamodbStreamRecord.getEventID()).thenReturn(eventId);
        when(dynamodbStreamRecord.getEventName()).thenReturn("MODIFY");
        when(dynamodbStreamRecord.getDynamodb()).thenReturn(streamRecord);

        assertThrows(IllegalArgumentException.class, () -> {
            DynamoDbConsentChangeEventConverter.toDynamoDbConsentChangeEvent(dynamodbStreamRecord);
        });
    }

    @Test
    void testToDynamoDbConsentChangeEventWhenInvalidPartitionKey() {
        final String invalidPartitionKey = "invalidPartitionKey";

        final Map<String, AttributeValue> keys = Map.of(ConsentTableAttributeName.ID.getValue(),
            new AttributeValue().withS(invalidPartitionKey));
        final Date eventTime = Date.from(Instant.now());
        final StreamRecord streamRecord = new StreamRecord()
            .withKeys(keys)
            .withApproximateCreationDateTime(eventTime)
            .withStreamViewType(StreamViewType.KEYS_ONLY);

        final String eventId = "abcd-1234";
        final DynamodbStreamRecord dynamodbStreamRecord = mock(DynamodbStreamRecord.class);
        when(dynamodbStreamRecord.getEventID()).thenReturn(eventId);
        when(dynamodbStreamRecord.getEventName()).thenReturn("MODIFY");
        when(dynamodbStreamRecord.getDynamodb()).thenReturn(streamRecord);

        final IllegalArgumentException thrownException = assertThrows(IllegalArgumentException.class, () -> {
            DynamoDbConsentChangeEventConverter.toDynamoDbConsentChangeEvent(dynamodbStreamRecord);
        });
        assertEquals("Invalid consent record partition key: " + invalidPartitionKey, thrownException.getMessage());
    }

    private String parseConsentDataAttribute(final StoredConsentImage consentImage, final String attributeName) {
        return consentImage.getConsentData().get(attributeName);
    }
}
