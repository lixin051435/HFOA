package com.hfoa.entity.permission;

public class BProcess {
    private Integer id;

    private String processname;

    private String processurl;

    private Integer parentid;
    //新加，非数据库
    private String state;
    private String text;
    private String checked;

    public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProcessname() {
        return processname;
    }

    public void setProcessname(String processname) {
        this.processname = processname == null ? null : processname.trim();
    }

    public String getProcessurl() {
        return processurl;
    }

    public void setProcessurl(String processurl) {
        this.processurl = processurl == null ? null : processurl.trim();
    }

    public Integer getParentid() {
        return parentid;
    }

    public void setParentid(Integer parentid) {
        this.parentid = parentid;
    }
}