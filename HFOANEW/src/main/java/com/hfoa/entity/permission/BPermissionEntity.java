package com.hfoa.entity.permission;

import java.io.Serializable;

/**
 * 
 * @author wzx
 * 权限实体类
 */
public class BPermissionEntity implements Serializable{
    private Integer id;//id

    private String permissionname;//权限名称

    private String permissiontype;//权限类型（一级菜单）

    private String permissionurl;//权限路径

    private String permissionlable;//权限标记

    private Integer parentid;//父级id

    private Integer permissionsort;//权限排序

    private Integer available;//是否可用
    
    private String parentids;//所有父级编号，以“/”隔开
    
    private String urlclass;//路径类型（后台，微信，公用）

    private String processpermission;//流程权限（用于存放流程节点，若是没有则为空）
    
    private String imageurl;//微信图片存放路径
    //新加属性，非数据库
    private String showAvailable;//前台显示是否可用
    private String showUrlClass;//前台显示路径类别
    private String state;
    private String text;
    private String checked;

	public String getImageurl() {
		return imageurl;
	}

	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getShowAvailable() {
		return showAvailable;
	}

	public void setShowAvailable(String showAvailable) {
		this.showAvailable = showAvailable;
	}

	public String getShowUrlClass() {
		return showUrlClass;
	}

	public void setShowUrlClass(String showUrlClass) {
		this.showUrlClass = showUrlClass;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPermissionname() {
        return permissionname;
    }

    public void setPermissionname(String permissionname) {
        this.permissionname = permissionname == null ? null : permissionname.trim();
    }

    public String getPermissiontype() {
        return permissiontype;
    }

    public void setPermissiontype(String permissiontype) {
        this.permissiontype = permissiontype == null ? null : permissiontype.trim();
    }

    public String getPermissionurl() {
        return permissionurl;
    }

    public void setPermissionurl(String permissionurl) {
        this.permissionurl = permissionurl == null ? null : permissionurl.trim();
    }

    public String getPermissionlable() {
        return permissionlable;
    }

    public void setPermissionlable(String permissionlable) {
        this.permissionlable = permissionlable == null ? null : permissionlable.trim();
    }

    public Integer getParentid() {
        return parentid;
    }

    public void setParentid(Integer parentid) {
        this.parentid = parentid;
    }

    public Integer getPermissionsort() {
        return permissionsort;
    }

    public void setPermissionsort(Integer permissionsort) {
        this.permissionsort = permissionsort;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }

	public String getParentids() {
		return parentids;
	}

	public void setParentids(String parentids) {
		this.parentids = parentids;
	}

	public String getUrlclass() {
		return urlclass;
	}

	public void setUrlclass(String urlclass) {
		this.urlclass = urlclass;
	}

	public String getProcesspermission() {
		return processpermission;
	}

	public void setProcesspermission(String processpermission) {
		this.processpermission = processpermission;
	}
    
}