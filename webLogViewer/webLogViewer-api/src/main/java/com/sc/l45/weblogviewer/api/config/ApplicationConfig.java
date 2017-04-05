package com.sc.l45.weblogviewer.api.config;

import java.util.Set;

import javax.ws.rs.core.Application;

@javax.ws.rs.ApplicationPath("/api")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.sc.l45.weblogviewer.api.config.CORSFilter.class);
        resources.add(com.sc.l45.weblogviewer.api.RestApi.class);
    }

}
