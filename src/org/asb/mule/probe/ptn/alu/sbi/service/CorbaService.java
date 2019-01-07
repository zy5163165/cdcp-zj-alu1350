package org.asb.mule.probe.ptn.alu.sbi.service;

import java.util.Timer;

import com.alcatelsbell.cdcp.nodefx.exception.EmsConnectionFailureException;
import nmsSession.NmsSession_I;
import nmsSession.NmsSession_IHelper;

import org.asb.mule.probe.framework.service.CorbaSbiService;
import org.asb.mule.probe.framework.util.CodeTool;
import org.asb.mule.probe.framework.util.FileLogger;
import org.asb.mule.probe.framework.util.corba.CorbaMgr;
import org.asb.mule.probe.ptn.alu.sbi.HeartBeat;
import org.asb.mule.probe.ptn.alu.sbi.NmsSession;
import org.omg.CORBA.Object;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import emsMgr.EMS_THolder;
import emsSession.EmsSession_I;
import emsSession.EmsSession_IHolder;
import emsSessionFactory.EmsSessionFactory_I;
import emsSessionFactory.EmsSessionFactory_IHelper;
import globaldefs.ProcessingFailureException;

public class 	CorbaService extends CorbaSbiService {

	private NmsSession nmsSession = null;
	private EmsSession_I _emsSession = null;
	private org.omg.CORBA.ORB orb = null;
	private org.omg.PortableServer.POA nmsPoa;

	private FileLogger nbilog = null;
	private FileLogger sbilog = null;
	private FileLogger errorlog = null;
	private FileLogger eventlog = null;
	
	private Timer timer = null;
	private HeartBeat heartBeat = null;
	private boolean initlog = false;

	private void initLog() {
		String name = getEmsName().contains("/") ? getEmsName().replace("/", "-") : getEmsName();
		nbilog = new FileLogger(name + "/nbi.log");
		sbilog = new FileLogger(name + "/sbi.log");
		errorlog = new FileLogger(name + "/error.log");
		eventlog = new FileLogger(name + "/event.log");
		initlog = true;
	}

	public boolean init() {
		if (!initlog) {
			initLog();
		}

		sbilog.info("corbaService init in");
		setConnectState(connect());
		sbilog.info("corbaService init out");
		sbilog.info("collect data as debug after init");

		// DayMigrationJob job=new DayMigrationJob();
		// job.execute(null);
		return true;

	}
	
	public boolean initFake() {
		if (!initlog) {
			initLog();
		}

		sbilog.info("corbaService init fake");
		setConnectState(true);
		sbilog.info("collect data as debug after init");

		// DayMigrationJob job=new DayMigrationJob();
		// job.execute(null);
		return true;
	}

