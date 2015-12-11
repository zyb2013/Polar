package com.game.lostskills.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.game.backpack.manager.BackpackManager;
import com.game.chat.bean.GoodsInfoRes;
import com.game.config.Config;
import com.game.data.bean.Q_lost_skillBean;
import com.game.data.manager.DataManager;
import com.game.dblog.LogService;
import com.game.json.JSONserializable;
import com.game.languageres.manager.ResManager;
import com.game.lostskills.bean.LostSkillInfo;
import com.game.lostskills.log.LostSkillLog;
import com.game.lostskills.message.ResActivateLostSkillMessage;
import com.game.lostskills.message.ResLostSkillInfosMessage;
import com.game.player.manager.PlayerAttributeManager;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerAttribute;
import com.game.player.structs.PlayerAttributeType;
import com.game.prompt.structs.Notifys;
import com.game.structs.Reasons;
import com.game.utils.CommonString;
import com.game.utils.Global;
import com.game.utils.MessageUtil;
import com.game.utils.StringUtil;

/**
 * 遗落技能管理器
 * @author hongxiao.z
 * @date   2014-2-18  下午5:08:49
 */
public class LostSkillManager 
{
	private LostSkillManager(){}
	
	private static Logger logger = Logger.getLogger(LostSkillManager.class);
	private static Set<String> attrs = new HashSet<String>();
	
	//采集遗落技能系统所用到的属性类型
	static
	{
		attrs.add(CommonString.ATTACK);  //攻击
		attrs.add(CommonString.ATTACKPERCENT);   //攻击比例
		attrs.add(CommonString.DEFENSE);   ////防御
		attrs.add(CommonString.DEFENSEPERCENT);  //防御比例
		attrs.add(CommonString.CRIT);  //暴击
		attrs.add(CommonString.CRITPERCENT);  //暴击比例
		attrs.add(CommonString.DODGE);  //闪避
		attrs.add(CommonString.DODGEPERCENT); //闪避比例
		attrs.add(CommonString.MAXHP); //生命上限
		attrs.add(CommonString.MAXHPPERCENT); //生命上限比例
		attrs.add(CommonString.MAXMP); //魔法值上限
		attrs.add(CommonString.MAXMPPERCENT);  //魔法值上限比例
		attrs.add(CommonString.MAXSP);  //体力上限
		attrs.add(CommonString.MAXSPPERCENT);  //体力上限比例
		attrs.add(CommonString.ATTACKSPEED);  //攻击速度
		attrs.add(CommonString.ATTACKSPEEDPERCENT);  //攻击速度比例
		attrs.add(CommonString.SPEED);  //移动速度
		attrs.add(CommonString.SPEEDPERCENT); //移动速度比例
		attrs.add(CommonString.REMARKABLEPROB); //卓越一击几率
		attrs.add(CommonString.REMARKABLEPHARM); //卓越一击伤害比率
		attrs.add(CommonString.KNOWINGPROB); //会心一击几率
		attrs.add(CommonString.KNOWINGPHARM); //会心一击伤害比率
		attrs.add(CommonString.IGNOREPHARM); //无视一击伤害比率
		attrs.add(CommonString.IGNOREPROB); //无视一击几率
		attrs.add(CommonString.ICEPT); //冰抗
		attrs.add(CommonString.TOXINPT); //毒抗
		attrs.add(CommonString.THUNDERPT);	//雷抗
		attrs.add(CommonString.ATTCKPROB);	//攻击成功率
		attrs.add(CommonString.PTPROB);	//防御成功率
	}
	
