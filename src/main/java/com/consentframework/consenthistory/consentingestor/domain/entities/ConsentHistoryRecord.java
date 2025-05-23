package com.consentframework.consenthistory.consentingestor.domain.entities;

import java.util.Optional;

/**
 * Represents a consent history data record created by this service
 * after a consent create/update/delete operation.
 *
 * @param <T> The type of the consent data.
 */
public record ConsentHistoryRecord<T>(
    // Unique consent ID from the source consent data store
    String id,
    // Unique consent event ID
    String eventId,
    // Type of event, eg. INSERT, MODIFY, DELETE
    String eventType,
    // Time of the consent event
    String eventTime,
    // ConsentHistoryByServiceUser GSI partition key, with values such as "ServiceId|UserId"
    // Used to efficiently query for all consent history records for a specific service and user
    String serviceUserId,
    // Optional time to live for the history record, primarily for testing purposes
    Optional<String> expiryTime,
    // Consent data before the operation, empty for creation events
    Optional<T> oldConsentData,
    // Consent data after the operation, empty for deletion events
    Optional<T> newConsentData
) {}
