package com.consentframework.consenthistory.consentingestor.infrastructure.adapters;

import com.consentframework.consenthistory.consentingestor.domain.entities.ConsentChangeEvent;
import com.consentframework.consenthistory.consentingestor.domain.entities.ConsentHistoryRecord;

import java.util.Optional;

/**
 * In-memory representation of a consent change event, used for testing.
 */
public class InMemoryConsentChangeEvent implements ConsentChangeEvent<String> {
    private final String sourceConsentId;
    private final String eventId;
    private final String eventTime;
    private final String oldConsentData;
    private final String newConsentData;

    /**
     * Construct an in-memory consent change event.
     *
     * @param sourceConsentId Unique identifier of the source consent
     * @param eventId Unique identifier of the consent change event
     * @param eventTime Time of the consent change event
     * @param oldConsentData Consent data prior to the source event
     * @param newConsentData Consent data after the source event
     */
    public InMemoryConsentChangeEvent(final String sourceConsentId, final String eventId, final String eventTime,
            final String oldConsentData, final String newConsentData) {
        this.sourceConsentId = sourceConsentId;
        this.eventId = eventId;
        this.eventTime = eventTime;
        this.oldConsentData = oldConsentData;
        this.newConsentData = newConsentData;
    }

    /**
     * Converts change event to a consent history record.
     */
    @Override
    public ConsentHistoryRecord<String> toConsentHistoryRecord() {
        return new ConsentHistoryRecord<String>(
            sourceConsentId,
            eventId,
            eventTime,
            null,
            Optional.ofNullable(oldConsentData),
            Optional.ofNullable(newConsentData)
        );
    }

}
