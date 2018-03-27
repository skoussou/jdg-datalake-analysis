package com.redhat.consulting.jdg.model.protobuf;

import java.util.ArrayList;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoMessage;

@ProtoMessage(name = "Incident")
//@ProtoDoc("@Indexed")
public class Incident {
	
	String planeId;

    //@ProtoDoc("@IndexedField(index = true, store = false)")
	@ProtoDoc("repeated")
    @ProtoField(number = 1, collectionImplementation = ArrayList.class, required = true)
	public String getPlaneId() {
		return planeId;
	}

	public void setPlaneId(String planeId) {
		this.planeId = planeId;
	}

	
	
}
