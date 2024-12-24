package com.consentframework.consenthistory.consentingestor;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.consentframework.consenthistory.consentingestor.domain.constants.HttpStatusCode;
import com.consentframework.consenthistory.consentingestor.domain.constants.ResponseParameterName;

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
        // TODO: implement logic to parse the consent change event and sync it to the consent history data store
        System.out.println("Processing record: " + record);
    }
}
