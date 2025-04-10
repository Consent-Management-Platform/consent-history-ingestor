package com.consentframework.consenthistory.consentingestor.infrastructure.adapters;

import com.consentframework.consenthistory.consentingestor.domain.entities.ConsentChangeEvent;
import com.consentframework.consenthistory.consentingestor.domain.entities.ConsentHistoryRecord;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.Optional;

/**
 * Consent change events ingested from the ServiceUserConsent DynamoDB table stream.
 */
public class DynamoDbConsentChangeEvent implements ConsentChangeEvent<Map<String, AttributeValue>> {
    private final String sourceConsentId;
    private final String eventId;
    private final String eventType;
    private final String eventTime;
    private final String serviceUserId;
    private final Optional<Map<String, AttributeValue>> oldConsentData;
    private final Optional<Map<String, AttributeValue>> newConsentData;

    /**
     * Create a new DynamoDB consent change event.
     */
    public DynamoDbConsentChangeEvent(
            final String sourceConsentId,
            final String eventId,
            final String eventType,
            final String eventTime,
            final String serviceUserId,
            final Optional<Map<String, AttributeValue>> oldConsentData,
            final Optional<Map<String, AttributeValue>> newConsentData) {
        this.sourceConsentId = sourceConsentId;
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.serviceUserId = serviceUserId;
        this.oldConsentData = oldConsentData;
        this.newConsentData = newConsentData;
    }

    /**
     * Convert the DynamoDB consent change event to a consent history record.
     */
    @Override
    public ConsentHistoryRecord<Map<String, AttributeValue>> toConsentHistoryRecord() {
        return new ConsentHistoryRecord<Map<String, AttributeValue>>(
            sourceConsentId,
            eventId,
            eventType,
            eventTime,
            serviceUserId,
            null,
            oldConsentData,
            newConsentData
        );
    }
}
