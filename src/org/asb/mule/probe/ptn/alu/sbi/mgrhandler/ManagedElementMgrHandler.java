package org.asb.mule.probe.ptn.alu.sbi.mgrhandler;

import java.util.Vector;

import globaldefs.NameAndStringValue_T;
import globaldefs.NamingAttributesIterator_IHolder;
import globaldefs.NamingAttributesList_THolder;
import globaldefs.ProcessingFailureException;
import managedElement.ManagedElementIterator_IHolder;
import managedElement.ManagedElementList_THolder;
import managedElement.ManagedElement_T;
import managedElement.ManagedElement_THolder;
import managedElementManager.ManagedElementMgr_I;
import subnetworkConnection.CCIterator_IHolder;
import subnetworkConnection.CrossConnectList_THolder;
import subnetworkConnection.CrossConnect_T;
import terminationPoint.GTP_T;
import terminationPoint.GTPiterator_IHolder;
import terminationPoint.GTPlist_THolder;
import terminationPoint.TerminationPointIterator_IHolder;
import terminationPoint.TerminationPointList_THolder;
import terminationPoint.TerminationPoint_T;

/**
 * Handler class for using ManagedElementMgr_I object.
 */
public class ManagedElementMgrHandler {
	private static ManagedElementMgrHandler instance;

	public static ManagedElementMgrHandler instance() {
		if (null == instance)
			instance = new ManagedElementMgrHandler();
		return instance;
	}

