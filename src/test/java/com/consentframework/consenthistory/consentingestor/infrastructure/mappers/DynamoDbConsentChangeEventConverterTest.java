package com.consentframework.consenthistory.consentingestor.infrastructure.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
            ConsentTableAttributeName.CONSENT_VERSION.getValue(), new AttributeValue().withN(oldConsentVersion),
            ConsentTableAttributeName.CONSENT_DATA.getValue(), new AttributeValue().withM(Map.of(
                testAttributeName, new AttributeValue().withS(oldTestAttributeValue)
            ))
        );
        final Map<String, AttributeValue> newImage = Map.of(
            ConsentTableAttributeName.ID.getValue(), new AttributeValue().withS(TestConstants.TEST_CONSENT_PARTITION_KEY),
            ConsentTableAttributeName.CONSENT_VERSION.getValue(), new AttributeValue().withN(newConsentVersion),
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

        final ConsentHistoryRecord<Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue>> consentHistoryRecord =
            changeEvent.toConsentHistoryRecord();
        assertNotNull(consentHistoryRecord);
        assertEquals(TestConstants.TEST_CONSENT_PARTITION_KEY, consentHistoryRecord.id());
        assertEquals(eventId, consentHistoryRecord.eventId());

        final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String expectedIso8601EventTime = isoFormat.format(eventTime);
        assertEquals(expectedIso8601EventTime, consentHistoryRecord.eventTime());

        final Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> oldImageMap =
            consentHistoryRecord.oldConsentData().get();
        assertEquals(TestConstants.TEST_CONSENT_PARTITION_KEY, oldImageMap.get(ConsentTableAttributeName.ID.getValue()).s());
        assertEquals(oldConsentVersion, oldImageMap.get(ConsentTableAttributeName.CONSENT_VERSION.getValue()).n());
        assertEquals(oldTestAttributeValue, parseConsentDataAttribute(oldImageMap, testAttributeName));

        final Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> newImageMap =
            consentHistoryRecord.newConsentData().get();
        assertEquals(TestConstants.TEST_CONSENT_PARTITION_KEY, newImageMap.get(ConsentTableAttributeName.ID.getValue()).s());
        assertEquals(newConsentVersion, newImageMap.get(ConsentTableAttributeName.CONSENT_VERSION.getValue()).n());
        assertEquals(newTestAttributeValue, parseConsentDataAttribute(newImageMap, testAttributeName));
    }

    private String parseConsentDataAttribute(
            final Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> consentImage,
            final String attributeName) {
        return consentImage.get(ConsentTableAttributeName.CONSENT_DATA.getValue()).m()
            .get(attributeName).s();
    }
}
