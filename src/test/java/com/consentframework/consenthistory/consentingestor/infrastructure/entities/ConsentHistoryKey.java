package com.consentframework.consenthistory.consentingestor.infrastructure.entities;

/**
 * Represents the composite key for a consent history record.
 */
public record ConsentHistoryKey(
    // Unique consent ID from the source consent data store
    String id,
    // Unique consent event ID
    String eventId
) {
    public static final String REQUIRED_FIELDS_ERROR_MESSAGE = "id and eventId must be non-null";

    /**
     * Constructor for the consent history key.
     *
     * @param id Unique consent ID from the source consent data store
     * @param eventId Unique consent event ID
     * @throws IllegalArgumentException if id or eventId are null
     */
    public ConsentHistoryKey {
        if (id == null || eventId == null) {
            throw new IllegalArgumentException(REQUIRED_FIELDS_ERROR_MESSAGE);
        }
    }
}
