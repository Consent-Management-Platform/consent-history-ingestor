package com.consentframework.consenthistory.consentingestor.infrastructure.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.consentframework.consenthistory.consentingestor.domain.entities.ConsentHistoryRecord;
import com.consentframework.consenthistory.consentingestor.infrastructure.entities.DynamoDbConsentHistory;
import com.consentframework.consenthistory.consentingestor.testcommon.constants.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.Optional;

class DynamoDbConsentHistoryRepositoryTest {
    private DynamoDbTable<DynamoDbConsentHistory> consentHistoryTable;
    private DynamoDbConsentHistoryRepository dynamoDbConsentHistoryRepository;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        consentHistoryTable = (DynamoDbTable<DynamoDbConsentHistory>) mock(DynamoDbTable.class);;
        dynamoDbConsentHistoryRepository = new DynamoDbConsentHistoryRepository(consentHistoryTable);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave_nullHistoryRecord() {
        assertThrows(NullPointerException.class, () -> dynamoDbConsentHistoryRepository.save(null));
    }

    @Test
    void testSave_nullImages() {
        final ConsentHistoryRecord<Map<String, AttributeValue>> consentHistoryRecord =
            new ConsentHistoryRecord<Map<String, AttributeValue>>(
                TestConstants.TEST_CONSENT_ID,
                TestConstants.TEST_CONSENT_EVENT_ID,
                TestConstants.TEST_CONSENT_EVENT_TIME,
                null,
                null,
                null
            );
        assertThrows(NullPointerException.class, () -> dynamoDbConsentHistoryRepository.save(consentHistoryRecord));
    }

    @Test
    void testSave_emptyImages() {
        final ConsentHistoryRecord<Map<String, AttributeValue>> consentHistoryRecord =
            new ConsentHistoryRecord<Map<String, AttributeValue>>(
                TestConstants.TEST_CONSENT_ID,
                TestConstants.TEST_CONSENT_EVENT_ID,
                TestConstants.TEST_CONSENT_EVENT_TIME,
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
            );
        final IllegalArgumentException thrownException = assertThrows(IllegalArgumentException.class, () ->
            dynamoDbConsentHistoryRepository.save(consentHistoryRecord));
        assertEquals(thrownException.getMessage(), DynamoDbConsentHistoryRepository.MISSING_DDB_IMAGES_ERROR_MESSAGE);
        verifyCalledPutItem(0);
    }

    @Test
    void testSave() {
        final ConsentHistoryRecord<Map<String, AttributeValue>> consentHistoryRecord =
            new ConsentHistoryRecord<Map<String, AttributeValue>>(
                TestConstants.TEST_CONSENT_ID,
                TestConstants.TEST_CONSENT_EVENT_ID,
                TestConstants.TEST_CONSENT_EVENT_TIME,
                Optional.empty(),
                Optional.of(TestConstants.TEST_CONSENT_RECORD),
                Optional.empty()
            );
        dynamoDbConsentHistoryRepository.save(consentHistoryRecord);
        verifyCalledPutItem(1);
    }

    private void verifyCalledPutItem(final int expectedTimes) {
        verify(consentHistoryTable, times(expectedTimes)).putItem(ArgumentMatchers.<PutItemEnhancedRequest<DynamoDbConsentHistory>>any());
    }
}
