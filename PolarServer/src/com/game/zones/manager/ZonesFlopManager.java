package com.game.zones.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.log4j.Logger;

import com.game.arrow.structs.ArrowReasonsType;
import com.game.backpack.bean.GoodsAttribute;
import com.game.backpack.bean.ItemInfo;
import com.game.backpack.bean.ItemReasonsInfo;
import com.game.backpack.manager.BackpackManager;
import com.game.backpack.message.ResGetItemReasonsMessage;
import com.game.backpack.structs.Attribute;
import com.game.backpack.structs.Equip;
import com.game.backpack.structs.Item;
import com.game.backpack.structs.ItemTypeConst;
import com.game.backpack.structs.Pandora;
import com.game.backpack.structs.Pandora.PandoraItem;
import com.game.config.Config;
import com.game.count.structs.CountTypes;
import com.game.data.bean.Q_characterBean;
import com.game.data.bean.Q_clone_activityBean;
import com.game.data.bean.Q_itemBean;
import com.game.data.bean.Q_spirittree_kiwiBean;
import com.game.data.bean.Q_spirittree_pack_conBean;
import com.game.data.manager.DataManager;
import com.game.dblog.LogService;
import com.game.languageres.manager.ResManager;
import com.game.liveness.manager.LivenessManager;
import com.game.manager.ManagerPool;
import com.game.map.manager.MapManager;
import com.game.map.message.ResRoundMonsterDisappearMessage;
import com.game.map.structs.Map;
import com.game.monster.message.ResMonsterDieMessage;
import com.game.monster.structs.Monster;
import com.game.monster.structs.MonsterState;
import com.game.player.structs.AttributeChangeReason;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.rank.structs.RankType;
import com.game.server.impl.MServer;
import com.game.spirittree.manager.FruitAppendManager;
import com.game.spirittree.structs.FruitReward;
import com.game.spirittree.structs.FruitRewardAttr;
import com.game.structs.Reasons;
import com.game.task.struts.Task;
import com.game.task.struts.TaskEnum;
import com.game.team.manager.TeamManager;
import com.game.utils.MessageUtil;
import com.game.utils.RandomUtils;
import com.game.utils.Symbol;
import com.game.vip.manager.VipManager;
import com.game.zones.bean.ZoneRewardNumInfo;
import com.game.zones.log.ZoneRewardLog;
import com.game.zones.message.ReqSelectAwardMessage;
import com.game.zones.message.ReqZoneReceiveawardsMessage;
import com.game.zones.message.ResAutoRaidInfoMessage;
import com.game.zones.message.ResBuffAddMessage;
import com.game.zones.message.ResClearZoneCDMessage;
import com.game.zones.message.ResZoneContinuousRaidsMessage;
import com.game.zones.message.ResZoneLifeTimeMessage;
import com.game.zones.message.ResZoneNoticeRewardMessage;
import com.game.zones.message.ResZonePassShowMessage;
import com.game.zones.message.ResZoneSelectAwardMessage;
import com.game.zones.structs.ContinuousRaidsInfo;
import com.game.zones.structs.Raid;
import com.game.zones.structs.ZoneContext;


public class ZonesFlopManager {
	protected Logger log = Logger.getLogger(ZonesFlopManager.class);
	//玩家管理类实例
		private static ZonesFlopManager manager;
		private static Object obj = new Object();
		private ZonesFlopManager(){}
		
		public static ZonesFlopManager getInstance(){
			synchronized (obj) {
				if(manager == null){
					manager = new ZonesFlopManager();
				}
			}
			return manager;
		}
		
	//设置翻牌数量
	private int REWARDSUM = 10;
		
	/**奖励组包内容
	 * @return 
	 * 
	 */
	public HashMap<Integer, Q_spirittree_pack_conBean> getpackcondata(){
		 HashMap<Integer, Q_spirittree_pack_conBean> data = DataManager.getInstance().q_spirittree_pack_conContainer.getMap();
		return data;
	}
	
	
	
	public void sendZoneReward(Player player,ZoneContext zone) {
		
	}
	
	
	/**
	 * 读取 表数据 根据 副本通关评价   给予玩家奖励
	 * @param player
	 * @param zoneModelId 副本ID
	 * @param star  星级
	 */
	public void getZoneReward(Player player,int zoneModelId,int star,ResZonePassShowMessage smsg){
		Q_clone_activityBean zoneBean = ManagerPool.zonesManager.getContainer(zoneModelId);
		String[] rewards = zoneBean.getQ_reward().split(Symbol.JINGHAO_REG);
		if(star<rewards.length){
			String[] reward = rewards[star].split(Symbol.FENHAO);
			for (int i = 0; i < reward.length; i++) {
				String itemid = reward[i].split(Symbol.XIAHUAXIAN_REG)[0];
				String number = reward[i].split(Symbol.XIAHUAXIAN_REG)[1];
				int num = Integer.parseInt(number);
				FruitReward  fruitreward = new FruitReward();
				
				if(itemid.indexOf("!")>=0){
				
					fruitreward.setItemModelid(Integer.parseInt(itemid.replace("!", "")));
					fruitreward.setBind(false);
				
				}
				else{
				
					fruitreward.setItemModelid(Integer.parseInt(itemid));
					fruitreward.setBind(true);
				
				}
				
				fruitreward.setNum(num);
				
				giveRewarded(player,fruitreward,1);
				smsg.getItemlist().add(getItemInfo(fruitreward));
			}
		}
	}
	
	
	/**
	 * 发送 副本通关奖励     和通关消息 
	 * @param player
	 * @param zoneModelId
	 * @param star
	 * @param type
	 * @param ispass
	 */
	public void getZoneReward(Player player,int zoneModelId,int star,int type,byte ispass){
			Map map = MapManager.getInstance().getMap(player);
			ZoneContext zoneContext = ManagerPool.zonesManager.getContexts().get(map.getZoneId());
			ResZonePassShowMessage smsg = new ResZonePassShowMessage();
			int ztime = (int)(System.currentTimeMillis()/1000) - (int)zoneContext.getOthers().get("time");
			if(zoneContext!=null){
				smsg.setKillmonstrnum((Integer)zoneContext.getOthers().get("kill"));
			}
			if(star>5){
				star = 5;
			}
			getZoneReward(player, zoneModelId, star, smsg);
				try {
					if(player.getZoneinfo().containsKey(ZonesManager.ManualDeathnum+"_"+zoneModelId))
						smsg.setDeathnum( player.getZoneinfo().get(ZonesManager.ManualDeathnum+"_"+zoneModelId));
					if(player.getZoneinfo().containsKey(ZonesManager.killmonsternum+"_"+zoneModelId))
						smsg.setKillmonstrnum( player.getZoneinfo().get(ZonesManager.killmonsternum+"_"+zoneModelId));
					if(player.getZoneinfo().containsKey(ZonesManager.Manualendtime+"_"+zoneModelId))
						smsg.setTime(player.getZoneinfo().get(ZonesManager.Manualendtime+"_"+zoneModelId));
				} catch (Exception e) {
					log.error(e, e);
					smsg.setTime(ztime);			//完成时间
				}
//			Raid raid=player.getRaidinfo();
//			ztime = raid.getZoneraidtimes().get(zoneModelId+"");
			smsg.setZoneid(zoneModelId);
			smsg.setTime(ztime);
			smsg.setType((byte) type);
			smsg.setStar((byte) star);
			smsg.setIspass(ispass);
			MessageUtil.tell_player_message(player, smsg);
	}
	
