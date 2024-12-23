package com.consentframework.consenthistory.consentingestor.domain.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.consentframework.consenthistory.consentingestor.testcommon.constants.TestConstants;
import org.junit.jupiter.api.Test;

class ConsentHistoryKeyTest {
    @Test
    void testRejectsNullId() {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new ConsentHistoryKey(null, TestConstants.TEST_CONSENT_EVENT_ID);
        });
        assertEquals(ConsentHistoryKey.REQUIRED_FIELDS_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    void testRejectsNullEventId() {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new ConsentHistoryKey(TestConstants.TEST_CONSENT_PARTITION_KEY, null);
        });
        assertEquals(ConsentHistoryKey.REQUIRED_FIELDS_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    void testConstructsValidKey() {
        final ConsentHistoryKey consentHistoryKey = new ConsentHistoryKey(
            TestConstants.TEST_CONSENT_PARTITION_KEY,
            TestConstants.TEST_CONSENT_EVENT_ID
        );
        assertEquals(TestConstants.TEST_CONSENT_PARTITION_KEY, consentHistoryKey.id());
        assertEquals(TestConstants.TEST_CONSENT_EVENT_ID, consentHistoryKey.eventId());
    }
}
