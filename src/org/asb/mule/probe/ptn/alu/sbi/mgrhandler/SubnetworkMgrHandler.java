package org.asb.mule.probe.ptn.alu.sbi.mgrhandler;

import java.util.Vector;

import org.asb.mule.probe.framework.util.CodeTool;
import org.asb.mule.probe.framework.util.FileLogger;

import common.CapabilityList_THolder;

import globaldefs.*;
import topologicalLink.*;
import emsMgr.*;
import multiLayerSubnetwork.*;
import subnetworkConnection.*;

public class SubnetworkMgrHandler {
	private static SubnetworkMgrHandler instance;

	private static final String head1 = "SubnetworkMgrHandler::";

	public static SubnetworkMgrHandler instance() {
		if (null == instance)
			instance = new SubnetworkMgrHandler();

		return instance;
	}

	public TopologicalLink_T[] retrieveAllTopologicalLinks(MultiLayerSubnetworkMgr_I subnetworkMgr, NameAndStringValue_T[] subnetworkName, FileLogger errorlog)
			throws ProcessingFailureException {
		TopologicalLinkList_THolder tpLinkList = new TopologicalLinkList_THolder();
		TopologicalLinkIterator_IHolder tpLinkIt = new TopologicalLinkIterator_IHolder();

		Vector<TopologicalLink_T> emsTPLinkVector = new Vector<TopologicalLink_T>();

		subnetworkMgr.getAllTopologicalLinks(subnetworkName, 50, tpLinkList, tpLinkIt);

		for (TopologicalLink_T topologicalLink : tpLinkList.value) {
			emsTPLinkVector.addElement(topologicalLink);
		}

		if (null != tpLinkIt.value) {
			boolean shouldContinue = true;
			while (shouldContinue) {
				try {
					shouldContinue = tpLinkIt.value.next_n(50, tpLinkList);
					for (TopologicalLink_T topologicalLink : tpLinkList.value) {
						emsTPLinkVector.addElement(topologicalLink);
					}
				} catch (ProcessingFailureException e) {
					errorlog.error("retrieveAllSections ProcessingFailureException: " + CodeTool.isoToGbk(e.errorReason), e);
					shouldContinue = false;
				} catch (org.omg.CORBA.SystemException e) {
					errorlog.error("retrieveAllSections CORBA.SystemException: " + e.getMessage(), e);
					shouldContinue = false;
				}
			}

			try {
				tpLinkIt.value.destroy();
			} catch (Throwable ex) {
				// datalog.info("retrieveAllTopologicalLinks:destory Iterator");
			}
		}

		TopologicalLink_T[] tpLinks = new TopologicalLink_T[emsTPLinkVector.size()];
		emsTPLinkVector.copyInto(tpLinks);

		return tpLinks;
	}

	public SubnetworkConnection_T[] retrieveAllSNCs(MultiLayerSubnetworkMgr_I subnetworkMgr, NameAndStringValue_T[] subnetworkName, short[] layerRateList)
			throws ProcessingFailureException {

		SubnetworkConnectionList_THolder sncList = new SubnetworkConnectionList_THolder();
		SNCIterator_IHolder sncIt = new SNCIterator_IHolder();

		java.util.Vector emsSNCVector = new java.util.Vector();

		subnetworkMgr.getAllSubnetworkConnections(subnetworkName, layerRateList, 50, sncList, sncIt);

		for (int i = 0; i < sncList.value.length; i++) {
			emsSNCVector.addElement(sncList.value[i]);
		}

		if (null != sncIt.value) {
			boolean shouldContinue = true;
			while (shouldContinue) {
				shouldContinue = sncIt.value.next_n(50, sncList);

				for (int i = 0; i < sncList.value.length; i++)
					emsSNCVector.addElement(sncList.value[i]);
			}

			try {
				sncIt.value.destroy();
			} catch (Throwable ex) {
				// datalog.info("destory Iterator");
			}
		}

		SubnetworkConnection_T[] sncs = new SubnetworkConnection_T[emsSNCVector.size()];
		emsSNCVector.copyInto(sncs);

		return sncs;
	}

