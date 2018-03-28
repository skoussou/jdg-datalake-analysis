package com.redhat.consulting.jdg.cache.listeners;

import java.util.logging.Logger;

import org.infinispan.client.hotrod.annotation.ClientCacheEntryCreated;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryExpired;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryModified;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryRemoved;
import org.infinispan.client.hotrod.annotation.ClientListener;
import org.infinispan.client.hotrod.event.ClientCacheEntryCreatedEvent;
import org.infinispan.client.hotrod.event.ClientCacheEntryExpiredEvent;
import org.infinispan.client.hotrod.event.ClientCacheEntryModifiedEvent;
import org.infinispan.client.hotrod.event.ClientCacheEntryRemovedEvent;

@ClientListener()
public class EventLogListener {

	private Logger log = Logger.getLogger(this.getClass().getName());	

	
   @ClientCacheEntryCreated
   public void handleCreatedEvent(ClientCacheEntryCreatedEvent e) {
	   log.info("REACT --> "+e);;
   }

   @ClientCacheEntryModified
   public void handleModifiedEvent(ClientCacheEntryModifiedEvent e) {
	   log.info("REACT --> "+e);;
   }

   @ClientCacheEntryExpired
   public void handleExpiredEvent(ClientCacheEntryExpiredEvent e) {
	   log.info("REACT --> "+e);;
   }

   @ClientCacheEntryRemoved
   public void handleRemovedEvent(ClientCacheEntryRemovedEvent e) {
	   log.info("REACT --> "+e);;
   }

}
