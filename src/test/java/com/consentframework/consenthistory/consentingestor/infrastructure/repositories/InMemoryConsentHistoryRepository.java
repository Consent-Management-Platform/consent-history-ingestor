package com.consentframework.consenthistory.consentingestor.infrastructure.repositories;

import com.consentframework.consenthistory.consentingestor.domain.entities.ConsentHistoryRecord;
import com.consentframework.consenthistory.consentingestor.domain.repositories.ConsentHistoryRepository;
import com.consentframework.consenthistory.consentingestor.infrastructure.entities.ConsentHistoryKey;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory representation of a consent history repository, used for testing.
 */
public class InMemoryConsentHistoryRepository implements ConsentHistoryRepository<String> {
    private final Map<ConsentHistoryKey, ConsentHistoryRecord<String>> consentHistoryDataStore = new HashMap<>();

    @Override
    public void save(final ConsentHistoryRecord<String> consentHistoryRecord) {
        final ConsentHistoryKey consentHistoryKey = new ConsentHistoryKey(consentHistoryRecord.id(), consentHistoryRecord.eventId());
        consentHistoryDataStore.put(consentHistoryKey, consentHistoryRecord);
    }
}
