package com.javaperks.api.api;

import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.health.HealthCheck.Result;
import com.codahale.metrics.health.HealthCheckRegistry;

@Produces(MediaType.APPLICATION_JSON)
@Path("/_health_check")
public class HealthCheckInterface {
    private HealthCheckRegistry registry;

    public HealthCheckInterface(HealthCheckRegistry registry) {
        this.registry = registry;
    }

    @GET
    public Set<Entry<String, Result>> getStatus(){
        return registry.runHealthChecks().entrySet();
    }
}
