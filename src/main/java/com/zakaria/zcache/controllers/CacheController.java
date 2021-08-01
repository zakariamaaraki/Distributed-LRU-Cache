package com.zakaria.zcache.controllers;

import com.zakaria.zcache.models.Cache;
import com.zakaria.zcache.models.Pair;
import com.zakaria.zcache.services.CacheService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/cache")
public class CacheController {
	
	@Autowired
	CacheService cacheService;
	
	@Autowired
	Cache<String, String> cache;
	
	@ApiOperation(
			value = "Put Method",
			notes = "Provide a Key/Value pair",
			response = ResponseEntity.class
	)
	@RequestMapping(
			method = RequestMethod.PUT,
			path = "api/put"
	)
	public ResponseEntity<Pair<String, String>> set(@RequestBody(required = true) Pair<String, String> pair){
		log.info("Set " + pair);
		return new ResponseEntity<>(cacheService.set(pair), HttpStatus.CREATED);
	}
	
	@RequestMapping(
			method = RequestMethod.PUT,
			path = "sync-set"
	)
	public synchronized ResponseEntity<String> syncSet(@RequestBody(required = true) Pair<String, String> pair){
		log.info("Set szynchronization signal " + pair);
		return new ResponseEntity<>(cache.set(pair.getKey(), pair.getValue()), HttpStatus.CREATED);
	}
	
	@RequestMapping(
			method = RequestMethod.PUT,
			path = "broadcast-set"
	)
	public ResponseEntity<Pair<String, String>> broadcastSet(@RequestBody(required = true) Pair<String, String> pair){
		
		log.info("Broadcast Set : " + pair);
		
		if(cacheService.broadcastedSetMessage(pair))
			return new ResponseEntity<>(pair, HttpStatus.OK);
		else
			return new ResponseEntity<>(HttpStatus.LOCKED);
	}
	
	@ApiOperation(
			value = "Delete Method",
			notes = "Provide the Key",
			response = ResponseEntity.class
	)
	@RequestMapping(
			method = RequestMethod.DELETE,
			path = "api/delete"
	)
	public ResponseEntity<String> remove(@RequestBody(required = true) String key){
		log.info("Remove " + key);
		cacheService.remove(key);
		return new ResponseEntity<>(key, HttpStatus.OK);
	}
	
	@RequestMapping(
			method = RequestMethod.DELETE,
			path = "sync-remove"
	)
	public synchronized ResponseEntity<String> syncRemove(@RequestBody(required = true) String key){
		log.info("Synchronization signal remove key = " + key);
		cache.remove(key);
		return new ResponseEntity<>(key, HttpStatus.OK);
	}
	
	@RequestMapping(
			method = RequestMethod.DELETE,
			path = "broadcast-remove"
	)
	public ResponseEntity<String> broadcastRemove(@RequestBody(required = true) String key){
		
		log.info("Broadcast remove key = " + key);
		
		if(cacheService.broadcastedRemoveMessage(key))
			return new ResponseEntity<>(key, HttpStatus.OK);
		else
			return new ResponseEntity<>(HttpStatus.LOCKED);
	}
	
	@ApiOperation(
			value = "Get Method",
			notes = "Provide the Key",
			response = ResponseEntity.class
	)
	@RequestMapping(
			method = RequestMethod.GET,
			path = "api/get"
	)
	public synchronized ResponseEntity<String> get(@RequestParam(value = "key", required = true) String key) {
		return new ResponseEntity<>(cacheService.get(key), HttpStatus.OK);
	}
	

}
