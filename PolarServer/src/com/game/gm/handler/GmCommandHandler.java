package com.game.gm.handler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.game.arrow.manager.ArrowManager;
import com.game.arrow.message.ResArrowInfoMessage;
import com.game.arrow.structs.ArrowData;
import com.game.arrow.structs.ArrowReasonsType;
import com.game.backpack.bean.ItemInfo;
import com.game.backpack.manager.BackpackManager;
import com.game.backpack.script.IItemScript;
import com.game.backpack.structs.Attribute;
import com.game.backpack.structs.Item;
import com.game.bank.manager.BankManager;
import com.game.bank.message.ReqQueryBankLogMessage;
import com.game.buff.structs.Buff;
import com.game.card.manager.CardManager;
import com.game.chat.bean.GoodsInfoRes;
import com.game.chat.manager.ChatManager;
import com.game.collect.struts.Collect;
import com.game.command.Handler;
import com.game.config.Config;
import com.game.count.manager.CountManager;
import com.game.count.manager.ServerCountManager;
import com.game.count.structs.CountTypes;
import com.game.country.manager.CountryAwardManager;
import com.game.country.message.ReqCountrysalaryToGameMessage;
import com.game.country.message.ResCountrySiegeSelectToGameMessage;
import com.game.country.structs.CountryData;
import com.game.data.bean.Q_backpack_gridBean;
import com.game.data.bean.Q_clone_activityBean;
import com.game.data.bean.Q_hiddenweapon_skillBean;
import com.game.data.bean.Q_petinfoBean;
import com.game.data.bean.Q_skill_modelBean;
import com.game.data.bean.Q_task_mainBean;
import com.game.data.manager.DataManager;
import com.game.data.reload.ReLoadManager;
import com.game.dazuo.manager.PlayerDaZuoManager;
import com.game.db.DBServer;
import com.game.db.bean.Role;
import com.game.db.dao.RoleDao;
import com.game.dblog.LogService;
import com.game.dianjiangchun.manager.DianjiangchunManager;
import com.game.enter.manager.EnterManager;
import com.game.epalace.structs.Epalace;
import com.game.equipstreng.manager.EquipAttributeAddManager;
import com.game.equipstreng.manager.EquipComposeManager;
import com.game.equipstreng.manager.EquipStrengManager;
import com.game.fight.manager.FightManager;
import com.game.gm.log.GmCommandLog;
import com.game.gm.message.GmCommandMessage;
import com.game.gm.message.GmCommandToGateMessage;
import com.game.gm.message.GmCommandToWorldMessage;
import com.game.gm.script.IGmCommandScript;
import com.game.gradegift.manager.GradeGiftManager;
import com.game.guild.manager.GuildServerManager;
import com.game.guild.message.ReqGuildCreateToServerMessage;
import com.game.hiddenweapon.manager.HiddenWeaponManager;
import com.game.hiddenweapon.message.ResHiddenWeaponLevelUpSkillMessage;
import com.game.hiddenweapon.structs.HiddenWeapon;
import com.game.json.JSONserializable;
import com.game.languageres.manager.ResManager;
import com.game.liveness.manager.LivenessManager;
import com.game.login.message.ReqLoginCharacterToGameMessage;
import com.game.longyuan.bean.LongYuanInfo;
import com.game.longyuan.message.ResLongYuanOpenMessage;
import com.game.longyuan.structs.LongYuanData;
import com.game.lostskills.manager.LostSkillManager;
import com.game.mail.manager.MailServerManager;
import com.game.manager.ManagerPool;
import com.game.map.manager.MapManager;
import com.game.map.message.ReqSelectLineMessage;
import com.game.map.message.ResMapBlocksMessage;
import com.game.map.message.ResRoundMonsterDisappearMessage;
import com.game.map.structs.Area;
import com.game.map.structs.Map;
import com.game.message.Message;
import com.game.monster.manager.MonsterManager;
import com.game.monster.structs.Monster;
import com.game.monster.structs.MonsterState;
import com.game.monster.structs.Morph;
import com.game.monster.timer.MonsterPositionTimer;
import com.game.offline.manager.OffLineManager;
import com.game.offline.timer.OfflineTimer;
import com.game.pet.manager.PetInfoManager;
import com.game.pet.manager.PetOptManager;
import com.game.pet.struts.Pet;
import com.game.player.manager.PlayerManager;
import com.game.player.message.ResPlayerNameInfoToClientMessage;
import com.game.player.structs.AttributeChangeReason;
import com.game.player.structs.GmState;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerAttributeType;
import com.game.prompt.message.AddToFavoriteMessage;
import com.game.prompt.structs.Notifys;
import com.game.sceneobj.manager.SceneobjManager;
import com.game.script.IScript;
import com.game.script.structs.ScriptEnum;
import com.game.server.config.MapConfig;
import com.game.server.impl.WServer;
import com.game.server.thread.SaveGoldExpendThread;
import com.game.signwage.manager.SignWageManager;
import com.game.skill.log.SkillLevelAction;
import com.game.skill.log.SkillLevelUpLog;
import com.game.skill.manager.SkillManager;
import com.game.skill.structs.Skill;
import com.game.spirittree.message.ReqContinuousRipeningToGameMessage;
import com.game.stalls.message.ReqChangeStallsNameMessage;
import com.game.stalls.message.ReqStallsAdjustPricesMessage;
import com.game.stalls.message.ReqStallsBuyMessage;
import com.game.stalls.message.ReqStallsLooklogMessage;
import com.game.stalls.message.ReqStallsOffShelfMessage;
import com.game.stalls.message.ReqStallsOpenUpMessage;
import com.game.stalls.message.ReqStallsPlayerIdLookMessage;
import com.game.stalls.message.ReqStallsProductWasAddedMessage;
import com.game.stalls.message.ReqStallsRatingMessage;
import com.game.stalls.message.ReqStallsSearchMessage;
import com.game.stalls.message.ReqStallsSortMessage;
import com.game.store.manager.StoreManager;
import com.game.structs.Grid;
import com.game.structs.Position;
import com.game.structs.Reasons;
import com.game.summonpet.manager.SummonPetInfoManager;
import com.game.summonpet.manager.SummonPetOptManager;
import com.game.summonpet.struts.SummonPet;
import com.game.systemgrant.manager.SystemgrantManager;
import com.game.task.manager.TaskConfig;
import com.game.task.manager.TaskManager;
import com.game.task.message.ResGetGuidesMessage;
import com.game.task.struts.ConquerTask;
import com.game.task.struts.DailyTask;
import com.game.task.struts.MainTask;
import com.game.team.message.ReqApplyGameMessage;
import com.game.team.message.ReqApplyGameSelectMessage;
import com.game.team.message.ReqAppointGameMessage;
import com.game.team.message.ReqAppointGameSelectMessage;
import com.game.team.message.ReqAutoIntoTeamApplyGameMessage;
import com.game.team.message.ReqAutoTeaminvitedGameMessage;
import com.game.team.message.ReqCreateateamGameMessage;
import com.game.team.message.ReqInviteGameMessage;
import com.game.team.message.ReqInviteGameSelectMessage;
import com.game.team.message.ReqMapSearchPlayerInfoGameMessage;
import com.game.team.message.ReqMapSearchTeamInfoGameMessage;
import com.game.team.message.ReqToleaveGameMessage;
import com.game.team.message.ReqUpdateTeaminfoGameMessage;
import com.game.team.message.TeambroadcastMessage;
import com.game.transactions.message.ReqAutorefusaldealMessage;
import com.game.transactions.message.ReqTransactionsAcceptMessage;
import com.game.transactions.message.ReqTransactionsCanceledMessage;
import com.game.transactions.message.ReqTransactionsChangeGoldMessage;
import com.game.transactions.message.ReqTransactionsChangeYuanbaoMessage;
import com.game.transactions.message.ReqTransactionsIntoItemMessage;
import com.game.transactions.message.ReqTransactionsLaunchMessage;
import com.game.transactions.message.ReqTransactionsRefuseMessage;
import com.game.transactions.message.ReqTransactionsRemoveItemMessage;
import com.game.transactions.message.ReqTransactionsSetStateMessage;
import com.game.utils.Global;
import com.game.utils.MapUtils;
import com.game.utils.MessageUtil;
import com.game.utils.NpcParamUtil;
import com.game.utils.RandomUtils;
import com.game.utils.ScriptsUtils;
import com.game.utils.ServerParamUtil;
import com.game.utils.StringUtil;
import com.game.utils.Symbol;
import com.game.utils.TimeUtil;
import com.game.utils.VersionUpdateUtil;
import com.game.vip.manager.VipManager;
import com.game.ybcard.message.ReqYBCardToWorldMessage;
import com.game.zones.manager.ZonesManager;
import com.game.zones.manager.ZonesTeamManager;
import com.game.zones.message.ReqAddBuffMessage;
import com.game.zones.message.ReqInventedZoneMessage;
import com.game.zones.message.ReqSelectAwardMessage;
import com.game.zones.message.ReqZoneIntoMessage;
import com.game.zones.message.ReqZoneOutMessage;
import com.game.zones.message.ReqZoneReceiveawardsMessage;
import com.game.zones.message.ReqZoneTeamEnterToGameMessage;
import com.game.zones.message.ReqZoneTeamSelectToGameMessage;
import com.game.zones.message.ResZoneTeamOpenBullToClientMessage;
import com.game.zones.structs.ZoneContext;

public class GmCommandHandler extends Handler {

	protected static Logger log = Logger.getLogger(GmCommandHandler.class);

