package com.colak.springr2dbctutorial.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Use @SpringJUnitConfig to load only one Spring bean which is SampleService
 */
@SpringJUnitConfig(classes = SampleService.class)
class SampleServiceTest {
    @Autowired
    private SampleService sampleService;

    @Test
    void testUppercaseListReactive() {
        List<String> names = List.of("John", "Doe");
        Mono<List<String>> processedNamesMono = sampleService.uppercaseListReactive(names);

        StepVerifier.create(processedNamesMono)
                .consumeNextWith(processedNames -> {
                    assertNotNull(processedNames);
                    assertEquals(2, processedNames.size());
                    assertEquals("JOHN", processedNames.get(0));
                })
                .verifyComplete();
    }

    @Test
    void testUppercaseMapValueReactive() {
        Map<String, String> keyValueMap = Map.of("key1", "value1", "key2", "value2");
        Mono<Map<String, String>> processedMapMono = sampleService.uppercaseMapValueReactive(keyValueMap);

        StepVerifier.create(processedMapMono)
                .consumeNextWith(processedMap -> {
                    assertNotNull(processedMap);
                    assertEquals(2, processedMap.size());
                    assertEquals("VALUE1", processedMap.get("key1"));
                })
                .verifyComplete();
    }

    @Test
    void testMultiplyArrayReactive() {
        Integer[] numbers = new Integer[]{1, 2, 3};
        Mono<int[]> processedArrayMono = sampleService.multiplyArrayReactive(numbers);

        StepVerifier.create(processedArrayMono)
                .consumeNextWith(processedArray -> {
                    assertNotNull(processedArray);
                    assertEquals(3, processedArray.length);
                    assertEquals(2, processedArray[0]);
                })
                .verifyComplete();
    }

    @ParameterizedTest
    @CsvSource({"Hello, World, Hello World", "JUnit, Testing, JUnit Testing"})
    void testConcatenateStringsReactive(String str1, String str2, String expected) {
        Mono<String> concatenatedStringMono = sampleService.concatenateStringsReactive(str1, str2);

        StepVerifier.create(concatenatedStringMono)
                .expectNext(expected)
                .verifyComplete();
    }

}
