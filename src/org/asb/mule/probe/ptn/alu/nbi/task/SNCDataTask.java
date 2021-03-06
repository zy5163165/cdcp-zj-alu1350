package org.asb.mule.probe.ptn.alu.nbi.task;

import java.util.List;
import java.util.Vector;

import org.asb.mule.probe.framework.entity.SubnetworkConnection;
import org.asb.mule.probe.framework.nbi.task.CommonDataTask;
import org.asb.mule.probe.framework.service.SqliteConn;
import org.asb.mule.probe.framework.service.SqliteService;

import com.alcatelsbell.nms.valueobject.BObject;

public class SNCDataTask extends CommonDataTask {

	public SNCDataTask(SqliteConn conn) {
        // TODO Auto-generated constructor stub
        this.setSqliteConn(conn);

    }


    @Override
	public Vector<BObject> excute() {
		// TODO Auto-generated method stub
		Vector<BObject> neVec = new Vector<BObject>();
		try {
			List<SubnetworkConnection> sncList = service.retrieveAllSNCs();
			if (sncList != null && sncList.size() > 0) {
				nbilog.info("SNC : " + sncList.size());
				for (SubnetworkConnection snc : sncList) {
                    getSqliteConn().insertBObject(snc);
					neVec.add(snc);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return neVec;

	}

	@Override
	public void insertDate(Vector<BObject> dataList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDate(Vector<BObject> dataList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteDate(Vector<BObject> dataList) {
		// TODO Auto-generated method stub

	}

}
