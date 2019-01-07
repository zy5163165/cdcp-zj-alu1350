package org.asb.mule.probe.ptn.alu.nbi.task;

import java.util.List;
import java.util.Vector;

import org.asb.mule.probe.framework.entity.R_FTP_PTP;
import org.asb.mule.probe.framework.nbi.task.CommonDataTask;
import org.asb.mule.probe.framework.service.SqliteConn;
import org.asb.mule.probe.framework.service.SqliteService;

import com.alcatelsbell.nms.valueobject.BObject;

public class FTPAndPTPDataTask extends CommonDataTask {
    public FTPAndPTPDataTask(SqliteConn conn) {
        // TODO Auto-generated constructor stub
        this.setSqliteConn(conn);

    }


    @Override
	public Vector<BObject> excute() {
		// TODO Auto-generated method stub
		try {
			List<R_FTP_PTP> resultList = service.retrieveAllPTPsByFtp(this.getTask().getObjectName());
			if (resultList != null && resultList.size() > 0) {
				for (R_FTP_PTP item : resultList) {
                    getSqliteConn().insertBObject(item);
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
