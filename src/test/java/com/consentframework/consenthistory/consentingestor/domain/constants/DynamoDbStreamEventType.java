package com.consentframework.consenthistory.consentingestor.domain.constants;

/**
 * DynamoDB stream event types.
 */
public enum DynamoDbStreamEventType {
    INSERT("INSERT"),
    MODIFY("MODIFY"),
    REMOVE("REMOVE");

    private final String value;

    private DynamoDbStreamEventType(final String value) {
        this.value = value;
    }

    /**
     * Return the event type string value.
     *
     * @return event type
     */
    public String getValue() {
        return value;
    }
}
