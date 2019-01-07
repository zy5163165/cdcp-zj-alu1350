package org.asb.mule.probe.ptn.alu.sbi;

import java.io.File;

import maintenanceOps.MaintenanceMgr_IHelper;
import managedElementManager.ManagedElementMgr_I;
import managedElementManager.ManagedElementMgr_IHelper;
import multiLayerSubnetwork.MultiLayerSubnetworkMgr_I;
import multiLayerSubnetwork.MultiLayerSubnetworkMgr_IHelper;
import nmsSession.NmsSession_IPOA;

import org.asb.mule.probe.framework.util.FileLogger;
import org.asb.mule.probe.framework.util.corba.CorbaMgr;
//import org.asb.mule.probe.ptn.alu.sbi.event.ObjectCreationNotification;
import org.omg.CosNotification.EventType;
import org.omg.CosNotification.StructuredEvent;
import org.omg.CosNotifyChannelAdmin.ClientType;
import org.omg.CosNotifyChannelAdmin.ConsumerAdmin;
import org.omg.CosNotifyChannelAdmin.EventChannelHolder;
import org.omg.CosNotifyChannelAdmin.StructuredProxyPushSupplier;
import org.omg.CosNotifyChannelAdmin.StructuredProxyPushSupplierHelper;
import org.omg.CosNotifyComm.StructuredPushConsumerHelper;
import org.omg.CosNotifyComm.StructuredPushConsumerOperations;
import org.omg.CosNotifyComm.StructuredPushConsumerPOATie;
import org.omg.PortableServer.POA;

import session.Session_I;
import trailNtwProtection.TrailNtwProtMgr_I;
import trailNtwProtection.TrailNtwProtMgr_IHelper;

import common.Common_IHolder;

import emsMgr.EMSMgr_I;
import emsMgr.EMSMgr_IHelper;
import emsSession.EmsSession_I;
import emsSession.EmsSession_IPackage.managerNames_THolder;
import equipment.EquipmentInventoryMgr_I;
import equipment.EquipmentInventoryMgr_IHelper;
import extendServiceMgr.ExtendServiceMgr_I;
import extendServiceMgr.ExtendServiceMgr_IHelper;
import flowDomain.FlowDomainMgr_IHelper;
import globaldefs.ProcessingFailureException;

/**
 * Implementation for interface nmsSession_I.
 */
public class NmsSession extends NmsSession_IPOA implements StructuredPushConsumerOperations {
	// protected Logger sbilog = ProbeLog.getInstance().getSbiLog();
	// protected Logger errorlog = ProbeLog.getInstance().getErrorLog();
	// protected Logger eventlog = ProbeLog.getInstance().getEventLog();
	private FileLogger sbilog = null;
	private FileLogger errorlog = null;
	private FileLogger eventlog = null;
	private static final String head1 = "NmsSession::";

	//
	// Name of the mgr objects.Fenghuo TMF 3.5
	/**
	 * * <li>"EMS" (mandatory) <li>"ManagedElement" (mandatory) <li>"MultiLayerSubnetwork" (mandatory) <li>"TrafficDescriptor" <li>"PerformanceManagement" <li>
	 * "Protection" <li>"EquipmentInventory" <li>"Maintenance" <li>"softwareAndDataManager" <li>"transmissionDescriptor" <li>"GuiCutThrough" (mandatory) <li>
	 * "FlowDomain" <li>"MultiLayerSNPPLink" <li>"TrafficConditioningProfile" <li>"MLSNPP"
	 */
	private static final String NameOfManagedElement = "ManagedElement";
	private static final String NameOfEquipmentInventory = "EquipmentInventory";
	private static final String NameOfEmsMgr = "EMS";
	private static final String NameOfMultiLayerSubnetwork = "MultiLayerSubnetwork";
	private static final String NameOfProtection = "Protection";
	private static final String NameOfPerformance = "PerformanceManagement";
	private static final String NameOfFdr = "FlowDomain";
	private static final String NameOfExtendService = "ExtendService";

	private static final String NameOfMaintenance = "Maintenance";
	private static final String NameOfTrailNtwProtMgr = "TrailNtwProt";

