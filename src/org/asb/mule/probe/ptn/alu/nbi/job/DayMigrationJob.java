package org.asb.mule.probe.ptn.alu.nbi.job;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.alcatelsbell.nms.util.ObjectUtil;
import org.asb.mule.probe.framework.CommandBean;
import org.asb.mule.probe.framework.entity.EDS_PTN;
import org.asb.mule.probe.framework.entity.Equipment;
import org.asb.mule.probe.framework.entity.FlowDomainFragment;
import org.asb.mule.probe.framework.entity.ManagedElement;
import org.asb.mule.probe.framework.entity.PTP;
import org.asb.mule.probe.framework.entity.Section;
import org.asb.mule.probe.framework.entity.SubnetworkConnection;
import org.asb.mule.probe.framework.entity.TrailNtwProtection;
import org.asb.mule.probe.framework.nbi.job.MigrateCommonJob;
import org.asb.mule.probe.framework.nbi.task.TaskPoolExecutor;
import org.asb.mule.probe.framework.service.SqliteConn;

import org.asb.mule.probe.framework.util.CodeTool;
import org.asb.mule.probe.framework.util.FileLogger;
import org.asb.mule.probe.ptn.alu.nbi.task.FlowDomainFragmentDataTask;
import org.asb.mule.probe.ptn.alu.nbi.task.ManagedElementDataTask;
import org.asb.mule.probe.ptn.alu.nbi.task.PhysicalDataTask;
import org.asb.mule.probe.ptn.alu.nbi.task.ProtectionGroupDataTask;
import org.asb.mule.probe.ptn.alu.nbi.task.SNCAndCCAndSectionDataTask;
import org.asb.mule.probe.ptn.alu.nbi.task.SNCDataTask;
import org.asb.mule.probe.ptn.alu.nbi.task.SectionDataTask;
import org.asb.mule.probe.ptn.alu.service.AluService;
import org.quartz.JobExecutionContext;

import com.alcatelsbell.cdcp.nodefx.FtpInfo;
import com.alcatelsbell.cdcp.nodefx.FtpUtil;
import com.alcatelsbell.cdcp.nodefx.MessageUtil;
import com.alcatelsbell.nms.db.components.service.JPASupport;
import com.alcatelsbell.nms.db.components.service.JPAUtil;
import com.alcatelsbell.nms.valueobject.BObject;

public class DayMigrationJob extends MigrateCommonJob implements CommandBean {

