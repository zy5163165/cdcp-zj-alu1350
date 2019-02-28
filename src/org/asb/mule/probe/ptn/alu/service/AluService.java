package org.asb.mule.probe.ptn.alu.service;

import com.alcatelsbell.cdcp.nodefx.exception.EmsDataIllegalException;
import com.alcatelsbell.cdcp.nodefx.exception.EmsFunctionInvokeException;
import equipment.EquipmentHolder_T;
import equipment.EquipmentOrHolder_T;
import equipment.EquipmentTypeQualifier_T;
import equipment.Equipment_T;
import flowDomain.FlowDomain_T;
import flowDomainFragment.FlowDomainFragment_T;
import globaldefs.NameAndStringValue_T;
import globaldefs.ProcessingFailureException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import managedElement.ManagedElement_T;
import multiLayerSubnetwork.MultiLayerSubnetwork_T;

import org.asb.mule.probe.framework.entity.CTP;
import org.asb.mule.probe.framework.entity.CrossConnect;
import org.asb.mule.probe.framework.entity.Equipment;
import org.asb.mule.probe.framework.entity.EquipmentHolder;
import org.asb.mule.probe.framework.entity.FlowDomain;
import org.asb.mule.probe.framework.entity.FlowDomainFragment;
import org.asb.mule.probe.framework.entity.IPCrossconnection;
import org.asb.mule.probe.framework.entity.ManagedElement;
import org.asb.mule.probe.framework.entity.PTP;
import org.asb.mule.probe.framework.entity.ProtectionGroup;
import org.asb.mule.probe.framework.entity.R_FTP_PTP;
import org.asb.mule.probe.framework.entity.Section;
import org.asb.mule.probe.framework.entity.SubnetworkConnection;
import org.asb.mule.probe.framework.entity.TrafficTrunk;
import org.asb.mule.probe.framework.entity.TrailNtwProtection;
import org.asb.mule.probe.framework.service.NbiService;
import org.asb.mule.probe.framework.util.CodeTool;
import org.asb.mule.probe.framework.util.FileLogger;
import org.asb.mule.probe.ptn.alu.sbi.mgrhandler.EMSMgrHandler;
import org.asb.mule.probe.ptn.alu.sbi.mgrhandler.EquipmentInventoryMgrHandler;
import org.asb.mule.probe.ptn.alu.sbi.mgrhandler.ExtendServiceMgrHandler;
import org.asb.mule.probe.ptn.alu.sbi.mgrhandler.FlowDomainMgrHandler;
import org.asb.mule.probe.ptn.alu.sbi.mgrhandler.ManagedElementMgrHandler;
import org.asb.mule.probe.ptn.alu.sbi.mgrhandler.ProtectionMgrHandler;
import org.asb.mule.probe.ptn.alu.sbi.mgrhandler.SubnetworkMgrHandler;
import org.asb.mule.probe.ptn.alu.sbi.service.CorbaService;
import org.asb.mule.probe.ptn.alu.service.mapper.CommonMapper;
import org.asb.mule.probe.ptn.alu.service.mapper.CtpMapper;
import org.asb.mule.probe.ptn.alu.service.mapper.EquipmentHolderMapper;
import org.asb.mule.probe.ptn.alu.service.mapper.EquipmentMapper;
import org.asb.mule.probe.ptn.alu.service.mapper.FlowDomainFragmentMapper;
import org.asb.mule.probe.ptn.alu.service.mapper.FlowDomainMapper;
import org.asb.mule.probe.ptn.alu.service.mapper.IPCrossconnectionMapper;
import org.asb.mule.probe.ptn.alu.service.mapper.ManagedElementMapper;
import org.asb.mule.probe.ptn.alu.service.mapper.ProtectGroupMapper;
import org.asb.mule.probe.ptn.alu.service.mapper.PtpMapper;
import org.asb.mule.probe.ptn.alu.service.mapper.SectionMapper;
import org.asb.mule.probe.ptn.alu.service.mapper.SubnetworkConnectionMapper;
import org.asb.mule.probe.ptn.alu.service.mapper.VendorDNFactory;

import subnetworkConnection.CrossConnect_T;
import subnetworkConnection.SubnetworkConnection_T;
import terminationPoint.GTP_T;
import terminationPoint.TerminationPoint_T;
import topologicalLink.TopologicalLink_T;
import trailNtwProtection.TrailNtwProtection_T;

import com.alcatelsbell.nms.util.ObjectUtil;

public class AluService implements NbiService {

	private CorbaService corbaService;
	private FileLogger sbilog = null;
	private FileLogger errorlog = null;

	private String key;

	public String getServiceName() {

		return corbaService.getEmsName();

	}

	public AluService() {
	}

	@Override
	public String getEmsName() {
		// TODO Auto-generated method stub
		sbilog = corbaService.getSbilog();
		errorlog = corbaService.getErrorlog();

		return corbaService.getEmsName();
	}

