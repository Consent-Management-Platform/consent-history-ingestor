package com.consentframework.consenthistory.consentingestor.testcommon.constants;

import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.consentframework.consenthistory.consentingestor.domain.constants.ConsentTableAttributeName;
import com.consentframework.consenthistory.consentingestor.domain.constants.DynamoDbStreamEventType;
import com.consentframework.shared.api.infrastructure.entities.DynamoDbConsentHistory;
import com.consentframework.shared.api.infrastructure.entities.StoredConsentImage;

import java.util.Map;

/**
 * Utility class defining common test constants.
 */
public final class TestConstants {
    public static final String TEST_CONSENT_ID = "TestConsentId";
    public static final String TEST_SERVICE_ID = "TestServiceId";
    public static final String TEST_USER_ID = "TestUserId";
    public static final String TEST_CONSENT_VERSION = "1";
    public static final String TEST_CONSENT_PARTITION_KEY = String.format("%s|%s|%s", TEST_SERVICE_ID, TEST_USER_ID, TEST_CONSENT_ID);
    public static final String TEST_SERVICE_USER_ID = String.format("%s|%s", TEST_SERVICE_ID, TEST_USER_ID);
    public static final String TEST_CONSENT_EVENT_ID = "TestConsentEventId";
    public static final String TEST_CONSENT_EVENT_TIME = "2021-01-01T00:00:00Z";
    public static final String TEST_CONSENT_STATUS = "ACTIVE";
    public static final String TEST_CONSENT_TYPE = "TestConsentType";
    public static final Map<String, String> TEST_CONSENT_DATA = Map.of(
        "testKey1", "testValue1",
        "testKey2", "testValue2"
    );

    public static final StoredConsentImage TEST_STORED_CONSENT = new StoredConsentImage()
        .id(TEST_CONSENT_PARTITION_KEY)
        .serviceId(TEST_SERVICE_ID)
        .userId(TEST_USER_ID)
        .consentId(TEST_CONSENT_ID)
        .consentVersion(1)
        .consentStatus(TEST_CONSENT_STATUS)
        .consentType(TEST_CONSENT_TYPE)
        .consentData(TEST_CONSENT_DATA);

    public static final DynamoDbConsentHistory TEST_CONSENT_HISTORY_INSERT_RECORD = DynamoDbConsentHistory.builder()
        .id(TEST_CONSENT_PARTITION_KEY)
        .eventId(TEST_CONSENT_EVENT_ID)
        .eventType(DynamoDbStreamEventType.INSERT.getValue())
        .eventTime(TEST_CONSENT_EVENT_TIME)
        .serviceUserId(TEST_SERVICE_USER_ID)
        .newImage(TEST_STORED_CONSENT)
        .build();

    public static final Map<String, com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue>
        TEST_LAMBDA_EVENT_CONSENT_WITH_ALL_ATTRIBUTES = Map.of(
            ConsentTableAttributeName.ID.getValue(), new AttributeValue().withS(TestConstants.TEST_CONSENT_PARTITION_KEY),
            ConsentTableAttributeName.SERVICE_ID.getValue(), new AttributeValue().withS(TestConstants.TEST_SERVICE_ID),
            ConsentTableAttributeName.USER_ID.getValue(), new AttributeValue().withS(TestConstants.TEST_USER_ID),
            ConsentTableAttributeName.CONSENT_ID.getValue(), new AttributeValue().withS(TestConstants.TEST_CONSENT_ID),
            ConsentTableAttributeName.CONSENT_VERSION.getValue(), new AttributeValue().withN(TestConstants.TEST_CONSENT_VERSION),
            ConsentTableAttributeName.CONSENT_STATUS.getValue(), new AttributeValue().withS(TestConstants.TEST_CONSENT_STATUS),
            ConsentTableAttributeName.CONSENT_TYPE.getValue(), new AttributeValue().withS(TestConstants.TEST_CONSENT_TYPE),
            ConsentTableAttributeName.CONSENT_DATA.getValue(), new AttributeValue().withM(Map.of(
                "testKey1", new AttributeValue().withS("testValue1"),
                "testKey2", new AttributeValue().withS("testValue2")
            )),
            ConsentTableAttributeName.EXPIRY_TIME.getValue(), new AttributeValue().withS(TestConstants.TEST_CONSENT_EVENT_TIME)
        );
}