	private FileLogger nbilog = null;
	private String name = "";
    private SqliteConn sqliteConn = null;
	@Override
	public void execute(JobExecutionContext arg0) {
		// nbilog = new FileLogger(service.getEmsName() + "/nbi.log");
		nbilog = ((AluService) service).getCorbaService().getNbilog();
		if (!service.getConnectState()) {
			nbilog.error(">>>EMS is disconnect.");
			try {
				MessageUtil.sendSBIFailedMessage("EMS is disconnect.", serial);
			} catch (Exception e) {
				nbilog.error("DayMigrationJob.Message Exception:", e);
			}
			return;
		}
		nbilog.info("Start for task : " + serial);
		nbilog.info("Start to migrate all data from ems: " + service.getEmsName());
		String dbName = getJobName() + ".db";
		// name = "";// set empty to create new db instance
		try {
			// 0. set new db for new task.
            sqliteConn = new SqliteConn();
            sqliteConn.setDataPath(dbName);
            sqliteConn.init();
			// 1.ne
			nbilog.info("ManagedElementDataTask : ");
			MessageUtil.sendSBIMessage(serial, "ManagedElementDataTask", 0);

			ManagedElementDataTask neTask = new ManagedElementDataTask(sqliteConn);
			neTask.CreateTask(service, getJobName(), service.getEmsName(), nbilog);
			Vector<BObject> neList = neTask.excute();

			nbilog.info("PhysicalDataTask CrossConnectionDataTask: ");
			MessageUtil.sendSBIMessage(serial, "PhysicalDataTask", 10);
			ObjectUtil.newFolder("../cache/" + service.getEmsName());
			for (BObject ne : neList) {
				ManagedElement newne = (ManagedElement) ne;
				if (newne.getCommunicationState().equals("CS_AVAILABLE") && !newne.getProductName().equals("External Network")) {
					PhysicalDataTask phyTask = new PhysicalDataTask(sqliteConn);
					phyTask.CreateTask(service, getJobName(), ne.getDn(), nbilog, false);
					phyTask.excute();
				}
			}

			nbilog.info("SectionDataTask: ");
			MessageUtil.sendSBIMessage(serial, "SectionDataTask", 40);
			SectionDataTask sectionTask = new SectionDataTask(sqliteConn);
			sectionTask.CreateTask(service, getJobName(), null, nbilog);
			sectionTask.excute();

			nbilog.info("FlowDomainFragmentDataTask: ");
			MessageUtil.sendSBIMessage(serial, "FlowDomainFragmentDataTask", 50);

			FlowDomainFragmentDataTask ffdrTask = new FlowDomainFragmentDataTask(sqliteConn);
			ffdrTask.CreateTask(service, getJobName(), null, nbilog);
			ffdrTask.excute();

			nbilog.info("SNCDataTask: ");
			MessageUtil.sendSBIMessage(serial, "SNCDataTask", 60);
			SNCDataTask ttTask = new SNCDataTask(sqliteConn);
			ttTask.CreateTask(service, getJobName(), null, nbilog);
			Vector<BObject> ttVector = ttTask.excute();
            sqliteConn.waitingForInsertBObject();

			nbilog.info("SNCAndCCAndSectionDataTask: ");
			MessageUtil.sendSBIMessage(serial, "SNCDataTask", 70);
			TaskPoolExecutor executor2 = new TaskPoolExecutor(20);
			for (BObject snc : ttVector) {
				if (snc.getDn().contains("TU_")) {
					SNCAndCCAndSectionDataTask task = new SNCAndCCAndSectionDataTask(sqliteConn);
					task.CreateTask(service, getJobName(), snc.getDn(), nbilog);
					executor2.executeTask(task);
				}
			}
			nbilog.info("SNCAndCCAndSectionDataTask: waitingForAllFinish.");
			executor2.waitingForAllFinish();
			nbilog.info("SNCAndCCAndSectionDataTask: waitingForInsertBObject.");
            sqliteConn.waitingForInsertBObject();

			nbilog.info("ProtectionGroupDataTask: ");
			MessageUtil.sendSBIMessage(serial, "ProtectionGroupDataTask", 80);
			ProtectionGroupDataTask pgTask = new ProtectionGroupDataTask(sqliteConn);
			pgTask.CreateTask(service, getJobName(), null, nbilog);
			pgTask.excute();

			neList.clear();
			ttVector.clear();

            sqliteConn.waitingForInsertBObject();
            sqliteConn.release();

			// printTalbe();
			nbilog.info("End to migrate all data from ems.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			nbilog.error("DayMigrationJob.execute Exception:", e);
			try {
				MessageUtil.sendSBIFailedMessage("SBI ERROR.", serial);
			} catch (Exception e1) {
				nbilog.error("DayMigrationJob.Message Exception:", e);
			}
		}

		// ftp
		try {
			MessageUtil.sendSBIMessage(serial, "ftpFile", 85);
			FtpInfo ftpInfo = FtpUtil.uploadFile("PTN", "ALU",
					service.getEmsName().contains("/") ? service.getEmsName().replace("/", "-") : service.getEmsName(), new File(dbName));

			EDS_PTN eds = geyEDS(dbName);
			MessageUtil.sendSBIFinishMessage(ftpInfo, serial, eds);
		} catch (Exception e) {
			nbilog.error("DayMigrationJob.ftp Exception:", e);
			try {
				MessageUtil.sendSBIFailedMessage("FTP ERROR.", serial);
			} catch (Exception e1) {
				nbilog.error("DayMigrationJob.Message Exception:", e);
			}
		}
		try {
			File file = new File(dbName);
			file.delete();
			MessageUtil.sendSBIMessage(serial, "End", 90);
		} catch (Exception e) {
			nbilog.error("DayMigrationJob.Message Exception:", e);
		}
		nbilog.info("End of task : " + serial);

	}

	private EDS_PTN geyEDS(String dn) {
		EDS_PTN eds = new EDS_PTN();
		try {
			JPASupport jpaSupport = sqliteConn.getJpaSupport();
			jpaSupport.begin();
			String[] sqls = { "SELECT count(ne.dn)     FROM  ManagedElement ne ",
					"SELECT count(slot.dn)       FROM  EquipmentHolder slot WHERE slot.holderType='slot' ",
					"SELECT count(subslot.dn)    FROM  EquipmentHolder subslot WHERE subslot.holderType='sub_slot' ",
					"SELECT count(card.dn)       FROM  Equipment card WHERE card.dn like '%slot%' ",
					"SELECT count(ptp.dn)        FROM  PTP ptp WHERE dn like '%PTP%' ", "SELECT count(ftp.dn)        FROM  PTP ftp WHERE dn like '%FTP%' ",
					"SELECT count(section.dn)    FROM  Section section WHERE dn like '%LOC%' ",
					"SELECT count(tunnel.dn)     FROM  SubnetworkConnection tunnel where tunnel.rate='1001' ",
					"SELECT count(pw.dn)         FROM  SubnetworkConnection pw   where pw.rate='1002' ",
					"SELECT count(fdfr.dn)       FROM  FlowDomainFragment fdfr ",
					"SELECT count(route.dn)      FROM  R_TrafficTrunk_CC_Section route where route.type='CC' ",
					"SELECT count(pg.dn)         FROM  TrailNtwProtection pg " };
			StringBuilder sb = new StringBuilder();
			int[] count = new int[sqls.length];
			for (int i = 0; i < sqls.length; i++) {
				List list = JPAUtil.getInstance().queryQL(jpaSupport, sqls[i]);
				sb.append(list.get(0)).append("	");

				count[i] = ((Long) list.get(0)).intValue();
			}
			nbilog.info("\nNE,Slot,subSlot,Equipment,PTP,FTP,Section,Tunnel,PW,PWE3,Route,TunnelPG\n" + sb.toString());
			jpaSupport.end();
			jpaSupport.release();

			eds.setDn(dn);
			eds.setCollectTime(new Date());
			eds.setCreateDate(new Date());
			eds.setTaskSerial(serial);
			eds.setEmsname(service.getEmsName());
			eds.setNeCount(count[0]);
			eds.setSlotCount(count[1]);
			eds.setSubSlotCount(count[2]);
			eds.setEquipmentCount(count[3]);
			eds.setPtpCount(count[4]);
			eds.setFtpCount(count[5]);
			eds.setSectionCount(count[6]);
			eds.setTunnelCount(count[7]);
			eds.setPwCount(count[8]);
			eds.setPwe3Count(count[9]);
			eds.setRouteCount(count[10]);
			eds.setTunnelPG(count[11]);
		} catch (Exception e) {
			nbilog.error("DayMigrationJob.count Exception:", e);
		}
		return eds;
	}

	// private void printTalbe() {
	// JPASupport jpaSupport = SqliteService.getInstance().getJpaSupport();
	// try {
	// jpaSupport.begin();
	// String[] sqls = { "SELECT count(ne.dn)     FROM  ManagedElement ne ",
	// "SELECT count(slot.dn) FROM  EquipmentHolder slot WHERE slot.holderType='slot' ",
	// "SELECT count(subslot.dn)    FROM  EquipmentHolder subslot WHERE subslot.holderType='sub_slot' ",
	// "SELECT count(card.dn)       FROM  Equipment card WHERE card.dn like '%slot%' ",
	// "SELECT count(ptp.dn)        FROM  PTP ptp ",
	// "SELECT count(section.dn)    FROM  Section section WHERE dn like '%LOC%' ",
	// "SELECT count(tunnel.dn)     FROM  SubnetworkConnection tunnel where tunnel.rate='1001' ",
	// "SELECT count(pw.dn)         FROM  SubnetworkConnection pw   where pw.rate='1002' ",
	// "SELECT count(fdfr.dn)       FROM  FlowDomainFragment fdfr ",
	// "SELECT count(route.dn)      FROM  R_TrafficTrunk_CC_Section route where route.type='CC' ",
	// "SELECT count(pg.dn)         FROM  TrailNtwProtection pg " };
	// StringBuilder sb = new StringBuilder();
	// for (String sql : sqls) {
	// List list = JPAUtil.getInstance().queryQL(jpaSupport, sql);
	// sb.append(list.get(0)).append("	");
	// }
	// nbilog.info("\nNE,Slot,SubSlot,Equipment,PTP,Section,Tunnel,PW,PWE3,Route,TunnelPG\n" + sb.toString());
	// jpaSupport.end();
	// jpaSupport.release();
	// } catch (Exception e) {
	// e.printStackTrace();
	// nbilog.error("printTalbe Exception:", e);
	// }
	//
	// }

	/**
	 * define job name ,as unique id for migration job.
	 * It can be used in failed job to migrate ems data from ems.
	 * 
	 * @return
	 */
	private String getJobName() {
		if (name.trim().length() == 0) {
			// name = CodeTool.getDatetime()+"-"+service.getEmsName()+"-DayMigration";
			name = service.getEmsName().contains("/") ? service.getEmsName().replace("/", "-") : service.getEmsName();
			name = CodeTool.getDatetimeStr() + "-" + name + "-DayMigration";
		}
		return name;
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		execute(null);
	}

}
