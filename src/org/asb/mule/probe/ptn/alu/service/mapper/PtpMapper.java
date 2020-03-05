package org.asb.mule.probe.ptn.alu.service.mapper;

import globaldefs.NameAndStringValue_T;

import org.asb.mule.probe.framework.entity.PTP;
import org.asb.mule.probe.framework.entity.R_FTP_PTP;
import org.asb.mule.probe.framework.util.CodeTool;

import subnetworkConnection.TPData_T;
import terminationPoint.GTP_T;
import terminationPoint.TPProtectionAssociation_T;
import terminationPoint.TerminationMode_T;
import terminationPoint.TerminationPoint_T;

public class PtpMapper extends CommonMapper

{
	private static PtpMapper instance;

	public static PtpMapper instance() {
		if (instance == null) {
			instance = new PtpMapper();
		}
		return instance;
	}

	public PTP convertPtp(TerminationPoint_T vendorEntity, String parentDn) {
		PTP ptp = new PTP();
		ptp.setDn(nv2dn(vendorEntity.name));
		ptp.setParentDn(parentDn);
		ptp.setEmsName(vendorEntity.name[0].value);
		ptp.setNativeEMSName(CodeTool.isoToGbk(vendorEntity.nativeEMSName));
		ptp.setUserLabel(CodeTool.isoToGbk(vendorEntity.userLabel));
		ptp.setConnectionState(mapperConnectionState(vendorEntity.connectionState));
		ptp.setDirection(mapperDirection(vendorEntity.direction));
		ptp.setEdgePoint(vendorEntity.edgePoint);
		ptp.setRate(mapperRates(vendorEntity.transmissionParams));
		ptp.setTpMappingMode(mapperTpMappingMode(vendorEntity.tpMappingMode));
		ptp.setTpProtectionAssociation(mapperProtectionAssiciation(vendorEntity.tpProtectionAssociation));
		ptp.setTransmissionParams(mapperTransmissionParas(vendorEntity.transmissionParams));
		ptp.setAdditionalInfo(mapperAdditionalInfo(vendorEntity.additionalInfo));
		for (NameAndStringValue_T nv : vendorEntity.additionalInfo) {
			if (nv.name.equals("location")) {
				ptp.setTag1(nv.value);
				break;
			}
		}
		ptp.setType(mapperTPtype(vendorEntity.type));
		return ptp;
	}

	public PTP convertGtp(GTP_T vendorEntity, String parentDn) {
		PTP ptp = new PTP();
		ptp.setDn(nv2dn(vendorEntity.name));
		ptp.setTag1(nvs2dn(vendorEntity.listOfTPs));
		ptp.setParentDn(parentDn);
		ptp.setEmsName(vendorEntity.name[0].value);
		ptp.setNativeEMSName(CodeTool.isoToGbk(vendorEntity.nativeEMSName));
		ptp.setUserLabel(CodeTool.isoToGbk(vendorEntity.userLabel));
		ptp.setConnectionState(mapperConnectionState(vendorEntity.gtpConnectionState));
		ptp.setAdditionalInfo(mapperAdditionalInfo(vendorEntity.additionalInfo));
		ptp.setRate("305");
		return ptp;
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
