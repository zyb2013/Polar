package scripts.npc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.game.backpack.structs.Item;
import com.game.config.Config;
import com.game.data.bean.Q_npcBean;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.npc.script.INpcGatherActionScript;
import com.game.npc.struts.NPC;
import com.game.npc.timer.NpcEventTimer;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.structs.Reasons;
import com.game.util.TimerUtil;
import com.game.utils.CollectionUtil;
import com.game.utils.MessageUtil;
import com.game.utils.StringUtil;
import com.game.utils.Symbol;

/**
 * npc出现
 * @author heyang
 *
 */
public class NpcGatherScript implements INpcGatherActionScript {
	
	protected Logger log = Logger.getLogger(NpcGatherScript.class);
	
	public static int scriptId = 5002;		//scriptid

	private static int show_scriptId = 5001;		//scriptid
	
	private static long show_delay = 3000;		//重新显示事件
	
	@Override
	public int getId() {
		return scriptId;
	}

	@Override
	public void gather(Player player, NPC npc){
		//根据npc判断采集物品
		Q_npcBean bean = ManagerPool.dataManager.q_npcContainer.getMap().get(npc.getModelId());
		if(bean==null){
			return;
		}
		String itemModelIds = bean.getQ_acquisition_item();
		if(StringUtil.isBlank(itemModelIds)){
			//player停止采集
			ManagerPool.npcManager.playerStopGather(player);
			return;
		}
		Map<Integer,Integer> gatherMap = new HashMap<Integer,Integer>();
		String[] split = itemModelIds.split(Symbol.FENHAO_REG);
		for(String str:split) {
			String[] split2 = str.split(Symbol.XIAHUAXIAN_REG);
			int id = Integer.parseInt(split2[0]);
			int num = 1;
			if(split2.length >1)
				num = Integer.parseInt(split2[1]);
			String jobLimit = null;
			if(split2.length >2)
				jobLimit = split2[2];
			if(PlayerManager.checkJob(player.getJob(), jobLimit))
				gatherMap.put(id, num);
		}
		if(gatherMap.size() == 0) {
			log.error("没有物品可以采集了："+bean.getQ_id());
			ManagerPool.npcManager.playerStopGather(player);
			return;
		}
		//判断包裹是否能够放下此物品
		int num = ManagerPool.backpackManager.getEmptyGridNum(player);
		if(num < gatherMap.size()){
			//提示包裹空间不足
			MessageUtil.notify_player(player, Notifys.ERROR,ResManager.getInstance().getString("背包空间不足"));
			//player停止采集
			ManagerPool.npcManager.playerStopGather(player);
			return;
		}
//		if(player.getCurrentMainTasks().size()<0){
//			log.error("身上没有主线任务");
//			return;
//		}
//		
//		MainTask mainTask = player.getCurrentMainTasks().get(0);
//		Q_task_mainBean taskModel= DataManager.getInstance().q_task_mainContainer.getMap().get(mainTask.getModelid());
//		if(player.getLevel()<taskModel.getQ_accept_needmingrade()){
//			//等级不足
//			MessageUtil.notify_player(player, Notifys.ERROR,"采集需要的等级不足");
//			return;
//		}
		
		
		
		//增加player物品
		List<Item> item = new ArrayList<Item>();
		Iterator<Entry<Integer, Integer>> iterator = gatherMap.entrySet().iterator();
		while(iterator.hasNext()) {
			Entry<Integer, Integer> next = iterator.next();
			List<Item> item2 = Item.createItems(next.getKey(), next.getValue(), true, 0);
			if(CollectionUtil.isNotBlank(item2))
				item.addAll(item2);
		}
		ManagerPool.backpackManager.addItems(player, item, Reasons.TAKEUP, Config.getId());
		
		//player停止采集
		ManagerPool.npcManager.playerStopGather(player, true);
		
		//npc消失
		ManagerPool.npcManager.hideNpc(npc);
		
		//增加npc出现事件
		NpcEventTimer timer = new NpcEventTimer(npc, show_scriptId, null, show_delay);
		TimerUtil.addTimerEvent(timer);
	}

}
