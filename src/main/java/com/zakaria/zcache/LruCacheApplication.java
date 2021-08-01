package com.zakaria.zcache;

import com.zakaria.zcache.processors.ProcessNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@EnableDiscoveryClient
@SpringBootApplication
public class LruCacheApplication implements CommandLineRunner {
	
	@Autowired
	ProcessNode processNode;

	public static void main(String[] args) {
		SpringApplication.run(LruCacheApplication.class, args);
	}
	
	@Override
	public void run(String...args) throws Exception {
		
		final ExecutorService service = Executors.newSingleThreadExecutor();
		
		final Future<?> status = service.submit(processNode);
		
		try {
			status.get();
		} catch (InterruptedException | ExecutionException e) {
			log.error(e.getMessage(), e);
			service.shutdown();
		}
	}

}
