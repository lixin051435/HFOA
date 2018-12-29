package com.hfoa.entity.car;
/**
 * 
 * @author wzx
 * 公车加油充值信息实体类
 */
public class BCargasinfo {
    private Integer id;//id

    private Integer carid;//车辆id

    private Float cardbalance;//余额

    private String executetime;//执行日期

    private Float changevalue;//改变的额度

    private String changetype;//类型

    private String remark;//备注
    
    private String carUrl;//车辆图片路径
    
    private String carType;//类型
    
    private String carNum;//车牌号
    
    public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	public String getCarNum() {
		return carNum;
	}

	public void setCarNum(String carNum) {
		this.carNum = carNum;
	}

	public String getCarUrl() {
		return carUrl;
	}

	public void setCarUrl(String carUrl) {
		this.carUrl = carUrl;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCarid() {
        return carid;
    }

    public void setCarid(Integer carid) {
        this.carid = carid;
    }

    public Float getCardbalance() {
        return cardbalance;
    }

    public void setCardbalance(Float cardbalance) {
        this.cardbalance = cardbalance;
    }

    public String getExecutetime() {
        return executetime;
    }

    public void setExecutetime(String executetime) {
        this.executetime = executetime == null ? null : executetime.trim();
    }

    public Float getChangevalue() {
        return changevalue;
    }

    public void setChangevalue(Float changevalue) {
        this.changevalue = changevalue;
    }

    public String getChangetype() {
        return changetype;
    }

    public void setChangetype(String changetype) {
        this.changetype = changetype == null ? null : changetype.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}