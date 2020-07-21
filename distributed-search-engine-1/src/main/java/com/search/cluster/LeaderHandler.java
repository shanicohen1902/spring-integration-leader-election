package com.search.server;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.integration.leader.event.OnGrantedEvent;
import org.springframework.integration.leader.event.OnRevokedEvent;
import org.springframework.stereotype.Component;
import org.apache.zookeeper.CreateMode;

@Component
public class LeaderHandler {
	
   @Autowired
   Environment env;
	
	@Autowired
	Environment environment;
	
	@Autowired
	CuratorFramework client;

	String LEADER_REGISTRY = "/leader";

    @EventListener(OnGrantedEvent.class)
    public void start() throws UnknownHostException {

    	try {
    		String address = String.format("http://%s:%s", InetAddress.getLocalHost().getCanonicalHostName(), env.getProperty("server.port"));
    		if(client.checkExists().forPath(LEADER_REGISTRY) == null) {
				String result = client.create().withMode(CreateMode.EPHEMERAL).forPath(LEADER_REGISTRY);
    		}
			client.setData().forPath(LEADER_REGISTRY,address.getBytes());
		} catch (Exception e) {
			System.out.println("Error when create leader znode: " + e.getMessage());
		}
    }

    @EventListener(OnRevokedEvent.class)
    public void stop() {
    	try {
			String address = String.format("http://%s:%s", InetAddress.getLocalHost().getCanonicalHostName(), env.getProperty("server.port"));
			String node = "/followers/follower"+ "_" + env.getProperty("server.port");
    		final String result = client.create().withMode(CreateMode.EPHEMERAL).forPath("/follower"+ "_" + env.getProperty("server.port"));
			if(client.checkExists().forPath(node) != null) {
				client.setData().forPath(node, address.getBytes());
			}
			client.setData().forPath(node,address.getBytes());
		} catch (Exception e) {
			System.out.println("Error when create follower znode: " + e.getStackTrace());
		}
    }

}
