package com.zakaria.zcache;

import com.zakaria.zcache.models.Cache;
import com.zakaria.zcache.models.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LruCacheApplicationTests {
	
	@Test
	public void givenPairOfKeyValue_WhenCallingSetMethod_ThenKeyValueAddedToTheCacheAndGetMethodReturnTheValue() {
		
		Cache<String, String> cache = new Cache<>(100);
		
		// Given
		Pair<String, String> pair = Pair.<String, String>builder()
				.key("key")
				.value("value")
				.build();
		
		// When
		cache.set(pair.getKey(), pair.getValue());
		
		// Then
		Assertions.assertTrue(cache.containsKey(pair.getKey()));
		Assertions.assertEquals(pair.getValue(), cache.get(pair.getKey()));
	}
	
	@Test
	public void givenPairOfKeyValue_WhenCallingContainsKey_ThenTheMethodShouldReturnTrue() {
		
		Cache<String, String> cache = new Cache<>(100);
		
		// Given
		Pair<String, String> pair = Pair.<String, String>builder()
				.key("key")
				.value("value")
				.build();
		
		cache.set(pair.getKey(), pair.getValue());
		Assertions.assertTrue(cache.containsKey(pair.getKey()));
		
		// When
		boolean isPairPresent = cache.containsKey(pair.getKey());
		
		// Then
		Assertions.assertTrue(isPairPresent);
	}
	
	@Test
	public void givenAnEmptyCache_WhenCallingContainsKey_ThenTheMethodShouldReturnFalse() {
		
		// Given
		Cache<String, String> cache = new Cache<>(100);
		Pair<String, String> pair = Pair.<String, String>builder()
				.key("key")
				.value("value")
				.build();
		
		// When
		boolean isPairPresent = cache.containsKey(pair.getKey());
		
		// Then
		Assertions.assertFalse(isPairPresent);
	}
	
	@Test
	public void givenPairOfKeyValue_WhenCallingRemoveMethod_ThenKeyValueAreRemovedFromTheCacheAndGetMethodReturnNullValue() {
		
		Cache<String, String> cache = new Cache<>(100);
		
		// Given
		Pair<String, String> pair = Pair.<String, String>builder()
				.key("key")
				.value("value")
				.build();
		
		cache.set(pair.getKey(), pair.getValue());
		Assertions.assertTrue(cache.containsKey(pair.getKey()));
		
		// When
		cache.remove(pair.getKey());
		
		// Then
		Assertions.assertFalse(cache.containsKey(pair.getKey()));
		Assertions.assertEquals(null, cache.get(pair.getKey()));
	}

}
