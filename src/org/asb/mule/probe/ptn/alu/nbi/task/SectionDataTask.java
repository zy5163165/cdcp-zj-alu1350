package org.asb.mule.probe.ptn.alu.nbi.task;

import java.util.List;
import java.util.Vector;

import org.asb.mule.probe.framework.entity.Section;
import org.asb.mule.probe.framework.nbi.task.CommonDataTask;
import org.asb.mule.probe.framework.service.SqliteConn;
import org.asb.mule.probe.framework.service.SqliteService;

import com.alcatelsbell.nms.valueobject.BObject;

public class SectionDataTask extends CommonDataTask {

	public SectionDataTask(SqliteConn conn) {
        // TODO Auto-generated constructor stub
        this.setSqliteConn(conn);

    }

    @Override
	public Vector<BObject> excute() {
		// TODO Auto-generated method stub
		try {
			List<Section> sectionList = service.retrieveAllSections();
			nbilog.info("Section : " + sectionList.size());
			if (sectionList != null && sectionList.size() > 0) {
				for (Section section : sectionList) {
                    getSqliteConn().insertBObject(section);
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
