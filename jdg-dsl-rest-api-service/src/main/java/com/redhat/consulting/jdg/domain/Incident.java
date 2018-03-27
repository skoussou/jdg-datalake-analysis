package com.redhat.consulting.jdg.domain;

import java.util.ArrayList;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoMessage;

@ProtoMessage(name = "Incident")
//@ProtoDoc("@Indexed")
public class Incident {
	
	ArrayList<String> planeIds;
	
    public void setPlaneIds(ArrayList<String> planeIds) {
		this.planeIds = planeIds;
	}

	//@ProtoDoc("@IndexedField(index = true, store = false)")
	//@ProtoDoc("repeated")
    @ProtoField(number = 1, collectionImplementation = ArrayList.class, required = true)
	public ArrayList<String> getPlaneIds() {
		return planeIds;
	}

	public void addPlaneId(String planeId) {
		this.planeIds.add(planeId);
	}

	@Override
	public String toString() {
		return "Incident [planeIds=" + planeIds + "]";
	}
}
