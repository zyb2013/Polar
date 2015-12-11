package scripts.zone.csys;

import org.apache.log4j.Logger;

import com.game.csys.manager.CsysManger;
import com.game.data.manager.DataManager;
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

/**
 * 采集完成
 * 
 */
public class NpcCsysGiveScript implements INpcGatherActionScript {

	protected Logger log = Logger.getLogger(NpcCsysGiveScript.class);

	public static int scriptId = 55014; // scriptid

	@Override
	public int getId() {
		return scriptId;
	}

	@Override
	public void gather(Player player, NPC npc) {
		
		MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("采集完成。"));
		//玩家获得积分
		
		CsysManger.getInstance().changeCsysSMSTopData(player, CsysManger.COLLECT, npc.getModelId(), null);
		
		// //npc消失
		ManagerPool.npcManager.hideNpc(npc);
		
		int  retime = DataManager.getInstance().q_csysContainer.getMap().get(npc.getModelId()+"").getQ_retime();
		
		npc.getParameters().put("retime", System.currentTimeMillis()+(retime*1000));
		
		// player停止采集
		ManagerPool.npcManager.playerStopGather(player, true);
		
	}

}
