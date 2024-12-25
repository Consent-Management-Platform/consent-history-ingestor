package com.consentframework.consenthistory.consentingestor.infrastructure.repositories;

import com.consentframework.consenthistory.consentingestor.domain.entities.ConsentHistoryRecord;
import com.consentframework.consenthistory.consentingestor.domain.repositories.ConsentHistoryRepository;
import com.consentframework.consenthistory.consentingestor.infrastructure.entities.DynamoDbConsentHistory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

/**
 * Class encapsulating logic to save consent history records to the ConsentHistory DynamoDB table.
 */
public class DynamoDbConsentHistoryRepository implements ConsentHistoryRepository<Map<String, AttributeValue>> {
    public static final String MISSING_DDB_IMAGES_ERROR_MESSAGE = "Both old and new consent data cannot be empty";

    private final DynamoDbTable<DynamoDbConsentHistory> consentHistoryTable;

    public DynamoDbConsentHistoryRepository(final DynamoDbTable<DynamoDbConsentHistory> consentHistoryTable) {
        this.consentHistoryTable = consentHistoryTable;
    }

    /**
     * Save consent history record to the ConsentHistory DynamoDB table.
     */
    @Override
    public void save(final ConsentHistoryRecord<Map<String, AttributeValue>> consentHistoryRecord) {
        System.out.println("Saving consent history record with source consent ID: " + consentHistoryRecord.id()
            + ", event ID: " + consentHistoryRecord.eventId());

        final DynamoDbConsentHistory.Builder ddbHistoryRecordBuilder = DynamoDbConsentHistory.builder()
            .id(consentHistoryRecord.id())
            .eventId(consentHistoryRecord.eventId())
            .eventTime(consentHistoryRecord.eventTime());

        if (consentHistoryRecord.oldConsentData().isEmpty() && consentHistoryRecord.newConsentData().isEmpty()) {
            throw new IllegalArgumentException(MISSING_DDB_IMAGES_ERROR_MESSAGE);
        }
        if (consentHistoryRecord.oldConsentData().isPresent()) {
            final Map<String, AttributeValue> oldImage = consentHistoryRecord.oldConsentData().get();
            ddbHistoryRecordBuilder.oldImage(oldImage);
        }
        if (consentHistoryRecord.newConsentData().isPresent()) {
            final Map<String, AttributeValue> newImage = consentHistoryRecord.newConsentData().get();
            ddbHistoryRecordBuilder.newImage(newImage);
        }

        final PutItemEnhancedRequest<DynamoDbConsentHistory> putItemRequest = PutItemEnhancedRequest.builder(DynamoDbConsentHistory.class)
            .item(ddbHistoryRecordBuilder.build())
            .build();
        consentHistoryTable.putItem(putItemRequest);

        System.out.println("Successfully saved consent history record with source consent ID: " + consentHistoryRecord.id()
            + ", event ID: " + consentHistoryRecord.eventId());
    }
}
