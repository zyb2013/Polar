package scripts.map;

import org.apache.log4j.Logger;

import com.game.csys.manager.CsysManger;
import com.game.manager.ManagerPool;
import com.game.map.script.IEnterMapScript;
import com.game.map.structs.Map;
import com.game.player.structs.Player;
import com.game.script.structs.ScriptEnum;
import com.game.zones.manager.ZonesConstantConfig;

public class EnterMapScript implements IEnterMapScript {

	private Logger log = Logger.getLogger(EnterMapScript.class);
////	private static int jiaochang_scriptid = 4003;
//	public static int mizong_scriptId = 4006;		//迷踪幻境副本scriptid
//	public static int maze_scriptId = 4007;		//迷宫副本scriptid
//	public static int baguazheng_scriptId = 4008;				//八卦阵
//	public static int SheZhanQunRusSriptId = 4009;		//舌战群儒副本scriptid
//	public static int simen_scriptId = 4010;				//四门阵
//	public static int MeiHuaXuanWuSriptId = 4011;		//梅花玄武阵副本scriptid
//	public static int[] bow_scriptId = new int[]{4101, 4102, 4103, 4104, 4105, 4106, 4107};				//弓箭boss副本
//	public static int shuiyandaliangId = 4012;				//水淹大梁
//	public static int prisonmap = 42122;          //监狱地图
	
	public static int XSCB_SCRIPT = 55002;          //血色城堡 脚本
	
	public static int EMGC_SCRIPT = 55001;          //恶魔广场 脚本
	
	public static int CSYS_SCRIPT = 55012;          //赤色要塞 脚本
	
	public static int COUNTRY_SCRIPT = 55015;      //攻城战
	
	@Override
	public int getId() {
		return ScriptEnum.ENTER_MAP;
	}

