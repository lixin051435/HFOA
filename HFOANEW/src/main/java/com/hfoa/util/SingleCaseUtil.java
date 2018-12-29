package com.hfoa.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;

import com.mysql.fabric.xmlrpc.base.Data;

public class SingleCaseUtil {
	 

		private static SingleCaseUtil SingleCaseUtil=null;
		
		private SingleCaseUtil()
		{
			
		}
		
		public static   SingleCaseUtil getInstance()
		{
			if(SingleCaseUtil==null)
			{
				synchronized (SingleCaseUtil.class)
				{
					if(null==SingleCaseUtil)
					{
						SingleCaseUtil=new SingleCaseUtil();
					}
				}
			}
			return SingleCaseUtil;
		}
		
		//连接两个汉字为字符串
		public String combineInt(int i,int j)
		{
			StringBuffer sb=new StringBuffer("");
			sb.append(i);
			sb.append(j);
			return sb.toString();
		}
		
		public InputStream toChinese(ByteArrayOutputStream by,String info)
		{
			try {
				by.write(info.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new ByteArrayInputStream(by.toByteArray()); 
		}
		
		//获取文件路径的扩展名称
		public String getPostfix(String path)
		{
			if(path==""||path==null)
			{
				return "";
			}
			if(path.contains("."))
			{
				return path.substring(path.lastIndexOf(".")+1,path.length());
			}
			return "";
		}
		
		
		//去掉.0
		public  String getNum(String cell)
		{
			String result="";
			if(cell.contains("."))
			{
				result=cell.replace(".0", "");
			}
			else
			{
				result=cell;
			}
			return result;
		}
		
		//将一个字符串改成MD5的形式
		public String bytesToMD5(byte[] input) {
			String md5str = null;
			try {
				//创建一个提供信息摘要算法的对象，初始化为md5算法对象
				MessageDigest md = MessageDigest.getInstance("MD5");
				//计算后获得字节数组
				byte[] buff = md.digest(input);
				//把数组每一字节换成16进制连成md5字符串
				md5str = bytesToHex(buff);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return md5str;
		}
		
		public String bytesToHex(byte[] bytes) {
			StringBuffer md5str = new StringBuffer();
			//把数组每一字节换成16进制连成md5字符串
			int digital;
			for (int i = 0; i < bytes.length; i++) {
				 digital = bytes[i];
				if(digital < 0) {
					digital += 256;
				}
				if(digital < 16){
					md5str.append("0");
				}
				md5str.append(Integer.toHexString(digital));
			}
			return md5str.toString();
		}
		
		
		//获取文件路径的盘号
		public String  getP(String filename)
		{
			String  result="";
			if(filename==""){
				return "";
			}
			else{
				String[] array=filename.split("\\\\");
				for(int i=0;i<array.length-2;i++)	{
					if(i==array.length-3){
						result=result+array[i];
					}
					else{
						result=result+array[i]+"\\\\";
					}
				}
				return result;
			}
		}
		
		
		//获取文件路径的除去文件名称
		public String  getPath(String filename)
		{
			String  result="";
			if(filename==""){
				return "";
			}
			else{
				String[] array=new String[]{};
				if(filename.contains("/")){
					array=filename.split("/");
							
				}
				else
				{
					array=filename.split("\\\\");
				}
				
				for(int i=0;i<array.length-1;i++)	{
					result=result+array[i]+"\\\\";
				}
				return result;
			}
		}
		
		//获取文件名称
		public String getFilename(String filepath)
		{
			String  result="";
			if(filepath==""){
				return "";
			}
			else{
				String[] array=new String[]{};
				if(filepath.contains("/")){
					array=filepath.split("/");
							
				}
				else
				{
					array=filepath.split("\\\\");
				}
				return array[array.length-1];
			}
			
			
		}
		//取到最后一个id
		public String getParentId(String parentids)
		{
			String []ids=parentids.split("/");
			return ids[ids.length-1];
		}
		
		//获取当前时间的月份
		public String getMonth()
		{
			SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM");
			return simpleDateFormat.format(new Data());
		}
		
		//处理字符串[fileName] 去掉开始和结束的]
		public  String  replaceD(String filename)
		{
			return filename.replace("[", "").replace("]", "");
			
		}
		

}
