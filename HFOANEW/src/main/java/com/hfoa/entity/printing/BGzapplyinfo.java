package com.hfoa.entity.printing;

import java.io.Serializable;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.hfoa.entity.fatherEntity.publicEntity;

/**
 * 
 * @author wzx
 * 用印申请实体类
 */
public class BGzapplyinfo extends publicEntity implements Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;//id

    private String approvalid;//审批单号
    
    private String department;//部门名称

    private String applyusername;//申请人账号

    private String reason;//申请缘由

    private String applytime;//申请时间

    private String sendto;//发往单位

    private String gzkind;//公章类型

    private Integer copies;//用印份数

    private String issecret;//是否涉密

    private String status;//审批状态（已完成，被否决，通过，）

    private String approveman;//审批人账号

    private String confirmman;//确认人账号
    
    private String handleman;//处理人账号
    
    private String entrustedman;//受托人
    
    private String entrustedpost;//受托人职务
    
    private String entrustedcardtype;//受托人证件类型
    
    private String entrustedcardnum;//受托人证件号码
    
    private String entrustedmatter;//受托事项
    
    private String entrustedpermission;//受托权限
    
    private String entrustedstarttime;//受托开始时间
    
    private String entrustedendtime;//受托结束时间
    
    private int maxgarde;//盖公章最大级别

    private int departmentid;//部门id
    
    private String BusinessManager;//业务主管账号
    
    private int approvalLable;//审批人状态
    
    private int bussinessLable;//业务主管状态
    
    private int confirmLable;//确认人状态
    
    private Integer gzId;//公章id 
    
    private double contracAmount;//合同金额
    
    private String borrowTime;//借用时间
    
    private String returnTime;//归还时间
    
    private String contractType;//合同类型，甲方，乙方
    //新加的，非数据库
    private String taskId;//业务编号
    
    private String openId;//微信认证编号
    
    private String starttime;//开始时间
    
    private String endtime;//结束时间
    
    private String title;
    
    private String url;
    
    private String param;
    
	private String canUpdate;//是否可修改
	
	private String canRevoke;//是否可撤回
    
	private String canUseSeal;//是否同意
	
	private String displayTime;//是否显示借归时间
	
	private String comment;//驳回意见
	
	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	private List<String> list;//业务id集合
	
    public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDisplayTime() {
		return displayTime;
	}

	public void setDisplayTime(String displayTime) {
		this.displayTime = displayTime;
	}

	public String getBorrowTime() {
		return borrowTime;
	}

	public void setBorrowTime(String borrowTime) {
		this.borrowTime = borrowTime;
	}

	public String getReturnTime() {
		return returnTime;
	}

	public void setReturnTime(String returnTime) {
		this.returnTime = returnTime;
	}

	public double getContracAmount() {
		return contracAmount;
	}

	public void setContracAmount(double contracAmount) {
		this.contracAmount = contracAmount;
	}

	public String getCanUseSeal() {
		return canUseSeal;
	}

	public void setCanUseSeal(String canUseSeal) {
		this.canUseSeal = canUseSeal;
	}

	public Integer getGzId() {
		return gzId;
	}

	public void setGzId(Integer gzId) {
		this.gzId = gzId;
	}

	public String getCanUpdate() {
		return canUpdate;
	}

	public void setCanUpdate(String canUpdate) {
		this.canUpdate = canUpdate;
	}

	public String getCanRevoke() {
		return canRevoke;
	}

	public void setCanRevoke(String canRevoke) {
		this.canRevoke = canRevoke;
	}

	public int getApprovalLable() {
		return approvalLable;
	}

	public void setApprovalLable(int approvalLable) {
		this.approvalLable = approvalLable;
	}

	public int getBussinessLable() {
		return bussinessLable;
	}

	public void setBussinessLable(int bussinessLable) {
		this.bussinessLable = bussinessLable;
	}

	public int getConfirmLable() {
		return confirmLable;
	}

	public void setConfirmLable(int confirmLable) {
		this.confirmLable = confirmLable;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public int getDepartmentid() {
		return departmentid;
	}

	public void setDepartmentid(int departmentid) {
		this.departmentid = departmentid;
	}

	public String getHandleman() {
		return handleman;
	}

	public void setHandleman(String handleman) {
		this.handleman = handleman;
	}

	public String getEntrustedman() {
		return entrustedman;
	}

	public void setEntrustedman(String entrustedman) {
		this.entrustedman = entrustedman;
	}

	public String getEntrustedpost() {
		return entrustedpost;
	}

	public void setEntrustedpost(String entrustedpost) {
		this.entrustedpost = entrustedpost;
	}

	public String getEntrustedcardtype() {
		return entrustedcardtype;
	}

	public void setEntrustedcardtype(String entrustedcardtype) {
		this.entrustedcardtype = entrustedcardtype;
	}

	public String getEntrustedcardnum() {
		return entrustedcardnum;
	}

	public void setEntrustedcardnum(String entrustedcardnum) {
		this.entrustedcardnum = entrustedcardnum;
	}

	public String getEntrustedmatter() {
		return entrustedmatter;
	}

	public void setEntrustedmatter(String entrustedmatter) {
		this.entrustedmatter = entrustedmatter;
	}

	public String getEntrustedpermission() {
		return entrustedpermission;
	}

	public void setEntrustedpermission(String entrustedpermission) {
		this.entrustedpermission = entrustedpermission;
	}

	public String getEntrustedstarttime() {
		return entrustedstarttime;
	}

	public void setEntrustedstarttime(String entrustedstarttime) {
		this.entrustedstarttime = entrustedstarttime;
	}

	public String getEntrustedendtime() {
		return entrustedendtime;
	}

	public void setEntrustedendtime(String entrustedendtime) {
		this.entrustedendtime = entrustedendtime;
	}

	public int getMaxgarde() {
		return maxgarde;
	}

	public void setMaxgarde(int maxgarde) {
		this.maxgarde = maxgarde;
	}

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getApprovalid() {
		return approvalid;
	}

	public void setApprovalid(String approvalid) {
		this.approvalid = approvalid;
	}

	public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department == null ? null : department.trim();
    }

    public String getApplyusername() {
        return applyusername;
    }

    public void setApplyusername(String applyusername) {
        this.applyusername = applyusername == null ? null : applyusername.trim();
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }

    public String getApplytime() {
        return applytime;
    }

    public void setApplytime(String applytime) {
        this.applytime = applytime == null ? null : applytime.trim();
    }

    public String getSendto() {
        return sendto;
    }

    public void setSendto(String sendto) {
        this.sendto = sendto == null ? null : sendto.trim();
    }

    public String getGzkind() {
        return gzkind;
    }

    public void setGzkind(String gzkind) {
        this.gzkind = gzkind == null ? null : gzkind.trim();
    }

    public Integer getCopies() {
        return copies;
    }

    public void setCopies(Integer copies) {
        this.copies = copies;
    }

    public String getIssecret() {
        return issecret;
    }

    public void setIssecret(String issecret) {
        this.issecret = issecret == null ? null : issecret.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getApproveman() {
        return approveman;
    }

    public void setApproveman(String approveman) {
        this.approveman = approveman == null ? null : approveman.trim();
    }

    public String getConfirmman() {
        return confirmman;
    }

    public void setConfirmman(String confirmman) {
        this.confirmman = confirmman == null ? null : confirmman.trim();
    }

	public String getBusinessManager() {
		return BusinessManager;
	}

	public void setBusinessManager(String businessManager) {
		BusinessManager = businessManager;
	}
    
}