	/**
	 * 升级技能
	 * @param player
	 * @param skillType
	 * @create	hongxiao.z      2014-2-18 下午5:09:44
	 */
	public static void updateSkill(Player player, int skillType)
	{
		//获取技能当前的等级
		int nowLv = getSkillLevel(player, skillType);
		
		//获取目标技能
		Q_lost_skillBean bean = DataManager.getInstance().q_lostskillContainer.getByTypeLv(skillType, nowLv + 1);
		
		if(bean == null)
		{
			logger.error("技能升级 --- 类型[" + skillType + "],等级[" + nowLv + "]已经达到最高顶级，或缺少下一级技能数据模型！");
			return;
		}
		
		//配置条件检测
		String info = check(player, bean);
		
		if(info != null)
		{
			MessageUtil.notify_player(player, Notifys.ERROR, info);
			return;
		}
		
		//扣除物品
		deduct(player, bean.getNeedItems());
		
		//保存技能等级
		player.getLostSkills().put(skillType + "", nowLv + 1);
//		ManagerPool.playerManager.updatePlayerSync(player);
		
		//! 增加技能学习提示
		if (nowLv == 0){
			MessageUtil.notify_player(player, Notifys.CHAT_IMPORTANT, ResManager
					.getInstance().getString("恭喜您学会了强大的法术{1}"), new ArrayList<GoodsInfoRes>(), 0, bean.getQ_skill_name());
			MessageUtil.notify_player(player, Notifys.CHAT_PERSONAL, ResManager
					.getInstance().getString("恭喜您学会了强大的法术{1}"), bean.getQ_skill_name());
		}else{
			MessageUtil.notify_player(player, Notifys.CHAT_IMPORTANT, ResManager
					.getInstance().getString("恭喜您将技能{1}提升至{2}级！"), new ArrayList<GoodsInfoRes>(), 0, bean.getQ_skill_name(), nowLv + 1 + "");
			MessageUtil.notify_player(player, Notifys.CHAT_PERSONAL, ResManager
					.getInstance().getString("恭喜您将技能{1}提升至{2}级！"), bean.getQ_skill_name(), nowLv + 1 + "");
		}
		
		//重算属性
		PlayerAttributeManager.getInstance().countPlayerAttribute(player, PlayerAttributeType.LOST_SKILL);
		
//		System.out.println("遗落技能升级 --- 技能类型[" + skillType + "],技能等级[" + (nowLv + 1) + "],加成的属性信息" + bean.getQ_add_attr());
		
		//日志记录
		try {
			LostSkillLog log = new LostSkillLog(player);
			log.setConsumeInfo(bean.getQ_need_info());
			log.setAddAttr(bean.getQ_add_attr());
			log.setSkillId(bean.getQ_skill_id());
			log.setSkillType(bean.getQ_skill_type());
			log.setSkillLevel(bean.getQ_skill_level());
			LogService.getInstance().execute(log);
		} catch (Exception e) {
			logger.error(e, e);
		}
		
		//发送消息通知前端
		ResActivateLostSkillMessage msg = new ResActivateLostSkillMessage();
		msg.setSkillId(bean.getQ_skill_id());
		msg.setSkillLv(bean.getQ_skill_level());
		msg.setSkillType(bean.getQ_skill_type());
		MessageUtil.tell_player_message(player, msg);
	}
	
	/**
	 * 获取角色遗落技能加成的属性列表
	 * @param player
	 * @return
	 * @create	hongxiao.z      2014-2-19 下午3:52:37
	 */
	public static PlayerAttribute getAttrs(Player player)
	{
		PlayerAttribute attr = new PlayerAttribute();
		
		for (Entry<String, Integer> entry : player.getLostSkills().entrySet()) 
		{
			int type = Integer.parseInt(entry.getKey());
			int level = entry.getValue();
			
			//根据技能等级获取原型数据
			Q_lost_skillBean bean = DataManager.getInstance().q_lostskillContainer.getByTypeLv(type, level);
			
			//获取本技能属性加成
			PlayerAttribute oneAttr = generateAttr(bean.getQ_add_attr());
			
			//合并总技能属性加成
			mergeAttr(attr, oneAttr);
		}
		
		attr.setKnowing_attackPercent(1.0 - attr.getKnowing_attackPercent());
		attr.setIgnore_attackPercent(1.0 - attr.getIgnore_attackPercent());
		
		return attr;
	}
	
	/**
	 * 生成属性类
	 * @param player
	 * @param attrInfo
	 * @return
	 * @create	hongxiao.z      2014-2-20 下午2:46:09
	 */
	@SuppressWarnings("unchecked")
	private static PlayerAttribute generateAttr(String attrInfo)
	{
		PlayerAttribute attr = new PlayerAttribute();
		
		//转换成属性集合
		HashMap<String, Integer> attHashMap = (HashMap<String, Integer>) JSONserializable.toObject(StringUtil.formatToJson(attrInfo), HashMap.class);
		
		//所有属性
		for (String str : attrs) 
		{
			//获取属性值
			Integer value = attHashMap.get(str);
			setAttr(str, value == null ? 0 : value, attr);
		}
		
		return attr;
	}
	
