/**
 * 
 */
package com.game.backpack.structs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.game.backpack.manager.BackpackManager;
import com.game.backpack.message.ResUseItemSuccessMessage;
import com.game.config.Config;
import com.game.data.bean.Q_PandoraBean;
import com.game.data.bean.Q_itemBean;
import com.game.data.manager.DataManager;
import com.game.drop.manager.CommonDrop;
import com.game.drop.structs.DropItem;
import com.game.drop.structs.MapDropInfo;
import com.game.languageres.manager.ResManager;
import com.game.map.bean.DropGoodsInfo;
import com.game.map.manager.MapManager;
import com.game.map.structs.Map;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.structs.Position;
import com.game.structs.Reasons;
import com.game.utils.BeanUtil;
import com.game.utils.MessageUtil;
import com.game.utils.RandomUtils;
import com.game.utils.Symbol;

/**
 * @author luminghua
 * 
 * @date 2013年12月18日 下午8:25:34
 * 
 *       潘多拉宝箱（礼包类）
 */
public class Pandora extends Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3832426760235648323L;

	private static Logger logger = Logger.getLogger(Pandora.class);

	private static final int MAX_GENERATE_NUM = 50;// 最大生成值

	/* 
	 * @see com.game.backpack.structs.Item#use(com.game.player.structs.Player, java.lang.String[])
	 */
	@Override
	public void use(Player player, String... parameters) {
		logger.error("开启潘多拉宝箱：" + player.getId() + "," + this.getId()+","+this.getItemModelId());
		int num = Integer.parseInt(parameters[0]);
		if (num <= 0 || this.getNum() < num) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("物品不足"));
			return;
		}
		List<PandoraItem> resultItems = Pandora.getRandomItemListByModelId(player, this.getItemModelId(), num);
		if (resultItems == null || resultItems.size() <= 0) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("随机物品错误"));
			return;
		}
		Q_PandoraBean q_PandoraBean = DataManager.getInstance().q_pandoraContainer.getMap().get(this.getItemModelId());
		
		// 配置物品是掉落在地上还是直接放到背包，默认是背包
		int q_drop_type = q_PandoraBean.getQ_drop_type();
		try {
			if (BackpackManager.getInstance().getEmptyGridNum(player) < resultItems.size()) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("背包空间不足"));
				return;
			}
			// 删除物品
			if (!BackpackManager.getInstance().removeItem(player, this, num, Reasons.Pandora, Config.getId())) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("扣除物品失败"));
				return;
			}
			
			// 发奖励
			StringBuilder messageBuilder = new StringBuilder();// 背包提示
			for (PandoraItem pandoraItem : resultItems) {
				List<Item> createItems = Item.createItems(pandoraItem.itemModelId, pandoraItem.num, this.isBind(), 0, pandoraItem.strengthLevel, pandoraItem.addAttriuteLevel,
						pandoraItem.zhuoyue);
				if (q_drop_type == 1) {// 掉落
					
					Map map = MapManager.getInstance().getMap(player);
					// 获取落点
					Position ableDropPoint = DropItem.getAbleDropPoint(player.getPosition(), map);
					for (Item item : createItems) {
						// 构造掉落物信息
						DropGoodsInfo buildDropInfo = item.buildDropInfo(ableDropPoint);
						buildDropInfo.setOwnerId(player.getId());
						buildDropInfo.setDropTime(System.currentTimeMillis());
						// 构造地图显示掉落物
						MapDropInfo mapDropInfo = new MapDropInfo(buildDropInfo, item, map, 0);
						mapDropInfo.setShow(true);
						CommonDrop commonDrop = new CommonDrop();// 没用的东西，我只是想调用drop而已，坑爹
						commonDrop.drop(mapDropInfo);
					}
					// 掉落物如果是金币或者绑钻，会在捡起来的时候判断
				} else {// 背包
					Q_itemBean q_itemBean = DataManager.getInstance().q_itemContainer.getMap().get(pandoraItem.itemModelId);
					if (q_itemBean.getQ_type() == ItemTypeConst.COPPER) {
						// 金币
						BackpackManager.getInstance().changeMoney(player, pandoraItem.num, Reasons.Pandora, Config.getId());
					} else if (q_itemBean.getQ_type() == ItemTypeConst.BINDGOLD) {
						// 绑钻
						BackpackManager.getInstance().changeBindGold(player, pandoraItem.num, Reasons.Pandora, Config.getId());
					} else {
						// 普通物品
						BackpackManager.getInstance().addItems(player, createItems, Reasons.Pandora, Config.getId());
//						BackpackManager.getInstance().addItems(player, createItems, Config.getId());
					}
					
					if (q_itemBean.getQ_auto_use() != 1) {
						messageBuilder.append(q_itemBean.getQ_name() + " ");
					}
				}
			}
			
			
			if (messageBuilder.length() > 0) 
			{
				//通知前端使用成功
				ResUseItemSuccessMessage msg = new ResUseItemSuccessMessage();
				msg.setItemId(this.getItemModelId());
//				msg.setItemId(-1);
				MessageUtil.tell_player_message(player, msg);
				messageBuilder.insert(0, "恭喜您获得：");
				MessageUtil.notify_player(player, Notifys.NORMAL, messageBuilder.toString());
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	/**
	 * 计算随机物品
	 * 
	 * @param player
	 * @param modelId
	 *            物品模板id
	 * @param num
	 *            使用数量，可以批量使用
	 * @return 如果返回null，就是策划配置有问题
	 */
	public static List<PandoraItem> getRandomItemListByModelId(Player player, int modelId, int num) {
		// 获取潘多拉配置
		Q_PandoraBean q_PandoraBean = DataManager.getInstance().q_pandoraContainer.getMap().get(modelId);
		if (q_PandoraBean == null) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("没有这个物品配置"));
			return null;
		}

		// 算出总概率并解析物品
		int totalRate = 0;
		List<PandoraItem> pandoraItemList = new ArrayList<PandoraItem>();
		for (int i = 1; i <= MAX_GENERATE_NUM; i++) {
			try {
				Object methodValue = BeanUtil.getMethodValue(q_PandoraBean, "Q_goods" + i);
				if (methodValue == null) {
					continue;
				}
				String goodsString = methodValue.toString();
				if (!StringUtils.isBlank(goodsString)) {
					// 权重 职业要求(111)_物品id_强化等级_追加等级_卓越属性 叠加数量
					String[] split = goodsString.split(" ");
					String[] itemStrings = split[1].split(Symbol.XIAHUAXIAN_REG);
					int job = player.getJob();
					boolean canUse = PlayerManager.checkJob(job, itemStrings[0]);
					if (!canUse) {
						// 职业不满足
						continue;
					}
					int rate = 0;
					PandoraItem item = new PandoraItem();
					if(split[0].startsWith("-")) {
						item.sure = true;
					}else {
						rate = Integer.parseInt(split[0]);
						if(rate == 0) {
							continue;
						}
					}
					item.rate = rate;
					item.itemModelId = Integer.parseInt(itemStrings[1]);
					if (itemStrings.length > 2) {
						item.strengthLevel = Integer.parseInt(itemStrings[2]);
					}
					if (itemStrings.length > 3) {
						item.addAttriuteLevel = Integer.parseInt(itemStrings[3]);
					}
					if (itemStrings.length > 4) {
						item.zhuoyue = itemStrings[4];
					}
					item.num = Integer.parseInt(split[2]);
					pandoraItemList.add(item);
					totalRate += rate;
				}
			} catch (Exception e) {
				logger.error("", e);
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("物品配置错误"));
				return null;
			}
		}
		if (totalRate <= 0) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("物品概率配置错误"));
			return null;
		}

		// 随机出来的物品
		List<PandoraItem> resultItems = new ArrayList<PandoraItem>();
		while (num-- > 0) {
			// 一个潘多拉可以开出多少礼品
			int count = RandomUtils.random(q_PandoraBean.getQ_item_min_num(), q_PandoraBean.getQ_item_max_num());
			if (count <= 0) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("物品生成数量配置错误"));
				return null;
			}
			int tmpTotalRate = totalRate;
			List<PandoraItem> tmpPandoraItemList = new ArrayList<PandoraItem>();
			tmpPandoraItemList.addAll(pandoraItemList);
			while (count-- > 0) {
				if (tmpTotalRate <= 0) {
					break;
				}
				int random = RandomUtils.random(tmpTotalRate);
				int addUp = 0;
				PandoraItem target = null;
				for (int j = 0; j < tmpPandoraItemList.size(); j++) {
					PandoraItem pi = tmpPandoraItemList.get(j);
					if (pi.sure || (pi.rate + addUp > random)) {
						target = pi;
						// 排除已经拿到的物品
						tmpPandoraItemList.remove(j);
						tmpTotalRate -= pi.rate;
						break;
					}
					// 累加
					addUp += pi.rate;
				}
				if (target == null) {
					logger.error("机率计算出错,random:" + random + ",list:" + tmpPandoraItemList);
					break;
				}
				resultItems.add(target);
			}
			if (resultItems.size() <= 0) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("随机物品错误"));
				return null;
			}

		}
		return resultItems;
	}
	
	
	public static void main(String[] args) {
//		System.out.println(randomObject(30, 35, 255)[0]);
	}
	
	/*
	 * luminghua
	 * public static int[]  randomObject(int ranStart,int ranEnd,int getPrize){
		
		int a = 0;
		int[] result={0,0,0,0,0,0,0,0};
			for (int i = 0; i < 8; i++) {
				
				Random random = new Random();
			    int ran =	random.nextInt(ranEnd-ranStart+1)+ranStart; 
			    a+=ran;
			    result[i]=a;
			
			}
		return result;
	}*/
	
	/**
	 *  获得 副本 翻盘奖励  集合    奖励内容 越翻越好
	 * @param player 
	 * @param modelId
	 * @param num
	 * @return
	 */
	/*
	 * luminghua
	 * public static List<PandoraItem> getAllRandomItemListByModelId(Player player, int modelId, int num) {
		
		// 获取潘多拉配置
		Q_PandoraBean q_PandoraBean = DataManager.getInstance().q_pandoraContainer.getMap().get(modelId);
		if (q_PandoraBean == null) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("没有这个物品配置"));
			return null;
		}
		
		int[] randoms = randomObject(30, 35, 255);
		int totalRate = 0;
		List<PandoraItem> pandoraItemList = new ArrayList<PandoraItem>();
		for (int i = 1; i <= MAX_GENERATE_NUM; i++) {
			try {
				Object methodValue = BeanUtil.getMethodValue(q_PandoraBean, "Q_goods" + i);
				if (methodValue == null) {
					continue;
				}
				String goodsString = methodValue.toString();
				if (!StringUtils.isBlank(goodsString)) {
					// 权重 职业要求(111)_物品id_强化等级_追加等级_卓越属性 叠加数量
					String[] split = goodsString.split(" ");
					
					int jilv = Integer.parseInt(split[0]);
					for (int j = 0; j < split.length; j++) {
						
					}
					
					
					String[] itemStrings = split[1].split(Symbol.XIAHUAXIAN_REG);
					int job = player.getJob();
					if (!itemStrings[0].equals("0") && !itemStrings[0].equals("111")) {
						boolean canUse = false;
						String q_job_limit_String = itemStrings[0];
						if (q_job_limit_String.length() < 2) {
							q_job_limit_String = "0" + q_job_limit_String;
						}
						if (q_job_limit_String.length() < 3) {
							q_job_limit_String = "0" + q_job_limit_String;
						}
						if (q_job_limit_String.charAt(job - 1) == 49) {// ascii码中49=1
							canUse = true;
						}
						if (!canUse) {
							// 职业不满足
							continue;
						}
					}
					PandoraItem item = new PandoraItem();
					int rate = Integer.parseInt(split[0]);
					if(rate == 0) {
						continue;
					}
					item.rate = rate;
					item.itemModelId = Integer.parseInt(itemStrings[1]);
					if (itemStrings.length > 2) {
						item.strengthLevel = Integer.parseInt(itemStrings[2]);
					}
					if (itemStrings.length > 3) {
						item.addAttriuteLevel = Integer.parseInt(itemStrings[3]);
					}
					if (itemStrings.length > 4) {
						item.zhuoyue = itemStrings[4];
					}
					item.num = Integer.parseInt(split[2]);
					pandoraItemList.add(item);
					totalRate += rate;
				}
			} catch (Exception e) {
				logger.error("", e);
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("物品配置错误"));
				return null;
			}
		}
		if (totalRate <= 0) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("物品概率配置错误"));
			return null;
		}

		// 随机出来的物品
		List<PandoraItem> resultItems = new ArrayList<PandoraItem>();
		while (num-- > 0) {
			// 一个潘多拉可以开出多少礼品
			int count = RandomUtils.random(q_PandoraBean.getQ_item_min_num(), q_PandoraBean.getQ_item_max_num());
			if (count <= 0) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("物品生成数量配置错误"));
				return null;
			}
			int tmpTotalRate = totalRate;
			List<PandoraItem> tmpPandoraItemList = new ArrayList<PandoraItem>();
			tmpPandoraItemList.addAll(pandoraItemList);
			while (count-- > 0) {
				if (tmpTotalRate <= 0) {
					break;
				}
				int random = RandomUtils.random(tmpTotalRate);
				int addUp = 0;
				PandoraItem target = null;
				for (int j = 0; j < tmpPandoraItemList.size(); j++) {
					PandoraItem pi = tmpPandoraItemList.get(j);
					if (pi.rate + addUp > random) {
						target = pi;
						// 排除已经拿到的物品
						tmpPandoraItemList.remove(j);
						tmpTotalRate -= pi.rate;
						break;
					}
					// 累加
					addUp += pi.rate;
				}
				if (target == null) {
					logger.error("机率计算出错,random:" + random + ",list:" + tmpPandoraItemList);
					break;
				}
				resultItems.add(target);
			}
			if (resultItems.size() <= 0) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("随机物品错误"));
				return null;
			}

		}
		return resultItems;
	}

*/
	/* 
	 * @see com.game.backpack.structs.Item#unuse(com.game.player.structs.Player, java.lang.String[])
	 */
	@Override
	public void unuse(Player player, String... parameters) {
		// TODO Auto-generated constructor stub

	}

	public static class PandoraItem {
		// 权重 物品id_强化等级_追加等级_卓越属性 叠加数量
		int rate;
		int itemModelId;
		int strengthLevel;
		int addAttriuteLevel;
		String zhuoyue;
		int num;
		boolean sure;//必出

		
		public int getItemModelId() {
			return itemModelId;
		}


		public void setItemModelId(int itemModelId) {
			this.itemModelId = itemModelId;
		}


		public int getStrengthLevel() {
			return strengthLevel;
		}


		public void setStrengthLevel(int strengthLevel) {
			this.strengthLevel = strengthLevel;
		}


		public int getAddAttriuteLevel() {
			return addAttriuteLevel;
		}


		public void setAddAttriuteLevel(int addAttriuteLevel) {
			this.addAttriuteLevel = addAttriuteLevel;
		}


		public String getZhuoyue() {
			return zhuoyue;
		}


		public void setZhuoyue(String zhuoyue) {
			this.zhuoyue = zhuoyue;
		}


		public int getNum() {
			return num;
		}


		public void setNum(int num) {
			this.num = num;
		}


		public boolean isSure() {
			return sure;
		}


		public void setSure(boolean sure) {
			this.sure = sure;
		}


		public String toString() {
			return "rate[" + rate + "],itemModelId[" + itemModelId + "]";
		}
	}

}
