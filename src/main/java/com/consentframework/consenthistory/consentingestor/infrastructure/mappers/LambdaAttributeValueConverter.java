package com.consentframework.consenthistory.consentingestor.infrastructure.mappers;

import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.consentframework.consenthistory.consentingestor.domain.constants.ConsentTableAttributeName;
import com.consentframework.shared.api.infrastructure.entities.StoredConsentImage;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class to convert between Lambda AttributeValue maps and consent data models.
 */
public final class LambdaAttributeValueConverter {
    /**
     * Private constructor to prevent instantiation.
     */
    private LambdaAttributeValueConverter() {}

    /**
     * Convert a map of Lambda AttributeValues to a StoredConsentImage object.
     *
     * @param lambdaAttributeMap The map of Lambda AttributeValues to convert.
     * @return The StoredConsentImage object.
     */
    public static StoredConsentImage toStoredConsentImage(final Map<String, AttributeValue> lambdaAttributeMap) {
        if (lambdaAttributeMap == null) {
            return null;
        }

        return new StoredConsentImage()
            .id(lambdaAttributeMap.get(ConsentTableAttributeName.ID.getValue()).getS())
            .serviceId(lambdaAttributeMap.get(ConsentTableAttributeName.SERVICE_ID.getValue()).getS())
            .userId(lambdaAttributeMap.get(ConsentTableAttributeName.USER_ID.getValue()).getS())
            .consentId(lambdaAttributeMap.get(ConsentTableAttributeName.CONSENT_ID.getValue()).getS())
            .consentVersion(Integer.valueOf(lambdaAttributeMap.get(ConsentTableAttributeName.CONSENT_VERSION.getValue()).getN()))
            .consentStatus(lambdaAttributeMap.get(ConsentTableAttributeName.CONSENT_STATUS.getValue()).getS())
            .consentType(parseOptionalStringAttributeValue(lambdaAttributeMap, ConsentTableAttributeName.CONSENT_TYPE))
            .consentData(parseConsentDataAttributeValueMap(lambdaAttributeMap))
            .expiryTime(parseExpiryTime(lambdaAttributeMap));
    }

    private static String parseOptionalStringAttributeValue(final Map<String, AttributeValue> lambdaAttributeMap,
            final ConsentTableAttributeName attributeName) {
        final AttributeValue attributeValue = lambdaAttributeMap.get(attributeName.getValue());
        if (attributeValue == null) {
            return null;
        }
        return attributeValue.getS();
    }

    private static OffsetDateTime parseExpiryTime(final Map<String, AttributeValue> lambdaAttributeMap) {
        final String expiryTimeString = parseOptionalStringAttributeValue(lambdaAttributeMap, ConsentTableAttributeName.EXPIRY_TIME);
        if (expiryTimeString == null) {
            return null;
        }
        return OffsetDateTime.parse(expiryTimeString).withOffsetSameLocal(ZoneOffset.UTC);
    }

    /**
     * Parse consentData from the Lambda AttributeValue map to a Map of String key-value pairs.
     *
     * Invariant: The consent data map has only string key values.
     *
     * @param attributeMap The attribute map to convert.
     * @return The parsed consent data map.
     */
    private static Map<String, String> parseConsentDataAttributeValueMap(final Map<String, AttributeValue> lambdaAttributeMap) {
        final AttributeValue consentDataAttributeValue = lambdaAttributeMap.get(ConsentTableAttributeName.CONSENT_DATA.getValue());
        if (consentDataAttributeValue == null) {
            return null;
        }
        final Map<String, AttributeValue> consentData = consentDataAttributeValue.getM();
        return consentData.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getS()));
    }
}
