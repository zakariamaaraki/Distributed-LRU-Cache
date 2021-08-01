package com.zakaria.zcache.controllers;

import com.zakaria.zcache.services.HealthService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/health")
public class HealthCheckController {
	
	@Autowired
	private HealthService healthService;
	
	@ApiOperation(
			value = "Get nodes",
			response = ResponseEntity.class
	)
	@RequestMapping(
			method = RequestMethod.GET,
			path = "instances"
	)
	public List<Map<String, String>> get(){
		return healthService.getInstances();
	}
	
	@ApiOperation(
			value = "Get actual instance role (LEADER or NOT)",
			response = ResponseEntity.class
	)
	@RequestMapping(
			method = RequestMethod.GET,
			path = "role"
	)
	public String getRole(){
		return healthService.getRole();
	}
	
	@ApiOperation(
			value = "Get actual instance starting time",
			response = ResponseEntity.class
	)
	@RequestMapping(
			method = RequestMethod.GET,
			path = "start-time"
	)
	public String getStartTime(){
		return healthService.getStartUpTime();
	}
	
}
