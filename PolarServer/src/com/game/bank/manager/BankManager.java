package com.game.bank.manager;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.game.backend.manager.BackendManager;
import com.game.bank.log.BankLog;
import com.game.bank.message.ReqQueryBankLogMessage;
import com.game.bank.message.ReqQueryBankLogToWorldMessage;
import com.game.bank.message.ReqSendBankLogToWorldMessage;
import com.game.bank.message.ResQueryBankLogMessage;
import com.game.bank.message.ResQueryBankMessage;
import com.game.config.Config;
import com.game.data.bean.Q_characterBean;
import com.game.data.bean.Q_prayBean;
import com.game.data.manager.DataManager;
import com.game.dblog.LogService;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.player.structs.AttributeChangeReason;
import com.game.player.structs.Player;
import com.game.pray.bean.PrayInfo;
import com.game.pray.log.PrayLog;
import com.game.pray.message.ResPrayInfoMessage;
import com.game.prompt.structs.Notifys;
import com.game.structs.Reasons;
import com.game.utils.MessageUtil;
import com.game.utils.TimeUtil;
import com.game.vip.manager.VipManager;

public class BankManager {

	private static final Logger log = Logger.getLogger("BANK");
	
	private static Object obj = new Object();
	
	//玩家管理类实例
	private static BankManager manager;
	
	private BankManager(){}
	
	public static BankManager getInstance(){
		synchronized (obj) 
		{
			if(manager == null)
			{
				manager = new BankManager();
			}
		}
		return manager;
	}
	
	//发送玩家投资信息
	public void sendBankStatToPlayer(Player player,int type) {
		if(type==0){
			ResQueryBankMessage msg = new ResQueryBankMessage();
			int lv =0;
			if(player.getBuyMonthBankTime()==null || player.getBuyMonthBankTime().equals("") ){
				lv = -1;
			}else{
				Calendar instance = Calendar.getInstance();
				int today = Integer.parseInt( ""+instance.get(Calendar.YEAR)+""+instance.get(Calendar.DAY_OF_YEAR));
				int buyday = Integer.parseInt(player.getBuyMonthBankTime()); 
				int sday = today-buyday  +1;
				if(sday<=30){
					msg.setCurryday(sday);
				}else{
					msg.setCurryday(31);
				}
				
			}
			msg.setBuyLv(lv);
			if(player.getMonthBankLog()!=null){
				msg.setNums(player.getMonthBankLog());
			}
			MessageUtil.tell_player_message(player, msg);
		}
		if(type==1){
			ResQueryBankMessage msg = new ResQueryBankMessage();
			int lv = player.getLvBank();
			if(lv==0){
				lv = -1;
			}
			msg.setBuyLv(lv);
			String numbs = "";
			for (Map.Entry<String, Integer> lvBanklog : player.getLvBankLog().entrySet()) {
				numbs+=lvBanklog.getKey()+"_"+lvBanklog.getValue()+",";
			}
			if(!numbs.equals("")){
				msg.setNums(numbs);
			}
			MessageUtil.tell_player_message(player, msg);
		}
	}
	
	
	//购买 月卡
	public void buyMonth(Player player) {
		if(player.getBuyMonthBankTime()!=null && !player.getBuyMonthBankTime().equals("")){
			MessageUtil.notify_player(player,Notifys.ERROR,ResManager.getInstance().getString("您已经购买过月卡投资 无法继续购买。"));
			return ;
		}
		if(ManagerPool.backpackManager.changeGold(player, -500, Reasons.MONTHBANK,  Config.getId())){
			Calendar instance = Calendar.getInstance();
			instance.setTimeInMillis(System.currentTimeMillis());
			player.setBuyMonthBankTime(""+instance.get(Calendar.YEAR)+""+instance.get(Calendar.DAY_OF_YEAR));
			sendOptionMessageToWorld(player.getName(), 500, 0,player);
			MessageUtil.notify_player(player,Notifys.SUCCESS,ResManager.getInstance().getString("购买成功。"));
			sendBankStatToPlayer(player, 0);
		}else{
			MessageUtil.notify_player(player,Notifys.ERROR,ResManager.getInstance().getString("钻石不足。"));
			return ;
		}
	}
	