	private EMSMgr_I emsMgr = null;
	private EquipmentInventoryMgr_I equipmentMgr = null;
	private ManagedElementMgr_I managedElementMgr = null;
	private MultiLayerSubnetworkMgr_I subnetworkMgr = null;
	private protection.ProtectionMgr_I protectionMgr = null;
	private TrailNtwProtMgr_I trailNtwProtMgr = null;
	private performance.PerformanceManagementMgr_I performanceMgr = null;
	private maintenanceOps.MaintenanceMgr_I maintenanceMgr = null;
	private flowDomain.FlowDomainMgr_I flowDomainMgr = null;
	private ExtendServiceMgr_I extendServiceMgr = null;

	private StructuredPushConsumerPOATie consumerTie = null;

	// object id of StructuredPushConsumer object. assigned by poa.
	private byte[] consumerObjectId;

	// proxy supplier used to receive notifications.
	private StructuredProxyPushSupplier proxySupplier;

	private void slog(String logMsg) {
		System.out.println("NmsSession::" + logMsg);
		sbilog.info("NmsSession::" + logMsg);
	}

	private String iorFile = "temp.ior";

	public NmsSession() {

	}

	public NmsSession(FileLogger sbilog, FileLogger errorlog, FileLogger eventlog) {
		this.sbilog = sbilog;
		this.errorlog = errorlog;
		this.eventlog = eventlog;
	}

	//
	// =====================================================================
	// IDL implementation methods, from NmsSession_I
	// =====================================================================
	//

	/**
	 * @parm startTime: The time of the first notification lost.
	 * @parm notificationId: The notificationId of the first notification lost.
	 **/
	public void eventLossOccurred(String startTime, String notificationId) {
		sbilog.info("eventLossOccurred>>	........");

	}

	/**
	 * @parm endTime: The time of the end of the event loss period, as determined by the EMS.
	 **/
	public void eventLossCleared(String endTime) {
		sbilog.info("eventLossOccurred>>	 ........");

	}

	/**
	 * readonly attribute Session_I associatedSession;
	 */
	public Session_I associatedSession() {
		return emsSession;
	}

	/**
	 * <p>
	 * Allows for the detection of loss of communication. It is implementation specific to differenciate intermittent problems from loss of connection.
	 * </p>
	 **/
	public void ping() {

		sbilog.info("ping>>	ping event...");

	}

	/**
	 * <p>
	 * Allows for a controlled disconnect between parties. All resources allocated for the session are deleted by operation.
	 * </p>
	 **/
	public void endSession() {
		String head2 = "endSession: ";

		sbilog.info("endSession>>	" + head1 + head2 + "Session ended.");

	}

	/**
	 * The cousumer was asked to disconnect. We consider this calling as a signal to end the session.
	 */
	public void disconnect_structured_push_consumer() {
		sbilog.info("disconnect_structured_push_consumer>>	" + head1 + " disconnect_structured_push_consumer.");
	}

