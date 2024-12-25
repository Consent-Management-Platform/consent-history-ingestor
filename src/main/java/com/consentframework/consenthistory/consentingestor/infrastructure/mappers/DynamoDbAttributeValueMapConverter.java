package com.consentframework.consenthistory.consentingestor.infrastructure.mappers;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.document.EnhancedDocument;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

/**
 * Attribute converter for mapping between a map of string to AttributeValue and a single JSON string AttributeValue.
 *
 * Used by the Enhanced DynamoDB client to serialize and deserialize nested DynamoDB map attributes.
 */
public class DynamoDbAttributeValueMapConverter implements AttributeConverter<Map<String, AttributeValue>> {
    /**
     * Convert a map of string to AttributeValue to a single JSON string AttributeValue.
     */
    @Override
    public AttributeValue transformFrom(final Map<String, AttributeValue> ddbAttributeMap) {
        final String attributesJson = EnhancedDocument.fromAttributeValueMap(ddbAttributeMap).toJson();
        return AttributeValue.builder().s(attributesJson).build();
    }

    /**
     * Convert a JSON string AttributeValue to a map of string to AttributeValue.
     */
    @Override
    public Map<String, AttributeValue> transformTo(final AttributeValue attributeValue) {
        return EnhancedDocument.fromJson(attributeValue.s()).toMap();
    }

    /**
     * The EnhancedType for a map of string to AttributeValue, used by the Enhanced DynamoDB client.
     */
    @Override
    public EnhancedType<Map<String, AttributeValue>> type() {
        return EnhancedType.mapOf(String.class, AttributeValue.class);
    }

    /**
     * The AttributeValueType for a JSON string AttributeValue, used by the Enhanced DynamoDB client.
     */
    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