	/**
	 * 根据类型设置属性值
	 * @param att
	 * @param attStr
	 * @param attInt
	 * @create	hongxiao.z      2014-2-19 下午4:27:07
	 */
	private static void setAttr(String attStr, int attInt, PlayerAttribute att)
	{
		if (attStr.equalsIgnoreCase(CommonString.ATTACK)) //攻击
		{
			att.setPhysic_attacklower(attInt);
			att.setPhysic_attackupper(attInt);
			att.setMagic_attacklower(attInt);
			att.setMagic_attackupper(attInt);
		} 
		else if (attStr.equalsIgnoreCase(CommonString.ATTACKPERCENT)) //攻击比例
		{
			att.setPhysic_attackPercent(attInt);
			att.setMagic_attackPercent(attInt);
		} 
		else if (attStr.equalsIgnoreCase(CommonString.DEFENSE)) 	//防御
		{
			att.setDefense(attInt);
		} 
		else if (attStr.equalsIgnoreCase(CommonString.DEFENSEPERCENT)) //防御比例
		{
			att.setDefensePercent(attInt);
		} 
		else if (attStr.equalsIgnoreCase(CommonString.CRIT)) //暴击
		{
			att.setCrit(attInt);
		} 
		else if (attStr.equalsIgnoreCase(CommonString.CRITPERCENT)) //暴击比例
		{
			att.setCritPercent(attInt);
		} 
//		else if (attStr.equalsIgnoreCase(CommonString.DODGE)) //闪避
//		{
//			att.setDodge(attInt + att.getDodge());
//		} 
		else if (attStr.equalsIgnoreCase(CommonString.DODGEPERCENT)) //闪避比例
		{
			att.setDodgePercent(attInt + att.getDodgePercent());
		} 
		else if (attStr.equalsIgnoreCase(CommonString.MAXHP)) //生命上限
		{
			att.setMaxHp(attInt);
		} 
		else if (attStr.equalsIgnoreCase(CommonString.MAXHPPERCENT)) //生命上限比例
		{
			att.setMaxHpPercent(attInt);
		} 
		else if (attStr.equalsIgnoreCase(CommonString.MAXMP)) 	//魔法值上限
		{
			att.setMaxMp(attInt);
		} 
		else if (attStr.equalsIgnoreCase(CommonString.MAXMPPERCENT)) //魔法值上限比例
		{
			att.setMaxMpPercent(attInt);
		} 
		else if (attStr.equalsIgnoreCase(CommonString.MAXSP)) 	//体力上限
		{
			att.setMaxSp(attInt);
		} 
		else if (attStr.equalsIgnoreCase(CommonString.MAXSPPERCENT)) //体力上限比例
		{
			att.setMaxSpPercent(attInt);
		} 
		else if (attStr.equalsIgnoreCase(CommonString.ATTACKSPEED))	//攻击速度
		{
			att.setAttackSpeed(attInt);
		}
		else if (attStr.equalsIgnoreCase(CommonString.ATTACKSPEEDPERCENT)) //攻击速度比例
		{
			att.setAttackSpeedPercent(attInt);
		} 
		else if (attStr.equalsIgnoreCase(CommonString.SPEED)) 	//移动速度
		{
			att.setSpeed(attInt);
		} 
		else if (attStr.equalsIgnoreCase(CommonString.SPEEDPERCENT)) //移动速度比例
		{
			att.setSpeedPercent(attInt);
		}
		else if(attStr.equalsIgnoreCase(CommonString.REMARKABLEPROB)) //卓越一击几率
		{
			att.setPerfect_attack(attInt);
		}
		else if(attStr.equalsIgnoreCase(CommonString.REMARKABLEPHARM))	//卓越一击伤害比率
		{
			att.setPerfectatk_addpercent(attInt);
		}
		else if(attStr.equalsIgnoreCase(CommonString.KNOWINGPROB))	//会心一击几率
		{
			att.setKnowing_attackPercent(attInt * 1.0 / Global.MAX_PROBABILITY);
		}
		else if(attStr.equalsIgnoreCase(CommonString.KNOWINGPHARM))	//会心一击伤害比率
		{
			att.setKnowingatk_addpercent(attInt);
		}
		else if(attStr.equalsIgnoreCase(CommonString.IGNOREPHARM))	//无视一击伤害比率
		{
			att.setIgnore_defendPercent(attInt);
		}
		else if(attStr.equalsIgnoreCase(CommonString.IGNOREPROB))	//无视一击几率
		{
			att.setIgnore_attackPercent(attInt * 1.0 / Global.MAX_PROBABILITY);
		}
		else if(attStr.equalsIgnoreCase(CommonString.ICEPT))	//冰抗
		{
			att.setIce_def(attInt);
		}
		else if(attStr.equalsIgnoreCase(CommonString.THUNDERPT))	//雷抗
		{
			att.setRay_def(attInt);
		}
		else if(attStr.equalsIgnoreCase(CommonString.TOXINPT))	//毒抗
		{
			att.setPoison_def(attInt);
		}
		else if(attStr.equalsIgnoreCase(CommonString.ATTCKPROB))	//攻击成功率
		{
			att.setHit(attInt);
		}
		else if(attStr.equalsIgnoreCase(CommonString.PTPROB))	//防御成功率
		{
			att.setDodge(attInt);
		}

	}
	
