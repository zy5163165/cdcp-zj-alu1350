package org.asb.mule.probe.ptn.alu.sbi.mgrhandler;

import java.util.Vector;

import globaldefs.*;
import protection.*;
import trailNtwProtection.TrailNtwProtMgr_I;
import trailNtwProtection.TrailNtwProtectionIterator_IHolder;
import trailNtwProtection.TrailNtwProtectionList_THolder;
import trailNtwProtection.TrailNtwProtection_T;

public class ProtectionMgrHandler {
	private static ProtectionMgrHandler instance;

	public static ProtectionMgrHandler instance() {
		if (null == instance) {
			instance = new ProtectionMgrHandler();
		}
		return instance;
	}

	public TrailNtwProtection_T[] getAllTrailNtwProtections(TrailNtwProtMgr_I protectionMgr, NameAndStringValue_T[] meName) throws ProcessingFailureException {
		TrailNtwProtectionList_THolder emspgList = new TrailNtwProtectionList_THolder();
		TrailNtwProtectionIterator_IHolder emspgIt = new TrailNtwProtectionIterator_IHolder();

		int howmany = 1000;
		protectionMgr.getAllTrailNtwProtections(meName, howmany, emspgList, emspgIt);

		Vector<TrailNtwProtection_T> emspgVector = new Vector<TrailNtwProtection_T>();

		for (TrailNtwProtection_T pg : emspgList.value) {
			emspgVector.addElement(pg);
		}

		if (null != emspgIt.value) {
			boolean shouldContinue = true;
			while (shouldContinue) {
				shouldContinue = emspgIt.value.next_n(howmany, emspgList);

				for (TrailNtwProtection_T pg : emspgList.value) {
					emspgVector.addElement(pg);
				}
			}

			try {
				emspgIt.value.destroy();
			} catch (Throwable ex) {

			}
		}

		TrailNtwProtection_T[] protectionGroups = new TrailNtwProtection_T[emspgVector.size()];
		emspgVector.copyInto(protectionGroups);

		return protectionGroups;

	}

	public ProtectionGroup_T[] retrieveAllProtectionGroups(ProtectionMgr_I protectionMgr, NameAndStringValue_T[] meName) throws ProcessingFailureException {
		ProtectionGroupList_THolder emspgList = new ProtectionGroupList_THolder();
		ProtectionGroupIterator_IHolder emspgIt = new ProtectionGroupIterator_IHolder();

		protectionMgr.getAllProtectionGroups(meName, 50, emspgList, emspgIt);

		java.util.Vector emspgVector = new java.util.Vector();

		for (int i = 0; i < emspgList.value.length; i++) {
			emspgVector.addElement(emspgList.value[i]);
		}

		if (null != emspgIt.value) {
			boolean shouldContinue = true;
			while (shouldContinue) {
				shouldContinue = emspgIt.value.next_n(50, emspgList);

				for (int i = 0; i < emspgList.value.length; i++)
					emspgVector.addElement(emspgList.value[i]);
			}

			try {
				emspgIt.value.destroy();
			} catch (Throwable ex) {

			}
		}

		ProtectionGroup_T[] protectionGroups = new ProtectionGroup_T[emspgVector.size()];
		emspgVector.copyInto(protectionGroups);

		return protectionGroups;

	}

	public SwitchData_T[] retrieveAllSwitchDatas(ProtectionMgr_I protectionMgr, NameAndStringValue_T[] pgName) throws ProcessingFailureException {
		SwitchDataList_THolder emssdList = new SwitchDataList_THolder();

		protectionMgr.retrieveSwitchData(pgName, emssdList);

		return emssdList.value;

	}

	public ProtectionGroup_T retrieveProtectionGroup(ProtectionMgr_I protectionMgr, NameAndStringValue_T[] meName) throws ProcessingFailureException {
		ProtectionGroup_THolder protecetGroup = new ProtectionGroup_THolder();

		protectionMgr.getProtectionGroup(meName, protecetGroup);

		return protecetGroup.value;
	}

	private ProtectionMgrHandler() {
	}
}
