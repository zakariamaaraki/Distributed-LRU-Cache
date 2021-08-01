package com.zakaria.zcache.models;

import java.util.LinkedHashMap;
import java.util.Map;

public class Cache<K,V> {
	
	public boolean isLeader;
	
	public Long startUpTime;
	
	private Map<K,V> map;
	
	public Cache(int capacity) {
		
		this.startUpTime = System.currentTimeMillis();
		
		map = new LinkedHashMap(capacity, 0.75f, true) {
			protected boolean removeEldestEntry(Map.Entry eldest) {
				return size() > capacity;
			}
		};
	}
	
	public V get(K key) {
		return map.get(key);
	}
	
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}
	
	public V remove(K key) {
		return map.remove(key);
	}
	
	public V set(K key, V value) {
		return map.put(key,value);
	}
	
	public String toString() {
		return map.toString();
	}
	
}
