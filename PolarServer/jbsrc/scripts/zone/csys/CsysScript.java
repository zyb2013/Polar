package scripts.zone.csys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.game.buff.manager.BuffManager;
import com.game.count.structs.CountTypes;
import com.game.csys.manager.CsysManger;
import com.game.csys.structs.CsysSMS;
import com.game.data.bean.Q_characterBean;
import com.game.data.bean.Q_clone_activityBean;
import com.game.data.bean.Q_monsterBean;
import com.game.dblog.LogService;
import com.game.fight.structs.Fighter;
import com.game.languageres.manager.ResManager;
import com.game.liveness.manager.LivenessManager;
import com.game.manager.ManagerPool;
import com.game.map.script.IEnterMapScript;
import com.game.map.script.IQuitMapScript;
import com.game.map.structs.ChangeReason;
import com.game.map.structs.Map;
import com.game.monster.script.IMonsterAiScript;
import com.game.monster.script.IMonsterDieScript;
import com.game.monster.structs.Monster;
import com.game.monster.structs.MonsterState;
import com.game.player.manager.PlayerManager;
import com.game.player.message.ResScriptCommonPlayerToClientMessage;
import com.game.player.script.IPlayerDieScript;
import com.game.player.structs.AttributeChangeReason;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.server.config.MapConfig;
import com.game.server.impl.WServer;
import com.game.server.script.IServerEventTimerScript;
import com.game.skill.structs.Skill;
import com.game.structs.Grid;
import com.game.structs.Position;
import com.game.team.manager.TeamManager;
import com.game.util.TimerUtil;
import com.game.utils.Global;
import com.game.utils.MapUtils;
import com.game.utils.MessageUtil;
import com.game.utils.RandomUtils;
import com.game.utils.TimeUtil;
import com.game.zones.bean.ZoneApplyDataInfo;
import com.game.zones.log.ZoneLog;
import com.game.zones.manager.ZonesManager;
import com.game.zones.message.ResZoneApplyDataInfoToClientMessage;
import com.game.zones.message.ResZoneLifeTimeMessage;
import com.game.zones.message.ResZoneTeamOpenBullToClientMessage;
import com.game.zones.script.ICreateZoneScript;
import com.game.zones.script.IZoneEventTimerScript;
import com.game.zones.structs.ZoneContext;
import com.game.zones.timer.ZoneLoopEventTimer;

/**
 * 赤色要塞
 * 
 * @author wzh
 * 
 */
public class CsysScript implements ICreateZoneScript, IEnterMapScript,
		IPlayerDieScript, IMonsterDieScript {

	protected static Logger logx = Logger.getLogger(CsysScript.class);

	int ZONEID = 6001;

	int MAPID = 600001;

	@Override
	public int getId() {
		return 55012;
	}

	@Override
	public ZoneContext onCreate(Player player, int zoneId) {

		if (zoneId == ZONEID) {

			if (ManagerPool.csysManger.getCstsstate() != 0) {
				ZoneContext zone = CsysManger.getInstance().getZone(player);
				if (zone == null) {

					return null;
				}
				return zone;
			} else {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager
						.getInstance().getString("活动已经结束，请下次再来"));
				return null;
			}
		}
		return null;
	}

	@Override
	public void onMonsterDie(Monster monster, Fighter killer) {
		Map map = ManagerPool.mapManager.getMap(monster);
		if (map == null || map.getMapModelid() != MAPID) {
			return;
		}
		Player player = PlayerManager.getInstance().getPlayer(killer.getId());
		CsysManger.getInstance().changeCsysSMSTopData(player,
				CsysManger.KILLMONSTER, monster.getModelId(), null);

	}

	@Override
	public void onPlayerDie(Player player, Fighter killer) {
		Map map = ManagerPool.mapManager.getMap(player);
		if (map == null || map.getMapModelid() != MAPID) {
			return;
		}
		// 加积分
		if (killer instanceof Player) {
			CsysManger.getInstance().changeCsysSMSTopData(player,
					CsysManger.KILLPLAYER, 0, killer);
		}
		if (killer instanceof Monster) {
			CsysManger.getInstance().monsterKill(player);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEnterMap(Player player, Map map) {

		if (map == null || map.getMapModelid() != MAPID) {
			return;
		}
		LivenessManager.getInstance().expenseCSYX(player);
		TeamManager.getInstance().stLeavetheteam(player, player.getId(),
				(byte) 1);

		ZoneContext zone = ManagerPool.zonesManager.getContexts().get(
				map.getZoneId());

		if (CsysManger.getInstance().getPlayerMap().get(player.getId()) == null) {

			CsysManger.getInstance().getplayerTopData(player);
			//
			if (zone.getOthers().get("playerList") == null) {
				zone.getOthers().put("playerList", new ArrayList<Long>());
			}
			((List<Long>) (zone.getOthers().get("playerList"))).add(player
					.getId());
			CsysManger.getInstance().getPlayerMap().put(player.getId(), zone);
		}
		CsysManger.getInstance().sendCsystopinfo(player);

		CsysManger.getInstance().sendPlayerKillCount(player);

		if (CsysManger.getInstance().getCstsstate() == 2) {
			// 切换模式为强制PK
			ManagerPool.playerManager.changePkState(player, 2, 0);
		}
		if (CsysManger.getInstance().getCstsstate() != 2) {
			// 切换模式为和平模式
			ManagerPool.playerManager.changePkState(player, 0, 0);
		}

		ResZoneLifeTimeMessage timemsg = new ResZoneLifeTimeMessage();

		if ((int) zone.getOthers().get("zoneprocess") == 1) {
			timemsg.setSurplustime((Integer) zone.getOthers().get("time") + 10
					* 60 - (int) (System.currentTimeMillis() / 1000));
		} else {
			timemsg.setSurplustime((Integer) zone.getOthers().get("time") + 20
					* 60 - (int) (System.currentTimeMillis() / 1000));
		}

		BuffManager.getInstance().addBuff(player, player,
				Global.CSYS_PROTECT_FOR_KILLED, 0, 0, 0);

		timemsg.setZoneid(map.getZoneModelId());
		timemsg.setPlayerCount(1);
		timemsg.setZoneprocess((Integer) zone.getOthers().get("zoneprocess"));
		MessageUtil.tell_player_message(player, timemsg);

	}

}
