package com.zakaria.zcache.processors;

import com.zakaria.zcache.models.Cache;
import com.zakaria.zcache.services.ZooKeeperService;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class ProcessNode implements Runnable{
	
	private static final String LEADER_ELECTION_ROOT_NODE = "/cache-election";
	private static final String PROCESS_NODE_PREFIX = "/p_";
	
	private String processNodePath;
	private String watchedNodePath;
	
	@Value("${spring.cloud.zookeeper.discovery.instance-id}")
	private int nodeId;
	
	@Autowired
	Cache<String, String> cache;
	
	@Autowired
	ZooKeeperService zooKeeperService;
	
	private void attemptForLeaderPosition() {
		
		final List<String> childNodePaths = zooKeeperService.getChildren(LEADER_ELECTION_ROOT_NODE, false);
		
		Collections.sort(childNodePaths);
		
		int index = childNodePaths.indexOf(processNodePath.substring(processNodePath.lastIndexOf('/') + 1));
		if(index == 0) {
			log.info("[Process: " + nodeId + "] I am the new leader!");
			cache.isLeader = true;
		} else {
			final String watchedNodeShortPath = childNodePaths.get(index - 1);
			cache.isLeader = false;
			
			watchedNodePath = LEADER_ELECTION_ROOT_NODE + "/" + watchedNodeShortPath;
			
			log.info("[Process: " + nodeId + "] - Setting watch on node with path: " + watchedNodePath);
			zooKeeperService.watchNode(watchedNodePath, true);
		}
	}
	
	@Override
	public void run() {
		
		log.info("Process with id: " + nodeId + " has started!");
		
		final String rootNodePath = zooKeeperService.createNode(LEADER_ELECTION_ROOT_NODE, false, false);
		if(rootNodePath == null) {
			throw new IllegalStateException("Unable to create/access leader election root node with path: " + LEADER_ELECTION_ROOT_NODE);
		}
		
		processNodePath = zooKeeperService.createNode(rootNodePath + PROCESS_NODE_PREFIX, false, true);
		if(processNodePath == null) {
			throw new IllegalStateException("Unable to create/access process node with path: " + LEADER_ELECTION_ROOT_NODE);
		}
		
		log.debug("[Process: " + nodeId + "] Process node created with path: " + processNodePath);
		
		attemptForLeaderPosition();
	}
	
	@Component
	public class ProcessNodeWatcher implements Watcher{
		
		@Value("${spring.cloud.zookeeper.discovery.instance-id}")
		private int nodeId;
		
		@Override
		public void process(WatchedEvent event) {
			
			log.debug("[Process: " + nodeId + "] Event received: " + event);
			
			final Event.EventType eventType = event.getType();
			if(Watcher.Event.EventType.NodeDeleted.equals(eventType)) {
				if(event.getPath().equalsIgnoreCase(watchedNodePath)) {
					attemptForLeaderPosition();
				}
			}
			
		}
		
	}
	
}
