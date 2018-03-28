package com.redhat.consulting.jdg.domain;

import java.util.ArrayList;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoMessage;

import com.redhat.consulting.lh.service.APIs;

@ProtoMessage(name = "Incident")
@ProtoDoc("@Indexed")
public class Incident implements APIs{
	
	ArrayList<String> planeIds;
	
	String incidentType;


	@ProtoDoc("@IndexedField(index = true, store = false)")
    @ProtoField(number = 1, collectionImplementation = ArrayList.class)
	public ArrayList<String> getPlaneIds() {
		return planeIds;
	}
	
	@ProtoDoc("@IndexedField(index = true, store = false)")
    @ProtoField(number = 2)
    public String getIncidentType() {
		return incidentType;
	}

	public void setIncidentType(String incident) {
		this.incidentType = incident;
	}

	public void setPlaneIds(ArrayList<String> planeIds) {
		this.planeIds = planeIds;
	}

	public void addPlaneId(String planeId) {
		if (planeIds == null) {
			planeIds = new ArrayList<String>();
		}
		this.planeIds.add(planeId);
	}

	@Override
	public String toString() {
		return "Incident [planeIds=" + planeIds + "]";
	}
}
