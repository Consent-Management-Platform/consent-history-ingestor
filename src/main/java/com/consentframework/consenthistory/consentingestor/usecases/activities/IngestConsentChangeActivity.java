package com.consentframework.consenthistory.consentingestor.usecases.activities;

import com.consentframework.consenthistory.consentingestor.domain.entities.ConsentChangeEvent;
import com.consentframework.consenthistory.consentingestor.domain.repositories.ConsentHistoryRepository;

/**
 * Activity to ingest a consent change event.
 *
 * This activity will:
 * - Receive a consent change event
 * - Convert it to a consent history record
 * - Save the consent history record to the consent history repository
 */
public class IngestConsentChangeActivity<T> {
    private final ConsentHistoryRepository<T> consentHistoryRepository;

    public IngestConsentChangeActivity(final ConsentHistoryRepository<T> consentHistoryRepository) {
        this.consentHistoryRepository = consentHistoryRepository;
    }

    /**
     * Process a consent change event, syncing it to consent history.
     *
     * @param consentChangeEvent The parsed consent change event.
     */
    public void processEvent(final ConsentChangeEvent<T> consentChangeEvent) {
        consentHistoryRepository.save(consentChangeEvent.toConsentHistoryRecord());
    }
}