	public void action() {
		try {
			GmCommandMessage msg = (GmCommandMessage) this.getMessage();
			Player player = (Player) this.getParameter();
			// 分割指令
			String[] strs = msg.getCommand().split(" ");
			if (msg.getCommand() == null || ("").equals(msg.getCommand())) {
				return;
			}
			
			int gmlevel = PlayerManager.getInstance().getPlayerGmlevel(player);
			// panic god 暂时屏蔽GM检查
//			if(gmlevel <= 0) return; //权限<0 返回
//			if(gmlevel > 0){ player.setGmState(GmState.GM.getValue()); }
//			if(!commandlevel.containsKey(strs[0].toLowerCase()) &&
//			gmlevel==1){ return;}//如果无此GM命令 或 GM对该指令的权限不够 返回
//			log.info(player.getGmState() + "\t" + player.getUserId() + "\t" +
//			player.getName() + "\t" + player.getLoginTime() + "\t" +
//			TimeUtil.getNowStringDate() + "\t" + msg.getCommand());
			
			try {
				GmCommandLog log = new GmCommandLog(player);
				log.setCommand(msg.getCommand());
				log.setType(1);
				LogService.getInstance().execute(log);
			} catch (Exception e) {
				log.error(e, e);
			}
			if ("&GGG".equalsIgnoreCase(strs[0])) {
				
				ManagerPool.mapManager.enterMap((Player)this.getParameter(), 1000, 1900);
			}
			if ("&G2".equalsIgnoreCase(strs[0])) {
				
				ReqLoginCharacterToGameMessage cmsg = new ReqLoginCharacterToGameMessage();
				
				
				cmsg.setPlayerId(player.getId());
				cmsg.setUserId(player.getUserId());
				cmsg.setGateId(player.getGateId());
				cmsg.setServerId(10000);
				cmsg.setLoginIp(player.getLoginIp());
				cmsg.setUserName(player.getName());
				
				WServer.getInstance().getPublicSession().write(cmsg);
				
				
			}
		
			//
			if ("&reload".equalsIgnoreCase(strs[0])) {
				ReLoadManager.getInstance().reLoad(strs[1], player.getId());
				GmCommandToWorldMessage wmsg = new GmCommandToWorldMessage();
				wmsg.setPlayerId(player.getId());
				wmsg.setCommand(msg.getCommand());
				MessageUtil.send_to_world(wmsg);
			} else if ("&allreload".equalsIgnoreCase(strs[0])) {
				ManagerPool.versionManager.versionUpdateToWorld(player);
			} else if ("&upJob".equalsIgnoreCase(strs[0])) {
				PlayerManager.getInstance().changeJob(player,player.getJob()<=3?TaskConfig.CHANGE_JOB_FINISH_TASK1_1:TaskConfig.CHANGE_JOB_FINISH_TASK2_1);
			} else if ("&EXP".equalsIgnoreCase(strs[0])) {
				PlayerManager.getInstance().addExp(player,
						Long.parseLong(strs[1].trim()),
						AttributeChangeReason.GM);
			} else if ("&msg".equalsIgnoreCase(strs[0])) {
				ChatManager.getInstance().chat(player, "",
						Integer.parseInt(strs[1]), null, strs[2]);
			} else if ("&ADDITEM".equals(strs[0].toUpperCase())) {
				addItem(player, strs);

			} else if ("&ADDITEM2".equals(strs[0].toUpperCase())) {
				addItem2(player, strs);
			} else if ("&ADDTASKITEM".equals(strs[0].toUpperCase())) {
				int modelid = Integer.parseInt(strs[1]);
				int num = Integer.parseInt(strs[2]);
				List<Item> createItems = Item.createItems(modelid, num, false,
						0, 0, "");
				BackpackManager.getInstance().addAbleReceieve(player,
						createItems);
				// } else
				// if("&TESTNEEDCELL".equalsIgnoreCase(strs[0].toUpperCase())){
				// //临时测试用GM命令
				// int index = Integer.valueOf(strs[1]);
				// BackpackManager.getInstance().TestNeedEmptyCellNum(player,
				// index);
			} else if ("&GOLD".equals(strs[0].toUpperCase())) {
				gold(player, strs);
			} else if ("&MONEY".equals(strs[0].toUpperCase())) {
				money(player, strs);
			} else if ("&BINDGOLD".equals(strs[0].toUpperCase())) {
				bindGold(player, strs);
			} else if ("&CLEAN".equals(strs[0].toUpperCase())) {
				WServer.delay.clear();
			} else if ("&HP".equalsIgnoreCase(strs[0])) {
				if (strs.length > 1) {
					player.setHp(Math.min(Integer.parseInt(strs[1]), player.getMaxHp()));
				} else {
					player.setHp(player.getMaxHp());
				}
				ManagerPool.playerManager.onHpChange(player);
			} else if ("&MP".equalsIgnoreCase(strs[0])) {
				if (strs.length > 1) {
					player.setMp(Integer.parseInt(strs[1]));
				} else {
					player.setMp(player.getMaxMp());
				}
				ManagerPool.playerManager.onMpChange(player);
			} else if ("&SP".equalsIgnoreCase(strs[0])) {
				if (strs.length > 1) {
					player.setSp(Integer.parseInt(strs[1]));
				} else {
					player.setSp(player.getMaxSp());
				}
				ManagerPool.playerManager.onSpChange(player);
			} else if ("&ATTONE".equalsIgnoreCase(strs[0])) { // 设置力量/体力/敏捷/智力
				if (strs.length > 2) {
					int i = Integer.parseInt(strs[1]);
					int num = Integer.parseInt(strs[2]);
					player.getAttibute_one_base()[i - 1] = num;
					ManagerPool.playerAttributeManager.countPlayerAttribute(
							player, PlayerAttributeType.BASE);
				}
			}else if ("&ice_attack".equalsIgnoreCase(strs[0])) {// 冰攻
				player.getOtherAttribute().setIce_attack(
						Integer.parseInt(strs[1]));
				ManagerPool.playerAttributeManager.countPlayerAttribute(player,
						PlayerAttributeType.OTHER);
			} else if ("&ray_attack".equalsIgnoreCase(strs[0])) {// 雷攻
				player.getOtherAttribute().setRay_attack(
						Integer.parseInt(strs[1]));
				ManagerPool.playerAttributeManager.countPlayerAttribute(player,
						PlayerAttributeType.OTHER);
			} else if ("&poison_attack".equalsIgnoreCase(strs[0])) {// 毒攻
				player.getOtherAttribute().setPoison_attack(
						Integer.parseInt(strs[1]));
				ManagerPool.playerAttributeManager.countPlayerAttribute(player,
						PlayerAttributeType.OTHER);
			} else if ("&ice_def".equalsIgnoreCase(strs[0])) {// 冰防
				player.getOtherAttribute()
						.setIce_def(Integer.parseInt(strs[1]));
				ManagerPool.playerAttributeManager.countPlayerAttribute(player,
						PlayerAttributeType.OTHER);
			} else if ("&ray_def".equalsIgnoreCase(strs[0])) {// 雷防
				player.getOtherAttribute()
						.setRay_def(Integer.parseInt(strs[1]));
				ManagerPool.playerAttributeManager.countPlayerAttribute(player,
						PlayerAttributeType.OTHER);
			} else if ("&poison_def".equalsIgnoreCase(strs[0])) {// 毒防
				player.getOtherAttribute().setPoison_def(
						Integer.parseInt(strs[1]));
				ManagerPool.playerAttributeManager.countPlayerAttribute(player,
						PlayerAttributeType.OTHER);
			} else if ("&attack".equalsIgnoreCase(strs[0])) {
				// 修改当前角色攻击力
				player.getOtherAttribute().setAttack(Integer.parseInt(strs[1]));
				ManagerPool.playerAttributeManager.countPlayerAttribute(player,
						PlayerAttributeType.OTHER);
			} else if ("&defence".equalsIgnoreCase(strs[0])) {
				// 修改当前角色防御力
				player.getOtherAttribute()
						.setDefense(Integer.parseInt(strs[1]));
				ManagerPool.playerAttributeManager.countPlayerAttribute(player,
						PlayerAttributeType.OTHER);
			} else if ("&baoji".equalsIgnoreCase(strs[0])) {
				// 修改当前角色暴击值
				player.getOtherAttribute().setCrit(Integer.parseInt(strs[1]));
				ManagerPool.playerAttributeManager.countPlayerAttribute(player,
						PlayerAttributeType.OTHER);
			} else if ("&shanbi".equalsIgnoreCase(strs[0])) {
				// 修改当前角色闪避值
				player.getOtherAttribute().setDodge(Integer.parseInt(strs[1]));
				ManagerPool.playerAttributeManager.countPlayerAttribute(player,
						PlayerAttributeType.OTHER);
			} else if ("&attackspeed".equalsIgnoreCase(strs[0])) {
				// 修改当前角色攻击速度
				player.getOtherAttribute().setAttackSpeed(
						Integer.parseInt(strs[1]));
				ManagerPool.playerAttributeManager.countPlayerAttribute(player,
						PlayerAttributeType.OTHER);
			} else if ("&movespeed".equalsIgnoreCase(strs[0])) {
				// 修改当前角色移动速度
				player.getOtherAttribute().setSpeed(Integer.parseInt(strs[1]));
				ManagerPool.playerAttributeManager.countPlayerAttribute(player,
						PlayerAttributeType.OTHER);
			} else if ("&attackspeed".equalsIgnoreCase(strs[0])) {
				player.getOtherAttribute().setAttackSpeed(
						Integer.parseInt(strs[1]));
				ManagerPool.playerAttributeManager.countPlayerAttribute(player,
						PlayerAttributeType.OTHER);
			} else if ("&loaddata".equalsIgnoreCase(strs[0])) {
				
				String dataString = ServerParamUtil.getImportantParamMap()
						.get(ServerParamUtil.COUNTRYDATA );
				CountryData jskingcity = JSON.parseObject(dataString,
						CountryData.class);
				
			}else if ("&luck".equalsIgnoreCase(strs[0])) {
				// 幸运值
				player.getOtherAttribute().setLuck(Integer.parseInt(strs[1]));
				ManagerPool.playerAttributeManager.countPlayerAttribute(player,
						PlayerAttributeType.OTHER);
			} else if ("&BAGCELLTIME".equalsIgnoreCase(strs[0])) {
				bagCelltime(player, strs);
			} else if ("&STORECELLTIME".equalsIgnoreCase(strs[0])) {
				storeCelltime(player, strs);
			} else if ("&BAGCLEAR".equalsIgnoreCase(strs[0])) {
				BackpackManager.getInstance().clear(player);
			} else if ("&KILL".equalsIgnoreCase(strs[0])) {
				killAmonster(player, strs);
			} else if ("&KILLALL".equalsIgnoreCase(strs[0])) {
				
				Map map = ManagerPool.mapManager.getMap(player);
				Iterator<Monster> monsters =    map.getMonsters().values().iterator();
				
				ResRoundMonsterDisappearMessage hidemsg = new ResRoundMonsterDisappearMessage();
				while (monsters.hasNext()) {
					Monster monster = monsters.next();
					if(monster.getModelId()!=130023007){
					monster.setState(MonsterState.DIE);
					hidemsg.getMonstersIds().add(monster.getId());
					}
				}
				for (Area area : map.getAreas().values()){
					area.getMonsters().clear();
				}
				map.getMonsters().clear();
				MessageUtil.tell_round_message(player, hidemsg);
			}
			else if ("&LEVEL".equalsIgnoreCase(strs[0])) {
				int level = Integer.parseInt(strs[1]);
				if(level <= Global.MAX_LEVEL && level>0) {
					ManagerPool.playerManager.setLevel(player,level, true, Reasons.LEVELUPGM);
				}
			} else if ("&ZHENQI".equalsIgnoreCase(strs[0])) {
				ManagerPool.playerManager.addZhenqi(player,
						Integer.parseInt(strs[1]), AttributeChangeReason.GM);
			} else if ("&NEWNAME".equalsIgnoreCase(strs[0])) { // 改名
				Player xplayer = ManagerPool.playerManager.getOnLinePlayer(Long
						.valueOf(strs[1]));
				if (xplayer != null) {
					ManagerPool.playerManager.testChangePlayerName(xplayer,
							strs[2]);
				} else {
					MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM,
							"您要操作的玩家不在线");
				}
			} else if ("&TECH".equalsIgnoreCase(strs[0])) { //修改玩家的工艺度 
				ManagerPool.castingManager.setPlayerTechnologyPoint(player, Integer.parseInt(strs[1]));
			} else if ("&SKILL".equalsIgnoreCase(strs[0])) {
				int nowLearnSkillId = player.getNowLearnSkillId();
				if (nowLearnSkillId == -1) {
					return;
				}
				Skill skillById = ManagerPool.skillManager.getSkillByModelId(
						player, nowLearnSkillId);
				if (skillById != null) {
					ManagerPool.skillManager.endUpLevel(player, skillById,
							skillById.getSkillLevel() + 1, true);
					try {
						SkillLevelUpLog log = new SkillLevelUpLog();
						log.setAction(SkillLevelAction.ENDLEVELUP.toString());
						log.setBeforeTime(0);
						log.setResumegold(0);
						log.setLevel(skillById.getSkillLevel());
						log.setSkillId(skillById.getSkillModelId());
						log.setBeforelevel(0);
						log.setResult(1);
						log.setRoleId(player.getId());
						log.setSid(player.getServerId());
						LogService.getInstance().execute(log);
					} catch (Exception e) {
						log.error(e, e);
					}
				}
			} else if ("&SKILLSTUDY".equalsIgnoreCase(strs[0])
					|| "&LAS".equalsIgnoreCase(strs[0])) {
				if (strs.length > 1 && strs[1] != null && strs[1].length() > 0) {
					int skillmodelId = Integer.valueOf(strs[1]);
					if (skillmodelId > 0) {
						ManagerPool.skillManager.addSkill(player, skillmodelId);
					}
				} else {
					for (int i = 10001; i < 10030; i++) {
						Q_skill_modelBean model = DataManager.getInstance().q_skill_modelContainer
								.getMap().get(i + "_" + 1);
						if (model != null) {
							ManagerPool.skillManager.addSkill(player, i);
						}
					}
				}
			} else if ("&LASS".equalsIgnoreCase(strs[0])) {
				int level = Integer.parseInt(strs[1]);
				List<Skill> skills = player.getSkills();
				for (Skill skill : skills) {
					Q_skill_modelBean model = DataManager.getInstance().q_skill_modelContainer
							.getMap().get(
									skill.getSkillModelId() + "_"
											+ skill.getSkillLevel());
					if (model != null) {
						if (model.getQ_max_level() == 0) {
							continue;
						}
						if (level > model.getQ_max_level()) {
							SkillManager.getInstance().endUpLevel(player,skill, model.getQ_max_level(), true);
						} else {
							SkillManager.getInstance().endUpLevel(player,skill, level, true);
						}
					}
				}
			} else if ("&DEBUGPOS".equalsIgnoreCase(strs[0])) {
				if (strs.length > 1) {
					PlayerManager.getSyncPosition().remove(player.getId());
				} else {
					PlayerManager.getSyncPosition().add(player.getId());
				}
			} else if ("&SHOWATTACKAREA".equalsIgnoreCase(strs[0])) {
				if (strs.length > 1) {
					FightManager.getSyncArea().remove(player.getId());
				} else {
					FightManager.getSyncArea().add(player.getId());
				}
			} else if ("&SKILLUP".equalsIgnoreCase(strs[0])) {
				int skillId = Integer.parseInt(strs[1]);
				int level = Integer.parseInt(strs[2]);
				Skill skillById = ManagerPool.skillManager.getSkillByModelId(
						player, skillId);
				if (skillById != null) {
					ManagerPool.skillManager.endUpLevel(player, skillById,
							level, true);
				}
			} else if ("&HORSE".equalsIgnoreCase(strs[0])) {
				ManagerPool.horseManager.testHorse(player,
						Integer.valueOf(strs[1]));
				ManagerPool.playerAttributeManager.countPlayerAttribute(player, PlayerAttributeType.HORSE);
			} else if ("&addzqexp".equalsIgnoreCase(strs[0])){
				ManagerPool.horseManager.addHorseExp(player, Integer.parseInt(strs[1]));
				
			}else if ("&diaoxiang".equalsIgnoreCase(strs[0])) {

				if (strs[1].equals("1")) {
					ManagerPool.countryManager.setdiaoxiang(true);
				} else if (strs[1].equals("2")) {
					ManagerPool.countryManager.setdiaoxiang(false);
				}

			} else if ("&HSKILL".equalsIgnoreCase(strs[0])) {
				ManagerPool.horseManager.testHorseskill(player,
						Integer.valueOf(strs[1]), Integer.valueOf(strs[2]));
			} else if ("&showreceiveretbindgold".equalsIgnoreCase(strs[0])) {// 测试聚宝盆面板
				ScriptsUtils.call(105, "showreceiveretbindgold", player);
			} else if ("&SD".equalsIgnoreCase(strs[0])) { // 设置开服日期
				WServer.getGameConfig()
						.stGmServerTime(player, strs[1], strs[2]);
			} else if ("&DATE".equalsIgnoreCase(strs[0])) { // 获取服务器相关时间
				int num = TimeUtil.getOpenAreaDay(player);
				Date optime = WServer.getGameConfig().getServerTimeByServer(
						WServer.getInstance().getServerId());
				Date date = new Date();
				@SuppressWarnings("deprecation")
				String datestr = date.toLocaleString();
				@SuppressWarnings("deprecation")
				String optimestr = optime.toLocaleString();
				MessageUtil.notify_player(player, Notifys.SUCCESS, "当前开服天数："
						+ num + "天,开服时间" + optimestr + ",当前系统时间:" + datestr
						+ ",当前系统毫秒数:" + System.currentTimeMillis());

			} else if ("&LY".equalsIgnoreCase(strs[0])) {

				Skill skill = ManagerPool.skillManager.getSkillByModelId(
						player, 30001);
				if (skill != null) {
					ManagerPool.skillManager.setLevel(player, skill, 3, true);
				} else {
					ManagerPool.skillManager.addSkill(player, 30001);
				}
				Skill skillx = ManagerPool.skillManager.getSkillByModelId(
						player, 30002);
				if (skillx != null) {
					ManagerPool.skillManager.setLevel(player, skillx, 3, true);
				} else {
					ManagerPool.skillManager.addSkill(player, 30002);
				}
				LongYuanData longyuan = player.getLongyuan();
				longyuan.setLylevel((byte) 15);
				longyuan.setLysection((byte) 9);
				LongYuanInfo lyinfo = new LongYuanInfo();
				lyinfo.setLongyuanlv((byte) 9);
				lyinfo.setLongyuannum((byte) 15);
				ResLongYuanOpenMessage cmsg = new ResLongYuanOpenMessage();
				cmsg.setLongyuaninfo(lyinfo);
				MessageUtil.tell_player_message(player, cmsg);
			} else if ("&GAIMING".equalsIgnoreCase(strs[0])) { // 测试改名和改帐号
				ResPlayerNameInfoToClientMessage cmsg = new ResPlayerNameInfoToClientMessage();
				cmsg.setChangeName((byte) 1);
				cmsg.setChangeUser((byte) 1);
				MessageUtil.tell_player_message(player, cmsg);
			} else if ("&HorseWeapon".equalsIgnoreCase(strs[0])) { // 骑兵
				ManagerPool.horseWeaponManager.testHorseWeapon(player,
						Integer.valueOf(strs[1]));

			} else if ("&SIGN".equalsIgnoreCase(strs[0])) { // 签到
				ManagerPool.signWageManager.testgm(player,
						Integer.valueOf(strs[1]));

			} else if ("&Ripening".equalsIgnoreCase(strs[0])) {
				ReqContinuousRipeningToGameMessage conmsg = new ReqContinuousRipeningToGameMessage();
				conmsg.setNum(15);
				conmsg.setType((byte) 2);
				ManagerPool.spiritTreeManager
						.stReqContinuousRipeningToGameMessage(player, conmsg);

			} else if ("&LYUPTO".equalsIgnoreCase(strs[0])) {
				// strs[1]=星图 strs[2]=星位
				LongYuanData longyuan = player.getLongyuan();
				longyuan.setLysection((byte) Byte.valueOf(strs[1]));
				longyuan.setLylevel((byte) Byte.valueOf(strs[2]));
				LongYuanInfo lyinfo = new LongYuanInfo();
				lyinfo.setLongyuanlv((byte) Byte.valueOf(strs[1]));
				lyinfo.setLongyuannum((byte) Byte.valueOf(strs[2]));
				ResLongYuanOpenMessage cmsg = new ResLongYuanOpenMessage();
				cmsg.setLongyuaninfo(lyinfo);
				MessageUtil.tell_player_message(player, cmsg);

			} else if ("&JINYAN".equalsIgnoreCase(strs[0])) {
				String name = strs[1];
				Player onlinePlayerByName = PlayerManager.getInstance()
						.getOnlinePlayerByName(name);
				if (onlinePlayerByName == null) {
					MessageUtil.notify_player(player, Notifys.ERROR, name
							+ "不存在或者已离线");
					return;
				}
				long time = Long.parseLong(strs[2]) * 60 * 1000;
				// if(ChatManager.getInstance().isProhibitChat(player)){
				// 如果在禁言中则替换原时间
				onlinePlayerByName.setStartProhibitChatTime(System
						.currentTimeMillis());
				onlinePlayerByName.setProhibitChatTime(time);
				// }else{
				// player.setStartProhibitChatTime(System.currentTimeMillis());
				// player.setProhibitChatTime(time);
				// }
				MessageUtil.notify_player(player, Notifys.SUCCESS, name
						+ "成功禁言" + Integer.parseInt(strs[2]) + "分钟");
				// }
				// else if("&TR".equalsIgnoreCase(strs[0])){
				// String name = strs[1];
				// Player onlinePlayerByName =
				// PlayerManager.getInstance().getOnlinePlayerByName(name);
				// if (onlinePlayerByName == null) {
				// MessageUtil.notify_player(player, Notifys.ERROR, name +
				// "不存在或者已离线");
				// return;
				// }
				// PlayerManager.getInstance().quit(onlinePlayerByName);
				// MessageUtil.notify_player(player, Notifys.SUCCESS, name +
				// "成功踢掉");
			} else if ("&DELPET".equalsIgnoreCase(strs[0])) {
				if (player.getPetList().size() > 0) {
					Pet pet = player.getPetList().remove(
							player.getPetList().size() - 1);
					pet.setHp(pet.getMaxHp());
					log.error("角色[" + player.getId() + "]美人[" + pet.getId()
							+ "]操作[gm DELPET]");
					PetOptManager.getInstance().hidePet(player, pet.getId());
					PetInfoManager.getInstance().sendPetInfo(player);
				}
			} else if ("&DELPETALL".equalsIgnoreCase(strs[0])) {
				if (player.getPetList().size() > 0) {
					for (Pet pet : player.getPetList()) {
						pet.setHp(pet.getMaxHp());
						log.error("角色[" + player.getId() + "]美人[" + pet.getId()
								+ "]操作[gm DELPETALL]");
						PetOptManager.getInstance()
								.hidePet(player, pet.getId());
					}
					player.getPetList().clear();
					PetInfoManager.getInstance().sendPetInfo(player);
				}

			} else if ("&RANK".equalsIgnoreCase(strs[0])) { // 军功
				ManagerPool.rankManager.testgm(player,
						Integer.valueOf(strs[1]), Integer.valueOf(strs[2]));

			} else if ("&ADDPET".equalsIgnoreCase(strs[0])) {
				String model = strs[1];
				boolean exclude = false;
				if (strs.length > 2) {
					String ex = strs[2];
					exclude = "1".equals(ex) ? true : false;
				}
				int parseInt = Integer.parseInt(model);
				PetOptManager.getInstance().addPet(player, parseInt, "gm",
						Config.getId(), exclude);
			} else if ("&SHOWPET".equalsIgnoreCase(strs[0])) {
				int model = Integer.parseInt(strs[1]);
				List<Pet> pets = player.getPetList();
				for (Pet pet : pets) {
					if (pet.getModelId() == model) {
						log.error("角色[" + player.getId() + "]美人[" + pet.getId()
								+ "]操作[gm SHOWPET]");
						PetOptManager.getInstance()
								.showPet(player, pet.getId());
					}
				}
			} else if ("&HIDEPET".equalsIgnoreCase(strs[0])) {
				int model = Integer.parseInt(strs[1]);
				List<Pet> pets = player.getPetList();
				for (Pet pet : pets) {
					if (pet.getModelId() == model) {
						pet.setHp(pet.getMaxHp());
						log.error("角色[" + player.getId() + "]美人[" + pet.getId()
								+ "]操作[gm HIDEPET]");
						PetOptManager.getInstance()
								.hidePet(player, pet.getId());
					}
				}
			} else if ("&petDie".equalsIgnoreCase(strs[0])) {
				Pet showPet = PetInfoManager.getInstance().getShowPet(player);
				if (showPet != null)
					PetOptManager.getInstance().die(showPet, player);
			}else if ("&summonpetDie".equalsIgnoreCase(strs[0])) {
				SummonPet showsummonPet = SummonPetInfoManager.getInstance().getShowSummonPet(player);
				if (showsummonPet != null)
					SummonPetOptManager.getInstance().die(showsummonPet, player);
			} else if ("&petrevive".equalsIgnoreCase(strs[0])) {
				Pet showPet = PetInfoManager.getInstance().getShowPet(player);
				if (showPet.isDie()) {
					Q_petinfoBean q_petinfoBean = DataManager.getInstance().q_petinfoContainer
							.getMap().get(showPet.getModelId());
					showPet.setDieTime(showPet.getDieTime()
							- q_petinfoBean.getQ_revive_time());
				}
			} else if ("&transpet".equalsIgnoreCase(strs[0])) {
				Pet showPet = PetInfoManager.getInstance().getShowPet(player);
				if (showPet != null)
					PetOptManager.getInstance().petTransToOwner(showPet);
			} else if ("&HETI".equalsIgnoreCase(strs[0])) {
				int model = Integer.parseInt(strs[1]);
				List<Pet> pets = player.getPetList();
				for (Pet pet : pets) {
					if (pet.getModelId() == model) {
						PetOptManager.getInstance().heti(player, pet.getId());
						break;
					}
				}
			} else if ("&clearhtcd".equalsIgnoreCase(strs[0])) {
				int model = Integer.parseInt(strs[1]);
				PetOptManager.getInstance().clearHtCD(player, model);

			} else if ("&marriage".equalsIgnoreCase(strs[0])) { // 清理结婚
				if (strs[1].equals("1")) {
					player.setMarriageid(0);
				} else if (strs[1].equals("2")) {

				}
			} else if ("&protect".equalsIgnoreCase(strs[0])) { // 清理钻石锁 2级密码
				player.setProtectstatus(0);
				player.setProtecttime(0);
				player.setProtectip("");
				ManagerPool.protectManager.delprotect(player);
			} else if ("&isopenprotect".equalsIgnoreCase(strs[0])) { // 钻石锁
																		// 1开启，2关闭
				if (strs[1].equals("1")) {
					ManagerPool.protectManager.setIsopenlock(true);
					MessageUtil.notify_player(player, Notifys.CHAT_ROLE,
							"钻石锁开启");
				} else if (strs[1].equals("2")) {
					ManagerPool.protectManager.setIsopenlock(false);
					MessageUtil.notify_player(player, Notifys.CHAT_ROLE,
							"钻石锁关闭");
				}
			} else if ("&DGTX".equalsIgnoreCase(strs[0])) { // 地宫探险
				Epalace epalace = player.getEpalace();
				epalace.setMovenum(12);
			}else if ("&angle".equalsIgnoreCase(strs[0])) { // 圣盟争夺战
				CountryAwardManager.getInstance().getAngleBox(player,Integer.parseInt(strs[1]));
			} else if ("&GCZ".equalsIgnoreCase(strs[0])) { // 圣盟争夺战
				ManagerPool.countryManager.testSiege(player);
			}else if ("&op1".equalsIgnoreCase(strs[0])) { // 圣盟争夺战
				ManagerPool.countryManager.opendoor1(Integer.parseInt(strs[1]));
			}
			else if ("&op2".equalsIgnoreCase(strs[0])) { // 圣盟争夺战
				ManagerPool.countryManager.opendoor2(Integer.parseInt(strs[1]));
			}
			else if ("&op3".equalsIgnoreCase(strs[0])) { // 圣盟争夺战
				ManagerPool.countryManager.opendoor3(Integer.parseInt(strs[1]));
			}
			
			
			else if ("&say1".equalsIgnoreCase(strs[0])) { // 圣盟争夺战
				ManagerPool.countryManager.say1();
			}
			else if ("&say2".equalsIgnoreCase(strs[0])) { // 圣盟争夺战
				ManagerPool.countryManager.say2();
			}
			else if ("&say3".equalsIgnoreCase(strs[0])) { // 圣盟争夺战
				ManagerPool.countryManager.say3();
			}
			else if ("&say4".equalsIgnoreCase(strs[0])) { // 圣盟争夺战
				ManagerPool.countryManager.say4();
			}else if ("&GCZJY".equalsIgnoreCase(strs[0])) { // 圣盟争夺战
				ManagerPool.countryManager.jinyan(player, strs[1]);
			}
			else if ("&playerdata".equalsIgnoreCase(strs[0])) { // 圣盟争夺战
				ManagerPool.countryManager.jinyan(player, strs[1]);
			}
			else if ("&GCZ1".equalsIgnoreCase(strs[0])) { // 圣盟争夺战
				ManagerPool.countryManager.creatMonster(0,1);
			}
			else if ("&GCZ1M".equalsIgnoreCase(strs[0])) { // 圣盟争夺战
				ManagerPool.countryManager.creatMonster2(0,1,player);
			}
			else if ("&GCZ1M2".equalsIgnoreCase(strs[0])) { // 圣盟争夺战
				ManagerPool.countryManager.creatMonster3(0,1,player);
			}
			else if ("&GCZ2".equalsIgnoreCase(strs[0])) { // 圣盟争夺战
				
				ManagerPool.countryManager.creatMonster2(1);
			}
			else if ("&GCZ3".equalsIgnoreCase(strs[0])) { // 圣盟争夺战
				
				ManagerPool.countryManager.creatMonster(0,3);
			}
			else if ("&GCZ4".equalsIgnoreCase(strs[0])) { // 圣盟争夺战
	
				ManagerPool.countryManager.creatMonster(0,4);
			}
			else if ("&GCZW".equalsIgnoreCase(strs[0])) { // 圣盟争夺战
	
				player.setGroupmark(1);
				ManagerPool.countryManager.creatMonster(player.getGroupmark(),1);
			}
			
			else if ("&GCZEND".equalsIgnoreCase(strs[0])) { // 圣盟争夺战
				ManagerPool.countryManager.testSiegeend(player);
				ManagerPool.countryManager.backtocitymove();
			} else if ("&GCZMON".equalsIgnoreCase(strs[0])) { // 圣盟争夺战刷怪
				ManagerPool.countryManager.appearMonster();
			} else if ("&GCZITEM".equalsIgnoreCase(strs[0])) { // 圣盟争夺战刷道具
				ManagerPool.countryManager.RefreshMapItemDrop();
			} else if ("&DIANJIANGCHUN".equalsIgnoreCase(strs[0])) {
				String model = strs[1];
				int parseInt = Integer.parseInt(model);
				switch (parseInt) {
				case 1:
					DianjiangchunManager.getInstance().GetDianjiangchunInfo(
							player);
					break;
				case 2:
					DianjiangchunManager.getInstance().GetBeginDianjiangchun(
							player);
					break;
				case 3:
					DianjiangchunManager.getInstance().GetChangeLuck(player);
					break;
				case 4:
					DianjiangchunManager.getInstance()
							.GetReceiveintinfuriatingvalue(player);
					break;
				}

			} else if ("&ZoneBulltest".equalsIgnoreCase(strs[0])) {
				ResZoneTeamOpenBullToClientMessage cmsg = new ResZoneTeamOpenBullToClientMessage();
				cmsg.setZonemodelid(Integer.valueOf(strs[1]));
				MessageUtil.tell_world_message(cmsg);
			}else if ("&zoneout".equalsIgnoreCase(strs[0])) {
				ReqZoneOutMessage cmsg = new ReqZoneOutMessage();
				ManagerPool.zonesManager.stReqZoneOutMessage((Player)this.getParameter(),cmsg);
				
				
			}else if ("&zoneCount".equalsIgnoreCase(strs[0])) {
//				ManagerPool.zonesTeamManager.stReqZoneTeamOpenToGameMessage((Player)this.getParameter());
				
				
				MessageUtil.notify_player(player, Notifys.SUCCESS, "已经开启副本数量："+ManagerPool.zonesManager.getmServers().size()+"");
				
			}else if ("&zonein".equalsIgnoreCase(strs[0])) {
//				ManagerPool.zonesTeamManager.stReqZoneTeamOpenToGameMessage((Player)this.getParameter());
				ReqZoneTeamEnterToGameMessage cmsg = new ReqZoneTeamEnterToGameMessage();
				cmsg.setEntertype((byte)0);
				cmsg.setZoneid(Integer.valueOf(strs[1]));
				ManagerPool.zonesTeamManager.stReqZoneTeamEnterToGameMessage((Player)this.getParameter(),cmsg);
			}
			else if ("&csysstart".equalsIgnoreCase(strs[0])) {
				ManagerPool.csysManger.fight();
			}
			else if ("&csys".equalsIgnoreCase(strs[0])) {
				ManagerPool.csysManger.creatZones();
				ManagerPool.csysManger.startCsys();
				
			}
			
			else if ("&csysend".equalsIgnoreCase(strs[0])) {
				ManagerPool.csysManger.endCsys();
			}
			else if ("&csysend2".equalsIgnoreCase(strs[0])) {
				ManagerPool.csysManger.sendCsystopinfo(player);
			}
			else if ("&sayend".equalsIgnoreCase(strs[0])) {
				ManagerPool.csysManger.sayend();
			}
			else if ("&zonemz".equalsIgnoreCase(strs[0])) {
				ResCountrySiegeSelectToGameMessage cmsg = new ResCountrySiegeSelectToGameMessage();
				cmsg.setType((byte)1);
				ManagerPool.countryManager.stResCountrySiegeSelectToGameMessage((Player)this.getParameter(),cmsg);
			}
			
			
			else if ("&mzget".equalsIgnoreCase(strs[0])) {
				ReqCountrysalaryToGameMessage cmsg = new ReqCountrysalaryToGameMessage();
				CountryAwardManager.getInstance().reqCountrysalaryToGame((Player)this.getParameter(), cmsg);
				
			}
			else if ("&zonemzout".equalsIgnoreCase(strs[0])) {
				ResCountrySiegeSelectToGameMessage cmsg = new ResCountrySiegeSelectToGameMessage();
				cmsg.setType((byte)2);
				ManagerPool.countryManager.stResCountrySiegeSelectToGameMessage((Player)this.getParameter(),cmsg);
				
			}
			else if ("&zonebuff".equalsIgnoreCase(strs[0])) {
				
//				ManagerPool.zonesTeamManager.stReqZoneTeamOpenToGameMessage((Player)this.getParameter());
				ReqAddBuffMessage cmsg = new ReqAddBuffMessage();
				cmsg.setType((byte)1);
				ManagerPool.zonesFlopManager.stReqAddBuffMessage((Player)this.getParameter(),cmsg.getType());
				
			}
			else if ("&zoneget".equalsIgnoreCase(strs[0])) {
			
				ReqSelectAwardMessage cmsg = new ReqSelectAwardMessage();
				cmsg.setIdx(0);
				cmsg.setType((byte)0);
				ManagerPool.zonesFlopManager.stReqSelectAwardMessage((Player)this.getParameter(),cmsg);
			}
			else if ("&mzSkill".equalsIgnoreCase(strs[0])) {
				//(Player player,  int mapModelId, int line, Position Dposition, int skillId, int direction,  List<Long> fightTargets, List<Byte> fightTypes )
				//ManagerPool.fightManager.playerAttackMonster((Player)this.getParameter(),0, 10173, 2);
				
				ManagerPool.fightManager.playerAttackPosition(player,player.getMapModelId(),1,player.getPosition(),10173,2,null,null );
			}
			else if ("&buyMonth".equalsIgnoreCase(strs[0])) {
			
				BankManager.getInstance().buyMonth(player);
				
			}else if ("&getMonth0".equalsIgnoreCase(strs[0])) {
			
				BankManager.getInstance().getMonthRewardByFrist(player);
				
			}else if ("&getMonth1".equalsIgnoreCase(strs[0])) {
			
				BankManager.getInstance().getMonthRewardByDay(player);
				
			}else if ("&buylv".equalsIgnoreCase(strs[0])) {
			
				BankManager.getInstance().buyLevelBank(player,Integer.parseInt(strs[1]));
				
			}else if ("&getlv".equalsIgnoreCase(strs[0])) {
			
				BankManager.getInstance().getLevelBankByLevel(player,Integer.parseInt(strs[1]));
				
			}else if ("&queryBank".equalsIgnoreCase(strs[0])) {
				ReqQueryBankLogMessage cmsg = new ReqQueryBankLogMessage();
				cmsg.setIndexLarge(100);
				cmsg.setIndexlittle(0);
				cmsg.setOption(Integer.parseInt( strs[1]));
				BankManager.getInstance().queryPlayersHistory(cmsg,player);
				
			}
			else if ("&zonef".equalsIgnoreCase(strs[0])) {
				
				ReqZoneReceiveawardsMessage cmsg =  new ReqZoneReceiveawardsMessage();
				ManagerPool.zonesFlopManager.stReqZoneReceiveawardsMessage((Player)this.getParameter(),cmsg);
				
			}
			else if ("&zoneend".equalsIgnoreCase(strs[0])) {
				Map map = MapManager.getInstance().getMap(player);
				ZoneContext zone = ManagerPool.zonesManager.getContexts().get(
						map.getZoneId());
				// 通关失败消息
//				if (zone.getOthers().get("isget") == null) {
					zone.getOthers().put("isget", true);
					byte ispass = 0;
					if (zone.getOthers().get("kill") == null || (Integer) zone.getOthers().get("kill") < 4) {
						zone.getOthers().put("kill", 0);
					} else {
						ispass = 1;
						map.getParameters().put("isfinsh", true);
					}
					int time =(int) (System.currentTimeMillis()/1000) - (Integer)zone.getOthers().get("time");
					Q_clone_activityBean zoneBean = ManagerPool.dataManager.q_clone_activityContainer.getMap().get(map.getZoneModelId());
//					int star =   (zoneBean.getQ_exist_time()-(time+180)*1000)/zoneBean.getQ_time_evaluate();
					for (Entry<Long, Player> entry : map.getPlayers().entrySet()) {
						Player menber = entry.getValue();
						LivenessManager.getInstance().completeEmgc(menber);
						ManagerPool.zonesFlopManager.getZoneReward(menber, map.getZoneModelId(),5 , 0,(byte)1);
					}
//				}
				
//						ManagerPool.zonesFlopManager.getZoneReward(player, 1, Integer.parseInt( strs[1]), new ResZonePassShowMessage());
				
//				//通过副本：
//				ManagerPool.taskManager.action(player, Task.ACTION_TYPE_RANK, TaskEnum.COMPLETECLASSICBATTLE, map.getZoneModelId());
//				//通过副本并且等到s级评价：
//				if(star==5){
//					ManagerPool.taskManager.action(player, Task.ACTION_TYPE_RANK, TaskEnum.S_ZONE, map.getZoneModelId());
//				}
//			}
				
			}
			else if ("&zoneadd".equalsIgnoreCase(strs[0])) {
				ReqZoneTeamSelectToGameMessage cmsg = new ReqZoneTeamSelectToGameMessage();
				cmsg.setSelect((byte)2);
				cmsg.setZoneid(Integer.valueOf(strs[1]));
				ManagerPool.zonesTeamManager.stReqZoneTeamSelectToGameMessage((Player)this.getParameter(),cmsg);
			}
			else if ("&zoneclear".equalsIgnoreCase(strs[0])) {
				ManagerPool.zonesFlopManager.stReqClearZoneCDMessage((Player)this.getParameter(),Integer.valueOf(strs[1]));
			}
			else if ("&izonein".equalsIgnoreCase(strs[0])) {
				
				ReqInventedZoneMessage cmsg = new ReqInventedZoneMessage();
				cmsg.setZoneid(300009);
				ManagerPool.zonesManager.stReqInventedZoneInMessage((Player)this.getParameter(),cmsg);
				
			}else if ("&izoneout".equalsIgnoreCase(strs[0])) {
				
				ReqInventedZoneMessage cmsg = new ReqInventedZoneMessage();
				cmsg.setZoneid(300009);
				ManagerPool.zonesManager.stReqInventedZoneOutMessage((Player)this.getParameter(),cmsg);
				
			}else if ("&getraward".equalsIgnoreCase(strs[0])) {
				
				ManagerPool.playerManager.collectionReward((Player)getParameter());
				
			}
		
			else if ("&CLEARDIANJIANGCHUN".equalsIgnoreCase(strs[0])) {
				DianjiangchunManager.getInstance().ClearDianjiangchunInfo(
						player);
			} else if ("&ts".equalsIgnoreCase(strs[0])) {
				MessageUtil.notify_All_player(Byte.parseByte(strs[1]) + "", strs[2]);
			} else if ("&qh".equalsIgnoreCase(strs[0])) {
				int pos = Integer.parseInt(strs[1]);
				int num = Integer.parseInt(strs[2]);
				ManagerPool.equipstrengManager.testStrengthen(player, pos, num);

			} else if ("&Createateam".equalsIgnoreCase(strs[0])) {// 创建队伍
				ManagerPool.teamManager.stCreateateam(
						(Player) this.getParameter(), 0);
			} else if ("&MOVEZONE".equalsIgnoreCase(strs[0])) {// 进入副本

				ReqZoneIntoMessage smsg = new ReqZoneIntoMessage();
				smsg.setIsauto((byte) 0);
				smsg.setZoneid(Integer.parseInt(strs[1]));
				ManagerPool.zonesManager.stResReqZoneIntoMessage(player, smsg);
			} else if ("&entrance".equalsIgnoreCase(strs[0])) { 
				// 获取八卦正入口方位
				Q_clone_activityBean db = ManagerPool.dataManager.q_clone_activityContainer
						.getMap().get(3002);
				List<Integer> mapidlist = JSON.parseArray(db.getQ_mapid(),
						Integer.class);
				String pos = "";
				for (int id : mapidlist) {
					pos = pos + id + "="
							+ (ZonesTeamManager.entrance.get(id) - 9) + "  ,  ";
				}
				MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, pos);
			} else if ("&ZONENUM".equalsIgnoreCase(strs[0])) {// 清除副本次数
				List<Q_clone_activityBean> data = ManagerPool.dataManager.q_clone_activityContainer
						.getList();
				for (Q_clone_activityBean q_clone_activityBean : data) {
					if (q_clone_activityBean.getQ_zone_type() == 1) {
						ManagerPool.countManager.removeCount(player,
								CountTypes.ZONE_MANUAL,
								q_clone_activityBean.getQ_id() + "");
						ManagerPool.countManager.removeCount(player,
								CountTypes.ZONE_AUTO,
								q_clone_activityBean.getQ_id() + "");
					} else if (q_clone_activityBean.getQ_zone_type() == 2) {
						int id = Integer.valueOf(strs[1]);
						if (id == 4003) {
							ManagerPool.countManager.removeCount(player,
									CountTypes.JIAOCHANG_NUM, "" + id);
						}
					} else if (q_clone_activityBean.getQ_zone_type() == 3) {
						ManagerPool.countManager.removeCount(player,
								CountTypes.ZONE_MANUAL,
								q_clone_activityBean.getQ_id() + "");
					} else if (q_clone_activityBean.getQ_zone_type() == 4) {
						ManagerPool.countManager.removeCount(player,
								CountTypes.ZONE_MANUAL,
								q_clone_activityBean.getQ_id() + "");
					}
				}

			} else if ("&ZONERW".equalsIgnoreCase(strs[0])) {
				ManagerPool.zonesManager.testzonesreward(player,
						Integer.parseInt(strs[1]));
			} else if ("&Flag".equalsIgnoreCase(strs[0])) {// 离开副本
				ManagerPool.guildFlagManager.testflag(player,
						Integer.valueOf(strs[1]));
			} else if ("&king".equalsIgnoreCase(strs[0])) {// 查看王城消息
				ManagerPool.countryManager
						.stReqCountryStructureInfoToGameMessage(player);

				ManagerPool.zonesManager.outZone(player);
			} else if ("&zoneAward".equalsIgnoreCase(strs[0])) {// 副本一键领奖
				ManagerPool.zonesFlopManager.autoAwardfor(player,
						(int) Integer.valueOf(strs[1]));

			} else if ("&Panel".equalsIgnoreCase(strs[0])) {// NPC面板测试
				NpcParamUtil.Testjiaoben(player, Integer.valueOf(strs[1]));

			}else if ("&testguild".equalsIgnoreCase(strs[0])) {// NPC面板测试
				GuildServerManager.getInstance().getGuildTmpInfo(1);

			}
			
