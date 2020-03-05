package org.asb.mule.probe.ptn.alu.service.mapper;

import org.asb.mule.probe.framework.entity.CTP;
import org.asb.mule.probe.framework.util.CodeTool;

import terminationPoint.TPProtectionAssociation_T;
import terminationPoint.TerminationMode_T;
import terminationPoint.TerminationPoint_T;

public class CtpMapper extends CommonMapper

{
	private static CtpMapper instance;

	public static CtpMapper instance() {
		if (instance == null) {
			instance = new CtpMapper();
		}
		return instance;
	}

	//protected Logger sbilog = ProbeLog.getInstance().getSbiLog();

	public CTP convertCtp(TerminationPoint_T vendorEntity, String parentDn) {

		CTP Ctp = new CTP();

		// String dn=vendorEntity.name[0].value + Constant.dnSplit
		// + vendorEntity.name[1].value + Constant.dnSplit
		// + vendorEntity.name[2].name+Constant.namevalueSplit+vendorEntity.name[2].value;
		// String dn = "";
		// for (int i = 0; i < vendorEntity.name.length; i++) {
		// if (i > 0) {
		// dn += Constant.dnSplit;
		// }
		// dn += vendorEntity.name[i].name + Constant.namevalueSplit + vendorEntity.name[i].value;
		// }

		// sbilog.info("CTP DN is "+debugStr);

		//Ctp.setDn(SysUtil.nextDN());

		Ctp.setDn(nv2dn(vendorEntity.name));

		if (parentDn.contains("@FTP:")) {
			if (Ctp.getDn().contains("@PTP:")) {
				Ctp.setDn(Ctp.getDn().replace("@PTP:","@FTP:"));
			}
		}
		//Ctp.setTag1(nv2dn(vendorEntity.name));
		// Ctp.setParentDn(vendorEntity.name[0].value + Constant.dnSplit + vendorEntity.name[1].value);
		Ctp.setParentDn(parentDn);
		Ctp.setEmsName(vendorEntity.name[0].value);

		Ctp.setNativeEMSName(CodeTool.isoToGbk(vendorEntity.nativeEMSName));
		Ctp.setUserLabel(CodeTool.isoToGbk(vendorEntity.userLabel));

		Ctp.setConnectionState(mapperConnectionState(vendorEntity.connectionState));
		Ctp.setDirection(mapperDirection(vendorEntity.direction));
		Ctp.setEdgePoint(vendorEntity.edgePoint);
		Ctp.setTpMappingMode(mapperTpMappingMode(vendorEntity.tpMappingMode));
		Ctp.setTpProtectionAssociation(mapperProtectionAssiciation(vendorEntity.tpProtectionAssociation));
		Ctp.setTransmissionParams(mapperTransmissionParas(vendorEntity.transmissionParams));

		Ctp.setAdditionalInfo(mapperAdditionalInfo(vendorEntity.additionalInfo));

		return Ctp;
	}

	private String mapperProtectionAssiciation(TPProtectionAssociation_T tpProtectionAssociation) {
		String pmode = "";
		switch (tpProtectionAssociation.value()) {
		case terminationPoint.TPProtectionAssociation_T._TPPA_NA:
			pmode = "TPPA_NA";
			break;
		case terminationPoint.TPProtectionAssociation_T._TPPA_PSR_RELATED:
			pmode = "TPPA_PSR_RELATED";
			break;

		}
		return pmode;
	}

	private String mapperTpMappingMode(TerminationMode_T tpMappingMode) {
		String mode = "";
		switch (tpMappingMode.value()) {
		case terminationPoint.TerminationMode_T._TM_NA:
			mode = "D_NA";
			break;
		case terminationPoint.TerminationMode_T._TM_NEITHER_TERMINATED_NOR_AVAILABLE_FOR_MAPPING:
			mode = "TM_NEITHER_TERMINATED_NOR_AVAILABLE_FOR_MAPPING";
			break;
		case terminationPoint.TerminationMode_T._TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING:
			mode = "TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING";
			break;

		}
		return mode;
	}

}
