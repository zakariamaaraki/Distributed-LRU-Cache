package com.zakaria.zcache.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Pair<K, V> {
	
	private K key;
	
	private V value;
}