	//领取即时 奖励
	public void getMonthRewardByFrist(Player player) {
		if(player.getMonthBankLog()!=null&&player.getMonthBankLog().contains("00,")){
			MessageUtil.notify_player(player,Notifys.ERROR,ResManager.getInstance().getString("已经领取过即时奖励了"));
			return;
		}
		if(ManagerPool.backpackManager.changeBindGold(player, 500, Reasons.MONTHBANK_FIST,  Config.getId())){
			String playerlog = player.getMonthBankLog();
			if(playerlog==null){
				playerlog="";
			}
			
			player.setMonthBankLog(playerlog+"00,");
			MessageUtil.notify_player(player,Notifys.SUCCESS,ResManager.getInstance().getString("领取月卡投资成功！获得{1}绑钻"),"500");
			sendOptionMessageToWorld(player.getName(), 500, 1,player);
			sendBankStatToPlayer(player, 0);
		}
	}
	
	//每日 领取月卡
	public void getMonthRewardByDay(Player player) {
		Calendar instance = Calendar.getInstance();
		instance.setTimeInMillis(System.currentTimeMillis());
		int today = Integer.parseInt( ""+instance.get(Calendar.YEAR)+""+instance.get(Calendar.DAY_OF_YEAR));
		int buyday = Integer.parseInt(player.getBuyMonthBankTime()); 
		int sday = today-buyday  +1;
		if( sday>30){
			MessageUtil.notify_player(player,Notifys.ERROR,ResManager.getInstance().getString("已经过了30天领取"));
		}
		if(player.getMonthBankLog()!=null&&player.getMonthBankLog().contains("0"+sday+",")){
			MessageUtil.notify_player(player,Notifys.ERROR,ResManager.getInstance().getString("已经领取过这个奖励了"));
			return;
		}
		if(ManagerPool.backpackManager.changeBindGold(player, 50, Reasons.MONTHBANK_DAY,  Config.getId())){
			String playerlog = player.getMonthBankLog();
			if(playerlog==null){
				playerlog="";
			}
			player.setMonthBankLog(playerlog+"0"+sday+",");
			MessageUtil.notify_player(player,Notifys.SUCCESS,ResManager.getInstance().getString("领取月卡投资成功！获得{1}绑钻"),"50");
			sendOptionMessageToWorld(player.getName(), 50, 1,player);
			sendBankStatToPlayer(player, 0);
		}
	}
	
	//**-------------------------------------------分隔线  上面是 月卡 下面是 升级-----------------------------------------------------------------**//
	//购买升级投资
	public void buyLevelBank(Player player,int lv) {
		//目前只提供 6个等级的投资
		if(lv>4){
			MessageUtil.notify_player(player,Notifys.ERROR,ResManager.getInstance().getString("要购买的投资等级不存在"));
			return;
		}
		if(player.getLvBank()>=6){
			MessageUtil.notify_player(player,Notifys.ERROR,ResManager.getInstance().getString("您升级投资已经达到最高等级！"));
			return ;
		}
		if(player.getLvBank()>=lv){
			MessageUtil.notify_player(player,Notifys.ERROR,ResManager.getInstance().getString("您已经购买过 该级别的 投资！"));
			return ;
		}
		int price = getLvBankPrice(lv)-getLvBankPrice(player.getLvBank());
		if(ManagerPool.backpackManager.changeGold(player, -price, Reasons.LVBANK,  Config.getId())){
			player.setLvBank(lv);
			sendOptionMessageToWorld(player.getName(), price, 2,player);
			MessageUtil.notify_player(player,Notifys.SUCCESS,ResManager.getInstance().getString("购买成功。"));
			sendBankStatToPlayer(player, 1);
		}else{
			MessageUtil.notify_player(player,Notifys.ERROR,ResManager.getInstance().getString("钻石不足。"));
			return ;
		}
		
	}
	
	
	
