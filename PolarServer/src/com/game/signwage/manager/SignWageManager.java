package com.game.signwage.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.game.backpack.manager.BackpackManager;
import com.game.backpack.structs.Item;
import com.game.config.Config;
import com.game.data.bean.Q_sign_wageBean;
import com.game.dblog.LogService;
import com.game.languageres.manager.ResManager;
import com.game.mail.manager.MailServerManager;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.signwage.bean.SignInfo;
import com.game.signwage.log.FillSignLog;
import com.game.signwage.log.SignWageLog;
import com.game.signwage.message.ReqReceiveBeautyMessage;
import com.game.signwage.message.ResFillSignSucceedMessage;
import com.game.signwage.message.ResReceiveAwardsMessage;
import com.game.signwage.message.ResSignWageInfoMessage;
import com.game.signwage.message.RessignnumToClientMessage;
import com.game.signwage.structs.Sign;
import com.game.signwage.structs.Wage;
import com.game.spirittree.structs.FruitReward;
import com.game.structs.Reasons;
import com.game.utils.MessageUtil;
import com.game.utils.TimeUtil;

public class SignWageManager {
	private Logger log = Logger.getLogger(SignWageManager.class);
	
	private static Object obj = new Object();
	// 管理类实例 
	private static SignWageManager manager;

	//补签消耗的钻石数量
	private static int fillSignMoney = 10;
	
	private SignWageManager() {}

	public static SignWageManager getInstance() {
		synchronized (obj) {
			if (manager == null) {
				manager = new SignWageManager();
			}
		}
		return manager;
	}
	
	
	/**计算本次在线时间
	 * 
	 */
	//hongxiao.z    2014.3.15 华哥要求废弃
	public void computesignwageitem(Player player,int time){
//		try {
//			List<Wage> wagelist = player.getWagelist();
//			int day = TimeUtil.getOpenAreaDay();
//			int openareamonth = day/30;
//			if (day%30 == 0) {
//				openareamonth = openareamonth-1;
//			}
//			boolean isnew = false;
//			
//			if (wagelist.size() >= 2) {	//如果是2个数据
//				Wage sdata = wagelist.get(1);	//当前月
//				if (sdata.getMonthnum() == openareamonth) {
//					String record = "累积在线日志 "+player.getId()+" "+player.getName()+" 本月在线"+sdata.getCumulativetime()+"秒 +"+"累加在线"+time+"秒=当前本月在线"; //记录文本日志
//					sdata.setCumulativetime(sdata.getCumulativetime() + time);
//					record += sdata.getCumulativetime()+"秒,角色在线时间Accunonlinetime="+player.getAccunonlinetime()+" 角色登录时间loginlinetime="+player.getLoginlinetime();
//					log.error(record);
//				}else {
//					wagelist.remove(0);
//					isnew = true;
//				}
//			}else if (wagelist.size() == 1) {	//如果只有1个数据
//				Wage sdata = wagelist.get(0);
//				if (sdata.getMonthnum() == openareamonth) {
//					String record = "累积在线日志 "+player.getId()+" "+player.getName()+" 本月在线"+sdata.getCumulativetime()+"秒 +"+"累加在线"+time+"秒=当前本月在线"; //记录文本日志
//					sdata.setCumulativetime(sdata.getCumulativetime() + time);
//					record += sdata.getCumulativetime()+"秒,角色在线时间Accunonlinetime="+player.getAccunonlinetime()+" 角色登录时间loginlinetime="+player.getLoginlinetime();
//					log.error(record);
//				}else {
//					isnew = true;
//				}
//			}else {
//				isnew = true;
//			}
//			if (isnew) {
//				Wage signWage = new Wage();
//				signWage.setMonthnum(openareamonth);
//				String record = "累积在线日志 "+player.getId()+" "+player.getName()+" 新的月份 累加在线"+time+"秒"; //记录文本日志
//				signWage.setCumulativetime(time);
//				record += "角色在线时间Accunonlinetime="+player.getAccunonlinetime()+" 角色登录时间loginlinetime="+player.getLoginlinetime();
//				log.error(record);
//				wagelist.add(signWage);	//新的月份
//			}
//		} catch (Exception e) {
//			log.error("离线计算本月在线时间错误"+time+" , "+e);
//		}
	}
	
	
	/**上线计算 上次在线时间
	 * 
	 * @param player
	 */
	
	public void loginsignwageitem(Player player ){
		int time =player.getAccunonlinetime() - player.getLoginlinetime();
		if (time > 0) {
			computesignwageitem(player,time);
		}
		player.setLoginlinetime(player.getAccunonlinetime());
	}
	
	/**得到当前月的签到数据
	 * 
	 * @param player
	 * @return 
	 */
	public Sign getCurrentMonthSign(Player player ){
		int month = TimeUtil.getMonth(System.currentTimeMillis())+1;
		String key = ""+month;
		HashMap<String, Sign> signmap = player.getSignmap();
		if (signmap.containsKey(key)) {
			return signmap.get(key);
		}else{
			if (month == 1) 
			{	//如果当前是1月，而没有数据，则清理所有月的数据
				signmap.clear();
			}
			Sign sign = new Sign();
			signmap.put(key, sign);
			
			//ADD 月累计签到次数重置 hongxiao.z
			player.setSignsum(0);
			
			return sign;
		}
	}
	
	
	
