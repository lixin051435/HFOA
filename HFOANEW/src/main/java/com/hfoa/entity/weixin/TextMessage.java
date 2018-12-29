package com.hfoa.entity.weixin;

import java.util.List;

public class TextMessage {

	private String ToUserName;
	
	private String FormUserName;
	
	private String CreateTime;
	
	private String MsgType;
	
	private String Content;
	
	private String MsgId;

	public String getToUserName() {
		return ToUserName;
	}

	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}

	public String getFormUserName() {
		return FormUserName;
	}

	public void setFormUserName(String formUserName) {
		FormUserName = formUserName;
	}

	public String getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}

	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public String getMsgId() {
		return MsgId;
	}

	public void setMsgId(String msgId) {
		MsgId = msgId;
	}

	@Override
	public String toString() {
		return "TextMessage [ToUserName=" + ToUserName + ", FormUserName=" + FormUserName + ", CreateTime=" + CreateTime
				+ ", MsgType=" + MsgType + ", Content=" + Content + ", MsgId=" + MsgId + "]";
	}
	
	
}