	/**
	 * 合并两个属性实体
	 * @param attr1
	 * @param attr2
	 * @return
	 * @create	hongxiao.z      2014-2-20 下午2:31:13
	 */
	private static PlayerAttribute mergeAttr(PlayerAttribute attr1, PlayerAttribute attr2)
	{
		//攻击
		attr1.setPhysic_attacklower(attr2.getPhysic_attacklower() + attr1.getPhysic_attacklower());
		attr1.setPhysic_attackupper(attr2.getPhysic_attackupper() + attr1.getPhysic_attackupper());
		attr1.setMagic_attacklower(attr2.getMagic_attacklower() + attr1.getMagic_attacklower());
		attr1.setMagic_attackupper(attr2.getMagic_attackupper() + attr1.getMagic_attackupper());
		//攻击比例
		attr1.setPhysic_attackPercent(attr2.getPhysic_attackPercent() + attr1.getPhysic_attackPercent());
		attr1.setMagic_attackPercent(attr2.getMagic_attackPercent() + attr1.getMagic_attackPercent());
		//防御
		attr1.setDefense(attr2.getDefense() + attr1.getDefense());
		//防御比例
		attr1.setDefensePercent(attr2.getDefensePercent() + attr1.getDefensePercent());
		//暴击
		attr1.setCrit(attr2.getCrit() + attr1.getCrit());
		//暴击比例
		attr1.setCritPercent(attr2.getCritPercent() + attr1.getCritPercent());
		//闪避
//		attr1.setDodge(attr2.getDodge() + attr1.getDodge());
		//闪避比例
		attr1.setDodgePercent(attr2.getDodgePercent() + attr1.getDodgePercent());
		//生命上限
		attr1.setMaxHp(attr2.getMaxHp() + attr1.getMaxHp());
		//生命上限比例
		attr1.setMaxHpPercent(attr2.getMaxHpPercent() + attr1.getMaxHpPercent());
		//魔法值上限
		attr1.setMaxMp(attr2.getMaxMp() + attr1.getMaxMp());
		//魔法值上限比例
		attr1.setMaxMpPercent(attr2.getMaxMpPercent() + attr1.getMaxMpPercent());
		//体力上限
		attr1.setMaxSp(attr2.getMaxSp() + attr1.getMaxSp());
		//体力上限比例
		attr1.setMaxSpPercent(attr2.getMaxSpPercent() + attr1.getMaxSpPercent());
		//攻击速度
		attr1.setAttackSpeed(attr2.getAttackSpeed() + attr1.getAttackSpeed());
		//攻击速度比例
		attr1.setAttackSpeedPercent(attr2.getAttackSpeedPercent() + attr1.getAttackSpeedPercent());
		//移动速度
		attr1.setSpeed(attr2.getSpeed() + attr1.getSpeed());
		//移动速度比例
		attr1.setSpeedPercent(attr2.getSpeedPercent() + attr1.getSpeedPercent());
		//卓越一击几率
		attr1.setPerfect_attack(attr2.getPerfect_attack() + attr1.getPerfect_attack());
		//卓越一击伤害比率
		attr1.setPerfectatk_addpercent(attr2.getPerfectatk_addpercent() + attr1.getPerfectatk_addpercent());
//		会心一击,出现会心一击的几率=(1-(1-第一件几率)*(1-第二件几率))……
		//会心一击几率
//		double probKnowing = (1 - attr2.getKnowing_attackPercent()) * (1 - attr1.getKnowing_attackPercent());
		double probKnowing = (attr2.getKnowing_attackPercent() + attr1.getKnowing_attackPercent());
		attr1.setKnowing_attackPercent(probKnowing);
		//会心一击伤害比率
		attr1.setKnowingatk_addpercent(attr2.getKnowingatk_addpercent() + attr1.getKnowingatk_addpercent());
		//无视一击伤害比率
		attr1.setIgnore_defendPercent(attr2.getIgnore_defendPercent() + attr1.getIgnore_defendPercent());
		//无视一击几率
		// 无视一击,出现无视一击的几率=(1-(1-第一件几率)*(1-第二件几率))……
//		double probIgnore = (1 - attr2.getIgnore_attackPercent()) * (1 - attr1.getIgnore_attackPercent());
		double probIgnore = (attr2.getIgnore_attackPercent() + attr1.getIgnore_attackPercent());
		attr1.setIgnore_attackPercent(probIgnore);
		//冰抗
		attr1.setIce_def(attr2.getIce_def() + attr1.getIce_def());
		//毒抗
		attr1.setRay_def(attr2.getRay_def() + attr1.getRay_def());
		//雷抗
		attr1.setPoison_def(attr2.getPoison_def() + attr1.getPoison_def());
		//攻击成功率
		attr1.setHit(attr2.getHit() + attr1.getHit());
		//防御成功率
		attr1.setDodge(attr2.getDodge() + attr1.getDodge());
		
		return attr1;
	}
	
