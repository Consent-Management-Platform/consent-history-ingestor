package com.consentframework.consenthistory.consentingestor.infrastructure.adapters;

import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.consentframework.consenthistory.consentingestor.domain.entities.ConsentChangeEvent;
import com.consentframework.consenthistory.consentingestor.domain.entities.ConsentHistoryRecord;

import java.util.Map;
import java.util.Optional;

/**
 * Consent change events ingested from the ServiceUserConsent DynamoDB table stream.
 */
public class DynamoDbConsentChangeEvent implements ConsentChangeEvent<Map<String, AttributeValue>> {
    private final String sourceConsentId;
    private final String eventId;
    private final String eventTime;
    private final Map<String, AttributeValue> oldConsentData;
    private final Map<String, AttributeValue> newConsentData;

    /**
     * Create a new DynamoDB consent change event.
     */
    public DynamoDbConsentChangeEvent(final String sourceConsentId, final String eventId, final String eventTime,
            final Map<String, AttributeValue> oldConsentData, final Map<String, AttributeValue> newConsentData) {
        this.sourceConsentId = sourceConsentId;
        this.eventId = eventId;
        this.eventTime = eventTime;
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
            eventTime,
            null,
            Optional.ofNullable(oldConsentData),
            Optional.ofNullable(newConsentData)
        );
    }
}
