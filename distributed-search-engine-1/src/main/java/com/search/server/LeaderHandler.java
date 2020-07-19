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

@Component
public class LeaderHandler {
	
	   @Autowired
	   Environment env;
	
	@Autowired
	Environment environment;
	
	@Autowired
	CuratorFramework client;

    @EventListener(OnGrantedEvent.class)
    public void start() throws UnknownHostException {
    	//System.out.println("port: mmmm" + env.getProperty("server.port"));
    	try {
    		if(client.checkExists().forPath("/leader") != null) {
    			client.setData().forPath("/leader",env.getProperty("server.port").getBytes());
    			return;
    		}
    		String result = client.create().forPath("/leader");
    		if(result != null) {
    			client.setData().forPath("/leader",env.getProperty("server.port").getBytes());

    		}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("start");
    }

    @EventListener(OnRevokedEvent.class)
    public void stop() {
    	System.out.println("port: revoke" + env.getProperty("server.port"));
    	try {
    		final String result = client.create().forPath("/follower");
			client.setData().forPath("/follower",env.getProperty("server.port").getBytes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("stop");
    }

}