			else if ("&BUFF".equalsIgnoreCase(strs[0])) {// 加BUFF
				ManagerPool.buffManager.addBuff(player, player,
						Integer.parseInt(strs[1]), Long.parseLong(strs[2]), 0,
						0);
			} else if ("&Auto".equalsIgnoreCase(strs[0])) {
				ManagerPool.teamManager.AutoTeaminvited(
						(Player) this.getParameter(), (byte) 1);
			} else if ("&WEARWEAPON".equalsIgnoreCase(strs[0])) {
				player.getEquips()[0] = null;
				List<Item> createItems = Item.createItems(
						Integer.parseInt(strs[1]), 1, false, 0);
				createItems.get(0).use(player);
			} else if ("&WEARARMOR".equalsIgnoreCase(strs[0])) {
				player.getEquips()[1] = null;
				List<Item> createItems = Item.createItems(
						Integer.parseInt(strs[1]), 1, false, 0);
				createItems.get(0).use(player);
			} else if ("&CHANGEMAP".equalsIgnoreCase(strs[0])) {
				int x = Integer.parseInt(strs[2]) * MapUtils.GRID_BORDER;
				int y = Integer.parseInt(strs[3]) * MapUtils.GRID_BORDER;
				ManagerPool.mapManager.changeMap(player,
						Integer.parseInt(strs[1]), Integer.parseInt(strs[1]),
						0, new Position((short) x, (short) y), this.getClass()
								.getName() + ".gm1");
			} else if ("&GuildCreate".equalsIgnoreCase(strs[0])) { // 创建行会
				ReqGuildCreateToServerMessage gmsg = new ReqGuildCreateToServerMessage();
				gmsg.setGuildName(strs[1]);
				gmsg.setGuildBanner(strs[2]);
				gmsg.setGuildBannerIcon(1);
				GuildServerManager.getInstance().reqGuildCreateToServer(
						(Player) this.getParameter(), gmsg);
			} else if ("&GCZTOP".equalsIgnoreCase(strs[0])) { // 圣盟争夺战排行榜测试

				ManagerPool.countryManager
						.stReqCountryOpenTopToGameMessage(player);
			} else if ("&ACTALLGEM".equalsIgnoreCase(strs[0])) { // 激活所有宝石
				ManagerPool.gemManager.testActAllGem(player);

			} else if ("&SETZONENUM".equalsIgnoreCase(strs[0])) { // 副本数量设置
				int num = Integer.parseInt(strs[1]);
				if (num > 0) {
					ManagerPool.zonesManager.setZONEMAX(num);
					MessageUtil.notify_player(player, Notifys.ERROR,
							"副本数量上限设置为{1}", strs[1]);
				}
			} else if ("&biwudao".equalsIgnoreCase(strs[0])) { // 比武岛活动
				if (strs[1].equals("1")) {
					ScriptsUtils.call(ScriptEnum.BIWUDAO, "biwudaoinitialize"); // 开始
				} else if (strs[1].equals("2")) {
					ScriptsUtils.call(ScriptEnum.BIWUDAO, "biwudaoend"); // 结束
				}
			} else if ("&biwudaomove".equalsIgnoreCase(strs[0])) { // 比武岛活动 进出
				if (strs[1].equals("1")) {
					ScriptsUtils
							.call(ScriptEnum.BIWUDAO, "biwudaoentr", player); // 进入
				} else if (strs[1].equals("2")) {
					ScriptsUtils.call(ScriptEnum.BIWUDAO, "biwudaoleave",
							player); // 离开
				}
			} else if ("&testsys1".equalsIgnoreCase(strs[0])) { // 测试公告
				/*xiaozhuoming: 暂时没有用到
				ParseUtil parseUtil = new ParseUtil();
				parseUtil.setValue(String.format(
						"玩家【%s】开启【美人百宝袋】获得8888钻石大奖，真是羡煞旁人！{@}",
 player.getName()), new ParseUtil.VipParm(VipManager.getInstance().getVIPLevel(player),
						GuideType.BAIBAODAI.getValue()));
				MessageUtil.notify_All_player(Notifys.CHAT_BULL,
						parseUtil.toString(), new ArrayList<GoodsInfoRes>(),
						GuideType.BAIBAODAI.getValue());
				*/
			} else if ("&testsys2".equalsIgnoreCase(strs[0])) { // 测试公告
				ItemInfo itemInfo = new ItemInfo();
				itemInfo.setItemModelId(1027);
				itemInfo.setNum(1);
				List<GoodsInfoRes> itemInfos = new ArrayList<GoodsInfoRes>();
				GoodsInfoRes goodsInfoRes = new GoodsInfoRes();
				goodsInfoRes.setItemInfo(itemInfo);
				itemInfos.add(goodsInfoRes);
				/* xuliang
				ParseUtil parseUtil = new ParseUtil();
				parseUtil.setValue(String.format(
						"恭喜玩家【%s】通过一键讨伐功能，快速完成了紫色讨伐任务获得紫色装备\f{@}",
						player.getName()), new ParseUtil.VipParm(VipManager
.getInstance().getVIPLevel(player),
						3));
				MessageUtil.notify_All_player(Notifys.CHAT_BULL,
						parseUtil.toString(), itemInfos, 3);
				*/
			} else if ("&UPALLGEM".equalsIgnoreCase(strs[0])) { // 所有宝石升满级
				ManagerPool.gemManager.testUPAllGem(player);
			} else if ("&SETGEM".equalsIgnoreCase(strs[0])) { // 设置指定部位宝石等级
				if (strs[1] == null || strs[2] == null || strs[3] == null) {
					MessageUtil.notify_player(player, Notifys.ERROR,
							"请输入正确格式(装备部位，宝石位置，等级)");
					return;
				}
				int pos = Integer.parseInt(strs[1]);
				int idx = Integer.parseInt(strs[2]);
				int lv = Integer.parseInt(strs[3]);
				ManagerPool.gemManager.testUPPosGem(player, pos, idx, lv);
			} else if ("&SPIRIT".equalsIgnoreCase(strs[0])) {// 果实全部成熟
				ManagerPool.spiritTreeManager.testReqGuildGMToWorldMessage(
						player, Integer.parseInt(strs[1]));

			} else if ("&ALLRELOAD".equalsIgnoreCase(strs[0])) {// 重读全部数据
				MessageUtil.notify_player(player, Notifys.SUCCESS,
						"allreload完成");
			} else if ("&TWREALMOPEN".equalsIgnoreCase(strs[0])) { // 台湾开放境界
				ManagerPool.realmManager.setIsopen(true);
			} else if ("&TESTYBCARD".equalsIgnoreCase(strs[0])) { // 测试钻石卡
				ReqYBCardToWorldMessage wmsg = new ReqYBCardToWorldMessage();
				wmsg.setPlayerid(player.getId());
				wmsg.setType(Byte.parseByte(strs[1]));
				MessageUtil.send_to_world(wmsg);
			} else if ("&SETDouble".equalsIgnoreCase(strs[0])) { // 设置服务器双倍时间
				if (strs[1] != null && !strs[1].equals("")) {
					MonsterManager.DaguaiDoubleTime = strs[1]; // 自定义服务器双倍时间
					MessageUtil.notify_player(player, Notifys.ERROR,
							"设置完成，稍后1分钟内生效");
				} else {
					MessageUtil.notify_player(player, Notifys.ERROR,
							"请输入双倍经验开放时间");
				}

			} else if ("&team".equalsIgnoreCase(strs[0])) {
				Message msMessage = null;
				switch (Integer.valueOf(strs[1])) {
				case 1: {
					ReqCreateateamGameMessage msGameMessage = new ReqCreateateamGameMessage();
					msMessage = msGameMessage;
				}
					break;
				case 2: {
					ReqInviteGameMessage msGameMessage = new ReqInviteGameMessage();
					msGameMessage.setTeamid(player.getTeamid());
					msGameMessage.setPlayerid(Long.valueOf("328349699349005"));
					// msGameMessage.setCaptainid(Long.valueOf("327855787086844"));
					msMessage = msGameMessage;
				}
					break;
				case 3: {
					ReqInviteGameSelectMessage msGameMessage = new ReqInviteGameSelectMessage();
					msGameMessage.setTeamid(Long.valueOf(strs[2]));
					// msGameMessage.setPlayerid(Long.valueOf("328349699349005"));
					msGameMessage.setCaptainid(Long.valueOf("327855787086844"));
					msGameMessage.setSelect((byte) 0);
					msMessage = msGameMessage;
				}
					break;
				case 4: {
					ReqApplyGameMessage msGameMessage = new ReqApplyGameMessage();
					msGameMessage.setTeamid(Long.valueOf(strs[2]));
					// msGameMessage.setPlayerid(player.getId());
					msMessage = msGameMessage;
				}
					break;
				case 5: {
					ReqApplyGameSelectMessage msGameMessage = new ReqApplyGameSelectMessage();
					msGameMessage.setTeamid(player.getTeamid());
					msGameMessage.setPlayerid(Long.valueOf("327855787086844"));
					msGameMessage.setSelect((byte) 0);
					msMessage = msGameMessage;
				}
					break;
				case 6: {
					ReqAppointGameMessage msGameMessage = new ReqAppointGameMessage();
					msGameMessage.setTeamid(player.getTeamid());
					msGameMessage.setPlayerid(Long.valueOf("328349699349005"));
					msMessage = msGameMessage;
				}
					break;
				case 7: {
					ReqAppointGameSelectMessage msGameMessage = new ReqAppointGameSelectMessage();
					msGameMessage.setTeamid(player.getTeamid());
					// msGameMessage.setPlayerid(player.getId());
					msGameMessage.setSelect((byte) 0);
					msMessage = msGameMessage;
				}
					break;
				case 8: {
					ReqAutoIntoTeamApplyGameMessage msGameMessage = new ReqAutoIntoTeamApplyGameMessage();
					// msGameMessage.setPlayerid(player.getId());
					msGameMessage.setAutointoteamapply((byte) 1);
					msMessage = msGameMessage;
				}
					break;
				case 9: {
					ReqAutoTeaminvitedGameMessage msGameMessage = new ReqAutoTeaminvitedGameMessage();
					// msGameMessage.setPlayerid(player.getId());
					msGameMessage.setAutoTeaminvited((byte) 1);
					msMessage = msGameMessage;
				}
					break;
				case 10: {
					ReqMapSearchPlayerInfoGameMessage msGameMessage = new ReqMapSearchPlayerInfoGameMessage();
					// msGameMessage.setPlayerid(player.getId());
					msGameMessage.setSearchcontent("ss");
					msMessage = msGameMessage;
				}
					break;
				case 11: {
					ReqMapSearchTeamInfoGameMessage msGameMessage = new ReqMapSearchTeamInfoGameMessage();
					// msGameMessage.setPlayerid(player.getId());
					msGameMessage.setSearchcontent("dd");
					msMessage = msGameMessage;
				}
					break;
				case 12: {
					ReqToleaveGameMessage msGameMessage = new ReqToleaveGameMessage();
					// msGameMessage.setTeamid(player.getTeamid());
					msGameMessage.setPlayerid(player.getId());
					msGameMessage.setType((byte) 0);
					msMessage = msGameMessage;
				}
					break;
				case 13: {
					ReqUpdateTeaminfoGameMessage msGameMessage = new ReqUpdateTeaminfoGameMessage();
					msGameMessage.setTeamid(player.getTeamid());
					// msGameMessage.setPlayerid(player.getId());
					// msGameMessage.setType((byte) 0);
					msMessage = msGameMessage;
				}
					break;
				case 14: {
					TeambroadcastMessage msGameMessage = new TeambroadcastMessage();
					msGameMessage.setPlayerid(player.getId());
					msMessage = msGameMessage;
				}
					break;
				}
				try {
					if (msMessage != null) {
						Handler handler = WServer.getMessage_pool().getHandler(
								msMessage.getId());
						handler.setMessage(msMessage);
						handler.setParameter(player);
						handler.action();
					}
				} catch (InstantiationException ex) {
					java.util.logging.Logger.getLogger(
							GmCommandHandler.class.getName()).log(Level.SEVERE,
							null, ex);
				} catch (IllegalAccessException ex) {
					java.util.logging.Logger.getLogger(
							GmCommandHandler.class.getName()).log(Level.SEVERE,
							null, ex);
				}
			} else if ("&stall".equalsIgnoreCase(strs[0])) {
				Message msMessage = null;
				switch (Integer.valueOf(strs[1])) {
				case 1: {
					ReqStallsProductWasAddedMessage msGameMessage = new ReqStallsProductWasAddedMessage();
					msGameMessage.setGoodsid(-1);
					msGameMessage.setPricegold(0);
					msGameMessage.setPriceyuanbao(1000);
					msGameMessage.setNum(10000);
					msMessage = msGameMessage;
				}
					break;
				case 2: {
					ReqStallsPlayerIdLookMessage msGameMessage = new ReqStallsPlayerIdLookMessage();
					msGameMessage.setPlayerid(Long.valueOf("327855787086844"));
					msMessage = msGameMessage;
				}
					break;
				case 3: {
					ReqStallsOpenUpMessage msGameMessage = new ReqStallsOpenUpMessage();
					msGameMessage.setIndexlittle(0);
					msGameMessage.setIndexLarge(5);
					msMessage = msGameMessage;
				}
					break;
				case 4: {
					ReqStallsBuyMessage msGameMessage = new ReqStallsBuyMessage();
					msGameMessage.setGoodsid(-1);
					msGameMessage.setPlayerid(Long.valueOf("327855787086844"));
					msGameMessage.setPricegold(0);
					msGameMessage.setPriceyuanbao(1000);
					msMessage = msGameMessage;
				}
					break;
				case 5: {
					ReqChangeStallsNameMessage msGameMessage = new ReqChangeStallsNameMessage();
					msGameMessage.setName("测试摊位");
					msMessage = msGameMessage;
				}
					break;
				case 6: {
					ReqStallsAdjustPricesMessage msGameMessage = new ReqStallsAdjustPricesMessage();
					msGameMessage.setGoodsid(-1);
					msGameMessage.setPos(2);
					msGameMessage.setPricegold(100);
					msGameMessage.setPriceyuanbao(100);
					msGameMessage.setNum(100);
					msMessage = msGameMessage;
				}
					break;
				case 7: {
					ReqStallsOffShelfMessage msGameMessage = new ReqStallsOffShelfMessage();
					msGameMessage.setGoodsid(-1);
					msMessage = msGameMessage;
				}
					break;
				case 8: {
					ReqStallsRatingMessage msGameMessage = new ReqStallsRatingMessage();
					msGameMessage.setPlayerid(Long.valueOf("327855787086844"));
					msMessage = msGameMessage;
				}
					break;
				case 9: {
					ReqStallsSearchMessage msGameMessage = new ReqStallsSearchMessage();
					msGameMessage.setGoodsname("金币");
					msGameMessage.setPlayername("ss");
					msGameMessage.setGoldyuanbao((byte) 3);
					msMessage = msGameMessage;
				}
					break;
				case 10: {
					ReqStallsSortMessage msGameMessage = new ReqStallsSortMessage();
					msGameMessage.setIndexlittle(0);
					msGameMessage.setIndexLarge(5);
					msGameMessage.setType(Byte.valueOf(strs[2]));
					msMessage = msGameMessage;
				}
					break;
				case 11: {
					ReqStallsLooklogMessage msGameMessage = new ReqStallsLooklogMessage();
					msMessage = msGameMessage;
				}
					break;
				}
				try {
					if (msMessage != null) {
						Handler handler = WServer.getMessage_pool().getHandler(
								msMessage.getId());
						handler.setMessage(msMessage);
						handler.setParameter(player);
						handler.action();
					}
				} catch (InstantiationException ex) {
					java.util.logging.Logger.getLogger(
							GmCommandHandler.class.getName()).log(Level.SEVERE,
							null, ex);
				} catch (IllegalAccessException ex) {
					java.util.logging.Logger.getLogger(
							GmCommandHandler.class.getName()).log(Level.SEVERE,
							null, ex);
				}
			} else if ("&trade".equalsIgnoreCase(strs[0])) {
				Message msMessage = null;
				switch (Integer.valueOf(strs[1])) {
				case 1: {
					ReqAutorefusaldealMessage msGameMessage = new ReqAutorefusaldealMessage();
					msGameMessage.setState((byte) 0);
					msMessage = msGameMessage;
				}
					break;
				case 2: {
					ReqTransactionsLaunchMessage msGameMessage = new ReqTransactionsLaunchMessage();
					msGameMessage.setPlayerid(Long.valueOf("328349699349005"));
					msMessage = msGameMessage;
				}
					break;
				case 3: {
					ReqTransactionsAcceptMessage msGameMessage = new ReqTransactionsAcceptMessage();
					msGameMessage.setTransid(Long.valueOf(strs[2]));
					msMessage = msGameMessage;
				}
					break;
				case 4: {
					ReqTransactionsChangeGoldMessage msGameMessage = new ReqTransactionsChangeGoldMessage();
					msGameMessage.setGold(10000);
					msMessage = msGameMessage;
				}
					break;
				case 5: {
					ReqTransactionsChangeYuanbaoMessage msGameMessage = new ReqTransactionsChangeYuanbaoMessage();
					msGameMessage.setYuanbao(1000);
					msMessage = msGameMessage;
				}
					break;
				case 6: {
					ReqTransactionsIntoItemMessage msGameMessage = new ReqTransactionsIntoItemMessage();
					msGameMessage.setItemid(Long.valueOf(strs[2]));
					msGameMessage.setItemposition(Short.valueOf(strs[3]));
					msMessage = msGameMessage;
				}
					break;
				case 7: {
					ReqTransactionsRemoveItemMessage msGameMessage = new ReqTransactionsRemoveItemMessage();
					msGameMessage.setItemid(Long.valueOf(strs[2]));
					msMessage = msGameMessage;
				}
					break;
				case 8: {
					ReqTransactionsRefuseMessage msGameMessage = new ReqTransactionsRefuseMessage();
					msGameMessage.setTransid(Long.valueOf(strs[2]));
					msMessage = msGameMessage;
				}
					break;
				case 9: {
					ReqTransactionsSetStateMessage msGameMessage = new ReqTransactionsSetStateMessage();
					msGameMessage.setState(Byte.valueOf(strs[2]));
					msMessage = msGameMessage;
				}
					break;
				case 10: {
					ReqTransactionsCanceledMessage msGameMessage = new ReqTransactionsCanceledMessage();
					msMessage = msGameMessage;
				}
					break;
				case 11: {
					ReqStallsLooklogMessage msGameMessage = new ReqStallsLooklogMessage();
					msMessage = msGameMessage;
				}
					break;
				}
				try {
					if (msMessage != null) {
						Handler handler = WServer.getMessage_pool().getHandler(
								msMessage.getId());
						handler.setMessage(msMessage);
						handler.setParameter(player);
						handler.action();
					}
				} catch (InstantiationException ex) {
					java.util.logging.Logger.getLogger(
							GmCommandHandler.class.getName()).log(Level.SEVERE,
							null, ex);
				} catch (IllegalAccessException ex) {
					java.util.logging.Logger.getLogger(
							GmCommandHandler.class.getName()).log(Level.SEVERE,
							null, ex);
				}
			} else if ("&addtask".equalsIgnoreCase(strs[0])) {
				if (strs.length > 1) {
					if (strs[1].equals("1")) {
						TaskManager.getInstance().acceptDailyTask(player);
					}
				}
				if (strs.length > 2) {
					if (strs[1].equals("2")) {
						int parseInt = Integer.parseInt(strs[2]);
						TaskManager.getInstance().acceptConquerTask(player,
								parseInt);
					}
				}
				// TaskManager.getInstance().accept(player,Byte.parseByte(strs[1]),Integer.parseInt(strs[2]));
			} else if ("&finishtask".equalsIgnoreCase(strs[0])) {
				if (strs[1] == null || strs[1].equals("1")) {
					MainTask mainTask = player.getCurrentMainTasks().get(0);
					mainTask.finshTask(null);
				}
				if (strs[1] != null && strs[1].equals("2")) {
					DailyTask dailyTask = player.getCurrentDailyTasks().get(0);
					dailyTask.finshTask(null);
				}
				if (strs[1] != null && strs[1].equals("3")) {
					if (strs[2] != null && !strs[2].equals("")) {
						ConquerTask task = player.getCurrentConquerTasks().get(
								Integer.parseInt(strs[2]));
						task.finshTask(null);
					}
				}
				// TaskManager.getInstance().finshTask(player,
				// Byte.parseByte(strs[1]),Integer.parseInt(strs[2]),0);
			} else if ("&totask".equalsIgnoreCase(strs[0])) {
				if (strs[1] != null && !StringUtil.isBlank(strs[1])) {
					int modelId = Integer.parseInt(strs[1]);
					MainTask mainTask = player.getCurrentMainTasks().get(0);
					Q_task_mainBean q_task_mainBean = DataManager.getInstance().q_task_mainContainer
							.getMap().get(mainTask.getModelid());
					Q_task_mainBean q_task_mainBean2 = DataManager
							.getInstance().q_task_mainContainer.getMap().get(
							q_task_mainBean.getQ_next_task());
					while (mainTask.getModelid() < modelId
							&& q_task_mainBean2 != null
							&& q_task_mainBean2.getQ_accept_needmingrade() <= player
									.getLevel()) {
						mainTask = player.getCurrentMainTasks().get(0);
						mainTask.finshTask(null);
						q_task_mainBean = DataManager.getInstance().q_task_mainContainer
								.getMap().get(mainTask.getModelid());
						q_task_mainBean2 = DataManager.getInstance().q_task_mainContainer
								.getMap().get(q_task_mainBean.getQ_next_task());
						log.info("接受任务" + q_task_mainBean2.getQ_name() + " "
								+ q_task_mainBean2.getQ_taskid());
					}

				}
			} else if ("&taskback".equalsIgnoreCase(strs[0])) {
				// MainTask mainTask = player.getCurrentMainTasks().get(0);
				// Q_task_mainBean beforeModel = null;
				// List<Q_task_mainBean> list = DataManager.getInstance().q_task_mainContainer
				// .getList();
				// for (Q_task_mainBean q_task_mainBean : list) {
				// if (q_task_mainBean.getQ_next_task() == mainTask
				// .getModelid()) {
				// beforeModel = q_task_mainBean;
				// }
				// }
				// if (beforeModel != null) {
				// player.getCurrentMainTasks().remove(mainTask);
				// player.getCurrentMainTasks().clear();
				// MainTask task = new MainTask();
				// task.initTask(player, beforeModel.getQ_taskid());
				// try {
				// IMainTaskAcceptAction acceptAction = (IMainTaskAcceptAction) ScriptManager
				// .getInstance().getScript(
				// ScriptEnum.TASK_ACCEPTAFTER);
				// if (acceptAction == null) {
				// log.info("接受主线任务脚本找不到");
				// } else {
				// acceptAction.acceptMainTaskAfter(player, task);
				// }
				// } catch (Exception e) {
				// log.info(e, e);
				// }
				// if (player.getLevel() >= 30
				// && beforeModel.getQ_chapter() >= 1) {
				// TaskManager.getInstance().acceptDailyTask(player);
				// }
				// }
			} else if ("&rsttask".equalsIgnoreCase(strs[0])) {
				player.setDailyTaskCount(0);
				player.setDailyTaskTime(0);
				player.setDaydevourcount(0);
				player.setDaydevourTime(0);
				player.setConquerTaskCount(0);
				player.setConquerTaskMaxCount(0);
				player.setConquerTaskTime(0);
				player.getCurrentDailyTasks().clear();
				player.getLaterList().clear();
				TaskManager.getInstance().loginCheckTask(player);
				// player.getCurrentConquerTasks().clear();
				// TaskManager.getInstance().sendTaskInfo(player);
			} else if ("&rsttask2".equalsIgnoreCase(strs[0])) {
				TaskManager.getInstance().resetTask(player);
			} else if ("&monster".equalsIgnoreCase(strs[0])) {
				int monsterModel = Integer.parseInt(strs[1]);
				int num = Integer.parseInt(strs[2]);
				HashMap<Integer, Integer> morphs = new HashMap<Integer, Integer>();
				if (strs.length > 3 && strs[3] != null
						&& !strs[3].trim().equals("")) {
					String[] split = strs[3].split(Symbol.SHUXIAN_REG);
					for (String string : split) {
						String[] split2 = string.split(Symbol.DOUHAO);
						morphs.put(Integer.parseInt(split2[0]),
								Integer.parseInt(split2[1]));
					}
				}
				List<Grid> roundNoBlockGrid = MapUtils.getRoundNoBlockGrid(
						player.getPosition(), 500, player.getMap());
				List<Grid> point = new ArrayList<Grid>();
				while (num > 0) {
					point.add(roundNoBlockGrid.remove(RandomUtils
							.random(roundNoBlockGrid.size())));
					num--;
				}
				Set<Integer> keySet = morphs.keySet();
				HashMap<Integer, Morph> map = new HashMap<Integer, Morph>();
				for (Integer integer : keySet) {
					Morph morph = new Morph();
					morph.setType(1 << (integer - 1));
					morph.setValue(morphs.get(integer));
					map.put(morph.getType(), morph);
				}
				for (Grid grid : point) {
					Monster createMonster = ManagerPool.monsterManager
							.createMonster(monsterModel, player.getServerId(),
									player.getLine(), player.getMap(),
									grid.getCenter());
					if (map.size() > 0) {
						createMonster.setMorph(map);
					}
					MapManager.getInstance().enterMap(createMonster);
				}
			} else if ("&onlymonster".equalsIgnoreCase(strs[0])) {
				int monsterModel = Integer.parseInt(strs[1]);
				int num = Integer.parseInt(strs[2]);
				HashMap<Integer, Integer> morphs = new HashMap<Integer, Integer>();
				if (strs.length > 3 && strs[3] != null
						&& !strs[3].trim().equals("")) {
					String[] split = strs[3].split(Symbol.SHUXIAN_REG);
					for (String string : split) {
						String[] split2 = string.split(Symbol.DOUHAO);
						morphs.put(Integer.parseInt(split2[0]),
								Integer.parseInt(split2[1]));
					}
				}
				List<Grid> roundNoBlockGrid = MapUtils.getRoundNoBlockGrid(
						player.getPosition(), 500, player.getMap());
				List<Grid> point = new ArrayList<Grid>();
				while (num > 0) {
					point.add(roundNoBlockGrid.remove(RandomUtils
							.random(roundNoBlockGrid.size())));
					num--;
				}
				Set<Integer> keySet = morphs.keySet();
				HashMap<Integer, Morph> map = new HashMap<Integer, Morph>();
				for (Integer integer : keySet) {
					Morph morph = new Morph();
					morph.setType(1 << (integer - 1));
					morph.setValue(morphs.get(integer));
					map.put(morph.getType(), morph);
				}
				for (Grid grid : point) {
					ManagerPool.monsterManager
							.createStoryMonsterAndEnterMap(player,
									monsterModel, player.getServerId(),
									player.getLine(), player.getMap(),
									grid.getCenter());
				}
			} else if ("&bagreset".equalsIgnoreCase(strs[0])) {
				BackpackManager.getInstance().bagClearUp(player, true);
			} else if ("&storereset".equalsIgnoreCase(strs[0])) {
				// StoreManager.getInstance().storeClearUp(player, true,-1);
			} else if ("&fangchenmi".equalsIgnoreCase(strs[0])) {
				GmCommandToWorldMessage wmsg = new GmCommandToWorldMessage();
				wmsg.setPlayerId(player.getId());
				wmsg.setCommand(msg.getCommand());
				MessageUtil.send_to_world(wmsg);
			} else if ("&kick".equalsIgnoreCase(strs[0])) {
				GmCommandToWorldMessage wmsg = new GmCommandToWorldMessage();
				wmsg.setPlayerId(player.getId());
				wmsg.setCommand(msg.getCommand());
				MessageUtil.send_to_world(wmsg);
			} else if ("&maxlogin".equalsIgnoreCase(strs[0])) {
				GmCommandToGateMessage wmsg = new GmCommandToGateMessage();
				wmsg.setPlayerId(player.getId());
				wmsg.setCommand(msg.getCommand());
				MessageUtil.send_to_gate(wmsg);
			} else if ("&reset".equalsIgnoreCase(strs[0])) {
				GmCommandToGateMessage wmsg = new GmCommandToGateMessage();
				wmsg.setPlayerId(player.getId());
				wmsg.setCommand(msg.getCommand());
				MessageUtil.send_to_gate(wmsg);
			} else if ("&gmchat".equalsIgnoreCase(strs[0])) {
				GmCommandToGateMessage wmsg = new GmCommandToGateMessage();
				wmsg.setPlayerId(player.getId());
				wmsg.setCommand(msg.getCommand());
				MessageUtil.send_to_gate(wmsg);
			} else if ("&settoken".equalsIgnoreCase(strs[0])) {
				if (strs.length > 1) {
					GmCommandToGateMessage gmsg = new GmCommandToGateMessage();
					gmsg.setPlayerId(player.getId());
					gmsg.setCommand(msg.getCommand());
					MessageUtil.send_to_gate(gmsg);
				}
			} else if ("&goto".equalsIgnoreCase(strs[0])) {
				gotoPlayer(player, strs);
			} else if ("&wudi".equalsIgnoreCase(strs[0])) {
				if (strs.length >= 2 && Integer.parseInt(strs[1]) == 1) {
					player.setGmState(player.getGmState()
							& (~GmState.WUDI.getValue()));
				} else {
					player.setGmState(player.getGmState()
							| GmState.WUDI.getValue());
				}
			} else if ("&yinshen".equalsIgnoreCase(strs[0])) {
				if (strs.length >= 2 && Integer.parseInt(strs[1]) == 1) {
					player.setGmState(player.getGmState()
							& (~GmState.YINSHEN.getValue()));
				} else {
					player.setGmState(player.getGmState()
							| GmState.YINSHEN.getValue());
				}
			} else if ("&REALM".equalsIgnoreCase(strs[0])) { // 设置境界
				player.getRealm().setRealmlevel(Integer.parseInt(strs[1]));
				if (strs.length > 2) {
					player.getRealm().setIntensifylevel(
							Integer.parseInt(strs[2]));
				} else {
					player.getRealm().setIntensifylevel(0);
				}
				player.getRealm().setBreaknum(0);
				player.getRealm().setBlessingnum(0);
				ManagerPool.realmManager.sendRealmInfo(player);
				ManagerPool.playerAttributeManager.countPlayerAttribute(player,
						PlayerAttributeType.REALM);

			} else if ("&showmonster".equalsIgnoreCase(strs[0])) {
				if (strs.length >= 2) {
					MonsterPositionTimer.monsters
							.add(Integer.parseInt(strs[1]));
				} else if (strs.length >= 3 && Integer.parseInt(strs[2]) == 1) {
					MonsterPositionTimer.monsters.remove(Integer
							.parseInt(strs[1]));
				}
			} else if ("&monsterstate".equalsIgnoreCase(strs[0])) {
				Map map = ManagerPool.mapManager.getMap(player);
				long id = Long.parseLong(strs[1], 16);
				Monster monster = map.getMonsters().get(id);
				MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM,
						JSONserializable.toString(monster));
			} else if ("&showblock".equalsIgnoreCase(strs[0])) {
				showBlock(player);
			} else if ("&selectline".equalsIgnoreCase(strs[0])) {
				ReqSelectLineMessage message = new ReqSelectLineMessage();
				message.setLine(Integer.valueOf(strs[1]));
				MapManager.getInstance().reqSelectLine(player, message);
			} else if ("&recharge".equalsIgnoreCase(strs[0])) {
				GmCommandToWorldMessage wmsg = new GmCommandToWorldMessage();
				wmsg.setCommand(msg.getCommand());
				wmsg.setPlayerId(player.getId());
				MessageUtil.send_to_world(wmsg);
			} else if ("&conserve".equalsIgnoreCase(strs[0])) {
				GmCommandToWorldMessage wmsg = new GmCommandToWorldMessage();
				wmsg.setCommand(msg.getCommand());
				wmsg.setPlayerId(player.getId());
				MessageUtil.send_to_world(wmsg);
			} else if ("&mapid".equalsIgnoreCase(strs[0])) {
				// 查看当前mapid
				int mapid = player.getMapModelId();
				MessageUtil.notify_player(player, Notifys.NORMAL, "当前地图  [ "
						+ mapid + " ]");
			} else if ("&whomapid".equalsIgnoreCase(strs[0])) {
				int count = PlayerManager.getInstance().getOnlineCount();
				MessageUtil.notify_player(player, Notifys.ERROR, "本服在线角色数"
						+ count);
			} else if ("&addfav".equalsIgnoreCase(strs[0])) {
				AddToFavoriteMessage message = new AddToFavoriteMessage();
				MessageUtil.tell_world_message(message);
			} else if ("&dazuo".equalsIgnoreCase(strs[0])) {
				PlayerDaZuoManager.getInstacne().startOrEndDaZuo(player);
			} else if ("&script".equalsIgnoreCase(strs[0])) {
				ManagerPool.scriptManager.reload(Integer.parseInt(strs[1]),
						player.getId());
			} else if ("&loadscript".equalsIgnoreCase(strs[0])) {
				ManagerPool.scriptManager.load(strs[1], player.getId());
			} else if ("&worldscript".equalsIgnoreCase(strs[0])) {
				GmCommandToWorldMessage wmsg = new GmCommandToWorldMessage();
				wmsg.setPlayerId(player.getId());
				wmsg.setCommand(msg.getCommand());
				MessageUtil.send_to_world(wmsg);
			} else if ("&worldloadscript".equalsIgnoreCase(strs[0])) {
				GmCommandToWorldMessage wmsg = new GmCommandToWorldMessage();
				wmsg.setPlayerId(player.getId());
				wmsg.setCommand(msg.getCommand());
				MessageUtil.send_to_world(wmsg);
			} else if ("&mapcount".equalsIgnoreCase(strs[0])) {
				int mapId = 0;
				int line = 0;
				if (strs.length > 1) {
					mapId = Integer.parseInt(strs[1]);
				}
				if (strs.length > 2) {
					line = Integer.parseInt(strs[2]);
				}
				HashMap<Long, Player> players = new HashMap<Long, Player>();
				if (mapId == 0) {
					Iterator<Map> iter = MapManager.getMapping().values()
							.iterator();
					while (iter.hasNext()) {
						Map map = (Map) iter.next();
						players.putAll(map.getPlayers());
					}
				} else if (line == 0) {
					for (int i = 1; i < 10; i++) {
						// 获取地图
						Map map = ManagerPool.mapManager.getMap(
								player.getServerId(), i, mapId);
						if (map != null) {
							players.putAll(map.getPlayers());
						}
					}
				} else {
					// 获取地图
					Map map = ManagerPool.mapManager.getMap(
							player.getServerId(), line, mapId);
					players.putAll(map.getPlayers());
				}

				Iterator<Player> iter = players.values().iterator();
				int num1 = 0;
				int num2 = 0;
				String reg = "\\[([\\d]+)区\\]n([\\d]+)";
				while (iter.hasNext()) {
					Player player2 = (Player) iter.next();

					if (Pattern.matches(reg, player2.getName())) {
						num1++;
					} else {
						num2++;
					}
				}

				MessageUtil.notify_player(player, Notifys.ERROR, "在线玩家" + num2
						+ "，在线机器人" + num1);
			} else if ("&SHOWMESSAGE".equalsIgnoreCase(strs[0])) { // 测试钻石卡
				GmCommandToWorldMessage wmsg = new GmCommandToWorldMessage();
				wmsg.setPlayerId(player.getId());
				wmsg.setCommand(msg.getCommand());
				MessageUtil.send_to_world(wmsg);

				MessageUtil.RECORD_PLAYER.put(player.getId(), 1);
			} else if ("&clearupSpirit".equalsIgnoreCase(strs[0])) { // 清理神树数据
				GmCommandToWorldMessage wmsg = new GmCommandToWorldMessage();
				wmsg.setPlayerId(player.getId());
				wmsg.setCommand(msg.getCommand());
				MessageUtil.send_to_world(wmsg);

			} else if ("&zero".equalsIgnoreCase(strs[0])) {
				TaskManager.getInstance().zeroClockAction(player);
			} else if ("&ZONEMAP".equalsIgnoreCase(strs[0])) {
				Map map = MapManager.getInstance().getMap(player);
				if (map != null && map.isCopy()) {
					ZoneContext context = ZonesManager.getInstance()
							.getContexts().get(map.getZoneId());
					if (context != null) {
						int ckmapmodelid = Integer.parseInt(strs[1]);
						int enter_x = Integer.parseInt(strs[2]);
						int enter_y = Integer.parseInt(strs[3]);
						MapConfig wantMapConfig = null;
						for (int i = 0; i < context.getConfigs().size(); i++) {
							MapConfig mapConfig = context.getConfigs().get(i);
							if (mapConfig.getMapModelId() == ckmapmodelid) {
								wantMapConfig = mapConfig;
							}
							if (i == 0) {
								if (ckmapmodelid < mapConfig.getMapModelId()) {
									wantMapConfig = mapConfig;
								}
							}
							if (i == context.getConfigs().size() - 1) {
								if (ckmapmodelid > mapConfig.getMapModelId()) {
									wantMapConfig = mapConfig;
								}
							}
						}
						if (wantMapConfig != null) {
							Grid grid = MapUtils.getGrid(enter_x, enter_y,
									wantMapConfig.getMapModelId());
							if (grid != null) {
								MapManager.getInstance().changeMap(player,
										wantMapConfig.getMapId(),
										wantMapConfig.getMapModelId(),
										wantMapConfig.getLineId(),
										grid.getCenter(),
										this.getClass().getName() + ".gm 2");
							}
						}
					}
				}
			} else if ("&ADDGM".equalsIgnoreCase(strs[0])) { // 添加临时GM
				Player p = PlayerManager.getInstance().getOnlinePlayerByName(
						strs[1]);
				int level = 1;
				if (strs.length >= 3 && StringUtil.isNumeric(strs[2])) {
					int tolevel = Integer.parseInt(strs[2]);
					if (tolevel == 1 || tolevel == 2) {
						level = Integer.parseInt(strs[2]);
					}
				}
				if (p != null) {
					p.setGmlevel(level);
					p.setGmState(p.getGmState()
							| GmState.GM.getValue());
				}
			} else if ("&DELGM".equalsIgnoreCase(strs[0])) {
				Player p = PlayerManager.getInstance().getOnlinePlayerByName(
						strs[1]);
				if (p != null
						&& gmlevel >= PlayerManager.getInstance()
								.getPlayerGmlevel(p)) {
					p.setGmlevel(0);
					p.setGmState(0);
				}
			} else if ("&removevip".equalsIgnoreCase(strs[0])) {
				// if (strs.length > 1) {
				// if ("1".equalsIgnoreCase(strs[1]))
				// VipManager.getInstance().removeVip(player);
				// // if("2".equalsIgnoreCase(strs[1]))VipManager.getInstance().reduceviptime(player);
				// }

			} else if ("&loadgrantdata".equalsIgnoreCase(strs[0])) { // 重载全服邮件
				SystemgrantManager.getInstance().system_GrantBean_load();
				MessageUtil.notify_player(player, Notifys.CHAT_ROLE,
						"全服邮件发放列表重载完成");
			} else if ("&resetvipreward".equalsIgnoreCase(strs[0])) {
				// VipManager.getInstance().resetVipReward(player);
			}
		
			else if ("&resetvipright".equalsIgnoreCase(strs[0])) {
				// player.getVipright().resetVipRight(player);
			} else if ("&stoplog".equalsIgnoreCase(strs[0])) {
				if (strs[1].equalsIgnoreCase("ad")) {
					LogService.stoplist.add(strs[2]);
				}
				if (strs[1].equalsIgnoreCase("rm")) {
					LogService.stoplist.remove(strs[2]);
				}
			} else if ("&TESTLINE".equalsIgnoreCase(strs[0])) {
				Grid[][] mapBlocks = MapManager.getInstance().getMapBlocks(
						player.getMapModelId());

				Grid start = MapUtils.getGrid(Integer.parseInt(strs[1]),
						Integer.parseInt(strs[2]), mapBlocks);
				Grid end = MapUtils.getGrid(Integer.parseInt(strs[3]),
						Integer.parseInt(strs[4]), mapBlocks);
				testLineGrids(start.getCenter(), end.getCenter(),
						player.getMapModelId());
			} else if ("&addcount".equalsIgnoreCase(strs[0])) {
				int key = Integer.parseInt(strs[1]);
				ServerCountManager.getInstance().addCount(key, 1);
			} else if ("&removeallbuff".equalsIgnoreCase(strs[0])) {
				ManagerPool.buffManager.removeAll(player);
			} else if ("&golddz".equalsIgnoreCase(strs[0])) {
				SceneobjManager.getInstance().loopRefreshSceneObjSpecial(
						"class com.game.server.timer.ServerHeartTimer");
			} else if ("&clearactivi".equalsIgnoreCase(strs[0])) {
				player.getActivitiesReward().clear();
				ScriptsUtils.callWorld(ScriptEnum.BASEACTIVITIES,
						"clearActivi", Long.toString(player.getId()));
			} else if ("&retreattime".equalsIgnoreCase(strs[0])) {
				int sec = Integer.parseInt(strs[1]);
				player.setRetreatGetAwardTime(System.currentTimeMillis() - sec
						* 1000);
				MessageUtil.notify_player(player, Notifys.SUCCESS, "修改成功");
			} else if ("&runonworld".equalsIgnoreCase(strs[0])) {
				GmCommandToWorldMessage wmsg = new GmCommandToWorldMessage();
				wmsg.setPlayerId(player.getId());
				wmsg.setCommand(msg.getCommand());
				MessageUtil.send_to_world(wmsg);
			} else if ("&addfsnum".equalsIgnoreCase(strs[0])) {
				int num = Integer.parseInt(strs[1]);
				int type = 0;
				if (strs.length >= 3) {
					type = Integer.parseInt(strs[2]);
				}
				if (type == 0) {
					for (int i = 1; i <= 7; i++) {
						ArrowManager.getInstance().addFightSpiritNum(player, i,
								num, false, ArrowReasonsType.GMCMD);
					}
					ResArrowInfoMessage sendMessage = new ResArrowInfoMessage();
					sendMessage.setNotifytype(9);
					sendMessage.setArrowinfo(player.getArrowData().toInfo());
					/*xiaozhuoming: 暂时没有用到
					MessageUtil.notify_player(player, Notifys.SUCCESS,
							"修改战魂值成功");
					*/
				} else {
					if (ArrowManager.getInstance().addFightSpiritNum(player,
							type, num, false, ArrowReasonsType.GMCMD)) {
						ResArrowInfoMessage sendMessage = new ResArrowInfoMessage();
						sendMessage.setNotifytype(9);
						sendMessage
								.setArrowinfo(player.getArrowData().toInfo());
						/*xiaozhuoming: 暂时没有用到
						MessageUtil.notify_player(player, Notifys.SUCCESS,
								"修改战魂值成功");
						*/
					}
				}
			} else if ("&cleararrow".equalsIgnoreCase(strs[0])) {
				player.setArrowData(new ArrowData());
				/*xiaozhuoming: 暂时没有用到
				MessageUtil.notify_player(player, Notifys.SUCCESS, "弓箭数据清除成功");
				*/
			} else if ("&cleararrowfs".equalsIgnoreCase(strs[0])) {
				ListIterator<Q_clone_activityBean> listIterator = DataManager
						.getInstance().q_clone_activityContainer.getList()
						.listIterator();
				while (listIterator.hasNext()) {
					Q_clone_activityBean q_clone_activityBean = listIterator
							.next();
					if (q_clone_activityBean != null
							&& q_clone_activityBean.getQ_zone_type() == 4) {
						CountManager.getInstance().removeCount(
								player,
								CountTypes.FIGHTSPIRIT_NUM,
								q_clone_activityBean.getQ_id() + "_"
										+ player.getId());
					}
				}
				/*xiaozhuoming: 暂时没有用到
				MessageUtil.notify_player(player, Notifys.SUCCESS,
						"七曜战魂搜索数据清除成功");
				*/
			} else if ("&resetranktask".equalsIgnoreCase(strs[0])) {
				TaskManager.getInstance().sendRankTaskInfoList(player, true);
				MessageUtil.notify_player(player, Notifys.SUCCESS, "军衔任务重置成功");
			} else if ("&setwebvip".equalsIgnoreCase(strs[0])) {
				// if (strs.length > 1) {
				// int lvl = Integer.parseInt(strs[1]);
				// player.getVipright().setWebVipLevel(lvl);
				// }
				// if (strs.length > 2) {
				// int lvl = Integer.parseInt(strs[2]);
				// player.getVipright().setWebVipLevel2(lvl);
				// }
				// MessageUtil.notify_player(player, Notifys.SUCCESS, "平台VIP重置成功");
			} else if ("&yuandan".equalsIgnoreCase(strs[0])) {
				int tmp = Integer.parseInt(strs[1]);
				int scriptId = 0;
				int itemId = 0;
				if (tmp == 1) {
					scriptId = 1009149;
					itemId = 9149;
				} else if (tmp == 2) {
					scriptId = 1009150;
					itemId = 9150;
				} else if (tmp == 3) {
					scriptId = 1009151;
					itemId = 9151;
				} else {
					return;
				}
				IScript tmpScript = ManagerPool.scriptManager
						.getScript(scriptId);
				if (tmpScript == null)
					return;
				IItemScript script = (IItemScript) tmpScript;
				Item item = ManagerPool.backpackManager.getFirstItemByModelId(
						player, itemId);
				if (item != null) {
					script.use(item, player);
					return;
				}
			} else if ("&anqi".equalsIgnoreCase(strs[0])) {
				HiddenWeapon hiddenWeapon = HiddenWeaponManager.getInstance()
						.getHiddenWeapon(player);
				if (hiddenWeapon == null) {
					return;
				}
				if ("lu".equalsIgnoreCase(strs[1])) {
					int times = Integer.valueOf(strs[2]);
					if (hiddenWeapon.getLayer() + times > 7) {
						return;
					}
					hiddenWeapon.setLayer(hiddenWeapon.getLayer() + times);
					HiddenWeaponManager.getInstance().sendHiddenWeaponInfo(
							player);
				} else if ("lt".equalsIgnoreCase(strs[1])) {
					int times = Integer.valueOf(strs[2]);
					int zhufu = Integer.valueOf(strs[3]);
					hiddenWeapon.setDayupnum(times);
					hiddenWeapon.setDayblessvalue(zhufu);
					HiddenWeaponManager.getInstance().sendHiddenWeaponInfo(
							player);
				} else if ("skill".equalsIgnoreCase(strs[1])) {
					int skill = Integer.valueOf(strs[2]);
					int level = Integer.valueOf(strs[3]);
					int index = Integer.valueOf(strs[4]) - 1;
					Q_hiddenweapon_skillBean skillBean = ManagerPool.dataManager.q_hiddenweapon_skillContainer
							.getMap().get(skill + "_" + level);
					if (skillBean == null) {
						return;
					}

					hiddenWeapon.removeSkillByIndex((byte) index);
					Skill tmpSkill = new Skill();
					tmpSkill.setId(Config.getId());
					tmpSkill.setSkillModelId(skill);
					tmpSkill.setSkillLevel(level);
					hiddenWeapon.addSkill(index, tmpSkill);

					// 这里如果说移除的技能刚好是ico显示的技能,那么就重新计算下一个ico
					if (index == hiddenWeapon.getIcoIndex()) {
						HiddenWeaponManager.getInstance().countNextIco(player);
					}

					// 同步到前端
					ResHiddenWeaponLevelUpSkillMessage retMsg = new ResHiddenWeaponLevelUpSkillMessage();
					retMsg.setIndex((byte) (index + 1));
					retMsg.setSkills(HiddenWeaponManager.getInstance()
							.getSkillInfo(hiddenWeapon, player));
					MessageUtil.tell_player_message(player, retMsg);
				} else if ("skillclear".equalsIgnoreCase(strs[1])) {
					hiddenWeapon.getSkills().clear();
					HiddenWeaponManager.getInstance().countNextIco(player);
					HiddenWeaponManager.getInstance().sendHiddenWeaponInfo(
							player);
				} else if ("round".equalsIgnoreCase(strs[1])) {
					Skill skill = new Skill();
					skill.setId(Config.getId());
					skill.setSkillModelId(25027);
					skill.setSkillLevel(1);
					FightManager.getInstance().useGroundMagic(player, skill,
							player.getMapModelId(), player.getLine(),
							player.getPosition());
				}
			} else if ("&fightpower_max".equalsIgnoreCase(strs[0])) {
				MessageUtil.notify_player(
						player,
						Notifys.ERROR,
						ResManager.getInstance().getString(
								"最大战斗力=" + player.getMaxFightPower()));
			} else if ("&search_expend".equalsIgnoreCase(strs[0])) {
				long roleid = Long.valueOf(strs[1]);
				String startTime = strs[2];
				String endTime = strs[3];
				class TT implements SaveGoldExpendThread.GetExpendGoldCallBack {
					public Player player;

					public void doWhenGetExpendGold(int expendGold) {
						MessageUtil.notify_player(player, Notifys.SUCCESS,
								"消费:" + expendGold);
					}
				}
				TT tt = new TT();
				tt.player = player;
				WServer.getInstance().getSaveGoldExpendThread()
						.addSearchTask(roleid, startTime, endTime, tt);
			}else if("&addzhengqi".equalsIgnoreCase(strs[0])) {
				PlayerManager.getInstance().addZhenqi(player, Integer.parseInt(strs[1]), AttributeChangeReason.BUFF);
			}else if("&openBagcell".equalsIgnoreCase(strs[0])) {
				Attribute coolDowningCell = BackpackManager.getInstance().getCoolDowningCellGrid(player);
				System.out.println("当前时间秒："+(System.currentTimeMillis()/1000)+"，玩家累计时间："+player.getBagCellTimeCount()+",正在倒计时的格子:"+coolDowningCell.getType()+",剩余时间秒:"+coolDowningCell.getValue());
				if(strs.length == 2) {
					int gridId = Integer.parseInt(strs[1]);
					BackpackManager.getInstance().openCellMsg(player, gridId);
				}else {
					// BackpackManager.getInstance().openCellByTime(player);
				}
				coolDowningCell = BackpackManager.getInstance().getCoolDowningCellGrid(player);
				System.out.println("当前时间秒："+(System.currentTimeMillis()/1000)+"，玩家累计时间："+player.getBagCellTimeCount()+",正在倒计时的格子:"+coolDowningCell.getType()+",剩余时间秒:"+coolDowningCell.getValue());
			}else if("&getopenBagcell".equalsIgnoreCase(strs[0])) {
				Attribute coolDowningCell = BackpackManager.getInstance().getCoolDowningCellGrid(player);
				System.out.println("当前时间秒："+(System.currentTimeMillis()/1000)+"，玩家累计时间："+player.getBagCellTimeCount()+",正在倒计时的格子:"+coolDowningCell.getType()+",剩余时间秒:"+coolDowningCell.getValue());
			}else if ("&bagtostore".equalsIgnoreCase(strs[0])) {
				StoreManager.getInstance().bagToStoreMsg(player, Long.parseLong(strs[1]), 0);
			}else if ("&storetobag".equalsIgnoreCase(strs[0])) {
				StoreManager.getInstance().storeToBagMsg(player, Long.parseLong(strs[1]), 0);
			}else if ("&sysmail".equalsIgnoreCase(strs[0])){
				MailServerManager.getInstance().gmSendSystemMail(player, strs);
			} else if ("&qianghua".equalsIgnoreCase(strs[0])) {
				EquipStrengManager.opengm = EquipStrengManager.opengm ? false : true;
			} else if ("&hecheng".equalsIgnoreCase(strs[0])) {
				EquipComposeManager.opengm = EquipComposeManager.opengm ? false : true;
			} else if ("&zhuijia".equalsIgnoreCase(strs[0])) {
				EquipAttributeAddManager.opengm = EquipAttributeAddManager.opengm ? false : true;
			} else if ("&offline".equalsIgnoreCase(strs[0])) {
				OfflineTimer.opengm = OfflineTimer.opengm ? false : true;
				if (strs.length > 1) {
					int alterOfflineCount = OffLineManager.getInstance().alterOfflineCount(player, Integer.parseInt(strs[1]));
					if (alterOfflineCount < OffLineManager.MAX_OFFLINE_COUNT) {
						OffLineManager.getInstance().sendRetreatInfoMessage(player, 1);
					}
				}
			} else if("&createmonster".equalsIgnoreCase(strs[0])){
				ManagerPool.monsterManager.createMonsterByGM(player, Integer.valueOf(strs[1]));
			} else if("&finishtask".equalsIgnoreCase(strs[0])){
				int type = 1;
				if (strs.length > 1){
					type = Integer.valueOf(strs[1]);
				}
				if (type == 1){
					player.getCurrentDailyTasks().get(0).finshTask(player);
				} else{
					player.getCurrentDailyTasks().get(0).finshTask(player);
				}
			} else if("&countplayer".equalsIgnoreCase(strs[0])){
				int count = PlayerManager.getInstance().getOnlineCount();
				
				MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, "本服当前在线人数为：" + count);
			} else if ("&addvip".equalsIgnoreCase(strs[0])) {
				if (strs.length < 3) {
					VipManager.getInstance().buyVIP(player, (byte) 1, Byte.parseByte(strs[1]), null);
				} else {
					VipManager.getInstance().buyVIP(player, Byte.parseByte(strs[1]), Byte.parseByte(strs[2]), strs[3]);
				}
			} else if ("&addvipexp".equalsIgnoreCase(strs[0])) {
				VipManager.getInstance().addExpByGM(player, Integer.parseInt(strs[1]));
			} else if ("&openall".equalsIgnoreCase(strs[0])){
				ArrayList<Integer> guides = new ArrayList<Integer>();
				for (int i = 0; i < 40; i ++){
					guides.add(1);
				}
				player.setGuides(guides);
				
				ResGetGuidesMessage guidesMsg = new ResGetGuidesMessage();
				guidesMsg.setGuides(guides);
				MessageUtil.tell_player_message(player, guidesMsg);
			} else if ("&clearbuff".equalsIgnoreCase(strs[0])){
				for(int i = 0; i < player.getBuffs().size(); i ++ ){
					Buff buff = player.getBuffs().get(i);
					buff.remove(player);
					player.getBuffs().remove(buff);
					ManagerPool.buffManager.sendRemoveBuffMessage(player, buff);
				}
			}else if("&killme".equalsIgnoreCase(strs[0])){
				player.setHp(0);
				ManagerPool.playerManager.onHpChange(player);
				ManagerPool.playerManager.die(player, player);
			}else if("&addSummonPetBuff".equalsIgnoreCase(strs[0])){
				if (player.getSummonPetList().isEmpty() == false){
					ManagerPool.buffManager.addBuff(player, player.getSummonPetList().get(0),
							Integer.parseInt(strs[1]), Long.parseLong(strs[2]), 0,
							0);
				}
			}else if("&chgSummonPetHp".equalsIgnoreCase(strs[0])){
				if (player.getSummonPetList().isEmpty() == false){
					player.getSummonPetList().get(0).setHp(Integer.valueOf(strs[1]));
					ManagerPool.summonpetInfoManager.onHpChange(player.getSummonPetList().get(0));
				}
			}else if("&addspirit".equalsIgnoreCase(strs[0])){
				BackpackManager.getInstance().changeSpirit(player, Integer.valueOf(strs[1]), Reasons.GMCOMMAND, Config.getId());
			}else if("&onekey".equalsIgnoreCase(strs[0])){
				oneKey(player);
			}else if("&onekey2".equalsIgnoreCase(strs[0])){
				oneKey2(player);
			}else if("&pktime".equalsIgnoreCase(strs[0])){
				player.setPkValueTime(Integer.valueOf(strs[1]));
			}else if("&pkvalue".equalsIgnoreCase(strs[0])){
				player.setPkValue(Integer.valueOf(strs[1]));
				PlayerManager.getInstance().onPkValueChange(player);
			}

			//-------hongxiao.z add-------------
		
			String common = strs[0].toLowerCase();
			switch(common)
			{
				case "&addliveness":
					LivenessManager.getInstance().updateLiveness(player, Integer.parseInt(strs[1]));
					break;
				case "&exec":
					LivenessManager.getInstance().exec(player, Short.parseShort(strs[1]));
					break;
				case "&gain":
					LivenessManager.getInstance().gainBox(player, Short.parseShort(strs[1]));
					break;
				case "&fill":
					SignWageManager.getInstance().fillSign(player, Integer.parseInt(strs[1]));
					break;
				case "&toreward":
					SignWageManager.getInstance().receiveSignReward(player, Integer.parseInt(strs[1]));
					break;
				case "&returnpt"://查看爬塔
					ZonesManager.getInstance().stReqZonePanelSelectMessage(player, Integer.parseInt(strs[1]));
					break;
				case "&ptin":	//爬塔调试
					ZonesManager.getInstance().testPt(player, Integer.parseInt(strs[1]));
					break;
				case "&gradegift":	//领取等级礼包奖励
					GradeGiftManager.gainReward(player, Integer.parseInt(strs[1]));
					break;
				case "&pinfo":	//查看手机验证信息
					CardManager.getInstance().testPInfo(player, strs[1], strs[2]);
					break;
				case "&gaincard"://领取新手卡奖励
					CardManager.getInstance().testGainCard(player, strs[1]);
					break;
				case "&jsrw":	//接任务
					TaskManager.getInstance().acceptMainTask(player, Integer.parseInt(strs[1]));
					break;
				case "&uplost":	//升级遗落技能
					LostSkillManager.updateSkill(player, Integer.parseInt(strs[1]));
					break;
				case "&spirit":
					BackpackManager.getInstance().changeSpirit(player, Integer.parseInt(strs[1]), Reasons.GMCOMMAND, Config.getId());
					break;
				case "&wcrw":{
					//完成任务
					List<MainTask> currentMainTasks = player.getCurrentMainTasks();
					if(strs.length > 1) {
						for(MainTask mainTask:currentMainTasks) {
							if(mainTask.getModelid() == Integer.parseInt(strs[1])) {
								mainTask.finshTask(player);
								break;
							}
						}
					}else {
						MainTask mainTask = currentMainTasks.get(0);
						mainTask.finshTask(player);
					}
					break;
				}
				case "&qcrw":{
					List<MainTask> currentMainTasks = player.getCurrentMainTasks();
					Iterator<MainTask> iterator = currentMainTasks.iterator();
					while(iterator.hasNext()) {
						MainTask next = iterator.next();
						if(next.getModelid() == Integer.parseInt(strs[1]) && next.getModelid() != player.getCurrentMainTaskId()) {
							iterator.remove();
							TaskManager.getInstance().sendTaskInfo(player, false);
							break;
						}
					}
					break;
				}
				case "&bianqiang":{
					long id = Config.getId();
					PlayerManager.getInstance().addExp(player,9999999,AttributeChangeReason.GM);
					BackpackManager.getInstance().addGold(player, 99999, Reasons.ACTIVITY_DEL,id );
					BackpackManager.getInstance().changeMoney(player, 99999999, Reasons.ACTIVITY_DEL,id );
					player.getAttibute_one_base()[0] = 9999;
					player.getAttibute_one_base()[1] = 9999;
					player.getAttibute_one_base()[2] = 9999;
					player.getAttibute_one_base()[3] = 9999;
					player.getAttibute_one_base()[4] = 9999;
					VipManager.getInstance().buyVIP(player, (byte)1, (byte)3, "");
					ManagerPool.playerAttributeManager.countPlayerAttribute(
							player, PlayerAttributeType.BASE);
					break;
				}
				case "&addpay":{
					boolean addGold = BackpackManager.getInstance().addGold(player, Integer.parseInt(strs[1]), Reasons.GOLD_RECHARGE, Config.getId());
					if(addGold)
						PlayerManager.getInstance().savePlayer(player);
					break;
				}
				
			}
			//-------end------------
			
			IGmCommandScript script = (IGmCommandScript) ManagerPool.scriptManager
					.getScript(ScriptEnum.GM_COMMAND);
			if (script != null) {
				try {
					script.doCommand(player, msg.getCommand());
				} catch (Exception e) {
					log.error(e, e);
				}
			} else {
				// log.error("GM命令脚本不存在！");
			}
		} catch (ClassCastException e) {
			log.error(e, e);
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	private void killAmonster(Player player, String[] strs) {
		Map map = MapManager.getInstance().getMap(player.getServerId(),
				player.getLine(), player.getMap());
		List<Area> round = MapManager.getInstance().getRound(map,
				player.getPosition());
		if (round != null) {
			for (Area area : round) {
				HashMap<Long, Monster> monsters = area.getMonsters();
				if (monsters != null && monsters.size() > 0) {
					List<Monster> kills = new ArrayList<Monster>();
					for (Entry<Long, Monster> j : monsters.entrySet()) {
						Monster value = j.getValue();
						kills.add(value);
					}
					for (Monster monster : kills) {
						FightManager.getInstance().addHatred(monster, player,
								100);
						monster.getDamages().put(player.getId(),
								monster.getHp());
						monster.setHp(0);
						MonsterManager.getInstance().die(monster, player, -1);
					}
				}
			}

		}

	}

	private void bagCelltime(Player player, String[] strs) {
		Q_backpack_gridBean bagConfig = DataManager.getInstance().q_backpack_gridContainer
				.getMap().get(
						Global.BAG_TYPE + "_" + (player.getBagCellsNum() + 1));
		player.setBagCellTimeCount(bagConfig.getQ_time() - 1);
	}

	private void storeCelltime(Player player, String[] strs) {
		Q_backpack_gridBean storeConfig = DataManager.getInstance().q_backpack_gridContainer
				.getMap().get(
						Global.STORE_TYPE + "_"
								+ (player.getStoreCellsNum() + 1));
		player.setStoreCellTimeCount(storeConfig.getQ_time() - 1);
	}

	private void money(Player player, String[] strs) {
		if (strs.length >= 2) {
			int money = Integer.parseInt(strs[1]);
			ManagerPool.backpackManager.changeMoney(player, money,
					Reasons.GMCOMMAND, Config.getId());
		}
	}

	private void addItem(Player player, String[] strs) {
		// &additem 装备ID 数量 强化等级 绑定(1绑) 附加属性1|附加属性值;附加属性2|附加属性值;附加属性3|附加属性值 失效时间秒;
		int modelid = Integer.parseInt(strs[1]);
		int num = Integer.parseInt(strs[2]);
		int gradenum = strs.length >= 4 ? Integer.parseInt(strs[3]) : 0;
		boolean bind = strs.length >= 5 ? strs[4].equals("1") : false;
		String append = strs.length >= 6 ? strs[5] : "";
		int lostTime = strs.length >= 7?Integer.parseInt(strs[6]):0;
		if (num > BackpackManager.getInstance().getAbleAddNum(player, modelid,
				bind, 0)) {
			MessageUtil.notify_player(player, Notifys.ERROR, "包裹空位不足");
			return;
		}
		List<Item> createItems = Item.createItems(modelid, num, bind, lostTime!=0?System.currentTimeMillis()+lostTime*1000l:0, gradenum, append);
		BackpackManager.getInstance().addItems(player, createItems,
				Reasons.GMCOMMAND, Config.getId());
	}

	private void addItem2(Player player, String[] strs) {
		// &additem 装备ID 数量 强化等级 绑定(1绑) 过期时间 附加属性条数;
		int modelid = Integer.parseInt(strs[1]);
		int num = Integer.parseInt(strs[2]);
		int gradenum = strs.length >= 4 ? Integer.parseInt(strs[3]) : 0;
		boolean bind = strs.length >= 5 ? strs[4].equals("1") : false;
		long losttime = strs.length >= 6 && !strs[5].equals("0") ? TimeUtil
				.getDateByString2(strs[5]).getTime() : 0;
		int appendcount = strs.length >= 7 ? Integer.parseInt(strs[6]) : 0;
		if (num > BackpackManager.getInstance().getAbleAddNum(player, modelid,
				bind, 0)) {
			MessageUtil.notify_player(player, Notifys.ERROR, "包裹空位不足");
			return;
		}
		List<Item> createItems = Item.createItems(modelid, num, bind, losttime,
				gradenum, appendcount);
		BackpackManager.getInstance().addItems(player, createItems,
				Reasons.GMCOMMAND, Config.getId());
	}

	private void gold(Player player, String[] strs) {
		if (strs.length >= 2) {
			int goldnum = Integer.parseInt(strs[1]);
			// Gold gold = BackpackManager.getInstance().getGold(player);
			if (goldnum <= 0) {
				ManagerPool.backpackManager.changeGold(player, goldnum,
						Reasons.GMCOMMAND, Config.getId());
			} else {
				ManagerPool.backpackManager.addGold(player, goldnum,
						Reasons.GMCOMMAND, Config.getId());
			}
		}
	}

	private void bindGold(Player player, String[] strs) {
		if (strs.length >= 2) {
			int bindGold = Integer.parseInt(strs[1]);
			if (player != null) {
				ManagerPool.backpackManager.changeBindGold(player, bindGold,
						Reasons.GMCOMMAND, Config.getId());
			}
		}
	}

	private void gotoPlayer(Player player, String[] strs) {
		if (strs.length >= 2) {
			String name = strs[1];
			Player toPlayer = ManagerPool.playerManager
					.getOnlinePlayerByName(name);
			if (toPlayer == null) {
				return;
			}
			ManagerPool.mapManager
					.changeMap(player, toPlayer.getMap(),
							toPlayer.getMapModelId(), toPlayer.getLine(),
							toPlayer.getPosition(), this.getClass().getName()
									+ ".gm 3");
		}
	}

	private void showBlock(Player player) {
		Map map = ManagerPool.mapManager.getMap(player);
		if (map == null) {
			return;
		}
		Grid[][] grids = ManagerPool.mapManager.getMapBlocks(map
				.getMapModelid());
		ResMapBlocksMessage msg = new ResMapBlocksMessage();
		for (int i = 0; i < grids.length; i++) {
			for (int j = 0; j < grids[i].length; j++) {
				msg.getBlocks().add(grids[i][j].getBlock());
			}
		}
		MessageUtil.tell_player_message(player, msg);
	}

	private void testLineGrids(Position pos1, Position pos2, int mapModelId) {
		List<Grid> grids = MapUtils.getLineGrids(pos1, pos2, mapModelId);
		for (int i = 0; i < grids.size(); i++) {
			System.out.println("grid:" + grids.get(i).getX() + ","
					+ grids.get(i).getY());
		}
	}
	private void oneKey2(Player player){
		ArrayList<Integer> guides = new ArrayList<Integer>();
		for (int i = 0; i < 40; i ++){
			guides.add(1);
		}
		player.setGuides(guides);
		PlayerManager.getInstance().addExp(player,
				299999,
				AttributeChangeReason.GM);
	
		ResGetGuidesMessage msg = new ResGetGuidesMessage();
		msg.setGuides(guides);
		MessageUtil.tell_player_message(player, msg);
		
		ManagerPool.backpackManager.changeMoney(player, 1000000000,
				Reasons.GMCOMMAND, Config.getId());
		ManagerPool.backpackManager.addGold(player, 3000,
				Reasons.GMCOMMAND, Config.getId());
		
		ManagerPool.playerAttributeManager.countPlayerAttribute(
				player, PlayerAttributeType.BASE);
		
		player.setHp(player.getMaxHp());
		player.setMp(player.getMaxMp());
		ManagerPool.playerManager.onHpChange(player);
		ManagerPool.playerManager.onMpChange(player);
		
		
		PlayerManager.getInstance().changeJob(player,player.getJob()<=3?TaskConfig.CHANGE_JOB_FINISH_TASK1_1:TaskConfig.CHANGE_JOB_FINISH_TASK2_1);
		
		List<Item> items = new ArrayList<Item>();
		if (player.getJob() == 1 || player.getJob() == 4 || player.getJob() == 7){
			player.setJob((byte)7);
			items.addAll(Item.createItems(2310024, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			items.addAll(Item.createItems(2310024, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			
			
			items.addAll(Item.createItems(1409000, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			items.addAll(Item.createItems(1409000, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			items.addAll(Item.createItems(1405004, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			items.addAll(Item.createItems(500015, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			
			
			items.addAll(Item.createItems(3418107, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			items.addAll(Item.createItems(3417107, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			items.addAll(Item.createItems(3416107, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			items.addAll(Item.createItems(3414107, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			items.addAll(Item.createItems(3413107, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
		}else if(player.getJob() == 2 || player.getJob() == 5 || player.getJob() == 8){
			player.setJob((byte)8);
			items.addAll(Item.createItems(1320010, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			
			
			items.addAll(Item.createItems(1409000, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			items.addAll(Item.createItems(1409000, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			items.addAll(Item.createItems(1405004, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			items.addAll(Item.createItems(500016, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			
			items.addAll(Item.createItems(2320125, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			
			items.addAll(Item.createItems(2423104, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			items.addAll(Item.createItems(2424104, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			items.addAll(Item.createItems(2426104, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			items.addAll(Item.createItems(2427104, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			items.addAll(Item.createItems(2428104, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
		}else if(player.getJob() == 3 || player.getJob() == 6 || player.getJob() == 9){
			player.setJob((byte)9);
			items.addAll(Item.createItems(2330016, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			
			
			items.addAll(Item.createItems(1409000, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			items.addAll(Item.createItems(1409000, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			items.addAll(Item.createItems(1405004, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			
			items.addAll(Item.createItems(500017, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			
			
			
			items.addAll(Item.createItems(2433106, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			items.addAll(Item.createItems(2434106, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			items.addAll(Item.createItems(2436106, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			items.addAll(Item.createItems(2437106, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
			items.addAll(Item.createItems(2438106, 1, true, 0, 15, 7, "45|1000;26|400;27|400;29|4000;31|20;32|200;33|20;34|200;35|7;36|8;37|8;47|1000;49|500;23|400;24|400;25|400;44|400"));
		}
		
		BackpackManager.getInstance().addItems(player, items,
				Reasons.GMCOMMAND, Config.getId());
		
		for (int i = 10001; i < 10030; i++) {
			Q_skill_modelBean model = DataManager.getInstance().q_skill_modelContainer
					.getMap().get(i + "_" + 1);
			if (model != null && PlayerManager.checkJob(player.getJob(), model.getQ_job())) {
				ManagerPool.skillManager.addSkill(player, i);
			}
		}
		
		ManagerPool.horseManager.testHorse(player, 1);
		PlayerManager.getInstance().addExp(player,
				9999999999999l,
				AttributeChangeReason.GM);
		
		
		VipManager.getInstance().buyVIP(player, (byte) 1, (byte)3, null);
		VipManager.getInstance().addExpByGM(player, 999999999);
		ManagerPool.horseManager.testHorse(player,72);
		ManagerPool.playerAttributeManager.countPlayerAttribute(player, PlayerAttributeType.HORSE);
	}
	private void oneKey(Player player){
		ArrayList<Integer> guides = new ArrayList<Integer>();
		for (int i = 0; i < 40; i ++){
			guides.add(1);
		}
		player.setGuides(guides);
		
		ResGetGuidesMessage msg = new ResGetGuidesMessage();
		msg.setGuides(guides);
		MessageUtil.tell_player_message(player, msg);
		
		ManagerPool.backpackManager.changeMoney(player, 1000000000,
				Reasons.GMCOMMAND, Config.getId());
		ManagerPool.backpackManager.addGold(player, 1000000000,
				Reasons.GMCOMMAND, Config.getId());
		BackpackManager.getInstance().changeSpirit(player, 1000000000, 
				Reasons.GMCOMMAND, Config.getId());
		
		for(int i = 0; i < player.getAttibute_one_base().length; i ++){
			player.getAttibute_one_base()[i] = 1000;
		}
		
		ManagerPool.playerAttributeManager.countPlayerAttribute(
				player, PlayerAttributeType.BASE);
		
		player.setHp(player.getMaxHp());
		player.setMp(player.getMaxMp());
		ManagerPool.playerManager.onHpChange(player);
		ManagerPool.playerManager.onMpChange(player);
		
		ManagerPool.playerManager.setLevel(player, 140, true, Reasons.LEVELUPGM);
		
		PlayerManager.getInstance().changeJob(player,player.getJob()<=3?TaskConfig.CHANGE_JOB_FINISH_TASK1_1:TaskConfig.CHANGE_JOB_FINISH_TASK2_1);
		
		List<Item> items = new ArrayList<Item>();
		if (player.getJob() == 1 || player.getJob() == 4 || player.getJob() == 7){
			items.addAll(Item.createItems(1310036, 1, true, 0));
			items.addAll(Item.createItems(1413005, 1, true, 0));
			items.addAll(Item.createItems(1414005, 1, true, 0));
			items.addAll(Item.createItems(1416005, 1, true, 0));
			items.addAll(Item.createItems(1417005, 1, true, 0));
			items.addAll(Item.createItems(1418005, 1, true, 0));
		}else if(player.getJob() == 2 || player.getJob() == 5 || player.getJob() == 8){
			items.addAll(Item.createItems(1320005, 1, true, 0));
			items.addAll(Item.createItems(1423003, 1, true, 0));
			items.addAll(Item.createItems(1424003, 1, true, 0));
			items.addAll(Item.createItems(1426003, 1, true, 0));
			items.addAll(Item.createItems(1427003, 1, true, 0));
			items.addAll(Item.createItems(1428003, 1, true, 0));
		}else if(player.getJob() == 3 || player.getJob() == 6 || player.getJob() == 9){
			items.addAll(Item.createItems(1330011, 1, true, 0));
			items.addAll(Item.createItems(1433004, 1, true, 0));
			items.addAll(Item.createItems(1434004, 1, true, 0));
			items.addAll(Item.createItems(1436004, 1, true, 0));
			items.addAll(Item.createItems(1437004, 1, true, 0));
			items.addAll(Item.createItems(1438004, 1, true, 0));
		}
		
		BackpackManager.getInstance().addItems(player, items,
				Reasons.GMCOMMAND, Config.getId());
		
		for (int i = 10001; i < 10030; i++) {
			Q_skill_modelBean model = DataManager.getInstance().q_skill_modelContainer
					.getMap().get(i + "_" + 1);
			if (model != null && PlayerManager.checkJob(player.getJob(), model.getQ_job())) {
				ManagerPool.skillManager.addSkill(player, i);
			}
		}
		
		ManagerPool.horseManager.testHorse(player, 1);
	}

	private static java.util.Map<String, Integer> commandlevel = new HashMap<String, Integer>();
	static {
		commandlevel.put("&jinyan", 1);
		commandlevel.put("&kick", 1);
		commandlevel.put("&ts", 1);
		commandlevel.put("&totask", 1);
		commandlevel.put("&changemap", 1);
		commandlevel.put("&fangchenmi", 1);
		commandlevel.put("&goto", 1);
		commandlevel.put("&wudi", 1);
		commandlevel.put("&selectline", 1);
		commandlevel.put("&whomapid", 1);
		commandlevel.put("&dazuo", 1);
		commandlevel.put("&mapcount", 1);
		commandlevel.put("&script", 1);
		commandlevel.put("&worldscript", 1);
		commandlevel.put("&loadscript", 1);
		commandlevel.put("&reload", 1);
		commandlevel.put("&worldreload", 1);
		commandlevel.put("&date", 1);
		commandlevel.put("&maxlogin", 1);
		commandlevel.put("&sd", 1);
		commandlevel.put("&worldloadscript", 1);
		commandlevel.put("&inspectplayergold", 1);
	}

	public static void main(String[] args) {
		new Thread(WServer.getInstance()).start();
		try {
			List<String> roleids = new ArrayList<String>();
			SqlSessionFactory sqlMapper = DBServer.getInstance().getSqlMapper();
			SqlSession session = sqlMapper.openSession();
			try {
				Connection conn = session.getConnection();
				PreparedStatement pstmt = conn
						.prepareStatement("SELECT roleid FROM role where level >= 30");

				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					roleids.add(rs.getString("roleid"));
				}
				rs.close();
			} catch (Exception e) {
				log.error(e, e);
			} finally {
				session.close();
			}
			int count = 0, totalplayer = roleids.size();
			RoleDao dao = new RoleDao();
			log.error("total:" + totalplayer);
			for (String roleid : roleids) {
				try { // 对每个角色的操作
						// 获取玩家
					Role role = dao.selectById(Long.valueOf(roleid));
					if (role == null) {
						continue;
					}
					Player loadplayer = (Player) JSONserializable.toObject(
							VersionUpdateUtil.dateLoad(role.getData()),
							Player.class);
					boolean change = false;
					int ontcount = 0;

					Collect collect = loadplayer.getCollect();
					Iterator<String> it = collect.getInfos().keySet()
							.iterator();
					while (it.hasNext()) {
						String key = it.next();
						if (!StringUtil.isNumeric(key)) { // 是垃圾对象
							it.remove();
							change = true;
							ontcount++;
						}
					}
					count++;
					log.error("player " + loadplayer.getName() + " remove "
							+ ontcount + " clearobj:" + count + "/"
							+ totalplayer);
					if (change)
						ManagerPool.playerManager.updatePlayerSync(loadplayer);

					if (count % 100 == 0) {
						// MessageUtil.notify_player(player,
						// Notifys.CHAT_SYSTEM,
						// "garbage:"+totalgarbage+" garbageplayer:"+garbageplayer+" average:"+(totalgarbage/garbageplayer)+" "+count+"/"+inspectplayer);
						Thread.sleep(100); // 休息一下
					}
				} catch (Exception e) {
					log.error(e, e);
				}
			}
			log.error("clean finish!");
			// log.error("garbage:"+totalgarbage+" garbageplayer:"+garbageplayer+" average:"+
			// (garbageplayer==0?0:(totalgarbage/garbageplayer))+" "+count+"/"+inspectplayer);
			// MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM,
			// "garbage:"+totalgarbage+" garbageplayer:"+garbageplayer+" average:"+(garbageplayer==0?0:(totalgarbage/garbageplayer))+" "+count+"/"+inspectplayer);
		} catch (Exception e) {
			log.error(e, e);
		}
	}
}
