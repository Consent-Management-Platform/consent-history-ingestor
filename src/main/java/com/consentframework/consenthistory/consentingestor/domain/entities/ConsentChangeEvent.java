package com.consentframework.consenthistory.consentingestor.domain.entities;

/**
 * Represents a consent change event ingested from an upstream data stream.
 *
 * The event must contain the necessary metadata to be converted into a {@link ConsentHistoryRecord}
 * that includes before-and-after states of the given consent record, as applicable
 * depending on whether the source consent operation was a create/update/delete.
 *
 * @param <T> The type of the consent data.
 */
public interface ConsentChangeEvent<T> {
    public ConsentHistoryRecord<T> toConsentHistoryRecord();
}
