package com.consentframework.consenthistory.consentingestor.domain.repositories;

import com.consentframework.consenthistory.consentingestor.domain.entities.ConsentHistoryRecord;

/**
 * Interface specifying supported integrations with consent history data.
 */
public interface ConsentHistoryRepository<T> {
    void save(final ConsentHistoryRecord<T> consentHistoryRecord);
}
