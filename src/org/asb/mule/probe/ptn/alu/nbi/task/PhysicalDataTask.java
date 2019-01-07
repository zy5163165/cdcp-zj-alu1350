package org.asb.mule.probe.ptn.alu.nbi.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.asb.mule.probe.framework.entity.CTP;
import org.asb.mule.probe.framework.entity.Equipment;
import org.asb.mule.probe.framework.entity.EquipmentHolder;
import org.asb.mule.probe.framework.entity.PTP;
import org.asb.mule.probe.framework.entity.R_FTP_PTP;
import org.asb.mule.probe.framework.nbi.task.CommonDataTask;
import org.asb.mule.probe.framework.service.Constant;
import org.asb.mule.probe.framework.service.SqliteConn;
import org.asb.mule.probe.framework.service.SqliteService;

import com.alcatelsbell.nms.valueobject.BObject;

public class PhysicalDataTask extends CommonDataTask {
    public PhysicalDataTask(SqliteConn conn) {
        // TODO Auto-generated constructor stub
        this.setSqliteConn(conn);

    }

    public Vector<BObject> excute() {
		try {
			List<EquipmentHolder> holderList = new ArrayList<EquipmentHolder>();
			List<Equipment> cardList = new ArrayList<Equipment>();
			service.retrieveAllEquipmentAndHolders(getTask().getObjectName(), holderList, cardList);
			List<PTP> ptpList = service.retrieveAllPtps(getTask().getObjectName());
			if (holderList != null && holderList.size() > 0) {
				for (EquipmentHolder holder : holderList) {
					getSqliteConn().insertBObject(holder);
				}
			}

			if (cardList != null && cardList.size() > 0) {
				for (Equipment card : cardList) {
					getSqliteConn().insertBObject(card);
				}
			}

			if (ptpList != null && ptpList.size() > 0) {
				for (PTP ptp : ptpList) {
					getSqliteConn().insertBObject(ptp);
				}

				// GTP,PTN需要CTP，OTN不需要
				if (!option) {
					for (PTP ptp : ptpList) {
						try {
							String ptpdn = ptp.getDn();
							if (ptpdn.contains("GTP")) {
								String gtp = ptp.getTag1();
								if (gtp != null) {
									String[] gtps = gtp.split(Constant.listSplitReg);
									for (String ptpname : gtps) {
										R_FTP_PTP rftp = new R_FTP_PTP();
										rftp.setDn(ptpdn + "<>" + ptpname);
										rftp.setFtpDn(ptpdn);
										rftp.setPtpDn(ptpname);

										getSqliteConn().insertBObject(rftp);
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				// CTP,PTN不需CTP，OTN需要
				if (option) {
					for (PTP ptp : ptpList) {
						try {
							List<CTP> ctpList = service.retrieveAllCtps(ptp.getDn());
							if (ctpList != null && ctpList.size() > 0) {
								for (CTP ctp : ctpList) {
									getSqliteConn().insertBObject(ctp);
								}
							}
						} catch (Exception e) {
							nbilog.error("PhysicalDataTask.excute Exception:", e);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public void deleteDate(Vector<BObject> dataList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void insertDate(Vector<BObject> dataList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDate(Vector<BObject> dataList) {
		// TODO Auto-generated method stub

	}

}
