package com.hfoa.entity.car;
/**
 * 
 * @author wzx
 * 公车基本信息实体类
 */
public class BCarbaseinfoDTO {
    private Integer id;//id

    private String carcode;//车辆型号

    private String Typenum;//车牌号+车牌

    private String carunit;//所属单位

    private String carbuytime;//购买时间

    private String cardetailinfo;//车辆详细信息

    private String carinsuranceinfo1;//车辆保险时间

    private String carinsuranceinfo;

    private Float cardvale;//油卡值

    private Float cardistance;//共行驶里程

    private String carinsuranceinfodetal;//车险信息

    private String others;//其他信息

    private Integer peasonnum;//承载量

    private String carstate;//车辆状态

    private String suspendday;//限号时间

    private int carLable;//车辆状态标识
    
    private String carUrl;//车辆图片
    
    private String carNum;//车牌号
    
    private String carType;//类型
    
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

	public int getCarLable() {
		return carLable;
	}

	public void setCarLable(int carLable) {
		this.carLable = carLable;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCarcode() {
        return carcode;
    }

    public void setCarcode(String carcode) {
        this.carcode = carcode == null ? null : carcode.trim();
    }

    public String getTypenum() {
		return Typenum;
	}

	public void setTypenum(String typenum) {
		Typenum = typenum;
	}

	public String getCarunit() {
        return carunit;
    }

    public void setCarunit(String carunit) {
        this.carunit = carunit == null ? null : carunit.trim();
    }

    public String getCarbuytime() {
        return carbuytime;
    }

    public void setCarbuytime(String carbuytime) {
        this.carbuytime = carbuytime == null ? null : carbuytime.trim();
    }

    public String getCardetailinfo() {
        return cardetailinfo;
    }

    public void setCardetailinfo(String cardetailinfo) {
        this.cardetailinfo = cardetailinfo == null ? null : cardetailinfo.trim();
    }

    public String getCarinsuranceinfo1() {
        return carinsuranceinfo1;
    }

    public void setCarinsuranceinfo1(String carinsuranceinfo1) {
        this.carinsuranceinfo1 = carinsuranceinfo1 == null ? null : carinsuranceinfo1.trim();
    }

    public String getCarinsuranceinfo() {
        return carinsuranceinfo;
    }

    public void setCarinsuranceinfo(String carinsuranceinfo) {
        this.carinsuranceinfo = carinsuranceinfo == null ? null : carinsuranceinfo.trim();
    }

    public Float getCardvale() {
        return cardvale;
    }

    public void setCardvale(Float cardvale) {
        this.cardvale = cardvale;
    }

    public Float getCardistance() {
        return cardistance;
    }

    public void setCardistance(Float cardistance) {
        this.cardistance = cardistance;
    }

    public String getCarinsuranceinfodetal() {
        return carinsuranceinfodetal;
    }

    public void setCarinsuranceinfodetal(String carinsuranceinfodetal) {
        this.carinsuranceinfodetal = carinsuranceinfodetal == null ? null : carinsuranceinfodetal.trim();
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others == null ? null : others.trim();
    }

    public Integer getPeasonnum() {
        return peasonnum;
    }

    public void setPeasonnum(Integer peasonnum) {
        this.peasonnum = peasonnum;
    }

    public String getCarstate() {
        return carstate;
    }

    public void setCarstate(String carstate) {
        this.carstate = carstate == null ? null : carstate.trim();
    }

    public String getSuspendday() {
        return suspendday;
    }

    public void setSuspendday(String suspendday) {
        this.suspendday = suspendday == null ? null : suspendday.trim();
    }
}