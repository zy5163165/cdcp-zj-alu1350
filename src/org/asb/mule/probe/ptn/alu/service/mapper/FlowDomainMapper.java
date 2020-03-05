package org.asb.mule.probe.ptn.alu.service.mapper;

import org.asb.mule.probe.framework.entity.FlowDomain;
import org.asb.mule.probe.framework.service.Constant;
import org.asb.mule.probe.framework.util.CodeTool;

import flowDomain.FlowDomain_T;
import globaldefs.NameAndStringValue_T;


public class FlowDomainMapper extends CommonMapper {
	private static FlowDomainMapper instance;

	public static FlowDomainMapper instance() {
		if (instance == null) {
			instance = new FlowDomainMapper();
		}
		return instance;
	}

	

	public FlowDomain convertFlowDomain(FlowDomain_T vendorEntity) {
		FlowDomain tt = new FlowDomain();

		String dn="";
		for (NameAndStringValue_T nv:vendorEntity.name) {
			if (dn.length()==0) {
				dn+=nv.name+Constant.namevalueSplit+nv.value;
			} else {
				dn+=Constant.dnSplit+nv.name+Constant.namevalueSplit+nv.value;
			}
			
		}
		tt.setDn(dn);


		tt.setNativeEMSName(CodeTool.isoToGbk(vendorEntity.nativeEMSName));
		tt.setUserLabel(CodeTool.isoToGbk(vendorEntity.userLabel));
		tt.setfDConnectivityState(mapperFDConnectivityState(vendorEntity.fDConnectivityState));
		/**
		 * 
	private String multipointServiceAttr;
		 */
		tt.setNetworkAccessDomain(vendorEntity.networkAccessDomain);
		tt.setFdType(vendorEntity.fdType);
		tt.setTransmissionParams(mapperTransmissionParas(vendorEntity.transmissionParams));

		
		
		tt.setAdditionalInfo(mapperAdditionalInfo(vendorEntity.additionalInfo));

		
		return tt;
	}
}
