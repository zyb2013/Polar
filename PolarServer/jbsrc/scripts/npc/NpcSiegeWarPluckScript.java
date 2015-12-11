
package scripts.npc;

import org.apache.log4j.Logger;

import com.game.buff.structs.Buff;
import com.game.country.message.ResCountrySiegeUpYuxiToClientMessage;
import com.game.country.structs.CountryFightStatus;
import com.game.data.bean.Q_npcBean;
import com.game.guild.manager.GuildServerManager;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.npc.script.INpcDefaultActionScript;
import com.game.npc.struts.NPC;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.structs.Position;
//import com.game.utils.Global;
import com.game.utils.MapUtils;
import com.game.utils.MessageUtil;

/**王城争霸战
 * 点击NPC 开始采集王座
 *
 */
public class NpcSiegeWarPluckScript implements INpcDefaultActionScript {
	
	protected Logger log = Logger.getLogger(NpcSiegeWarPluckScript.class);
	
	public static int scriptId = 5007;		//scriptid
	
	//王座
	public int YuXiNpc = 105001;

	public int YuXiRange = 10 ;//王座范围
	
//	public void NpcSiegeWarPluckScript(){
//		//必要的时候可在这里重载王座NCP
//		//ManagerPool.countryManager.setYuXiNpc(YuXiNpc);
//	}
	
	
	@Override
	public int getId() {
		return scriptId;
	}

	@Override
	public void defaultAction(Player player, NPC npc){
//		if (ManagerPool.countryManager.getSiegestate() != 1) {
//			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("现在不是圣盟争夺战期间，不能对王座操作."));
//			return;
//		}
//	
//		
//		//采集王座不扣钱
////		int money = ManagerPool.countryManager.getYuXiGuildGold();
//		if (player.getGuildId() >0) {
//			if(player.getGuildId()==     ManagerPool.countryManager.getKingcity().getGuildid()){
//				MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("王座已经被您的战盟 占领！"));
//				return;
//			}
//			
//			
//			if (player.getMemberInfo().getGuildPowerLevel() == 1 || player.getMemberInfo().getGuildPowerLevel() == 2) {
////				if (player.getGuildInfo().getStockGold()  >= money) {
//					Q_npcBean npcdata = ManagerPool.dataManager.q_npcContainer.getMap().get(YuXiNpc);
//					Position npcposition = new Position();
//					npcposition.setX((short) (npcdata.getQ_x()*MapUtils.GRID_BORDER));
//					npcposition.setY((short) (npcdata.getQ_y()*MapUtils.GRID_BORDER));
//					double dis = MapUtils.countDistance(npcposition, player.getPosition());	//得到距离
//					if (dis > YuXiRange * MapUtils.GRID_BORDER) {
//						MessageUtil.notify_player(player, Notifys.CUTOUT, ResManager.getInstance().getString("您离王座远了，无法取得。"));
//						return;
//					}
//					
//					ManagerPool.npcManager.playerGather(player, npc);
//					if (System.currentTimeMillis()  - ManagerPool.countryManager.getYuxitime() > 15*1000) {
//						ManagerPool.countryManager.setYuxitime(System.currentTimeMillis());
//						ResCountrySiegeUpYuxiToClientMessage cmsg= new ResCountrySiegeUpYuxiToClientMessage();
//						cmsg.setPlayerid(player.getId());
//						cmsg.setPlayername(player.getName());
//						MessageUtil.tell_world_message(cmsg);
//					}
//					ManagerPool.buffManager.addBuff(player, player, 1114, 0, 0, 0);
////				}
////			else {
////					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("帮贡仓库金币不足{1}，无法拔起王座"),money+"");
////				}
//			}else {
//				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("只有盟主或者副盟主才能持有王座"));
//			}
//		}else {
//			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("只有盟主或者副盟主才能持有王座"));
//		}
		
		
		if(npc.getModelId()==CountryFightStatus.NPC1){
			if(player.getGroupmark()!=1){
				MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("只有防守方才能修复城墙！"));
				return;
			}
			MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("开始修复城墙"));
//			CountryFightStatus
		}
		if(npc.getModelId()==CountryFightStatus.NPC2){
//			if(player.getGroupmark()!=1){
//				MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("只有防守方才能修复城墙！"));
//				return;
//			}
			MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("开始修复城墙"));
		}
		if(npc.getModelId()==CountryFightStatus.NPC3){
			if(player.getGroupmark()!=1){
				MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("只有防守方才能修复城墙！"));
				return;
			}
			MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("开始修复城墙"));
		}
		if(npc.getModelId()==CountryFightStatus.NPCBUFF){
			
//			GuildServerManager.getInstance().getGuildTmpInfo(player.getGuildId()).getbangzhu(pos)
			
			
			if (player.getMemberInfo().getContributionPoint() < 20) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("帮贡不足{1}，无法装配炸药"),20+"");
				return;
			}
			
			if(player.getGroupmark()==1){
				MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("只有进攻方才能装配炸弹！"));
				return;
			}
			for(Buff buf : player.getBuffs()){
				if(buf.getModelId()==1146){
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("已经装配过炸药了"));
					return;
				}
				if(buf.getModelId()==1147){
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("已经装配过炸药了"));
					return;
				}
				if(buf.getModelId()==1148){
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("已经装配过炸药了"));
					return;
				}
				if(buf.getModelId()==1166){
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("已经装配过炸药了"));
					return;
				}
			}
			MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("开始装配炸药"));
//			CountryFightStatus
		}
		

		ManagerPool.npcManager.playerGatherNoTask(player, npc);
//		if (player.getGuildId() >0) {
//			
//		}
	}

}
