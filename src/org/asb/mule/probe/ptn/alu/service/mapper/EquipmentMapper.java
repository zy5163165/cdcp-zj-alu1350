package org.asb.mule.probe.ptn.alu.service.mapper;

import org.asb.mule.probe.framework.entity.Equipment;
import org.asb.mule.probe.framework.util.CodeTool;

import equipment.Equipment_T;
import equipment.ServiceState_T;

public class EquipmentMapper extends CommonMapper

{
	private static EquipmentMapper instance;

	public static EquipmentMapper instance() {
		if (instance == null) {
			instance = new EquipmentMapper();
		}
		return instance;
	}

	public Equipment convertEquipment(Equipment_T vendorEntity, String parentDn)

	{
		Equipment ne = new Equipment();

		ne.setDn(nv2dn(vendorEntity.name));
		ne.setParentDn(parentDn);
		ne.setEmsName(vendorEntity.name[0].value);

		ne.setNativeEMSName(CodeTool.isoToGbk(vendorEntity.nativeEMSName));
		ne.setUserLabel(CodeTool.isoToGbk(vendorEntity.userLabel));
		ne.setAlarmReportingIndicator(vendorEntity.alarmReportingIndicator);
		ne.setExpectedEquipmentObjectType(vendorEntity.expectedEquipmentObjectType);
		ne.setInstalledEquipmentObjectType(vendorEntity.installedEquipmentObjectType);
		ne.setInstalledPartNumber(vendorEntity.installedPartNumber);
		ne.setInstalledSerialNumber(vendorEntity.installedSerialNumber);
		ne.setInstalledVersion(vendorEntity.installedVersion);
		ne.setServiceState(mapperServiceState(vendorEntity.serviceState));
		ne.setAdditionalInfo(CodeTool.isoToGbk(mapperAdditionalInfo(vendorEntity.additionalInfo)));

		String objectType = vendorEntity.installedEquipmentObjectType;
		if (objectType == null || objectType.isEmpty()) {
			objectType = vendorEntity.expectedEquipmentObjectType;
		}
		ne.setTag1(objectType);

		return ne;
	}

	public String mapperServiceState(ServiceState_T state) {
		switch (state.value()) {
		case 0:
			return "IN_SERVICE";
		case 1:
			return "OUT_OF_SERVICE";
		case 2:
			return "OUT_OF_SERVICE_BY_MAINTENANCE";
		case 3:
			return "SERV_NA";
		}
		return "";
	}

}