	/**
	 * Dispath the notifications to specific listener based on the event type of notification.
	 * 
	 * @param notification
	 */
	public void push_structured_event(StructuredEvent notification) {

		String eventType = notification.header.fixed_header.event_type.type_name;
		eventlog.info("push_structured_event>>	=================================");

		eventlog.info("push_structured_event>>	Got notification of type: " + eventType);

		if (eventType.equals("NT_OBJECT_CREATION")) {
			eventlog.info("push_structured_event>>	NT_OBJECT_CREATION EVENT==========");
		//	eventlog.info(new ObjectCreationNotification(notification));
		} else if (eventType.equals("NT_ALARM")) {
			eventlog.info("push_structured_event>>	NT_ALARM EVENT==========");
		} else if (eventType.equals("NT_HEARTBEAT")) {
			eventlog.info("push_structured_event>>	NT_HEARTBEAT EVENT==========");
		} else if (eventType.equals("NT_FILE_TRANSFER_STATUS")) {
			eventlog.info("push_structured_event>>	NT_FILE_TRANSFER_STATUS EVENT==========");
		} else if (eventType.equals("NT_OBJECT_DELETION")) {
			eventlog.info("push_structured_event>>	NT_OBJECT_DELETION EVENT==========");
		} else if (eventType.equals("NT_ATTRIBUTE_VALUE_CHANGE")) {
			eventlog.info("push_structured_event>>	NT_ATTRIBUTE_VALUE_CHANGE EVENT==========");
		} else if (eventType.equals("NT_STATE_CHANGE")) {
			eventlog.info("push_structured_event>>	NT_STATE_CHANGE EVENT==========");
		} else if (eventType.equals("NT_ROUTE_CHANGE")) {
			eventlog.info("push_structured_event>>	NT_ROUTE_CHANGE EVENT==========");
		} else if (eventType.equals("NT_PROTECTION_SWITCH")) {
			eventlog.info("push_structured_event>>	NT_PROTECTION_SWITCH EVENT==========");
		} else if (eventType.equals("NT_TCA")) {
			eventlog.info("push_structured_event>>	NT_TCA EVENT==========");
		} else {
			eventlog.info("push_structured_event>>	UNKNOWN EVENT==========");
		}
	}

	public void offer_change(EventType[] added, EventType[] removed) {
		sbilog.info("offer_change>>	start...");
	}

	/**
	 * Shut down this session.
	 * <P>
	 * Main job in shutdown is to release the resources, including: disconnect the supplier; deactivate this NmsSession object. deactivate the
	 * StructuredPushConsumer object.
	 * </P>
	 */
	public void shutdownSession() {
		// send signal to end the ems session.
		try {
			// 'endSession' is defined as oneway operation, so the calling won't hang NmsSession_I and emsSession object even if
			// we enter this procedure from 'endSession' or 'disconnect_structured_event_consumer'.
			if (emsSession != null)
				emsSession.endSession();
			sbilog.info("shutdownSession>>	We ask ems session to be ended");
		} catch (Throwable e) {
			sbilog.info("shutdownSession>>	Exception: Failed to ask ems session be enede, detail:" + e);
		}

		// Disconnect supplier.
		try {
			proxySupplier.disconnect_structured_push_supplier();
			sbilog.info("shutdownSession>>	Proxy supplier disconnected");
		} catch (Throwable e) {
			sbilog.info("shutdownSession>>	Exception: Failed to disconnect push supplier, detail: " + e);
		}

		if (_poa != null) {

			try {
				_poa.deactivate_object(_poa.servant_to_id(this));
				sbilog.info("shutdownSession>>	NmsSession_I object was deactivated");
			} catch (Throwable e) {
				sbilog.info("shutdownSession>>	Exception: Failed to deactivate the NmsSession_I,detail: " + e);
			}

			// Deactivate this StructuredPushConsumer object.
			try {
				_poa.deactivate_object(consumerObjectId);
				sbilog.info("shutdownSession>>	Consumer object was deactivated");
			} catch (Throwable e) {
				sbilog.info("shutdownSession>>	Exception: Failed to deac tivate the consumer,detail: " + e);
			}

			try {
				_poa.destroy(false, false);
				sbilog.info("shutdownSession>>	nmsSessionPOA destoryed!");

				if (_poa != null) {
					sbilog.info("shutdownSession>>	after _nmsSessionPOA destoryed, set _nmsSessionPOA=null");
					_poa = null;
				}
			} catch (Exception ex) {
				sbilog.info("shutdownSession>>	Exception: Can not destory _nmsSessionPOA! " + ex);
			}
		}
		// Has disconnected so delete the iorFile.
		File file = new File(iorFile);
		file.delete();

		sbilog.info("shutdownSession>>	Session was shutdown");
	}

	/**
	 * Whether the associated ems session is in good status.
	 * 
	 * @param boolean
	 */
	public boolean isEmsSessionOK() {
		try {
			emsSession.ping();
			return true;
		} catch (Throwable e) {
			sbilog.info("isEmsSessionOK>>	Failed to ping ems session:" + e);
			sbilog.error("isEmsSessionOK>>	Failed to ping ems session:" + e, e);
			errorlog.error("isEmsSessionOK>>	Failed to ping ems session:" + e, e);
		}

		return false;
	}

