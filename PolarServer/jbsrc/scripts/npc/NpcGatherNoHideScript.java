package scripts.npc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.game.backpack.manager.BackpackManager;
import com.game.backpack.structs.Item;
import com.game.config.Config;
import com.game.data.bean.Q_npcBean;
import com.game.data.bean.Q_task_mainBean;
import com.game.data.manager.DataManager;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.npc.script.INpcGatherActionScript;
import com.game.npc.struts.NPC;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.structs.Reasons;
import com.game.task.struts.MainTask;
import com.game.utils.CollectionUtil;
import com.game.utils.MessageUtil;
import com.game.utils.StringUtil;
import com.game.utils.Symbol;

/**
 * 采集后给予物品并完成任务
 * @author heyang
 *
 */
public class NpcGatherNoHideScript implements INpcGatherActionScript {
	
	protected Logger log = Logger.getLogger(NpcGatherNoHideScript.class);
	
	public static int scriptId = 5005;		//scriptid
	
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
		
		//根据npc判断采集物品
		String taskModelId = bean.getQ_acquisition_task();
		if(StringUtil.isNotBlank(taskModelId)){
			MainTask mainTask = null;
			for(MainTask task : player.getCurrentMainTasks()) {
				if(taskModelId.contains(String.valueOf(task.getModelid()))) {
					mainTask = task;
					break;
				}
			}
			if(mainTask == null) {
				return;
			}
			Q_task_mainBean taskModel= DataManager.getInstance().q_task_mainContainer.getMap().get(mainTask.getModelid());
			if(player.getLevel()<taskModel.getQ_accept_needmingrade()){
				//等级不足
				MessageUtil.notify_player(player, Notifys.ERROR,ResManager.getInstance().getString("采集需要的等级不足"));
				return;
			}
			//增加player物品
			List<Item> item = new ArrayList<Item>();
			Iterator<Entry<Integer, Integer>> iterator = gatherMap.entrySet().iterator();
			while(iterator.hasNext()) {
				Entry<Integer, Integer> next = iterator.next();
				List<Item> item2 = Item.createItems(next.getKey(), next.getValue(), true, 0);
				if(CollectionUtil.isNotBlank(item2))
					item.addAll(item2);
			}
			
			if(BackpackManager.getInstance().hasAddSpace(player, item)){
				log.error("玩家(" + player.getId() + ")完成任务(" + mainTask.getModelid() + ")检查物品有地儿放!");
			}else{
				log.error("玩家(" + player.getId() + ")完成任务(" + mainTask.getModelid() + ")检查物品没地儿放!");
			}				
			if(ManagerPool.backpackManager.addItems(player, item, Reasons.TAKEUP, Config.getId())){
				log.error("玩家(" + player.getId() + ")完成任务(" + mainTask.getModelid() + ")获得物品成功!");;
			}else{
				log.error("玩家(" + player.getId() + ")完成任务(" + mainTask.getModelid() + ")获得物品失败!");;
			}
			log.error("玩家(" + player.getId() + ")采集成功准备完成任务(" + mainTask.getModelid() + ")!");
			//触发完成任务
//			mainTask.changeTask();
			if(mainTask.checkFinsh(false, player)) {
				mainTask.finshTask(player);
				log.error("玩家(" + player.getId() + ")采集成功完成任务(" + taskModelId + ")了!");
			}else {

				log.error("玩家(" + player.getId() + ")采集成功未完成任务(" + taskModelId + ")了!");
			}
//			ManagerPool.taskManager.finishMainTask(player, taskModelId);
		}
		
		//player停止采集
		ManagerPool.npcManager.playerStopGather(player);
	}

}
