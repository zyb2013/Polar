package com.game.protect.manager;

import java.security.MessageDigest;

import org.apache.log4j.Logger;

import com.game.data.bean.Q_globalBean;
import com.game.db.bean.ProtectBean;
import com.game.db.dao.ProtectDao;
import com.game.dblog.LogService;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
//import com.game.prompt.structs.Notifys;
import com.game.protect.log.ProtectLog;
import com.game.protect.message.ReqBackstageModifyToGameMessage;
import com.game.protect.message.ReqModifyPasswordToGameMessage;
import com.game.protect.message.ReqPasswordSetToGameMessage;
import com.game.protect.message.ReqPasswordUnlockToGameMessage;
import com.game.protect.message.ReqVerificationToGameMessage;
import com.game.protect.message.ResAskedPanelToClientMessage;
import com.game.protect.message.ResPointToClientMessage;
import com.game.protect.message.ResVerificationCoolingToGameMessage;
import com.game.protect.structs.MailBean;
import com.game.server.impl.WServer;
import com.game.utils.CommonConfig;
import com.game.utils.MessageUtil;
import com.game.utils.RandomUtils;
import com.game.utils.Symbol;
//import com.game.utils.TimeUtil;


/**二级密码钻石保护
 * 
 * @author zhangrong
 *
 */
public class ProtectManager {
	private ProtectManager() {}
	
	private Logger log = Logger.getLogger(ProtectManager.class);
	//玩家管理类实例
	private static ProtectManager manager;
	private static Object obj = new Object();
	public static ProtectManager getInstance() {
		synchronized (obj) {
			if (manager == null) {
				manager = new ProtectManager();
			}
		}
		return manager;
	}
	
	
	private ProtectDao dao = new ProtectDao();
	/**
	 * 是否开启2级密码锁，默认true开启
	 * 
	 */
	private boolean isopenlock = false;

	/**得到平台排除列表  true=排除
	 * 
	 * @param id
	 * @return
	 */
	public boolean isexclude(){
		Q_globalBean data = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.PROTECT.getValue());
		if (data == null) {
			return false;
		}
		String[] str = data.getQ_string_value().split(Symbol.SHUXIAN_REG);
		if (str == null ) {
			return false;
		}
			
		if (str.length > 0) {
			String sysplatform = WServer.getInstance().getServerWeb();
			for (int i = 0; i < str.length; i++) {
				if (str[i].equals(sysplatform)) {
					return true;//排除
				}
			}
		}