	/**
	 * Get ManagedElementMgr_I object.
	 */
	public ManagedElementMgr_I getManagedElementMgr() throws ProcessingFailureException {
		if (managedElementMgr == null) {
			managedElementMgr = ManagedElementMgr_IHelper.narrow(getManager(NameOfManagedElement));
		}
		return managedElementMgr;
	}

	/**
	 * Get Equipment inventory mgr.
	 */
	public EquipmentInventoryMgr_I getEquipmentInventoryMgr() throws ProcessingFailureException {
		if (equipmentMgr == null)
			equipmentMgr = EquipmentInventoryMgr_IHelper.narrow(getManager(NameOfEquipmentInventory));
		return equipmentMgr;
	}

	public EMSMgr_I getEmsMgr() throws ProcessingFailureException {
		if (emsMgr == null)
			emsMgr = EMSMgr_IHelper.narrow(getManager(NameOfEmsMgr));
		return emsMgr;

	}

	public MultiLayerSubnetworkMgr_I getMultiLayerSubnetworkMgr() throws ProcessingFailureException {
		if (subnetworkMgr == null)
			subnetworkMgr = MultiLayerSubnetworkMgr_IHelper.narrow(getManager(NameOfMultiLayerSubnetwork));
		return subnetworkMgr;
	}

	public protection.ProtectionMgr_I getProtectionMgr() throws ProcessingFailureException {
		if (protectionMgr == null)
			protectionMgr = protection.ProtectionMgr_IHelper.narrow(getManager(NameOfProtection));
		return protectionMgr;
	}

	public TrailNtwProtMgr_I getTrailNtwProtMgr() throws ProcessingFailureException {
		if (trailNtwProtMgr == null)
			trailNtwProtMgr = TrailNtwProtMgr_IHelper.narrow(getManager(NameOfTrailNtwProtMgr));
		return trailNtwProtMgr;
	}

	public performance.PerformanceManagementMgr_I getPerformanceMgr() throws ProcessingFailureException {
		if (performanceMgr == null)
			performanceMgr = performance.PerformanceManagementMgr_IHelper.narrow(getManager(NameOfPerformance));
		return performanceMgr;
	}

	common.Common_I getManager(String mgrName) throws globaldefs.ProcessingFailureException {
		common.Common_IHolder commonHolder = new common.Common_IHolder();

		try {
			emsSession.getManager(mgrName, commonHolder);
		} catch (ProcessingFailureException ex) {
			throw ex;
		} catch (Throwable ex) {
			sbilog.info("getManager>>	Exception:" + ex.getMessage());

		}

		return commonHolder.value;
	}

	/**
	 * Intialize this session.
	 * <P>
	 * During intialization, we will: 1) establish event channel with EMS;
	 * </P>
	 * 
	 * @param poa
	 * @param alarmReport
	 * @param oldIorFile
	 */
	public boolean startAlarm() {
		//
		// Perform necessary initialization for attributes.
		//
		try {

			EventChannelHolder eventChannelHolder = new EventChannelHolder();
			emsSession.getEventChannel(eventChannelHolder);

			sbilog.info("startAlarm>>	eventChannel: " + CorbaMgr.instance().ORB().object_to_string(eventChannelHolder.value));

			// modified:
			ConsumerAdmin consumerAdmin = eventChannelHolder.value.default_consumer_admin();
			// ConsumerAdmin consumerAdmin = eventChannelHolder.value.get_consumeradmin(1);
			ClientType cType = ClientType.STRUCTURED_EVENT;
			org.omg.CORBA.IntHolder pid = new org.omg.CORBA.IntHolder();

			// subscribe the changes
			org.omg.CosNotification.EventType[] added = new org.omg.CosNotification.EventType[1];
			added[0] = new org.omg.CosNotification.EventType("*", "*");
			org.omg.CosNotification.EventType[] removed = new org.omg.CosNotification.EventType[0];
			try {
				consumerAdmin.subscription_change(added, removed);
			} catch (Throwable ex) {
				sbilog.info("startAlarm>>	Exception: Error get subscribe the change " + ex.getMessage());

			}

			// obtain proxy push supplier, we keep it's filter and qos setting as the original.
			proxySupplier = StructuredProxyPushSupplierHelper.narrow(consumerAdmin.obtain_notification_push_supplier(cType, pid));

			// activate consumer object and connect it into the supplier.
			consumerTie = new StructuredPushConsumerPOATie(this);
			consumerObjectId = _poa.activate_object(consumerTie);
			proxySupplier.connect_structured_push_consumer(StructuredPushConsumerHelper.narrow(_poa.servant_to_reference(consumerTie)));

		} catch (Throwable e) {
			sbilog.info("startAlarm>>	Exception: Failed to establish event channel ," + e.getMessage());
			return false;

		}

		sbilog.info("startAlarm>>	Connect event channel successfully.");

		// If the last connection was disconnected abnoramlly, now as a repair, the disconnection function should run.
		// RWProxySupplier rwProxySupplier = new RWProxySupplier(this,iorFile);
		// rwProxySupplier.RWFile();

		return true;
	}

