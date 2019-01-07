package org.asb.mule.probe.ptn.alu;

import com.alcatelsbell.cdcp.nodefx.CorbaEmsAdapterTemplate;
import com.alcatelsbell.nms.valueobject.sys.Ems;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.asb.mule.probe.framework.service.CorbaSbiService;
import org.asb.mule.probe.framework.service.NbiService;
import org.asb.mule.probe.ptn.alu.nbi.job.DayMigrationJob;
import org.asb.mule.probe.ptn.alu.nbi.job.DayMigrationJob4SDH;
import org.asb.mule.probe.ptn.alu.nbi.job.DeviceJob;
import org.asb.mule.probe.ptn.alu.sbi.service.CorbaService;
import org.asb.mule.probe.ptn.alu.service.AluService;

/**
 * Author: Ronnie.Chen
 * Date: 14-8-31
 * Time: 下午2:37
 * rongrong.chen@alcatel-sbell.com.cn
 */
public class AluEmsAdapterV2 extends CorbaEmsAdapterTemplate {


    @Override
    public Object doTestEms(NbiService nbiService) {
        return ((AluService)nbiService).getCorbaService().isConnectState();
    }

    @Override
    public Object doSyncEms(NbiService nbiService, Ems ems, String _serial) {
        AluService AluService = (AluService)nbiService;
        if (ems.getTag1() == null) ems.setTag1("PTN");
        if (ems.getTag1().equals("SDH") || ems.getTag1().equals("OTN") || ems.getTag1().equals("DWDM")) {
            DayMigrationJob4SDH job = new DayMigrationJob4SDH();
            job.setService(AluService);
            job.setSerial(_serial);
            job.execute();
        }
        else {
            DayMigrationJob  job = new DayMigrationJob();
            job.setService(AluService);
            job.setSerial(_serial);
            job.execute();
        }

        return null;
    }

    @Override
    public Object doSyncDevice(NbiService nbiService, String _serial, String devicedn) {
        DeviceJob job = new DeviceJob(devicedn);
        job.setService(nbiService);
        job.setSerial(_serial);
        job.execute();
        return null;
    }

    @Override
    public CorbaSbiService createCorbaSbiService() {
        return new CorbaService();
    }

    @Override
    public NbiService createNbiService(CorbaSbiService corbaSbiService) {
        AluService AluService = new AluService();
        AluService.setCorbaService((CorbaService)corbaSbiService);
        return AluService;
    }

    @Override
    public String getType() {
        return "ALU";
    }
}
