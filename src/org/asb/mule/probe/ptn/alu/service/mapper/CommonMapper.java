/**************************************************************************
 *
 * $RCSfile: ConfigDataConvertor.java,v $  $Revision: 1.1 $  $Date: 2010/04/01 03:03:44 $
 *
 * $Log: ConfigDataConvertor.java,v $
 * Revision 1.1  2010/04/01 03:03:44  panling
 * MR#:151
 *
 * Revision 1.1  2004/11/26 06:16:52  sea2000
 * MR#:NGINF-124
 *
 * Revision 1.1  2003/08/25 08:34:40  zhough
 * no message
 *
 * Revision 1.3  2003/08/06 08:05:27  zhough
 * *** empty log message ***
 *
 * Revision 1.2  2003/06/30 01:28:20  zhough
 * no message
 *
 * Revision 1.1  2003/06/20 06:22:51  zhough
 * *** empty log message ***
 *
 *
 *************************************************************************/

/*
 * @(#)ConfigDataConvertor.java 1.0 2003-06-10
 * Author: Zhou Ganhong
 *
 * Copyright (c) 2001 Gxlu, Inc.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Gxlu, Inc. 
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Gxlu.
 */

package org.asb.mule.probe.ptn.alu.service.mapper;

import org.asb.mule.probe.framework.service.Constant;

import performance.AdministrativeState_T;

import subnetworkConnection.Reroute_T;
import subnetworkConnection.SNCState_T;
import subnetworkConnection.SNCType_T;
import subnetworkConnection.TPData_T;
import terminationPoint.Directionality_T;
import terminationPoint.TPConnectionState_T;
import terminationPoint.TPType_T;
import transmissionParameters.LayeredParameters_T;

import flowDomain.ConnectivityState_T;
import globaldefs.ConnectionDirection_T;
import globaldefs.NameAndStringValue_T;

public abstract class CommonMapper {

	protected String mapperConnectionState(TPConnectionState_T vendorState) {
		String state;
		if (vendorState.equals(TPConnectionState_T.TPCS_BI_CONNECTED)) {
			state = "TPCS_BI_CONNECTED";
		} else if (vendorState.equals(TPConnectionState_T.TPCS_NA)) {
			state = "NA";
		} else if (vendorState.equals(TPConnectionState_T.TPCS_NOT_CONNECTED)) {
			state = "TPCS_NOT_CONNECTED";
		} else if (vendorState.equals(TPConnectionState_T.TPCS_SINK_CONNECTED)) {
			state = "TPCS_SINK_CONNECTED";
		} else if (vendorState.equals(TPConnectionState_T.TPCS_SOURCE_CONNECTED)) {
			state = "TPCS_SOURCE_CONNECTED";
		} else {
			state = "unknown";
		}
		return state;
	}

	protected String mapperDirection(Directionality_T vendorDirection) {
		String direction = null;
		switch (vendorDirection.value()) {
		case terminationPoint.Directionality_T._D_NA:
			direction = "D_NA";
			break;
		case terminationPoint.Directionality_T._D_BIDIRECTIONAL:
			direction = "D_BIDIRECTIONAL";
			break;
		case terminationPoint.Directionality_T._D_SOURCE:
			direction = "D_SOURCE";
			break;
		case terminationPoint.Directionality_T._D_SINK:
			direction = "D_SINK";
			break;
		}
		return direction;
	}

	protected String mapperRates(LayeredParameters_T[] transmissionParams) {
		if (transmissionParams != null && transmissionParams.length > 0) {
			StringBuffer sb = new StringBuffer();
			for (LayeredParameters_T nst : transmissionParams) {
				sb.append(Constant.listSplit);
				sb.append(nst.layer);
			}
			return sb.substring(2);
		}
		return "";
	}

	protected String mapperConnectionDirection(ConnectionDirection_T vendorDirection) {
		String direction = null;
		switch (vendorDirection.value()) {
		case ConnectionDirection_T._CD_BI:
			direction = "CD_BI";
			break;
		case ConnectionDirection_T._CD_UNI:
			direction = "CD_UNI";
			break;

		}
		return direction;
	}

