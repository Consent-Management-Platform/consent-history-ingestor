package com.consentframework.consenthistory.consentingestor.infrastructure.mappers;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class to convert between Lambda and DynamoDB AttributeValue types.
 */
public final class LambdaAttributeValueConverter {
    /**
     * Private constructor to prevent instantiation.
     */
    private LambdaAttributeValueConverter() {}

    /**
     * Convert a map of Lambda AttributeValues to a map of DynamoDB AttributeValues.
     *
     *  @param lambdaAttributeMap The map of Lambda AttributeValues to convert.
     * @return The map of DynamoDB AttributeValues.
     */
    public static Map<String, AttributeValue> toDynamoDbAttributeValueMap(
            Map<String, com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue> lambdaAttributeMap) {
        if (lambdaAttributeMap == null) {
            return null;
        }

        return lambdaAttributeMap.entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey, entry -> convertAttributeValue(entry.getValue())
            ));
    }

    private static AttributeValue convertAttributeValue(
            com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue lambdaAttrValue) {
        final AttributeValue.Builder ddbAttrValueBuilder = AttributeValue.builder();

        if (lambdaAttrValue.getS() != null) {
            ddbAttrValueBuilder.s(lambdaAttrValue.getS());
        } else if (lambdaAttrValue.getN() != null) {
            ddbAttrValueBuilder.n(lambdaAttrValue.getN());
        } else if (lambdaAttrValue.getB() != null) {
            final SdkBytes sdkBytes = SdkBytes.fromByteBuffer(lambdaAttrValue.getB());
            ddbAttrValueBuilder.b(sdkBytes);
        } else if (lambdaAttrValue.getSS() != null) {
            ddbAttrValueBuilder.ss(lambdaAttrValue.getSS());
        } else if (lambdaAttrValue.getNS() != null) {
            ddbAttrValueBuilder.ns(lambdaAttrValue.getNS());
        } else if (lambdaAttrValue.getBS() != null) {
            ddbAttrValueBuilder.bs(lambdaAttrValue.getBS().stream()
                .map(SdkBytes::fromByteBuffer)
                .toList());
        } else if (lambdaAttrValue.getM() != null) {
            ddbAttrValueBuilder.m(toDynamoDbAttributeValueMap(lambdaAttrValue.getM()));
        } else if (lambdaAttrValue.getL() != null) {
            ddbAttrValueBuilder.l(lambdaAttrValue.getL().stream()
                .map(LambdaAttributeValueConverter::convertAttributeValue)
                .toList());
        } else if (lambdaAttrValue.getNULL() != null) {
            ddbAttrValueBuilder.nul(lambdaAttrValue.getNULL());
        } else if (lambdaAttrValue.getBOOL() != null) {
            ddbAttrValueBuilder.bool(lambdaAttrValue.getBOOL());
        }

        return ddbAttrValueBuilder.build();
    }
}