	/**发送通关奖励
	 *  
	 * @param player
	 * @param msg 表示多人副本
	 */
	public void stZonePassShow(Player player,int type,int zoneModelId) 
	{
		Q_clone_activityBean zonedata = ManagerPool.zonesManager.getContainer(zoneModelId);
		List<FruitReward> rewardlist = null;
		if ( zonedata.getQ_zone_type() == 4) {	//如果没有临时储存的奖励选择，则重新随机一套奖励
			rewardlist = player.getRaidflop().getQiyaorewardlist();//奖励临时存放
			if (rewardlist.size() == 0) {
				List<FruitReward> fruitRewards= ManagerPool.zonesFlopManager.createFruitRewardList(player,1,zoneModelId);//筛选并设置奖励列表 
				rewardlist.clear();
				rewardlist.addAll(fruitRewards);
			}
		}else {
			rewardlist = player.getRaidflop().getManualrewardlist();//奖励临时存放
			if (rewardlist.size() == 0) {
				List<FruitReward> fruitRewards= ManagerPool.zonesFlopManager.createFruitRewardList(player,1,zoneModelId);//筛选并设置奖励列表 
				rewardlist.clear();
				rewardlist.addAll(fruitRewards);
			}
		}
		
		ResZonePassShowMessage smsg = new ResZonePassShowMessage();
		Raid raid=player.getRaidinfo();
		int ztime = 0;
		
		if (type <=1) {
			if (raid.getZoneraidtimes().containsKey(zoneModelId+"")) {
				ztime = raid.getZoneraidtimes().get(zoneModelId+"");
			}	
		}

		player.getRaidinfo().setRaidmanualzonemodelid(zoneModelId);// -------设定领取奖励的副本
		
		if (type == 0 || type == 3 || type == 4) { //手动
			try {
				if(player.getZoneinfo().containsKey(ZonesManager.ManualDeathnum+"_"+zoneModelId))
					smsg.setDeathnum( player.getZoneinfo().get(ZonesManager.ManualDeathnum+"_"+zoneModelId));
				if(player.getZoneinfo().containsKey(ZonesManager.killmonsternum+"_"+zoneModelId))
					smsg.setKillmonstrnum( player.getZoneinfo().get(ZonesManager.killmonsternum+"_"+zoneModelId));
				if(player.getZoneinfo().containsKey(ZonesManager.Manualendtime+"_"+zoneModelId))
					smsg.setTime(player.getZoneinfo().get(ZonesManager.Manualendtime+"_"+zoneModelId));
			} catch (Exception e) {
				log.error(e, e);
				smsg.setTime(ztime);			//完成时间
			}
		}else {
			smsg.setKillmonstrnum(0);	
			smsg.setTime(ztime);			//完成时间
		}
		
		smsg.setZoneid(zoneModelId);
		smsg.setThroughtime(ztime);
		smsg.setType((byte) type);
		for (FruitReward fruitReward : rewardlist) {
			smsg.getItemlist().add(getItemInfo(fruitReward));
		}
		
		if (smsg.getItemlist().size() > 0) {
			for (int i = 0; i < 25; i++) {
				int rnd = RandomUtils.random(1,smsg.getItemlist().size()) - 1;
				ItemInfo item = smsg.getItemlist().remove(rnd);
				smsg.getItemlist().add(item);
			}
		}
		if (type == 4) {
			if (player.getZoneinfo().containsKey(ZonesManager.Entrances+"_"+zoneModelId)) {
				int entrances = (Integer)player.getZoneinfo().get(ZonesManager.Entrances+"_"+zoneModelId);
				if (entrances == 1) {
					smsg.setIsfirst(1);
				}
			}
		}
		MessageUtil.tell_player_message(player, smsg);
	}
	
	
	
	
	/**生成ItemInfo
	 * 
	 * @param fruitReward
	 * @return
	 */
	public ItemInfo getItemInfo(FruitReward fruitReward){
		ItemInfo itemInfo = new ItemInfo();
		itemInfo.setIntensify((byte) fruitReward.getStrenglevel());
		if (fruitReward.isBind()) {
			itemInfo.setIsbind((byte) 1);
		}
		itemInfo.setItemModelId(fruitReward.getItemModelid());
		itemInfo.setNum(fruitReward.getNum());
		itemInfo.setLostTime((int) fruitReward.getLosttime());
		itemInfo.setAttributs((byte) fruitReward.getFruitRewardAttrslist().size());
		for (FruitRewardAttr fruitRewardAttr : fruitReward.getFruitRewardAttrslist()) {
			GoodsAttribute goodsatt=new GoodsAttribute();
			goodsatt.setType(fruitRewardAttr.getType());
			goodsatt.setValue(fruitRewardAttr.getValue());
			itemInfo.getGoodAttributes().add(goodsatt);
		}
		itemInfo.setGridId(-1);
		itemInfo.setItemId(fruitReward.getId());
		return itemInfo;
	}
	
	
	public ItemInfo getItemInfoBy(PandoraItem pandoraItem){
		ItemInfo itemInfo = new ItemInfo();
		itemInfo.setIntensify((byte) pandoraItem.getStrengthLevel());
//		if (pandoraItem.isBind()) {
//			itemInfo.setIsbind((byte) 1);
//		}
		itemInfo.setItemModelId(pandoraItem.getItemModelId());
		itemInfo.setNum(pandoraItem.getNum());
		itemInfo.setAddAttributLevel((byte)pandoraItem.getAddAttriuteLevel());
		
//		itemInfo.setLostTime((int) pandoraItem.get);
//		itemInfo.setAttributs((byte) pandoraItem.get);
//		for (FruitRewardAttr fruitRewardAttr : fruitReward.getFruitRewardAttrslist()) {
//			GoodsAttribute goodsatt=new GoodsAttribute();
//			goodsatt.setType(fruitRewardAttr.getType());
//			goodsatt.setValue(fruitRewardAttr.getValue());
//			itemInfo.getGoodAttributes().add(goodsatt);
//		}
//		itemInfo.setGridId(-1);
//		itemInfo.setItemId(pandoraItem.getId());
//		itemInfo.setParameters(parameters);
		return itemInfo;
	}
	
	
	/**
	 * 筛选并设置奖励列表 
	 * @param player
	 * @param fruit
	 * @param rewarddata
	 * @return 
	 */
	public List<FruitReward> createFruitRewardList(Player player,int type,int zonemodelid){
		Q_clone_activityBean zonedata = ManagerPool.zonesManager.getContainer(zonemodelid);
		List<Integer> grouplist=null;
		if (zonemodelid > 0 && zonedata.getQ_zone_type() == 4) {
			 grouplist= randomGroup(player,5);//如果是七曜战将副本，奖励类型为5
		}else{
			 grouplist= randomGroup(player,3);//其他副本奖励类型为3
		}

		HashMap<Integer, Q_spirittree_pack_conBean> rewarddata = getpackcondata();
		List<FruitReward> rewlist = new ArrayList<FruitReward>();
		for (Integer gid :grouplist) {
			List<Integer> tmpidxs =new ArrayList<Integer>(); // 储存符合条件的道具奖励索引
			List<Integer> tmprnds =new ArrayList<Integer>(); //选中道具的几率值
			Iterator<Entry<Integer, Q_spirittree_pack_conBean>> it = rewarddata.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Integer, Q_spirittree_pack_conBean> pack = it.next();
				Q_spirittree_pack_conBean data = pack.getValue();
				if (gid ==data.getQ_packet_id() ) {
					if (player.getLevel() >= data.getQ_need_level() && player.getLevel() < data.getQ_exclude_level() ) {
						if (data.getQ_item_id() > 0) {
							 Q_itemBean itemdata = ManagerPool.dataManager.q_itemContainer.getMap().get(data.getQ_item_id());
							 if (itemdata != null) {
								if (itemdata.getQ_type() == 1) {	
									//检测装备性别,没有性别要求了 luminghua hide
//									if (itemdata.getQ_sex() == 0 || itemdata.getQ_sex() == player.getSex()) {
										tmpidxs.add(data.getQ_id());
										tmprnds.add(data.getQ_selected_rnd());
//									}
								}else {
									tmpidxs.add(data.getQ_id());
									tmprnds.add(data.getQ_selected_rnd());
								}
							}
						}else {
							tmpidxs.add(data.getQ_id());
							tmprnds.add(data.getQ_selected_rnd());
						}
					}
				}
			}

			
			if (tmprnds.size() > 0) {
				int index= RandomUtils.randomIndexByProb(tmprnds);//在道具合集中随机一个道具
				int idx = tmpidxs.get(index);
				Q_spirittree_pack_conBean rewdata = rewarddata.get(idx);//获取选中道具信息
				FruitReward fruitreward = createFruitreward(rewdata);
				rewlist.add(fruitreward);
			}
		}
		return rewlist;
	}
	
	
	/**随机奖励组包 :
	 * @return 
	 * type=3  副本奖励
	 */
	public List<Integer> randomGroup(Player player ,int type){
		List<Integer > gidlist = new ArrayList<Integer>();  //选中的组包列表
		List<Integer > tmplist = new ArrayList<Integer>();	//临时补充列表
		HashMap<Integer, Integer> numMap= new HashMap<Integer, Integer>();
		List<Q_spirittree_kiwiBean> data = DataManager.getInstance().q_spirittree_kiwiContainer.getList();
		 for (Q_spirittree_kiwiBean kiwiBean : data) {
			 if  (kiwiBean.getQ_type() == type){
				 if (player.getLevel() >= kiwiBean.getQ_need_level()) {
					 int num = 0;
					 if(numMap.containsKey(kiwiBean.getQ_packet_id())){
						 num = numMap.get(kiwiBean.getQ_packet_id());
					 }
					 
					 for (int i = 0; i < kiwiBean.getQ_check_num(); i++) {
						 if(RandomUtils.isGenerate2(10000, kiwiBean.getQ_arise_rnd()) ){
							 if (num < kiwiBean.getQ_check_num() ) {	//复选
								if (gidlist.size() >= REWARDSUM) {
									 break;
								}
								 gidlist.add(kiwiBean.getQ_packet_id());
								 numMap.put(kiwiBean.getQ_packet_id(), num+1);
							}
						 } 
					}
				 }
				 tmplist.add(kiwiBean.getQ_packet_id());
			}

		}
		 
		if (gidlist.size() < REWARDSUM) {
			//组包不足10个，需要补充
			 if (tmplist.size() > 1) {
				 Collections.sort(tmplist, new Comparator<Integer>() {
					@Override
					public int compare(Integer o1, Integer o2) {
						if (o1 < o2) {
							return 1;
						}
						return 0;
					}
				} );
				int num = REWARDSUM - gidlist.size();//补充数量
				for (int i = 0; i < num; i++) {
					gidlist.add(tmplist.get(0));
				}
			}
		}
		return gidlist;
	}
	
	/**
	 * 创建首通礼包奖励
	 * @param bean
	 * @return
	 * @create	hongxiao.z      2014-1-2 下午7:40:45
	 */
	public FruitReward createFruitReward(Q_clone_activityBean bean)
	{
		FruitReward fruitReward = null;
		if(bean.getQ_first_award() != null)
		{
			String firstAward = bean.getQ_first_award();
			fruitReward = createFruitreward(1, 1, Integer.parseInt(firstAward));
		}
		return fruitReward;
	}
	
	/**
	 * 创建首通礼包奖励
	 * @param num
	 * @param sum
	 * @param modelid
	 * @return
	 * @create	hongxiao.z      2014-1-2 下午7:38:53
	 */
	private FruitReward createFruitreward(int num, int sum, int modelid)
	{
		FruitReward  fruitreward = new FruitReward();
		fruitreward.setNum(sum);
		fruitreward.setSum(sum);
		fruitreward.setItemModelid(modelid);
		fruitreward.setBind(false);
		return fruitreward;
	}
	
	/**
	 * 创建翻牌奖励
	 * @param rewdata
	 * @return
	 */

	public FruitReward createFruitreward(Q_spirittree_pack_conBean rewdata){
		if (rewdata != null ) {
			FruitReward  fruitreward = new FruitReward();
			fruitreward.setNum(rewdata.getQ_item_num());
			fruitreward.setSum(rewdata.getQ_item_num());
			fruitreward.setItemModelid(rewdata.getQ_item_id());
			fruitreward.setIdx(rewdata.getQ_id());
			fruitreward.setStrenglevel(rewdata.getQ_streng_level());	//产生道具属性
			fruitreward.setLosttime(rewdata.getQ_existtime());
			boolean isbidn = false;
			if (rewdata.getQ_is_binding() == 1) {
				isbidn=true;
			}
			fruitreward.setBind(isbidn);
			if (rewdata.getQ_addProperty_num() > 0 && rewdata.getQ_item_id() > 0) {
				FruitAppendManager.getInstance().buildAppend(fruitreward, rewdata.getQ_addProperty_num());
			}
			if (fruitreward.getItemModelid() == 0) {
				log.error("创建翻牌奖励=0");
			}
			return fruitreward;
		}
		return null;
	}
	
	public void stReqClearZoneCDMessage(Player player,int	zoneId) {
		
		int cdzone = zoneId;
		if(zoneId >= ZonesTeamManager.EMGC_MIN && zoneId <= ZonesTeamManager.EMGC_MAX){
			cdzone = ZonesTeamManager.EMGC_MIN;
		}
		if(zoneId >= ZonesTeamManager.XSCB_MIN && zoneId <= ZonesTeamManager.XSCB_MAX){
			cdzone = ZonesTeamManager.XSCB_MIN;
		}
		
		
		long manual = ManagerPool.countManager.getCount(player, CountTypes.ZONE_MANUAL, ""+cdzone);
		
		Q_clone_activityBean zonedata = ManagerPool.dataManager.q_clone_activityContainer.getMap().get(zoneId);
		
//		int vipLV = VipManager.getInstance().getVIPLevel(player);
////		 0-无VIP  1-白金VIP 2-钻石VIP 3-至尊VIP  101-临时VIP
//		int addcount = 0;
//		if(vipLV!=101){
//			addcount = vipLV;
//			
//		}
			
			if(manual<(zonedata.getQ_manual_num()+VipManager.getInstance().getDevilAddition(player))){
				
				long lastTime = ManagerPool.countManager.getLastTime(player, CountTypes.ZONE_MANUAL, ""+cdzone);
				
				//判断 此次 CD 是否购买过   0代表没买过
				
				if(lastTime!=0 && ManagerPool.countManager.getClearCd(player, CountTypes.ZONE_MANUAL, ""+cdzone)==0){
					int delCount = (int)((zonedata.getQ_wait_time()- (System.currentTimeMillis()-lastTime))/180/1000);
					if(delCount<1){
						delCount=1;
					}
				
					if(!ManagerPool.backpackManager.changeGold(player, -delCount, Reasons.LEVELUPBUFF,  Config.getId())){
						MessageUtil.notify_player(player,Notifys.NORMAL, ResManager.getInstance().getString("钻石不足"));
						return;
					}
				 ManagerPool.countManager.setClearCd(player, CountTypes.ZONE_MANUAL, ""+cdzone,(byte)1);
				 ResClearZoneCDMessage msg = new ResClearZoneCDMessage();
				 msg.setZoneid(zoneId);
				 MessageUtil.tell_player_message(player, msg);
				 
				ZonesTeamManager.getInstance().stReqZoneTeamOpenToGameMessage(player, 0);
				 
				}else{
					MessageUtil.notify_player(player,Notifys.NORMAL, ResManager.getInstance().getString("副本无CD  不需要清理"));
				}
				 
			}else{
				MessageUtil.notify_player(player,Notifys.NORMAL, ResManager.getInstance().getString("今日挑战次数已达上限 请明日再来"));
				return;
			}
	}
	private  int BUFF_1=1101;
	private  int BUFF_2=1102;
	public void stReqAddBuffMessage(Player player,byte type) {
		Map map = ManagerPool.mapManager.getMap(player);
		int zoneModelId = map.getZoneModelId();
		Q_clone_activityBean zonedata = ManagerPool.dataManager.q_clone_activityContainer.getMap().get(zoneModelId);
		if (zonedata == null) {
			return;
		}
		//  3层BUFF   随机  加 其中一层    每层 最多加 5层
		
		if( map.getParameters().get(player.getId()+"_Buff_Number_"+BUFF_1)==null){
			map.getParameters().put(player.getId()+"_Buff_Number_"+BUFF_1, 0);
		}
		if( map.getParameters().get(player.getId()+"_Buff_Number_"+BUFF_2)==null){
			map.getParameters().put(player.getId()+"_Buff_Number_"+BUFF_2, 0);
		}
		
		
		int buf1 = (Integer)map.getParameters().get(player.getId()+"_Buff_Number_"+BUFF_1);
		int buf2 = (Integer)map.getParameters().get(player.getId()+"_Buff_Number_"+BUFF_2);
		
		int count  = buf1+buf2;
//		最多 15层BUFF
		if(count>=10){
			MessageUtil.notify_player(player,Notifys.ERROR, ResManager.getInstance().getString("鼓舞效果已达上限"));
			return;
		}
		
		int buffid = 0;
		int resultcount = 0;
		if(type==1){
			 if(!VipManager.getInstance().canInspireAddition(player)){
					MessageUtil.notify_player(player,Notifys.ERROR, ResManager.getInstance().getString("只有VIP2以上等级才能使用钻石鼓舞"));
				 return ;
			 }
			
			if(!ManagerPool.backpackManager.changeGold(player, -2, Reasons.LEVELUPBUFF,  Config.getId())){
				MessageUtil.notify_player(player,Notifys.ERROR, ResManager.getInstance().getString("钻石不足"));
				return;
			}
//			if(new Random().nextInt(100)<50){
//				
//				MessageUtil.notify_player(player,Notifys.NORMAL, ResManager.getInstance().getString("鼓舞失败"));	
//				return;
//			}
			int random =0;
			
			if(buf1==5 || buf2==5 ){
				
			}else{
				 if(count>=10){
						MessageUtil.notify_player(player,Notifys.ERROR, ResManager.getInstance().getString("鼓舞效果已达上限"));
						return;
					}
				 random = new Random().nextInt(2);
			}
			switch (random) {
			case 0:
				if(buf1<5){
					buffid = BUFF_1;
					resultcount= buf1+1;
					if(random==0){
						break;
					}
				}
			case 1:
				if(buf2<5){
					buffid = BUFF_2;
					resultcount= buf2+1;
					if(random==1){
						break;
					}
				}
			default:
				break;
			}
			
			map.getParameters().put(player.getId()+"_Buff_Number_"+buffid, resultcount);
			ManagerPool.buffManager.addBuff(player, player, buffid,1, 0, 0, 0, 0);
			MessageUtil.notify_player(player,Notifys.ERROR, ResManager.getInstance().getString("鼓舞成功"));	
		}
		
		if(type==0){
			if(buf2+buf1>=10){
				MessageUtil.notify_player(player,Notifys.ERROR, ResManager.getInstance().getString("鼓舞 已达上限"));
				return;
			}
			
			if(!ManagerPool.backpackManager.changeMoney(player, -100000, Reasons.LEVELUPBUFF,  Config.getId())){
				MessageUtil.notify_player(player,Notifys.ERROR, ResManager.getInstance().getString("金币不足"));	
				return;
			}
			if(new Random().nextInt(100)<50){
				MessageUtil.notify_player(player,Notifys.ERROR, ResManager.getInstance().getString("鼓舞失败"));	
				return;
			}
			int random =0;
			if(buf2==5 || buf1==5){
				
			}else{
			 random = new Random().nextInt(2);
			}
		
			switch (random) {
			case 0:
				if(buf2<5){
					buffid = BUFF_2;
					resultcount= buf2+1;
					if(random==0){
						
						break;
					}
				}
			case 1:
				if(buf1<5){
					buffid = BUFF_1;
					resultcount= buf1+1;
					if(random==1){
						
						break;
					}
				}
			default:
				break;
			}
			map.getParameters().put(player.getId()+"_Buff_Number_"+buffid, resultcount);
			ManagerPool.buffManager.addBuff(player, player, buffid,1, 0, 0, 0, 0);
			
			MessageUtil.notify_player(player,Notifys.ERROR, ResManager.getInstance().getString("鼓舞成功"));	
		}
		ResBuffAddMessage smsg = new ResBuffAddMessage();
		smsg.setNum((byte)resultcount);
		smsg.setBuffId(buffid);
		MessageUtil.tell_player_message(player, smsg);
		
	}
	
