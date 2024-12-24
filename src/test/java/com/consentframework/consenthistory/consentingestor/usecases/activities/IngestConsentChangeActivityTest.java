package com.consentframework.consenthistory.consentingestor.usecases.activities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.consentframework.consenthistory.consentingestor.domain.entities.ConsentChangeEvent;
import com.consentframework.consenthistory.consentingestor.domain.entities.ConsentHistoryRecord;
import com.consentframework.consenthistory.consentingestor.domain.repositories.ConsentHistoryRepository;
import com.consentframework.consenthistory.consentingestor.infrastructure.adapters.InMemoryConsentChangeEvent;
import com.consentframework.consenthistory.consentingestor.infrastructure.repositories.InMemoryConsentHistoryRepository;
import com.consentframework.consenthistory.consentingestor.testcommon.constants.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class IngestConsentChangeActivityTest {
    private ConsentHistoryRepository<String> consentHistoryRepository;
    private IngestConsentChangeActivity<String> ingestConsentChangeActivity;

    @Captor
    private ArgumentCaptor<ConsentHistoryRecord<String>> consentHistoryRecordArgumentCaptor;

    @BeforeEach
    public void setUp() {
        consentHistoryRepository = Mockito.spy(new InMemoryConsentHistoryRepository());
        ingestConsentChangeActivity = new IngestConsentChangeActivity<>(consentHistoryRepository);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessEvent() {
        final String oldConsentData = "oldConsentData";
        final String newConsentData = "newConsentData";
        final ConsentChangeEvent<String> consentChangeEvent = new InMemoryConsentChangeEvent(
            TestConstants.TEST_CONSENT_PARTITION_KEY,
            TestConstants.TEST_CONSENT_EVENT_ID,
            TestConstants.TEST_CONSENT_EVENT_TIME,
            oldConsentData,
            newConsentData
        );

        ingestConsentChangeActivity.processEvent(consentChangeEvent);

        verify(consentHistoryRepository, times(1)).save(consentHistoryRecordArgumentCaptor.capture());
        assertEquals(TestConstants.TEST_CONSENT_PARTITION_KEY, consentHistoryRecordArgumentCaptor.getValue().id());
        assertEquals(TestConstants.TEST_CONSENT_EVENT_ID, consentHistoryRecordArgumentCaptor.getValue().eventId());
        assertEquals(oldConsentData, consentHistoryRecordArgumentCaptor.getValue().oldConsentData().get());
        assertEquals(newConsentData, consentHistoryRecordArgumentCaptor.getValue().newConsentData().get());
    }

    @Test
    public void testProcessEventWithNullOldConsentData() {
        final String newConsentData = "newConsentData";
        final ConsentChangeEvent<String> consentChangeEvent = new InMemoryConsentChangeEvent(
            TestConstants.TEST_CONSENT_PARTITION_KEY,
            TestConstants.TEST_CONSENT_EVENT_ID,
            TestConstants.TEST_CONSENT_EVENT_TIME,
            null,
            newConsentData
        );

        ingestConsentChangeActivity.processEvent(consentChangeEvent);

        verify(consentHistoryRepository, times(1)).save(consentHistoryRecordArgumentCaptor.capture());
        assertEquals(TestConstants.TEST_CONSENT_PARTITION_KEY, consentHistoryRecordArgumentCaptor.getValue().id());
        assertEquals(TestConstants.TEST_CONSENT_EVENT_ID, consentHistoryRecordArgumentCaptor.getValue().eventId());
        assertTrue(consentHistoryRecordArgumentCaptor.getValue().oldConsentData().isEmpty());
        assertEquals(newConsentData, consentHistoryRecordArgumentCaptor.getValue().newConsentData().get());
    }
}
