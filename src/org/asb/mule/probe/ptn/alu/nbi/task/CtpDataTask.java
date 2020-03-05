package org.asb.mule.probe.ptn.alu.nbi.task;

import java.util.List;
import java.util.Vector;

import org.asb.mule.probe.framework.entity.CTP;
import org.asb.mule.probe.framework.nbi.task.CommonDataTask;
import org.asb.mule.probe.framework.service.SqliteConn;
import org.asb.mule.probe.framework.service.SqliteService;

import com.alcatelsbell.nms.valueobject.BObject;

public class CtpDataTask extends CommonDataTask {

	public CtpDataTask(SqliteConn conn) {
        // TODO Auto-generated constructor stub
        this.setSqliteConn(conn);

    }

    @Override
	public Vector<BObject> excute() {
		// TODO Auto-generated method stub
		try {
			List<CTP> ctpList = service.retrieveAllCtps(this.getTask().getObjectName());
			if (ctpList != null && ctpList.size() > 0) {
				for (CTP ctp : ctpList) {
                    getSqliteConn().insertBObject(ctp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
