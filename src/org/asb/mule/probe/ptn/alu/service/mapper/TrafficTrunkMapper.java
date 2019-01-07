package org.asb.mule.probe.ptn.alu.service.mapper;

import org.asb.mule.probe.framework.entity.TrafficTrunk;
import org.asb.mule.probe.framework.util.CodeTool;

import flowDomainFragment.FlowDomainFragment_T;
import globaldefs.NameAndStringValue_T;

public class TrafficTrunkMapper extends CommonMapper

{
	private static TrafficTrunkMapper instance;

	public static TrafficTrunkMapper instance() {
		if (instance == null) {
			instance = new TrafficTrunkMapper();
		}
		return instance;
	}

	public TrafficTrunk convertTrafficTrunk(FlowDomainFragment_T vendorEntity, NameAndStringValue_T[] parentDn)

	{
		TrafficTrunk tt = new TrafficTrunk();
		tt.setDn(nv2dn(vendorEntity.name));
		tt.setParentDn(nv2dn(parentDn));
		tt.setEmsName(vendorEntity.name[0].value);
		tt.setNativeEMSName(CodeTool.isoToGbk(vendorEntity.nativeEMSName));
		tt.setUserLabel(CodeTool.isoToGbk(vendorEntity.userLabel));
		tt.setDirection(mapperConnectionDirection(vendorEntity.direction));
		tt.setTransmissionParams(mapperTransmissionPara(vendorEntity.transmissionParams));
		tt.setAdministrativeState(mapperAdministrativeState(vendorEntity.administrativeState));
		tt.setActiveState(mapperActiveState(vendorEntity.fdfrState));
		if (vendorEntity.aEnd.length > 0 && vendorEntity.aEnd[0] != null) {
			tt.setaEnd(end2String(vendorEntity.aEnd));
			tt.setaEnd(end2Ptp(vendorEntity.aEnd));
			tt.setaEndTrans(mapperTransmissionParas(vendorEntity.aEnd[0].transmissionParams));
		}
		if (vendorEntity.zEnd.length > 0 && vendorEntity.zEnd[0] != null) {
			tt.setzEnd(end2String(vendorEntity.zEnd));
			tt.setzEnd(end2Ptp(vendorEntity.zEnd));
			tt.setzEndtrans(mapperTransmissionParas(vendorEntity.zEnd[0].transmissionParams));
		}
		tt.setAdditionalInfo(mapperAdditionalInfo(vendorEntity.additionalInfo));
		return tt;

	}
}