	/**打开签到领奖励面板
	 * 
	 * @param player
	 */
	public void openSign(Player player){
		Sign sign = getCurrentMonthSign(player);
		SignInfo signInfo = new SignInfo();
		signInfo.setYear(TimeUtil.getYear(System.currentTimeMillis()));
		signInfo.setDaylist(sign.getDaylist());
		signInfo.setAward(sign.getRewardstatelist());
		signInfo.setDay(TimeUtil.getDayOfMonth(System.currentTimeMillis()));
		signInfo.setMonth(TimeUtil.getMonth(System.currentTimeMillis())+1);
		ResSignWageInfoMessage cmsg = new ResSignWageInfoMessage();
		cmsg.setSignInfo(signInfo);
		MessageUtil.tell_player_message(player, cmsg);
		
		loginRessignnum(player);
	}
	
	
	
	
	/**点击今日签到
	 * 
	 * @param player
	 */
	public void setSign(Player player){
		Sign sign = getCurrentMonthSign(player);
		if(sign.addSign()){
			MessageUtil.notify_player(player, Notifys.MOUSEPOS, ResManager.getInstance().getString("今日已经签到成功。"));
			player.setSignsum(player.getSignsum() +1);
			openSign(player);
			RessignnumToClientMessage cnms= new RessignnumToClientMessage();
			cnms.setSignnum(player.getSignsum());
			MessageUtil.tell_player_message(player, cnms);
			
			log.info(new StringBuilder("[每日签到][玩家ID:").append(player.getId()).append("]").toString());
			
//			Q_sign_wageBean wagebean = ManagerPool.dataManager.q_sign_wageContainer.getMap().get(2);	
//			if (wagebean != null) { //这个次数的奖励是否存在
//				if (ManagerPool.vipManager.getPlayerVipId(player) > 0) {//VIP额外奖励
//					toreward(player , wagebean.getQ_vip_reward(),1,"VIP");
//				}
//			}
		}else {
			MessageUtil.notify_player(player, Notifys.MOUSEPOS, ResManager.getInstance().getString("您今日已经签到了。"));
		}
	}
	
	
	/**领取美人
	 * @param player
	 * @param msg
	 * @update  hongxiao.z	2013-12-30
	 *  		暂时没有这个需求，已经屏蔽
	 */
	public void stReceiveBeauty(Player player,ReqReceiveBeautyMessage msg  ){
//		int receivemodel = 0;
//		String name="";
//		if (msg.getType() == 2 || msg.getType() == 3) {
//			if (msg.getType() == 2 && player.getSignsum() >= 7 ) {
//				int modelid= 7;//鹿丹儿
//				if(ManagerPool.petInfoManager.getPetByModelId(player, modelid) == null){
//					ManagerPool.petOptManager.addPet(player, modelid, "sign", Config.getId());
//					Q_petinfoBean model = DataManager.getInstance().q_petinfoContainer.getMap().get(modelid);
//					if (model != null) {
//						name = model.getQ_name();
//					}
//					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("恭喜您获得美人：{1}"),name);
//					receivemodel = 7;
//				}else {
//					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您已经有获得这个美人，不能重复领取 "));
//				}
//			}else if (msg.getType() == 3 && player.getSignsum() >= 25) {
//				int modelid= 10;//赵倩
//				if(ManagerPool.petInfoManager.getPetByModelId(player, modelid) == null){
//					ManagerPool.petOptManager.addPet(player, modelid, "sign", Config.getId());
//					Q_petinfoBean model = DataManager.getInstance().q_petinfoContainer.getMap().get(modelid);
//					if (model != null) {
//						name = model.getQ_name();
//					}
//					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("恭喜您获得美人：{1}"),name);
//					receivemodel = 25;
//				}else {
//					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您已经有获得这个美人，不能重复领取 "));
//				}
//			}else {
//				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("没有达到累计签到次数，不能领取美人"));
//			}
//		}else if (msg.getType() == 4) {
//			int modelid= 12;//赵致
//			Q_petinfoBean model = DataManager.getInstance().q_petinfoContainer.getMap().get(modelid);
//			if (model != null) {
//				name = model.getQ_name();
//			}
//			long logtime = Config.getId();
//			if(ManagerPool.petInfoManager.getPetByModelId(player, modelid) == null){
//				//检测2级密码
//				if (ManagerPool.protectManager.checkProtectStatus(player) ) {
//					return;
//				}
//				if (!ManagerPool.backpackManager.checkGold(player, 180)) {
//					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您没有180钻石，无法获得美人：{1}"),name);
//					return;
//				}
//				if (ManagerPool.backpackManager.changeGold(player, -180, Reasons.MEIREN, logtime)) {
//					ManagerPool.petOptManager.addPet(player, modelid, "gold", logtime);
//					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("恭喜您获得美人：{1}"),name);
//					receivemodel = 12;
//				}else {
//					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您没有180钻石，无法获得美人：{1}"),name);
//				}
//			}else {
//				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您已经有获得获得美人：{1}，不能重复领取"),name);
//			}
//		}else if (msg.getType() == 5 && player.getSignsum() >= 14) { //韩国领取赵致
//			int modelid= 12;//赵致
//			if (!WServer.getInstance().getServerWeb().equals("hgpupugame")) {	
//				return;
//			}
//			
//			Q_petinfoBean model = DataManager.getInstance().q_petinfoContainer.getMap().get(modelid);
//			if (model != null) {
//				name = model.getQ_name();
//			}
//			long logtime = Config.getId();
//			if(ManagerPool.petInfoManager.getPetByModelId(player, modelid) == null){
//				ManagerPool.petOptManager.addPet(player, modelid, "hgpupugame", logtime);
//				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("恭喜您获得美人：{1}"),name);
//				receivemodel = 12;
//			}else {
//				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您已经有获得获得美人：{1}，不能重复领取"),name);
//			}
//		}
//		//日志记录
//		try {
//			if(receivemodel>0){
//				SignWageLog log = new SignWageLog(player);
//				log.setType(3);
//				Map<String, String> parammap = new HashMap<String, String>();
//				parammap.put("modelid", String.valueOf(receivemodel));
//				parammap.put("msgtype", String.valueOf(msg.getType()));
//				parammap.put("signnum", String.valueOf(player.getSignsum()));
//				log.setContent(JSONserializable.toString(parammap));
//				LogService.getInstance().execute(log);
//			}
//		} catch (Exception e) {
//			log.error(e, e);
//		}
	}
	
	
	/**领取签到奖励
	 * 
	 * @param player
	 */
	public void receiveSignReward(Player player , int type ){
		Sign sign = getCurrentMonthSign(player);
		int num = sign.getDaylist().size();
		Q_sign_wageBean wagebean = ManagerPool.dataManager.q_sign_wageContainer.getMap().get(type);	
		
		if (wagebean != null) //这个次数的奖励是否存在
		{ 
			if (num >= type ) //签到次数是否达到
			{	
				List<Integer> rewardstatelist = sign.getRewardstatelist();
				if (rewardstatelist.contains(type)) 	//判断奖励是否领取过
				{
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您已经领取过这个签到奖励。"));
				}
				else 
				{
					rewardstatelist.add(type);		
					//需求不一致，暂时废弃
//					toreward(player , wagebean.getQ_reward(),1,null);
					reward(player, wagebean.getQ_reward());
					String rewardcontent = wagebean.getQ_reward(); //奖励内容 -用于日志记录
					if (wagebean != null) 
					{ //这个次数的奖励是否存在
						if (ManagerPool.vipManager.isVIP(player)) 
						{// VIP额外奖励
							reward(player, wagebean.getQ_vip_reward());
							//需求不一致，暂时废弃
//							toreward(player , wagebean.getQ_vip_reward(), 1, "VIP");
							rewardcontent += ";" + wagebean.getQ_vip_reward();
						}
					}
					
					//领取成功通知
					ResReceiveAwardsMessage resultMsg = new ResReceiveAwardsMessage((byte)type);
					MessageUtil.tell_player_message(player, resultMsg);
					
					//日志记录
					try {
						SignWageLog log = new SignWageLog(player);
						log.setType(1);
						log.setContent(rewardcontent);
						LogService.getInstance().execute(log);
					} catch (Exception e) {
						log.error(e, e);
					}
					
					log.info(new StringBuilder("[领取签到奖励][玩家ID:").append(player.getId())
							.append("]-[次数索引:").append(type).append("]-[奖励信息:").append(rewardcontent).append("]").toString());
				}
				
//				openSign(player);
				
			}else {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("签到次数没有达到{1}次，不能领取奖励。"),""+ type);
			}
			
			//考虑是否添加成功通知
		}
		else 
		{
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("没有这个类型的奖励内容。"));
		}
	}
	
	/**
	 * 领取签到奖励
	 * @create	hongxiao.z      2013-12-30 下午5:34:04
	 */
	private void reward(Player player, String itemsInfo)
	{
		//解析所有道具数据
		String[] itemInfo = itemsInfo.split("_");
		
		//声明道具容器装载道具
		List<Item> items = new ArrayList<Item>();
		
		//循环创建每个道具
		for (String iInfo : itemInfo) 
		{
			//解析获得道具原型ID和数量 下标0为道具id，1为数量
			String[] itemAndCount = iInfo.split(":");
			
			//获取绑定状态	0不绑定（false）， 1绑定（true）
			boolean bind = itemAndCount.length > 2 ? (Integer.parseInt(itemAndCount[2]) == 1) : true;
			
			List<Item> temps = Item.createItems(Integer.parseInt(itemAndCount[0]), Integer.parseInt(itemAndCount[1]), bind, 0);
			items.addAll(temps);
		}
		
		//获取入包所需容量空间
		int emptynum = BackpackManager.getInstance().getEmptyGridNum(player);
		
		//背包已满, 发邮件
		if(items.size() > emptynum)
		{
			MailServerManager.getInstance().sendSystemMail(player.getId(), player.getName(), ResManager.getInstance().getString("签到奖励"), ResManager.getInstance().getString("您的包裹格子不足，签到奖励未领取成功，请点击附件领取。"), (byte) 1, 0, items);
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您的包裹格子不足，不能获得签到奖励物品，已经转发邮件。"));
		}
		else
		{
			//道具入包
			BackpackManager.getInstance().addItems(player, items, Reasons.SIGN_REWARD, 0);
		}
	}
	
	/**
	 * 补签
	 * @param player
	 * @param day			要补签的天数（第几号）
	 * @create	hongxiao.z      2013-12-30 下午4:21:54
	 */
	public void fillSign(Player player, int day)
	{
		//防止负数
		day = (Math.abs(day));
		
		long nowTime = System.currentTimeMillis();
		
		//创建角色的日期
		Calendar cal = TimeUtil.getCalendar(player.getCreateTime());
		int createYear = cal.get(Calendar.YEAR);
		int createMonth = cal.get(Calendar.MONTH);
		int createDay  = cal.get(Calendar.DATE);
		
		//签到的日期计算
		cal = TimeUtil.getCalendar(nowTime);
		int fillYear = cal.get(Calendar.YEAR);
		int fillMonth = cal.get(Calendar.MONTH);
		
		//获取今天是几号
		int currDay = cal.get(Calendar.DATE);
		
		//时间是否有效可以签到
		//以玩家创建账号的时间为起始开始才可以签到
		boolean valid = createYear > fillYear ? false : createYear < fillYear ? true :
						createMonth > fillMonth ? false : createMonth < fillMonth ? true :
						createDay > day ? false : true;
		
		if(!valid)
		{
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("角色创建当天或之前的时间不能补签！"));
			return;
		}
		
		//不是今天前的日期不能补签
		if(day >= currDay)
		{
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("今天不是有效的补签时间！"));
			return;
		}
		
