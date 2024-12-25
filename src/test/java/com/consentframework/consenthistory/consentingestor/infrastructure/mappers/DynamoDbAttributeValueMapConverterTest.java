package com.consentframework.consenthistory.consentingestor.infrastructure.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.document.EnhancedDocument;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

class DynamoDbAttributeValueMapConverterTest {
    private DynamoDbAttributeValueMapConverter converter;

    private final String strAttributeName = "testStringAttribute";
    private final String strAttributeValue = "testStringValue";
    private final String numAttributeName = "testNumberAttribute";
    private final String numAttributeValue = "123";
    private final String boolAttributeName = "testBooleanAttribute";
    private final boolean boolAttributeValue = true;

    @BeforeEach
    void setUp() {
        converter = new DynamoDbAttributeValueMapConverter();
    }

    @Test
    void testTransformFrom() {
        final Map<String, AttributeValue> attributeMap = Map.of(
            strAttributeName, AttributeValue.builder().s(strAttributeValue).build(),
            numAttributeName, AttributeValue.builder().n(numAttributeValue).build(),
            boolAttributeName, AttributeValue.builder().bool(boolAttributeValue).build()
        );

        final AttributeValue result = converter.transformFrom(attributeMap);
        assertNotNull(result);
        assertNotNull(result.s());
        final EnhancedDocument document = EnhancedDocument.fromJson(result.s());
        assertEquals(strAttributeValue, document.getString(strAttributeName));
        assertEquals(numAttributeValue, document.getString(numAttributeName));
        assertTrue(document.getBoolean(boolAttributeName));
    }

    @Test
    void testTransformTo() {
        final String json = "{\"" + strAttributeName + "\":\"" + strAttributeValue + "\","
            + "\"" + numAttributeName + "\":" + numAttributeValue + ","
            + "\"" + boolAttributeName + "\":true}";
        final AttributeValue attributeValue = AttributeValue.builder().s(json).build();

        final Map<String, AttributeValue> result = converter.transformTo(attributeValue);
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(strAttributeValue, result.get(strAttributeName).s());
        assertEquals(numAttributeValue, result.get(numAttributeName).n());
        assertTrue(result.get(boolAttributeName).bool());
    }

    @Test
    void testType() {
        final EnhancedType<Map<String, AttributeValue>> result = converter.type();
        assertNotNull(result);
        assertEquals(EnhancedType.mapOf(String.class, AttributeValue.class), result);
    }

    @Test
    void testAttributeValueType() {
        final AttributeValueType result = converter.attributeValueType();
        assertNotNull(result);
        assertEquals(AttributeValueType.S, result);
    }
}
