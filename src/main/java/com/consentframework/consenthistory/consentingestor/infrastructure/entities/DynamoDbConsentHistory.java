package com.consentframework.consenthistory.consentingestor.infrastructure.entities;

import com.consentframework.consenthistory.consentingestor.infrastructure.annotations.DynamoDbImmutableStyle;
import com.consentframework.consenthistory.consentingestor.infrastructure.mappers.DynamoDbAttributeValueMapConverter;
import jakarta.annotation.Nullable;
import org.immutables.value.Value.Immutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

/**
 * Represents a ConsentHistory DynamoDB table record.
 */
@Immutable
@DynamoDbImmutableStyle
@DynamoDbImmutable(builder = DynamoDbConsentHistory.Builder.class)
public interface DynamoDbConsentHistory {
    public static final String TABLE_NAME = "ConsentHistory";
    public static final String PARTITION_KEY = "id";
    public static final String SORT_KEY = "eventId";

    static Builder builder() {
        return new Builder();
    }

    /**
     * DynamoDbConsentHistory Builder class, intentionally empty.
     */
    class Builder extends ImmutableDynamoDbConsentHistory.Builder {}

    @DynamoDbPartitionKey
    String id();

    @DynamoDbSortKey
    String eventId();

    String eventType();

    String eventTime();

    @Nullable
    @DynamoDbConvertedBy(DynamoDbAttributeValueMapConverter.class)
    Map<String, AttributeValue> oldImage();

    @Nullable
    @DynamoDbConvertedBy(DynamoDbAttributeValueMapConverter.class)
    Map<String, AttributeValue> newImage();
}
