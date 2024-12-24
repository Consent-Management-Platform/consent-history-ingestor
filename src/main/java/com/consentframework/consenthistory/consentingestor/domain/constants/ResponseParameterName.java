package com.consentframework.consenthistory.consentingestor.domain.constants;

/**
 * Response body parameter names.
 */
public enum ResponseParameterName {
    BODY("body"),
    STATUS_CODE("statusCode");

    private final String value;

    private ResponseParameterName(final String value) {
        this.value = value;
    }

    /**
     * Return parameter name.
     *
     * @return parameter name
     */
    public String getValue() {
        return value;
    }
}
