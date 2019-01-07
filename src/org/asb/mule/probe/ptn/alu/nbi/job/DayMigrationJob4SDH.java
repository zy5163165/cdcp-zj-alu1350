package org.asb.mule.probe.ptn.alu.nbi.job;

import com.alcatelsbell.cdcp.domain.SummaryUtil;
import com.alcatelsbell.cdcp.nodefx.FtpInfo;
import com.alcatelsbell.cdcp.nodefx.FtpUtil;
import com.alcatelsbell.cdcp.nodefx.MessageUtil;
import com.alcatelsbell.cdcp.nodefx.NodeContext;
import com.alcatelsbell.nms.db.components.service.JPASupport;
import com.alcatelsbell.nms.db.components.service.JPAUtil;
import com.alcatelsbell.nms.valueobject.BObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.asb.mule.probe.framework.CommandBean;
import org.asb.mule.probe.framework.entity.EDS_PTN;
import org.asb.mule.probe.framework.entity.ManagedElement;
import org.asb.mule.probe.framework.nbi.job.MigrateCommonJob;
import org.asb.mule.probe.framework.nbi.task.TaskPoolExecutor;
import org.asb.mule.probe.framework.service.SqliteConn;
import org.asb.mule.probe.framework.util.CodeTool;
import org.asb.mule.probe.framework.util.FileLogger;
import org.asb.mule.probe.ptn.alu.nbi.task.*;
import org.asb.mule.probe.ptn.alu.service.AluService;
import org.quartz.JobExecutionContext;

import java.io.File;
import java.util.*;

/**
 * Author: Ronnie.Chen
 * Date: 14-8-31
 * Time: 下午2:42
 * rongrong.chen@alcatel-sbell.com.cn
 */
public class DayMigrationJob4SDH  extends MigrateCommonJob implements CommandBean {

    private FileLogger nbilog = null;
    private String name = "";
    private String emstype = "OTN";
    private SqliteConn sqliteConn = null;

    @Override
    public void execute(JobExecutionContext arg0) {
        // nbilog = new FileLogger(service.getEmsName() + "/nbi.log");
        Date startTime = new Date();
        nbilog = ((AluService) service).getCorbaService().getNbilog();
        String dbName = null;
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
            dbName = getJobName() + ".db";
            sqliteConn.setDataPath(dbName);
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
            TaskPoolExecutor executor2 = new TaskPoolExecutor(1);
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

            queryCount();
            sqliteConn.release();
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

        // ftp
        try {
            MessageUtil.sendSBIMessage(serial, "ftpFile", 85);
            FtpInfo ftpInfo = FtpUtil.uploadFile("SDH", "ALU",
                    service.getEmsName().contains("/") ? service.getEmsName().replace("/", "-") : service.getEmsName(), new File(dbName));


        //    EDS_PTN eds = geyEDS(dbName);
            EDS_PTN eds = SummaryUtil.geyEDS(serial, sqliteConn, service.getEmsName(), dbName);
            eds.setStartTime(startTime);
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

    private void queryCount() {
        Logger logger = NodeContext.getNodeContext().getLogger();
        synchronized (logger) {
            logger.info("===========================  ["+service.getEmsName()+"]"+getJobName()+" =========================================================");
            try {
                JPASupport jpaSupport = sqliteConn.getJpaSupport();
                HashMap<String,String> sqls = new HashMap<String, String>();
                sqls.put("NE:","SELECT count(ne.dn)     FROM  ManagedElement ne ");
                sqls.put("slot:","SELECT count(slot.dn)       FROM  EquipmentHolder slot WHERE slot.holderType='slot' ");
                sqls.put("subslot:", "SELECT count(subslot.dn)    FROM  EquipmentHolder subslot WHERE subslot.holderType='sub_slot' ");
                sqls.put("card:","SELECT count(card.dn)       FROM  Equipment card ");
                sqls.put("ptp:","SELECT count(ptp.dn)        FROM  PTP ptp WHERE dn like '%PTP%' ");
                sqls.put("ftp:","SELECT count(ftp.dn)        FROM  PTP ftp WHERE dn like '%FTP%' ");
                sqls.put("ctp:","SELECT count(id)        FROM  CTP ");
                sqls.put("crossconnect:","SELECT count(id)        FROM  CrossConnect ");
                sqls.put("subnetworkconnection:","SELECT count(id) FROM SubnetworkConnection ");
                sqls.put("section:","SELECT count(id) FROM Section ");
                sqls.put("R_TrafficTrunk_CC_Section:","SELECT count(id) FROM R_TrafficTrunk_CC_Section ");

                Set<String> keySet = sqls.keySet();
                for (String key : keySet) {
                    String sql = sqls.get(key);
                    List list = JPAUtil.getInstance().queryQL(jpaSupport, sql);
                    int count = ((Long) list.get(0)).intValue();
                    nbilog.info(key+" "+count);
                    logger.info(key+" "+count);
                }


                // jpaSupport.end();
            } catch (Exception e) {
                nbilog.error(e,e);
            }
            logger.info("===============================================================================================================");
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
