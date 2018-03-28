package com.redhat.consulting.lh.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.Set;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.api.continuous.ContinuousQuery;
import org.infinispan.query.api.continuous.ContinuousQueryListener;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;

import com.redhat.consulting.jdg.cache.RemoteCacheConfiguration;
import com.redhat.consulting.jdg.cache.listeners.EventLogListener;
import com.redhat.consulting.jdg.cache.GRID_NAMES;
import com.redhat.consulting.jdg.domain.Incident;

@Path("/api/incident")
@Model
public class IncidentsService {

	private Logger log = Logger.getLogger(this.getClass().getName());	
	
	@Inject 
	RemoteCacheConfiguration configuration;
	
	private RemoteCache<String, Incident> cache;
	
	private ContinuousQuery<String, Incident> continuousQuery;
	
//	public IncidentsService() {
//		configuration = new RemoteCacheConfiguration();
//		configuration.initialize();
//				
//		cache = configuration.getCache(GRID_NAMES.LUFTHANSA_INCIDENTS.toString());
//	}
	
	@PostConstruct
	public void initializeService() {
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
    @Path("/plane/{planeid}")
    @Produces("application/json")
    @Consumes("application/json")
    public void addPlaneIncident(final String key, final @PathParam("planeid") String planeid) {
    	try {
    		cache = configuration.getCache(GRID_NAMES.LUFTHANSA_INCIDENTS.toString());
    		
    		log.info("CACHE Is null="+cache);
    		
    		log.info("key adding "+key);

    		
    		Incident incident = cache.get(key);
    		if (cache.get(key) == null) {
    			incident = new Incident();
    		}
    		
    		
    		incident.addPlaneId(planeid);
    		log.info("INCIDENT ADDING "+incident);
    		log.info("IS CACHE NULL : "+cache);

    		
    		cache.put(key, incident);

    	} catch (Exception e) {
    		log.info(e.getMessage());
    		e.printStackTrace();
    	}
    }
    
    
    @PUT
    @Path("/plane/{planeid}/search")
    @Produces("application/json")
    @Consumes("application/json")
    public ArrayList<Incident> queryByPlaneIds(final @PathParam("planeid") String planeid) {
    	List<Incident> results = null;
    	try {
    		cache = configuration.getCache(GRID_NAMES.LUFTHANSA_INCIDENTS.toString());
    		
    		log.info("CACHE Is null="+cache);
    		log.info("search key is "+planeid);

    		
//    		Incident incident = cache.get(key);
//    		if (cache.get(key) == null) {
//    			incident = new Incident();
//    		}
//    		
//    		
//    		incident.addPlaneId(planeid);
//    		log.info("INCIDENT ADDING "+incident);
//    		log.info("IS CACHE NULL : "+cache);
//
//    		
//    		cache.put(key, incident);
    		
            QueryFactory qf = Search.getQueryFactory(cache);
            Query query = qf.from(Incident.class)
                  .having("planeIds").contains(planeid)
                  .build();

            results = query.list();
            System.out.printf("Found %d matches:\n", results.size());
            for (Incident p : results) {
            	log.info(">> " + p);
            }
            return (ArrayList<Incident>) results;

    	} catch (Exception e) {
    		log.info(e.getMessage());
    		e.printStackTrace();
    	}
    	return (ArrayList<Incident>) results;
    }
    
    @GET
    @Path("/plane/{planeid}/stream")
    @Produces("application/json")
    @Consumes("application/json")
    public List<String> processStreamEntries(final @PathParam("planeid") String planeid) {
    	List<String> incidentTypesList = new ArrayList<String>();
    	try {
    		cache = configuration.getCache(GRID_NAMES.LUFTHANSA_INCIDENTS.toString());

    		log.info("CACHE Is null="+cache);
    		log.info("search key is "+planeid);

    		
    		cache.entrySet().parallelStream()
    		.filter(e -> e.getValue().getPlaneIds().contains(planeid))
    		.forEach((e) -> {
    			log.info(e.getKey());
    			// c.getCacheManager().getCache("RedHat").put(e.getKey(), e.getValue());
    			incidentTypesList.add(e.getKey());
    		});
    		
//    		cache.entrySet().stream()
//    		.filter(e -> e.getValue().getPlaneIds().contains(planeid))
//    		.forEach((e) -> {
//    			log.info(e.getKey());
//    			// c.getCacheManager().getCache("RedHat").put(e.getKey(), e.getValue());
//    			incidentTypesList.add(e.getKey());
//    		});


    		log.info("Incidents List for plan ID <"+planeid+"> --> ["+incidentTypesList+"]");

    	} catch (Exception e) {
    		log.info(e.getMessage());
    		e.printStackTrace();
    	}
    	return incidentTypesList;
    }

//     private void queryPersonByPhone() {
//        String phoneNumber = readConsole("Enter phone number: ");
//
//        QueryFactory qf = Search.getQueryFactory(remoteCache);
//        Query query = qf.from(Person.class)
//              .having("phone.number").eq(phoneNumber)
//              .build();
//
//        List<Person> results = query.list();
//        System.out.printf("Found %d matches:\n", results.size());
//        for (Person p : results) {
//           System.out.println(">> " + p);
//        }
//     }
    
    @PUT
    @Path("/listener")
    @Produces("application/json")
    public void addRemoteListener() {
    	List<String> incidentTypesList = new ArrayList<String>();
    	try {
    		log.info("ADDING Remote Listener");

    		//configuration.getCache(GRID_NAMES.LUFTHANSA_INCIDENTS.toString()).addClientListener(listener, filterFactoryParams, converterFactoryParams);.addClientListener(new EventLogListener());
    		configuration.getCache(GRID_NAMES.LUFTHANSA_INCIDENTS.toString()).addClientListener(new EventLogListener());
    		log.info("Cache <"+GRID_NAMES.LUFTHANSA_INCIDENTS.toString()+"> has listeners ["+configuration.getCache(GRID_NAMES.LUFTHANSA_INCIDENTS.toString()).getListeners()+"]");
    	} catch (Exception e) {
    		log.info(e.getMessage());
    		e.printStackTrace();
    	}
    }
    
    @DELETE
    @Path("/listener")
    @Produces("application/json")
    public void removeRemoteListener() {
    	List<String> incidentTypesList = new ArrayList<String>();
    	try {
    		log.info("REmoving Remote Listener");
    		//configuration.getCache(GRID_NAMES.LUFTHANSA_INCIDENTS.toString()).addClientListener(listener, filterFactoryParams, converterFactoryParams);.addClientListener(new EventLogListener());
    		configuration.getCache(GRID_NAMES.LUFTHANSA_INCIDENTS.toString()).removeClientListener(new EventLogListener());
    		log.info("Cache <"+GRID_NAMES.LUFTHANSA_INCIDENTS.toString()+"> has listeners ["+configuration.getCache(GRID_NAMES.LUFTHANSA_INCIDENTS.toString()).getListeners()+"]");

    	} catch (Exception e) {
    		log.info(e.getMessage());
    		e.printStackTrace();
    	}
    }
    

    @PUT
    @Path("/continuousQuery")
    @Produces("application/json")
    public void addContinuousQueryListener() {
    	
        if (continuousQuery == null) {
        	configuration = new RemoteCacheConfiguration();
        	configuration.initialize();
    	cache = configuration.getCache(GRID_NAMES.LUFTHANSA_INCIDENTS.toString());

           QueryFactory qf = Search.getQueryFactory(cache);

           Query query = qf.from(Incident.class).maxResults(3)
//                 .having("humidity").lte(75.0f)
//                 .and().having("temperature").lte(3.0f)
//                 .and().having("rain").eq(0)
//                 .and().having("snowfall").gte(0)
                 .build();

           continuousQuery = Search.getContinuousQuery(cache);

           ContinuousQueryListener<String, Incident> cqListener = new ContinuousQueryListener<String, Incident>() {

              @Override
              public void resultJoining(String key, Incident f) {
                 //System.out.printf("Great news! Found perfect ski conditions at '%s' in %d-%d-%d\n", f.getLocation(), f.getYear(), f.getMonth(), f.getDay());
            	  log.info("CQ Joining: key="+key+" and Incidents = "+f.getPlaneIds() + " numbers is "+f.getPlaneIds().size());
              }

              @Override
              public void resultUpdated(String key, Incident f) {
                 //System.out.printf("The forecast was updated and the ski conditions are still good at '%s' in %d-%d-%d\n", f.getLocation(), f.getYear(), f.getMonth(), f.getDay());
            	  log.info("CQ Updated: key="+key+" and Incidents = "+f.getPlaneIds() + " numbers is "+f.getPlaneIds().size());

              }

              @Override
              public void resultLeaving(String key) {
//                 System.out.printf("The forecast %s was updated (or removed) and it no longer predicts good ski conditions\n", key);
            	  log.info("CQ Leaving: key="+key);

              }
              
           };

           continuousQuery.addContinuousQueryListener(query, cqListener);
           log.info("Continuous query listener added.");
        }
     }

    @DELETE
    @Path("/continuousQuery")
    @Produces("application/json")
     private void removeContinuousQueryListener() {
        if (continuousQuery != null) {
           continuousQuery.removeAllListeners();
           continuousQuery = null;
           System.out.println("Continuous query listener removed.");
        }
     }

}