	/**
	 * Retrieve all managed elements using the given mgr.
	 * 
	 * @param mgr
	 *            mgr from which managed elements retrieved.
	 * @return ManagedElement_T[]
	 */
	public ManagedElement_T[] retrieveAllManagedElements(ManagedElementMgr_I mgr) throws globaldefs.ProcessingFailureException {
		int how_many = 50;

		java.util.Vector mes = new java.util.Vector();
		ManagedElementList_THolder meList = new ManagedElementList_THolder();
		ManagedElementIterator_IHolder meIt = new ManagedElementIterator_IHolder();

		mgr.getAllManagedElements(how_many, meList, meIt);
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

			}
		}

		ManagedElement_T[] result = new ManagedElement_T[mes.size()];
		mes.copyInto(result);
		// for(ManagedElement_T ne:result )
		// {
		// datalog.info(ne.toString());
		// }

		return result;
	}

	public NameAndStringValue_T[][] retrieveAllManagedElementNames(ManagedElementMgr_I mgr) throws globaldefs.ProcessingFailureException {
		int how_many = 50;

		java.util.Vector mes = new java.util.Vector();
		NamingAttributesList_THolder meList = new NamingAttributesList_THolder();
		NamingAttributesIterator_IHolder meIt = new NamingAttributesIterator_IHolder();

		mgr.getAllManagedElementNames(how_many, meList, meIt);
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

			}
		}

		NameAndStringValue_T[][] result = new NameAndStringValue_T[mes.size()][];
		mes.copyInto(result);

		return result;
	}

	/**
	 * Retrieve one managed elements using the given meName.
	 * 
	 * @param mgr
	 *            ,neName mgr from which managed elements retrieved.
	 * @return ManagedElement_T
	 */
	public ManagedElement_T retrieveManagedElement(ManagedElementMgr_I mgr, globaldefs.NameAndStringValue_T[] vendorMeName)
			throws globaldefs.ProcessingFailureException {
		ManagedElement_THolder me = new ManagedElement_THolder();

		mgr.getManagedElement(vendorMeName, me);

		return me.value;
	}

	public TerminationPoint_T[] retrieveAllPTPs(ManagedElementMgr_I mgr, NameAndStringValue_T[] managedElementName, short[] tpLayerRateList,
			short[] connectionLayerRateList) throws ProcessingFailureException {
		int how_many = 50;

		Vector<TerminationPoint_T> tps = new Vector<TerminationPoint_T>();
		TerminationPointList_THolder tpList = new TerminationPointList_THolder();
		TerminationPointIterator_IHolder tpIt = new TerminationPointIterator_IHolder();

		mgr.getAllPTPs(managedElementName, tpLayerRateList, connectionLayerRateList, how_many, tpList, tpIt);
		for (TerminationPoint_T tp : tpList.value) {
			tps.addElement(tp);
		}

		if (tpIt.value != null) {
			boolean hasMore;
			do {
				hasMore = tpIt.value.next_n(how_many, tpList);
				for (TerminationPoint_T tp : tpList.value) {
					tps.addElement(tp);
				}
			} while (hasMore);

			try {
				tpIt.value.destroy();
			} catch (Throwable ex) {

			}
		}

		TerminationPoint_T result[] = new TerminationPoint_T[tps.size()];
		tps.copyInto(result);

		return result;
	}

	public TerminationPoint_T[] retrieveAllFTPs(ManagedElementMgr_I mgr, NameAndStringValue_T[] managedElementName, short[] tpLayerRateList,
			short[] connectionLayerRateList) throws ProcessingFailureException {
		int how_many = 50;

		Vector<TerminationPoint_T> tps = new Vector<TerminationPoint_T>();
		TerminationPointList_THolder tpList = new TerminationPointList_THolder();
		TerminationPointIterator_IHolder tpIt = new TerminationPointIterator_IHolder();

		mgr.getAllFTPs(managedElementName, tpLayerRateList, connectionLayerRateList, how_many, tpList, tpIt);
		for (TerminationPoint_T tp : tpList.value) {
			tps.addElement(tp);
		}

		if (tpIt.value != null) {
			boolean hasMore;
			do {
				hasMore = tpIt.value.next_n(how_many, tpList);
				for (TerminationPoint_T tp : tpList.value) {
					tps.addElement(tp);
				}
			} while (hasMore);

			try {
				tpIt.value.destroy();
			} catch (Throwable ex) {

			}
		}

		TerminationPoint_T result[] = new TerminationPoint_T[tps.size()];
		tps.copyInto(result);

		return result;
	}

	public GTP_T[] retrieveAllGTPs(ManagedElementMgr_I mgr, NameAndStringValue_T[] managedElementName, short[] tpLayerRateList)
			throws ProcessingFailureException {
		int how_many = 50;

		GTPlist_THolder tpList = new GTPlist_THolder();
		GTPiterator_IHolder tpIt = new GTPiterator_IHolder();
		Vector<GTP_T> tps = new Vector<GTP_T>();

		mgr.getAllGTPs(managedElementName, tpLayerRateList, how_many, tpList, tpIt);
		for (GTP_T tp : tpList.value) {
			tps.addElement(tp);
		}

		if (tpIt.value != null) {
			boolean hasMore;
			do {
				hasMore = tpIt.value.next_n(how_many, tpList);
				for (GTP_T tp : tpList.value) {
					tps.addElement(tp);
				}
			} while (hasMore);

			try {
				tpIt.value.destroy();
			} catch (Throwable ex) {

			}
		}

		GTP_T result[] = new GTP_T[tps.size()];
		tps.copyInto(result);

		return result;
	}

	public TerminationPoint_T[] retrieveContainingTPs(ManagedElementMgr_I mgr, NameAndStringValue_T[] tpName) throws ProcessingFailureException {

		TerminationPointList_THolder tpList = new TerminationPointList_THolder();

		mgr.getContainingTPs(tpName, tpList);

		return tpList.value;
	}

	public TerminationPoint_T[] retrieveContainedInUseTPs(ManagedElementMgr_I mgr, NameAndStringValue_T[] tpName, short[] layerRateList)
			throws ProcessingFailureException {
		int how_many = 50;

		java.util.Vector tps = new java.util.Vector();
		TerminationPointList_THolder tpList = new TerminationPointList_THolder();
		TerminationPointIterator_IHolder tpIt = new TerminationPointIterator_IHolder();

		try {
			mgr.getContainedInUseTPs(tpName, layerRateList, how_many, tpList, tpIt);
			// mgr.getContainedCurrentTPs(tpName, layerRateList, how_many, tpList, tpIt);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ProcessingFailureException();
		}

		for (int i = 0; i < tpList.value.length; i++) {
			tps.addElement(tpList.value[i]);
		}

		if (tpIt.value != null) {
			boolean hasMore;
			do {
				hasMore = tpIt.value.next_n(how_many, tpList);
				for (int i = 0; i < tpList.value.length; i++) {
					tps.addElement(tpList.value[i]);
				}
			} while (hasMore);
			try {
				tpIt.value.destroy();
			} catch (Throwable ex) {

			}
		}

		TerminationPoint_T result[] = new TerminationPoint_T[tps.size()];
		tps.copyInto(result);

		return result;
	}

	public TerminationPoint_T[] retrieveContainedPotentialTPs(ManagedElementMgr_I mgr, NameAndStringValue_T[] tpName, short[] layerRateList)
			throws ProcessingFailureException {
		int how_many = 50;

		java.util.Vector tps = new java.util.Vector();
		TerminationPointList_THolder tpList = new TerminationPointList_THolder();
		TerminationPointIterator_IHolder tpIt = new TerminationPointIterator_IHolder();

		try {
			mgr.getContainedPotentialTPs(tpName, layerRateList, how_many, tpList, tpIt);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ProcessingFailureException();
		}
		for (int i = 0; i < tpList.value.length; i++) {
			tps.addElement(tpList.value[i]);
		}

		if (tpIt.value != null) {
			boolean hasMore;
			do {
				hasMore = tpIt.value.next_n(how_many, tpList);
				for (int i = 0; i < tpList.value.length; i++) {
					tps.addElement(tpList.value[i]);
				}
			} while (hasMore);
			try {
				tpIt.value.destroy();
			} catch (Throwable ex) {

			}
		}

		TerminationPoint_T result[] = new TerminationPoint_T[tps.size()];
		tps.copyInto(result);

		return result;
	}

	public TerminationPoint_T[] retrieveContainedCurrentTPs(ManagedElementMgr_I mgr, NameAndStringValue_T[] tpName, short[] layerRateList)
			throws ProcessingFailureException {
		int how_many = 50;

		java.util.Vector tps = new java.util.Vector();
		TerminationPointList_THolder tpList = new TerminationPointList_THolder();
		TerminationPointIterator_IHolder tpIt = new TerminationPointIterator_IHolder();

		// test
		// datalog.debug("layerRateList.length =" + layerRateList.length);
		// int len = layerRateList.length;
		// for (int i = 0; i < len; i++)
		// datalog.debug("layerRateList	: " + layerRateList[i]);

		// len = tpName.length;
		// for (int i = 0; i < len; i++)
		// datalog.debug("retrieveContainedCurrentTPs	name: " + tpName[i].name + "=" + tpName[i].value);
		// test end

		try {
			mgr.getContainedCurrentTPs(tpName, layerRateList, how_many, tpList, tpIt);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ProcessingFailureException();
		}
		for (int i = 0; i < tpList.value.length; i++) {
			tps.addElement(tpList.value[i]);
		}

		if (tpIt.value != null) {
			boolean hasMore;
			do {
				hasMore = tpIt.value.next_n(how_many, tpList);

				for (int i = 0; i < tpList.value.length; i++) {
					tps.addElement(tpList.value[i]);
				}
			} while (hasMore);

			try {
				tpIt.value.destroy();
			} catch (Throwable ex) {

			}
		}
		// datalog.debug("retrieveContainedTPs: tps.size() =" + tps.size());
		TerminationPoint_T result[] = new TerminationPoint_T[tps.size()];
		tps.copyInto(result);

		return result;
	}

	public CrossConnect_T[] retrieveAllCrossConnections(ManagedElementMgr_I mgr, NameAndStringValue_T[] meName, short[] connectionRateList)
			throws ProcessingFailureException {
		int how_many = 50;

		java.util.Vector ccs = new java.util.Vector();
		CrossConnectList_THolder ccList = new CrossConnectList_THolder();
		CCIterator_IHolder ccIt = new CCIterator_IHolder();

		mgr.getAllCrossConnections(meName, connectionRateList, how_many, ccList, ccIt);
		for (int i = 0; i < ccList.value.length; i++) {
			ccs.addElement(ccList.value[i]);
		}

		if (ccIt.value != null) {
			boolean hasMore;
			do {
				hasMore = ccIt.value.next_n(how_many, ccList);

				for (int i = 0; i < ccList.value.length; i++) {
					ccs.addElement(ccList.value[i]);
				}
			} while (hasMore);

			try {
				ccIt.value.destroy();
			} catch (Throwable ex) {

			}
		}

		CrossConnect_T result[] = new CrossConnect_T[ccs.size()];
		ccs.copyInto(result);

		return result;
	}

	public org.omg.CosNotification.StructuredEvent[] retrieveAllActiveAlarms(ManagedElementMgr_I meMgr, NameAndStringValue_T[] meName,
			String[] excludeProbCauseList, notifications.PerceivedSeverity_T[] excludeSeverity) throws ProcessingFailureException {

		notifications.EventList_THolder eventList = new notifications.EventList_THolder();
		notifications.EventIterator_IHolder eventIt = new notifications.EventIterator_IHolder();

		java.util.Vector eventVector = new java.util.Vector();
		meMgr.getAllActiveAlarms(meName, excludeProbCauseList, excludeSeverity, 50, eventList, eventIt);

		for (int i = 0; i < eventList.value.length; i++) {
			eventVector.addElement(eventList.value[i]);
		}

		if (null != eventIt.value) {
			boolean shouldContinue = true;
			while (shouldContinue) {
				shouldContinue = eventIt.value.next_n(50, eventList);

				for (int i = 0; i < eventList.value.length; i++)
					eventVector.addElement(eventList.value[i]);
			}

			try {
				eventIt.value.destroy();
			} catch (Throwable ex) {

			}
		}

		org.omg.CosNotification.StructuredEvent[] alarms = new org.omg.CosNotification.StructuredEvent[eventVector.size()];
		eventVector.copyInto(alarms);

		return alarms;
	}

	private ManagedElementMgrHandler() {
	}

}
