package com.game.server.loader;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.game.server.config.MailConfig;

public class MailConfigXmlloader {
	private static Logger log = Logger.getLogger(MailConfigXmlloader.class);
	//读邮件配置信息配置信息
		public MailConfig load(){
	        try
	        {
	            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	            InputStream in = new FileInputStream("server-config/mail-config.xml");
	            Document doc = builder.parse(in);
	            NodeList list = doc.getElementsByTagName("server");
	            if(list.getLength() > 0)
	            {
	            	MailConfig sconfig = new MailConfig();
	                Node node = list.item(0);
	                NodeList childs = node.getChildNodes();
	                for (int i = 0; i < childs.getLength(); i++) {
	                	Node schilds = childs.item(i).getFirstChild();
	                	if(("host").equals(childs.item(i).getNodeName())){
	                		sconfig.setHost(schilds.getTextContent());
	                	}else if (("port").equals(childs.item(i).getNodeName())) {
	                		sconfig.setPort(schilds.getTextContent());
	                	}else if (("timeout").equals(childs.item(i).getNodeName())) {
	                		sconfig.setTimeout(schilds.getTextContent());
	                	}else if (("defaultuser").equals(childs.item(i).getNodeName())) {
	                		sconfig.setDefaultuser(schilds.getTextContent());
	                	}else if (("defaultfrom").equals(childs.item(i).getNodeName())) {
	                		sconfig.setDefaultfrom(schilds.getTextContent());
	                	}else if (("username").equals(childs.item(i).getNodeName())) {
	                		sconfig.setUsername(schilds.getTextContent());
	                	}else if (("password").equals(childs.item(i).getNodeName())) {
	                		sconfig.setPassword(schilds.getTextContent());
	                	}else if (("from").equals(childs.item(i).getNodeName())) {
	                		sconfig.setFrom(schilds.getTextContent());
	                	}
					}
	                in.close();
	                return sconfig;
	            }
	        }catch(Exception e){
	        	log.error(e, e);
	        }
	        return null;
		}
		
}
