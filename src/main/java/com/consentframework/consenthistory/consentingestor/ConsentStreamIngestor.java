package com.consentframework.consenthistory.consentingestor;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.StreamRecord;
import com.consentframework.consenthistory.consentingestor.domain.constants.HttpStatusCode;
import com.consentframework.consenthistory.consentingestor.domain.constants.ResponseParameterName;
import com.consentframework.consenthistory.consentingestor.domain.entities.ConsentHistoryRecord;
import com.consentframework.consenthistory.consentingestor.infrastructure.adapters.DynamoDbConsentChangeEvent;

import java.util.List;
import java.util.Map;

/**
 * Entry point for the service, used to ingest consent updates
 * from a data stream and sync changes to the consent history data store.
 */
public class ConsentStreamIngestor implements RequestHandler<DynamodbEvent, Map<String, Object>> {
    /**
     * Handle an incoming request to ingest consent updates.
     *
     * @return A map of response data.
     */
    @Override
    public Map<String, Object> handleRequest(final DynamodbEvent event, final Context context) {
        try {
            final List<DynamodbStreamRecord> dynamodbStreamRecords = event.getRecords();
            dynamodbStreamRecords.forEach(this::processDynamoDbStreamRecord);

            System.out.println("Successfully processed " + dynamodbStreamRecords.size() + " consent changes.");
            return Map.of(ResponseParameterName.STATUS_CODE.getValue(), HttpStatusCode.SUCCESS.getValue());
        } catch (final Exception e) {
            e.printStackTrace();

            final String errorMessage = "Error processing event: " + e.getMessage();
            System.err.println(errorMessage);
            return Map.of(
                ResponseParameterName.STATUS_CODE.getValue(), HttpStatusCode.INTERNAL_SERVER_ERROR.getValue(),
                ResponseParameterName.BODY.getValue(), errorMessage
            );
        }
    }

    private void processDynamoDbStreamRecord(final DynamodbStreamRecord record) {
        System.out.println("Processing record: " + record);
        final ConsentHistoryRecord<Map<String, AttributeValue>> consentHistoryRecord = parseDynamoDbStreamRecord(record);

        // TODO: implement logic to write consent history record to the ConsentHistory DynamoDB table
        System.out.println("TODO: Writing consent history record with source consent ID: " + consentHistoryRecord.id()
            + ", event ID: " + consentHistoryRecord.eventId());
    }

    private ConsentHistoryRecord<Map<String, AttributeValue>> parseDynamoDbStreamRecord(final DynamodbStreamRecord record) {
        final String eventId = record.getEventID();
        final StreamRecord streamRecord = record.getDynamodb();
        final String consentRecordPartitionKey = streamRecord.getKeys().get("id").getS();
        final Map<String, AttributeValue> oldImage = streamRecord.getOldImage();
        final Map<String, AttributeValue> newImage = streamRecord.getNewImage();

        final DynamoDbConsentChangeEvent consentChangeEvent = new DynamoDbConsentChangeEvent(consentRecordPartitionKey, eventId,
            oldImage, newImage);
        return consentChangeEvent.toConsentHistoryRecord();
    }
}