	/**
	 * 扣除物品 
	 * @create	hongxiao.z      2014-2-18 下午8:29:34
	 */
	private static void deduct(Player player, List<int[]> needItems)
	{
		for (int[] temp : needItems) 
		{
			int id  = temp[0];
			int num = temp[1]; 
			switch(id)
			{
				case -6:	//精魄
					BackpackManager.getInstance().changeSpirit(player, -num, Reasons.LOST_SKILL_STUDY, Config.getId());
					break;
				case -5:	//绑定钻石
					BackpackManager.getInstance().changeBindGold(player, -num, Reasons.LOST_SKILL_STUDY, Config.getId());
					break;
				case -4:	//暂缺
					break;
				case -3:	//暂缺
					break;
				case -2:	//钻石
					BackpackManager.getInstance().changeGold(player, -num, Reasons.LOST_SKILL_STUDY, Config.getId());
					break;
				case -1:
					BackpackManager.getInstance().changeMoney(player, -num, Reasons.LOST_SKILL_STUDY, Config.getId());
					break;
				default:
					BackpackManager.getInstance().removeItem(player, id, num, Reasons.LOST_SKILL_STUDY, Config.getId());
			}
		}
	}
	
	/**
	 * 技能学习的前置条件检查
	 * @param player
	 * @param needItems	-1 铜钱 -2 钻石 -3 真气 -4 经验 -5 绑定钻石 -6 精魄
	 * @return
	 * @create	hongxiao.z      2014-2-18 下午7:50:00
	 */
	private static String check(Player player, Q_lost_skillBean bean) 
	{
		//前置条件判断
		if(player.getLevel() < bean.getQ_need_level())
		{
			return "很抱歉，您的等级不足!";
		}
		
		List<int[]> needItems = bean.getNeedItems();
		for (int[] temp : needItems) 
		{
			int id  = temp[0];
			int num = temp[1]; 
			switch(id)
			{
				case -6:	//精魄
					if(player.getSpirit() < num) return "很抱歉，您的精魄不足！";
					break;
				case -5:	//绑定钻石
					if(player.getBindGold() < num) return "很抱歉，您的绑定钻石不足！";
					break;
				case -4:	//暂缺
					break;
				case -3:	//暂缺
					break;
				case -2:	//钻石
					int gold = player.getGold() == null ? 0 : player.getGold().getGold(); 
					if(gold < num) return "很抱歉，您的钻石不足！";
					break;
				case -1:	//铜钱
					if(player.getMoney() < num) return "很抱歉，您的铜钱不足！";
					break;
				default:	//道具
					int number = BackpackManager.getInstance().getItemNum(player, id);
					if(number < num) 
					{
						if(bean.getQ_skill_level() > 1)
						{
							return "很抱歉，您缺少升级该技能所需的道具！";
						}
						else
						{
							return "很抱歉，您缺少激活该技能所需的道具";
						}
					}
			}
		}
		
		return null;
	}

	/**
	 * 获取角色指定类型技能的等级
	 * @param player
	 * @param skillType
	 * @return
	 * @create	hongxiao.z      2014-2-18 下午7:13:00
	 */
	private static int getSkillLevel(Player player, int skillType)
	{
		Integer level = player.getLostSkills().get(skillType + "");
		
		return level == null ? 0 : level;
	}
	
	/**
	 * 推送所有技能信息
	 * @param player
	 * @create	hongxiao.z      2014-2-18 下午5:10:19
	 */
	public static void pushSkillInfos(Player player)
	{
		ResLostSkillInfosMessage msg = new ResLostSkillInfosMessage();
		
		for (Entry<String, Integer> entry : player.getLostSkills().entrySet()) 
		{
			int type = Integer.parseInt(entry.getKey());
			Q_lost_skillBean bean = DataManager.getInstance().q_lostskillContainer.getByTypeLv(type, entry.getValue());
			LostSkillInfo info = new LostSkillInfo();
			info.setSkillId(bean.getQ_skill_id());
			info.setSkillLv(bean.getQ_skill_level());
			info.setSkillType(bean.getQ_skill_type());
			msg.getSkillInfos().add(info);
		}
		
		MessageUtil.tell_player_message(player, msg);
	}

}
