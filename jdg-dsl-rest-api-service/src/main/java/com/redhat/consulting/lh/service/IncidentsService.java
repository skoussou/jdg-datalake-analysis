package com.redhat.consulting.lh.service;

import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.infinispan.client.hotrod.RemoteCache;

import com.redhat.consulting.jdg.cache.RemoteCacheConfiguration;
import com.redhat.consulting.jdg.cache.GRID_NAMES;
import com.redhat.consulting.jdg.domain.Incident;

@Path("/api/incident")
@Model
public class IncidentsService {

	@Inject 
	RemoteCacheConfiguration configuration;
	
	private RemoteCache<String, Incident> cache;
	
	//@PostConstruct
	public IncidentsService() {
		configuration = new RemoteCacheConfiguration();
				configuration.initialize();
				
		cache = configuration.getCache(GRID_NAMES.LUFTHANSA_INCIDENTS.toString());
	}
	
    @GET
    @Path("/")
    @Produces({ "application/json" })
    public Set<Entry<String, Incident>> getAllIncidents() {
        return cache.entrySet();
    }
    
    @GET
    @Path("/numbers")
    @Produces({ "application/json" })
    public int getDifferentIncidentsNumbers() {
        return cache.keySet().size();
    }
    
    @GET
    @Path("/keys")
    @Produces({ "application/json" })
    public Set<String> getIncidentsTypes() {
        return cache.keySet();
    }
    
    @PUT
    @Path("/plane")
    @Produces("application/json")
    public void put(final @QueryParam("key") String key,
    		final @QueryParam("value") String value) {
    	try {
    		Incident incident = cache.get(key);
    		if (cache.get(key) == null) {
    			incident = new Incident();
    		}
    		incident.addPlaneId(value);

    		cache.put(key, incident);

    	} catch (Exception e) {
    		System.out.println(e);
    	}
    }
}
