package com.consentframework.consenthistory.consentingestor.testcommon.constants;

import com.consentframework.consenthistory.consentingestor.domain.constants.ConsentTableAttributeName;
import com.consentframework.consenthistory.consentingestor.domain.constants.DynamoDbStreamEventType;
import com.consentframework.consenthistory.consentingestor.infrastructure.entities.DynamoDbConsentHistory;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

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

    public static final Map<String, AttributeValue> TEST_CONSENT_RECORD = Map.of(
        ConsentTableAttributeName.ID.getValue(), AttributeValue.builder().s(TEST_CONSENT_PARTITION_KEY).build(),
        ConsentTableAttributeName.CONSENT_VERSION.getValue(), AttributeValue.builder().n(TEST_CONSENT_VERSION).build(),
        ConsentTableAttributeName.CONSENT_ID.getValue(), AttributeValue.builder().s(TEST_CONSENT_ID).build(),
        ConsentTableAttributeName.SERVICE_ID.getValue(), AttributeValue.builder().s(TEST_SERVICE_ID).build(),
        ConsentTableAttributeName.USER_ID.getValue(), AttributeValue.builder().s(TEST_USER_ID).build(),
        ConsentTableAttributeName.CONSENT_STATUS.getValue(), AttributeValue.builder().s("ACTIVE").build(),
        ConsentTableAttributeName.CONSENT_DATA.getValue(), AttributeValue.builder().m(
                Map.of("testAttribute", AttributeValue.builder().s("testAttributeValue").build())
            ).build()
    );

    public static final DynamoDbConsentHistory TEST_CONSENT_HISTORY_INSERT_RECORD = DynamoDbConsentHistory.builder()
        .id(TEST_CONSENT_PARTITION_KEY)
        .eventId(TEST_CONSENT_EVENT_ID)
        .eventType(DynamoDbStreamEventType.INSERT.getValue())
        .eventTime(TEST_CONSENT_EVENT_TIME)
        .serviceUserId(TEST_SERVICE_USER_ID)
        .newImage(TEST_CONSENT_RECORD)
        .build();
}
