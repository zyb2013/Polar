package com.game.gradegift.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.game.activities.script.AbstractActivityScript;
import com.game.player.structs.Player;
import com.game.script.manager.ScriptManager;
import com.game.script.structs.ScriptEnum;
import com.game.utils.ServerParamUtil;

/**
 * 等级礼包管理类
 * @author hongxiao.z
 * @date   2014-2-15  下午5:07:11
 */
public class GradeGiftManager 
{
	static Logger log = Logger.getLogger(GradeGiftManager.class);
	private GradeGiftManager(){}
	
	/**
	 * 礼包信息
	 * key 礼包子ID
	 * value 领取信息
	 */
	private static Map<Integer, Integer> records = null;
	
	//全局等级礼包信息存储索引
	public static String GRADE_GIFT = "GRADE_GIFT";
	
	@SuppressWarnings("unchecked")
	public static Integer getRecord(int giftId)
	{
		if(records == null)
		{
			String value = ServerParamUtil.getNormalParamMap().get(GRADE_GIFT);
			records  = JSON.parseObject(value, ConcurrentHashMap.class);
			
			if(records == null)	//加载不到
			{
				records = new ConcurrentHashMap<Integer, Integer>();
			}
		}
		
		return records.get(giftId);
	}
	/**
	 * 保存等级礼包记录信息
	 * @param zoneId
	 * @param record
	 * @create	hongxiao.z      2014-2-13 下午12:21:25
	 */
	public static void saveRecord(int giftId, Integer record)
	{
		records.put(giftId, record);
		
		//放入缓存更新队列
		ServerParamUtil.threadSaveNormal(GRADE_GIFT, JSON.toJSONString(records, SerializerFeature.WriteClassName));
		
		//立即更新到DB
//		ServerParamUtil.immediateSaveNormal(ZonesConstantConfig.PT_RECORD, JSON.toJSONString(towerRecord, SerializerFeature.WriteClassName));
	}
	
	/**
	 * 领取礼包
	 * @param player
	 * @param giftId
	 * @create	hongxiao.z      2014-2-15 下午5:28:51
	 */
	public static void gainReward(Player player, int giftId)
	{
		AbstractActivityScript script = (AbstractActivityScript)ScriptManager.getInstance().getScript(ScriptEnum.ACTIVITY_GRADE_GIFT);
		
		if(script != null)
		{
			if(giftId <= 0)
			{
				GradeGiftManager.sendGiftInfo(player);
			}
			else
			{
				script.initiative(player, AbstractActivityScript.GAIN_REWARD, giftId);
			}
		}
		
	}
	
	/**
	 * 上线推送礼包数量信息 
	 * @create	hongxiao.z      2014-2-15 下午5:29:49
	 */
	public static void sendGiftInfo(Player player)
	{
		AbstractActivityScript script = (AbstractActivityScript)ScriptManager.getInstance().getScript(ScriptEnum.ACTIVITY_GRADE_GIFT);
		
		if(script != null)
		{
			script.passivity(player, AbstractActivityScript.SEND_GRADE_GIFT_INFO);
		}
	}
}
