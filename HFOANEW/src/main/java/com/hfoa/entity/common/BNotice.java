package com.hfoa.entity.common;

public class BNotice {
    private Integer id;

    private String imgurl;

    private String linkurl;

    private String content;
    //新加，非数据库字段
    private Integer forUpload;
    
	public Integer getForUpload() {
		return forUpload;
	}

	public void setForUpload(Integer forUpload) {
		this.forUpload = forUpload;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl == null ? null : imgurl.trim();
    }

    public String getLinkurl() {
        return linkurl;
    }

    public void setLinkurl(String linkurl) {
        this.linkurl = linkurl == null ? null : linkurl.trim();
    }
}