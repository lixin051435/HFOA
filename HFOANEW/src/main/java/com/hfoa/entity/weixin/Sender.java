package com.hfoa.entity.weixin;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class Sender {

	private String touser;
	private String template_id;
	private String url;
	private Object miniprogram;
	private Object data;
	
	
	public Object getMiniprogram() {
		return miniprogram;
	}
	public void setMiniprogram(Object miniprogram) {
		this.miniprogram = miniprogram;
	}
	public String getTouser() {
		return touser;
	}
	public void setTouser(String touser) {
		this.touser = touser;
	}
	public String getTemplate_id() {
		return template_id;
	}
	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	
	@Override
	public String toString() {
		return "Sender [touser=" + touser + ", template_id=" + template_id + ", url=" + url + ", miniprogram="
				+ miniprogram + ", data=" + data + "]";
	}
	/**
	 * obj --> json
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public String toJson() throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
}
