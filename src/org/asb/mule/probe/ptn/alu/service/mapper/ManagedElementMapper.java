package org.asb.mule.probe.ptn.alu.service.mapper;

import managedElement.CommunicationState_T;

import org.asb.mule.probe.framework.entity.ManagedElement;
import org.asb.mule.probe.framework.service.Constant;
import org.asb.mule.probe.framework.util.CodeTool;

public class ManagedElementMapper extends CommonMapper

{
	private static ManagedElementMapper instance;

	public static ManagedElementMapper instance() {
		if (instance == null) {
			instance = new ManagedElementMapper();
		}
		return instance;
	}

	public ManagedElement convertManagedElement(managedElement.ManagedElement_T vendorEntity)

	{
		ManagedElement ne = new ManagedElement();

		ne.setDn(nv2dn(vendorEntity.name));
		ne.setParentDn(vendorEntity.name[0].name + Constant.namevalueSplit + vendorEntity.name[0].value);
		ne.setEmsName(vendorEntity.name[0].value);

		ne.setNativeEMSName(CodeTool.isoToGbk(vendorEntity.nativeEMSName));
		ne.setUserLabel(CodeTool.isoToGbk(vendorEntity.userLabel));
		ne.setNeVersion(vendorEntity.version);
		ne.setLocation(CodeTool.isoToGbk(vendorEntity.location));
		ne.setProductName(CodeTool.isoToGbk(vendorEntity.productName));
		ne.setCommunicationState(mapperCommunicationState(vendorEntity.communicationState));
		ne.setEmsInSyncState(vendorEntity.emsInSyncState);

		StringBuilder rateSb = new StringBuilder();
		for (short rate : vendorEntity.supportedRates) {
			rateSb.append(rate);
			rateSb.append(Constant.listSplit);
		}
		ne.setSupportedRates(rateSb.toString());

		ne.setAdditionalInfo(CodeTool.isoToGbk(mapperAdditionalInfo(vendorEntity.additionalInfo)));

		return ne;
	}

	private String mapperCommunicationState(CommunicationState_T state) {
		switch (state.value()) {
		case CommunicationState_T._CS_AVAILABLE:
			return "CS_AVAILABLE";
		case CommunicationState_T._CS_UNAVAILABLE:
			return "CS_UNAVAILABLE";

		}
		return "";
	}

}
