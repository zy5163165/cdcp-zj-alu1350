package org.asb.mule.probe.ptn.alu.sbi.mgrhandler;

import java.util.Vector;

import topologicalLink.TopologicalLinkIterator_IHolder;
import topologicalLink.TopologicalLinkList_THolder;
import topologicalLink.TopologicalLink_T;
import extendServiceMgr.ExtendServiceMgr_I;
import globaldefs.NameAndStringValue_T;
import globaldefs.ProcessingFailureException;

/**
 * Handler class for using EMSMgr .
 */
public class ExtendServiceMgrHandler {
	private static ExtendServiceMgrHandler instance;

	public static ExtendServiceMgrHandler instance() {
		if (null == instance)
			instance = new ExtendServiceMgrHandler();

		return instance;
	}

	public TopologicalLink_T[] retrieveTopologicalLinksOfFDFr(ExtendServiceMgr_I extendServiceMgr, NameAndStringValue_T[] fdfrName)
			throws ProcessingFailureException {

		TopologicalLinkList_THolder topoList = new TopologicalLinkList_THolder();
		TopologicalLinkIterator_IHolder topoIt = new TopologicalLinkIterator_IHolder();

		Vector<TopologicalLink_T> topoVector = new Vector<TopologicalLink_T>();
		int how_many = 50;
		extendServiceMgr.getTopologicalLinksOfFDFr(fdfrName, how_many, topoList, topoIt);

		for (TopologicalLink_T topo : topoList.value) {
			topoVector.addElement(topo);
		}

		if (null != topoIt.value) {
			boolean shouldContinue = true;
			while (shouldContinue) {
				shouldContinue = topoIt.value.next_n(50, topoList);
				for (TopologicalLink_T topo : topoList.value) {
					topoVector.addElement(topo);
				}
			}
			try {
				topoIt.value.destroy();
			} catch (Throwable ex) {

			}
		}

		TopologicalLink_T[] toplinks = new TopologicalLink_T[topoVector.size()];
		topoVector.copyInto(toplinks);

		return toplinks;
	}

	private ExtendServiceMgrHandler() {
	}

}
