package com.consentframework.consenthistory.consentingestor.infrastructure.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.consentframework.consenthistory.consentingestor.domain.entities.ConsentHistoryKey;
import com.consentframework.consenthistory.consentingestor.domain.entities.ConsentHistoryRecord;
import com.consentframework.consenthistory.consentingestor.infrastructure.adapters.InMemoryConsentChangeEvent;
import com.consentframework.consenthistory.consentingestor.testcommon.constants.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryConsentHistoryRepositoryTest {
    private InMemoryConsentHistoryRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryConsentHistoryRepository();
    }

    @Test
    void testSaveThrowsExceptionIfNull() {
        assertThrows(NullPointerException.class, () -> {
            repository.save(null);
        });
    }

    @Test
    void testSaveThrowsExceptionIfNullId() {
        final ConsentHistoryRecord<String> consentHistoryRecord = new ConsentHistoryRecord<String>(
            null,
            TestConstants.TEST_CONSENT_EVENT_ID,
            InMemoryConsentChangeEvent.INSERT_EVENT_TYPE,
            TestConstants.TEST_CONSENT_EVENT_TIME,
            TestConstants.TEST_SERVICE_USER_ID,
            null,
            null,
            null
        );
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            repository.save(consentHistoryRecord);
        });
        assertEquals(ConsentHistoryKey.REQUIRED_FIELDS_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    void testSaveThrowsExceptionIfNullEventId() {
        final ConsentHistoryRecord<String> consentHistoryRecord = new ConsentHistoryRecord<String>(
            TestConstants.TEST_CONSENT_PARTITION_KEY,
            null,
            null,
            null,
            TestConstants.TEST_SERVICE_USER_ID,
            null,
            null,
            null
        );
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            repository.save(consentHistoryRecord);
        });
        assertEquals(ConsentHistoryKey.REQUIRED_FIELDS_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    void testSaveSucceedsForValidRecord() {
        final ConsentHistoryRecord<String> consentHistoryRecord = new ConsentHistoryRecord<String>(
            TestConstants.TEST_CONSENT_PARTITION_KEY,
            TestConstants.TEST_CONSENT_EVENT_ID,
            InMemoryConsentChangeEvent.INSERT_EVENT_TYPE,
            TestConstants.TEST_CONSENT_EVENT_TIME,
            TestConstants.TEST_SERVICE_USER_ID,
            null,
            null,
            null
        );
        repository.save(consentHistoryRecord);
    }
}