	@Override
	public void onEnterMap(Player player, Map map) 
	{
		IEnterMapScript script = (IEnterMapScript) ManagerPool.scriptManager.getScript(ZonesConstantConfig.PT_SCRIPT_ID);
		if (script != null) 
		{
			try {
				script.onEnterMap(player, map);
			} catch (Exception e) {
				log.error(e, e);
			}
		}
		
		script = (IEnterMapScript) ManagerPool.scriptManager.getScript(XSCB_SCRIPT);
		if (script != null) 
		{
			try {
				script.onEnterMap(player, map);
			} catch (Exception e) {
				log.error(e, e);
			}
		}
		
		
		script = (IEnterMapScript) ManagerPool.scriptManager.getScript(CSYS_SCRIPT);
		if (script != null) 
		{
			try {
				script.onEnterMap(player, map);
			} catch (Exception e) {
				log.error(e, e);
			}
		}
		
		script = (IEnterMapScript) ManagerPool.scriptManager.getScript(COUNTRY_SCRIPT);
		if (script != null) 
		{
			try {
				script.onEnterMap(player, map);
			} catch (Exception e) {
				log.error(e, e);
			}
		}
		script = (IEnterMapScript) ManagerPool.scriptManager.getScript(EMGC_SCRIPT);
		if (script != null) 
		{
			try {
				script.onEnterMap(player, map);
			} catch (Exception e) {
				log.error(e, e);
			}
		}
		/*panic god暂时屏蔽
		IEnterMapScript script = (IEnterMapScript) ManagerPool.scriptManager.getScript(jiaochang_scriptid);
>>>>>>> .r926
		if (script != null) {
			try {
				script.onEnterMap(player, map);
			} catch (Exception e) {
				log.error(e, e);
			}
		}
		
		script = (IEnterMapScript) ManagerPool.scriptManager.getScript(jiaochang_scriptid);
		if (script != null) {
			try {
				script.onEnterMap(player, map);
			} catch (Exception e) {
				log.error(e, e);
			}
		}
		script = (IEnterMapScript) ManagerPool.scriptManager.getScript(mizong_scriptId);
		if (script != null) {
			try {
				script.onEnterMap(player, map);
			} catch (Exception e) {
				log.error(e, e);
			}
		}
		script = (IEnterMapScript) ManagerPool.scriptManager.getScript(maze_scriptId);
		if (script != null) {
			try {
				script.onEnterMap(player, map);
			} catch (Exception e) {
				log.error(e, e);
			}
		}

		script = (IEnterMapScript) ManagerPool.scriptManager.getScript(baguazheng_scriptId);
		if (script != null) {
			try {
				script.onEnterMap(player, map);
			} catch (Exception e) {
				log.error(e, e);
			}
		}

		script = (IEnterMapScript) ManagerPool.scriptManager.getScript(SheZhanQunRusSriptId);
		if (script != null) {
			try {
				script.onEnterMap(player, map);
			} catch (Exception e) {
				log.error(e, e);
			}
		}

		script = (IEnterMapScript) ManagerPool.scriptManager.getScript(simen_scriptId);
		if (script != null) {
			try {
				script.onEnterMap(player, map);
			} catch (Exception e) {
				log.error(e, e);
			}
		}
		
		script = (IEnterMapScript) ManagerPool.scriptManager.getScript(MeiHuaXuanWuSriptId);
		if (script != null) {
			try {
				script.onEnterMap(player, map);
			} catch (Exception e) {
				log.error(e, e);
			}
		}
		script = (IEnterMapScript) ManagerPool.scriptManager.getScript(shuiyandaliangId);
		if (script != null) {
			try {
				script.onEnterMap(player, map);
			} catch (Exception e) {
				log.error(e, e);
			}
		}
		script = (IEnterMapScript) ManagerPool.scriptManager.getScript(ScriptEnum.GUILDFLAG);
		if (script != null) {
			try {
				script.onEnterMap(player, map);
			} catch (Exception e) {
				log.error(e, e);
			}
		}
		
		script = (IEnterMapScript) ManagerPool.scriptManager.getScript(ScriptEnum.BIWUDAO);
		if (script != null) {
			try {
				script.onEnterMap(player, map);
			} catch (Exception e) {
				log.error(e, e);
			}
		}
		
		
		for (int i = 0; i < bow_scriptId.length; i++) {
			script = (IEnterMapScript) ManagerPool.scriptManager.getScript(bow_scriptId[i]);
			if (script != null) {
				try {
					script.onEnterMap(player, map);
				} catch (Exception e) {
					log.error(e, e);
				}
			}
		}

		script = (IEnterMapScript) ManagerPool.scriptManager.getScript(ScriptEnum.BF4V4);
		if (script != null) {
			try {
				script.onEnterMap(player, map);
			} catch (Exception e) {
				log.error(e, e);
			}
		}
		
		
		try {
			List<Q_task_mainBean> tasks = ManagerPool.dataManager.q_task_mainContainer.getList();
			Iterator<Q_task_mainBean> iter = tasks.iterator();
			while (iter.hasNext()) {
				Q_task_mainBean q_task_mainBean = (Q_task_mainBean) iter.next();
				if (q_task_mainBean.getQ_brush_mon_map() == map.getMapModelid()) {
					if (ManagerPool.taskManager.getMainTaskState(player, q_task_mainBean.getQ_taskid()) == 2) {
						List<Monster> monsters = ManagerPool.taskManager.getStoryMonster(player, q_task_mainBean.getQ_task_brush_monid());
						if (monsters.size() == 0) {
							String[] strs = q_task_mainBean.getQ_brush_mon_xy().split(Symbol.XIAHUAXIAN_REG);
							Grid grid = MapUtils.getGrid(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]), ManagerPool.mapManager.getMapBlocks(map.getMapModelid()));
							if (grid != null) {
								ManagerPool.monsterManager.createStoryMonsterAndEnterMap(player, q_task_mainBean.getQ_task_brush_monid(), WServer.getInstance().getServerId(), map.getLineId(), (int) map.getId(), grid.getCenter());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e, e);
		}
		
		try{
			Collect collect = player.getCollect();
			Iterator<Entry<String, CollectItem>>  iter = collect.getInfos().entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, CollectItem> entry = (Entry<String, CollectItem>) iter.next();
				if(!StringUtil.isNumeric(entry.getKey())){
					iter.remove();
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		//如果玩家进入监狱地图 则发送角色监狱剩余时间
		try{
			if(map.getMapModelid()==prisonmap){ 
				PlayerManager.getInstance().sendPlayerPrisonState(player);
			}
		}catch (Exception e) {
			log.error(e, e);
		}
		
		//移除玩家跳跃状态
		try{
			player.setJumpProtect(false);
		}catch (Exception e) {
			log.error(e, e);
		}
		*/
	}
}
