package com.smartcampus;

import com.smartcampus.filter.AuthFilter;
import com.smartcampus.filter.LoggingFilter;
import com.smartcampus.mapper.GlobalExceptionMapper;
import com.smartcampus.mapper.LinkedResourceNotFoundMapper;
import com.smartcampus.mapper.RoomNotEmptyMapper;
import com.smartcampus.mapper.SensorUnavailableMapper;
import com.smartcampus.resource.DiscoveryResource;
import com.smartcampus.resource.RoomResource;
import com.smartcampus.resource.SensorResource;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

// @ApplicationPath sets the base URL prefix for all resources
// Combined with Tomcat context path, full URL becomes:
// http://localhost:8080/smart-campus-api/api/v1/...
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {

    // Override getClasses() to explicitly register all JAX-RS components
    // This replaces the ResourceConfig setup that was previously in Main.java
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        // Register resource classes (endpoints)
        classes.add(DiscoveryResource.class);
        classes.add(RoomResource.class);
        classes.add(SensorResource.class);
        // Note: SensorReadingResource is NOT registered here because
        // it is a sub-resource — created by SensorResource's locator method

        // Register exception mappers
        classes.add(RoomNotEmptyMapper.class);
        classes.add(LinkedResourceNotFoundMapper.class);
        classes.add(SensorUnavailableMapper.class);
        classes.add(GlobalExceptionMapper.class);

        // Register filters
        classes.add(AuthFilter.class);
        classes.add(LoggingFilter.class);

        return classes;
    }
}
