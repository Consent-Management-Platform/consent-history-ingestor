package com.consentframework.consenthistory.consentingestor.infrastructure.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.consentframework.consenthistory.consentingestor.domain.constants.ConsentTableAttributeName;
import com.consentframework.consenthistory.consentingestor.testcommon.constants.TestConstants;
import com.consentframework.shared.api.infrastructure.entities.StoredConsentImage;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

class LambdaAttributeValueConverterTest {
    @Test
    void toStoredConsentImageWithNull() {
        assertNull(LambdaAttributeValueConverter.toStoredConsentImage(null));
    }

    @Test
    void toStoredConsentImageWithEmptyMap() {
        assertThrows(NullPointerException.class, () -> LambdaAttributeValueConverter.toStoredConsentImage(Map.of()));
    }

    @Test
    void toStoredConsentImageWithoutOptionalAttributes() {
        final Map<String, AttributeValue> lambdaAttributeMap = Map.of(
            ConsentTableAttributeName.ID.getValue(), new AttributeValue().withS(TestConstants.TEST_CONSENT_PARTITION_KEY),
            ConsentTableAttributeName.SERVICE_ID.getValue(), new AttributeValue().withS(TestConstants.TEST_SERVICE_ID),
            ConsentTableAttributeName.USER_ID.getValue(), new AttributeValue().withS(TestConstants.TEST_USER_ID),
            ConsentTableAttributeName.CONSENT_ID.getValue(), new AttributeValue().withS(TestConstants.TEST_CONSENT_ID),
            ConsentTableAttributeName.CONSENT_VERSION.getValue(), new AttributeValue().withN(TestConstants.TEST_CONSENT_VERSION),
            ConsentTableAttributeName.CONSENT_STATUS.getValue(), new AttributeValue().withS(TestConstants.TEST_CONSENT_STATUS)
        );

        final StoredConsentImage parsedConsentImage = LambdaAttributeValueConverter.toStoredConsentImage(lambdaAttributeMap);
        assertEquals(TestConstants.TEST_CONSENT_PARTITION_KEY, parsedConsentImage.getId());
        assertEquals(TestConstants.TEST_SERVICE_ID, parsedConsentImage.getServiceId());
        assertEquals(TestConstants.TEST_USER_ID, parsedConsentImage.getUserId());
        assertEquals(TestConstants.TEST_CONSENT_ID, parsedConsentImage.getConsentId());
        assertEquals(TestConstants.TEST_CONSENT_VERSION, parsedConsentImage.getConsentVersion().toString());
        assertEquals(TestConstants.TEST_CONSENT_STATUS, parsedConsentImage.getConsentStatus());
        assertNull(parsedConsentImage.getConsentType());
        assertNull(parsedConsentImage.getConsentData());
        assertNull(parsedConsentImage.getExpiryTime());
    }

    @Test
    void toStoredConsentImageWithAllAttributes() {
        final StoredConsentImage parsedConsentImage = LambdaAttributeValueConverter.toStoredConsentImage(
            TestConstants.TEST_LAMBDA_EVENT_CONSENT_WITH_ALL_ATTRIBUTES);
        assertEquals(TestConstants.TEST_CONSENT_PARTITION_KEY, parsedConsentImage.getId());
        assertEquals(TestConstants.TEST_SERVICE_ID, parsedConsentImage.getServiceId());
        assertEquals(TestConstants.TEST_USER_ID, parsedConsentImage.getUserId());
        assertEquals(TestConstants.TEST_CONSENT_ID, parsedConsentImage.getConsentId());
        assertEquals(TestConstants.TEST_CONSENT_VERSION, parsedConsentImage.getConsentVersion().toString());
        assertEquals(TestConstants.TEST_CONSENT_STATUS, parsedConsentImage.getConsentStatus());
        assertEquals(TestConstants.TEST_CONSENT_TYPE, parsedConsentImage.getConsentType());
        assertEquals(TestConstants.TEST_CONSENT_DATA, parsedConsentImage.getConsentData());

        final OffsetDateTime expectedExpiryTime = OffsetDateTime.parse(TestConstants.TEST_CONSENT_EVENT_TIME)
            .withOffsetSameLocal(ZoneOffset.UTC);
        assertEquals(expectedExpiryTime, parsedConsentImage.getExpiryTime());
    }
}
