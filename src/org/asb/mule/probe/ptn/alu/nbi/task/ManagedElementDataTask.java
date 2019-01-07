package org.asb.mule.probe.ptn.alu.nbi.task;

import java.util.List;
import java.util.Vector;

import org.asb.mule.probe.framework.entity.ManagedElement;
import org.asb.mule.probe.framework.nbi.task.CommonDataTask;
import org.asb.mule.probe.framework.service.SqliteConn;
import org.asb.mule.probe.framework.service.SqliteService;

import com.alcatelsbell.nms.valueobject.BObject;

public class ManagedElementDataTask extends CommonDataTask {
    public ManagedElementDataTask(SqliteConn conn) {
        // TODO Auto-generated constructor stub
        this.setSqliteConn(conn);

    }

    public Vector<BObject> excute() {
		List<ManagedElement> neList = service.retrieveAllManagedElements();

		Vector<BObject> neVec = new Vector<BObject>();
		try {
			if (neList != null && neList.size() > 0) {
				nbilog.info("ManagedElement : " + neList.size());
				for (ManagedElement ne : neList) {
                    getSqliteConn().insertBObject(ne);
					neVec.add(ne);
				}

			}

		} catch (Throwable e) {
			e.printStackTrace();
		}
		return neVec;

	}

	@Override
	public void insertDate(Vector<BObject> dataList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteDate(Vector<BObject> dataList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDate(Vector<BObject> dataList) {
		// TODO Auto-generated method stub

	}

}
