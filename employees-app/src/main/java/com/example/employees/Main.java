/* Copyright 2015 Oracle and/or its affiliates. All rights reserved. */
package com.example.employees;

import java.util.Optional;
import java.util.Properties;

import org.apache.catalina.startup.Tomcat;

public class Main {
    
    public static final Optional<String> PORT = Optional.ofNullable(System.getenv("PORT"));
    public static final Optional<String> HOSTNAME = Optional.ofNullable(System.getenv("HOSTNAME"));
    
    public static void main(String[] args) throws Exception {
    		//System.setProperty("org.apache.logging.log4j.simplelog.StatusLogger.level", "TRACE");
    	
    	
        String contextPath = "/" ;
        String appBase = ".";
        Tomcat tomcat = new Tomcat();   
        String port = "8080";
        if(args.length>0)
        {
        		port = args[0];
        }
        tomcat.setPort(Integer.valueOf(PORT.orElse(port) ));
        tomcat.setHostname(HOSTNAME.orElse("localhost"));
        tomcat.getHost().setAppBase(appBase);
        tomcat.addWebapp(contextPath, appBase);
        tomcat.start();
        tomcat.getServer().await();
    }
}