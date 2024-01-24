package com.colak.springr2dbctutorial.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * This is a simple processing service. It has no dependency to a repository
 */
@Service
public class SampleService {

    public Mono<List<String>> uppercaseListReactive(List<String> names) {
        return Flux.fromIterable(names)
                .map(String::toUpperCase)
                .collectList();
    }

    public Mono<Map<String, String>> uppercaseMapValueReactive(Map<String, String> keyValueMap) {
        return Flux.fromIterable(keyValueMap.entrySet())
                .collectMap(Map.Entry::getKey, entry -> entry.getValue().toUpperCase());
    }

    public Mono<int[]> multiplyArrayReactive(Integer[] numbers) {
        return Flux.fromArray(numbers)
                .map(num -> num * 2)
                .collectList()
                .map(list -> list.stream().mapToInt(Integer::intValue).toArray());
    }

    public Mono<String> concatenateStringsReactive(String str1, String str2) {
        return Mono.just(str1 + " " + str2);
    }
}
