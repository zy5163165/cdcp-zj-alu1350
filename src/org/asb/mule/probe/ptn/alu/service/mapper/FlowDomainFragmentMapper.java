package org.asb.mule.probe.ptn.alu.service.mapper;

import org.asb.mule.probe.framework.entity.FlowDomainFragment;
import org.asb.mule.probe.framework.util.CodeTool;

import flowDomainFragment.FlowDomainFragment_T;
import globaldefs.NameAndStringValue_T;

public class FlowDomainFragmentMapper extends CommonMapper

{
	private static FlowDomainFragmentMapper instance;

	public static FlowDomainFragmentMapper instance() {
		if (instance == null) {
			instance = new FlowDomainFragmentMapper();
		}
		return instance;
	}

	public FlowDomainFragment convertFlowDomainFragment(FlowDomainFragment_T vendorEntity) {
		FlowDomainFragment tt = new FlowDomainFragment();

		tt.setDn(nv2dn(vendorEntity.name));
		tt.setEmsName(vendorEntity.name[0].value);

		tt.setNativeEMSName(CodeTool.isoToGbk(vendorEntity.nativeEMSName));
		tt.setUserLabel(CodeTool.isoToGbk(vendorEntity.userLabel));
		tt.setNetworkAccessDomain(vendorEntity.networkAccessDomain);
		tt.setFdfrType(vendorEntity.fdfrType);
		tt.setDirection(mapperConnectionDirection(vendorEntity.direction));
		tt.setTransmissionParams(mapperTransmissionPara(vendorEntity.transmissionParams));
		tt.setFlexible(vendorEntity.flexible);
		tt.setRate(vendorEntity.transmissionParams.layer + "");
		tt.setAdministrativeState(mapperAdministrativeState(vendorEntity.administrativeState));
		tt.setFdfrState(mapperActiveState(vendorEntity.fdfrState));
		if (vendorEntity.aEnd.length > 0 && vendorEntity.aEnd[0] != null) {
			tt.setaEnd(end2String(vendorEntity.aEnd));
			tt.setaPtp(end2Ptp(vendorEntity.aEnd));
			tt.setaEndTrans(mapperTransmissionParas(vendorEntity.aEnd[0].transmissionParams));
			tt.setaNE(end2ne(vendorEntity.aEnd));
		}

		if (vendorEntity.zEnd.length > 0 && vendorEntity.zEnd[0] != null) {
			tt.setzEnd(end2String(vendorEntity.zEnd));
			tt.setzPtp(end2Ptp(vendorEntity.zEnd));
			tt.setzEndtrans(mapperTransmissionParas(vendorEntity.zEnd[0].transmissionParams));
			tt.setzNE(end2ne(vendorEntity.zEnd));
		}
		tt.setAdditionalInfo(mapperAdditionalInfo(vendorEntity.additionalInfo));

		return tt;
	}

}