	public void retrieveRouteAndTopologicalLinks(MultiLayerSubnetworkMgr_I subnetworkMgr, NameAndStringValue_T[] sncName, Route_THolder route,
			TopologicalLinkList_THolder topologicalLinkList) throws ProcessingFailureException {

		subnetworkMgr.getRouteAndTopologicalLinks(sncName, route, topologicalLinkList);

	}

	public CrossConnect_T[] retrieveRoute(MultiLayerSubnetworkMgr_I subnetworkMgr, NameAndStringValue_T[] sncName, boolean includeHigherOrderCCs)
			throws ProcessingFailureException {

		Route_THolder ccList = new Route_THolder();

		subnetworkMgr.getRoute(sncName, includeHigherOrderCCs, ccList);

		return ccList.value;
	}

	/**
	 * Retrieve all managed elements using the given mgr.
	 * 
	 * @param mgr
	 *            mgr from which managed elements retrieved.
	 * @return ManagedElement_T[]
	 */
	public managedElement.ManagedElement_T[] retrieveAllManagedElements(MultiLayerSubnetworkMgr_I subnetworkMgr, NameAndStringValue_T[] subnetwrokName)
			throws ProcessingFailureException {
		int how_many = 50;

		java.util.Vector mes = new java.util.Vector();
		managedElement.ManagedElementList_THolder meList = new managedElement.ManagedElementList_THolder();
		managedElement.ManagedElementIterator_IHolder meIt = new managedElement.ManagedElementIterator_IHolder();

		subnetworkMgr.getAllManagedElements(subnetwrokName, how_many, meList, meIt);
		for (int i = 0; i < meList.value.length; i++) {
			mes.addElement(meList.value[i]);
		}

		if (meIt.value != null) {
			boolean hasMore;
			do {
				hasMore = meIt.value.next_n(how_many, meList);
				for (int i = 0; i < meList.value.length; i++) {
					mes.addElement(meList.value[i]);
				}
			} while (hasMore);

			try {
				meIt.value.destroy();
			} catch (Throwable ex) {
				// datalog.info("destory Iterator");
			}
		}

		managedElement.ManagedElement_T result[] = new managedElement.ManagedElement_T[mes.size()];
		mes.copyInto(result);

		return result;
	}

	public terminationPoint.TerminationPoint_T[] retrieveAllEdgePoints(MultiLayerSubnetworkMgr_I subnetworkMgr, NameAndStringValue_T[] subnetworkName,
			short[] tpLayerRateList, short[] connectionLayerRateList) throws ProcessingFailureException {
		terminationPoint.TerminationPointList_THolder tpList = new terminationPoint.TerminationPointList_THolder();
		terminationPoint.TerminationPointIterator_IHolder tpIt = new terminationPoint.TerminationPointIterator_IHolder();

		java.util.Vector emsTPVector = new java.util.Vector();

		subnetworkMgr.getAllEdgePoints(subnetworkName, tpLayerRateList, connectionLayerRateList, 50, tpList, tpIt);

		for (int i = 0; i < tpList.value.length; i++) {
			emsTPVector.addElement(tpList.value[i]);
		}

		if (null != tpIt.value) {
			boolean shouldContinue = true;
			while (shouldContinue) {
				shouldContinue = tpIt.value.next_n(50, tpList);

				for (int i = 0; i < tpList.value.length; i++)
					emsTPVector.addElement(tpList.value[i]);
			}

			try {
				tpIt.value.destroy();
			} catch (Throwable ex) {
				// datalog.info("destory Iterator");
			}
		}

		terminationPoint.TerminationPoint_T[] tps = new terminationPoint.TerminationPoint_T[emsTPVector.size()];
		emsTPVector.copyInto(tps);

		return tps;
	}

	private SubnetworkMgrHandler() {
	}

