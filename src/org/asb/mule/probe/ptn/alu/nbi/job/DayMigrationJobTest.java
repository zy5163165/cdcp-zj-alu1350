package org.asb.mule.probe.ptn.alu.nbi.job;

import java.util.List;
import java.util.Vector;

import org.asb.mule.probe.framework.CommandBean;
import org.asb.mule.probe.framework.entity.CTP;
import org.asb.mule.probe.framework.entity.ManagedElement;
import org.asb.mule.probe.framework.nbi.job.MigrateCommonJob;
import org.asb.mule.probe.framework.nbi.task.TaskPoolExecutor;
import org.asb.mule.probe.framework.service.SqliteConn;

import org.asb.mule.probe.framework.util.CodeTool;
import org.asb.mule.probe.framework.util.FileLogger;
import org.asb.mule.probe.ptn.alu.nbi.task.CrossConnectionDataTask;
import org.asb.mule.probe.ptn.alu.nbi.task.FlowDomainFragmentDataTask;
import org.asb.mule.probe.ptn.alu.nbi.task.ManagedElementDataTask;
import org.asb.mule.probe.ptn.alu.nbi.task.PhysicalDataTask;
import org.asb.mule.probe.ptn.alu.nbi.task.ProtectionGroupDataTask;
import org.asb.mule.probe.ptn.alu.nbi.task.SNCAndCCAndSectionDataTask;
import org.asb.mule.probe.ptn.alu.nbi.task.SNCDataTask;
import org.asb.mule.probe.ptn.alu.nbi.task.SectionDataTask;
import org.asb.mule.probe.ptn.alu.service.AluService;
import org.quartz.JobExecutionContext;

import com.alcatelsbell.nms.db.components.service.JPASupport;
import com.alcatelsbell.nms.db.components.service.JPAUtil;
import com.alcatelsbell.nms.valueobject.BObject;

public class DayMigrationJobTest extends MigrateCommonJob implements CommandBean {

	private FileLogger nbilog = null;
	private String name = "";
	private String emstype = "OTN";
    private SqliteConn sqliteConn = null;
    @Override
    public void execute(JobExecutionContext arg0) {

        queryCTP("EMS:ZJ-ALU-1-OTN@ManagedElement:100/7@FTP:ODU4-1-1-71-2");
        queryCTP("EMS:ZJ-ALU-1-OTN@ManagedElement:100/7@PTP:OCH-1-1-43-1");

    }