	protected String mapperActiveState(SNCState_T state) {
		String value = "";
		switch (state.value()) {
		case SNCState_T._SNCS_ACTIVE:
			value = "SNCS_ACTIVE";
			break;
		case SNCState_T._SNCS_NONEXISTENT:
			value = "SNCS_NONEXISTENT";
			break;
		case SNCState_T._SNCS_PARTIAL:
			value = "SNCS_PARTIAL";
			break;
		case SNCState_T._SNCS_PENDING:
			value = "SNCS_PENDING";
			break;

		}
		return value;
	}

	protected String mapperCcType(SNCType_T ccType) {
		String value = "";
		switch (ccType.value()) {
		case SNCType_T._ST_ADD_DROP_A:
			value = "ST_ADD_DROP_A";
			break;
		case SNCType_T._ST_ADD_DROP_Z:
			value = "ST_ADD_DROP_Z";
			break;
		case SNCType_T._ST_DOUBLE_ADD_DROP:
			value = "ST_DOUBLE_ADD_DROP";
			break;
		case SNCType_T._ST_DOUBLE_INTERCONNECT:
			value = "ST_DOUBLE_INTERCONNECT";
			break;
		case SNCType_T._ST_EXPLICIT:
			value = "ST_EXPLICIT";
			break;
		case SNCType_T._ST_INTERCONNECT:
			value = "ST_INTERCONNECT";
			break;
		case SNCType_T._ST_OPEN_ADD_DROP:
			value = "ST_OPEN_ADD_DROP";
			break;
		case SNCType_T._ST_SIMPLE:
			value = "ST_SIMPLE";
			break;

		}
		return value;
	}

	protected String mapperAdministrativeState(AdministrativeState_T administrativeState) {
		String value = "";
		switch (administrativeState.value()) {
		case AdministrativeState_T._AS_Locked:
			value = "AS_Locked";
			break;
		case AdministrativeState_T._AS_Unlocked:
			value = "AS_Unlocked";
			break;

		}
		return value;
	}

	protected String mapperFDConnectivityState(ConnectivityState_T state) {
		String value = "";
		switch (state.value()) {
		case ConnectivityState_T._CS_FULLY_CONNECTED:
			value = "CS_FULLY_CONNECTED";
			break;
		case ConnectivityState_T._CS_NOT_FULLY_CONNECTED:
			value = "CS_NOT_FULLY_CONNECTED";
			break;
		case ConnectivityState_T._CS_UNKNOWN:
			value = "CS_UNKNOWN";
			break;
		}
		return value;
	}

	protected String mapperRerouteAllowed(Reroute_T rerouteAllowed) {
		String value = "";
		switch (rerouteAllowed.value()) {
		case Reroute_T._RR_NA:
			value = "RR_NA";
			break;
		case Reroute_T._RR_NO:
			value = "RR_NO";
			break;
		case Reroute_T._RR_YES:
			value = "RR_YES";
			break;
		}
		return value;
	}

	protected String mapperTransmissionParas(LayeredParameters_T[] transmissionParams) {
		StringBuffer sb = new StringBuffer();
		for (LayeredParameters_T nst : transmissionParams) {
			sb.append(mapperTransmissionPara(nst));

		}

		return sb.toString();
	}

	protected String mapperTransmissionPara(LayeredParameters_T nst) {
		StringBuffer sb = new StringBuffer();
		sb.append(nst.layer + Constant.dnSplit);
		for (NameAndStringValue_T name : nst.transmissionParams) {
			sb.append(name.name);
			sb.append(Constant.namevalueSplit);
			sb.append(name.value);
			sb.append(Constant.listSplit);
		}
		return sb.toString();
	}

	protected String mapperAdditionalInfo(NameAndStringValue_T[] additionalInfo) {
		StringBuffer sb = new StringBuffer();
		for (NameAndStringValue_T nst : additionalInfo) {
			sb.append(nst.name);
			sb.append(Constant.namevalueSplit);
			sb.append(nst.value);
			sb.append(Constant.listSplit);
		}

		return sb.toString();
	}

	protected String mapperTPtype(TPType_T type) {
		switch (type.value()) {
		case TPType_T._TPT_CTP:
			return "TPT_CTP";
		case TPType_T._TPT_PTP:
			return "TPT_PTP";
		case TPType_T._TPT_TPPool:
			return "TPT_TPPool";
		}
		return "";
	}

