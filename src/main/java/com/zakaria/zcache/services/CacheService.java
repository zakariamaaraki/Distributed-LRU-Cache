package com.zakaria.zcache.services;

import com.zakaria.zcache.models.Cache;
import com.zakaria.zcache.models.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class CacheService {
	
	@Autowired
	private Cache<String,String> cache;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private DiscoveryClient discoveryClient;
	
	@Value("${spring.application.name}")
	private String applicationName;
	
	@Value("${spring.cloud.zookeeper.discovery.instance-id}")
	private int nodeId;
	
	public Pair<String, String> set(Pair<String, String> pair){
		
		if(cache.isLeader) {
			log.info("I'm the leader ...");
			cache.set(pair.getKey(), pair.getValue());
			syncSet(pair);
		} else {
			broadcastSet(pair);
		}
		return pair;
	}
	
	public boolean broadcastedSetMessage(Pair<String, String> pair){
		
		if(cache.isLeader) {
			cache.set(pair.getKey(), pair.getValue());
			syncSet(pair);
			return true;
		}
		
		return false;
	}
	
	public boolean broadcastedRemoveMessage(String key){
		
		if(cache.isLeader) {
			cache.remove(key);
			syncRemove(key);
			return true;
		}
		
		return false;
	}
	
	private void syncSet(Pair<String, String> pair) {
		
		var list = getInstances();
		
		if (list != null && list.size() > 0 ) {
			
			for(ServiceInstance node : list) {
				
				if(Integer.parseInt(node.getInstanceId()) == nodeId)
					continue;
				
				log.info("Set synchronization with : " + node.getHost());
				
				try {
					restTemplate.put(node.getUri().toString() + "/cache/sync-set", pair);
				} catch(RestClientException restClientException) {
					log.warn("Error : ", restClientException);
				}
				
			}
		}
	}
	
	private void broadcastSet(Pair<String, String> pair) {
		
		var list = getInstances();
		
		if (list != null && list.size() > 0 ) {
			
			for(ServiceInstance node : list) {
				
				if(Integer.parseInt(node.getInstanceId()) == nodeId)
					continue;
				
				restTemplate.put(node.getUri().toString() + "/cache/broadcast-set", pair);
			}
		}
	}
	
	public synchronized String get(String key){
		return cache.get(key);
	}
	
	public void remove(String key) {
		
		if(cache.isLeader) {
			log.info("I'm the leader ...");
			cache.remove(key);
			syncRemove(key);
		} else {
			broadcastRemove(key);
		}
	}
	
	private void broadcastRemove(String key) {
		
		var list = getInstances();
		
		if (list != null && list.size() > 0 ) {
			
			for(ServiceInstance node : list) {
				
				if(Integer.parseInt(node.getInstanceId()) == nodeId)
					continue;
				
				restTemplate.delete(node.getUri().toString() + "/cache/broadcast-remove", key);
			}
		}
	}
	
	private void syncRemove(String key) {
		
		var list = getInstances();
		
		if (list != null && list.size() > 0 ) {
			
			for(ServiceInstance node : list) {
				
				if(Integer.parseInt(node.getInstanceId()) == nodeId)
					continue;
				
				log.info("Remove synchronization with : " + node.getHost());
				restTemplate.delete(node.getUri().toString() + "/cache/sync-remove", key);
			}
		}
	}
	
	public List<ServiceInstance> getInstances() {
		return discoveryClient.getInstances(applicationName);
	}
	
}
