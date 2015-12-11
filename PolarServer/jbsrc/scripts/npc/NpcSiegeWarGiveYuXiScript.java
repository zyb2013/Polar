
package scripts.npc;

import org.apache.log4j.Logger;

import com.game.country.structs.CountryFightStatus;
import com.game.guild.manager.GuildServerManager;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.map.manager.MapManager;
import com.game.npc.script.INpcGatherActionScript;
import com.game.npc.struts.NPC;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.utils.Global;
import com.game.utils.MessageUtil;

/**王城争霸战
 * NPC玉玺采集完成
 *
 */
public class NpcSiegeWarGiveYuXiScript implements INpcGatherActionScript {
	
	protected Logger log = Logger.getLogger(NpcSiegeWarGiveYuXiScript.class);
	
	public static int scriptId = 5008;		//scriptid
	
	@Override
	public int getId() {
		return scriptId;
	}

	@Override
	public void gather(Player player, NPC npc){
//		if (ManagerPool.countryManager.getSiegestate() != 1) {
//			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("圣盟争夺战已结束，不能对王座操作."));
//			//player停止采集
//			ManagerPool.npcManager.playerStopGather(player, true);
//			return;
//		}
////		int money = ManagerPool.countryManager.getYuXiGuildGold();
//		if (player.getGuildId() >0) {
//			if (player.getMemberInfo().getGuildPowerLevel() == 1 || player.getMemberInfo().getGuildPowerLevel() == 2) {
////				if (player.getGuildInfo().getStockGold()  >= money) {
////					GuildServerManager.getInstance().reqInnerKingCityEventToWorld(player,1,money+"");	//扣钱
//					ManagerPool.countryManager.setYuxitime(0);//清除点击冷却
//					
//					ManagerPool.countryManager.SiegeGrabYuXi(player);
////					//npc消失
////					ManagerPool.npcManager.hideNpc(npc);
//					//player停止采集
//					ManagerPool.npcManager.playerStopGather(player, true);
//					
//					//采集成功   记录  夺得  王座的战盟
//					MapManager.getInstance().getMap(player).getParameters().put("currentWiner", player.getGuildId());
//					
//					
//					ManagerPool.buffManager.addBuff(player, player, Global.PROTECT_IN_BANGZHU, 0, 0, 0);
//				
//					return;
////				}else {
////					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("帮贡仓库金币不足{1}，无法拔起秦王玉玺"),money+"");
////				}
//			}else {
//				/* xuliang
//				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("只有盟主或者副盟主才能持有秦王玉玺"));
//				*/
//			}
//		}
		if(npc.getModelId()==CountryFightStatus.NPC1){
			
			if(ManagerPool.countryManager.getCm1Status()==0){
				ManagerPool.countryManager.creatMonster(0,1);
				ManagerPool.countryManager.setCm1Status(3);
				ManagerPool.countryManager.stcountryWarInfo(null, true);
				MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("修复城墙成功！"));
				
				MessageUtil.notify_map(ManagerPool.countryManager.getSiegeMap(), Notifys.CUTOUT, ResManager.getInstance().getString("{1}通过重重阻碍，修复了外墙城门，号称敌后特工队！"),player.getName());
				
			
				ManagerPool.npcManager.hideNpc(npc);
				ManagerPool.countryManager.opendoor1(0);
			}
//			CountryFightStatus
		}
		if(npc.getModelId()==CountryFightStatus.NPC2){
			
			if(ManagerPool.countryManager.getCm2Status()==0){
				ManagerPool.countryManager.creatMonster(0,2);
				ManagerPool.countryManager.setCm2Status(3);
				ManagerPool.countryManager.stcountryWarInfo(null, true);
				MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("修复城墙成功！"));
				MessageUtil.notify_map(ManagerPool.countryManager.getSiegeMap(), Notifys.CUTOUT, ResManager.getInstance().getString("{1}通过重重阻碍，修复了中墙城门，号称敌后特工队！"),player.getName());
				ManagerPool.npcManager.hideNpc(npc);
				ManagerPool.countryManager.opendoor2(0);
			}
		}
		if(npc.getModelId()==CountryFightStatus.NPC3){
			if(ManagerPool.countryManager.getCm3Status()==0){
				ManagerPool.countryManager.creatMonster(0,3);
				ManagerPool.countryManager.setCm3Status(3);
				ManagerPool.countryManager.stcountryWarInfo(null, true);
				MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("修复城墙成功！"));
				MessageUtil.notify_map(ManagerPool.countryManager.getSiegeMap(), Notifys.CUTOUT, ResManager.getInstance().getString("{1}通过重重阻碍，修复了内墙城门，号称敌后特工队！"),player.getName());
				ManagerPool.npcManager.hideNpc(npc);
				ManagerPool.countryManager.opendoor3(0);
			}
		}
		if(npc.getModelId()==CountryFightStatus.NPCBUFF){
			
			GuildServerManager.getInstance().reqInnerKingCityEventToWorld(player,3,20+"");
			long endtime = System.currentTimeMillis()/1000 - ManagerPool.countryManager.getKingcity().getHoldtime();
			
			
//			1146 变身1级
//			1147 变身2级
//			1148 变身3级
//			1166 变身4级（注意这个Id是1166 不是1149）

			if(endtime>=15*60){
				ManagerPool.buffManager.addBuff(player, player,
						1166, 1, 0,
						0);
			}else if(endtime>=10*60){
				ManagerPool.buffManager.addBuff(player, player,
						1148, 1, 0,
						0);
			}else if(endtime>=5*60){
				ManagerPool.buffManager.addBuff(player, player,
						1147, 1, 0,
						0);
			}else {
				ManagerPool.buffManager.addBuff(player, player,
						1146, 1, 0,
						0);
			}
			
			
			MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("装配炸药成功！"));
//			CountryFightStatus
		}
		//player停止采集
	
		ManagerPool.npcManager.playerStopGather(player, true);
	}
	
}
