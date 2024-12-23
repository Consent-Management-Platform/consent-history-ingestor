package com.consentframework.consenthistory.consentingestor.testcommon.constants;

/**
 * Utility class defining common test constants.
 */
public final class TestConstants {
    public static final String TEST_CONSENT_ID = "TestConsentId";
    public static final String TEST_SERVICE_ID = "TestServiceId";
    public static final String TEST_USER_ID = "TestUserId";
    public static final String TEST_CONSENT_PARTITION_KEY = String.format("%s|%s|%s", TEST_SERVICE_ID, TEST_USER_ID, TEST_CONSENT_ID);
    public static final String TEST_CONSENT_EVENT_ID = "TestConsentEventId";
}