//	public return_type name() {
//		
//	}
	
	
	/**选择奖励,0到9自选，不能重复选
	 * 
	 * @param player
	 * @param msg
	 */
	public void stReqSelectAwardMessage(Player player,ReqSelectAwardMessage msg) {
		if (player == null) {
			return;
		}
		Map map = ManagerPool.mapManager.getMap(player);
		if(map.getParameters().get("isfinsh")==null){
			return;
		}
		int zoneModelId = map.getZoneModelId();
		
		Q_clone_activityBean zonedata = ManagerPool.dataManager.q_clone_activityContainer.getMap().get(zoneModelId);
		if (zonedata == null) {
			return;
		}
		if( map.getParameters().get(player.getId()+"_getNumber")==null){
			map.getParameters().put(player.getId()+"_getNumber", 0);
		}
		
		int count  = (Integer)map.getParameters().get(player.getId()+"_getNumber");
		
		if(count>7){
			return;
		}
		//免费就放行
		if(	VipManager.getInstance().getMissionLotteryAddition(player)>count){
			
			

		}else if(!ManagerPool.backpackManager.changeGold(player, (int)(-10*Math.pow(2,count)), Reasons.FLOP_YB,  Config.getId())){
			//不免费就扣钱
			MessageUtil.notify_player(player,Notifys.NORMAL, ResManager.getInstance().getString("钻石不足"));
			return;
		}

		
		map.getParameters().put(player.getId()+"_getNumber", count+1);
		
		
		List<PandoraItem> li = Pandora.getRandomItemListByModelId(player, Integer.parseInt( zonedata.getQ_Participation_Award()), 1);
		
		
		PandoraItem pandoraItem = li.get(0);
		
		Q_itemBean q_itemBean = DataManager.getInstance().q_itemContainer.getMap().get(pandoraItem.getItemModelId());
		if (q_itemBean.getQ_type() == ItemTypeConst.COPPER) {
			// 金币
			BackpackManager.getInstance().changeMoney(player, pandoraItem.getNum(), Reasons.Pandora, Config.getId());
		} else if (q_itemBean.getQ_type() == ItemTypeConst.BINDGOLD) {
			// 绑钻
			BackpackManager.getInstance().changeBindGold(player, pandoraItem.getNum(), Reasons.Pandora, Config.getId());
		} else {
			// 普通物品
			List<Item> createItems = Item.createItems(pandoraItem.getItemModelId(), pandoraItem.getNum(), true, 0, pandoraItem.getStrengthLevel(), pandoraItem.getAddAttriuteLevel(),
					pandoraItem.getZhuoyue());
			BackpackManager.getInstance().addItems(player, createItems, Config.getId());
		}
		
		
		
		
			ResZoneSelectAwardMessage smsg = new ResZoneSelectAwardMessage();
			smsg.setType(msg.getType());
			smsg.setNum((byte)count);
			smsg.setIteminfo(getItemInfoBy(pandoraItem));
			smsg.setIdx(msg.getIdx());
			
			MessageUtil.tell_player_message(player, smsg);
			

			ManagerPool.playerManager.savePlayer(player);
	
		
/*		if (player == null) {   这部分 代码 被我重写了  汪振瀚
//			return;
//		}
//		
//		int zoneModelId = player.getRaidinfo().getRaidmanualzonemodelid();	//
//		Q_clone_activityBean zonedata = ManagerPool.dataManager.q_clone_activityContainer.getMap().get(zoneModelId);
//		if (zonedata == null) {
//			return;
//		}
//		if (zonedata.getQ_zone_type() == 1 && msg.getType() > 1) {
//			return;
//		}
//		if (zonedata.getQ_zone_type() > 1 && zonedata.getQ_zone_type() != msg.getType() ) {
//			return;
//		}
//		
//		List<Integer> idxlist = player.getRaidflop().getManualrewardidx();
//		List<FruitReward> rewardlist = player.getRaidflop().getManualrewardlist();//战役奖励临时存放
//		if (zonedata.getQ_zone_type() == 4) {
//			rewardlist = player.getRaidflop().getQiyaorewardlist();//七曜战将奖励临时存放
//		}
//		
//		
//		if (rewardlist.size() == 0) {
//			return;
//		}
//
//		if (idxlist.size() == 1) {	//现在只开放一次选择
//			return;
//		}
//		
//		int rnd= 0;
//		List<Integer> rndlist = new ArrayList<Integer>();
//		for (FruitReward fruitReward : rewardlist) {
//			 Q_spirittree_pack_conBean fruitdata = getpackcondata().get(fruitReward.getIdx());
//			 if (fruitdata == null) {
//				 rndlist.add(100);	//数据库改变后，默认值
//			}else{
//				rndlist.add(fruitdata.getQ_selected_rnd());
//			}
//		}
//		rnd = RandomUtils.randomIndexByProb(rndlist);
//
//		idxlist.add(rnd);
//		FruitReward fruitReward = rewardlist.remove(rnd);
//		
//		if (idxlist.size() == 1) {	//在这里进行传送，清除奖励数据
//			HashMap<String, Integer> rewmap = player.getZonerewardmap();
//			if (zonedata.getQ_zone_type() == 3) {//读多人副本奖励
//				rewmap = player.getZoneteamrewardmap();
//			}else if (zonedata.getQ_zone_type() == 4) {//读取七曜战将副本
//				rewmap= player.getZoneqiyaorewardmap();
//			}
//			
////			Raid raid=player.getRaidinfo();
//			if (zonedata.getQ_zone_type() == 4) {
//				player.getRaidflop().getQiyaorewardlist().clear();	//清除奖励
//			}else {
//				player.getRaidflop().getManualrewardlist().clear();	//清除奖励
//			}
//
//			idxlist.clear();
//			ZoneRewardLog zlog =new ZoneRewardLog();
//			zlog.setBeforerewardlist(rewmap.toString());
//			player.getRaidinfo().setRaidmanualzonemodelid(0);
//			
//			boolean is = false;	//标记当前地图次数是否存在
//			if (rewmap.containsKey(zoneModelId+"")) {
//				int num = rewmap.get(zoneModelId+"");
//				if (num > 0) {
//					num = num -1;
//					rewmap.put(zoneModelId+"",num);
//					is = true;	//次数已扣
//				}
//			}
//
//			if (is==false) {	//如果上面没扣除次数，则在这里找一个副本次数扣除
//				for (String strzid : rewmap.keySet()) {
//					int tmpnum = rewmap.get(strzid);
//					if(tmpnum > 0){
//						tmpnum = tmpnum -1;
//						rewmap.put(strzid, tmpnum);
//						is = true;
//						break;
//					}
//				}
//			}
//			if (is == false) {
//				return;
//			}
//			if(msg.getType() == 0){
//				giveZoneFixedReward(player, zoneModelId,0,false);//手动单人扫荡固定奖励
//				zlog.setZonemodelid(zoneModelId);
//			}else if (msg.getType() == 1) {
//				giveZoneFixedReward(player, zoneModelId,0,true);// 自动单人扫荡固定奖励
//				zlog.setZonemodelid(zoneModelId);
//			}else if (msg.getType() == 3) {						
//				giveZoneFixedReward(player, zoneModelId,0,false);//多人副本固定奖励
//				zlog.setZonemodelid(zoneModelId);
//			}else if (msg.getType() == 4) {
//				giveZoneFixedReward(player, zoneModelId,0,false);//七曜副本固定奖励
//				zlog.setZonemodelid(zoneModelId);
//				if (player.getZoneinfo().containsKey(ZonesManager.Entrances+"_"+zoneModelId)) {
//					int entrances = (Integer)player.getZoneinfo().get(ZonesManager.Entrances+"_"+zoneModelId);
//					if (entrances == 1) {
//						giveZoneFixedReward(player, zoneModelId,1,false);//七曜副本第一次额外奖励（参与奖）
//					}
//				}
//			}
//			
//			LoginRaidRewarded(player);
//			zlog.setPlayerid(player.getId());
//			zlog.setRemainderrewardlist(rewmap.toString());
//			LogService.getInstance().execute(zlog);		
//			
//			ResZoneSelectAwardMessage smsg = new ResZoneSelectAwardMessage();
//			smsg.setType(msg.getType());
//			smsg.setNum((byte) idxlist.size());
//			smsg.setIteminfo(getItemInfo(fruitReward));
//			MessageUtil.tell_player_message(player, smsg);
//
//			giveRewarded(player, fruitReward,0);	//给副本奖励
//			ManagerPool.playerManager.savePlayer(player);
	}*/	
	}

	

	/**得到副本奖励 
	 * type = 0副本抽奖，1通关固定奖励,2翻牌,3 扫荡
	 * 
	 * @param msg
	 */
	public void giveRewarded(Player player , FruitReward fruitReward,int type) {
		
		
		String rewardedname = ResManager.getInstance().getString("副本抽奖"); //默认0
		
		if  (type == 1) {
			rewardedname = ResManager.getInstance().getString("副本通关奖励");
		}else if (type == 2) {
			rewardedname = ResManager.getInstance().getString("翻牌奖励");
		}else if (type == 3) {
			rewardedname = ResManager.getInstance().getString("收藏奖励");
		}else if (type > 100) {
			rewardedname = "";
		}
		
		int id = fruitReward.getItemModelid();
		long action = Config.getId();
		//-1金币，-2钻石，-3真气，-4经验  -5绑钻-6精魄
		if (fruitReward.getNum() == 0) {
			return ;
		}
		boolean issuccess = true;
		List<Item> createItems = new ArrayList<Item>();
		String itemname="";
		if (id == -1) {
			itemname = ResManager.getInstance().getString("金币");
			if(player != null && ManagerPool.backpackManager.changeMoney(player, fruitReward.getNum(), Reasons.RAID_MONEY, action)){
//				MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("恭喜！获得了{1}{2}({3})"),rewardedname,itemname,fruitReward.getNum()+"");	
			}else {
				issuccess =false;
			}
			
//		}else if (id== -2) {
//			itemname = "钻石";
//			if(player != null && ManagerPool.backpackManager.changeGold(player, fruitReward.getNum(), Reasons.RAID_YUANBAO, action)){
//				MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, "恭喜！获得了{1}{2}({3})",rewardedname,itemname,fruitReward.getNum()+"");
//			}else {
//				issuccess =false;
//			}
//		}else if ( id == -3) {
//			itemname = ResManager.getInstance().getString("真气");
//			if(player != null){
//				ManagerPool.playerManager.addZhenqi(player, fruitReward.getNum(),AttributeChangeReason.FUBENG);
//				MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("恭喜！获得了{1}{2}({3})"),rewardedname,itemname,fruitReward.getNum()+"");
//			}
		}else if (id == -4) {
			itemname = ResManager.getInstance().getString("经验");
			if(player != null){
				ManagerPool.playerManager.addExp(player, fruitReward.getNum(),AttributeChangeReason.FUBENG);
//				MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("恭喜！获得了{1}{2}({3})"),rewardedname,itemname,fruitReward.getNum()+"");
			}
		}else if (id == -5) {
			itemname = ResManager.getInstance().getString("绑钻");
			if(player != null && ManagerPool.backpackManager.changeBindGold(player, fruitReward.getNum(), Reasons.RAID_BIND_YUANBAO, action)){
//				MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("恭喜！获得了{1}{2}({3})"),rewardedname,itemname,fruitReward.getNum()+"");
			}
//		}else if (id == -6) {	//战魂
//			itemname = ResManager.getInstance().getString("七曜战魂");
//			if(player != null){
//				ManagerPool.arrowManager.addFightSpiritNum(player, 1, fruitReward.getNum(), true, ArrowReasonsType.ZONES);
//				MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("恭喜！获得了{1}{2}({3})"),rewardedname,itemname,fruitReward.getNum()+"");
//			}
//		}else if (id == -7) {	//军功
//			itemname = ResManager.getInstance().getString("军功值");
//			if(player != null){
//				ManagerPool.rankManager.addranknum(player, fruitReward.getNum(), RankType.OTHER);
//				MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("恭喜！获得了{1}{2}({3})"),rewardedname,itemname,fruitReward.getNum()+"");
//			}
		}else if(id == -6)
		{
			itemname = ResManager.getInstance().getString("精魄");
			BackpackManager.getInstance().changeSpirit(player, fruitReward.getNum(), Reasons.PT_REWARD, action);
		}else if (id > 0) {
			Q_itemBean itemMode = ManagerPool.dataManager.q_itemContainer.getMap().get(fruitReward.getItemModelid());
			if (itemMode != null) {
				itemname = itemMode.getQ_name();
				createItems = Item.createItems(fruitReward.getItemModelid(), fruitReward.getNum(), fruitReward.isBind(),((fruitReward.getLosttime() == 0) ? fruitReward.getLosttime() : (System.currentTimeMillis() + fruitReward.getLosttime() * 1000)) , fruitReward.getStrenglevel(), null);
				List<FruitRewardAttr> attrs = fruitReward.getFruitRewardAttrslist();
				//写入属性
				if (itemMode.getQ_type()== ItemTypeConst.EQUIP) {
					if (attrs.size() > 0) {
						for (Item item : createItems) {
							Equip equip = (Equip)item;
							for (FruitRewardAttr attr : attrs) {
								Attribute attribute = new Attribute();
								attribute.setType(attr.getType());
								attribute.setValue(attr.getValue());
								equip.getAttributes().add(attribute);
							}
						}
					}
				}

				if(player != null && ManagerPool.backpackManager.getEmptyGridNum(player) >= createItems.size()) {
					BackpackManager.getInstance().addItems(player, createItems,Reasons.RAID_ITEM,action);
					MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("恭喜！获得了{1}({2})。"),itemMode.getQ_name(),fruitReward.getNum()+"");
				}else {
					issuccess =false;
				}
			}else {
				log.error(rewardedname+"道具item不存在：["+id +"]");
			}
		}else{
			log.error(rewardedname+"ID类型未知：["+id+"]");
		}
		
		if(issuccess == false){
			itemname = itemname+"("+fruitReward.getNum()+")";
			if (id > 0) {
				ManagerPool.mailServerManager.sendSystemMail(player.getId(),null,ResManager.getInstance().getString("系统邮件"),rewardedname+":"+itemname,(byte) 1,0,createItems);
			}else {
				if (id == -1 ) {	//金币
					List<Item> createItems2 = new ArrayList<Item>();
					createItems2.add(Item.createMoney(fruitReward.getNum()));
					ManagerPool.mailServerManager.sendSystemMail(player.getId(),null,ResManager.getInstance().getString("系统邮件"),rewardedname+":"+itemname,(byte) 1,0,createItems2);
				//}else if (id == -2 ) {	//钻石
				//	ManagerPool.mailServerManager.sendSystemMail(player.getId(),null,"系统邮件",rewardedname+":"+itemname,(byte) 2,fruitReward.getNum(),new ArrayList<Item>());
				}else if (id == -3) {	//真气
					List<Item> createItems2 = new ArrayList<Item>();
					createItems2.add(Item.createZhenQi(fruitReward.getNum()));
					ManagerPool.mailServerManager.sendSystemMail(player.getId(),null,ResManager.getInstance().getString("系统邮件"),rewardedname+":"+itemname,(byte) 1,0,createItems2);
				}else if ( id == -4 ) {	//经验
					List<Item> createItems2 = new ArrayList<Item>();
					createItems2.add(Item.createExp(fruitReward.getNum()));
					ManagerPool.mailServerManager.sendSystemMail(player.getId(),null,ResManager.getInstance().getString("系统邮件"),rewardedname+":"+itemname,(byte) 1,0,createItems2);
				}else if ( id == -5) {	//绑钻
					List<Item> createItems2 = new ArrayList<Item>();
					createItems2.add(Item.createBindGold(fruitReward.getNum()));
					ManagerPool.mailServerManager.sendSystemMail(player.getId(),null,ResManager.getInstance().getString("系统邮件"),rewardedname+":"+itemname,(byte) 1,0,createItems2);
				}
			}
			if (player != null) {
				MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM,ResManager.getInstance().getString("由于您的包裹已满，")+rewardedname+"："+itemname+ResManager.getInstance().getString(" 已经通过邮件发送给您。"));
			}
		}
	}

	
	/**登录发送扫荡奖励信息
	 * 
	 * @param player
	 */
	public void LoginRaidRewarded(Player player){
		Raid raidinfo = player.getRaidinfo();
		if (raidinfo != null && raidinfo.getRaidzonemodelid() > 0 && raidinfo.getRaidautoendtime() > 0) {
			ResAutoRaidInfoMessage reidmsg = new ResAutoRaidInfoMessage();
			reidmsg.setSurplustime(raidinfo.getRaidautoendtime() - (int)(System.currentTimeMillis()/1000));
			reidmsg.setZoneid(raidinfo.getRaidzonemodelid());
			MessageUtil.tell_player_message(player, reidmsg);
		}
		
		if (raidinfo != null && raidinfo.getQiyaoraidzonemodelid() > 0 && raidinfo.getQiyaoraidautoendtime() > 0) {
			ResAutoRaidInfoMessage reidmsg = new ResAutoRaidInfoMessage();
			reidmsg.setSurplustime(raidinfo.getQiyaoraidautoendtime() - (int)(System.currentTimeMillis()/1000));
			reidmsg.setZoneid(raidinfo.getQiyaoraidzonemodelid());
			MessageUtil.tell_player_message(player, reidmsg);
		}
		
		//---------------------------上面是单个自动扫荡，下面是连续自动扫荡 + 奖励信息
		//---------------------------------单人副本奖励数量信息
		ResZoneNoticeRewardMessage smsg = new ResZoneNoticeRewardMessage();
		int rewnum = 0;
		HashMap<String, Integer> rewardmap = player.getZonerewardmap();
		Iterator<Entry<String, Integer>> it = rewardmap.entrySet().iterator();
		while (it.hasNext()) {
			ZoneRewardNumInfo info = new ZoneRewardNumInfo();
			Entry<String, Integer> entry = it.next();
			
			int zid = Integer.parseInt(entry.getKey());
			info.setZoneid(zid);
			info.setNum(entry.getValue());
			smsg.getZoneRewardnuminfo().add(info);
			rewnum = rewnum + entry.getValue();
		}
		
		//七曜战将副本
		HashMap<String, Integer> qiyaorewardmap = player.getZoneqiyaorewardmap();
		Iterator<Entry<String, Integer>> qiyaoit = qiyaorewardmap.entrySet().iterator();
		while (qiyaoit.hasNext()) {
			ZoneRewardNumInfo info = new ZoneRewardNumInfo();
			Entry<String, Integer> entry = qiyaoit.next();
			
			int zid = Integer.parseInt(entry.getKey());
			info.setZoneid(zid);
			info.setNum(entry.getValue());
			smsg.getZoneRewardnuminfo().add(info);
			//rewnum = rewnum + entry.getValue();
		}
		
		
		
		//多人副本奖励数量
		HashMap<String, Integer> teamrewardmap = player.getZoneteamrewardmap();
		Iterator<Entry<String, Integer>> it2 = teamrewardmap.entrySet().iterator();
		while (it2.hasNext()) {
			ZoneRewardNumInfo info = new ZoneRewardNumInfo();
			Entry<String, Integer> entry = it2.next();
			
			int zid = Integer.parseInt(entry.getKey());
			info.setZoneid(zid);
			info.setNum(entry.getValue());
			smsg.getZoneRewardnuminfo().add(info);
			//rewnum = rewnum + entry.getValue();
		}
		
		MessageUtil.tell_player_message(player, smsg);
		
		
		//-------------------------------剧情副本-连续自动扫荡
		
		List<ContinuousRaidsInfo> Raidcontinuouslist = player.getRaidcontinuouslist();
		if (Raidcontinuouslist.size() > 0) {
			int zonenum = 0;
			for (int i = 0; i < Raidcontinuouslist.size(); i++) {
				ContinuousRaidsInfo zones= Raidcontinuouslist.get(i);
				if (zones.getStatus() == 0) {
					zonenum = zonenum+ 1;
				}
			}
			if (zonenum > 0) {
				ResZoneContinuousRaidsMessage conmsg = new ResZoneContinuousRaidsMessage();
				ContinuousRaidsInfo zuihou = Raidcontinuouslist.get(Raidcontinuouslist.size()-1);
				conmsg.setZonenum(zonenum);
				conmsg.setSumtime(zuihou.getTime() - player.getRaidcontinuoustime());
				conmsg.setPassedime((int)(System.currentTimeMillis()/1000 ) - player.getRaidcontinuoustime());
				conmsg.setRewardnum(rewnum);
				conmsg.setZonetype(1);
				MessageUtil.tell_player_message(player, conmsg);
			}else {
				Raidcontinuouslist.clear();
			}
		}	
		
		//-----------------------七曜连续扫荡
		List<ContinuousRaidsInfo> qiyaolist = player.getQiyaocontinuouslist();
		if (qiyaolist.size() > 0) {
			int zonenum = 0;
			for (int i = 0; i < qiyaolist.size(); i++) {
				ContinuousRaidsInfo zones= qiyaolist.get(i);
				if (zones.getStatus() == 0) {
					zonenum = zonenum+ 1;
				}
			}
			if (zonenum > 0) {
				ResZoneContinuousRaidsMessage conmsg = new ResZoneContinuousRaidsMessage();
				ContinuousRaidsInfo zuihou = qiyaolist.get(qiyaolist.size()-1);
				conmsg.setZonenum(zonenum);
				conmsg.setSumtime(zuihou.getTime() - player.getRaidcontinuoustime());
				conmsg.setPassedime((int)(System.currentTimeMillis()/1000 ) - player.getQiyaocontinuoustime());
				conmsg.setRewardnum(rewnum);
				conmsg.setZonetype(4);
				MessageUtil.tell_player_message(player, conmsg);
			}else {
				qiyaolist.clear();
			}
		}	
		
		
	}
	

	
	/**前端手动请求领取奖励消息  
	 * 
	 * 
	 * @param player
	 * @param msg
	 */
	public void stReqZoneReceiveawardsMessage(Player player,ReqZoneReceiveawardsMessage msg) {
		Map map = ManagerPool.mapManager.getMap(player);
		ZoneContext zone = ManagerPool.zonesManager.getContexts().get(
				map.getZoneId());
		if(zone == null){
			return ;
		}
			Iterator<Monster> monsters =    map.getMonsters().values().iterator();
			ResRoundMonsterDisappearMessage hidemsg = new ResRoundMonsterDisappearMessage();
			int star = 0;
			int  playerSouldCount =ManagerPool.backpackManager.getItemNum(player, 720101);
			int  playerSouldCount2 =ManagerPool.backpackManager.getItemNum(player, 720102);
			int  playerSouldCount3 =ManagerPool.backpackManager.getItemNum(player, 720100);
			boolean scuess = false;
			
			
			if(playerSouldCount>0){
				if(ManagerPool.backpackManager.removeItem(player, 720101,1, Reasons.GOODUSE, Config.getId())){
					scuess = true;
				}else{
					MessageUtil.notify_player(player, Notifys.ERROR,ResManager.getInstance().getString("扣除道具失败"));
				}
			}else if(playerSouldCount2>0){
				if(ManagerPool.backpackManager.removeItem(player, 720102,1, Reasons.GOODUSE, Config.getId())){
					scuess = true;
				}else{
					MessageUtil.notify_player(player, Notifys.ERROR,ResManager.getInstance().getString("扣除道具失败"));
				}
			}else if(playerSouldCount3>0){
				if(ManagerPool.backpackManager.removeItem(player, 720100,1, Reasons.GOODUSE, Config.getId())){
					scuess = true;
				}else{
					MessageUtil.notify_player(player, Notifys.ERROR,ResManager.getInstance().getString("扣除道具失败"));
				}
			}else{
				MessageUtil.notify_player(player, Notifys.ERROR,ResManager.getInstance().getString("请帮我拿回武器"));
			}
			if(scuess){
				
				while (monsters.hasNext()) {
					Monster monster = monsters.next();
					monster.setState(MonsterState.DIE);
					hidemsg.getMonstersIds().add(monster.getId());
					
				}
				
				int time =(int) (System.currentTimeMillis()/1000) - (Integer)zone.getOthers().get("time");
//				zone.getOthers().put("zoneprocess",7);
				ResZoneLifeTimeMessage timemsg = new ResZoneLifeTimeMessage();
				Q_clone_activityBean zoneBean = ManagerPool.dataManager.q_clone_activityContainer.getMap().get(map.getZoneModelId());
				timemsg.setSurplustime((zoneBean.getQ_exist_time()/1000)-time );
				timemsg.setZoneid(map.getZoneModelId());
				timemsg.setZoneprocess(-1);
				timemsg.setPlayerCount(0);
				MessageUtil.tell_map_message(map, timemsg);
				//发完之后 关闭副本
				zone.getOthers().put("zoneprocess",-1);
				star =   getStart(zoneBean.getQ_exist_time()-(time+180)*1000, zoneBean.getQ_time_evaluate());
				if(star==-1){
					MessageUtil.notify_player(player, Notifys.ERROR,ResManager.getInstance().getString("副本评价计算    配置错误"));
					return;
				}
				if(star>5){
					star=5;
				}
				for (Entry<Long, Player> entry : map.getPlayers().entrySet()) {
					Player menber = entry.getValue();
//					if(map.getPlayers().size()>1){
						LivenessManager.getInstance().completeXscb(menber);
//					}
					ManagerPool.zonesFlopManager.getZoneReward(menber, map.getZoneModelId(), star, 0,(byte)1);
					MessageUtil.tell_round_message(menber, hidemsg);
					

					//通过副本：
					ManagerPool.taskManager.action(player, Task.ACTION_TYPE_RANK, TaskEnum.COMPLETECLASSICBATTLE, 7001);
					//通过副本并且等到s级评价：
					if(star>=3){
						ManagerPool.taskManager.action(player, Task.ACTION_TYPE_RANK, TaskEnum.S_ZONE, 7001);
						if(star>=4){
							ManagerPool.taskManager.action(player, Task.ACTION_TYPE_RANK, TaskEnum.SS_ZONE, 7001);
							if(star>=5){
								ManagerPool.taskManager.action(player, Task.ACTION_TYPE_RANK, TaskEnum.SSS_ZONE, 7001);
							}
						}
					}
				}
			}
			
//		
//		Map map=ManagerPool.mapManager.getMap(player);
//		if (map.getZoneId() > 0) {
//			MessageUtil.notify_player(player, Notifys.ERROR,ResManager.getInstance().getString("在副本中不能领取自动扫荡奖励,离开副本后再领取吧"));
//			return;
//		}
//		
//		
//		if (msg.getZid() > 0) {
//			stZonePassShow(player,msg.getType(),msg.getZid());//发送奖励列表到前端
//			return;
//		}
//
//		if (msg.getType() == 3) {	//多人活动副本
//			HashMap<String, Integer> rewmap = player.getZoneteamrewardmap();
//			for (String strzid : rewmap.keySet()) {
//				int zid= Integer.valueOf(strzid);
//				if((int)rewmap.get(strzid) > 0){
//					stZonePassShow(player,msg.getType(),zid);//发送奖励列表到前端
//					break;
//				}
//			}
//		}else if (msg.getType() == 4) {	//七曜战将
//			HashMap<String, Integer> rewmap = player.getZoneqiyaorewardmap();
//			for (String strzid : rewmap.keySet()) {
//				int zid= Integer.valueOf(strzid);
//				if((int)rewmap.get(strzid) > 0){
//					stZonePassShow(player,msg.getType(),zid);//发送奖励列表到前端
//					break;
//				}
//			}
//		}else {	//单人
//			HashMap<String, Integer> rewmap = player.getZonerewardmap();
//			for (String strzid : rewmap.keySet()) {
//				int zid= Integer.valueOf(strzid);
//				if((int)rewmap.get(strzid) > 0){
//					stZonePassShow(player,msg.getType(),zid);//发送奖励列表到前端
//					break;
//				}
//			}
//		}

		
	}
	
	public int getStart(int time ,String value) {
		String[] values = value.split(";");
		if(values.length!=5){
			return -1;
		}
		if(time>Integer.parseInt(values[4]) ){
			return 5;
		}
		if(time>Integer.parseInt(values[3]) ){
			return 4;
		}
		if(time>Integer.parseInt(values[2]) ){
			return 3;
		}
		if(time>Integer.parseInt(values[1]) ){
			return 2;
		}
		if(time>Integer.parseInt(values[0]) ){
			return 1;
		}
		return 0;
	}
	
	
	
	/**解析副本奖励
	 * 
	 * @param str
	 * @return
	 */
	public int[][] getZoneFixedReward(String str) {
		if (str == null || str.equals("")) {
			return null;
		}
		
		String[] rewardlist = str.split(Symbol.FENHAO);
		int[][] items = new int[rewardlist.length][3];
		if (rewardlist.length > 0) {
			for (int i = 0; i < items.length; i++) {
				String[] tmp = rewardlist[i].split(Symbol.XIAHUAXIAN_REG);
				if (tmp.length >= 3) {
					items[i][0] = Integer.parseInt(tmp[0]);
					items[i][1] = Integer.parseInt(tmp[1]);
					items[i][2] = Integer.parseInt(tmp[2]);
				}else {
					items[i][0] = Integer.parseInt(tmp[0]);
					items[i][1] = Integer.parseInt(tmp[1]);
					items[i][2] = 1;
				}
			}
		}
		return items;
	}
	
	
	
	public HashMap<Integer, Integer> getZoneCountReward(Player player,int zoneModelid,int type ){
		return getZoneCountReward(player,zoneModelid,type,false);
		
	}
	
	
	
	/**副本奖励计算
	 * player
	 * zoneModelid  副本模版ID
	 * type = 0 通关固定奖励，1参与奖
	 *  isauto = fulse 手动扫荡，true 自动扫荡
	 */
	public HashMap<Integer, Integer> getZoneCountReward(Player player,int zoneModelid,int type,boolean isauto){
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		Q_clone_activityBean zoneBean = ManagerPool.zonesManager.getContainer(zoneModelid);
 		if (zoneBean != null) {
 			int[][] items = null;
 			if (type == 1) {	//参与奖
 				items=getZoneFixedReward(zoneBean.getQ_Participation_Award());
			}else if (type == 0){
				items = getZoneFixedReward(zoneBean.getQ_reward());
			}
 			if (items == null) {
 				MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("数据库奖励为空"));
				return null;
			}
 			//+++++++++++++世界地图副本，星星数量决定奖励
 			int starlevel = 0;
 			double xishu = 1;	//奖励系数
 			if (zoneBean.getQ_zone_type() == 1) {
	 			if (isauto) {//自动扫荡获取最高星记录
					HashMap<String, Integer> zonestar = player.getRaidinfo().getZonestar();
					if (zonestar.containsKey(""+zoneModelid)) {
						starlevel = zonestar.get(""+zoneModelid);
		 	 			if (starlevel == 2) {
		 	 				xishu = 0.7;
		 				}else if(starlevel <= 1){
		 					xishu = 0.5;
		 				}
					}else {
						xishu = 0.5;
					}
				}else {
	 				starlevel = ManagerPool.zonesManager.computeStar(player,zoneModelid);
	 				
	 	 			if (starlevel == 2) {
	 	 				xishu = 0.7;
	 				}else if(starlevel <= 1){
	 					xishu = 0.5;
	 				}
	 	 			if(zoneModelid==1){
	 					xishu =1;  
	 				}
				}
 			}

 			//+++++++++++++++世界地图副本，星星数量决定奖励
 			
 			
 			for (int[] is : items) {
 				if (is.length == 3 && is[2] > 1) {	//第3个数值大于1， 奖励存在系数，而且大于1
					int key = ManagerPool.dataManager.q_characterContainer.getKey(player.getJob(), player.getLevel());
					Q_characterBean data = ManagerPool.dataManager.q_characterContainer.getMap().get(key);

 					if (is[0] == -3) {	//真气
						map.put(is[0], (int) ( data.getQ_basis_zhenqi() * is[2] * xishu));
					}else if (is[0] == -4) {//经验
						map.put(is[0], (int) ( data.getQ_basis_exp() * is[2] * xishu));
					}else  {
						map.put(is[0], (int) ( is[1] * xishu));
					}
				}else {
					if (is[0] > 0) {
						map.put(is[0],(int) (is[1]));
					}else {
						map.put(is[0], (int) ( is[1] * xishu));
					}
				}
			}
 		}
 		return map;
	}
	
	
	/**给玩家副本固定奖励
	 * 
	 * @param player
	 * @param zoneModelid
	 * @param type
	 */
	public void giveZoneFixedReward(Player player,int zoneModelid,int type){
		giveZoneFixedReward(player,zoneModelid,type,false,(byte)1,-1);
		
	}
	
	/**给玩家副本固定奖励
	 * player
	 * zoneModelid  副本模版ID
	 * type = 0 通关固定奖励，1参与奖  
	 * isauto = fulse 手动扫荡，true 自动扫荡
	 */
	public void giveZoneFixedReward(Player player,int zoneModelId,int type,boolean isauto,byte isPass,int killNumber){
		HashMap<Integer, Integer> map = getZoneCountReward( player, zoneModelId, type,isauto);
		Iterator<Entry<Integer, Integer>> items = map.entrySet().iterator();
		ResZonePassShowMessage smsg = new ResZonePassShowMessage();
		while (items.hasNext()) {
			Entry<Integer, Integer> entry = (Entry<Integer, Integer>) items.next();
			int id = entry.getKey();
			int num = entry.getValue();
			FruitReward  fruitreward = new FruitReward();
			fruitreward.setItemModelid(id);
			fruitreward.setNum(num);
			fruitreward.setBind(true);
			giveRewarded(player,fruitreward,1);
			ItemInfo itemInfo = new ItemInfo();
			itemInfo.setItemModelId(id);
			itemInfo.setNum(num);
			smsg.getItemlist().add(itemInfo);
		}
		
	
		Raid raid=player.getRaidinfo();
		int ztime = 0;
		
		if (type <=1) {
			if (raid.getZoneraidtimes().containsKey(zoneModelId+"")) {
				ztime = raid.getZoneraidtimes().get(zoneModelId+"");
			}	
		}

		player.getRaidinfo().setRaidmanualzonemodelid(zoneModelId);// -------设定领取奖励的副本
		
		if (type == 0 || type == 3 || type == 4) { //手动
			try {
				if(player.getZoneinfo().containsKey(ZonesManager.ManualDeathnum+"_"+zoneModelId))
					smsg.setDeathnum( player.getZoneinfo().get(ZonesManager.ManualDeathnum+"_"+zoneModelId));
				if(player.getZoneinfo().containsKey(ZonesManager.killmonsternum+"_"+zoneModelId))
					smsg.setKillmonstrnum( player.getZoneinfo().get(ZonesManager.killmonsternum+"_"+zoneModelId));
				if(player.getZoneinfo().containsKey(ZonesManager.Manualendtime+"_"+zoneModelId))
					smsg.setTime(player.getZoneinfo().get(ZonesManager.Manualendtime+"_"+zoneModelId));
			} catch (Exception e) {
				log.error(e, e);
				smsg.setTime(ztime);			//完成时间
			}
		}else {
			smsg.setKillmonstrnum(0);	
			smsg.setTime(ztime);			//完成时间
		}
		
		smsg.setZoneid(zoneModelId);
		smsg.setThroughtime(ztime);
		smsg.setType((byte) type);
		
		
		if (smsg.getItemlist().size() > 0) {
			for (int i = 0; i < 25; i++) {
				int rnd = RandomUtils.random(1,smsg.getItemlist().size()) - 1;
				ItemInfo item = smsg.getItemlist().remove(rnd);
				smsg.getItemlist().add(item);
			}
		}
		if (type == 4) {
			if (player.getZoneinfo().containsKey(ZonesManager.Entrances+"_"+1)) {
				int entrances = (Integer)player.getZoneinfo().get(ZonesManager.Entrances+"_"+1);
				if (entrances == 1) {
					smsg.setIsfirst(1);
				}
			}
		}
		
		if(killNumber!=-1){
			smsg.setKillmonstrnum(killNumber);
			int star = killNumber/100;
			if(star>5){
				star = 5;
			}
			smsg.setStar((byte)star);
		}
		
		smsg.setIspass(isPass);
		
		MessageUtil.tell_player_message(player, smsg);
	}
 		
	
	/**设置每次通关奖励
	 * 
	 * @param player
	 * @param zonemodelid 类型不一样，奖励次数保存的地方不一样
	 */	
	public void addZoneReward(Player player ,int zoneModelId, boolean imme) {
		try {
			Q_clone_activityBean db = ManagerPool.dataManager.q_clone_activityContainer.getMap().get(zoneModelId);
			HashMap<String, Integer> rewardmap = player.getZonerewardmap();//默认读取单人副本奖励
			if (db.getQ_zone_type() == 3) {
				rewardmap = player.getZoneteamrewardmap();//多人副本奖励保存
				List<Player> mapSameTeam = TeamManager.getInstance().getZoneMapSameTeam(player);//获得同组队同副本的玩家列表
				if (!imme && mapSameTeam.size() >= 2) {
					//军衔任务加次数
					ManagerPool.taskManager.action(player, Task.ACTION_TYPE_RANK, TaskEnum.COMPLETETEAMZONE, 1);
				}
				try {
					log.error(String.format("设置每次通关奖励 player=[%s][%s],zoneModelId=[%d],imme=[%d],同副本地图组队人数=[%d]", player.getName(), String.valueOf(player.getId()), zoneModelId, imme ? 1 :0, mapSameTeam.size()));
					Iterator<Player> iterator = mapSameTeam.iterator();
					while(iterator.hasNext()) {
						Player curPlayer =  iterator.next();
						if (curPlayer != null) {
							log.error(String.format("设置每次通关奖励 同副本地图组队玩家=[%s][%s]", curPlayer.getName(), String.valueOf(curPlayer.getId())));
						}
					}
				} catch (Exception e) {
					log.error(e,e);
				}
			}else if (db.getQ_zone_type() == 1) {
				//军衔任务加次数 luminghua hide
//				ManagerPool.taskManager.action(player, Task.ACTION_TYPE_RANK, TaskEnum.COMPLETECLASSICBATTLE, 1);
			}else if (db.getQ_zone_type() ==4) {
				rewardmap = player.getZoneqiyaorewardmap();	//七曜战将副本
				
				//记录七曜副本通关次数（只是记录第一次领取）
				if (player.getZoneinfo().containsKey(ZonesManager.Entrances+"_"+zoneModelId)) {
					int entrances = (Integer)player.getZoneinfo().get(ZonesManager.Entrances+"_"+zoneModelId);
					player.getZoneinfo().put(ZonesManager.Entrances+"_"+zoneModelId,entrances+1);
				}else {
					player.getZoneinfo().put(ZonesManager.Entrances+"_"+zoneModelId,1);
				}
				
				List<FruitReward> rewardlist = player.getRaidflop().getQiyaorewardlist();
				if (rewardlist.size() == 0) { //生成七曜副本奖励
					List<FruitReward> fruitRewards= ManagerPool.zonesFlopManager.createFruitRewardList(player,1,zoneModelId);//筛选并设置奖励列表 
					player.getRaidflop().getQiyaorewardlist().addAll(fruitRewards);
				}
			}
			
			
			if (db.getQ_zone_type() !=4) {	//生成普通战役奖励
				List<FruitReward> rewardlist = player.getRaidflop().getManualrewardlist();
				if (rewardlist.size() == 0) {
				List<FruitReward> fruitRewards = ManagerPool.zonesFlopManager.createFruitRewardList(player,1,zoneModelId);//筛选并设置奖励列表 
				player.getRaidflop().getManualrewardlist().addAll(fruitRewards);
				}
			}

			
			if(!rewardmap.containsKey(zoneModelId+"")){
				rewardmap.put(zoneModelId+"", 1);
			}else {
				int num = rewardmap.get(zoneModelId+"") + 1;
				rewardmap.put(zoneModelId+"", num);
			}
			
			long status = ManagerPool.countManager.getCount(player, CountTypes.ZONE_TEAM_ST, ""+zoneModelId);
			if (status == 0) {
				ManagerPool.countManager.addCount(player, CountTypes.ZONE_TEAM_ST, ""+zoneModelId,1, 2, 0);
			}else {
				ManagerPool.countManager.addCount(player, CountTypes.ZONE_TEAM_ST, ""+zoneModelId,2);
			}
			
			ManagerPool.zonesManager.stReqZoneOpenPanelMessage(player,null);//刷新剩余次数
		} catch (Exception e) {
			log.error(e, e);
		}
	}
	
	
	
	
	/**循环领奖
	 * 
	 */
	public void autoAwardfor(Player player,int zonetype){
		HashMap<String, Integer> rewmap = null;
		if (zonetype == 4) {
			rewmap = player.getZoneqiyaorewardmap();	//七曜奖励次数保存
		}else {
			rewmap = player.getZonerewardmap();	//单人扫荡副本励次数保存
		}
		
		Iterator<Entry<String, Integer>> it = rewmap.entrySet().iterator();
		ResGetItemReasonsMessage cmsg = new ResGetItemReasonsMessage();
		while (it.hasNext()) {
			Entry<String, Integer> entry = it.next();
			int num =entry.getValue();
			for (int i = 0; i < num; i++) {//可领奖励次数
				FruitReward fruitReward = autoAward(player,Integer.parseInt(entry.getKey()));
				if (fruitReward != null) {
					ItemReasonsInfo info = new ItemReasonsInfo();
					info.setItemId(fruitReward.getItemModelid());
					info.setItemModelId(fruitReward.getItemModelid());
					info.setItemNum(fruitReward.getNum());
					info.setItemReasons(0);
					cmsg.getItemReasonsInfoList().add(info);
				}
			}
		}
		
//		HashMap<String, Integer> teamrewmap = player.getZoneteamrewardmap();//多人活动副本
//		Iterator<Entry<String, Integer>> teamit = teamrewmap.entrySet().iterator();
//		while (teamit.hasNext()) {
//			Entry<String, Integer> entry = teamit.next();
//			int num =entry.getValue();
//			for (int i = 0; i < num; i++) {
//				autoAward(player,Integer.parseInt(entry.getKey()));
//			}
//		}
		
		cmsg.setItemReasons(Reasons.RAID_ITEM.getValue());
		MessageUtil.tell_player_message(player, cmsg);
		LoginRaidRewarded(player);
	}
	
	
	
	
	/**自动领奖
	 * @return 
	 * 
	 */
	public FruitReward autoAward(Player player ,int zoneModelId){
		Q_clone_activityBean data = ManagerPool.zonesManager.getContainer(zoneModelId);
		if (data != null) {
			List<FruitReward> fruitRewards= ManagerPool.zonesFlopManager.createFruitRewardList(player,1,zoneModelId);//筛选并设置奖励列表 
			if (fruitRewards.size() > 0) {
				ZoneRewardLog zlog =new ZoneRewardLog();
				HashMap<String, Integer> rewmap = player.getZonerewardmap();
				if (data.getQ_zone_type() == 3) {//读多人副本奖励
					rewmap = player.getZoneteamrewardmap();
				}else if (data.getQ_zone_type() == 4) {
					rewmap = player.getZoneqiyaorewardmap();
				}
				
				zlog.setBeforerewardlist(rewmap.toString());
				
				boolean is = false;	//标记当前地图次数是否存在
				if (rewmap.containsKey(zoneModelId+"")) {
					int num = rewmap.get(zoneModelId+"");
					if (num > 0) {
						num = num -1;
						rewmap.put(zoneModelId+"",num);
						is = true;	//次数已扣
					}
				}

				if (is==false) {	//如果上面没扣除次数，则在这里找一个副本次数扣除
					for (String strzid : rewmap.keySet()) {
						int tmpnum = rewmap.get(strzid);
						if(tmpnum > 0){
							tmpnum = tmpnum -1;
							rewmap.put(strzid, tmpnum);
							is = true;
							break;
						}
					}
				}
				if (is == false) {
					return null;
				}
				
				if (data.getQ_zone_type() == 1) {//读战役单人副本奖励
					giveZoneFixedReward(player, zoneModelId,0,true,(byte)1,-1);	// 自动扫荡固定奖励
					zlog.setZonemodelid(zoneModelId);
				}else if (data.getQ_zone_type() == 3) {//读多人副本奖励
					giveZoneFixedReward(player, zoneModelId,0,false,(byte)1,-1);
					zlog.setZonemodelid(zoneModelId);
				}else if (data.getQ_zone_type() == 4) {
					giveZoneFixedReward(player, zoneModelId,0,false,(byte)1,-1);
					zlog.setZonemodelid(zoneModelId);
					if (player.getZoneinfo().containsKey(ZonesManager.Entrances+"_"+zoneModelId)) {
						int entrances = (Integer)player.getZoneinfo().get(ZonesManager.Entrances+"_"+zoneModelId);
						if (entrances == 1) {
							giveZoneFixedReward(player, zoneModelId,1,false,(byte)1,-1);//七曜副本第一次额外奖励（参与奖）
						}
					}
				}
				
				int rnd= 0;
				List<Integer> rndlist = new ArrayList<Integer>();
				for (FruitReward fruitReward : fruitRewards) {
					 Q_spirittree_pack_conBean fruitdata = getpackcondata().get(fruitReward.getIdx());
					 if (fruitdata == null) {
						 rndlist.add(100);	//数据库改变后，默认值
					}else{
						rndlist.add(fruitdata.getQ_selected_rnd());
					}
				}
				
				rnd = RandomUtils.randomIndexByProb(rndlist);
				FruitReward fruitReward = fruitRewards.get(rnd);
				giveRewarded(player, fruitReward,0);	//给副本奖励
				zlog.setPlayerid(player.getId());
				zlog.setRemainderrewardlist(rewmap.toString());
				LogService.getInstance().execute(zlog);
				return fruitReward;
			}
		}
		return null;
	}
	public static void main(String[] args) {
//		for (int i = 0; i < 99; i++) {
//			System.out.println(new Random().nextInt(3));
//		}
//		int random =0;
//		
//		int buffid = 0;
//		
//		int buf1 =5;
//		int buf2 =5;
//		int buf3 =5;
//		
//		switch (random) {
//		case 0:
//			if(buf1<5){
//				buffid = 1;
//				if(random==0){
//					break;
//				}
//			}
//		case 1:
//			if(buf2<5){
//				buffid = 2;
//				if(random==1){
//					break;
//				}
//			}
//		case 2:
//
//			if(buf3<5){
//				buffid = 3;
//				if(random==2){
//					break;
//				}
//			}
//
//		default:
//			break;
//		}
//		System.out.println(buffid);
		int result =0;
		for (int i = 1; i <8; i++) {
			result+=10*Math.pow(2,i-1);
			System.out.println(10*Math.pow(2,i-1));
		}
		System.out.println(result);
	}
	
	
}
