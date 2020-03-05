package org.asb.mule.probe.ptn.alu.nbi.task;

import java.util.List;
import java.util.Vector;

import org.asb.mule.probe.framework.entity.FlowDomainFragment;
import org.asb.mule.probe.framework.nbi.task.CommonDataTask;
import org.asb.mule.probe.framework.service.SqliteConn;
import org.asb.mule.probe.framework.service.SqliteService;

import com.alcatelsbell.nms.valueobject.BObject;

public class FlowDomainFragmentDataTask extends CommonDataTask {

	public FlowDomainFragmentDataTask(SqliteConn conn) {
        // TODO Auto-generated constructor stub
        this.setSqliteConn(conn);

    }


    @Override
	public Vector<BObject> excute() {
		// TODO Auto-generated method stub
		try {
			List<FlowDomainFragment> fdrsList = service.retrieveAllFdrs();
			nbilog.info("FlowDomainFragment : " + fdrsList.size());
			if (fdrsList != null && fdrsList.size() > 0) {
				for (FlowDomainFragment fdrs : fdrsList) {
                    getSqliteConn().insertBObject(fdrs);
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
