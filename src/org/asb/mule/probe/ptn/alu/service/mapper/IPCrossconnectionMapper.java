package org.asb.mule.probe.ptn.alu.service.mapper;

import globaldefs.NameAndStringValue_T;

import org.asb.mule.probe.framework.entity.CrossConnect;
import org.asb.mule.probe.framework.entity.IPCrossconnection;
import org.asb.mule.probe.framework.service.Constant;
import org.asb.mule.probe.framework.util.CodeTool;

import com.alcatelsbell.nms.common.SysUtil;

import subnetworkConnection.CrossConnect_T;

public class IPCrossconnectionMapper extends CommonMapper

{
	private static IPCrossconnectionMapper instance;

	public static IPCrossconnectionMapper instance() {
		if (instance == null) {
			instance = new IPCrossconnectionMapper();
		}
		return instance;
	}

	public IPCrossconnection convertIPCrossConnection(CrossConnect_T vendorEntity, String neDn) {
		IPCrossconnection cc = new IPCrossconnection();

		//
		cc.setDn(SysUtil.nextDN());
		cc.setParentDn(neDn);
		String[] emsName = neDn.split(Constant.dnSplit);
		cc.setEmsName(emsName[0]);

		cc.setDirection(mapperConnectionDirection(vendorEntity.direction));
		cc.setCcType(mapperCcType(vendorEntity.ccType));
		cc.setaEnd(nvs2dn(vendorEntity.aEndNameList));
		cc.setzEnd(nvs2dn(vendorEntity.zEndNameList));

		cc.setAdditionalInfo(mapperAdditionalInfo(vendorEntity.additionalInfo));

		return cc;
	}

	public CrossConnect convertCrossConnect(CrossConnect_T vendorEntity, String neDn) {
		CrossConnect cc = new CrossConnect();

		cc.setDn(getCCdn(vendorEntity.aEndNameList, vendorEntity.zEndNameList));
		cc.setParentDn(neDn);
		String[] emsName = neDn.split(Constant.dnSplit);
		cc.setEmsName(emsName[0]);
		cc.setActive(vendorEntity.active);
		cc.setDirection(mapperConnectionDirection(vendorEntity.direction));
		cc.setCcType(mapperCcType(vendorEntity.ccType));
		cc.setaEndNameList(nvs2dn(vendorEntity.aEndNameList));
		cc.setzEndNameList(nvs2dn(vendorEntity.zEndNameList));
		cc.setaEndTP(end2tp(vendorEntity.aEndNameList));
		cc.setzEndTP(end2tp(vendorEntity.zEndNameList));
		cc.setAdditionalInfo(CodeTool.isoToGbk(mapperAdditionalInfo(vendorEntity.additionalInfo)));

		return cc;
	}

	private String getCCdn(NameAndStringValue_T[][] aEnds, NameAndStringValue_T[][] zEnds) {
		StringBuilder buffer = new StringBuilder();
		for (NameAndStringValue_T[] end : aEnds) {
			buffer.append(getTPRdn(end));
		}
		buffer.append("_");
		for (NameAndStringValue_T[] end : zEnds) {
			buffer.append(getTPRdn(end));
		}

		NameAndStringValue_T[] dn = new NameAndStringValue_T[3];
		dn[0] = new NameAndStringValue_T();
		dn[1] = new NameAndStringValue_T();
		dn[2] = new NameAndStringValue_T();
		dn[0].name = "EMS";
		dn[1].name = "ManagedElement";
		dn[2].name = "CrossConnect";
		dn[0].value = aEnds[0][0].value;
		dn[1].value = aEnds[0][1].value;
		dn[2].value = buffer.toString();

		return nv2dn(dn);
	}

	private String getTPRdn(NameAndStringValue_T[] tpName) {
		StringBuilder tpRdn = new StringBuilder();
		for (int i = 2; i < tpName.length; i++)
			tpRdn.append(tpName[i].value);
		return tpRdn.toString();
	}

}
