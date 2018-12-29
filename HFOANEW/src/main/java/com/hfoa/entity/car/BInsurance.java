package com.hfoa.entity.car;
/**
 * 
 * @author wzx
 * 公车保险信息实体类
 */
public class BInsurance {
    private Float id;//id

    private String carnum;//车牌号

    private String insurancetype;//保险类型

    private Float insurancesum;//保险金额

    private Float insurancefare;//保险费用

    private String suspendtime;//限行时间
    
    private String carUrl;//车辆图片路径

    private String carType;//类型
    
    public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	public String getCarUrl() {
		return carUrl;
	}

	public void setCarUrl(String carUrl) {
		this.carUrl = carUrl;
	}

	public Float getId() {
        return id;
    }

    public void setId(Float id) {
        this.id = id;
    }

    public String getCarnum() {
        return carnum;
    }

    public void setCarnum(String carnum) {
        this.carnum = carnum == null ? null : carnum.trim();
    }

    public String getInsurancetype() {
        return insurancetype;
    }

    public void setInsurancetype(String insurancetype) {
        this.insurancetype = insurancetype == null ? null : insurancetype.trim();
    }

    public Float getInsurancesum() {
        return insurancesum;
    }

    public void setInsurancesum(Float insurancesum) {
        this.insurancesum = insurancesum;
    }

    public Float getInsurancefare() {
        return insurancefare;
    }

    public void setInsurancefare(Float insurancefare) {
        this.insurancefare = insurancefare;
    }

    public String getSuspendtime() {
        return suspendtime;
    }

    public void setSuspendtime(String suspendtime) {
        this.suspendtime = suspendtime == null ? null : suspendtime.trim();
    }
}