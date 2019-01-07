package org.asb.mule.probe.ptn.alu.service.mapper;

import org.asb.mule.probe.framework.entity.EquipmentHolder;
import org.asb.mule.probe.framework.util.CodeTool;

import equipment.EquipmentHolder_T;
import equipment.HolderState_T;

public class EquipmentHolderMapper extends CommonMapper

{
	private static EquipmentHolderMapper instance;

	public static EquipmentHolderMapper instance() {
		if (instance == null) {
			instance = new EquipmentHolderMapper();
		}
		return instance;
	}

	public EquipmentHolder convertEquipmentHolder(EquipmentHolder_T vendorEntity, String parentDn)

	{
		EquipmentHolder ne = new EquipmentHolder();

		ne.setDn(nv2dn(vendorEntity.name));
		ne.setParentDn(parentDn);
		ne.setEmsName(vendorEntity.name[0].value);

		ne.setNativeEMSName(CodeTool.isoToGbk(vendorEntity.nativeEMSName));
		ne.setUserLabel(CodeTool.isoToGbk(vendorEntity.userLabel));
		ne.setAlarmReportingIndicator(vendorEntity.alarmReportingIndicator);
		ne.setAcceptableEquipmentTypeList(mapperStringList(vendorEntity.acceptableEquipmentTypeList));
		ne.setExpectedOrInstalledEquipment(nv2dn(vendorEntity.expectedOrInstalledEquipment));
		ne.setHolderState(mapperHolderState(vendorEntity.holderState));
		ne.setHolderType(vendorEntity.holderType);
		ne.setAdditionalInfo(CodeTool.isoToGbk(mapperAdditionalInfo(vendorEntity.additionalInfo)));

		return ne;
	}

	private String mapperHolderState(HolderState_T holderState) {
		String state;
		if (holderState.equals(HolderState_T.EMPTY)) {
			state = "EMPTY";
		} else if (holderState.equals(HolderState_T.EXPECTED_AND_NOT_INSTALLED)) {
			state = "EXPECTED_AND_NOT_INSTALLED";
		} else if (holderState.equals(HolderState_T.INSTALLED_AND_EXPECTED)) {
			state = "INSTALLED_AND_EXPECTED";
		} else if (holderState.equals(HolderState_T.INSTALLED_AND_NOT_EXPECTED)) {
			state = "INSTALLED_AND_NOT_EXPECTED";
		} else if (holderState.equals(HolderState_T.MISMATCH_OF_INSTALLED_AND_EXPECTED)) {
			state = "MISMATCH_OF_INSTALLED_AND_EXPECTED";
		} else {
			state = "unknown";
		}
		return state;
	}

}