	//领取等级 奖励
	public void getLevelBankByLevel(Player player ,int lv) {
		
		if(lv>player.getLevel()){
			MessageUtil.notify_player(player,Notifys.ERROR,ResManager.getInstance().getString("等级不足！"));
			return ;
		}
		
		
		if(player.getLvBank()==0){
			MessageUtil.notify_player(player,Notifys.ERROR,ResManager.getInstance().getString("擅未购买升级投资！"));
			return ;
		}
		
		//领取过
		if(player.getLvBankLog().get(Integer.toString(lv) )!=null){
			//计算玩家可以领取的 钱
			int playerBankPrice = getPlayerLvBankPrice(player.getLvBank(),lv) ;
				if(playerBankPrice==-1){
					MessageUtil.notify_player(player,Notifys.ERROR,ResManager.getInstance().getString("没有找到这个级别的投资配置"));
					return;
				}
			if( playerBankPrice <= player.getLvBankLog().get(Integer.toString(lv) ).intValue()){
				MessageUtil.notify_player(player,Notifys.ERROR,ResManager.getInstance().getString("已经领取过这个奖励了"));
				return;
			}else{
				
				//计算差价
				int price = playerBankPrice- player.getLvBankLog().get(Integer.toString(lv) ).intValue();
				//发放奖励
				if(ManagerPool.backpackManager.changeBindGold(player, price, Reasons.LVBANK_LV,  Config.getId())){
					player.getLvBankLog().put(Integer.toString(lv) , playerBankPrice);
					MessageUtil.notify_player(player,Notifys.SUCCESS,ResManager.getInstance().getString("领取升级投资成功！获得{1}绑钻"),price+"");
					//send 消息到世界服务器
					sendOptionMessageToWorld(player.getName(), price, 3,player);
					//发送消息给前端
					sendBankStatToPlayer(player, 1);
					return ;
				}
			}
			
		}
		int playerBankPrice = getPlayerLvBankPrice(player.getLvBank(),lv) ;
		if(playerBankPrice==-1){
			MessageUtil.notify_player(player,Notifys.ERROR,ResManager.getInstance().getString("没有找到这个级别的投资配置"));
			return;
		}
		if(ManagerPool.backpackManager.changeBindGold(player, playerBankPrice, Reasons.LVBANK_LV,  Config.getId())){
			player.getLvBankLog().put(Integer.toString(lv) , playerBankPrice);
			MessageUtil.notify_player(player,Notifys.SUCCESS,ResManager.getInstance().getString("领取升级投资成功！获得{1}绑钻"),playerBankPrice+"");
			sendOptionMessageToWorld(player.getName(), playerBankPrice, 3,player);
			sendBankStatToPlayer(player, 1);
		}
	}
	
	//**-------------------------------------------分隔线  上面是 业务方法 下面是  工具型方法  -----------------------------------------------------------------**//
	//获得购买投资的价格
	public int getLvBankPrice(int lv) {
		int result =0;
		switch (lv) {
		case 1:
			result = 1000;
			break;
		case 2:
			result = 3000;
			break;
		case 3:
			result = 5000;
			break;
		case 4:
			result = 10000;
			break;
		
		default:
			break;
		}
		return result;
	}
	
	//获得这个等级 玩家 应该获得多少 绑钻
	public int getPlayerLvBankPrice(int lv,int playerLv) {
		int rate = DataManager.getInstance().q_bank_rateContainer.getMap().get(playerLv).getQ_reward_rate();
		if(rate==0){
			return -1;
		}
		return (getLvBankPrice(lv) *rate) /100;
	}
	
	
	public void queryPlayersHistory(ReqQueryBankLogMessage msg,Player player) {
		ReqQueryBankLogToWorldMessage wmsg = new ReqQueryBankLogToWorldMessage();
		wmsg.setPlayerId(player.getId());
		wmsg.setIndexLarge(msg.getIndexLarge());
		wmsg.setIndexlittle(msg.getIndexlittle());
		wmsg.setOption(msg.getOption());
		MessageUtil.send_to_world(wmsg);
	}
	
	//发送玩家 操作记录 并保存用户操作
	public void sendOptionMessageToWorld(String name,int count,int option,Player player) {
		ManagerPool.playerManager.savePlayer(player);	
		ReqSendBankLogToWorldMessage wmsg = new ReqSendBankLogToWorldMessage();
		wmsg.setPlayerName(name);
		wmsg.setCount(count);
		wmsg.setOption(option);
		MessageUtil.send_to_world(wmsg);
		
		BankLog clog = new BankLog();
		clog.setCount(count);
		clog.setLevel(player.getLevel());
		clog.setPlayerId(player.getId());
		clog.setVip(VipManager.getInstance().getVIPLevel(player));
		clog.setType(option);
		clog.setTime(System.currentTimeMillis());
		LogService.getInstance().execute(clog);
		
		
	}
	
	public static void main(String[] args) {
		
	}
	
}