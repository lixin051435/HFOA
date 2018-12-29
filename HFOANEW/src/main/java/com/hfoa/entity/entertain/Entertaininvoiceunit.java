package com.hfoa.entity.entertain;

import java.io.Serializable;

public class Entertaininvoiceunit implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String invoiceUnit;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInvoiceUnit() {
		return invoiceUnit;
	}

	public void setInvoiceUnit(String invoiceUnit) {
		this.invoiceUnit = invoiceUnit;
	}

	@Override
	public String toString() {
		return "Entertaininvoiceunit [id=" + id + ", invoiceUnit=" + invoiceUnit + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((invoiceUnit == null) ? 0 : invoiceUnit.hashCode());
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
		Entertaininvoiceunit other = (Entertaininvoiceunit) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (invoiceUnit == null) {
			if (other.invoiceUnit != null)
				return false;
		} else if (!invoiceUnit.equals(other.invoiceUnit))
			return false;
		return true;
	}
	
	
	
		
}
