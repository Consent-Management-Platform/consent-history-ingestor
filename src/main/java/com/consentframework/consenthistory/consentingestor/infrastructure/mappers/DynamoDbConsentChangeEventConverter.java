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
        final String serviceUserId = parseServiceUserId(consentRecordPartitionKey);
        final Map<String, AttributeValue> oldImage = LambdaAttributeValueConverter.toDynamoDbAttributeValueMap(streamRecord.getOldImage());
        final Map<String, AttributeValue> newImage = LambdaAttributeValueConverter.toDynamoDbAttributeValueMap(streamRecord.getNewImage());

        return new DynamoDbConsentChangeEvent(consentRecordPartitionKey, eventId, eventType, eventTime, serviceUserId,
            Optional.ofNullable(oldImage), Optional.ofNullable(newImage));
    }

    /**
     * Parse the consent record partition key to get the service user id.
     *
     * Consent partition keys have format "ServiceId|UserId|ConsentId".
     *
     * This method extracts the "ServiceId|UserId" component for use as a ConsentHistory table GSI key,
     * to enable efficient consent history lookups for a given service user.
     *
     * @param consentRecordPartitionKey The partition key of the consent record.
     * @return The service user id.
     */
    private static String parseServiceUserId(final String consentRecordPartitionKey) {
        final String[] parts = consentRecordPartitionKey.split("\\|", 3);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid consent record partition key: " + consentRecordPartitionKey);
        }
        return parts[0] + "|" + parts[1];
    }
}
