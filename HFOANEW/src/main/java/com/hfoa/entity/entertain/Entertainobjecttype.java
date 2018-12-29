package com.hfoa.entity.entertain;

import java.io.Serializable;

import com.hfoa.entity.fatherEntity.publicEntity;

public class Entertainobjecttype implements Serializable,Comparable<Entertainobjecttype>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	
	private String oName;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getoName() {
		return oName;
	}

	public void setoName(String oName) {
		this.oName = oName;
	}

	@Override
	public String toString() {
		return "Entertainobjecttype [id=" + id + ", oName=" + oName + "]";
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((oName == null) ? 0 : oName.hashCode());
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
		Entertainobjecttype other = (Entertainobjecttype) obj;
		if (oName == null) {
			if (other.oName != null)
				return false;
		} else if (!oName.equals(other.oName))
			return false;
		return true;
	}

	@Override
	public int compareTo(Entertainobjecttype o) {
		int i=0;
		if(o.oName.equals(this.oName)){
			i++;
		}
		return i;
	}

	
	
		
}
