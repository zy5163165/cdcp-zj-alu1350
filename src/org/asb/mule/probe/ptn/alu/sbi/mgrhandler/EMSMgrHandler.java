package org.asb.mule.probe.ptn.alu.sbi.mgrhandler;

import globaldefs.*;
import topologicalLink.*;
import emsMgr.*;
import multiLayerSubnetwork.*;

import java.util.Vector;

/**
 * Handler class for using EMSMgr .
 */
public class EMSMgrHandler {
	private static EMSMgrHandler instance;

	public static EMSMgrHandler instance() {
		if (null == instance)
			instance = new EMSMgrHandler();

		return instance;
	}

	public MultiLayerSubnetwork_T[] retrieveAllTopLevelSubnetworks(EMSMgr_I emsMgr) throws ProcessingFailureException {

		SubnetworkList_THolder emsSubNEList = new SubnetworkList_THolder();
		SubnetworkIterator_IHolder emsSubNEIt = new SubnetworkIterator_IHolder();

		Vector emsSubNEVector = new Vector();

		emsMgr.getAllTopLevelSubnetworks(50, emsSubNEList, emsSubNEIt);

		for (int i = 0; i < emsSubNEList.value.length; i++) {
			emsSubNEVector.addElement(emsSubNEList.value[i]);
		}

		if (null != emsSubNEIt.value) {
			boolean shouldContinue = true;
			while (shouldContinue) {
				shouldContinue = emsSubNEIt.value.next_n(50, emsSubNEList);

				for (int i = 0; i < emsSubNEList.value.length; i++)
					emsSubNEVector.addElement(emsSubNEList.value[i]);
			}
			try {
				emsSubNEIt.value.destroy();
			} catch (Throwable ex) {

			}
		}
		// datalog.debug("retrieveAllTopLevelSubnetworks: " + emsSubNEVector.size());
		MultiLayerSubnetwork_T[] subnetworks = new MultiLayerSubnetwork_T[emsSubNEVector.size()];
		emsSubNEVector.copyInto(subnetworks);

		return subnetworks;
	}

	public NameAndStringValue_T[][] retrieveAllTopLevelSubnetworkNames(EMSMgr_I emsMgr) throws ProcessingFailureException {

		NamingAttributesList_THolder nameList = new NamingAttributesList_THolder();
		NamingAttributesIterator_IHolder nameIt = new NamingAttributesIterator_IHolder();

		Vector<NameAndStringValue_T[]> emsSubNEVector = new Vector<NameAndStringValue_T[]>();

		emsMgr.getAllTopLevelSubnetworkNames(50, nameList, nameIt);

		for (int i = 0; i < nameList.value.length; i++) {
			emsSubNEVector.addElement(nameList.value[i]);
		}

		if (null != nameIt.value) {
			boolean shouldContinue = true;
			while (shouldContinue) {
				shouldContinue = nameIt.value.next_n(50, nameList);

				for (int i = 0; i < nameList.value.length; i++)
					emsSubNEVector.addElement(nameList.value[i]);
			}
			try {
				nameIt.value.destroy();
			} catch (Throwable ex) {

			}
		}
		NameAndStringValue_T[][] subnetworks = new NameAndStringValue_T[emsSubNEVector.size()][];
		emsSubNEVector.copyInto(subnetworks);

		return subnetworks;
	}

	public TopologicalLink_T[] retrieveAllTopLevelTopologicalLinks(EMSMgr_I emsMgr) throws ProcessingFailureException {

		TopologicalLinkList_THolder emsTopLinkNEList = new TopologicalLinkList_THolder();
		TopologicalLinkIterator_IHolder emsTopLinkIt = new TopologicalLinkIterator_IHolder();

		Vector emsTopLinkVector = new Vector();

		emsMgr.getAllTopLevelTopologicalLinks(50, emsTopLinkNEList, emsTopLinkIt);

		for (int i = 0; i < emsTopLinkNEList.value.length; i++) {
			emsTopLinkVector.addElement(emsTopLinkNEList.value[i]);
		}

		if (null != emsTopLinkIt.value) {
			boolean shouldContinue = true;
			while (shouldContinue) {
				shouldContinue = emsTopLinkIt.value.next_n(50, emsTopLinkNEList);

				for (int i = 0; i < emsTopLinkNEList.value.length; i++)
					emsTopLinkVector.addElement(emsTopLinkNEList.value[i]);
			}
			try {
				emsTopLinkIt.value.destroy();
			} catch (Throwable ex) {

			}
		}

		TopologicalLink_T[] toplinks = new TopologicalLink_T[emsTopLinkVector.size()];
		emsTopLinkVector.copyInto(toplinks);

		return toplinks;
	}

	public org.omg.CosNotification.StructuredEvent[] retrieveAllActiveAlarms(EMSMgr_I emsMgr, String[] excludeProbCauseList,
			notifications.PerceivedSeverity_T[] excludeSeverity) throws ProcessingFailureException {
		notifications.EventList_THolder eventList = new notifications.EventList_THolder();
		notifications.EventIterator_IHolder eventIt = new notifications.EventIterator_IHolder();

		// gj.util.Vector<org.omg.CosNotification.StructuredEvent> eventVector = new gj.util.Vector<org.omg.CosNotification.StructuredEvent>();
		Vector eventVector = new Vector();

		// datalog.info("Now we are going to retrieve alarms from EMS.");
		emsMgr.getAllEMSAndMEActiveAlarms(excludeProbCauseList, excludeSeverity, 50, eventList, eventIt);
		// datalog.info("Succeeded to retrieve alarms from EMS.");

		for (int i = 0; i < eventList.value.length; i++) {
			eventVector.addElement(eventList.value[i]);
		}
		// datalog.info("Alarms returned from the first call are added into data vector.");

		if (null != eventIt.value) {
			boolean shouldContinue = true;
			// datalog.debug("There are " + eventIt.value.getLength() + " alarms remained to be returned from iterator");

			while (shouldContinue) {
				shouldContinue = eventIt.value.next_n(50, eventList);

				// datalog.debug("" + eventList.value.length + " alarms returned from iterator");

				for (int i = 0; i < eventList.value.length; i++)
					eventVector.addElement(eventList.value[i]);

				// datalog.info("Alarms returned from iterator are added into data vector.");
			}
			try {
				eventIt.value.destroy();
			} catch (Throwable ex) {

			}
		}

		// datalog.debug("Totally " + eventVector.size() + " alarms were synchronized and now we try to return them");
		org.omg.CosNotification.StructuredEvent[] alarms = new org.omg.CosNotification.StructuredEvent[eventVector.size()];
		eventVector.copyInto(alarms);
		// datalog.debug("Return the retrieved alarms to caller.");

		return alarms;
	}

	private EMSMgrHandler() {
	}

}