	public void setCorbaService(CorbaService corbaService) {
		sbilog = corbaService.getSbilog();
		errorlog = corbaService.getErrorlog();
		this.corbaService = corbaService;
	}

	public CorbaService getCorbaService() {
		return corbaService;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public boolean ping() {

		return corbaService.getNmsSession().isEmsSessionOK();
	}

	public String getKey() {
		return key;
	}

	// 1.閿熸枻鎷峰幓閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷峰厓
	public List<ManagedElement> retrieveAllManagedElements() {
		ManagedElement_T[] vendorNeList = null;
		List<ManagedElement> neList = new ArrayList();
		try {
			vendorNeList = ManagedElementMgrHandler.instance().retrieveAllManagedElements(corbaService.getNmsSession().getManagedElementMgr());
		} catch (ProcessingFailureException e) {
			corbaService.handleException(new EmsFunctionInvokeException("retrieveAllManagedElements",e));
			errorlog.error("retrieveAllManagedElements ProcessingFailureException: " + CodeTool.isoToGbk(e.errorReason), e);
		}
		if (vendorNeList == null || vendorNeList.length == 0)
			corbaService.handleException(new EmsDataIllegalException("Managedelement",null," size = 0 "));

		if (vendorNeList != null && vendorNeList.length > 0) {
			corbaService.handleExceptionRecover(EmsDataIllegalException.EXCEPTION_CODE+"Managedelement");
			corbaService.handleExceptionRecover(EmsFunctionInvokeException.EXCEPTION_CODE+"retrieveAllManagedElements");

			for (ManagedElement_T vendorNe : vendorNeList) {
				try {
					sbilog.info("vendorNe : " + CodeTool.isoToGbk(vendorNe.toString()));
					if (vendorNe.productName.contains("External Network")) {
						sbilog.info("VirtualNE : " + vendorNe);
						continue;
					}
					ManagedElement ne = ManagedElementMapper.instance().convertManagedElement(vendorNe);
					neList.add(ne);
				} catch (Exception e) {
					errorlog.error("retrieveAllManagedElements convertException: ", e);
				}
			}
		}
		sbilog.info("retrieveAllManagedElements : " + neList.size());
		return neList;
	}

	/**
	 * 2.閿熸枻鎷峰幓鏌愰敓鏂ゆ嫹鍏冮敓鏂ゆ嫹閿熸枻鎷烽敓鍙鎷烽敓鏂ゆ嫹
	 * 
	 * @return
	 */
	public List<Equipment> retrieveAllEquipments(String neName) {
		Equipment_T[] vendorCardList = null;
		List<Equipment> cardList = new ArrayList<Equipment>();
		try {
			// String[] neNameList = neName.split(Constant.dnSplit);
			NameAndStringValue_T[] neDn = VendorDNFactory.createCommonDN(neName);

			vendorCardList = EquipmentInventoryMgrHandler.instance().retrieveAllEquipments(corbaService.getNmsSession().getEquipmentInventoryMgr(), neDn);
		} catch (ProcessingFailureException e) {
			errorlog.error("retrieveAllEquipments ProcessingFailureException: " + CodeTool.isoToGbk(e.errorReason), e);
		}
		if (vendorCardList != null) {
			for (Equipment_T vendorCard : vendorCardList) {
				try {
					Equipment card = EquipmentMapper.instance().convertEquipment(vendorCard, neName);
					cardList.add(card);
				} catch (Exception e) {
					errorlog.error("retrieveAllEquipments convertException: ", e);
				}

			}
		}
		sbilog.info("retrieveAllEquipments : " + cardList.size());
		return cardList;

	}

	/**
	 * 3.閿熸枻鎷峰幓鏌愰敓鏂ゆ嫹鍏冮敓鏂ゆ嫹閿熸枻鎷烽敓鍙啱鍖℃嫹閿燂拷
	 * 
	 * @return
	 */
	public List<EquipmentHolder> retrieveAllEquipmentHolders(String neName) {
		EquipmentHolder_T[] vendorHolderList = null;
		List<EquipmentHolder> holderList = new ArrayList();
		try {

			// String[] neNameList = neName.split(Constant.dnSplit);
			NameAndStringValue_T[] neDn = VendorDNFactory.createCommonDN(neName);

			vendorHolderList = EquipmentInventoryMgrHandler.instance().retrieveAllEquipmentHolders(corbaService.getNmsSession().getEquipmentInventoryMgr(),
					neDn);
		} catch (ProcessingFailureException e) {
			errorlog.error("retrieveAllEquipmentHolders ProcessingFailureException: " + CodeTool.isoToGbk(e.errorReason), e);
		}
		if (vendorHolderList != null) {
			for (EquipmentHolder_T vendorHolder : vendorHolderList) {
				try {
					EquipmentHolder card = EquipmentHolderMapper.instance().convertEquipmentHolder(vendorHolder, neName);
					holderList.add(card);
				} catch (Exception e) {
					errorlog.error("retrieveAllEquipmentHolders convertException: ", e);
				}

			}
		}
		sbilog.info("retrieveAllEquipmentHolders : " + holderList.size());
		return holderList;
	}

	public void retrieveAllEquipmentAndHolders(String neName, List<EquipmentHolder> equipmentHolderList, List<Equipment> equipmentList) {
		EquipmentOrHolder_T[] equipmentOrHolderList = null;
		NameAndStringValue_T[] neDn = VendorDNFactory.createCommonDN(neName);
		try {
			equipmentOrHolderList = EquipmentInventoryMgrHandler.instance().retrieveAllEquipmentAndHolders(
					corbaService.getNmsSession().getEquipmentInventoryMgr(), neDn);
		} catch (ProcessingFailureException e) {
			errorlog.error(neName + " retrieveAllEquipmentAndHolders ProcessingFailureException: " + CodeTool.isoToGbk(e.errorReason), e);
			// once again
			try {
				Thread.sleep(120000L);
			} catch (InterruptedException e2) {
				errorlog.error("retrieveAllEquipmentAndHolders1 InterruptedException: ", e2);
			}
			try {
				equipmentOrHolderList = EquipmentInventoryMgrHandler.instance().retrieveAllEquipmentAndHolders(
						corbaService.getNmsSession().getEquipmentInventoryMgr(), neDn);
			} catch (ProcessingFailureException e1) {
				errorlog.error(neName + " retrieveAllEquipmentAndHolders1 ProcessingFailureException: " + CodeTool.isoToGbk(e.errorReason), e);
			} catch (org.omg.CORBA.SystemException e1) {
				errorlog.error("retrieveAllEquipmentAndHolders1 CORBA.SystemException: " + e.getMessage(), e1);
			}
		} catch (org.omg.CORBA.SystemException e) {
			errorlog.error(neName + " retrieveAllEquipmentAndHolders CORBA.SystemException: " + e.getMessage(), e);
		}
		if (equipmentOrHolderList != null) {
			for (EquipmentOrHolder_T equipmentOrHolder : equipmentOrHolderList) {
				try {
					if (equipmentOrHolder.discriminator().equals(EquipmentTypeQualifier_T.EQT_HOLDER)) {
						EquipmentHolder holder = EquipmentHolderMapper.instance().convertEquipmentHolder(equipmentOrHolder.holder(), neName);
						equipmentHolderList.add(holder);
					} else if (equipmentOrHolder.discriminator().equals(EquipmentTypeQualifier_T.EQT)) {
						Equipment card = EquipmentMapper.instance().convertEquipment(equipmentOrHolder.equip(), neName);
						equipmentList.add(card);
					}
				} catch (Exception e) {
					errorlog.error("retrieveAllEquipmentAndHolders convertException: ", e);
				}
			}
		}
		sbilog.info(neName + " retrieveAllEquipmentAndHolders EquipmentHolders: " + equipmentHolderList.size());
		List<EquipmentHolder> dsnc = new ArrayList<EquipmentHolder>();
		for (int i = 0; i < equipmentHolderList.size() - 1; i++) {
			for (int j = i + 1; j < equipmentHolderList.size(); j++) {
				if (equipmentHolderList.get(i).getDn().equals(equipmentHolderList.get(j).getDn())) {
					dsnc.add(equipmentHolderList.get(j));
					sbilog.error("duplicate equipmentHolder: " + equipmentHolderList.get(j).getDn());
					errorlog.error("duplicate equipmentHolder: " + equipmentHolderList.get(j).getDn());
				}
			}
		}
		sbilog.info("duplicate equipmentHolder: " + dsnc.size());
		equipmentHolderList.removeAll(dsnc);
		sbilog.info(neName + " retrieveAllEquipmentAndHolders Equipments: " + equipmentList.size());
		List<Equipment> dcard = new ArrayList<Equipment>();
		for (int i = 0; i < equipmentList.size() - 1; i++) {
			for (int j = i + 1; j < equipmentList.size(); j++) {
				if (equipmentList.get(i).getDn().equals(equipmentList.get(j).getDn())) {
					dcard.add(equipmentList.get(j));
					sbilog.error("duplicate equipment: " + equipmentList.get(j).getDn());
					errorlog.error("duplicate equipment: " + equipmentList.get(j).getDn());
				}
			}
		}
		sbilog.info("duplicate equipment: " + dcard.size());
		equipmentList.removeAll(dcard);
		sbilog.info(neName + " retrieveAllEquipmentAndHolders EquipmentHolders: " + equipmentHolderList.size());
		sbilog.info(neName + " retrieveAllEquipmentAndHolders Equipments: " + equipmentList.size());

	}

	public List<PTP> retrieveAllPtps(String neName) {
		// List<TerminationPoint_T> vendorPtpList = new ArrayList<TerminationPoint_T>();
		List<PTP> ptpList = new ArrayList<PTP>();
		TerminationPoint_T[] vendorPtps = null;
        TerminationPoint_T[] vendorFtps = null;
		GTP_T[] vendorGtps = null;
		NameAndStringValue_T[] neDn = VendorDNFactory.createCommonDN(neName);
		short[] tpLayerRateList = new short[0];
		short[] connectionLayerRateList = new short[0];
		try {
			vendorPtps = ManagedElementMgrHandler.instance().retrieveAllPTPs
                    (corbaService.getNmsSession().getManagedElementMgr(), neDn, tpLayerRateList,
					connectionLayerRateList);
			vendorFtps = ManagedElementMgrHandler.instance().retrieveAllFTPs
                     (corbaService.getNmsSession().getManagedElementMgr(), neDn,
			 tpLayerRateList, connectionLayerRateList);
			// if (vendorPtps != null) {
			// vendorPtpList.addAll(Arrays.asList(vendorPtps));
			// }
			// if (vendorFtps != null) {
			// for (TerminationPoint_T tp : vendorFtps) {
			// ManagedElementMgrHandler.instance().retrieveContainingTPs(corbaService.getNmsSession().getManagedElementMgr(), neDn)
			// }
			// vendorPtpList.addAll(Arrays.asList(vendorFtps));
			// }
		} catch (ProcessingFailureException e) {
			errorlog.error("retrieveAllPtps ProcessingFailureException: " + CodeTool.isoToGbk(e.errorReason), e);
		} catch (org.omg.CORBA.SystemException e) {
			errorlog.error("retrieveAllPtps CORBA.SystemException: " + e.getMessage(), e);
		}
//		try {
//			vendorGtps = ManagedElementMgrHandler.instance().retrieveAllGTPs(corbaService.getNmsSession().getManagedElementMgr(), neDn, tpLayerRateList);
//		} catch (ProcessingFailureException e) {
//			errorlog.error("retrieveAllGTPs ProcessingFailureException: " + CodeTool.isoToGbk(e.errorReason), e);
//		} catch (org.omg.CORBA.SystemException e) {
//			errorlog.error("retrieveAllGTPs CORBA.SystemException: " + e.getMessage(), e);
//		}
		if (vendorPtps != null) {
			for (TerminationPoint_T vendorPtp : vendorPtps) {
				try {
					PTP ptp = PtpMapper.instance().convertPtp(vendorPtp, neName);
					ptpList.add(ptp);
				} catch (Exception e) {
					errorlog.error("retrieveAllPtps convertException: ", e);
				}
			}
		}
        if (vendorFtps != null) {
            sbilog.info("ftpsize="+vendorFtps.length);
            for (TerminationPoint_T vendorPtp : vendorFtps) {
                try {
                    PTP ptp = PtpMapper.instance().convertPtp(vendorPtp, neName);
                    ptpList.add(ptp);
                } catch (Exception e) {
                    errorlog.error("retrieveAllPtps convertException: ", e);
                }
            }
        }
		if (vendorGtps != null) {
			for (GTP_T vendorPtp : vendorGtps) {
				try {
					PTP ptp = PtpMapper.instance().convertGtp(vendorPtp, neName);
					ptpList.add(ptp);
				} catch (Exception e) {
					errorlog.error("retrieveAllPtps convertException: ", e);
				}
			}
		}
		sbilog.info(neName + " retrieveAllPtps : " + ptpList.size());
		// List<PTP> dsnc = new ArrayList<PTP>();
		// for (int i = 0; i < ptpList.size() - 1; i++) {
		// for (int j = i + 1; j < ptpList.size(); j++) {
		// if (ptpList.get(i).getDn().equals(ptpList.get(j).getDn())) {
		// dsnc.add(ptpList.get(j));
		// sbilog.error("duplicate PTP: " + ptpList.get(j).getDn());
		// errorlog.error("duplicate PTP: " + ptpList.get(j).getDn());
		// }
		// }
		// }
		// sbilog.info("duplicate ptp: " + dsnc.size());
		// ptpList.removeAll(dsnc);
		// sbilog.info(neName + " retrieveAllPtps : " + ptpList.size());
		return ptpList;
	}

	public List<CTP> retrieveAllCtps(String ptpName) {
		TerminationPoint_T[] vendorCtpList = null;
		List<CTP> ctpList = new ArrayList();
		try {
			NameAndStringValue_T[] ptpDn = VendorDNFactory.createCommonDN(ptpName);

//			vendorCtpList = ManagedElementMgrHandler.instance().retrieveContainedPotentialTPs(corbaService.getNmsSession().getManagedElementMgr(), ptpDn,
//					new short[0]);
            vendorCtpList = ManagedElementMgrHandler.instance().retrieveContainedCurrentTPs(corbaService.getNmsSession().getManagedElementMgr(), ptpDn,
                    new short[0]);

			if (ptpName.contains("@FTP:")) {
				if (vendorCtpList != null) {
					for (TerminationPoint_T terminationPoint_t : vendorCtpList) {

					}
				}
			}

		} catch (ProcessingFailureException e) {
			errorlog.error("retrieveAllCtps ProcessingFailureException: " + CodeTool.isoToGbk(e.errorReason), e);
		}
		if (vendorCtpList != null) {
			for (TerminationPoint_T vendorctp : vendorCtpList) {
				try {
					CTP ctp = CtpMapper.instance().convertCtp(vendorctp, ptpName);
					ctpList.add(ctp);
				} catch (Exception e) {
					errorlog.error("retrieveAllCtps convertException: ", e);
				}
			}
		}
		sbilog.info("retrieveAllCtps : " + ctpList.size());
		return ctpList;
	}

	public List<Section> retrieveAllSections() {
		List<Section> sectionList = new ArrayList();
		List<TopologicalLink_T> sections = new ArrayList<TopologicalLink_T>();
		// NameAndStringValue_T[] subnetDn = VendorDNFactory.createSubnetworkDN(corbaService.getEmsDn(), "PTN");
		try {
//			MultiLayerSubnetwork_T[] subs = EMSMgrHandler.instance().retrieveAllTopLevelSubnetworks(corbaService.getNmsSession().getEmsMgr());
//			for (MultiLayerSubnetwork_T sub : subs) {
//				try {
//					TopologicalLink_T[] vendorSectionList = SubnetworkMgrHandler.instance().retrieveAllTopologicalLinks(
//							corbaService.getNmsSession().getMultiLayerSubnetworkMgr(), sub.name, errorlog);
//					sections.addAll(Arrays.asList(vendorSectionList));
//				} catch (ProcessingFailureException e) {
//					errorlog.error("retrieveAllSections ProcessingFailureException: " + CodeTool.isoToGbk(e.errorReason), e);
//				} catch (org.omg.CORBA.SystemException e) {
//					errorlog.error("retrieveAllSections CORBA.SystemException: " + e.getMessage(), e);
//				}
//			}
			NameAndStringValue_T[] subnetDnSdh = VendorDNFactory.createSubnetworkDN(corbaService.getEmsDn(), "SDH");
			NameAndStringValue_T[] subnetDnOtn = VendorDNFactory.createSubnetworkDN(corbaService.getEmsDn(), "OTN");
			TopologicalLink_T[] vendorSectionListSdh = SubnetworkMgrHandler.instance().retrieveAllTopologicalLinks(
					corbaService.getNmsSession().getMultiLayerSubnetworkMgr(), subnetDnSdh, errorlog);
			TopologicalLink_T[] vendorSectionListOtn = SubnetworkMgrHandler.instance().retrieveAllTopologicalLinks(
					corbaService.getNmsSession().getMultiLayerSubnetworkMgr(), subnetDnOtn, errorlog);
			sections.addAll(Arrays.asList(vendorSectionListSdh));
			sections.addAll(Arrays.asList(vendorSectionListOtn));
		} catch (ProcessingFailureException e) {
			errorlog.error("retrieveAllSections ProcessingFailureException: " + CodeTool.isoToGbk(e.errorReason), e);
		} catch (org.omg.CORBA.SystemException e) {
			errorlog.error("retrieveAllSections CORBA.SystemException: " + e.getMessage(), e);
		}
		if (sections != null) {
			for (TopologicalLink_T vendorSection : sections) {
				try {
					Section section = SectionMapper.instance().convertSection(vendorSection);
					sectionList.add(section);
				} catch (Exception e) {
					errorlog.error("retrieveAllSections convertException: ", e);
				}
			}
		}
		sbilog.info("retrieveAllSections : " + sectionList.size());
		return sectionList;
	}

	public List<FlowDomainFragment> retrieveAllFdrs() {
		FlowDomainFragment_T[] vendorFdrList = null;
		List<FlowDomainFragment> fdrList = new ArrayList<FlowDomainFragment>();
		try {
			FlowDomain_T[] fds = FlowDomainMgrHandler.instance().retrieveAllFlowDomains(corbaService.getNmsSession().getFlowDomainMgr());
			for (FlowDomain_T fd : fds) {
				NameAndStringValue_T[] fdDn = fd.name;
				short[] rates = new short[0];
				try {
					vendorFdrList = FlowDomainMgrHandler.instance().retrieveAllFDFrs(corbaService.getNmsSession().getFlowDomainMgr(), rates, fdDn);
				} catch (ProcessingFailureException e) {
					errorlog.error("retrieveAllFdrs ProcessingFailureException: " + CodeTool.isoToGbk(e.errorReason), e);
				} catch (org.omg.CORBA.SystemException e) {
					errorlog.error("retrieveAllFdrs CORBA.SystemException: " + e.getMessage(), e);
				}
				sbilog.info(CommonMapper.nv2dn(fd.name));
				sbilog.info("AllFdrs COUNTS: " + vendorFdrList.length);
				for (int i = 0; i < vendorFdrList.length; i++) {
					sbilog.info("The " + i + " fdfr: " + vendorFdrList[i]);
				}

				if (vendorFdrList != null) {
					for (FlowDomainFragment_T vendorFdr : vendorFdrList) {
						try {
							FlowDomainFragment fdr = FlowDomainFragmentMapper.instance().convertFlowDomainFragment(vendorFdr);
							fdrList.add(fdr);

							TopologicalLink_T[] topos = null;
							try {
								topos = ExtendServiceMgrHandler.instance().retrieveTopologicalLinksOfFDFr(corbaService.getNmsSession().getExtendServiceMgr(),
										vendorFdr.name);
							} catch (ProcessingFailureException e) {
								errorlog.error("retrieveTopologicalLinksOfFDFr ProcessingFailureException: " + CodeTool.isoToGbk(e.errorReason), e);
							} catch (org.omg.CORBA.SystemException e) {
								errorlog.error("retrieveTopologicalLinksOfFDFr CORBA.SystemException: " + e.getMessage(), e);
							}
							if (topos != null && topos.length > 0) {
								sbilog.info(CommonMapper.nv2dn(vendorFdr.name));
								List<NameAndStringValue_T[]> namelist = new ArrayList<NameAndStringValue_T[]>();
								List<String> nativeEMSNamelist = new ArrayList<String>();
								for (int i = 0; i < topos.length; i++) {
									sbilog.info("The " + i + " topo: " + topos[i]);
									namelist.add(topos[i].name);
									nativeEMSNamelist.add(topos[i].nativeEMSName);
								}
								NameAndStringValue_T[][] names = new NameAndStringValue_T[namelist.size()][];
								namelist.toArray(names);
								StringBuilder buff = new StringBuilder();
								for (String name : nativeEMSNamelist) {
									buff.append("||");
									buff.append(name);
								}
								String nativeEMSName = buff.substring(2);

								fdr.setParentDn(CommonMapper.nvs2dn(names));
								fdr.setTag1(nativeEMSName);
							}
						} catch (Exception e) {
							errorlog.error("retrieveAllFdrs convertException: ", e);
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return fdrList;
	}

	public List<FlowDomain> retrieveAllFlowDomain() {
		FlowDomain_T[] vendorFdList = null;
		List<FlowDomain> fdList = new ArrayList();
		try {
			vendorFdList = FlowDomainMgrHandler.instance().retrieveAllFlowDomains(corbaService.getNmsSession().getFlowDomainMgr());
			sbilog.info("retrieveAllFlowDomain : " + vendorFdList.length);
		} catch (ProcessingFailureException e) {
			errorlog.error("retrieveAllFlowDomain ProcessingFailureException: " + CodeTool.isoToGbk(e.errorReason), e);
		}
		if (vendorFdList != null) {
			for (FlowDomain_T vendorFd : vendorFdList) {
				try {
					FlowDomain fd = FlowDomainMapper.instance().convertFlowDomain(vendorFd);
					fdList.add(fd);
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		}

		return fdList;
	}

	public List<SubnetworkConnection> retrieveAllSNCs() {
		List<SubnetworkConnection> sncList = new ArrayList<SubnetworkConnection>();
		List<SubnetworkConnection_T> snclist = new ArrayList<SubnetworkConnection_T>();
//		SubnetworkConnection_T[] sncs = null;
		SubnetworkConnection_T[] otnSncs = null;
		try {
			// NameAndStringValue_T[][] subs = EMSMgrHandler.instance().retrieveAllTopLevelSubnetworkNames(corbaService.getNmsSession().getEmsMgr());
//			MultiLayerSubnetwork_T[] subs = EMSMgrHandler.instance().retrieveAllTopLevelSubnetworks(corbaService.getNmsSession().getEmsMgr());
//			for (MultiLayerSubnetwork_T sub : subs) {
//				try {
//					sbilog.info(sub);
//					sncs = SubnetworkMgrHandler.instance().retrieveAllSNCs(corbaService.getNmsSession().getMultiLayerSubnetworkMgr(), sub.name, new short[0]);
//					snclist.addAll(Arrays.asList(sncs));
//				} catch (ProcessingFailureException e) {
//					errorlog.error("retrieveAllSNCs ProcessingFailureException: " + CodeTool.isoToGbk(e.errorReason), e);
//				} catch (org.omg.CORBA.SystemException e) {
//					errorlog.error("retrieveAllSNCs CORBA.SystemException: " + e.getMessage(), e);
//				}
//			}
			NameAndStringValue_T[] subnetworkNameOtn = VendorDNFactory.createSubnetworkDN(corbaService.getEmsDn(), "OTN");
			for (NameAndStringValue_T subnetworkNameOtn1 : subnetworkNameOtn) {
				sbilog.info("retrieveAllOtnSNCs name: " + subnetworkNameOtn1.name);
				sbilog.info("retrieveAllOtnSNCs value: " + subnetworkNameOtn1.value);
			}
			sbilog.info("retrieveAllOtnSNCs : " + subnetworkNameOtn.toString());
			otnSncs = SubnetworkMgrHandler.instance().retrieveAllSNCs(corbaService.getNmsSession().getMultiLayerSubnetworkMgr(), subnetworkNameOtn, new short[0]);
			if (null != otnSncs) {
				snclist.addAll(Arrays.asList(otnSncs));
			} else {
				sbilog.info("retrieveAllOtnSNCs size = 0");
			}
		} catch (ProcessingFailureException e) {
			errorlog.error("retrieveAllSNCs ProcessingFailureException: " + CodeTool.isoToGbk(e.errorReason), e);
		} catch (org.omg.CORBA.SystemException e) {
			errorlog.error("retrieveAllSNCs CORBA.SystemException: " + e.getMessage(), e);
		}
		sbilog.info("retrieveAllOtnSNCs : size-" + otnSncs==null?0:otnSncs.length);
//		sbilog.info("retrieveAllOtnSNCs : " + otnSncs.length);
		// for (int i = 0; i < sncs.length; i++) {
		// sbilog.debug("The " + i + " snc: " + sncs[i]);
		// }
		if (snclist != null) {
			for (SubnetworkConnection_T snc : snclist) {
				try {
					sncList.add(SubnetworkConnectionMapper.instance().convertSNC(snc));
				} catch (Exception e) {
					errorlog.error("retrieveAllSNCs convertException: ", e);
				}
			}
		}
		List<SubnetworkConnection> dsnc = new ArrayList<SubnetworkConnection>();
		for (int i = 0; i < sncList.size() - 1; i++) {
			for (int j = i + 1; j < sncList.size(); j++) {
				if (sncList.get(i).getDn().equals(sncList.get(j).getDn())) {
					dsnc.add(sncList.get(j));
					sbilog.error("duplicate snc: " + sncList.get(j).getDn());
					errorlog.error("duplicate snc: " + sncList.get(j).getDn());
				}
			}
		}
		sbilog.info("duplicate snc: " + dsnc.size());
		sncList.removeAll(dsnc);
		sbilog.info("retrieveAllSNCs : " + sncList.size());
		return sncList;
	}

	@Override
	public void retrieveRouteAndTopologicalLinks(String sncName, List<CrossConnect> ccList, List<Section> sectionList) {
		subnetworkConnection.Route_THolder normalRoute = new subnetworkConnection.Route_THolder();
		topologicalLink.TopologicalLinkList_THolder topologicalLinkList = new topologicalLink.TopologicalLinkList_THolder();
		NameAndStringValue_T[] vendorSncName = VendorDNFactory.createCommonDN(sncName);
		try {
			corbaService.getNmsSession().getMultiLayerSubnetworkMgr().getRouteAndTopologicalLinks(vendorSncName, normalRoute, topologicalLinkList);
		} catch (ProcessingFailureException e) {
			errorlog.error(sncName + " retrieveRouteAndTopologicalLinks ProcessingFailureException: " + CodeTool.isoToGbk(e.errorReason), e);
		} catch (org.omg.CORBA.SystemException e) {
			errorlog.error("retrieveRouteAndTopologicalLinks CORBA.SystemException: " + e.getMessage(), e);
		}
		for (subnetworkConnection.CrossConnect_T cc : normalRoute.value) {
			try {
				ccList.add(IPCrossconnectionMapper.instance().convertCrossConnect(cc, sncName));
			} catch (Exception e) {
				errorlog.error("retrieveRouteAndTopologicalLinks convertException: ", e);
			}
		}

		for (topologicalLink.TopologicalLink_T section : topologicalLinkList.value) {
			try {
				sectionList.add(SectionMapper.instance().convertSection(section));
			} catch (Exception e) {
				errorlog.error("retrieveRouteAndTopologicalLinks convertException: ", e);
			}
		}
		sbilog.info(sncName + " retrieveRouteAndTopologicalLinks ccList: " + ccList.size());
		sbilog.info(sncName + " retrieveRouteAndTopologicalLinks sectionList: " + sectionList.size());

	}

	@Override
	public boolean connect() {
		return corbaService.connect();

	}

	@Override
	public boolean disconnect() {
		return corbaService.disconnect();
	}

	@Override
	public boolean getConnectState() {
		// TODO Auto-generated method stub
		return corbaService.isConnectState();
	}

	@Override
	public String getLastestDayMigrationJobName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IPCrossconnection> retrieveRoute(String trafficTrunkName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<R_FTP_PTP> retrieveAllPTPsByFtp(String ftpName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProtectionGroup> retrieveAllProtectionGroupByMe(String meDn) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {
		List<FlowDomainFragment_T> list = (List) ObjectUtil.readObjectByPath("./cache/AllFdrs");
		for (FlowDomainFragment_T fdfrs : list) {
			System.out.println(fdfrs);
		}
	}

	@Override
	public List<CrossConnect> retrieveAllCrossConnects(String neName) {
		List<CrossConnect> ccList = new ArrayList<CrossConnect>();
		CrossConnect_T[] ccs = null;
		NameAndStringValue_T[] neDn = VendorDNFactory.createCommonDN(neName);
		try {
			short[] layer = null;
			ManagedElement_T ne = ManagedElementMgrHandler.instance().retrieveManagedElement(corbaService.getNmsSession().getManagedElementMgr(), neDn);
			layer = ne.supportedRates;
			if (layer == null || layer.length == 0) {
				layer = new short[] { 339, 99, 42, 108, 40, 109, 41, 106, 107, 104, 105, 50, 87, 49, 334, 27 };
			}

            layer = new short[0];
			ccs = ManagedElementMgrHandler.instance().retrieveAllCrossConnections(corbaService.getNmsSession().getManagedElementMgr(), neDn, layer);
		} catch (ProcessingFailureException e) {
			errorlog.error("retrieveAllCrossconnections ProcessingFailureException: " + neName + CodeTool.isoToGbk(e.errorReason), e);
		} catch (org.omg.CORBA.SystemException e) {
			errorlog.error("retrieveAllCrossconnections CORBA.SystemException: " + neName + e.getMessage(), e);
		}
		if (ccs != null) {
			for (CrossConnect_T vendorIPCc : ccs) {
				try {
					CrossConnect ipCC = IPCrossconnectionMapper.instance().convertCrossConnect(vendorIPCc, neName);
					ccList.add(ipCC);
				} catch (Exception e) {
					errorlog.error("retrieveAllCrossconnections convertException: ", e);
				}
			}
		}
		sbilog.debug(neName + " CrossConnectList : " + ccList.size());
		return ccList;
	}

	@Override
	public List<IPCrossconnection> retrieveAllCrossconnections(String neName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TrafficTrunk> retrieveAllTrafficTrunk() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TrailNtwProtection> retrieveAllTrailNtwProtections() {
		List<TrailNtwProtection> tpglist = new ArrayList<TrailNtwProtection>();
		// Map<String, TrailNtwProtection> map = new HashMap<String, TrailNtwProtection>();
		// NameAndStringValue_T[][] neNames = null;
		// try {
		// neNames = ManagedElementMgrHandler.instance().retrieveAllManagedElementNames(corbaService.getNmsSession().getManagedElementMgr());
		// } catch (ProcessingFailureException e) {
		// errorlog.error("retrieveAllManagedElementNames ProcessingFailureException: " + CodeTool.isoToGbk(e.errorReason), e);
		// } catch (org.omg.CORBA.SystemException e) {
		// errorlog.error("retrieveAllManagedElementNames CORBA.SystemException: " + e.getMessage(), e);
		// }
		// if (neNames != null) {
		// for (NameAndStringValue_T[] meName : neNames) {
		TrailNtwProtection_T[] pgs = null;
		try {
			NameAndStringValue_T[] meName = new NameAndStringValue_T[0];
			pgs = ProtectionMgrHandler.instance().getAllTrailNtwProtections(corbaService.getNmsSession().getTrailNtwProtMgr(), meName);
		} catch (ProcessingFailureException e) {
			errorlog.error("retrieveAllTrailNtwProtection ProcessingFailureException: " + CodeTool.isoToGbk(e.errorReason), e);
		} catch (org.omg.CORBA.SystemException e) {
			errorlog.error("retrieveAllTrailNtwProtection CORBA.SystemException: " + e.getMessage(), e);
		}
		if (pgs != null) {
			for (TrailNtwProtection_T trailpg : pgs) {
				try {
					TrailNtwProtection pg = ProtectGroupMapper.instance().convert(trailpg);
					// map.put(pg.getDn(), pg);
					tpglist.add(pg);
				} catch (Exception e) {
					errorlog.error("retrieveAllTrailNtwProtection convertException: ", e);
				}
			}
		}
		// }
		// }
		// sbilog.debug("TrailNtwProtectionList : " + map.size());
		// return new ArrayList<TrailNtwProtection>(map.values());
		return tpglist;
	}

	@Override
	public ManagedElement retrieveManagedElement(String neName) {
		ManagedElement_T vendorNe = null;
		try {
			NameAndStringValue_T[] ns = VendorDNFactory.createCommonDN(neName);
			vendorNe = ManagedElementMgrHandler.instance().retrieveManagedElement(corbaService.getNmsSession().getManagedElementMgr(), ns);
		} catch (ProcessingFailureException e) {
			errorlog.error("retrieveAllManagedElements ProcessingFailureException: " + CodeTool.isoToGbk(e.errorReason), e);
		} catch (org.omg.CORBA.SystemException e) {
			errorlog.error("retrieveAllManagedElements CORBA.SystemException: " + e.getMessage(), e);
		}
		ManagedElement ne = null;
		try {
			ne = ManagedElementMapper.instance().convertManagedElement(vendorNe);
		} catch (Exception e) {
			errorlog.error("retrieveAllManagedElements convertException: ", e);
		}
		return ne;
	}
}