		return false;
	}
	

	/**
	 * 储存单个二级密码数据  到数据库 异步
	 * @param pid
	 * @param true插入,false更新
	 */
	public void saveProtect(String uesrid ,String password,String mail, boolean insert) {
		try {
			ProtectBean protectBean  = new ProtectBean();
			protectBean.setUserid(uesrid);
			protectBean.setPassword(password);
			protectBean.setMail(mail);
			int type=0;	//更新
			if (insert) {
				type = 1;//插入
			}
			WServer.getInstance().getwSaveProtectThread().dealprotect(protectBean, type);
		} catch (Exception e) {
			log.error("储存单个结婚数据"+e,e);
		}
	}
	
	
	/**
	 * 储存单个二级密码数据  到数据库 异步
	 * @param pid
	 * @param true插入,false更新
	 */
	public void saveProtect(Player player, boolean insert) {
		saveProtect(player.getUserId(),player.getProtectpassword(),player.getProtectmail(),insert);
	}
	
	/**GM命令清理密码
	 * 
	 * @param player
	 */
	public void delprotect(Player player){
		dao.delete(player.getUserId());
		/*xiaozhuoming: 暂时没有用到
		MessageUtil.notify_player(player, Notifys.CHAT_ROLE, ResManager.getInstance().getString("执行完成，仅供GM测试用。"));
		*/
	}
	
	
	/**发送邮件
	 * 
	 * @param player  收件人  ,标题，内容，发件人
	 */

	private void SendMail(Player player,String addressee,String title,String content){
		MailBean mailBean = new MailBean();
		mailBean.setUserid(player.getUserId());
		mailBean.setContent(content);
		mailBean.setTitle(title);
		mailBean.setAddressee(addressee);
		WServer.getInstance().getwSaveProtectMailThread().dealmail(mailBean,0);
	}
	
	
	
	
	/**玩家登录时载入2级保护
	 * 
	 * @param player
	 */
	public void reloadProtect(Player player){
		try {
			ProtectBean protectBean = dao.selectsingle(player.getUserId());
			if(protectBean!=null){
				player.setProtectpassword(protectBean.getPassword());
				player.setProtectmail(protectBean.getMail());
			}else{
				player.setProtectpassword("");
				player.setProtectmail("");
			}
		} catch (Exception e) {
			log.error(e, e);
		}
	}
	
	
	/**发送玩家密码状态
	 * 
	 * @param player
	 */
	public void SendPasswordStatus(Player player){
		if(player.getProtectpassword()!= null && !player.getProtectpassword().equals("")){
			ResPointToClientMessage msg= new ResPointToClientMessage();
			msg.setType((byte) 7);	//通知前端，已经设置密码
			MessageUtil.tell_player_message(player, msg);
		}
	}
	
	
	
	/**设定状态  true=弹出
	 * 
	 */
	public void setProtectStatus(Player player){
		try {
			long time = System.currentTimeMillis() - player.getProtecttime() ;
			if(time > 60*60*1000*24){ //大于24小时  ，不管设置过密码没，都要弹出
				player.setProtecttime(System.currentTimeMillis());
				if(player.getLoginIp() == null || player.getLoginIp().equals("") ){
					player.setProtectip(""+System.currentTimeMillis());//如果IP为空，用时间代替
				}else {
					player.setProtectip(player.getLoginIp());
				}
				player.setProtectstatus(0);
			}else {//小于24小时
				//已经设定过密码的情况下
				if(player.getProtectpassword() != null && !player.getProtectpassword().equals("")){
					if (player.getProtectip() == null || player.getProtectip().equals("")) {
						player.setProtectip("");
					}
					
					if (!player.getLoginIp().equals(player.getProtectip())) {//IP不相等
						player.setProtectstatus(0);
						player.setProtecttime(System.currentTimeMillis());
						player.setProtectip(player.getLoginIp());
					}else {
						if (player.getProtectstatus() == 1) {//1表示之前取消密码输入，或者设定密码后没有输入过密码
							player.setProtectstatus(0);
						}
					}
					
				}else {//没设置过密码
					if(player.getLoginIp() == null || player.getLoginIp().equals("") ){
						player.setProtectstatus(0);
						player.setProtecttime(System.currentTimeMillis());
						player.setProtectip(""+System.currentTimeMillis());//如果IP为空，用时间代替
					}else {
						if (player.getProtectip() == null || player.getProtectip().equals("")) {
							player.setProtectip("");
						}
						
						if (!player.getLoginIp().equals(player.getProtectip())) {//IP不相等
							player.setProtectstatus(0);
							player.setProtecttime(System.currentTimeMillis());
							player.setProtectip(player.getLoginIp());
						}else {
							player.setProtectstatus(1);	//小于24小时而且IP相等，设置为不弹出
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
		}
	}
	
	
	
	
	/**是否弹出2级密码确认面板,true=弹出面板，并中断
	 * 
	 * @param player
	 * @return
	 */
	public boolean checkProtectStatus(Player player){
		if (!isopenlock) { 
			return false;
		}
		if (!isexclude()) {
			setProtectStatus(player);
			if (player.getProtectstatus() == 0) {
				ResAskedPanelToClientMessage cmsg = new ResAskedPanelToClientMessage();
				if (player.getProtectpassword() != null && !player.getProtectpassword().equals("")) {
					//已经设置过密码 
					cmsg.setType((byte) 2);
				}else {
					//没有设置密码
					cmsg.setType((byte) 1);
				}
				MessageUtil.tell_player_message(player, cmsg);
				return true;
			}
		}
		return false;
	}


	

	/**
	 * 修改密码消息
	 * @param player
	 * @param msg
	 */
	public void stReqModifyPasswordToGameMessage(Player player,ReqModifyPasswordToGameMessage msg) {
		if (msg.getOldpassword() == null || msg.getOldpassword().equals("")) {
			return;
		}
		if (msg.getNewpassword() == null || msg.getNewpassword().equals("")) {
			return;
		}
		
		if (player.getProtectpassword() == null || player.getProtectpassword().equals("")) {
			return;//没有设置过密码
		}
		
		if (msg.getOldpassword().equals(player.getProtectpassword())) {	//原密码=当前密码
			if (!msg.getOldpassword().equals(msg.getNewpassword())) {
				player.setProtectpassword(msg.getNewpassword());
				ResPointToClientMessage cmsg= new ResPointToClientMessage();
				cmsg.setType((byte) 5);
				MessageUtil.tell_player_message(player, cmsg);
				saveProtect(player,false);
				/*xiaozhuoming: 暂时没有用到
				MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("修改密码成功"));
				*/
				ProtectLog prplog = new ProtectLog();
				prplog.setMail(player.getProtectmail());
				prplog.setPassword(player.getProtectpassword());
				prplog.setUserid(player.getUserId());
				prplog.setType(1);
				LogService.getInstance().execute(prplog);
			}
		}else {
			/*xiaozhuoming: 暂时没有用到
			MessageUtil.notify_player(player, Notifys.MOUSEPOS, ResManager.getInstance().getString("密码错误"));
			*/
		}
	}



	/**找回密码 （发送邮件）
	 * 
	 * @param player
	 */
	public void stReqForgetPasswordToGameMessage(Player player) {
		if (player.getProtectmail() != null && !player.getProtectmail().equals("")) {
			long ms = System.currentTimeMillis() - player.getProtectpincooldown();
			if(ms > 2*60*1000){
				player.setProtectpincooldown(System.currentTimeMillis());
//				String mailstr = player.getProtectmail();
//				String str = mailstr.substring(1, 3);
//				String newstr = mailstr.replaceFirst(str,"**");//屏蔽部分字符的邮箱地址

				SendMail(player,player.getProtectmail(), ResManager.getInstance().getString("大天使钻石锁密码找回"),String.format(ResManager.getInstance().getString("您的角色：%s，钻石锁密码是：%s"), player.getName(),player.getProtectpassword()));
				
				ResPointToClientMessage msg= new ResPointToClientMessage();
				msg.setType((byte) 6);
				MessageUtil.tell_player_message(player, msg);
				/*xiaozhuoming: 暂时没有用到
				MessageUtil.notify_player(player, Notifys.CHAT_ROLE, ResManager.getInstance().getString("密码已经发送至您绑定的邮箱{1}中。"),newstr);
				*/
			}else {
				/*xiaozhuoming: 暂时没有用到
				MessageUtil.notify_player(player, Notifys.CHAT_ROLE, ResManager.getInstance().getString("密码已经发送至您绑定的邮箱中,如未收到，请等待2分钟后再重试"));
				*/
			}
		}
	}



	/**取消密码设置消息 （24小时内，IP不变的情况下不弹出设置框）
	 * 
	 * @param parameter
	 */
	public void stReqSelectIsSetToGameMessage(Player player) {
		player.setProtectstatus(1);//置为不弹出
		player.setProtecttime(System.currentTimeMillis());
		player.setProtectip(player.getLoginIp());
	}

	
	
	/**
	 * 新密码时，请求服务器，获得验证码消息
	 * @param player
	 * @param msg
	 */

	public void stReqVerificationToGameMessage(Player player,ReqVerificationToGameMessage msg) {
		if (msg.getMail() != null && !msg.getMail().equals("")) {
			long ms = System.currentTimeMillis() - player.getProtectpincooldown();
			if(ms > 5*60*1000){
				player.setProtectpincooldown(System.currentTimeMillis());
				String pin = "";
				for (int i = 0; i < 5; i++) {
					if (RandomUtils.random(0, 5) ==1) {
						pin = pin+RandomUtils.random(1, 9);
					}else {
						char str='A';
						str=(char)(str+(int)(Math.random()*26));
						pin= pin+str;
					}
				}
				player.setProtectPIN(pin);
				
				SendMail(player,msg.getMail(), ResManager.getInstance().getString("大天使钻石锁验证码"),String.format(ResManager.getInstance().getString("您当前要设置的大天使钻石锁验证码是：%s"), pin));
				
				ResVerificationCoolingToGameMessage cmsg= new ResVerificationCoolingToGameMessage();
				cmsg.setTime(5*60);
				MessageUtil.tell_player_message(player, cmsg);
				/*xiaozhuoming: 暂时没有用到
				MessageUtil.notify_player(player, Notifys.CHAT_ROLE, ResManager.getInstance().getString("验证码已经发送要您的邮箱，请注意查收"));
				MessageUtil.notify_player(player, Notifys.CHAT_ROLE, ResManager.getInstance().getString("验证码是{1}。"),pin+"");
				*/
			}else {
				/*xiaozhuoming: 暂时没有用到
				String timestr = TimeUtil.millisecondToStr( 5*60*1000-ms);
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("{1}后可重新发送验证码"),timestr);
				*/
			}
		}
	}
	
	
	

	/**提交新设置密码消息
	 * 
	 * @param player
	 * @param msg
	 */
	public void stReqPasswordSetToGameMessage(Player player,ReqPasswordSetToGameMessage msg) {
		if (player.getProtectpassword() != null && !player.getProtectpassword().equals("")) {
			return;//已经设置过密码
		}
		
		if (msg.getMail() == null || msg.getMail().equals("")) {
			/*xiaozhuoming: 暂时没有用到
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("未填写邮箱"));
			*/
			return;
		}
		if (msg.getVerification() == null || msg.getVerification().equals("")) {
			return;
		}
		if (msg.getPassword() == null || msg.getPassword().equals("")) {
			return;
		}
		if (player.getProtectPIN()== null || player.getProtectPIN().equals("")) {
			/*xiaozhuoming: 暂时没有用到
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("验证码超时，请重新获取验证码"));
			*/
			return;
		}
		
		if(player.getProtectPIN().equalsIgnoreCase(msg.getVerification())){	//验证码通过
			player.setProtectpassword(msg.getPassword());
			player.setProtectmail(msg.getMail());
			saveProtect(player,true);
			/*xiaozhuoming: 暂时没有用到
			MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("恭喜！您的钻石密码锁已设置成功！可在系统设置界面中更改密码"));
			*/
			ResPointToClientMessage cmsg= new ResPointToClientMessage();
			cmsg.setType((byte) 3);
			MessageUtil.tell_player_message(player, cmsg);
			
			ProtectLog prplog = new ProtectLog();
			prplog.setMail(player.getProtectmail());
			prplog.setPassword(player.getProtectpassword());
			prplog.setUserid(player.getUserId());
			LogService.getInstance().execute(prplog);
		}else {
			ResPointToClientMessage cmsg= new ResPointToClientMessage();
			cmsg.setType((byte) 1);
			MessageUtil.tell_player_message(player, cmsg);
			/*xiaozhuoming: 暂时没有用到
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("验证码错误"));
			*/
		}
	}







	/**输入密码解锁消息
	 * 
	 * @param player
	 * @param msg
	 */
	public void stReqPasswordUnlockToGameMessage(Player player ,ReqPasswordUnlockToGameMessage msg) {
		if (msg.getPassword() != null && !msg.getPassword().equals("")) {
			String md5str = md5(player.getProtectpassword());
			if (msg.getPassword().equals(md5str)) {
				ResPointToClientMessage cmsg= new ResPointToClientMessage();
				cmsg.setType((byte) 4);
				MessageUtil.tell_player_message(player, cmsg);
				/*xiaozhuoming: 暂时没有用到
				MessageUtil.notify_player(player, Notifys.CHAT_ROLE, ResManager.getInstance().getString("解锁成功,只要您IP不变或24小时内，将不会再弹出解锁窗口"));
				*/
				player.setProtectstatus(2);  //2表示解锁成功
				player.setProtecttime(System.currentTimeMillis());
				player.setProtectip(player.getLoginIp());
				
			}else {
				ResPointToClientMessage cmsg= new ResPointToClientMessage();
				cmsg.setType((byte) 2);
				MessageUtil.tell_player_message(player, cmsg);
				/*xiaozhuoming: 暂时没有用到
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("密码错误"));
				*/
			}
		}
	}



	/**后台修改2级密码或邮件消息
	 * 
	 * @param msg
	 */
	public void stReqBackstageModifyToGameMessage(ReqBackstageModifyToGameMessage msg) {
		saveProtect(msg.getUserid(),msg.getPassword(),msg.getMail(),false);
	}

	
	
	
	
	
	
	
	
	//MD5验证
	protected String md5(String str){
		if(str==null) return null;
		try{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] bytes = md5.digest(str.getBytes("UTF-8"));
			StringBuilder ret=new StringBuilder(bytes.length<<1);
			for(int i=0;i<bytes.length;i++){
			  ret.append(Character.forDigit((bytes[i]>>4)&0xf,16));
			  ret.append(Character.forDigit(bytes[i]&0xf,16));
			}
			return ret.toString();
		}catch (Exception e) {
			log.error(e, e);
		}
		return null;
	}

	/**
	 * 是否开启2级密码锁，默认true开启
	 * 
	 */
	public boolean isIsopenlock() {
		return isopenlock;
	}

	/**
	 * 是否开启2级密码锁，默认true开启
	 * 
	 */
	public void setIsopenlock(boolean isopenlock) {
		this.isopenlock = isopenlock;
	}


	
	
}





