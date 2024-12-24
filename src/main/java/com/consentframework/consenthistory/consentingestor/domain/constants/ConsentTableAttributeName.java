package com.consentframework.consenthistory.consentingestor.domain.constants;

/**
 * Consent table attribute names.
 */
public enum ConsentTableAttributeName {
    ID("id"),
    SERVICE_ID("serviceId"),
    USER_ID("userId"),
    CONSENT_ID("consentId"),
    CONSENT_DATA("consentData"),
    CONSENT_VERSION("consentVersion"),
    CONSENT_STATUS("consentStatus");

    private final String value;

    private ConsentTableAttributeName(final String value) {
        this.value = value;
    }

    /**
     * Return attribute name.
     *
     * @return attribute name
     */
    public String getValue() {
        return value;
    }
}