	// add by fanwenjie at 2006-09-22
	public NamingAttributesList_THolder retrieveAllSNCNames(MultiLayerSubnetworkMgr_I subnetworkMgr, NameAndStringValue_T[] subnetworkName,
			short[] layerRateList) throws ProcessingFailureException {

		NamingAttributesList_THolder sncNames = new NamingAttributesList_THolder();

		NamingAttributesList_THolder nameListHolder = new NamingAttributesList_THolder();
		NamingAttributesIterator_IHolder nameItHolder = new NamingAttributesIterator_IHolder();

		java.util.Vector nameVector = new java.util.Vector();

		subnetworkMgr.getAllSubnetworkConnectionNames(subnetworkName, layerRateList, 50, nameListHolder, nameItHolder);

		for (int i = 0; i < nameListHolder.value.length; i++) {
			nameVector.addElement(nameListHolder.value[i]);
		}

		if (null != nameItHolder.value) {
			boolean shouldContinue = true;
			while (shouldContinue) {
				shouldContinue = nameItHolder.value.next_n(50, nameListHolder);

				for (int i = 0; i < nameListHolder.value.length; i++)
					nameVector.addElement(nameListHolder.value[i]);
			}

			try {
				nameItHolder.value.destroy();
			} catch (Throwable ex) {
				// datalog.info("retrieveAllSNCNames:destory Iterator");

			}
		}

		sncNames.value = new globaldefs.NameAndStringValue_T[nameVector.size()][];
		nameVector.copyInto(sncNames.value);

		return sncNames;
	}

	public SubnetworkConnection_T retrieveSNCByName(MultiLayerSubnetworkMgr_I subnetworkMgr, NameAndStringValue_T[] sncName) throws ProcessingFailureException {

		SubnetworkConnection_THolder snc = new SubnetworkConnection_THolder();

		try {
			subnetworkMgr.getSNC(sncName, snc);
		} catch (org.omg.CORBA.TIMEOUT ex) {
			throw new globaldefs.ProcessingFailureException();
		}

		// datalog.debug("retrieveSNCByName::Get snc :" + snc.value);

		return snc.value;
	}

	public void supportedFunction(MultiLayerSubnetworkMgr_I subnetworkMgr) {
		CapabilityList_THolder capList = new CapabilityList_THolder();
		try {
			subnetworkMgr.getCapabilities(capList);
		} catch (ProcessingFailureException e) {
			e.printStackTrace();
		}
		// for (int i = 0; i < capList.value.length; i++) {
		// datalog.debug("The name of " + i + " function is " + capList.value[i].name);
		// datalog.debug("The value of" + i + " function is " + capList.value[i].value);
		// }

	}

	public MultiLayerSubnetwork_T[] retrieveAllSubordinateMLSNs(MultiLayerSubnetworkMgr_I subnetworkMgr, NameAndStringValue_T[] subnetwrokName)
			throws ProcessingFailureException {
		int how_many = 50;
		SubnetworkList_THolder subnetworkList = new SubnetworkList_THolder();
		SubnetworkIterator_IHolder subnetworkIterator = new SubnetworkIterator_IHolder();
		subnetworkMgr.getAllSubordinateMLSNs(subnetwrokName, how_many, subnetworkList, subnetworkIterator);

		Vector<MultiLayerSubnetwork_T> subnetworkVector = new Vector<MultiLayerSubnetwork_T>();
		for (MultiLayerSubnetwork_T subnetwork : subnetworkList.value) {
			subnetworkVector.add(subnetwork);
		}

		if (subnetworkIterator.value != null) {
			boolean hasMore;
			do {
				hasMore = subnetworkIterator.value.next_n(how_many, subnetworkList);
				for (MultiLayerSubnetwork_T subnetwork : subnetworkList.value) {
					subnetworkVector.add(subnetwork);
				}
			} while (hasMore);

			try {
				subnetworkIterator.value.destroy();
			} catch (Throwable ex) {
				// datalog.info("destory Iterator");
			}
		}

		MultiLayerSubnetwork_T result[] = new MultiLayerSubnetwork_T[subnetworkVector.size()];
		subnetworkVector.copyInto(result);

		return result;
	}

	public MultiLayerSubnetwork_T retrieveMultiLayerSubnetwork(MultiLayerSubnetworkMgr_I subnetworkMgr, NameAndStringValue_T[] subnetwrokName)
			throws ProcessingFailureException {
		MultiLayerSubnetwork_THolder subnetwork = new MultiLayerSubnetwork_THolder();
		subnetworkMgr.getMultiLayerSubnetwork(subnetwrokName, subnetwork);

		return subnetwork.value;
	}
}
