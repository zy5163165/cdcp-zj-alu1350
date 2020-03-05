package org.asb.mule.probe.ptn.alu.service.mapper;

import globaldefs.NameAndStringValue_T;
import multiLayerSubnetwork.MultiLayerSubnetwork_T;

import org.asb.mule.probe.framework.entity.TopoNode;
import org.asb.mule.probe.framework.util.CodeTool;

import com.alcatelsbell.nms.common.SysUtil;

public class TopoNodeMapper extends CommonMapper {
	private static TopoNodeMapper instance;

	public static TopoNodeMapper instance() {
		if (instance == null) {
			instance = new TopoNodeMapper();
		}
		return instance;
	}

	public TopoNode convertTopoNode(MultiLayerSubnetwork_T vendorEntity, NameAndStringValue_T[] subnetwrokName) {
		TopoNode node = new TopoNode();
		// node.setDn(SysUtil.nextDN());
		node.setDn(nv2dn(vendorEntity.name));
		node.setName(nv2dn(vendorEntity.name));
		node.setParent(nv2dn(subnetwrokName));
		node.setNativeemsname(CodeTool.isoToGbk(vendorEntity.nativeEMSName));
		node.setAdditionalInfo(mapperAdditionalInfo(vendorEntity.additionalInfo));
		return node;
	}
}
