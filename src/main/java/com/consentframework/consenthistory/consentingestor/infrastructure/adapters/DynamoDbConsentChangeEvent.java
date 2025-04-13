package com.consentframework.consenthistory.consentingestor.infrastructure.adapters;

import com.consentframework.consenthistory.consentingestor.domain.entities.ConsentChangeEvent;
import com.consentframework.consenthistory.consentingestor.domain.entities.ConsentHistoryRecord;
import com.consentframework.shared.api.infrastructure.entities.StoredConsentImage;

import java.util.Optional;

/**
 * Consent change events ingested from the ServiceUserConsent DynamoDB table stream.
 */
public class DynamoDbConsentChangeEvent implements ConsentChangeEvent<StoredConsentImage> {
    private final String sourceConsentId;
    private final String eventId;
    private final String eventType;
    private final String eventTime;
    private final String serviceUserId;
    private final Optional<StoredConsentImage> oldConsentData;
    private final Optional<StoredConsentImage> newConsentData;

    /**
     * Create a new DynamoDB consent change event.
     */
    public DynamoDbConsentChangeEvent(
            final String sourceConsentId,
            final String eventId,
            final String eventType,
            final String eventTime,
            final String serviceUserId,
            final Optional<StoredConsentImage> oldConsentData,
            final Optional<StoredConsentImage> newConsentData) {
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
    public ConsentHistoryRecord<StoredConsentImage> toConsentHistoryRecord() {
        return new ConsentHistoryRecord<StoredConsentImage>(
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
