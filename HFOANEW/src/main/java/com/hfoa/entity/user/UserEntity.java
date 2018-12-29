package com.hfoa.entity.user;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hfoa.entity.fatherEntity.publicEntity;

public class UserEntity implements Serializable,Comparable<UserEntity>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;

    private String code;

    private String username;

    private String realname;

    private String userfrom;

    private String workcategory;

    private Integer companyid;

    private String companyname;

    private Integer departmentid;

    private String departmentname;

    private String workgroupid;

    private String workgroupname;

    private String gender;

    private String mobile;

    private String telephone;

    private String officeaddr;

    private String birthday;

    private String duty;

    private String title;

    private String userpassword;

    private String changepassworddate;

    private String qicq;

    private String email;

    private String lang;

    private String theme;

    private String allowstarttime;

    private String allowendtime;

    private String lockstartdate;

    private String lockenddate;

    private String firstvisit;

    private String previousvisit;

    private String lastvisit;

    private String createon;

    private String isstaff;

    private String auditstatus;

    private String isvisible;

    private String useronline;

    private String ipaddress;

    private String macaddress;

    private String homeaddress;

    private String openid;

    private String question;

    private String answerquestion;

    private String useraddress;

    private String deletemark;

    private String enabled;

    private String sortcode;

    private String description;

    private String logoncount;

    private String createuserid;

    private String createby;

    private String modifiedon;

    private String modifyuserid;

    private String modifiedby;

    private String leadername;

    private Integer status;

    private String roleIds;
    
    //鏂板姞锛岄潪鏁版嵁搴�
    private int grade;
    
    public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public String getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(String roleIds) {
		this.roleIds = roleIds;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname == null ? null : realname.trim();
    }

    public String getUserfrom() {
        return userfrom;
    }

    public void setUserfrom(String userfrom) {
        this.userfrom = userfrom == null ? null : userfrom.trim();
    }

    public String getWorkcategory() {
        return workcategory;
    }

    public void setWorkcategory(String workcategory) {
        this.workcategory = workcategory == null ? null : workcategory.trim();
    }

    public Integer getCompanyid() {
        return companyid;
    }

    public void setCompanyid(Integer companyid) {
        this.companyid = companyid;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname == null ? null : companyname.trim();
    }

    public Integer getDepartmentid() {
        return departmentid;
    }

    public void setDepartmentid(Integer departmentid) {
        this.departmentid = departmentid;
    }

    public String getDepartmentname() {
        return departmentname;
    }

    public void setDepartmentname(String departmentname) {
        this.departmentname = departmentname == null ? null : departmentname.trim();
    }

    public String getWorkgroupid() {
        return workgroupid;
    }

    public void setWorkgroupid(String workgroupid) {
        this.workgroupid = workgroupid == null ? null : workgroupid.trim();
    }

    public String getWorkgroupname() {
        return workgroupname;
    }

    public void setWorkgroupname(String workgroupname) {
        this.workgroupname = workgroupname == null ? null : workgroupname.trim();
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender == null ? null : gender.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone == null ? null : telephone.trim();
    }

    public String getOfficeaddr() {
        return officeaddr;
    }

    public void setOfficeaddr(String officeaddr) {
        this.officeaddr = officeaddr == null ? null : officeaddr.trim();
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday == null ? null : birthday.trim();
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty == null ? null : duty.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword == null ? null : userpassword.trim();
    }

    public String getChangepassworddate() {
        return changepassworddate;
    }

    public void setChangepassworddate(String changepassworddate) {
        this.changepassworddate = changepassworddate == null ? null : changepassworddate.trim();
    }

    public String getQicq() {
        return qicq;
    }

    public void setQicq(String qicq) {
        this.qicq = qicq == null ? null : qicq.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang == null ? null : lang.trim();
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme == null ? null : theme.trim();
    }

    public String getAllowstarttime() {
        return allowstarttime;
    }

    public void setAllowstarttime(String allowstarttime) {
        this.allowstarttime = allowstarttime == null ? null : allowstarttime.trim();
    }

    public String getAllowendtime() {
        return allowendtime;
    }

    public void setAllowendtime(String allowendtime) {
        this.allowendtime = allowendtime == null ? null : allowendtime.trim();
    }

    public String getLockstartdate() {
        return lockstartdate;
    }

    public void setLockstartdate(String lockstartdate) {
        this.lockstartdate = lockstartdate == null ? null : lockstartdate.trim();
    }

    public String getLockenddate() {
        return lockenddate;
    }

    public void setLockenddate(String lockenddate) {
        this.lockenddate = lockenddate == null ? null : lockenddate.trim();
    }

    public String getFirstvisit() {
        return firstvisit;
    }

    public void setFirstvisit(String firstvisit) {
        this.firstvisit = firstvisit == null ? null : firstvisit.trim();
    }

    public String getPreviousvisit() {
        return previousvisit;
    }

    public void setPreviousvisit(String previousvisit) {
        this.previousvisit = previousvisit == null ? null : previousvisit.trim();
    }

    public String getLastvisit() {
        return lastvisit;
    }

    public void setLastvisit(String lastvisit) {
        this.lastvisit = lastvisit == null ? null : lastvisit.trim();
    }

    public String getCreateon() {
        return createon;
    }

    public void setCreateon(String createon) {
        this.createon = createon == null ? null : createon.trim();
    }

    public String getIsstaff() {
        return isstaff;
    }

    public void setIsstaff(String isstaff) {
        this.isstaff = isstaff == null ? null : isstaff.trim();
    }

    public String getAuditstatus() {
        return auditstatus;
    }

    public void setAuditstatus(String auditstatus) {
        this.auditstatus = auditstatus == null ? null : auditstatus.trim();
    }

    public String getIsvisible() {
        return isvisible;
    }

    public void setIsvisible(String isvisible) {
        this.isvisible = isvisible == null ? null : isvisible.trim();
    }

    public String getUseronline() {
        return useronline;
    }

    public void setUseronline(String useronline) {
        this.useronline = useronline == null ? null : useronline.trim();
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress == null ? null : ipaddress.trim();
    }

    public String getMacaddress() {
        return macaddress;
    }

    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress == null ? null : macaddress.trim();
    }

    public String getHomeaddress() {
        return homeaddress;
    }

    public void setHomeaddress(String homeaddress) {
        this.homeaddress = homeaddress == null ? null : homeaddress.trim();
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid == null ? null : openid.trim();
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question == null ? null : question.trim();
    }

    public String getAnswerquestion() {
        return answerquestion;
    }

    public void setAnswerquestion(String answerquestion) {
        this.answerquestion = answerquestion == null ? null : answerquestion.trim();
    }

    public String getUseraddress() {
        return useraddress;
    }

    public void setUseraddress(String useraddress) {
        this.useraddress = useraddress == null ? null : useraddress.trim();
    }

    public String getDeletemark() {
        return deletemark;
    }

    public void setDeletemark(String deletemark) {
        this.deletemark = deletemark == null ? null : deletemark.trim();
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled == null ? null : enabled.trim();
    }

    public String getSortcode() {
        return sortcode;
    }

    public void setSortcode(String sortcode) {
        this.sortcode = sortcode == null ? null : sortcode.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getLogoncount() {
        return logoncount;
    }

    public void setLogoncount(String logoncount) {
        this.logoncount = logoncount == null ? null : logoncount.trim();
    }

    public String getCreateuserid() {
        return createuserid;
    }

    public void setCreateuserid(String createuserid) {
        this.createuserid = createuserid == null ? null : createuserid.trim();
    }

    public String getCreateby() {
        return createby;
    }

    public void setCreateby(String createby) {
        this.createby = createby == null ? null : createby.trim();
    }

    public String getModifiedon() {
        return modifiedon;
    }

    public void setModifiedon(String modifiedon) {
        this.modifiedon = modifiedon == null ? null : modifiedon.trim();
    }

    public String getModifyuserid() {
        return modifyuserid;
    }

    public void setModifyuserid(String modifyuserid) {
        this.modifyuserid = modifyuserid == null ? null : modifyuserid.trim();
    }

    public String getModifiedby() {
        return modifiedby;
    }

    public void setModifiedby(String modifiedby) {
        this.modifiedby = modifiedby == null ? null : modifiedby.trim();
    }

    public String getLeadername() {
        return leadername;
    }

    public void setLeadername(String leadername) {
        this.leadername = leadername == null ? null : leadername.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

	@Override
	public String toString() {
		return "UserEntity [id=" + id + ", code=" + code + ", username=" + username + ", realname=" + realname
				+ ", userfrom=" + userfrom + ", workcategory=" + workcategory + ", companyid=" + companyid
				+ ", companyname=" + companyname + ", departmentid=" + departmentid + ", departmentname="
				+ departmentname + ", workgroupid=" + workgroupid + ", workgroupname=" + workgroupname + ", gender="
				+ gender + ", mobile=" + mobile + ", telephone=" + telephone + ", officeaddr=" + officeaddr
				+ ", birthday=" + birthday + ", duty=" + duty + ", title=" + title + ", userpassword=" + userpassword
				+ ", changepassworddate=" + changepassworddate + ", qicq=" + qicq + ", email=" + email + ", lang="
				+ lang + ", theme=" + theme + ", allowstarttime=" + allowstarttime + ", allowendtime=" + allowendtime
				+ ", lockstartdate=" + lockstartdate + ", lockenddate=" + lockenddate + ", firstvisit=" + firstvisit
				+ ", previousvisit=" + previousvisit + ", lastvisit=" + lastvisit + ", createon=" + createon
				+ ", isstaff=" + isstaff + ", auditstatus=" + auditstatus + ", isvisible=" + isvisible + ", useronline="
				+ useronline + ", ipaddress=" + ipaddress + ", macaddress=" + macaddress + ", homeaddress="
				+ homeaddress + ", openid=" + openid + ", question=" + question + ", answerquestion=" + answerquestion
				+ ", useraddress=" + useraddress + ", deletemark=" + deletemark + ", enabled=" + enabled + ", sortcode="
				+ sortcode + ", description=" + description + ", logoncount=" + logoncount + ", createuserid="
				+ createuserid + ", createby=" + createby + ", modifiedon=" + modifiedon + ", modifyuserid="
				+ modifyuserid + ", modifiedby=" + modifiedby + ", leadername=" + leadername + ", status=" + status
				+ "]";
	}

	@Override
	public int compareTo(UserEntity o) {
		SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
		int i=0;
		
			if(o.getId()==this.getId()){
				i++;
			}
		
		
		return i;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allowendtime == null) ? 0 : allowendtime.hashCode());
		result = prime * result + ((allowstarttime == null) ? 0 : allowstarttime.hashCode());
		result = prime * result + ((answerquestion == null) ? 0 : answerquestion.hashCode());
		result = prime * result + ((auditstatus == null) ? 0 : auditstatus.hashCode());
		result = prime * result + ((birthday == null) ? 0 : birthday.hashCode());
		result = prime * result + ((changepassworddate == null) ? 0 : changepassworddate.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((companyid == null) ? 0 : companyid.hashCode());
		result = prime * result + ((companyname == null) ? 0 : companyname.hashCode());
		result = prime * result + ((createby == null) ? 0 : createby.hashCode());
		result = prime * result + ((createon == null) ? 0 : createon.hashCode());
		result = prime * result + ((createuserid == null) ? 0 : createuserid.hashCode());
		result = prime * result + ((deletemark == null) ? 0 : deletemark.hashCode());
		result = prime * result + ((departmentid == null) ? 0 : departmentid.hashCode());
		result = prime * result + ((departmentname == null) ? 0 : departmentname.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((duty == null) ? 0 : duty.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
		result = prime * result + ((firstvisit == null) ? 0 : firstvisit.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result + grade;
		result = prime * result + ((homeaddress == null) ? 0 : homeaddress.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((ipaddress == null) ? 0 : ipaddress.hashCode());
		result = prime * result + ((isstaff == null) ? 0 : isstaff.hashCode());
		result = prime * result + ((isvisible == null) ? 0 : isvisible.hashCode());
		result = prime * result + ((lang == null) ? 0 : lang.hashCode());
		result = prime * result + ((lastvisit == null) ? 0 : lastvisit.hashCode());
		result = prime * result + ((leadername == null) ? 0 : leadername.hashCode());
		result = prime * result + ((lockenddate == null) ? 0 : lockenddate.hashCode());
		result = prime * result + ((lockstartdate == null) ? 0 : lockstartdate.hashCode());
		result = prime * result + ((logoncount == null) ? 0 : logoncount.hashCode());
		result = prime * result + ((macaddress == null) ? 0 : macaddress.hashCode());
		result = prime * result + ((mobile == null) ? 0 : mobile.hashCode());
		result = prime * result + ((modifiedby == null) ? 0 : modifiedby.hashCode());
		result = prime * result + ((modifiedon == null) ? 0 : modifiedon.hashCode());
		result = prime * result + ((modifyuserid == null) ? 0 : modifyuserid.hashCode());
		result = prime * result + ((officeaddr == null) ? 0 : officeaddr.hashCode());
		result = prime * result + ((openid == null) ? 0 : openid.hashCode());
		result = prime * result + ((previousvisit == null) ? 0 : previousvisit.hashCode());
		result = prime * result + ((qicq == null) ? 0 : qicq.hashCode());
		result = prime * result + ((question == null) ? 0 : question.hashCode());
		result = prime * result + ((realname == null) ? 0 : realname.hashCode());
		result = prime * result + ((roleIds == null) ? 0 : roleIds.hashCode());
		result = prime * result + ((sortcode == null) ? 0 : sortcode.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((telephone == null) ? 0 : telephone.hashCode());
		result = prime * result + ((theme == null) ? 0 : theme.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((useraddress == null) ? 0 : useraddress.hashCode());
		result = prime * result + ((userfrom == null) ? 0 : userfrom.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		result = prime * result + ((useronline == null) ? 0 : useronline.hashCode());
		result = prime * result + ((userpassword == null) ? 0 : userpassword.hashCode());
		result = prime * result + ((workcategory == null) ? 0 : workcategory.hashCode());
		result = prime * result + ((workgroupid == null) ? 0 : workgroupid.hashCode());
		result = prime * result + ((workgroupname == null) ? 0 : workgroupname.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserEntity other = (UserEntity) obj;
		if (allowendtime == null) {
			if (other.allowendtime != null)
				return false;
		} else if (!allowendtime.equals(other.allowendtime))
			return false;
		if (allowstarttime == null) {
			if (other.allowstarttime != null)
				return false;
		} else if (!allowstarttime.equals(other.allowstarttime))
			return false;
		if (answerquestion == null) {
			if (other.answerquestion != null)
				return false;
		} else if (!answerquestion.equals(other.answerquestion))
			return false;
		if (auditstatus == null) {
			if (other.auditstatus != null)
				return false;
		} else if (!auditstatus.equals(other.auditstatus))
			return false;
		if (birthday == null) {
			if (other.birthday != null)
				return false;
		} else if (!birthday.equals(other.birthday))
			return false;
		if (changepassworddate == null) {
			if (other.changepassworddate != null)
				return false;
		} else if (!changepassworddate.equals(other.changepassworddate))
			return false;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (companyid == null) {
			if (other.companyid != null)
				return false;
		} else if (!companyid.equals(other.companyid))
			return false;
		if (companyname == null) {
			if (other.companyname != null)
				return false;
		} else if (!companyname.equals(other.companyname))
			return false;
		if (createby == null) {
			if (other.createby != null)
				return false;
		} else if (!createby.equals(other.createby))
			return false;
		if (createon == null) {
			if (other.createon != null)
				return false;
		} else if (!createon.equals(other.createon))
			return false;
		if (createuserid == null) {
			if (other.createuserid != null)
				return false;
		} else if (!createuserid.equals(other.createuserid))
			return false;
		if (deletemark == null) {
			if (other.deletemark != null)
				return false;
		} else if (!deletemark.equals(other.deletemark))
			return false;
		if (departmentid == null) {
			if (other.departmentid != null)
				return false;
		} else if (!departmentid.equals(other.departmentid))
			return false;
		if (departmentname == null) {
			if (other.departmentname != null)
				return false;
		} else if (!departmentname.equals(other.departmentname))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (duty == null) {
			if (other.duty != null)
				return false;
		} else if (!duty.equals(other.duty))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (enabled == null) {
			if (other.enabled != null)
				return false;
		} else if (!enabled.equals(other.enabled))
			return false;
		if (firstvisit == null) {
			if (other.firstvisit != null)
				return false;
		} else if (!firstvisit.equals(other.firstvisit))
			return false;
		if (gender == null) {
			if (other.gender != null)
				return false;
		} else if (!gender.equals(other.gender))
			return false;
		if (grade != other.grade)
			return false;
		if (homeaddress == null) {
			if (other.homeaddress != null)
				return false;
		} else if (!homeaddress.equals(other.homeaddress))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (ipaddress == null) {
			if (other.ipaddress != null)
				return false;
		} else if (!ipaddress.equals(other.ipaddress))
			return false;
		if (isstaff == null) {
			if (other.isstaff != null)
				return false;
		} else if (!isstaff.equals(other.isstaff))
			return false;
		if (isvisible == null) {
			if (other.isvisible != null)
				return false;
		} else if (!isvisible.equals(other.isvisible))
			return false;
		if (lang == null) {
			if (other.lang != null)
				return false;
		} else if (!lang.equals(other.lang))
			return false;
		if (lastvisit == null) {
			if (other.lastvisit != null)
				return false;
		} else if (!lastvisit.equals(other.lastvisit))
			return false;
		if (leadername == null) {
			if (other.leadername != null)
				return false;
		} else if (!leadername.equals(other.leadername))
			return false;
		if (lockenddate == null) {
			if (other.lockenddate != null)
				return false;
		} else if (!lockenddate.equals(other.lockenddate))
			return false;
		if (lockstartdate == null) {
			if (other.lockstartdate != null)
				return false;
		} else if (!lockstartdate.equals(other.lockstartdate))
			return false;
		if (logoncount == null) {
			if (other.logoncount != null)
				return false;
		} else if (!logoncount.equals(other.logoncount))
			return false;
		if (macaddress == null) {
			if (other.macaddress != null)
				return false;
		} else if (!macaddress.equals(other.macaddress))
			return false;
		if (mobile == null) {
			if (other.mobile != null)
				return false;
		} else if (!mobile.equals(other.mobile))
			return false;
		if (modifiedby == null) {
			if (other.modifiedby != null)
				return false;
		} else if (!modifiedby.equals(other.modifiedby))
			return false;
		if (modifiedon == null) {
			if (other.modifiedon != null)
				return false;
		} else if (!modifiedon.equals(other.modifiedon))
			return false;
		if (modifyuserid == null) {
			if (other.modifyuserid != null)
				return false;
		} else if (!modifyuserid.equals(other.modifyuserid))
			return false;
		if (officeaddr == null) {
			if (other.officeaddr != null)
				return false;
		} else if (!officeaddr.equals(other.officeaddr))
			return false;
		if (openid == null) {
			if (other.openid != null)
				return false;
		} else if (!openid.equals(other.openid))
			return false;
		if (previousvisit == null) {
			if (other.previousvisit != null)
				return false;
		} else if (!previousvisit.equals(other.previousvisit))
			return false;
		if (qicq == null) {
			if (other.qicq != null)
				return false;
		} else if (!qicq.equals(other.qicq))
			return false;
		if (question == null) {
			if (other.question != null)
				return false;
		} else if (!question.equals(other.question))
			return false;
		if (realname == null) {
			if (other.realname != null)
				return false;
		} else if (!realname.equals(other.realname))
			return false;
		if (roleIds == null) {
			if (other.roleIds != null)
				return false;
		} else if (!roleIds.equals(other.roleIds))
			return false;
		if (sortcode == null) {
			if (other.sortcode != null)
				return false;
		} else if (!sortcode.equals(other.sortcode))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (telephone == null) {
			if (other.telephone != null)
				return false;
		} else if (!telephone.equals(other.telephone))
			return false;
		if (theme == null) {
			if (other.theme != null)
				return false;
		} else if (!theme.equals(other.theme))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (useraddress == null) {
			if (other.useraddress != null)
				return false;
		} else if (!useraddress.equals(other.useraddress))
			return false;
		if (userfrom == null) {
			if (other.userfrom != null)
				return false;
		} else if (!userfrom.equals(other.userfrom))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		if (useronline == null) {
			if (other.useronline != null)
				return false;
		} else if (!useronline.equals(other.useronline))
			return false;
		if (userpassword == null) {
			if (other.userpassword != null)
				return false;
		} else if (!userpassword.equals(other.userpassword))
			return false;
		if (workcategory == null) {
			if (other.workcategory != null)
				return false;
		} else if (!workcategory.equals(other.workcategory))
			return false;
		if (workgroupid == null) {
			if (other.workgroupid != null)
				return false;
		} else if (!workgroupid.equals(other.workgroupid))
			return false;
		if (workgroupname == null) {
			if (other.workgroupname != null)
				return false;
		} else if (!workgroupname.equals(other.workgroupname))
			return false;
		return true;
	}
    
}