    private void queryCTP(String ftp) {
        System.out.println("ptp = " + ftp);
        List<CTP> ctps = ((AluService) service).retrieveAllCtps(ftp);
        System.out.println("ctps = " + (ctps == null ? null: ctps.size()));
        if (ctps != null) {
            for (CTP ctp : ctps) {
                System.out.println("ctp = " + ctp.getDn());
            }
        }
    }

//	@Override
	public void executeJob(JobExecutionContext arg0) {
		// nbilog = new FileLogger(service.getEmsName() + "/nbi.log");
		nbilog = ((AluService) service).getCorbaService().getNbilog();

		if (!service.getConnectState()) {
			nbilog.error(">>>EMS is disconnect.");
			try {
				// MessageUtil.sendSBIFailedMessage("EMS is disconnect.", serial);
			} catch (Exception e) {
				nbilog.error("DayMigrationJob.Message Exception:", e);
			}
			return;
		}
		nbilog.info("Start for task : " + serial);
		nbilog.info("Start to migrate all data from ems: " + service.getEmsName());
		// name = "";// set empty to create new db instance
		try {
			// 0. set new db for new task.
            sqliteConn = new SqliteConn();
            sqliteConn.setDataPath(getJobName() + ".db");
            sqliteConn.init();
			// 1.ne
			nbilog.info("ManagedElementDataTask : ");
			ManagedElementDataTask neTask = new ManagedElementDataTask(sqliteConn);
			neTask.CreateTask(service, getJobName(), service.getEmsName(), nbilog);
			Vector<BObject> neList = neTask.excute();

			nbilog.info("PhysicalDataTask CrossConnectionDataTask: ");
			TaskPoolExecutor executor = new TaskPoolExecutor(1);
			for (BObject ne : neList) {
				ManagedElement newne = (ManagedElement) ne;
				if (newne.getCommunicationState().equals("CS_AVAILABLE") && !newne.getProductName().equals("External Network")) {
					PhysicalDataTask phyTask = new PhysicalDataTask(sqliteConn);
					phyTask.CreateTask(service, getJobName(), ne.getDn(), nbilog, true);
					executor.executeTask(phyTask);

					CrossConnectionDataTask ccTask = new CrossConnectionDataTask(sqliteConn);
					ccTask.CreateTask(service, getJobName(), ne.getDn(), nbilog);
					executor.executeTask(ccTask);
				}
			}
			nbilog.info("PhysicalDataTask CrossConnectionDataTask: waitingForAllFinish.");
			executor.waitingForAllFinish();
			nbilog.info("PhysicalDataTask CrossConnectionDataTask: waitingForInsertBObject.");
            sqliteConn.waitingForInsertBObject();

			nbilog.info("SectionDataTask: ");
			SectionDataTask sectionTask = new SectionDataTask(sqliteConn);
			sectionTask.CreateTask(service, getJobName(), null, nbilog);
			sectionTask.excute();

			nbilog.info("FlowDomainFragmentDataTask: ");
			FlowDomainFragmentDataTask ffdrTask = new FlowDomainFragmentDataTask(sqliteConn);
			ffdrTask.CreateTask(service, getJobName(), null, nbilog);
			ffdrTask.excute();

			nbilog.info("SNCDataTask: ");
			SNCDataTask ttTask = new SNCDataTask(sqliteConn);
			ttTask.CreateTask(service, getJobName(), null, nbilog);
			Vector<BObject> ttVector = ttTask.excute();
            sqliteConn.waitingForInsertBObject();

			nbilog.info("SNCAndCCAndSectionDataTask: ");
			TaskPoolExecutor executor2 = new TaskPoolExecutor(6);
			if (emstype.contains(EMS_TYPE_PTN)) {
				for (BObject snc : ttVector) {
					if (snc.getDn().contains("TU_")) {
						SNCAndCCAndSectionDataTask task = new SNCAndCCAndSectionDataTask(sqliteConn);
						task.CreateTask(service, getJobName(), snc.getDn(), nbilog);
						executor2.executeTask(task);
					}
				}
			} else {
				for (BObject snc : ttVector) {
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
			ProtectionGroupDataTask pgTask = new ProtectionGroupDataTask(sqliteConn);
			pgTask.CreateTask(service, getJobName(), null, nbilog);
			pgTask.excute();

			neList.clear();
			ttVector.clear();

            sqliteConn.waitingForInsertBObject();
			printTalbe();
			nbilog.info("End to migrate all data from ems.");
			// MessageUtil.sendSBIMessage(serial, "", 100);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			nbilog.error("DayMigrationJob.execute Exception:", e);
			try {
				// MessageUtil.sendSBIFailedMessage("SBI ERROR.", serial);
			} catch (Exception e1) {
				nbilog.error("DayMigrationJob.Message Exception:", e);
			}
		}
		nbilog.info("End of task : " + serial);

	}

	private void printTalbe() {
		JPASupport jpaSupport = sqliteConn.getJpaSupport();
		try {
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
			for (String sql : sqls) {
				List list = JPAUtil.getInstance().queryQL(jpaSupport, sql);
				sb.append(list.get(0)).append("	");
			}
			nbilog.info("\nNE,Slot,SubSlot,Equipment,PTP,FTP,Section,Tunnel,PW,PWE3,Route,TunnelPG\n" + sb.toString());
			jpaSupport.end();
			jpaSupport.release();
		} catch (Exception e) {
			e.printStackTrace();
			nbilog.error("printTalbe Exception:", e);
		}

	}

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
