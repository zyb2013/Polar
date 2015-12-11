package com.game.utils;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.game.server.impl.WServer;

//通过gmail发送邮件
public class MailUtil {
	
	private static Logger log = Logger.getLogger(MailUtil.class);

	/**
	 * 发送邮件
	 * @param recipients 收件人
	 * @param title 标题
	 * @param content 内容
	 * @return
	 */
	public static int sendMail(String recipients,String title, String content){
		try {
			Properties props = new Properties();
//			props.put("mail.smtp.auth", "true");
//			props.put("mail.smtp.starttls.enable", "false");
//			props.put("mail.smtp.host", "mail.qmr.moloong.com");	//服务器地址
//			props.put("mail.smtp.port", "25");
//			props.put("mail.stmp.timeout", "60000");
//			props.put("mail.stmp.user", "gm@qmr.moloong.com");	//默认用户
//			props.put("mail.stmp.from", "gm@qmr.moloong.com");	//默认发件人
//			String from = "gm@qmr.moloong.com";	//发件人
//			final String username = "gm@qmr.moloong.com"; 	//用户名
//			final String password = "sdji@#^;ldkfLI786LK34A#^asd)*6ddf"; //密码
			
			
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "false");
			props.put("mail.smtp.host",WServer.getMailConfig().getHost());	//服务器地址
			props.put("mail.smtp.port", WServer.getMailConfig().getPort());
			props.put("mail.stmp.timeout", WServer.getMailConfig().getTimeout());
			props.put("mail.stmp.user", WServer.getMailConfig().getDefaultuser());	//默认用户
			props.put("mail.stmp.from", WServer.getMailConfig().getDefaultfrom());	//默认发件人
			String from = WServer.getMailConfig().getFrom();	//发件人
			final String username = WServer.getMailConfig().getUsername(); 	//用户名
			final String password = WServer.getMailConfig().getPassword(); //密码
			
			Session session = Session.getInstance(props,new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			  });
			try {
				
				Message message = new MimeMessage(session);
				//发件人
				message.setFrom(new InternetAddress(from));
				//收件人
				message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(recipients));
				message.setSubject(title); //主题
				message.setText(content);  //内容
				message.setSentDate(new Date(System.currentTimeMillis()));
				Transport.send(message);
				log.error(recipients+"邮件发送完成");
				return 1;
			} catch (MessagingException e) {
				log.error(e, e);
			}
		} catch (Exception e) {
			log.error(e, e);
		}
		return 0;
	}


}









