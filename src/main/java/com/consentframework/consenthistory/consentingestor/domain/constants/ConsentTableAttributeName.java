package com.consentframework.consenthistory.consentingestor.domain.constants;

/**
 * Consent table attribute names.
 */
public enum ConsentTableAttributeName {
    ID("id"),
    CONSENT_VERSION("consentVersion"),
    CONSENT_DATA("consentData");

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