	/**
	 * Start a thread to shutdown this session.
	 */
	public void waitAndShutdownSession() {
		Thread worker = new Thread() {
			public void run() {
				shutdownSession();
			}
		};

		worker.start();
	}

	public StructuredProxyPushSupplier getProxySupplier() {
		return proxySupplier;
	}

	public EmsSession_I getEMSSession() {
		return emsSession;
	}

	//
	// Attributes
	//

	// ems session.
	// @see associatedSession.
	protected EmsSession_I emsSession = null;

	// POA used to activate this servant.
	private POA _poa;

	/**
	 * get MaintenanceMgr
	 * 
	 * @return
	 * @throws ProcessingFailureException
	 */

	public maintenanceOps.MaintenanceMgr_I getMaintenanceMgr() throws ProcessingFailureException {
		if (maintenanceMgr == null) {
			Common_IHolder commonHolder = new Common_IHolder();
			try {
				emsSession.getManager(NameOfMaintenance, commonHolder);
			} catch (ProcessingFailureException ex) {
				throw ex;
			} catch (Throwable ex) {
				ex.printStackTrace();
				sbilog.info("getMaintenanceMgr>>	Exception" + ex);
			}
			maintenanceMgr = MaintenanceMgr_IHelper.narrow(commonHolder.value);
		}
		return maintenanceMgr;

	}

	public void getsupportedManagers() throws ProcessingFailureException {
		managerNames_THolder ManagerName = new managerNames_THolder();
		try {
			this.emsSession.getSupportedManagers(ManagerName);
		} catch (ProcessingFailureException ex) {
			throw ex;
		} catch (Throwable ex) {
			ex.printStackTrace();
			sbilog.info("getsupportedManagers>>	Exception: " + ex);
		}

		for (int i = 0; i < ManagerName.value.length; i++) {
			sbilog.info("getsupportedManagers>>	The " + i + " supportedManager is : " + ManagerName.value[i]);
		}

		sbilog.info("getsupportedManagers>>	Leave getsupportedManagers.");
		return;

	}

	public void setEmsSession(EmsSession_I emsSession) {
		this.emsSession = emsSession;
	}

	public void set_poa(POA _poa) {
		this._poa = _poa;
	}

	@Override
	public void alarmLossOccurred(String startTime, String notificationId) {
		// TODO Auto-generated method stub

	}

	public flowDomain.FlowDomainMgr_I getFlowDomainMgr() throws ProcessingFailureException {
		if (flowDomainMgr == null) {
			flowDomainMgr = FlowDomainMgr_IHelper.narrow(getManager(NameOfFdr));
		}

		return flowDomainMgr;
	}

	public ExtendServiceMgr_I getExtendServiceMgr() throws ProcessingFailureException {
		if (extendServiceMgr == null) {
			extendServiceMgr = ExtendServiceMgr_IHelper.narrow(getManager(NameOfExtendService));
		}

		return extendServiceMgr;
	}

}
