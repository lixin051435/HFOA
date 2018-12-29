package com.hfoa.entity.common;

public class BMessagenotice {
    private Integer id;

    private String maintitle;

    private String contenttitle;

    private String usertime;

    private String content;

    public String getContent() {
		return content;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMaintitle() {
        return maintitle;
    }

    public void setMaintitle(String maintitle) {
        this.maintitle = maintitle == null ? null : maintitle.trim();
    }

    public String getContenttitle() {
        return contenttitle;
    }

    public void setContenttitle(String contenttitle) {
        this.contenttitle = contenttitle == null ? null : contenttitle.trim();
    }

    public String getUsertime() {
        return usertime;
    }

    public void setUsertime(String usertime) {
        this.usertime = usertime == null ? null : usertime.trim();
    }

    public void setContent(String content) {
		this.content = content;
	}
}