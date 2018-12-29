package com.hfoa.entity.entertain;

public class EntertainhospitalitySubsidiary {
	
	
	private String Id;//主键
	
	private String HospitalityId;
	
	private String Sum;//金额
	
	private String AdjustmentTime;//调整时间

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getHospitalityId() {
		return HospitalityId;
	}

	public void setHospitalityId(String hospitalityId) {
		HospitalityId = hospitalityId;
	}

	public String getSum() {
		return Sum;
	}

	public void setSum(String sum) {
		Sum = sum;
	}

	public String getAdjustmentTime() {
		return AdjustmentTime;
	}

	public void setAdjustmentTime(String adjustmentTime) {
		AdjustmentTime = adjustmentTime;
	}

	@Override
	public String toString() {
		return "EntertainhospitalitySubsidiary [Id=" + Id + ", HospitalityId=" + HospitalityId + ", Sum=" + Sum
				+ ", AdjustmentTime=" + AdjustmentTime + "]";
	}
	
	
	
	

}
