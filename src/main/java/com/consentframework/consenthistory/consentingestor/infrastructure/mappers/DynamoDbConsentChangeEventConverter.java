package com.consentframework.consenthistory.consentingestor.infrastructure.mappers;

import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.StreamRecord;
import com.consentframework.consenthistory.consentingestor.infrastructure.adapters.DynamoDbConsentChangeEvent;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.Optional;

/**
 * Converter for mapping between a DynamoDB stream record and a consent change event.
 */
public final class DynamoDbConsentChangeEventConverter {
    private DynamoDbConsentChangeEventConverter() {}

    /**
     * Convert a DynamoDB stream record to a consent change event.
     *
     * @param record The DynamoDB stream record.
     * @return The consent change event.
     */
    public static DynamoDbConsentChangeEvent toDynamoDbConsentChangeEvent(final DynamodbStreamRecord record) {
        final String eventId = record.getEventID();
        final StreamRecord streamRecord = record.getDynamodb();
        final String eventType = record.getEventName();
        final String eventTime = streamRecord.getApproximateCreationDateTime().toInstant().toString();
        final String consentRecordPartitionKey = streamRecord.getKeys().get("id").getS();
        final Map<String, AttributeValue> oldImage = LambdaAttributeValueConverter.toDynamoDbAttributeValueMap(streamRecord.getOldImage());
        final Map<String, AttributeValue> newImage = LambdaAttributeValueConverter.toDynamoDbAttributeValueMap(streamRecord.getNewImage());

        return new DynamoDbConsentChangeEvent(consentRecordPartitionKey, eventId, eventType, eventTime,
            Optional.ofNullable(oldImage), Optional.ofNullable(newImage));
    }
}