	/**
	 * 核心方法
	 * 1.连接厂商网管
	 */
	public boolean connect() {
		// 1.init orb
		if (orb == null) {
			CorbaMgr corba = CorbaMgr.instance();
			corba.initORB(getNamingServiceDns(), getNamingServiceIp(), sbilog);
			Thread corbaThread = new Thread(corba);
			corbaThread.start();

		}
		orb = CorbaMgr.instance().ORB();
		// org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(new String[0], null);

		try {
			// 2.connect vendor nameService
			sbilog.info("connect>>	vendor NameService URL = " + getCorbaUrl());
			Object string_to_object = orb.string_to_object(getCorbaUrl());
			// NamingContextExt ns = NamingContextExtHelper.narrow(string_to_object);
			// 3. get EmsSessionFactory_I
			EmsSessionFactory_I _emsSessionFactory = null;
			// _emsSessionFactory = EmsSessionFactory_IHelper.narrow(ns.resolve_str(getCorbaTree()));
			_emsSessionFactory = EmsSessionFactory_IHelper.narrow(string_to_object);

			if (_emsSessionFactory == null) {
				sbilog.info("connect>>	Get EmsSessionFactory failed");
				return false;
			} else {
				sbilog.info("connect>>	Get EmsSessionFactory successfully.");
			}

			// 4. create poa

			nmsPoa = CorbaMgr.instance().createNmsSessionPOA(getEmsName());

			sbilog.info("connect>>	createNmsSessionPOA ok");

			// 5. create NmsSession client
			// nmsSession = new NmsSession();
			nmsSession = new NmsSession(sbilog, errorlog, eventlog);
			nmsSession.set_poa(nmsPoa);

			org.omg.CORBA.Object nmsobj = null;
			try {
				nmsobj = nmsPoa.servant_to_reference(nmsSession);
			} catch (ServantNotActive e) {

				e.printStackTrace();
			} catch (WrongPolicy e) {

				e.printStackTrace();
			}

			sbilog.info("connect>>	NmsSession IOR:" + orb.object_to_string(NmsSession_IHelper.narrow(nmsobj)));
			NmsSession_I nmsSession_I = NmsSession_IHelper.narrow(nmsobj);

			sbilog.info("connect>>	active _nmsSession successfully");

			// 6. login by EmsSessionFactory_I,开始调用CORBA服务器端的'getEmsSession'方法"
			EmsSession_IHolder emsSessionHolder = new EmsSession_IHolder();
            String corbaUserName = getCorbaUserName();
            String corbaPassword = getCorbaPassword();

            corbaUserName = "alcatel";
            corbaPassword = "alcatel";

            System.out.println("corbaUserName = " + corbaUserName);
            System.out.println("corbaPassword = " + corbaPassword);
            sbilog.info("corbaUserName = " + corbaUserName);
            sbilog.info("corbaPassword = " + corbaPassword);
            _emsSessionFactory.getEmsSession(corbaUserName, corbaPassword, nmsSession_I, emsSessionHolder);

			sbilog.info("connect>>	EmsSession IOR:" + orb.object_to_string(emsSessionHolder.value));
			sbilog.info("connect>>	emsSessionFactory.getEmsSession successfully");
			_emsSession = emsSessionHolder.value;

			if (_emsSession == null) {
				sbilog.info("connect>>	Get EmsSession failed");
				return false;
			} else {
				nmsSession.setEmsSession(_emsSession);
				sbilog.info("connect>>	Get EmsSession successfully.");

			}
			String emsVersionInfo = _emsSessionFactory.getVersion();
			sbilog.info("**********************************************");
			sbilog.info("CORBA 服务器版本信息:" + emsVersionInfo);
			sbilog.info("**********************************************");

			nmsSession.getsupportedManagers();

			// set emsDn as cache
			EMS_THolder ems = new EMS_THolder();
			nmsSession.getEmsMgr().getEMS(ems);
			setEmsDn(ems.value.name[0].value);
			sbilog.info(ems.value.toString());

			// // 7.receive alarm
			// if (nmsSession.startAlarm()) {
			// sbilog.info("startListenAlarm>>	success");
			// } else {
			// sbilog.info("startListenAlarm>>	failed.");
			// }
		//	startHB();
		} catch (ProcessingFailureException pe) {
			sbilog.error("Failed to get ems session,detail: " + CodeTool.isoToGbk(pe.errorReason));
			sbilog.error("connect ProcessingFailureException: " + CodeTool.isoToGbk(pe.errorReason), pe);
			handleException(new EmsConnectionFailureException(pe,"EMS连接失败:"+CodeTool.isoToGbk(pe.errorReason)));

			//
			return false;
		} catch (Exception e) {
			sbilog.error("connect>>	Exception: " + CodeTool.isoToGbk(e.getMessage()));
			sbilog.error("connect ProcessingFailureException: " + CodeTool.isoToGbk(e.getMessage()), e);
			handleException(new EmsConnectionFailureException(e,"EMS连接失败:"+CodeTool.isoToGbk(e.getMessage())));

			//
			return false;
		}
		handleExceptionRecover(EmsConnectionFailureException.EXCEPTION_CODE);
		setConnectState(true);
		return true;

	}
	
	private void startHB() {
		if (timer == null) {
			timer = new Timer("HeartBeat-" + getEmsName());
		}
		if (heartBeat == null)
			heartBeat = new HeartBeat(this, sbilog);
		timer.scheduleAtFixedRate(heartBeat, 5000L, 2 * 60 * 1000L);
	}

	/**
	 * 核心方法
	 * 2.断开厂商网管
	 */
	public boolean disconnect() {
		try {
			if (nmsSession != null) {
				// nmsSession.waitAndShutdownSession();
				nmsSession.shutdownSession();
				nmsSession = null;
			}
			//
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
			if (heartBeat != null) {
				heartBeat.cancel();
				heartBeat = null;
			}
		} catch (Throwable e) {
			sbilog.error("disConnect>>	Exception:" + e.getMessage());
			// return false;
		}
		setConnectState(false);
		return true;
	}

	/**
	 * @return the nmsSession
	 */
	public NmsSession getNmsSession() {
		while (!isConnectState()) {
			errorlog.error("nmsSession is still disconnected ,");
			try {
				Thread.sleep(30000l);
			} catch (InterruptedException e) {

			}
		}
		return nmsSession;
	}
	/**
	 * @param nmsSession
	 *            the nmsSession to set
	 */
	public void setNmsSession(NmsSession nmsSession) {
		this.nmsSession = nmsSession;
	}

	public FileLogger getSbilog() {
		return sbilog;
	}

	public FileLogger getErrorlog() {
		return errorlog;
	}

	public FileLogger getNbilog() {
		return nbilog;
	}
}
