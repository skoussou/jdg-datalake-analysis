package com.redhat.consulting.jdg.cache;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import java.util.logging.Logger;

import com.redhat.consulting.jdg.domain.Incident;

@ApplicationScoped
public class RemoteCacheConfiguration implements CacheConfiguration {

	private Logger log = Logger.getLogger(this.getClass().getName());
	
	//@Inject
    //@SystemProperty(value = "cache.hotrod.hostname")
    private String host;

    //@Inject
    //@SystemProperty(value = "cache.hotrod.port", defaultValue = "11222")
    private String port = "11222";

    //@Inject
    //@SystemProperty(value = "cache.username")
    private String username = "admin";

    //@Inject
    //@SystemProperty(value = "cache.password")
    private String password = "redhat1!";

    private RemoteCacheManager cacheManager;    
    
    @PostConstruct
    public void initialize() {
    	log.info("Establishing cache manager for "+username+" at "+host+":"+port );

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer()
                .host(host)
                .port(Integer.parseInt(port))
                .marshaller(new ProtoStreamMarshaller())
                .maxRetries(10)
                .security()
                    .authentication()
                        .serverName("jdg-server")
                        .saslMechanism("DIGEST-MD5")
                        .callbackHandler(new LoginHandler(username, password.toCharArray(), "ApplicationRealm"))
                    .enable();
        cacheManager = new RemoteCacheManager(builder.build());
        
        log.info("Established cache manager for "+username+" at "+host+":"+port );

        registerSchemasAndMarshallers();
    }

    private void registerSchemasAndMarshallers() {
        try {
            // Register entity marshaller on the client side ProtoStreamMarshaller instance associated with the remote cache manager.
        	SerializationContext serCtx =
        		    ProtoStreamMarshaller.getSerializationContext(cacheManager);

        	ProtoSchemaBuilder protoSchemaBuilder = new ProtoSchemaBuilder();
        	String incidentSchemaFile = protoSchemaBuilder
        	    .fileName("incident.proto")
        	    .packageName("lufthansa")
        	    .addClass(Incident.class)
        	    .build(serCtx);

        	// the types can be marshalled now
        	log.info("Can marshal Incident.class now = "+serCtx.canMarshall(Incident.class) );
//        	assertTrue(serCtx.canMarshall(Incident.class));
//        	assertTrue(serCtx.canMarshall(Note.class));

        	// display the schema file
        	System.out.println(incidentSchemaFile);

            // register the schemas with the server too
            RemoteCache<String, String> metadataCache = cacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
            //metadataCache.put(PROTOBUF_DEFINITION_RESOURCE, readResource(PROTOBUF_DEFINITION_RESOURCE));
            metadataCache.put("incident.proto", incidentSchemaFile);
            String errors = metadataCache.get(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX);
            if (errors != null) {
               throw new IllegalStateException("Some Protobuf schema files contain errors:\n" + errors);
            }        	
      
        } catch (IOException ioe) {
        	log.info("Could not register marshallers! Remote querying is not going to work properly.");
        	log.info(ioe.getMessage());
        }
    }
    
    public RemoteCache getCache(String namedCache) {
    	if (namedCache == null) {
    		namedCache = "default";
    		System.out.printf("Cache provided was null. So, Reaching out to <%s> cache.", namedCache);
    	}
		System.out.printf("Reaching out to <%s> cache.", namedCache);
        return cacheManager.getCache("namedCache");
    }
    
//    private String readResource(String resourcePath) throws IOException {
//        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
//            Reader reader = new InputStreamReader(is, "UTF-8");
//            StringWriter writer = new StringWriter();
//            char[] buf = new char[1024];
//            int len;
//            while ((len = reader.read(buf)) != -1) {
//                writer.write(buf, 0, len);
//            }
//            return writer.toString();
//        }
//    }
}
