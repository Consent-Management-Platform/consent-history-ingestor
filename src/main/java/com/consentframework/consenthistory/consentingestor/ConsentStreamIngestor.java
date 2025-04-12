package com.consentframework.consenthistory.consentingestor;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.consentframework.consenthistory.consentingestor.domain.constants.HttpStatusCode;
import com.consentframework.consenthistory.consentingestor.domain.constants.ResponseParameterName;
import com.consentframework.consenthistory.consentingestor.domain.repositories.ConsentHistoryRepository;
import com.consentframework.consenthistory.consentingestor.infrastructure.adapters.DynamoDbConsentChangeEvent;
import com.consentframework.consenthistory.consentingestor.infrastructure.mappers.DynamoDbConsentChangeEventConverter;
import com.consentframework.consenthistory.consentingestor.infrastructure.repositories.DynamoDbConsentHistoryRepository;
import com.consentframework.consenthistory.consentingestor.usecases.activities.IngestConsentChangeActivity;
import com.consentframework.shared.api.infrastructure.entities.DynamoDbConsentHistory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;

/**
 * Entry point for the service, used to ingest consent updates
 * from a data stream and sync changes to the consent history data store.
 */
public class ConsentStreamIngestor implements RequestHandler<DynamodbEvent, Map<String, Object>> {
    private static final Logger logger = LogManager.getLogger(ConsentStreamIngestor.class);

    private final DynamoDbTable<DynamoDbConsentHistory> consentHistoryTable;
    private final ConsentHistoryRepository<Map<String, AttributeValue>> consentHistoryRepository;
    private final IngestConsentChangeActivity<Map<String, AttributeValue>> ingestConsentChangeActivity;

    /**
     * Constructor to inject dependencies.
     *
     * @param consentHistoryTable The DynamoDB table for storing consent history records.
     * @param consentHistoryRepository The repository for storing consent history records.
     * @param ingestConsentChangeActivity The activity for ingesting consent change events.
     */
    public ConsentStreamIngestor(final DynamoDbTable<DynamoDbConsentHistory> consentHistoryTable,
                                 final ConsentHistoryRepository<Map<String, AttributeValue>> consentHistoryRepository,
                                 final IngestConsentChangeActivity<Map<String, AttributeValue>> ingestConsentChangeActivity) {
        this.consentHistoryTable = consentHistoryTable;
        this.consentHistoryRepository = consentHistoryRepository;
        this.ingestConsentChangeActivity = ingestConsentChangeActivity;
    }

    /**
     * Constructor to initialize dependencies with default implementations.
     */
    public ConsentStreamIngestor() {
        final DynamoDbClient ddb = DynamoDbClient.builder().build();
        final DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(ddb)
            .build();
        final TableSchema<DynamoDbConsentHistory> tableSchema = TableSchema.fromImmutableClass(DynamoDbConsentHistory.class);

        this.consentHistoryTable = enhancedClient.table(DynamoDbConsentHistory.TABLE_NAME, tableSchema);
        this.consentHistoryRepository = new DynamoDbConsentHistoryRepository(this.consentHistoryTable);
        this.ingestConsentChangeActivity = new IngestConsentChangeActivity<Map<String, AttributeValue>>(consentHistoryRepository);
    }

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

            logger.info("Successfully processed {} consent changes.", dynamodbStreamRecords.size());
            return Map.of(ResponseParameterName.STATUS_CODE.getValue(), HttpStatusCode.SUCCESS.getValue());
        } catch (final Exception e) {
            final String errorMessage = String.format("Error processing event: %s", e.getMessage());
            logger.error(errorMessage, e);

            return Map.of(
                ResponseParameterName.STATUS_CODE.getValue(), HttpStatusCode.INTERNAL_SERVER_ERROR.getValue(),
                ResponseParameterName.BODY.getValue(), errorMessage
            );
        }
    }

    private void processDynamoDbStreamRecord(final DynamodbStreamRecord record) {
        logger.info("Processing record: {}", record);
        final DynamoDbConsentChangeEvent consentChangeEvent = DynamoDbConsentChangeEventConverter.toDynamoDbConsentChangeEvent(record);
        this.ingestConsentChangeActivity.processEvent(consentChangeEvent);
    }
}
