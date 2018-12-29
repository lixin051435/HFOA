package com.hfoa.entity.fatherEntity;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hfoa.entity.vacation.LearYear;

public class publicEntity implements Serializable,Comparable<publicEntity>{

	
	
	
	public String sortTime;

	public String faterApplyman;
	
	public String fatherdepartment;
	
	public String fathertitle;
	
	public String fatherId;
	
	public String comment;
	
	
	
	
	
	
	public String getFatherId() {
		return fatherId;
	}



	public void setFatherId(String fatherId) {
		this.fatherId = fatherId;
	}



	public String getComment() {
		return comment;
	}



	public void setComment(String comment) {
		this.comment = comment;
	}



	public String getFathertitle() {
		return fathertitle;
	}



	public void setFathertitle(String fathertitle) {
		this.fathertitle = fathertitle;
	}



	public String getFaterApplyman() {
		return faterApplyman;
	}



	public void setFaterApplyman(String faterApplyman) {
		this.faterApplyman = faterApplyman;
	}



	public String getFatherdepartment() {
		return fatherdepartment;
	}



	public void setFatherdepartment(String fatherdepartment) {
		this.fatherdepartment = fatherdepartment;
	}



	public String getSortTime() {
		return sortTime;
	}



	public void setSortTime(String sortTime) {
		this.sortTime = sortTime;
	}



	@Override
	public String toString() {
		return "publicEntity [sortTime=" + sortTime + ", faterApplyman=" + faterApplyman + ", fatherdepartment="
				+ fatherdepartment + ", fathertitle=" + fathertitle + ", fatherId=" + fatherId + ", comment=" + comment
				+ "]";
	}



	@Override
	public int compareTo(publicEntity o) {
		SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
		int i=0;
		Date before = null;
		Date after = null;
		try {
			before = smf.parse(o.sortTime);
			after = smf.parse(this.sortTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if(after.getTime()-before.getTime()>0){
			i = (int)(after.getTime()-before.getTime());
		}else if(after.getTime()-before.getTime()<0){
			i = (int)(after.getTime()-before.getTime());
		}else{
			if(!o.getFatherId().equals(this.fatherId)){
				i++;
			}
		}
		
		return i;
	}
	


}





	
	
		