	protected String mapperNameAndStringValue(NameAndStringValue_T[] object) {
		StringBuffer sb = new StringBuffer();
		for (NameAndStringValue_T nst : object) {
			sb.append(nst.value);
			sb.append(Constant.dnSplit);
		}
		if (object.length > 0) {
			return sb.toString().trim().substring(0, sb.toString().trim().length() - 1);

		}
		return sb.toString();
	}

	protected String mapperNameAndStringValueList(NameAndStringValue_T[][] endNameList) {
		StringBuffer sb = new StringBuffer();
		for (NameAndStringValue_T[] nst : endNameList) {
			sb.append(mapperNameAndStringValue(nst));
			sb.append(Constant.listSplit);
		}
		return sb.toString();
	}

	protected String mapperStringList(String[] object) {
		StringBuffer sb = new StringBuffer();
		for (String nst : object) {
			sb.append(nst);
			sb.append(Constant.listSplit);
		}

		return sb.toString().trim();
	}

	protected String mapperParentDnNameAndStringValue(NameAndStringValue_T[] object) {
		String dn = "";
		for (int i = 0; i < object.length; i++) {
			if (i > 0) {
				dn += Constant.dnSplit;
			}
			dn += object[i].name + Constant.namevalueSplit + object[i].value;
			if ((i + 2) >= object.length) {
				break;
			}
		}
		return dn;
	}

	protected String mapperNameAndStringValues(NameAndStringValue_T[][] object) {
		StringBuffer sb = new StringBuffer();
		for (NameAndStringValue_T[] ns : object) {
			sb.append(mapperNameAndStringValue(ns));
			sb.append(Constant.listSplit);
		}
		return sb.toString();
	}

	//
	public static String nv2dn(NameAndStringValue_T[] name) {
		if (name != null && name.length > 0) {
			StringBuilder dnString = new StringBuilder();
			for (NameAndStringValue_T nv : name) {
				dnString.append(Constant.dnSplit).append(nv.name).append(Constant.namevalueSplit).append(nv.value);
			}
			return dnString.substring(1);
		}
		return "";
	}

	public static String nvs2dn(NameAndStringValue_T[][] object) {
		if (object != null && object.length > 0) {
			StringBuffer sb = new StringBuffer();
			for (NameAndStringValue_T[] ns : object) {
				sb.append(Constant.listSplit);
				sb.append(nv2dn(ns));
			}
			return sb.substring(2);
		}
		return "";
	}

	public String nvss2dn(NameAndStringValue_T[][][] object) {
		if (object != null && object.length > 0) {
			StringBuffer sb = new StringBuffer();
			for (NameAndStringValue_T[][] nvs : object) {
				sb.append(Constant.listSplit);
				sb.append(nvs2dn(nvs));
			}
			return sb.substring(2);
		}
		return "";
	}

	public String getParentdn(NameAndStringValue_T[] nv, int length) {
		StringBuilder dnString = new StringBuilder();
		for (int i = 0; i < length; i++) {
			dnString.append(Constant.dnSplit).append(nv[i].name).append(Constant.namevalueSplit).append(nv[i].value);
		}
		return dnString.substring(1);
	}

	public String end2String(TPData_T[] end) {
		if (end != null && end.length > 0) {
			StringBuilder sb = new StringBuilder();
			for (TPData_T tp : end) {
				sb.append(Constant.listSplit);
				sb.append(nv2dn(tp.tpName));
			}
			return sb.substring(2);
		}
		return "";
	}

	public String end2Ptp(TPData_T[] end) {
		if (end != null && end.length > 0) {
			StringBuilder sb = new StringBuilder();
			for (TPData_T tp : end) {
				sb.append(Constant.listSplit);
				sb.append(getParentdn(tp.tpName, 3));
			}
			return sb.substring(2);
		}
		return "";
	}

	public String end2tp(NameAndStringValue_T[][] object) {
		if (object != null && object.length > 0) {
			StringBuilder sb = new StringBuilder();
			for (NameAndStringValue_T[] ns : object) {
				sb.append(Constant.listSplit);
				sb.append(getParentdn(ns, 3));
			}
			return sb.substring(2);
		}
		return "";
	}

	public String end2ne(TPData_T[] end) {
		if (end != null && end.length > 0) {
			StringBuilder sb = new StringBuilder();
			for (TPData_T tp : end) {
				sb.append(Constant.listSplit);
				sb.append(getParentdn(tp.tpName, 2));
			}
			return sb.substring(2);
		}
		return "";
	}
}