//		//是否开F当月
//		if(TimeUtil.isOpenMonth())
//		{
//			//获得开服时间
//			Calendar openDate = TimeUtil.getOpenTime();
//			
//			if(day <= openDate.get(Calendar.DATE))
//			{
//				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("开区前的时间不能补签！"));
//				return;
//			}
//		}
		
		//判断角色是否vip
		if(!ManagerPool.vipManager.canReSignIn(player))
		{//不是VIP不能补签
			return;
		}
		
		Sign sign = getCurrentMonthSign(player);
		
		//判断是否已经签到过这天
		if(sign.getDaylist().contains(day))
		{//已经签到过
			return;
		}
		
		//扣除钻石
		boolean result = ManagerPool.backpackManager.changeGold(player, -fillSignMoney, Reasons.FILL_SIGN, Config.getId());
		
		//判断钻石是否扣除成功
		if(!result)
		{
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("所需钻石不足,请充值后再补签"));
			return;
		}
		
		//签到成功执行
		sign.getDaylist().add(day);
		//累加签到次数
		player.setSignsum(player.getSignsum() + 1);
		//通知前端补签成功
		ResFillSignSucceedMessage msg = new ResFillSignSucceedMessage();
		msg.setDay(day);
		MessageUtil.tell_player_message(player, msg);
		
		//日志记录
		try {
			FillSignLog log = new FillSignLog(player);
			log.setDay(day);
			LogService.getInstance().execute(log);
		} catch (Exception e) {
			log.error(e, e);
		}
	}
	
	/**打工资面板
	 * 
	 * @param player
	 */
	public void openWage(Player player){
//		long time = (System.currentTimeMillis() - player.getLoginTime())/1000;
//		List<Wage> wagelist = player.getWagelist();
//		int day = TimeUtil.getOpenAreaDay();
//		int curmonth = day/30;
//		if (day%30 == 0) {
//			curmonth = curmonth -1;
//		}
//		int oldmonth = 0;
//		int oldms = 0;
//		int curms = 0;
//		int ernienum = 0;
//		WageInfo wageInfo = new WageInfo();
//		wageInfo.setCurmonth(day);
//		List<FruitReward> fruitRewards = new ArrayList<FruitReward>();
//		boolean isnew= false;
//		if (wagelist.size() >= 2) {
//			Wage wage = wagelist.get(1);
//			if (wage.getMonthnum() == curmonth) {
//				wage.clearernie();	//清理今日领奖次数
//				curms = wage.getCumulativetime() + (int)time;	//本月累计时间
//				wageInfo.setCurstatus((byte) wage.getStatus());	//本月领奖状态
//				fruitRewards.addAll(wage.getErnierewardlist()) ;
//				
//				oldms = wagelist.get(0).getCumulativetime();	//上月累计时间
//				oldmonth = wagelist.get(0).getMonthnum();	//上个月份
//				wageInfo.setOldstatus((byte) wagelist.get(0).getStatus());//本月领奖状态
//				
//				ernienum= wage.getErnienum();//摇奖次数
//			}else {
//				wageInfo.setOldstatus((byte) wagelist.get(1).getStatus());	//上月领奖状态
//				oldms = wagelist.get(1).getCumulativetime();	//上月累计时间
//				oldmonth = wagelist.get(1).getMonthnum();	//上个月份
//				wagelist.remove(0);	//删除更古老月份
//				isnew = true;
//
//			}
//		}else if (wagelist.size() == 1) {
//			Wage wage = wagelist.get(0);
//			if (wage.getMonthnum() == curmonth) {
//				wage.clearernie();	//清理今日领奖次数
//				ernienum= wage.getErnienum();//摇奖次数
//				fruitRewards.addAll(wage.getErnierewardlist()) ;	//今日已领取奖励
//				curms = wage.getCumulativetime() + (int)time;	//本月累计在线时间
//				wageInfo.setCurstatus((byte) wage.getStatus());	//本月领奖状态
//			}else {
//				oldms = wagelist.get(0).getCumulativetime();	//上月累计时间
//				oldmonth = wagelist.get(0).getMonthnum();	//上个月份
//				wageInfo.setOldstatus((byte) wagelist.get(0).getStatus());	//上个月领奖状态
//				isnew = true;
//			}
//		}else {
//			isnew = true;
//		}
//		
//		if (isnew) {
//			Wage signWage = new Wage();	//新的月份
//			signWage.setMonthnum(curmonth);
//			wagelist.add(signWage);	
//			curms = (int)time;
//		}
//
//		Date sdate = WServer.getGameConfig().getServerTimeByServer(WServer.getInstance().getServerId());
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		wageInfo.setSvrstarttime(format.format(sdate));
//		int curhour=curms/3600 ;
//		int oldhour=oldms/3600;
//		if (curhour > 300) {
//			curhour = 300;
//		}
//		if (oldhour > 300) {
//			oldhour = 300;
//		}
//		
//		int interval = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.ERNIE_INTERVAL.getValue()).getQ_int_value();
//		int minute = player.getDayonlinetime()/60 ;
//		int num  = minute/interval;
//		if (num > 4) {
//			num = 4;
//		}
//		
//		for (int i = 1; i <= 4; i++) {
//			if (i <= ernienum) {//已经领取的格子
//				wageInfo.getErnie().add(1);//1表示已经领取
//			}else if (i <= num) {	//可领取格子
//				wageInfo.getErnie().add(0);
//			}else {
//				wageInfo.getErnie().add(2);//条件未达到
//			}
//		}
//		if (wageInfo.getOldstatus() == 0) {
//			wageInfo.setOldwage(oldhour * getBasisWage(oldmonth));
//		}
//		if (wageInfo.getCurstatus() == 0) {
//			wageInfo.setCurwage(curhour * getBasisWage(curmonth) );
//		}
//
//		wageInfo.setDaytime(player.getDayonlinetime());//今日累计时间
//		wageInfo.setMonthtime(curms);	//本月累计时间
//		if (fruitRewards.size() > 0) {
//			for (FruitReward fruitReward : fruitRewards) {
//				wageInfo.getFruitRewardinfo().add(fruitReward.makeinfo());
//			}
//		}
//		
//		ResSignWagetoWageInfoMessage cmsg = new ResSignWagetoWageInfoMessage();
//		cmsg.setWageInfo(wageInfo);
//		MessageUtil.tell_player_message(player, cmsg);
	}
	
	
	
	/**领取工资
	 * 
	 * @param player
	 */
	public void receiveWage(Player player) {
//		long actionid = Config.getId();
//		int time = (int)((System.currentTimeMillis() - player.getLoginTime())/1000);
//		Wage oldwage = getOldWage(player);
//		if (oldwage != null) {
//			if (oldwage.getStatus() == 0) {
//				int oldhour= oldwage.getCumulativetime()/3600;
//				if (oldhour > 300) {//每月上限300小时
//					oldhour = 300;
//				}
//				int basis = getBasisWage(oldwage.getMonthnum());
//				int oldyb = oldhour * basis;
//				if (oldyb > 0) {
//					oldwage.setStatus(1);
//					ManagerPool.backpackManager.changeBindGold(player, oldyb, Reasons.WAGE_OLD_LIJING, actionid);
//					MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("恭喜您领取到上个月工资{1}绑钻"), oldyb+"");
//					//领取工资日志记录
//					try{
//						SignWageLog log = new SignWageLog(player);
//						log.setType(2); //领取工资
//						Map<String, String> parammap = new HashMap<String, String>();
//						parammap.put("hour", String.valueOf(oldhour));
//						parammap.put("monthnum", String.valueOf(oldwage.getMonthnum()));
//						parammap.put("num", String.valueOf(oldyb));
//						parammap.put("cumulativetime", String.valueOf(oldwage.getCumulativetime()));
//						log.setContent(JSONserializable.toString(parammap));
//						LogService.getInstance().execute(log);
//					}catch (Exception e) {
//						log.error(e, e);
//					}
//				}
//			}
//		}
//		
//		//领取本月工资
//		Wage curwage = getCurWage(player);
//		if (curwage != null) {
//			if (curwage.getStatus() == 0) {
//				int day = TimeUtil.getOpenAreaDay();
//				int num = day%30;
//				if (num != 0) {
//					MessageUtil.notify_player(player, Notifys.MOUSEPOS, ResManager.getInstance().getString("很抱歉，本月工资需要再等{1}天后才能领取"), (30-num)+"");
//					return;
//				}
//				int curhour= (curwage.getCumulativetime()+time)/3600 ;
//				if (curhour > 300) {
//					curhour = 300;
//				}
//				int basis = getBasisWage(curwage.getMonthnum());
//				int curyb = curhour * basis;
//				if (curyb > 0) {
//					curwage.setStatus(1);
//					ManagerPool.backpackManager.changeBindGold(player, curyb, Reasons.WAGE_CUR_LIJING, actionid);
//					MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("恭喜您领取到本月工资{1}绑钻"), curyb+"");
//					//领取工资日志记录
//					try{
//						SignWageLog log = new SignWageLog(player);
//						log.setType(2); //领取工资
//						Map<String, String> parammap = new HashMap<String, String>();
//						parammap.put("hour", String.valueOf(curhour));
//						parammap.put("monthnum", String.valueOf(oldwage.getMonthnum()));
//						parammap.put("num", String.valueOf(curyb));
//						parammap.put("cumulativetime", String.valueOf(oldwage.getCumulativetime()));
//						log.setContent(JSONserializable.toString(parammap));
//						LogService.getInstance().execute(log);
//					}catch (Exception e) {
//						log.error(e, e);
//					}
//				}
//			}else {
//				MessageUtil.notify_player(player, Notifys.MOUSEPOS, ResManager.getInstance().getString("很抱歉，您已经领取本月工资，请下个月再来吧。"));
//			}
//		}
//		openWage(player);
	}

		
	
	
	/**工资基础值
	 * @param num
	 * @return
	 */
	public int getBasisWage(int num ){
		if (num == 0 ) {
			return 20;
		}else if (num == 1) {
			return 40;
		}else {
			return 80;
		}
		
	}
	
	
	
	
	/**获取本月工资数据
	 * 
	 * @param player
	 * @return 
	 */
	public Wage getCurWage(Player player){
		List<Wage> wagelist = player.getWagelist();
		int day = TimeUtil.getOpenAreaDay();
		int curmonth = day/30;
		if (day%30==0) {
			curmonth = curmonth - 1;
		}
		if (wagelist.size() >= 2) {
			if (wagelist.get(1).getMonthnum() == curmonth) {
				wagelist.get(1).clearernie();
				return wagelist.get(1);
			}else {
				log.error("本月工资数据记录位置不对");
				return null;
			}
			
		}else if (wagelist.size() == 1) {
			Wage wage = wagelist.get(0);
			wage.clearernie();	//清理今日领奖次数
			return wagelist.get(0);
		}
		return null;
	}
	
	
	
	
	/**获取上个月工资数据
	 * 
	 * @param player
	 * @return 
	 */
	public Wage getOldWage(Player player){
		List<Wage> wagelist = player.getWagelist();
		int day = TimeUtil.getOpenAreaDay();
		int curmonth = day/30;
		if (day%30==0) {
			curmonth = curmonth - 1;
		}
		if (wagelist.size() >= 2) {
			if (wagelist.get(0).getMonthnum() != curmonth) {
				return wagelist.get(0);
			}
		}
		return null;
	}
	


	
	
	/**签到给奖励
	 * 
	 * @param player
	 * @param rewardstr
	 */
	//与需求不一致，暂时废弃 hongxiao.z
	public void toreward(Player player , String rewardstr,int type ,String explain  ){
//		String rewardname ="";
//		if (type == 1) {
//			rewardname =  ResManager.getInstance().getString("签到奖励");
//		}
//		if (explain !=null) {
//			rewardname= explain + rewardname;
//		}
//		List<Item> itemlist = new ArrayList<Item>();
//		int itemNum = 0;
//		//奖励道具（道具ID,数量,性别（0通，1男，2女）,绑定（0,1绑定）,消失时间,强化等级,附加属性(类型|值);道具ID,数量）
//		String[] activities_dataStrings = rewardstr.split(";");
//		for (int i = 0; i < activities_dataStrings.length; i++) {
//			String activities_item = activities_dataStrings[i];
//			if (activities_item != null && !activities_item.isEmpty()) {
//				String[] itemdataStrings = activities_item.split(",");
//				int itemid = 0;
//				int itemnum = 0;
//				int sex = 0;//1 男 2 女
//				boolean bind = true;
//				long losttime = 0;
//				int gradenum = 0;
//				int append = 0;
//				for (int j = 0; j < itemdataStrings.length; j++) {
//					String item_data = itemdataStrings[j];
//					if (item_data != null && !item_data.isEmpty()) {
//						switch (j) {
//							case 0: {
//								itemid = Integer.valueOf(item_data);
//							}
//							break;
//							case 1: {
//								itemnum = Integer.valueOf(item_data);
//							}
//							break;
//							case 2: {
//								sex = Integer.valueOf(item_data);
//							}
//							break;
//							case 3: {
//								int bindidx = Integer.valueOf(item_data);
//								bind = (bindidx == 1) ? true : false;
//							}
//							break;
//							case 4: {
//								losttime = Long.valueOf(item_data);
//							}
//							break;
//							case 5: {
//								gradenum = Integer.valueOf(item_data);
//							}
//							break;
//							case 6: {
//								append = Integer.valueOf(item_data);
//							}
//							break;
//						}
//					}
//				}
//				
//				if (itemdataStrings.length >= 2) {
//					if (itemid != 0 && itemnum != 0) {
//						if (sex == 0 || sex == player.getSex()) {
//							switch (itemid) {
//								case -1: {
//									Item moneyItem = Item.createMoney(itemnum);
//									if (moneyItem != null) {
//										itemlist.add(moneyItem);
//									}
//								}
//								break;
//								case -2: {
//									Item goldItem = Item.createGold(itemnum, true);
//									if (goldItem != null) {
//										itemlist.add(goldItem);
//									}
//								}
//								break;
//								case -3: {
//									Item zhenqiItem = Item.createZhenQi(itemnum);
//									if (zhenqiItem != null) {
//										itemlist.add(zhenqiItem);
//									}
//								}
//								break;
//								case -4: {
//									Item expItem = Item.createExp(itemnum);
//									if (expItem != null) {
//										itemlist.add(expItem);
//									}
//								}
//								break;
//								case -5: {
//									Item bindgoldItem = Item.createBindGold(itemnum);
//									if (bindgoldItem != null) {
//										itemlist.add(bindgoldItem);
//									}
//								}
//								break;
//								default: {
//									List<Item> items = Item.createItems(itemid, itemnum, bind, losttime == 0 ? losttime : System.currentTimeMillis() + losttime * 1000, gradenum, append);
//									if (!items.isEmpty()) {
//										itemlist.addAll(items);
//										itemNum += items.size();
//									}
//								}
//								break;
//							}
//						}
//					}
//				}
//			}
//		}
//		if (!itemlist.isEmpty()) {
//			if (BackpackManager.getInstance().getEmptyGridNum(player) >= itemNum) {
//				ResGetItemReasonsMessage sendMessage = new ResGetItemReasonsMessage();
//				for (int i = 0; i < itemlist.size(); i++) {
//					Item item = itemlist.get(i);
//					if (item != null) {
//						boolean is =true;
//						int num = item.getNum();
//						String itemname = item.acqItemModel().getQ_name();
//						if (item.getItemModelId() == -1) {//Money
//							if (!BackpackManager.getInstance().changeMoney(player, item.getNum(), Reasons.ACTIVITY_GIFT, Config.getId())) {
//								List<Item> items = new ArrayList<Item>();
//								items.add(Item.createMoney(item.getNum()));
//								MailServerManager.getInstance().sendSystemMail(player.getId(), player.getName(), ResManager.getInstance().getString("活动系统邮件"), ResManager.getInstance().getString("由于未知原因，您的活动中金币未领取成功，请点击附件领取。"), (byte) 1, 0, items);
//								MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("由于未知原因，签到奖励金币未领取成功，请点击邮件领取。"));
//								is=false;
//							}
////						} else if (item.getItemModelId() == -2) {//Gold
////							if (!BackpackManager.getInstance().changeBindGold(player, item.getNum(), Reasons.ACTIVITY_GIFT, Config.getId())) {
////								List<Item> items = new ArrayList<Item>();
////								items.add(Item.createBindGold(item.getNum()));
////								MailServerManager.getInstance().sendSystemMail(player.getId(), player.getName(), "活动系统邮件", "由于未知原因，您的活动中绑钻未领取成功，请点击附件领取。", (byte) 1, 0, items);
////								MessageUtil.notify_player(player, Notifys.ERROR, "由于未知原因，您的活动中绑钻未领取成功，请点击邮件领取。");
////							}
//						} else if (item.getItemModelId() == -3) {//zhenqi
//							if (PlayerManager.getInstance().addZhenqi(player, item.getNum(),AttributeChangeReason.SIGNWAGE) == 0 && item.getNum() != 0) {
//								List<Item> items = new ArrayList<Item>();
//								items.add(Item.createZhenQi(item.getNum()));
//								MailServerManager.getInstance().sendSystemMail(player.getId(), player.getName(), ResManager.getInstance().getString("签到奖励"), ResManager.getInstance().getString("由于未知原因，真气未领取成功，请点击附件领取。"), (byte) 1, 0, items);
//								MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("由于未知原因，签到奖励真气未领取成功，请点击邮件领取。"));
//								is=false;
//							}
//						} else if (item.getItemModelId() == -4) {//exp
//							PlayerManager.getInstance().addExp(player, item.getNum(),AttributeChangeReason.SIGNWAGE);
//						} else if (item.getItemModelId() == -5) {//bindgold
//							if (!BackpackManager.getInstance().changeBindGold(player, item.getNum(), Reasons.ACTIVITY_GIFT, Config.getId())) {
//								List<Item> items = new ArrayList<Item>();
//								items.add(Item.createBindGold(item.getNum()));
//								MailServerManager.getInstance().sendSystemMail(player.getId(), player.getName(), ResManager.getInstance().getString("签到奖励"), ResManager.getInstance().getString("由于未知原因，绑钻未领取成功，请点击附件领取。"), (byte) 1, 0, items);
//								MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("由于未知原因，签到奖励绑钻未领取成功，请点击邮件领取。"));
//								is=false;
//							}
//						}else if (item.getItemModelId() == -6) {	//战魂
//							itemname = ResManager.getInstance().getString("七曜战魂");
//							if(player != null){
//								ManagerPool.arrowManager.addFightSpiritNum(player, 1, item.getNum(), true, ArrowReasonsType.SIGN);
//								MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("恭喜！获得了{1}({2})"),itemname,item.getNum()+"");
//							}
//						}else if (item.getItemModelId() == -7) {	//军功
//							itemname = ResManager.getInstance().getString("军功值");
//							if(player != null){
//								ManagerPool.rankManager.addranknum(player, item.getNum(), RankType.OTHER);
//								MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("恭喜！获得了{1}({2})"),itemname,item.getNum()+"");
//							}
//						} else {//普通物品
//							List<Item> items = new ArrayList<Item>();
//							if (item instanceof Equip) {
//								Equip equip = (Equip) item;
//								items = Item.createItems(item.getItemModelId(), item.getNum(), item.isBind(), (long) equip.getLosttime() * 1000, equip.getGradeNum(), equip.getAttributes().size());
//							} else {
//								items = Item.createItems(item.getItemModelId(), item.getNum(), item.isBind(), (long) item.getLosttime() * 1000, 0, 0);
//							}
//							if (!BackpackManager.getInstance().addItems(player, items, Reasons.ACTIVITY_GIFT, Config.getId())) {
//								MailServerManager.getInstance().sendSystemMail(player.getId(), player.getName(), ResManager.getInstance().getString("签到奖励"), ResManager.getInstance().getString("由于未知原因，该物品未领取成功，请点击附件领取。"), (byte) 1, 0, items);
//								MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("由于未知原因，您签到奖励中有物品未领取成功，请点击邮件领取。"));
//								is=false;
//							} else {
//								ItemReasonsInfo itemReasonsInfo = new ItemReasonsInfo();
//								if (!items.isEmpty()) {
//									itemReasonsInfo.setItemId(items.get(0).getId());
//								}
//								itemReasonsInfo.setItemModelId(item.getItemModelId());
//								itemReasonsInfo.setItemNum(item.getNum());
//								itemReasonsInfo.setItemReasons(0);
//								sendMessage.getItemReasonsInfoList().add(itemReasonsInfo);
//							}
//						}
//						if (is) {
//							MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("恭喜！获得了{1}{2}({3})"),rewardname,itemname,num+"");
//						}
//					}
//				}
//				sendMessage.setItemReasons(Reasons.ACTIVITY_GIFT.getValue());
//				MessageUtil.tell_player_message(player, sendMessage);
//			} else {
//				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您的包裹格子不足，不能获得签到奖励物品，已经转发邮件。"));
//				MailServerManager.getInstance().sendSystemMail(player.getId(), player.getName(), ResManager.getInstance().getString("签到奖励"), ResManager.getInstance().getString("由于您的包裹格子不足，签到奖励物品未领取成功，请点击附件领取。"), (byte) 1, 0, itemlist);
//			}
//		}
	}
	
	
	
	/**在线摇奖
	 * 所有奖励均为绑定
	 */
	//暂无需求, hongxiao.z
	public void onlineERNIE(Player player ){
//		Wage wage = getCurWage(player);
//		int interval = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.ERNIE_INTERVAL.getValue()).getQ_int_value();
//		if (wage.getErnienum() >= 4) {
//			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("今日的摇奖次数已经全部使用，明天再来吧。"));
//			return;
//		}
//		
//		int minute = player.getDayonlinetime()/60 ;
//		int num  = minute/interval;
//		if (num > 4) {
//			num = 4;
//		}
//		
//		
//		int shake = num -wage.getErnienum();
//		ResWageERNIEinofMessage cmsg = new ResWageERNIEinofMessage();
//		for (int i = 0; i < shake; i++) {
//			List<FruitReward> fruitRewards= ManagerPool.zonesFlopManager.createFruitRewardList(player,1,0);//筛选并设置奖励列表 
//			if (fruitRewards.size() > 0) {
//				int rnd= 0;
//				List<Integer> rndlist = new ArrayList<Integer>();
//				for (FruitReward fruitReward : fruitRewards) {
//					 Q_spirittree_pack_conBean fruitdata = ManagerPool.zonesFlopManager.getpackcondata().get(fruitReward.getIdx());
//					 if (fruitdata == null) {
//						 rndlist.add(100);	//数据库改变后，默认值
//					}else{
//						rndlist.add(fruitdata.getQ_selected_rnd());
//					}
//				}
//				
//				rnd = RandomUtils.randomIndexByProb(rndlist);
//				FruitReward fruitReward = fruitRewards.get(rnd);
//				fruitReward.setBind(true);	//设置为绑定
//				giveRewarded(player, fruitReward,0);	//给奖励
//				cmsg.getFruitRewardinfo().add(fruitReward.makeinfo());
//				wage.getErnierewardlist().add(fruitReward);
//				wage.setErnienum(wage.getErnienum()+1);
//				cmsg.getPos().add(wage.getErnienum());
//			}
//		}
//		MessageUtil.tell_player_message(player, cmsg);
	}
	
	
	
	/**在线摇奖
	 * type = 0翻牌奖励，1通关固定奖励
	 * 
	 * @param msg
	 */
	//暂无需求
	public void giveRewarded(Player player , FruitReward fruitReward,int type) {
//		String rewardedname = ""; //默认0
//		if (type == 0) {
//			rewardedname = ResManager.getInstance().getString("在线时间摇奖");
//		}
//		
//		int id = fruitReward.getItemModelid();
//		long action = Config.getId();
//		//-1金币，-2钻石，-3真气，-4经验  -5绑钻
//		if (fruitReward.getNum() == 0) {
//			return ;
//		}
//		boolean issuccess = true;
//		List<Item> createItems = new ArrayList<Item>();
//		String itemname="";
//		if (id == -1) {
//			itemname = ResManager.getInstance().getString("金币");
//			if(player != null && ManagerPool.backpackManager.changeMoney(player, fruitReward.getNum(), Reasons.RAID_MONEY, action)){
//				MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("恭喜！获得了{1}{2}({3})"),rewardedname,itemname,fruitReward.getNum()+"");	
//			}else {
//				issuccess =false;
//			}
//			
////		}else if (id== -2) {
////			itemname = "钻石";
////			if(player != null && ManagerPool.backpackManager.changeGold(player, fruitReward.getNum(), Reasons.RAID_YUANBAO, action)){
////				MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, "恭喜！获得了{1}{2}({3})",rewardedname,itemname,fruitReward.getNum()+"");
////			}else {
////				issuccess =false;
////			}
//		}else if ( id == -3) {
//			itemname = ResManager.getInstance().getString("真气");
//			if(player != null){
//				ManagerPool.playerManager.addZhenqi(player, fruitReward.getNum(),AttributeChangeReason.SIGNWAGE);
//				MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("恭喜！获得了{1}{2}({3})"),rewardedname,itemname,fruitReward.getNum()+"");
//			}
//		}else if (id == -4) {
//			itemname = ResManager.getInstance().getString("经验");
//			if(player != null){
//				ManagerPool.playerManager.addExp(player, fruitReward.getNum(),AttributeChangeReason.SIGNWAGE);
//				MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("恭喜！获得了{1}{2}({3})"),rewardedname,itemname,fruitReward.getNum()+"");
//			}
//		}else if (id == -5) {
//			itemname = ResManager.getInstance().getString("绑钻");
//			if(player != null && ManagerPool.backpackManager.changeBindGold(player, fruitReward.getNum(), Reasons.RAID_BIND_YUANBAO, action)){
//				MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("恭喜！获得了{1}{2}({3})"),rewardedname,itemname,fruitReward.getNum()+"");
//			}
//			
//		}else if (id == -6) {	//战魂
//			itemname = ResManager.getInstance().getString("七曜战魂");
//			if(player != null){
//				ManagerPool.arrowManager.addFightSpiritNum(player, 1, fruitReward.getNum(), true, ArrowReasonsType.SIGN);
//				MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("恭喜！获得了{1}{2}({3})"),rewardedname,itemname,fruitReward.getNum()+"");
//			}
//		}else if (id == -7) {	//军功
//			itemname = ResManager.getInstance().getString("军功值");
//			if(player != null){
//				ManagerPool.rankManager.addranknum(player, fruitReward.getNum(), RankType.OTHER);
//				MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("恭喜！获得了{1}{2}({3})"),rewardedname,itemname,fruitReward.getNum()+"");
//			}	
//		}else if (id > 0) {
//			Q_itemBean itemMode = ManagerPool.dataManager.q_itemContainer.getMap().get(fruitReward.getItemModelid());
//			if (itemMode != null) {
//				itemname = itemMode.getQ_name();
//				createItems = Item.createItems(fruitReward.getItemModelid(), fruitReward.getNum(), fruitReward.isBind(),((fruitReward.getLosttime() == 0) ? fruitReward.getLosttime() : (System.currentTimeMillis() + fruitReward.getLosttime() * 1000)) , fruitReward.getStrenglevel(), null);
//				List<FruitRewardAttr> attrs = fruitReward.getFruitRewardAttrslist();
//				//写入属性
//				if (itemMode.getQ_type()== ItemTypeConst.EQUIP) {
//					if (attrs.size() > 0) {
//						for (Item item : createItems) {
//							Equip equip = (Equip)item;
//							for (FruitRewardAttr attr : attrs) {
//								Attribute attribute = new Attribute();
//								attribute.setType(attr.getType());
//								attribute.setValue(attr.getValue());
//								equip.getAttributes().add(attribute);
//							}
//						}
//					}
//				}
//
//				if(player != null && ManagerPool.backpackManager.getEmptyGridNum(player) >= createItems.size()) {
//					BackpackManager.getInstance().addItems(player, createItems,Reasons.RAID_ITEM,action);
//					MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("恭喜！获得了{1}{2}({3})。"),rewardedname,itemMode.getQ_name(),fruitReward.getNum()+"");
//				}else {
//					issuccess =false;
//				}
//			}else {
//				log.error(rewardedname+"道具item不存在：["+id +"]");
//			}
//		}else{
//			log.error(rewardedname+"ID类型未知：["+id+"]");
//		}
//		
//		if(issuccess == false){
//			itemname = itemname+"("+fruitReward.getNum()+")";
//			if (id > 0) {
//				ManagerPool.mailServerManager.sendSystemMail(player.getId(),null,ResManager.getInstance().getString("系统邮件"),rewardedname+":"+itemname,(byte) 1,0,createItems);
//			}else {
//				if (id == -1 ) {	//金币
//					List<Item> createItems2 = new ArrayList<Item>();
//					createItems2.add(Item.createMoney(fruitReward.getNum()));
//					ManagerPool.mailServerManager.sendSystemMail(player.getId(),null,ResManager.getInstance().getString("系统邮件"),rewardedname+":"+itemname,(byte) 1,0, createItems2);
//				//}else if (id == -2 ) {	//钻石
//				//	ManagerPool.mailServerManager.sendSystemMail(player.getId(),null,"系统邮件",rewardedname+":"+itemname,(byte) 2,fruitReward.getNum(),new ArrayList<Item>());
//				}else if (id == -3) {	//真气
//					List<Item> createItems2 = new ArrayList<Item>();
//					createItems2.add(Item.createZhenQi(fruitReward.getNum()));
//					ManagerPool.mailServerManager.sendSystemMail(player.getId(),null,ResManager.getInstance().getString("系统邮件"),rewardedname+":"+itemname,(byte) 1,0,createItems2);
//				}else if ( id == -4 ) {	//经验
//					List<Item> createItems2 = new ArrayList<Item>();
//					createItems2.add(Item.createExp(fruitReward.getNum()));
//					ManagerPool.mailServerManager.sendSystemMail(player.getId(),null,ResManager.getInstance().getString("系统邮件"),rewardedname+":"+itemname,(byte) 1,0,createItems2);
//				}else if ( id == -5) {	//绑钻
//					List<Item> createItems2 = new ArrayList<Item>();
//					createItems2.add(Item.createBindGold(fruitReward.getNum()));
//					ManagerPool.mailServerManager.sendSystemMail(player.getId(),null,ResManager.getInstance().getString("系统邮件"),rewardedname+":"+itemname,(byte) 1,0,createItems2);
//				}
//			}
//			if (player != null) {
//				MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM,ResManager.getInstance().getString("由于您的包裹已满，")+rewardedname+"："+itemname+ResManager.getInstance().getString(" 已经通过邮件发送给您。"));
//			}
//		}
	}
	
	
	/**登录发送累计签到次数
	 * 
	 * @param player
	 */
	public void loginRessignnum(Player player ){
		RessignnumToClientMessage cnms= new RessignnumToClientMessage();
		cnms.setSignnum(player.getSignsum());
		MessageUtil.tell_player_message(player, cnms);
	}
	

	
	public void testgm(Player player,int type)
	{
		Sign sign = getCurrentMonthSign(player);
		if (type == 1) {
			sign.getDaylist().clear();
			sign.getRewardstatelist().clear();
			player.setSignsum(31);
			for (int i = 1; i <= 31; i++) {
				sign.getDaylist().add(i);
			}
			openSign(player);
			loginRessignnum(player);
		}else if (type ==2) {
			Wage wage = getCurWage(player);
			wage.setErnieday(0);
			wage.setErnienum(0);
			openWage(player);
		}
	}
	
	
}
