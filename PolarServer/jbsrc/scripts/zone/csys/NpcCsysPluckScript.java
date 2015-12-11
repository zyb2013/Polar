package scripts.zone.csys;

import org.apache.log4j.Logger;

import com.game.country.message.ResCountrySiegeUpYuxiToClientMessage;
import com.game.data.bean.Q_npcBean;
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

/**
 * 开始采集
 * 
 */
public class NpcCsysPluckScript implements INpcDefaultActionScript {

	protected Logger log = Logger.getLogger(NpcCsysPluckScript.class);

	public static int scriptId = 55013; // scriptid


	public int YuXiRange = 10;// 王座范围

	// public void NpcSiegeWarPluckScript(){
	// //必要的时候可在这里重载王座NCP
	// //ManagerPool.countryManager.setYuXiNpc(YuXiNpc);
	// }

	@Override
	public int getId() {
		return scriptId;
	}

	@Override
	public void defaultAction(Player player, NPC npc) {
		
		Q_npcBean npcdata = ManagerPool.dataManager.q_npcContainer.getMap().get(npc.getModelId());
		
		Position npcposition = new Position();
		npcposition.setX((short) (npcdata.getQ_x() * MapUtils.GRID_BORDER));
		npcposition.setY((short) (npcdata.getQ_y() * MapUtils.GRID_BORDER));
		double dis = MapUtils.countDistance(npcposition, player.getPosition()); // 得到距离
		if (dis > YuXiRange * MapUtils.GRID_BORDER) {
			MessageUtil.notify_player(player, Notifys.CUTOUT, ResManager.getInstance().getString("您离旗帜远了，无法取得。"));
			return;
		}
		ManagerPool.npcManager.playerGatherNoTask(player, npc);
		MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("开始采集。"));
	}

}
