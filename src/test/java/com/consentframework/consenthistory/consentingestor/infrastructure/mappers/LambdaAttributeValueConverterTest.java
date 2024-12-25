package com.consentframework.consenthistory.consentingestor.infrastructure.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.consentframework.consenthistory.consentingestor.domain.constants.ConsentTableAttributeName;
import com.consentframework.consenthistory.consentingestor.testcommon.constants.TestConstants;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class LambdaAttributeValueConverterTest {
    @Test
    void testConvertNull() {
        assertNull(LambdaAttributeValueConverter.toDynamoDbAttributeValueMap(null));
    }

    @Test
    void testConvertEmptyAttributeMap() {
        assertEquals(Map.of(), LambdaAttributeValueConverter.toDynamoDbAttributeValueMap(Map.of()));
    }

    @Test
    void testConvertEmptyAttribute() {
        final String attributeName = "test";
        final Map<String, AttributeValue> lambdaAttributeMap = Map.of(attributeName, new AttributeValue());
        final software.amazon.awssdk.services.dynamodb.model.AttributeValue emptyDdbAttribute =
            software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder().build();
        assertEquals(Map.of(attributeName, emptyDdbAttribute),
            LambdaAttributeValueConverter.toDynamoDbAttributeValueMap(lambdaAttributeMap));
    }

    @Test
    void testConvertAttributeValueMap() {
        final String stringAttributeName = "testStringAttribute";
        final String stringAttributeValue = "test";
        final String numberAttributeName = "testNumberAttribute";
        final String numberAttributeValue = "5";
        final String booleanAttributeName = "testBooleanAttribute";
        final boolean booleanAttributeValue = true;
        final String bytesAttributeName = "testBytesAttribute";
        final ByteBuffer bytesAttributeValue = ByteBuffer.wrap("test".getBytes());
        final String nullAttributeName = "testNullAttribute";
        final boolean nullAttributeValue = true;
        final String listAttributeName = "testListAttribute";
        final List<String> listAttributeStringValues = List.of("testListVal1", "testListVal2");
        final List<AttributeValue> listAttributeValue = listAttributeStringValues.stream()
            .map(strVal -> new AttributeValue().withS(strVal))
            .toList();
        final String strListAttributeName = "testStrListAttribute";
        final String numListAttributeName = "testNumListAttribute";
        final List<String> numListAttributeStringValues = List.of("1", "2");
        final String bytesListAttributeName = "testListBytesAttribute";
        final List<ByteBuffer> bytesListAttributeValues = List.of(ByteBuffer.wrap("test1".getBytes()), ByteBuffer.wrap("test2".getBytes()));

        final Map<String, AttributeValue> lambdaAttributeMap = Map.of(
            ConsentTableAttributeName.ID.getValue(), new AttributeValue().withS(TestConstants.TEST_CONSENT_PARTITION_KEY),
            ConsentTableAttributeName.CONSENT_VERSION.getValue(), new AttributeValue().withN(TestConstants.TEST_CONSENT_VERSION),
            ConsentTableAttributeName.CONSENT_DATA.getValue(), new AttributeValue().withM(Map.of(
                stringAttributeName, new AttributeValue().withS(stringAttributeValue),
                booleanAttributeName, new AttributeValue().withBOOL(booleanAttributeValue),
                numberAttributeName, new AttributeValue().withN(numberAttributeValue)
            )),
            bytesAttributeName, new AttributeValue().withB(bytesAttributeValue),
            nullAttributeName, new AttributeValue().withNULL(nullAttributeValue),
            listAttributeName, new AttributeValue().withL(listAttributeValue),
            strListAttributeName, new AttributeValue().withSS(listAttributeStringValues),
            numListAttributeName, new AttributeValue().withNS(numListAttributeStringValues),
            bytesListAttributeName, new AttributeValue().withBS(bytesListAttributeValues)
        );

        final Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> dynamoDbAttributeMap =
            LambdaAttributeValueConverter.toDynamoDbAttributeValueMap(lambdaAttributeMap);

        final Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> consentData =
            getAttribute(dynamoDbAttributeMap, ConsentTableAttributeName.CONSENT_DATA).m();
        assertEquals(TestConstants.TEST_CONSENT_PARTITION_KEY, getAttribute(dynamoDbAttributeMap, ConsentTableAttributeName.ID).s());
        assertEquals(TestConstants.TEST_CONSENT_VERSION,
            getAttribute(dynamoDbAttributeMap, ConsentTableAttributeName.CONSENT_VERSION).n());
        assertEquals(stringAttributeValue, consentData.get(stringAttributeName).s());
        assertEquals(booleanAttributeValue, consentData.get(booleanAttributeName).bool());
        assertEquals(numberAttributeValue, consentData.get(numberAttributeName).n());
        assertEquals(bytesAttributeValue, dynamoDbAttributeMap.get(bytesAttributeName).b().asByteBuffer());
        assertEquals(nullAttributeValue, dynamoDbAttributeMap.get(nullAttributeName).nul());

        assertEquals(listAttributeStringValues, dynamoDbAttributeMap.get(listAttributeName).l().stream()
            .map(ddbListAttributeValue -> ddbListAttributeValue.s())
            .collect(Collectors.toList()));
        assertEquals(listAttributeStringValues, dynamoDbAttributeMap.get(strListAttributeName).ss());
        assertEquals(numListAttributeStringValues, dynamoDbAttributeMap.get(numListAttributeName).ns());
        assertEquals(bytesListAttributeValues, dynamoDbAttributeMap.get(bytesListAttributeName).bs().stream()
            .map(buffer -> buffer.asByteBuffer())
            .collect(Collectors.toList()));
    }

    private software.amazon.awssdk.services.dynamodb.model.AttributeValue getAttribute(
            final Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> ddbAttributes,
            final ConsentTableAttributeName attributeName) {
        return ddbAttributes.get(attributeName.getValue());
    }
}
