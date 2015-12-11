package com.game.guild.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.game.config.Config;
import com.game.country.manager.CountryManager;
import com.game.data.bean.Q_guildbannerBean;
import com.game.data.dao.Q_guildbannerDao;
import com.game.db.bean.GuildBean;
import com.game.db.dao.GuildDao;
import com.game.db.dao.GuildeventDao;
import com.game.db.dao.GuildmemberDao;
import com.game.dblog.LogService;
import com.game.guild.bean.DiplomaticInfo;
import com.game.guild.bean.EventInfo;
import com.game.guild.bean.GuildInfo;
import com.game.guild.bean.GuildShortInfo;
import com.game.guild.bean.MemberInfo;
import com.game.guild.log.GuildLog;
import com.game.guild.message.*;
import com.game.guild.structs.ApplyAndInvite;
import com.game.guild.structs.Guild;
import com.game.guild.structs.GuildBanner;
import com.game.guild.structs.GuildData;
import com.game.json.JSONserializable;
import com.game.languageres.manager.ResManager;
import com.game.mail.manager.MailWorldManager;
import com.game.manager.ManagerPool;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerWorldInfo;
import com.game.prompt.structs.Notifys;
import com.game.server.WorldServer;
import com.game.setupmenu.manager.SetupMenuManager;
import com.game.toplist.manager.TopListManager;
import com.game.toplist.structs.GuildFightPowerTop;
import com.game.utils.MessageUtil;
import com.game.utils.ParseUtil;
import com.game.utils.TimeUtil;

import org.apache.commons.lang.math.RandomUtils;

/**
 * @author xiaozhuoming 战盟管理类(世界服务器)
 */
public class GuildWorldManager {

	private Logger log = Logger.getLogger(GuildWorldManager.class);
	private static Object obj = new Object();
	//战盟管理类实例
	private static GuildWorldManager manager;

	private GuildWorldManager() {
	}

	public static GuildWorldManager getInstance() {
		synchronized (obj) {
			if (manager == null) {
				manager = new GuildWorldManager();
			}
		}
		return manager;
	}
	//战盟数据接口
	private GuildDao guildDao = new GuildDao();
	private GuildmemberDao guildmemberDao = new GuildmemberDao();
	private GuildeventDao guildeventDao = new GuildeventDao();

	public GuildDao getGuildDao() {
		return guildDao;
	}

	public GuildmemberDao getGuildmemberDao() {
		return guildmemberDao;
	}

	public GuildeventDao getGuildeventDao() {
		return guildeventDao;
	}

	public boolean deleteGuild(Guild guild) {
		try {
			if (getGuildDao().delete(guild.getGuildInfo().getGuildId()) == 0) {
				log.error(String.format("战盟数据删除错误，战盟id[%s]", Long.toString(guild.getGuildInfo().getGuildId())));
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void saveGuild(Guild guild) {
		try {
			if (!guild.getMemberinfoHashMap().isEmpty()) {
				GuildBean guildBean = new GuildBean();
				guildBean.setGuildid(guild.getGuildInfo().getGuildId());
				guildBean.setGuildName(guild.getGuildInfo().getGuildName());
				GuildData guildData = new GuildData();
				guildData.toData(guild.getGuildInfo());
				guildBean.setGuilddata(JSONserializable.toString(guildData));
				guildBean.setGuildmsgdata(JSONserializable.toString(guild.getSaveSendMsgList()));
				guildBean.setGuildactivevalue(JSON.toJSONString(guild.getActiveValueList()));
				guildBean.setGuildcalactivevaluetime(guild.getCalActiveValueTime());
				guildBean.setGuildfightpower(guild.getGuildInfo().getMemberFightPower());
				if (getGuildDao().update(guildBean) == 0) {
					if (getGuildDao().insert(guildBean) == 0) {
						log.error(String.format("战盟数据保存错误，战盟id[%s]", Long.toString(guild.getGuildInfo().getGuildId())));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveAllGuild() {
		for (Map.Entry<Long, Guild> entry : getGuildMapManager().entrySet()) {
			Guild guild = entry.getValue();
			if (guild != null) {
				saveGuild(guild);
				guild.saveAll();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void loadallGuild() {
		try {
			List<GuildBean> list = getGuildDao().select();
			Iterator<GuildBean> iterator = list.iterator();
			while (iterator.hasNext()) {
				GuildBean guildBean = (GuildBean) iterator.next();
				if (guildBean != null) {
					Guild guild = new Guild();
					GuildData guildData = (GuildData) JSONserializable.toObject(guildBean.getGuilddata(), GuildData.class);
					GuildInfo guildInfo = guildData.toInfo();
					List<ApplyAndInvite> applyList = new ArrayList<ApplyAndInvite>();
					applyList = (List<ApplyAndInvite>) JSONserializable.toList(guildBean.getGuildmsgdata(), ApplyAndInvite.class);
					List<Long> activeValueList = new ArrayList<Long>();
					activeValueList = JSON.parseArray(guildBean.getGuildactivevalue(), Long.class);
					if (guildInfo != null && applyList != null && activeValueList != null) {
						guild.setGuildInfo(guildInfo);
						guild.setSaveSendMsgList(applyList);
						guild.setActiveValueList(activeValueList);
						guild.setCalActiveValueTime(guildBean.getGuildcalactivevaluetime());
						guild.loadAll();
						if (!guild.getMemberinfoHashMap().isEmpty()) {
							getGuildMapManager().put(guild);
							
							//排行榜初始化
							GuildFightPowerTop fightPowerTop = new GuildFightPowerTop(guild.getGuildInfo().getGuildId(), guild.getGuildInfo().getMemberFightPower(), guild.getGuildInfo().getBannerLevel());
							TopListManager.getInstance().getGuildTopMap().put(fightPowerTop, fightPowerTop.getId());
							
						} else {
							log.error(String.format("战盟成员为零，战盟id[%s]名字[%s]", Long.toString(guildBean.getGuildid()), guildBean.getGuildName()));
						}
					} else {
						log.error(String.format("战盟数据读取错误，战盟id[%s]名字[%s]", Long.toString(guildBean.getGuildid()), guildBean.getGuildName()));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//战盟旗帜配置数据
	private Q_guildbannerDao guildbannerDao = new Q_guildbannerDao();

	public Q_guildbannerDao getGuildbannerDao() {
		return guildbannerDao;
	}
	private HashMap<Integer, GuildBanner> guildBannerMap = new HashMap<Integer, GuildBanner>();

	public HashMap<Integer, GuildBanner> getGuildBannerMap() {
		return guildBannerMap;
	}

	public void loadBannerData() {
		try {
			List<Q_guildbannerBean> list = getGuildbannerDao().select();
			Iterator<Q_guildbannerBean> iterator = list.iterator();
			while (iterator.hasNext()) {
				Q_guildbannerBean guildbannerBean = (Q_guildbannerBean) iterator.next();
				if (guildbannerBean != null) {
					GuildBanner guildBanner = new GuildBanner();
					guildBanner.getBannerInfo(guildbannerBean);
					getGuildBannerMap().put(guildBanner.getGuildbannerlv(), guildBanner);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//战盟HashMapManager
	private class GuildMapManager {

		private HashMap<Long, Guild> guildIdMap = new HashMap<Long, Guild>();			//战盟ID索引
		private HashMap<String, Guild> guildNameMap = new HashMap<String, Guild>();		//战盟名字索引
		private HashMap<String, Guild> guildBannerNameMap = new HashMap<String, Guild>();	//战盟旗帜名字索引

		public HashMap<Long, Guild> getGuildIdMap() {
			return guildIdMap;
		}

		public boolean put(Guild guild) {
			if (!guildIdMap.containsKey(guild.getGuildInfo().getGuildId())) {
				if (!guildNameMap.containsKey(guild.getGuildInfo().getGuildName())) {
					if (!guildBannerNameMap.containsKey(guild.getGuildInfo().getGuildBanner())) {
						guildIdMap.put(guild.getGuildInfo().getGuildId(), guild);
						guildNameMap.put(guild.getGuildInfo().getGuildName(), guild);
						guildBannerNameMap.put(guild.getGuildInfo().getGuildBanner(), guild);
						return true;
					} else {
						guild.getGuildInfo().setGuildBanner(guild.getGuildInfo().getGuildName());
						guildIdMap.put(guild.getGuildInfo().getGuildId(), guild);
						guildNameMap.put(guild.getGuildInfo().getGuildName(), guild);
						guildBannerNameMap.put(guild.getGuildInfo().getGuildBanner(), guild);
						return true;
					}
				}
			}
			return false;
		}

		public boolean remove(Guild guild) {
			if (guildIdMap.remove(guild.getGuildInfo().getGuildId()) != null) {
				if (guildNameMap.remove(guild.getGuildInfo().getGuildName()) != null) {
					if (guildBannerNameMap.remove(guild.getGuildInfo().getGuildBanner()) != null) {
						return true;
					} else {
						guildIdMap.put(guild.getGuildInfo().getGuildId(), guild);
						guildNameMap.put(guild.getGuildInfo().getGuildName(), guild);
					}
				} else {
					guildIdMap.put(guild.getGuildInfo().getGuildId(), guild);
				}
			}
			return false;
		}

		@SuppressWarnings("unused")
		public boolean removeId(Long guildid) {
			Guild guild = guildIdMap.remove(guildid);
			if (guild != null) {
				if (guildNameMap.remove(guild.getGuildInfo().getGuildName()) != null) {
					if (guildBannerNameMap.remove(guild.getGuildInfo().getGuildBanner()) != null) {
						return true;
					} else {
						guildIdMap.put(guild.getGuildInfo().getGuildId(), guild);
						guildNameMap.put(guild.getGuildInfo().getGuildName(), guild);
					}
				} else {
					guildIdMap.put(guild.getGuildInfo().getGuildId(), guild);
				}
			}
			return false;
		}

		@SuppressWarnings("unused")
		public boolean removeName(String guildname) {
			Guild guild = guildNameMap.remove(guildname);
			if (guild != null) {
				if (guildIdMap.remove(guild.getGuildInfo().getGuildId()) != null) {
					if (guildBannerNameMap.remove(guild.getGuildInfo().getGuildBanner()) != null) {
						return true;
					} else {
						guildIdMap.put(guild.getGuildInfo().getGuildId(), guild);
						guildNameMap.put(guild.getGuildInfo().getGuildName(), guild);
					}
				} else {
					guildNameMap.put(guild.getGuildInfo().getGuildName(), guild);
				}
			}
			return false;
		}

		@SuppressWarnings("unused")
		public boolean removeBannerName(String guildBannerName) {
			Guild guild = guildBannerNameMap.remove(guildBannerName);
			if (guild != null) {
				if (guildIdMap.remove(guild.getGuildInfo().getGuildId()) != null) {
					if (guildNameMap.remove(guild.getGuildInfo().getGuildName()) != null) {
						return true;
					} else {
						guildIdMap.put(guild.getGuildInfo().getGuildId(), guild);
						guildBannerNameMap.put(guild.getGuildInfo().getGuildBanner(), guild);
					}
				} else {
					guildBannerNameMap.put(guild.getGuildInfo().getGuildBanner(), guild);
				}
			}
			return false;
		}

		public Guild FindById(Long guildid) {
			return guildIdMap.get(guildid);
		}

		public Guild FindByName(String guildname) {
			return guildNameMap.get(guildname);
		}

		public Guild FindByBannerName(String guildBannerName) {
			return guildBannerNameMap.get(guildBannerName);
		}

		public Set<Map.Entry<Long, Guild>> entrySet() {
			return guildIdMap.entrySet();
		}
	}
	private GuildMapManager guildMapManager = new GuildMapManager();

	private GuildMapManager getGuildMapManager() {
		return guildMapManager;
	}

	public Guild getGuild(long guildid) {
		return getGuildMapManager().FindById(guildid);
	}

	public Set<Map.Entry<Long, Guild>> entrySet() {
		return getGuildMapManager().entrySet();
	}
	//战盟限制条件
	@SuppressWarnings("unused")
	private int Create_Need_Gold = 1000000;	//创建战盟所需金币
	@SuppressWarnings("unused")
	private short Create_Need_Level = 30;		//创建战盟所需等级
	private byte Guild_MaxNum = 30;		//战盟最大人数
//	private String Default_Bulletin = "盟主很懒，什么话都没写，大家一起鄙视他。\n最多100个汉字。";	//默认公告
	private String[] Default_Power = {ResManager.getInstance().getString("盟主"), ResManager.getInstance().getString("副盟主"), ResManager.getInstance().getString("精英"), ResManager.getInstance().getString("普通成员")};			//权限名字
	//错误代码
	private byte Error_Fail = -1;		//失败
	private byte Error_Success = 0;		//成功
	private byte Error_AddGuild = 1;	//广播添加战盟信息
	//通知类型
	public byte Notify_Create = 0;		//创建
	public byte Notify_AddOrUpdate = 1;	//添加或更新
	public byte Notify_Delete = 2;		//删除
	public byte Notity_DiplomaticChange = 3;//外交关系改变
	public byte Notity_AddMember = 4;	//添加战盟成员
	public byte Notity_KingCity = 5;	//王城盟派(领地争夺)
	public byte Notity_DeleteGuild = 6;	//解散盟派
	public byte Notity_ChangeBanner = 7;	//改变盟旗
	public byte Notity_BannerBuff = 8;	//盟旗BUFF
	private List<ApplyAndInvite> applyAndInvitesList = new ArrayList<ApplyAndInvite>();

	public List<ApplyAndInvite> getApplyAndInvitesList() {
		return applyAndInvitesList;
	}

	public void deleteApplyAndInvite(long userid) {
		Iterator<ApplyAndInvite> iterator = getApplyAndInvitesList().iterator();
		while (iterator.hasNext()) {
			ApplyAndInvite applyAndInvite = iterator.next();
			if (applyAndInvite != null) {
				if (applyAndInvite.getType() == ApplyAndInvite.Apply_Type && applyAndInvite.getSrcid() == userid) {
					iterator.remove();
				} else if (applyAndInvite.getType() == ApplyAndInvite.Invite_Type && applyAndInvite.getDestid() == userid) {
					iterator.remove();
				}
			}
		}
	}
	
	public void deleteApplyAndInvite2(long userid) {
		Iterator<ApplyAndInvite> iterator = getApplyAndInvitesList().iterator();
		while (iterator.hasNext()) {
			ApplyAndInvite applyAndInvite = iterator.next();
			if (applyAndInvite != null) {
				if (applyAndInvite.getType() == ApplyAndInvite.Apply_Type && applyAndInvite.getDestid() == userid) {
					iterator.remove();
				}
			}
		}
	}
	
	//--------------------------外部接口------------------------//

	/**
	 * 判断是否王城盟派
	 *
	 * @param guild 盟派
	 * @return true 是 false 不是
	 */
	public boolean checkKingCityGuild(Guild guild) {
		if (!guild.getMemberinfoHashMap().isEmpty()) {
			MemberInfo memberInfo = (MemberInfo) guild.getMemberinfoHashMap().values().toArray()[0];
			if (memberInfo != null) {
				PlayerWorldInfo playerWorldInfo = PlayerManager.getInstance().getPlayerWorldInfo(memberInfo.getUserId());
				if (playerWorldInfo != null) {
					if (CountryManager.kingcitymap.containsKey(playerWorldInfo.getCountry())) {
						long kingguildid = CountryManager.kingcitymap.get(playerWorldInfo.getCountry());
						return guild.getGuildInfo().getGuildId() == kingguildid;
					}
				}
			}
		}
		return false;
	}

	public Guild getGuildByUserId(long userid) {
		for (Map.Entry<Long, Guild> entry : this.entrySet()) {
			Guild guild = entry.getValue();
			if (guild != null && guild.getMemberinfoHashMap().containsKey(userid)) {
				return guild;
			}
		}
		return null;
	}

	public void loginSendGuildAndMemberInfo(Player player) {
		Guild guild = getGuild(player.getGuildid());

		if (guild != null) {
			MemberInfo memberInfo = guild.findMemberInfo(player);
			if (memberInfo != null) {
				guild.calActiveValue(player);
				sendAllGuildShortInfoToSelf(player);
				sendAllMemberInfoToSelf(player, guild);
				sendGuildAndMemberInfo(null, player, Notify_AddOrUpdate, guild);
				sendOtherGuildInfo(Notify_AddOrUpdate, guild, player);
				sendOtherMemberInfo(Notify_AddOrUpdate, guild, player);
				sendGuildInfo(player, Notity_KingCity, guild);
				sendGuildInfo(player, Notity_BannerBuff, guild);
				if (memberInfo.getGuildPowerLevel() == 1) {
					guild.sendIcoMsgList(player);
				}
				log.info(String.format("发送登陆公会信息，是成员，全部发送==%s", JSON.toJSONString(memberInfo)));
				
				//盟主或者副盟主上线，发送申请消息
				if(memberInfo.getGuildPowerLevel() <= 2) {
					List<ApplyAndInvite> applyAndInvitesList = getApplyAndInvitesList();
					for(ApplyAndInvite applyAndInvite : applyAndInvitesList) {
						if(applyAndInvite == null) continue;
						if(applyAndInvite.getType() == ApplyAndInvite.Apply_Type) {
							Player srcPlayer = ManagerPool.playerManager.getPlayer(applyAndInvite.getSrcid());
							Player destPlayer = ManagerPool.playerManager.getPlayer(applyAndInvite.getDestid());
							if(srcPlayer != null && destPlayer != null && destPlayer.getId() == player.getId()) {
								ResGuildApplyAddDoingToClientMessage sendMessage = new ResGuildApplyAddDoingToClientMessage();
								sendMessage.setBtErrorCode(Error_Success);
								sendMessage.setUserId(srcPlayer.getId());
								sendMessage.setApplyLevel((short) srcPlayer.getLevel());
								sendMessage.setApplyName(srcPlayer.getName());
								sendMessage.setApplyModeInfo(srcPlayer.getFriendModeInfo());
								MessageUtil.tell_player_message(destPlayer, sendMessage);
							}
						}
					}
				}
			} else {
				player.setGuildid(0);
				if (player.getLastAfkGuildTime() == 0) {
					player.setLastAfkGuildTime((int) (System.currentTimeMillis() / 1000));
				}
				sendAllGuildShortInfoToSelf(player);
				log.error("发送登陆公会信息，不是成员");
			}
		} else {
			player.setGuildid(0);
			if (player.getLastAfkGuildTime() == 0) {
				player.setLastAfkGuildTime((int) (System.currentTimeMillis() / 1000));
			}
			sendAllGuildShortInfoToSelf(player);
			log.info("发送登陆公会信息，没有公会");
			
			//玩家上线有申请记录，发送申请消息
			List<ApplyAndInvite> applyAndInvitesList = getApplyAndInvitesList();
			for(ApplyAndInvite applyAndInvite : applyAndInvitesList) {
				if(applyAndInvite == null) continue;
				if(applyAndInvite.getType() == ApplyAndInvite.Apply_Type) {
					Player srcPlayer = ManagerPool.playerManager.getPlayer(applyAndInvite.getSrcid());
					Player destPlayer = ManagerPool.playerManager.getPlayer(applyAndInvite.getDestid());
					if(srcPlayer != null && destPlayer != null && srcPlayer.getId() == player.getId()) {
						ResGuildApplyAddDoingToClientMessage sendMessage = new ResGuildApplyAddDoingToClientMessage();
						sendMessage.setBtErrorCode(Error_Success);
						sendMessage.setUserId(srcPlayer.getId());
						sendMessage.setApplyLevel((short) srcPlayer.getLevel());
						sendMessage.setApplyName(srcPlayer.getName());
						sendMessage.setApplyModeInfo(srcPlayer.getFriendModeInfo());
						MessageUtil.tell_player_message(destPlayer, sendMessage);
					}
				}
			}
		}
	}

	public void quitClearGuildOtherData(Player player) {
		Guild guild = getGuild(player.getGuildid());
		if (guild != null) {
			guild.Update();
			deleteApplyAndInvite2(player.getId());
			MemberInfo memberInfo = guild.findMemberInfo(player);
			if (memberInfo != null) {
				guild.setBasicInfo(memberInfo, player, guild.getGuildInfo());
				if (memberInfo.getGuildPowerLevel() == 1) {
					guild.getGuildapplyAndInviteList().clear();
					boardcastGuildInfo(player, guild);
				}
				if (memberInfo.getGuildPowerLevel() == 2) {
					boardcastGuildInfo(player, guild);
				}
				sendGuildAndMemberInfo(null, player, Notify_AddOrUpdate, guild);
				sendOtherGuildInfo(Notify_AddOrUpdate, guild, player);
				sendOtherMemberInfo(Notify_AddOrUpdate, guild, player);
				saveGuildData(3, guild, memberInfo);
			}
		}
	}
	//--------------------------成员函数------------------------//

	public void boardcastGuildInfo(Player player, Guild guild) {
		if (guild != null) {
			ResGuildGetGuildListToClientMessage sendMessage = new ResGuildGetGuildListToClientMessage();
			sendMessage.setBtErrorCode(Error_AddGuild);
			GuildMapManager guildMapManager = getGuildMapManager();
			List<GuildInfo> guildInfoList = new ArrayList<GuildInfo>();
			for(Guild data : guildMapManager.guildIdMap.values()) {
				guildInfoList.add(data.getGuildInfo());
			}
			sendMessage.getGuildList().addAll(guildInfoList);
//			sendMessage.getGuildList().add(guild.getGuildInfo());
			if (player == null) {
				MessageUtil.tell_world_message(sendMessage);
			} else {
				List<Player> players = new ArrayList<Player>();
				for (Map.Entry<Long, Player> entry : PlayerManager.getPlayers().entrySet()) {
					Player allplayer = entry.getValue();
					if (allplayer != null) {
						if (allplayer == player) {
							continue;
						} else {
							players.add(allplayer);
						}
					}
				}
				MessageUtil.tell_player_message(players, sendMessage);
			}
		}
	}

	public void sendGuildInfo(Player player, byte notify, Guild guild) {
		if (player != null) {
			ResInnerGuildAloneGuildInfoToServerMessage guildInfoToServerMessage = new ResInnerGuildAloneGuildInfoToServerMessage();
			guildInfoToServerMessage.setNotifyType(notify);
			guildInfoToServerMessage.setGuildInfo(new GuildInfo());
			if (player.getGuildid() == guild.getGuildInfo().getGuildId()) {
				guildInfoToServerMessage.setGuildInfo(guild.getGuildInfo());
				if (notify == Notity_KingCity) {
					log.info(String.format("发送公会信息=playerid=%s=notify=%d=guildid=%s", String.valueOf(player.getId()), notify, String.valueOf(player.getGuildid())));
				}
			}
			MessageUtil.send_to_game(player, guildInfoToServerMessage);
		}
	}

	public void sendMemberInfo(Player toPlayer, Player player, byte notify, Guild guild) {
		if (player != null) {
			ResInnerGuildAloneMemberInfoToServerMessage memberInfoToServerMessage = new ResInnerGuildAloneMemberInfoToServerMessage();
			memberInfoToServerMessage.setNotifyType(notify);
			memberInfoToServerMessage.setMemberInfo(new MemberInfo());
			MemberInfo memberInfo = guild.findMemberInfo(player);
			if (memberInfo != null) {
				memberInfoToServerMessage.setMemberInfo(memberInfo);
			}
			if (toPlayer != null) {
				MessageUtil.send_to_game(toPlayer, memberInfoToServerMessage);
			} else {
				MessageUtil.send_to_game(player, memberInfoToServerMessage);
			}
		}
	}

	public void sendMemberInfoById(Player toPlayer, long playerId, byte notify, Guild guild) {
		if (toPlayer != null) {
			ResInnerGuildAloneMemberInfoToServerMessage memberInfoToServerMessage = new ResInnerGuildAloneMemberInfoToServerMessage();
			memberInfoToServerMessage.setNotifyType(notify);
			memberInfoToServerMessage.setMemberInfo(new MemberInfo());
			MemberInfo memberInfo = guild.findMemberInfoById(playerId);
			if (memberInfo != null) {
				memberInfoToServerMessage.setMemberInfo(memberInfo);
			}
			MessageUtil.send_to_game(toPlayer, memberInfoToServerMessage);
		}
	}

	public void sendGuildAndMemberInfo(Player toPlayer, Player player, byte notify, Guild guild) {
		if (toPlayer != null) {
			sendGuildInfo(toPlayer, notify, guild);
		} else {
			sendGuildInfo(player, notify, guild);
		}
		sendMemberInfo(toPlayer, player, notify, guild);
	}

	public void sendGuildAndMemberInfoById(Player toPlayer, long playerId, byte notify, Guild guild) {
		if (toPlayer != null) {
			sendGuildInfo(toPlayer, notify, guild);
			sendMemberInfoById(toPlayer, playerId, notify, guild);
		}
	}

	public void sendAllGuildInfo(byte notify, Guild guild) {
		for (Map.Entry<Long, MemberInfo> entry : guild.getMemberinfoHashMap().entrySet()) {
			MemberInfo memberInfo = entry.getValue();
			if (memberInfo != null) {
				Player player = PlayerManager.getInstance().getPlayer(memberInfo.getUserId());
				if (player != null) {
					sendGuildInfo(player, notify, guild);
				}
			}
		}
	}
	
	public void sendAllMemberInfo(byte notify, Guild guild) {
		List<Player> playerList = new ArrayList<Player>();
		ResGuildGetMemberListToClientMessage sendMessage = new ResGuildGetMemberListToClientMessage();
		sendMessage.setBtErrorCode(Error_Success);
		for (Map.Entry<Long, MemberInfo> entry : guild.getMemberinfoHashMap().entrySet()) {
			MemberInfo memberInfo = entry.getValue();
			if (memberInfo != null) {
				Player player = PlayerManager.getInstance().getPlayer(memberInfo.getUserId());
				if (player != null) {
					playerList.add(player);
				}
				sendMessage.getMemberList().add(memberInfo);
			}
		}
		MessageUtil.tell_player_message(playerList, sendMessage);
	}

	public void sendAllGuildAndMemberInfo(byte notify, Guild guild) {
		sendAllGuildInfo(notify, guild);
		sendAllMemberInfo(notify, guild);
	}
	
	public void sendOtherGuildInfo(byte notify, Guild guild, Player destPlayer) {
		for (Map.Entry<Long, MemberInfo> entry : guild.getMemberinfoHashMap().entrySet()) {
			MemberInfo memberInfo = entry.getValue();
			if (memberInfo != null) {
				Player player = PlayerManager.getInstance().getPlayer(memberInfo.getUserId());
				if (player != null && destPlayer != null && player.getId() != destPlayer.getId()) {
					sendGuildInfo(player, notify, guild);
				}
			}
		}
	}
	
	public void sendOtherMemberInfo(byte notify, Guild guild, Player destPlayer) {
		for (Map.Entry<Long, MemberInfo> entry : guild.getMemberinfoHashMap().entrySet()) {
			MemberInfo memberInfo = entry.getValue();
			if (memberInfo != null) {
				Player player = PlayerManager.getInstance().getPlayer(memberInfo.getUserId());
				if (player != null && destPlayer != null && player.getId() != destPlayer.getId()) {
					sendMemberInfo(player, destPlayer, notify, guild);
				}
			}
		}
	}
	
	public void sendOtherMemberInfoById(byte notify, Guild guild, long destId) {
		for (Map.Entry<Long, MemberInfo> entry : guild.getMemberinfoHashMap().entrySet()) {
			MemberInfo memberInfo = entry.getValue();
			if (memberInfo != null) {
				Player player = PlayerManager.getInstance().getPlayer(memberInfo.getUserId());
				if (player != null && player.getId() != destId) {
					sendMemberInfoById(player, destId, notify, guild);
				}
			}
		}
	}

	public void sendAllGuildInfoToSelf(Player player) {
		ResGuildGetGuildListToClientMessage sendMessage = new ResGuildGetGuildListToClientMessage();
		sendMessage.setBtErrorCode(Error_Fail);
		for (Map.Entry<Long, Guild> entry : getGuildMapManager().getGuildIdMap().entrySet()) {
			Guild guild = entry.getValue();
			if (guild != null) {
				guild.Update();
				sendMessage.getGuildList().add(guild.getGuildInfo());
			}
		}
		sendMessage.setBtErrorCode(Error_Success);
		MessageUtil.tell_player_message(player, sendMessage);
	}

	public void sendAllMemberInfoToSelf(Player player, Guild guild) {
		if (guild != null) {
			ResGuildGetMemberListToClientMessage sendMessage = new ResGuildGetMemberListToClientMessage();
			sendMessage.setBtErrorCode(Error_Fail);
			guild.Update();
			for (Map.Entry<Long, MemberInfo> entry : guild.getMemberinfoHashMap().entrySet()) {
				MemberInfo memberInfo = entry.getValue();
				if (memberInfo != null) {
					sendMessage.getMemberList().add(memberInfo);
				}
			}
			sendMessage.setBtErrorCode(Error_Success);
			MessageUtil.tell_player_message(player, sendMessage);
		}
	}

	public void sendAllGuildAndMemberInfoToSelf(Player player, Guild guild) {
		sendAllGuildInfoToSelf(player);
		sendAllMemberInfoToSelf(player, guild);
	}

	public void sendAllGuildShortInfoToSelf(Player player) {
		ResGuildGetGuildShortInfoListToClientMessage sendMessage = new ResGuildGetGuildShortInfoListToClientMessage();
		sendMessage.setBtErrorCode(Error_Fail);
		for (Map.Entry<Long, Guild> entry : getGuildMapManager().getGuildIdMap().entrySet()) {
			Guild guild = entry.getValue();
			if (guild != null) {
				GuildShortInfo guildShortInfo = new GuildShortInfo();
				guildShortInfo.setGuildId(guild.getGuildInfo().getGuildId());
				guildShortInfo.setGuildName(guild.getGuildInfo().getGuildName());
				sendMessage.getGuildShortInfoList().add(guildShortInfo);
			}
		}
		sendMessage.setBtErrorCode(Error_Success);
		MessageUtil.tell_player_message(player, sendMessage);
		log.info(String.format("%s 发送 公会短信息列表 (WORLD)", player.getName()));
	}

	public void saveGuildData(int saveType, Guild guild, MemberInfo memberInfo) {
		if (guild != null) {
			if ((saveType & 1) != 0) {
				saveGuild(guild);
			}
			if ((saveType & 2) != 0) {
				if (memberInfo != null) {
					guild.saveMember(memberInfo);
				}
			}
		}
	}

	public void deleteGuildData(int deleteType, Guild guild, MemberInfo memberInfo) {
		if (guild != null) {
			if ((deleteType & 1) != 0) {
				deleteGuild(guild);
			}
			if ((deleteType & 2) != 0) {
				if (memberInfo != null) {
					guild.deleteMember(memberInfo);
				}
			}
		}
	}

	public String getEventString(String eventType, Object... values) {
		try {
			if (eventType.equals(ResManager.getInstance().getString("创建战盟"))) {
				return String.format(ResManager.getInstance().getString("[%s]战盟被{@}创建。"), values);
			} else if (eventType.equals(ResManager.getInstance().getString("添加成员"))) {
				return String.format(ResManager.getInstance().getString("欢迎{@}玩家加入本盟，我盟变得更强大了"), values);
			} else if (eventType.equals(ResManager.getInstance().getString("逐出成员"))) {
				return String.format(ResManager.getInstance().getString("{@}玩家被[%s]逐出了战盟"), values);
			} else if (eventType.equals(ResManager.getInstance().getString("退出战盟"))) {
				return String.format(ResManager.getInstance().getString("{@}玩家离开了我们的战盟"), values);
			} else if (eventType.equals(ResManager.getInstance().getString("调整职务"))) {
				return String.format(ResManager.getInstance().getString("本盟[%s]变更为：{@}"), values);
			} else if (eventType.equals(ResManager.getInstance().getString("调整昵称"))) {
				return String.format(ResManager.getInstance().getString("{@}玩家的昵称被“%s”修改为：%s"), values);
			} else if (eventType.equals(ResManager.getInstance().getString("调整分组头衔"))) {
				return String.format(ResManager.getInstance().getString("{@}玩家的分组头衔被“%s”修改为：%s"), values);
			} else if (eventType.equals(ResManager.getInstance().getString("行会结盟"))) {
				return String.format(ResManager.getInstance().getString("恭喜恭喜，您的战盟与%s战盟结成联盟，并肩战斗"), values);
			} else if (eventType.equals(ResManager.getInstance().getString("本盟添加其他行会敌对"))) {
				return String.format(ResManager.getInstance().getString("本战盟已将%s战盟列为敌对战盟"), values);
			} else if (eventType.equals(ResManager.getInstance().getString("本盟被其他行会加为敌对"))) {
				return String.format(ResManager.getInstance().getString("%s战盟已将本盟列为敌对战盟"), values);
			} else if (eventType.equals(ResManager.getInstance().getString("成员被其他玩家击败"))) {
				return String.format(ResManager.getInstance().getString("本盟成员{@}玩家名在地图【{@}】被玩家【{@}】击败"), values);
			} else if (eventType.equals(ResManager.getInstance().getString("成员击败其他玩家"))) {
				return String.format(ResManager.getInstance().getString("本盟成员{@}玩家名在地图【{@}】击败了玩家【{@}】"), values);
			} else if (eventType.equals(ResManager.getInstance().getString("成员击败BOSS"))) {
				return String.format(ResManager.getInstance().getString("本盟成员{@}玩家名在地图【{@}】击败BOSS【%s】"), values);
			} else if (eventType.equals(ResManager.getInstance().getString("成员被BOSS击败"))) {
				return String.format(ResManager.getInstance().getString("本盟成员{@}玩家名在地图【{@}】被BOSS【%s】击败"), values);
			} else if (eventType.equals(ResManager.getInstance().getString("盟贡捐献"))) {
				return String.format(ResManager.getInstance().getString("感谢{@}玩家向盟贡仓库提交了：<font color='#18ee1d'>%s*%d</font>，获得<font color='#e028c4'>盟贡点：%d</font>"), values);
			} else if (eventType.equals(ResManager.getInstance().getString("盟旗改名"))) {
				return String.format(ResManager.getInstance().getString("本盟盟主启用了新的盟旗名称：%s，消耗盟贡仓库：%s*%d；%s*%d；%s*%d；%s*%d；%s*%d"), values);
			} else if (eventType.equals(ResManager.getInstance().getString("盟旗更换造型"))) {
				return String.format(ResManager.getInstance().getString("本盟盟主启用了新的盟旗造型，消耗盟贡仓库：%s*%d；%s*%d；%s*%d；%s*%d；%s*%d"), values);
			} else if (eventType.equals(ResManager.getInstance().getString("盟旗升级"))) {
				return String.format(ResManager.getInstance().getString("本盟盟旗已升级到[%d]级，消耗盟贡仓库：%s*%d；%s*%d；%s*%d；%s*%d；%s*%d"), values);
			} else if (eventType.equals(ResManager.getInstance().getString("占领旗帜消耗盟贡"))) {
				return String.format(ResManager.getInstance().getString("成功占领[%s]的旗帜，消耗盟贡仓库：%s*%d；%s*%d；%s*%d；%s*%d；%s*%d"), values);
			}
		} catch (Exception e) {
			log.error(String.format("得到事件字符串出错 类型[%s]", eventType));
		}
		return "";
	}

	/**
	 * 创建战盟
	 *
	 * @param message 创建战盟消息
	 * @return
	 */
	public void reqInnerGuildCreateToWorld(ReqInnerGuildCreateToWorldMessage message) {
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
		if (player != null) {
			ResGuildCreateToClientMessage sendMessage = new ResGuildCreateToClientMessage();
			sendMessage.setBtErrorCode(Error_Fail);
			String guildName = String.format(ResManager.getInstance().getString("[%d区]%s"), WorldServer.getGameConfig().getServerByCountry(player.getCountry()), message.getGuildName());
			if (getGuildMapManager().FindByName(guildName) == null) {
				if (getGuildMapManager().FindByBannerName(message.getGuildBanner()) == null) {
					Guild guild = new Guild();
					guild.setCountry(player.getCountry());
					guild.setCalActiveValueTime(System.currentTimeMillis());
					guild.getGuildInfo().setGuildId(Config.getId());
					guild.getGuildInfo().setGuildName(guildName);
					guild.getGuildInfo().setGuildBanner(message.getGuildBanner());
					guild.getGuildInfo().setBannerIcon(message.getGuildBannerIcon());
					guild.getGuildInfo().setBannerLevel((byte) 1);
					guild.getGuildInfo().setGuildBulletin("");
					guild.setBangZhuInfo(guild.getGuildInfo(), player);
					guild.getGuildInfo().setAutoGuildAgreeAdd((byte) 1);
					guild.getGuildInfo().setCreateTime((int) (System.currentTimeMillis() / 1000));
					guild.getGuildInfo().setCreateIp(player.getIpString());
					if (guild.addMember(player, (byte) 1)) {
						if (getGuildMapManager().FindByBannerName(guild.getGuildInfo().getGuildBanner()) == null && getGuildMapManager().put(guild)) {
							guild.calFightPowerAndMemberNum();
							guild.calActiveValue(player);
							guild.addParseEvent(ResManager.getInstance().getString("创建战盟"), getEventString(ResManager.getInstance().getString("创建战盟"), guild.getGuildInfo().getGuildName()), new ParseUtil.PlayerParm(player.getId(), player.getName()));
							saveGuildData(7, guild, guild.findMemberInfo(player));

							sendMessage.setBtErrorCode(Error_Success);
							sendGuildAndMemberInfo(null, player, Notify_Create, guild);
							MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("恭喜您，战盟创建成功，赶快去招募成员吧"));
							MessageUtil.notify_All_player(Notifys.SROLL, ResManager.getInstance().getString("江湖风起云涌，一个全新的战盟：【{1}】成立了"), guild.getGuildInfo().getGuildName());

							boardcastGuildInfo(null, guild);
						} else {
							guild.removeMember(player);
							MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，创建战盟失败"));
						}
					} else {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，添加战盟成员失败"));
					}
				} else {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，盟旗名字已被其他战盟占用"));
				}
			} else {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，战盟名字已被其他战盟占用"));
			}
			MessageUtil.tell_player_message(player, sendMessage);
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
		}
	}

	/**
	 * 玩家自动同意加入战盟
	 *
	 * @param message 玩家自动同意加入战盟消息
	 * @return
	 */
	public void reqInnerGuildAutoArgeeAddGuildToWorld(ReqInnerGuildAutoArgeeAddGuildToWorldMessage message) {
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
		if (player != null) {
			ResGuildAutoArgeeAddGuildToClientMessage sendMessage = new ResGuildAutoArgeeAddGuildToClientMessage();
			sendMessage.setBtErrorCode(Error_Fail);
			if (message.getAutoArgeeAddGuild() == 0) {
				player.setAutoArgeeAddGuild((byte) 0);//不同意
			} else {
				player.setAutoArgeeAddGuild((byte) 1);//同意
			}
			sendMessage.setBtErrorCode(Error_Success);
			MessageUtil.tell_player_message(player, sendMessage);
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
		}
	}

	/**
	 * 获取公会列表
	 *
	 * @param message 获取公会列表消息
	 * @return
	 */
	public void reqInnerGuildGetGuildListToWorld(ReqInnerGuildGetGuildListToWorldMessage message) {
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
		if (player != null) {
			ResGuildGetGuildListToClientMessage sendMessage = new ResGuildGetGuildListToClientMessage();
			sendMessage.setBtErrorCode(Error_Fail);
			for (Map.Entry<Long, Guild> entry : getGuildMapManager().getGuildIdMap().entrySet()) {
				Guild guild = entry.getValue();
				if (guild != null && !guild.getMemberinfoHashMap().isEmpty()) {
					guild.Update();
					sendMessage.getGuildList().add(guild.getGuildInfo());
				}
			}
			sendMessage.setBtErrorCode(Error_Success);
			MessageUtil.tell_player_message(player, sendMessage);
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
		}
	}

	/**
	 * 玩家申请加入战盟
	 *
	 * @param message 通知世界服务器申请加入战盟消息
	 * @return
	 */
	public void reqInnerGuildApplyAddToWorld(ReqInnerGuildApplyAddToWorldMessage message) {
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
		if (player != null) {
			if (player.getGuildid() == 0) {
				Guild guild = getGuildMapManager().FindById(message.getGuildId());
				if (guild != null) {
					MemberInfo memberInfo = guild.getPowerLevelMemberInfo(1);
					if (memberInfo != null) {
						PlayerWorldInfo playerWorldInfo = PlayerManager.getInstance().getPlayerWorldInfo(memberInfo.getUserId());
						if (playerWorldInfo != null) {
							guild.setCountry(playerWorldInfo.getCountry());
						}
					}
					if (guild.getCountry() == player.getCountry()) {
						if (guild.getMemberinfoHashMap().size() < Guild_MaxNum) {
							if (guild.getGuildInfo().getAutoGuildAgreeAdd() == 1) {
								if (guild.addMember(player, (byte) 4)) {
									guild.calFightPowerAndMemberNum();
									guild.calActiveValue(player);
									guild.addParseEvent(ResManager.getInstance().getString("添加成员"), getEventString(ResManager.getInstance().getString("添加成员")), new ParseUtil.PlayerParm(player.getId(), player.getName()));
									saveGuildData(7, guild, guild.findMemberInfo(player));

									sendGuildAndMemberInfo(null, player, Notity_AddMember, guild);
									sendOtherGuildInfo(Notify_AddOrUpdate, guild, player);
									sendOtherMemberInfo(Notify_AddOrUpdate, guild, player);
									sendGuildInfo(player, Notity_KingCity, guild);
									sendGuildInfo(player, Notity_BannerBuff, guild);
//									MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("恭喜，对方盟派同意了您的入盟请求"));
									guild.sendAllMemberNotify(Notifys.SUCCESS, getParseString("欢迎玩家<font color='#FFDE00'>{@}</font>加入本盟，我盟变得更强大了", player));
									guild.sendAllMemberNotify(Notifys.CHAT_GROUP, getParseString("欢迎玩家<font color='#FFDE00'>{@}</font>加入本盟，我盟变得更强大了", player));
								}
							} else {
								Player addPermissionsPlayer = guild.getPowerLevelPlayer(1);
								Player vicePlayer = guild.getPowerLevelPlayer(2);
								if(addPermissionsPlayer != null) {
									//给盟主发送申请消息
									ResGuildApplyAddDoingToClientMessage sendBangzhuMessage = new ResGuildApplyAddDoingToClientMessage();
									sendBangzhuMessage.setBtErrorCode(Error_Success);
									sendBangzhuMessage.setUserId(player.getId());
									sendBangzhuMessage.setApplyLevel((short) player.getLevel());
									sendBangzhuMessage.setApplyName(player.getName());
									sendBangzhuMessage.setApplyModeInfo(player.getFriendModeInfo());
									MessageUtil.tell_player_message(addPermissionsPlayer, sendBangzhuMessage);
									MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("您的入盟申请已发送，请等待对方盟主或副盟主回应"));

									ApplyAndInvite applyAndInviteToBangzhu = new ApplyAndInvite();
									applyAndInviteToBangzhu.setGuildid(guild.getGuildInfo().getGuildId());
									applyAndInviteToBangzhu.setType(ApplyAndInvite.Apply_Type);
									applyAndInviteToBangzhu.setSrcid(player.getId());
									applyAndInviteToBangzhu.setDestid(addPermissionsPlayer.getId());
									getApplyAndInvitesList().add(applyAndInviteToBangzhu);
								}
								if(vicePlayer != null) {
									//给副盟主发送申请消息
									ResGuildApplyAddDoingToClientMessage sendViceBangzhuMessage = new ResGuildApplyAddDoingToClientMessage();
									sendViceBangzhuMessage.setBtErrorCode(Error_Success);
									sendViceBangzhuMessage.setUserId(player.getId());
									sendViceBangzhuMessage.setApplyLevel((short) player.getLevel());
									sendViceBangzhuMessage.setApplyName(player.getName());
									sendViceBangzhuMessage.setApplyModeInfo(player.getFriendModeInfo());
									MessageUtil.tell_player_message(vicePlayer, sendViceBangzhuMessage);

									ApplyAndInvite applyAndInviteToViceBangzhu = new ApplyAndInvite();
									applyAndInviteToViceBangzhu.setGuildid(guild.getGuildInfo().getGuildId());
									applyAndInviteToViceBangzhu.setType(ApplyAndInvite.Apply_Type);
									applyAndInviteToViceBangzhu.setSrcid(player.getId());
									applyAndInviteToViceBangzhu.setDestid(vicePlayer.getId());
									getApplyAndInvitesList().add(applyAndInviteToViceBangzhu);
								}
								if (addPermissionsPlayer == null && vicePlayer == null) {
									MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，对方盟主/副盟主均不在线"));
								}
							}
						} else {
							MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，对方战盟人数已满，无法招收更多成员了"));
						}
					} else {
						/*xiaozhuoming: 暂时没有用到
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，不同国家不能加入同一个战盟"));
						*/
					}
				} else {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，无法找到该战盟"));
				}
			} else {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您已加入战盟，如需换盟则需先退出战盟"));
			}
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
		}
	}

	/**
	 * 战盟邀请玩家加入
	 *
	 * @param message 通知世界服务器邀请加入战盟消息
	 * @return
	 */
	public void reqInnerGuildInviteAddToWorld(ReqInnerGuildInviteAddToWorldMessage message) {
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
		if (player != null) {
			if (player.getId() != message.getUserId()) {
				if (player.getGuildid() != 0) {
					Guild guild = getGuildMapManager().FindById(player.getGuildid());
					if (guild != null) {
						MemberInfo memberInfo = guild.findMemberInfo(player);
						if (memberInfo != null) {
							if (memberInfo.getGuildPowerLevel() == 1 || memberInfo.getGuildPowerLevel() == 2) {
								if (guild.getMemberinfoHashMap().size() < Guild_MaxNum) {
									Player invitePlayer = PlayerManager.getInstance().getPlayer(message.getUserId());
									if (invitePlayer != null) {
										if (SetupMenuManager.getInstance().isBanGuild(invitePlayer)) {
											MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，该玩家已经设置禁止盟派邀请。"));
											return;
										}
										if (player.getCountry() == invitePlayer.getCountry()) {
											if (invitePlayer.getGuildid() == 0) {
												if (invitePlayer.getAutoArgeeAddGuild() == 1) {
													if (guild.addMember(invitePlayer, (byte) 4)) {
														guild.calFightPowerAndMemberNum();
														guild.calActiveValue(invitePlayer);
														guild.addParseEvent(ResManager.getInstance().getString("添加成员"), getEventString(ResManager.getInstance().getString("添加成员")), new ParseUtil.PlayerParm(invitePlayer.getId(), invitePlayer.getName()));
														saveGuildData(7, guild, guild.findMemberInfo(invitePlayer));

														sendGuildAndMemberInfo(null, invitePlayer, Notity_AddMember, guild);
														sendOtherGuildInfo(Notify_AddOrUpdate, guild, invitePlayer);
														sendOtherMemberInfo(Notify_AddOrUpdate, guild, invitePlayer);
//														sendGuildAndMemberInfo(player, invitePlayer, Notify_AddOrUpdate, guild);
														sendGuildInfo(invitePlayer, Notity_KingCity, guild);
														sendGuildInfo(invitePlayer, Notity_BannerBuff, guild);
//														MessageUtil.notify_player(invitePlayer, Notifys.SUCCESS, ResManager.getInstance().getString("恭喜，您成功加入了{1}战盟"), guild.getGuildInfo().getGuildName());
//														MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("恭喜，{1}玩家同意了您的战盟邀请"), invitePlayer.getName());
														guild.sendAllMemberNotify(Notifys.SUCCESS, getParseString("欢迎玩家<font color='#FFDE00'>{@}</font>加入本盟，我盟变得更强大了", invitePlayer));
														guild.sendAllMemberNotify(Notifys.CHAT_GROUP, getParseString("欢迎玩家<font color='#FFDE00'>{@}</font>加入本盟，我盟变得更强大了", invitePlayer));
													} else {
														MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，加入战盟失败，该玩家可能已经加入了本战盟"));
													}
												} else {
													ResGuildInviteAddDoingToClientMessage sendMessage = new ResGuildInviteAddDoingToClientMessage();
													sendMessage.setBtErrorCode(Error_Success);
													sendMessage.setGuildId(guild.getGuildInfo().getGuildId());
													sendMessage.setGuildName(guild.getGuildInfo().getGuildName());
													sendMessage.setInviteLevel((short) player.getLevel());
													sendMessage.setInviteName(player.getName());
													sendMessage.setInviteModeInfo(player.getFriendModeInfo());
													MessageUtil.tell_player_message(invitePlayer, sendMessage);
													MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("您的入盟邀请已发送，请等待对方玩家回应"));

													ApplyAndInvite applyAndInvite = new ApplyAndInvite();
													applyAndInvite.setGuildid(guild.getGuildInfo().getGuildId());
													applyAndInvite.setType(ApplyAndInvite.Invite_Type);
													applyAndInvite.setSrcid(player.getId());
													applyAndInvite.setDestid(invitePlayer.getId());
													getApplyAndInvitesList().add(applyAndInvite);
												}
											} else {
												if (invitePlayer.getGuildid() == guild.getGuildInfo().getGuildId()) {
													MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("该玩家已经在您的战盟中，无需重复邀请"));
												} else {
													MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，该玩家已加入了其他战盟，可先通知其退盟"));
												}
											}
										} else {
											/*xiaozhuoming: 暂时没有用到
											MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，不同国家不能加入同一个战盟"));
											*/
										}
									} else {
										MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，没有找到被邀请的玩家"));
									}
								} else {
									MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，战盟最大成员上限为100人，目前已达上限"));
								}
							} else {
								MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，您没有权限进行此项操作"));
							}
						} else {
							MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，无法找到该成员"));
						}
					} else {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，无法找到该战盟"));
					}
				} else {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，您没有加入战盟"));
				}
			} else {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，您无法邀请自己加入战盟"));
			}
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
		}
	}

	/**
	 * 获得战盟成员列表
	 *
	 * @param message 通知世界服务器获取成员列表
	 * @return
	 */
	public void reqInnerGuildGetMemberListToWorld(ReqInnerGuildGetMemberListToWorldMessage message) {
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
		if (player != null) {
			ResGuildGetMemberListToClientMessage sendMessage = new ResGuildGetMemberListToClientMessage();
			sendMessage.setBtErrorCode(Error_Fail);
			if (player.getGuildid() == message.getGuildId()) {
				Guild guild = getGuildMapManager().FindById(player.getGuildid());
				if (guild != null) {
					guild.Update();
					for (Map.Entry<Long, MemberInfo> entry : guild.getMemberinfoHashMap().entrySet()) {
						MemberInfo memberInfo = entry.getValue();
						if (memberInfo != null) {
							sendMessage.getMemberList().add(memberInfo);
						}
					}
					sendMessage.setBtErrorCode(Error_Success);
					sendGuildInfo(player, Notify_AddOrUpdate, guild);
				} else {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，没有找到该战盟"));
				}
			} else {
				if (message.getGuildId() == -1) {
					Guild guild = getGuildMapManager().FindById(player.getGuildid());
					if (guild != null) {
						sendGuildInfo(player, Notify_AddOrUpdate, guild);
						return;
					} else {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，没有找到该战盟"));
					}
				} else {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，不能获得其他战盟的成员列表"));
				}
			}
			MessageUtil.tell_player_message(player, sendMessage);
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
		}
	}

	/**
	 * 添加战盟成员
	 *
	 * @param message 通知世界服务器添加战盟成员
	 * @return
	 */
	public void reqInnerGuildAddMemberToWorld(ReqInnerGuildAddMemberToWorldMessage message) {
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
		Player destPlayer = PlayerManager.getInstance().getPlayer(message.getUserId());
		if (player != null && destPlayer != null) {
			ResGuildAddMemberToClientMessage sendMessage = new ResGuildAddMemberToClientMessage();
			sendMessage.setBtErrorCode(Error_Fail);
			if (player.getCountry() == destPlayer.getCountry()) {
				Iterator<ApplyAndInvite> iterator = getApplyAndInvitesList().iterator();
				while (iterator.hasNext()) {
					ApplyAndInvite applyAndInvite = iterator.next();
					if (applyAndInvite != null && applyAndInvite.getSrcid() == destPlayer.getId() && applyAndInvite.getDestid() == player.getId()) {
						if (applyAndInvite.getType() == ApplyAndInvite.Apply_Type) {//player 战盟管理人员 destplayer 申请加入玩家
							if (message.getArgee() == 0) {
								Guild guild = getGuildMapManager().FindById(player.getGuildid());
								if (guild != null) {
									MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您拒绝了来自{1}玩家的入盟申请"), destPlayer.getName());
									MessageUtil.notify_player(destPlayer, Notifys.ERROR, ResManager.getInstance().getString("很遗憾，{1}战盟拒绝了您的战盟申请"), guild.getGuildInfo().getGuildName());
								}
								iterator.remove();
							} else {
								if (applyAndInvite.getGuildid() == message.getGuildId()) {
									Guild guild = getGuildMapManager().FindById(applyAndInvite.getGuildid());
									if (guild != null && guild.getGuildInfo().getGuildId() == player.getGuildid()) {
										MemberInfo memberInfo = guild.findMemberInfo(player);
										if (memberInfo != null && (memberInfo.getGuildPowerLevel() == 1 || memberInfo.getGuildPowerLevel() == 2)) {
											if (destPlayer.getGuildid() == 0) {
												if (guild.getMemberinfoHashMap().size() < Guild_MaxNum) {
													if (guild.addMember(destPlayer, (byte) 4)) {
														guild.calFightPowerAndMemberNum();
														guild.calActiveValue(destPlayer);
														guild.addParseEvent(ResManager.getInstance().getString("添加成员"), getEventString(ResManager.getInstance().getString("添加成员")), new ParseUtil.PlayerParm(destPlayer.getId(), destPlayer.getName()));
														saveGuildData(7, guild, guild.findMemberInfo(destPlayer));

														sendGuildAndMemberInfo(null, destPlayer, Notity_AddMember, guild);
														sendOtherGuildInfo(Notify_AddOrUpdate, guild, destPlayer);
														sendOtherMemberInfo(Notify_AddOrUpdate, guild, destPlayer);
//														sendGuildAndMemberInfo(player, destPlayer, Notify_AddOrUpdate, guild);
														sendGuildInfo(destPlayer, Notity_KingCity, guild);
														sendGuildInfo(destPlayer, Notity_BannerBuff, guild);
//														MessageUtil.notify_player(destPlayer, Notifys.SUCCESS, ResManager.getInstance().getString("恭喜，对方盟派同意了您的入盟请求"));
														guild.sendAllMemberNotify(Notifys.SUCCESS, getParseString("欢迎玩家<font color='#FFDE00'>{@}</font>加入本盟，我盟变得更强大了", destPlayer));
														guild.sendAllMemberNotify(Notifys.CHAT_GROUP, getParseString("欢迎玩家<font color='#FFDE00'>{@}</font>加入本盟，我盟变得更强大了", destPlayer));
														sendMessage.setBtErrorCode(Error_Success);

														deleteApplyAndInvite(destPlayer.getId());
													}
												} else {
													MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，战盟最大成员上限为100人，目前已达上限"));
													MessageUtil.notify_player(destPlayer, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，对方战盟人数已满，无法招收更多成员了"));
												}
											} else {
												if (destPlayer.getGuildid() == guild.getGuildInfo().getGuildId()) {
													MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("该玩家已经在您的战盟中"));
												} else {
													MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，对方已加入了其他战盟"));
												}
											}
										} else {
											MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，您的权限不够"));
										}
									} else {
										MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，无法找到该战盟"));
									}
								}
							}
						} else if (applyAndInvite.getType() == ApplyAndInvite.Invite_Type) {//player 邀请加入玩家 destplayer 战盟管理人员
							if (message.getArgee() == 0) {
								Guild guild = getGuildMapManager().FindById(destPlayer.getGuildid());
								if (guild != null) {
									MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您拒绝了来自{1}战盟的入盟邀请"), guild.getGuildInfo().getGuildName());
									MessageUtil.notify_player(destPlayer, Notifys.ERROR, ResManager.getInstance().getString("很遗憾，{1}玩家拒绝了您的战盟邀请"), player.getName());
								}
								iterator.remove();
							} else {
								if (applyAndInvite.getGuildid() == message.getGuildId()) {
									Guild guild = getGuildMapManager().FindById(applyAndInvite.getGuildid());
									if (guild != null && guild.getGuildInfo().getGuildId() == destPlayer.getGuildid()) {
										MemberInfo memberInfo = guild.findMemberInfo(destPlayer);
										if (memberInfo != null && (memberInfo.getGuildPowerLevel() == 1 || memberInfo.getGuildPowerLevel() == 2)) {
											if (player.getGuildid() == 0) {
												if (guild.getMemberinfoHashMap().size() < Guild_MaxNum) {
													if (guild.addMember(player, (byte) 4)) {
														guild.calFightPowerAndMemberNum();
														guild.calActiveValue(player);
														guild.addParseEvent(ResManager.getInstance().getString("添加成员"), getEventString(ResManager.getInstance().getString("添加成员")), new ParseUtil.PlayerParm(player.getId(), player.getName()));
														saveGuildData(7, guild, guild.findMemberInfo(player));

														sendGuildAndMemberInfo(null, player, Notity_AddMember, guild);
														sendOtherGuildInfo(Notify_AddOrUpdate, guild, player);
														sendOtherMemberInfo(Notify_AddOrUpdate, guild, player);
//														sendGuildAndMemberInfo(destPlayer, player, Notify_AddOrUpdate, guild);
														sendGuildInfo(player, Notity_KingCity, guild);
														sendGuildInfo(player, Notity_BannerBuff, guild);
//														MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("恭喜，您成功加入了{1}战盟"), guild.getGuildInfo().getGuildName());
//														MessageUtil.notify_player(destPlayer, Notifys.SUCCESS, ResManager.getInstance().getString("恭喜，{1}玩家同意了您的战盟邀请"), player.getName());
														guild.sendAllMemberNotify(Notifys.SUCCESS, getParseString("欢迎玩家<font color='#FFDE00'>{@}</font>加入本盟，我盟变得更强大了", player));
														guild.sendAllMemberNotify(Notifys.CHAT_GROUP, getParseString("欢迎玩家<font color='#FFDE00'>{@}</font>加入本盟，我盟变得更强大了", player));
														sendMessage.setBtErrorCode(Error_Success);

														deleteApplyAndInvite(player.getId());
													}
												} else {
													MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，对方战盟人数已满，无法招收更多成员了"));
												}
											} else {
												if (player.getGuildid() == guild.getGuildInfo().getGuildId()) {
													MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您已经在这个战盟中"));
												} else {
													MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，您已加入了其他战盟"));
												}
											}
										} else {
											MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，权限不够"));
										}
									} else {
										MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，无法找到该战盟"));
									}
								}
							}
						}
						break;
					}
				}
			} else {
				/*xiaozhuoming: 暂时没有用到
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，不同国家不能加入同一个战盟"));
				MessageUtil.notify_player(destPlayer, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，不同国家不能加入同一个战盟"));
				*/
			}
			MessageUtil.tell_player_message(player, sendMessage);
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
		}
	}

	/**
	 * 退出战盟
	 *
	 * @param message 通知世界服务器退出战盟
	 * @return
	 */
	public void reqInnerGuildQuitToWorld(ReqInnerGuildQuitToWorldMessage message) {
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
		if (player != null) {
			ResGuildQuitToClientMessage sendMessage = new ResGuildQuitToClientMessage();
			sendMessage.setBtErrorCode(Error_Fail);
			if (player.getGuildid() == message.getGuildId()) {
				Guild guild = getGuildMapManager().FindById(player.getGuildid());
				if (guild != null) {
					MemberInfo memberInfo = guild.findMemberInfo(player);
					if (memberInfo != null && memberInfo.getGuildPowerLevel() != 1) {
//						sendGuildAndMemberInfo(null, player, Notify_Delete, guild);						
						deleteGuildData(2, guild, memberInfo);
						sendGuildAndMemberInfo(null, player, Notify_Delete, guild);
						sendOtherMemberInfoById(Notify_Delete, guild, player.getId());
						guild.removeMember(player);
						guild.Update();
						guild.addParseEvent(ResManager.getInstance().getString("退出战盟"), getEventString(ResManager.getInstance().getString("退出战盟")), new ParseUtil.PlayerParm(player.getId(), player.getName()));
						saveGuildData(7, guild, null);
						player.setLastAfkGuildTime((int) (System.currentTimeMillis() / 1000));
						sendOtherGuildInfo(Notify_AddOrUpdate, guild, player);
						
						MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("您离开了战盟：{1}"), guild.getGuildInfo().getGuildName());
						guild.sendAllMemberNotify(Notifys.SUCCESS, ResManager.getInstance().getString("{1}玩家离开了我们的战盟"), player.getName());
						guild.sendAllMemberNotify(Notifys.CHAT_GROUP, getParseString("<font color='#FFDE00'>{@}</font>玩家离开了我们的战盟", player));
						sendMessage.setBtErrorCode(Error_Success);
						
//						//排行榜数据覆盖
//						GuildFightPowerTop fightPowerTop = new GuildFightPowerTop(guild.getGuildInfo().getGuildId(), guild.getGuildInfo().getMemberFightPower(), guild.getGuildInfo().getBannerLevel());
////						 TopListManager.getInstance().getGuildTopMap().get(fightPowerTop.getId());
//						 TopListManager.getInstance().getGuildTopMap().remove(fightPowerTop);
//						TopListManager.getInstance().getGuildTopMap().put(fightPowerTop, fightPowerTop.getId());
					} else {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您是盟主，如需退盟，请先任命其他成员为盟主"));
					}
				} else {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，没有找到该战盟"));
				}
			}
			MessageUtil.tell_player_message(player, sendMessage);
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
		}
	}

	/**
	 * 修改昵称
	 *
	 * @param message 通知世界服务器修改昵称
	 * @return
	 */
	public void reqInnerGuildChangeNickNameToWorld(ReqInnerGuildChangeNickNameToWorldMessage message) {
		if (message.getNickName() == null) {
			message.setNickName("");
		}
		if (message.getGroupName() == null) {
			message.setGroupName("");
		}
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
		if (player != null) {
			ResGuildChangeNickNameToClientMessage sendMessage = new ResGuildChangeNickNameToClientMessage();
			sendMessage.setBtErrorCode(Error_Fail);
			if (player.getGuildid() == message.getGuildId()) {
				Guild guild = getGuildMapManager().FindById(player.getGuildid());
				if (guild != null) {
					MemberInfo memberInfo = guild.findMemberInfo(player);
					if (memberInfo != null && memberInfo.getGuildPowerLevel() != 4) {
						Player destPlayer = PlayerManager.getInstance().getPlayer(message.getUserId());
						PlayerWorldInfo destPlayerWorldInfo = PlayerManager.getInstance().getPlayerWorldInfo(message.getUserId());
						MemberInfo destMemberInfo = guild.findMemberInfoById(message.getUserId());
						if (destMemberInfo != null && destPlayerWorldInfo != null) {
							destMemberInfo.setNickName(message.getNickName());
							destMemberInfo.setGroupName(message.getGroupName());
							if (destPlayer != null) {
								sendGuildAndMemberInfo(null, destPlayer, Notify_AddOrUpdate, guild);
								sendGuildAndMemberInfo(player, destPlayer, Notify_AddOrUpdate, guild);
								if ("".equals(message.getNickName())) {
									if (player != destPlayer) {
										MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("成功取消了{1}玩家的盟中昵称"), destPlayer.getName());
										MessageUtil.notify_player(destPlayer, Notifys.SUCCESS, ResManager.getInstance().getString("您的盟中昵称被“{1}”清除了"), Default_Power[memberInfo.getGuildPowerLevel() - 1]);
									} else {
										MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("成功取消了盟中昵称"));
									}
								} else {
									guild.addParseEvent(ResManager.getInstance().getString("调整昵称"), getEventString(ResManager.getInstance().getString("调整昵称"), Default_Power[memberInfo.getGuildPowerLevel() - 1], message.getNickName()), new ParseUtil.PlayerParm(destPlayer.getId(), destPlayer.getName()));
									saveGuildData(6, guild, destMemberInfo);

									if (player != destPlayer) {
										MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("成功修改{1}玩家的盟中昵称为：{2}"), destPlayer.getName(), message.getNickName());
										MessageUtil.notify_player(destPlayer, Notifys.SUCCESS, ResManager.getInstance().getString("您的盟中昵称被“{1}”修改为：{2}"), Default_Power[memberInfo.getGuildPowerLevel() - 1], message.getNickName());
									} else {
										MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("成功修改了盟中昵称"));
									}
								}
								if ("".equals(message.getGroupName())) {
									if (player != destPlayer) {
										MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("成功取消了{1}玩家的分组与头衔"), destPlayer.getName());
										MessageUtil.notify_player(destPlayer, Notifys.SUCCESS, ResManager.getInstance().getString("您的分组与头衔被“{1}”清除了"), Default_Power[memberInfo.getGuildPowerLevel() - 1]);
									} else {
										MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("成功取消了分组与头衔"));
									}
								} else {
									guild.addParseEvent(ResManager.getInstance().getString("调整分组头衔"), getEventString(ResManager.getInstance().getString("调整分组头衔"), Default_Power[memberInfo.getGuildPowerLevel() - 1], message.getGroupName()), new ParseUtil.PlayerParm(destPlayer.getId(), destPlayer.getName()));
									saveGuildData(6, guild, destMemberInfo);

									if (player != destPlayer) {
										MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("成功修改{1}玩家的分组与头衔为：{2}"), destPlayer.getName(), message.getGroupName());
										MessageUtil.notify_player(destPlayer, Notifys.SUCCESS, ResManager.getInstance().getString("您的分组与头衔被“{1}”修改为：{2}"), Default_Power[memberInfo.getGuildPowerLevel() - 1], message.getGroupName());
									} else {
										MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("成功修改了分组与头衔"));
									}
								}
							} else {
								sendGuildAndMemberInfoById(player, destPlayerWorldInfo.getId(), Notify_AddOrUpdate, guild);
								if ("".equals(message.getNickName())) {
									if (player != destPlayer) {
										MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("成功取消了{1}玩家的盟中昵称"), destPlayerWorldInfo.getName());
									} /*else {
										MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("成功取消了盟中昵称"));
									}*/
								} else {
									guild.addParseEvent(ResManager.getInstance().getString("调整昵称"), getEventString(ResManager.getInstance().getString("调整昵称"), Default_Power[memberInfo.getGuildPowerLevel() - 1], message.getNickName()), new ParseUtil.PlayerParm(destPlayerWorldInfo.getId(), destPlayerWorldInfo.getName()));
									saveGuildData(6, guild, destMemberInfo);

									if (player != destPlayer) {
										MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("成功修改{1}玩家的盟中昵称为：{2}"), destPlayerWorldInfo.getName(), message.getNickName());
									} /*else {
										MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("成功修改了盟中昵称"));
									}*/
								}
								if ("".equals(message.getGroupName())) {
									if (player != destPlayer) {
										MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("成功取消了{1}玩家的分组与头衔"), destPlayerWorldInfo.getName());
									} /*else {
										MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("成功取消了分组与头衔"));
									}*/
								} else {
									guild.addParseEvent(ResManager.getInstance().getString("调整分组头衔"), getEventString(ResManager.getInstance().getString("调整分组头衔"), Default_Power[memberInfo.getGuildPowerLevel() - 1], message.getGroupName()), new ParseUtil.PlayerParm(destPlayerWorldInfo.getId(), destPlayerWorldInfo.getName()));
									saveGuildData(6, guild, destMemberInfo);

									if (player != destPlayer) {
										MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("成功修改{1}玩家的分组与头衔为：{2}"), destPlayerWorldInfo.getName(), message.getGroupName());
									} /*else {
										MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("成功修改了分组与头衔"));
									}*/
								}
							}
							sendMessage.setBtErrorCode(Error_Success);
						} else {
							MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，没有找到这个战盟成员"));
						}
					} else {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，您没有修改盟中昵称的权限"));
					}
				} else {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，没有找到该战盟"));
				}
			}
			MessageUtil.tell_player_message(player, sendMessage);
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
		}
	}

	/**
	 * 修改职权
	 *
	 * @param message 通知世界服务器修改职权
	 * @return
	 */
	public void reqInnerGuildChangePowerLevelToWorld(ReqInnerGuildChangePowerLevelToWorldMessage message) {
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
		if (player != null) {
			ResGuildChangePowerLevelToClientMessage sendMessage = new ResGuildChangePowerLevelToClientMessage();
			sendMessage.setBtErrorCode(Error_Fail);
			if (player.getGuildid() == message.getGuildId()) {
				Guild guild = getGuildMapManager().FindById(player.getGuildid());
				if (guild != null) {
					MemberInfo memberInfo = guild.findMemberInfo(player);
					if (memberInfo != null && (memberInfo.getGuildPowerLevel() == 1 || memberInfo.getGuildPowerLevel() == 2)) {
						Player destPlayer = PlayerManager.getInstance().getPlayer(message.getUserId());
						PlayerWorldInfo destPlayerWorldInfo = PlayerManager.getInstance().getPlayerWorldInfo(message.getUserId());
						MemberInfo destMemberInfo = guild.findMemberInfoById(message.getUserId());
						if (destPlayerWorldInfo != null && destMemberInfo != null) {
							//检查权限
							if(memberInfo.getGuildPowerLevel() == 1 && destMemberInfo.getGuildPowerLevel() == 1) {
								MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，不能修改盟主自己的职位"));
							} else if(memberInfo.getGuildPowerLevel() == 2 && (destMemberInfo.getGuildPowerLevel() <= 2 || message.getNewPowerLevel() <= 2)) {
								MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，不能修改比自己职位高的职位"));
							} else {
								//检查人数
								int viceCount = guild.getPowerLevelCount(2); //副盟主的人数
								int eliteCount = guild.getPowerLevelCount(3);//战盟精英的人数
								if(memberInfo.getGuildPowerLevel() == 1 && ((message.getNewPowerLevel() == 2 && viceCount >= 1) || (message.getNewPowerLevel() == 3 && eliteCount >= 3))) {
									MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，被任命的职位人数已满"));	
								} else if(memberInfo.getGuildPowerLevel() == 2 && (message.getNewPowerLevel() == 3 && eliteCount >= 3)) {
									MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，被任命的职位人数已满"));
								} else {
									if (message.getNewPowerLevel() == 1) {//变更盟主
										memberInfo.setGuildPowerLevel((byte) 4);
										destMemberInfo.setGuildPowerLevel((byte) 1);
										guild.Update();
										guild.addParseEvent(ResManager.getInstance().getString("调整职务"), getEventString(ResManager.getInstance().getString("调整职务"), Default_Power[0]), new ParseUtil.PlayerParm(destPlayerWorldInfo.getId(), destPlayerWorldInfo.getName()));
										saveGuildData(7, guild, destMemberInfo);
										saveGuildData(2, guild, memberInfo);

										sendAllGuildInfo(Notify_AddOrUpdate, guild);
										if (destPlayer != null) {
											sendMemberInfo(null, destPlayer, Notify_AddOrUpdate, guild);
										}
										sendMemberInfo(null, player, Notify_AddOrUpdate, guild);
										sendOtherMemberInfoById(Notify_AddOrUpdate, guild, player.getId());
										sendOtherMemberInfoById(Notify_AddOrUpdate, guild, destPlayerWorldInfo.getId());
										MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("您将盟主职位禅让给了：{1}"), destPlayerWorldInfo.getName());
										if (destPlayer != null) {
											MessageUtil.notify_player(destPlayer, Notifys.SUCCESS, ResManager.getInstance().getString("恭喜您被提升为：盟主"));
										}
										guild.sendAllMemberNotify(Notifys.SUCCESS, ResManager.getInstance().getString("本盟【盟主】变更，欢迎新盟主：{1}"), destPlayerWorldInfo.getName());
										sendMessage.setBtErrorCode(Error_Success);
									}else if (destMemberInfo.getGuildPowerLevel() != message.getNewPowerLevel()) {//变更其他职位
										destMemberInfo.setGuildPowerLevel(message.getNewPowerLevel());
										guild.Update();
										guild.addParseEvent(ResManager.getInstance().getString("调整职务"), getEventString(ResManager.getInstance().getString("调整职务"), Default_Power[message.getNewPowerLevel() - 1]), new ParseUtil.PlayerParm(destPlayerWorldInfo.getId(), destPlayerWorldInfo.getName()));
										saveGuildData(7, guild, destMemberInfo);

										sendOtherGuildInfo(Notify_AddOrUpdate, guild, destPlayer);
//										sendGuildInfo(player, Notify_AddOrUpdate, guild);
										sendGuildInfo(destPlayer, Notify_AddOrUpdate, guild);
										if (destPlayer != null) {
											sendMemberInfo(null, destPlayer, Notify_AddOrUpdate, guild);
										}
										sendOtherMemberInfoById(Notify_AddOrUpdate, guild, destPlayerWorldInfo.getId());
//										sendMemberInfoById(player, destPlayerWorldInfo.getId(), Notify_AddOrUpdate, guild);
										if (destPlayer != null) {
											MessageUtil.notify_player(destPlayer, Notifys.SUCCESS, ResManager.getInstance().getString("您在盟中的职务变更为：{1}"), Default_Power[message.getNewPowerLevel() - 1]);
										}
										if (message.getNewPowerLevel() != 4) {
											guild.sendAllMemberNotify(Notifys.SUCCESS, ResManager.getInstance().getString("本盟[{1}]变更为：{2}"), Default_Power[message.getNewPowerLevel() - 1], destPlayerWorldInfo.getName());
										}
										sendMessage.setBtErrorCode(Error_Success);
									} else {
										MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，职位相同，变更无效"));
									}
								}
							}		
							
							/*
							if (destMemberInfo != null && destMemberInfo.getGuildPowerLevel() != 1) {
								if (message.getNewPowerLevel() == 1) {//变更盟主
									memberInfo.setGuildPowerLevel((byte) 6);
									destMemberInfo.setGuildPowerLevel((byte) 1);
									guild.Update();
									guild.addParseEvent(ResManager.getInstance().getString("调整职务"), getEventString(ResManager.getInstance().getString("调整职务"), Default_Power[0]), new ParseUtil.PlayerParm(destPlayerWorldInfo.getId(), destPlayerWorldInfo.getName()));
									saveGuildData(7, guild, destMemberInfo);
									saveGuildData(2, guild, memberInfo);

									sendGuildInfo(player, Notify_AddOrUpdate, guild);
									sendGuildInfo(destPlayer, Notity_ChangeBanner, guild);
									sendGuildInfo(player, Notity_KingCity, guild);
									sendGuildInfo(destPlayer, Notity_KingCity, guild);
									sendMemberInfo(null, player, Notify_AddOrUpdate, guild);
									if (destPlayer != null) {
										sendMemberInfo(null, destPlayer, Notify_AddOrUpdate, guild);
										sendMemberInfo(destPlayer, player, Notify_AddOrUpdate, guild);
									}
									sendMemberInfoById(player, destPlayerWorldInfo.getId(), Notify_AddOrUpdate, guild);
									MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("您将盟主职位禅让给了：{1}"), destPlayerWorldInfo.getName());
									if (destPlayer != null) {
										MessageUtil.notify_player(destPlayer, Notifys.SUCCESS, ResManager.getInstance().getString("恭喜您被提升为：盟主"));
									}
									guild.sendAllMemberNotify(Notifys.SUCCESS, ResManager.getInstance().getString("本盟【盟主】变更，欢迎新盟主：{1}"), destPlayerWorldInfo.getName());
									sendMessage.setBtErrorCode(Error_Success);
								} else {//变更其他职位
									if (destMemberInfo.getGuildPowerLevel() != message.getNewPowerLevel()) {
										Player otherPlayer = guild.getPowerLevelPlayer(message.getNewPowerLevel());
										MemberInfo otherMemberInfo = guild.getPowerLevelMemberInfo(message.getNewPowerLevel());
										if (otherMemberInfo != null) {
											if (otherMemberInfo.getGuildPowerLevel() == message.getNewPowerLevel()) {
												otherMemberInfo.setGuildPowerLevel((byte) 6);
												destMemberInfo.setGuildPowerLevel(message.getNewPowerLevel());
												guild.Update();
												guild.addParseEvent(ResManager.getInstance().getString("调整职务"), getEventString(ResManager.getInstance().getString("调整职务"), Default_Power[message.getNewPowerLevel() - 1]), new ParseUtil.PlayerParm(destPlayerWorldInfo.getId(), destPlayerWorldInfo.getName()));
												saveGuildData(7, guild, destMemberInfo);
												saveGuildData(2, guild, otherMemberInfo);

												sendGuildInfo(player, Notify_AddOrUpdate, guild);
												sendGuildInfo(destPlayer, Notity_ChangeBanner, guild);
												sendGuildInfo(otherPlayer, Notify_AddOrUpdate, guild);
												if (destPlayer != null) {
													sendMemberInfo(null, destPlayer, Notify_AddOrUpdate, guild);
													sendMemberInfoById(destPlayer, otherMemberInfo.getUserId(), Notify_AddOrUpdate, guild);
												}
												if (otherPlayer != null) {
													sendMemberInfo(null, otherPlayer, Notify_AddOrUpdate, guild);
													sendMemberInfoById(otherPlayer, destPlayerWorldInfo.getId(), Notify_AddOrUpdate, guild);
												}
												sendMemberInfoById(player, destPlayerWorldInfo.getId(), Notify_AddOrUpdate, guild);
												sendMemberInfoById(player, otherMemberInfo.getUserId(), Notify_AddOrUpdate, guild);
												if (destPlayer != null) {
													MessageUtil.notify_player(destPlayer, Notifys.SUCCESS, ResManager.getInstance().getString("您在盟中的职务变更为：{1}"), Default_Power[message.getNewPowerLevel() - 1]);
												}
												guild.sendAllMemberNotify(Notifys.SUCCESS, ResManager.getInstance().getString("本盟[{1}]变更为：{2}"), Default_Power[message.getNewPowerLevel() - 1], destPlayerWorldInfo.getName());
												sendMessage.setBtErrorCode(Error_Success);
											} else {
												MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，职位调整错误，变更无效"));
											}
										} else {
											destMemberInfo.setGuildPowerLevel(message.getNewPowerLevel());
											guild.Update();
											guild.addParseEvent(ResManager.getInstance().getString("调整职务"), getEventString(ResManager.getInstance().getString("调整职务"), Default_Power[message.getNewPowerLevel() - 1]), new ParseUtil.PlayerParm(destPlayerWorldInfo.getId(), destPlayerWorldInfo.getName()));
											saveGuildData(7, guild, destMemberInfo);

											sendGuildInfo(player, Notify_AddOrUpdate, guild);
											sendGuildInfo(destPlayer, Notity_ChangeBanner, guild);
											if (destPlayer != null) {
												sendMemberInfo(null, destPlayer, Notify_AddOrUpdate, guild);
											}
											sendMemberInfoById(player, destPlayerWorldInfo.getId(), Notify_AddOrUpdate, guild);
											if (destPlayer != null) {
												MessageUtil.notify_player(destPlayer, Notifys.SUCCESS, ResManager.getInstance().getString("您在盟中的职务变更为：{1}"), Default_Power[message.getNewPowerLevel() - 1]);
											}
											if (message.getNewPowerLevel() != 6) {
												guild.sendAllMemberNotify(Notifys.SUCCESS, ResManager.getInstance().getString("本盟[{1}]变更为：{2}"), Default_Power[message.getNewPowerLevel() - 1], destPlayerWorldInfo.getName());
											}
											sendMessage.setBtErrorCode(Error_Success);
										}
									} else {
										MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，职位相同，变更无效"));
									}
								}
							} else {
								MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，盟主不能修改盟主自己的职位"));
							}*/
						} else {
							MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，没有找到被修改者数据"));
						}
					} else {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，只有盟主和副盟主才可以修改其他玩家的职位"));
					}
				} else {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，没有找到该战盟"));
				}
			}
			MessageUtil.tell_player_message(player, sendMessage);
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
		}
	}

	/**
	 * 删除战盟成员
	 *
	 * @param message 通知世界服务器删除战盟成员
	 * @return
	 */
	public void reqInnerGuildDeleteMemberToWorld(ReqInnerGuildDeleteMemberToWorldMessage message) {
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
		if (player != null) {
			ResGuildDeleteMemberToClientMessage sendMessage = new ResGuildDeleteMemberToClientMessage();
			sendMessage.setBtErrorCode(Error_Fail);
			if (player.getGuildid() == message.getGuildId()) {
				if (player.getId() != message.getUserId()) {
					Guild guild = getGuildMapManager().FindById(player.getGuildid());
					if (guild != null) {
						MemberInfo memberInfo = guild.findMemberInfo(player);
						if (memberInfo != null) {
							if (memberInfo.getGuildPowerLevel() == 1 || memberInfo.getGuildPowerLevel() == 2) {
								Player destPlayer = PlayerManager.getInstance().getPlayer(message.getUserId());
								PlayerWorldInfo destPlayerWorldInfo = PlayerManager.getInstance().getPlayerWorldInfo(message.getUserId());
								MemberInfo destMemberInfo = guild.findMemberInfoById(message.getUserId());
								if (destMemberInfo != null && destPlayerWorldInfo != null) {
									switch (destMemberInfo.getGuildPowerLevel()) {
										case 1: {
											if (memberInfo.getGuildPowerLevel() == 1) {
												MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，您不能开除自己"));
											} else {
												MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，您的权限不足"));
											}
										}
										break;
										case 2: {
											if (memberInfo.getGuildPowerLevel() == 2) {
												MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，您不能开除自己"));
												break;
											} else if(memberInfo.getGuildPowerLevel() != 1) {
												MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，您的权限不足"));
												break;
											}
										}
										case 3: {
											if (memberInfo.getGuildPowerLevel() == 1 || memberInfo.getGuildPowerLevel() == 2) {
												deleteGuildData(2, guild, destMemberInfo);
												if (destPlayer != null) {
													sendGuildAndMemberInfo(null, destPlayer, Notify_Delete, guild);
												}
												sendOtherMemberInfoById(Notify_Delete, guild, destPlayerWorldInfo.getId());
//												sendMemberInfoById(player, destPlayerWorldInfo.getId(), Notify_Delete, guild);
												guild.removeMemberById(destPlayerWorldInfo.getId());
												guild.Update();
												guild.addParseEvent(ResManager.getInstance().getString("逐出成员"), getEventString(ResManager.getInstance().getString("逐出成员"), Default_Power[0]), new ParseUtil.PlayerParm(destPlayerWorldInfo.getId(), destPlayerWorldInfo.getName()));
												saveGuildData(7, guild, null);

												if (destPlayer != null) {
													destPlayer.setLastAfkGuildTime((int) (System.currentTimeMillis() / 1000));
												}
												sendOtherGuildInfo(Notify_AddOrUpdate, guild, destPlayer);
//												sendGuildInfo(player, Notify_AddOrUpdate, guild);
												if (destPlayer != null) {
													MessageUtil.notify_player(destPlayer, Notifys.SUCCESS, getParseString(String.format("[%s]<font color='#FFDE00'>{@}</font>将您移出了战盟", Default_Power[memberInfo.getGuildPowerLevel() - 1]), player));
													MessageUtil.notify_player(destPlayer, Notifys.CHAT_GROUP, getParseString(String.format("[%s]<font color='#FFDE00'>{@}</font>将您移出了战盟", Default_Power[memberInfo.getGuildPowerLevel() - 1]), player));
												}
//												guild.sendAllMemberNotify(Notifys.SUCCESS, ResManager.getInstance().getString("{1}被[{2}]逐出了战盟"), destPlayerWorldInfo.getName(), Default_Power[memberInfo.getGuildPowerLevel() - 1]);
												sendMessage.setBtErrorCode(Error_Success);
											} else {
												MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，您没有权限开除职权盟众"));
											}
										}
										break;
										case 4: {
											deleteGuildData(2, guild, destMemberInfo);
											if (destPlayer != null) {
												sendGuildAndMemberInfo(null, destPlayer, Notify_Delete, guild);
											}
											sendOtherMemberInfoById(Notify_Delete, guild, destPlayerWorldInfo.getId());
//											sendMemberInfoById(player, destPlayerWorldInfo.getId(), Notify_Delete, guild);
											guild.removeMemberById(destPlayerWorldInfo.getId());
											guild.Update();
											guild.addParseEvent(ResManager.getInstance().getString("逐出成员"), getEventString(ResManager.getInstance().getString("逐出成员"), Default_Power[memberInfo.getGuildPowerLevel() - 1]), new ParseUtil.PlayerParm(destPlayerWorldInfo.getId(), destPlayerWorldInfo.getName()));
											saveGuildData(7, guild, null);

											if (destPlayer != null) {
												destPlayer.setLastAfkGuildTime((int) (System.currentTimeMillis() / 1000));
											}
											sendOtherGuildInfo(Notify_AddOrUpdate, guild, destPlayer);
//											sendGuildInfo(player, Notify_AddOrUpdate, guild);
											if (destPlayer != null) {
												MessageUtil.notify_player(destPlayer, Notifys.SUCCESS, getParseString(String.format("[%s]<font color='#FFDE00'>{@}</font>将您移出了战盟", Default_Power[memberInfo.getGuildPowerLevel() - 1]), player));
												MessageUtil.notify_player(destPlayer, Notifys.CHAT_GROUP, getParseString(String.format("[%s]<font color='#FFDE00'>{@}</font>将您移出了战盟", Default_Power[memberInfo.getGuildPowerLevel() - 1]), player));
											}
//											guild.sendAllMemberNotify(Notifys.SUCCESS, ResManager.getInstance().getString("{1}被[{2}]逐出了战盟"), destPlayerWorldInfo.getName(), Default_Power[memberInfo.getGuildPowerLevel() - 1]);
											sendMessage.setBtErrorCode(Error_Success);
										}
										break;
									}
								} else {
									MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，没有找到该战盟成员"));
								}
							} else {
								MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，您的权限不足"));
							}
						} else {
							MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，您不是该战盟成员"));
						}
					} else {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，没有找到该战盟"));
					}
				} else {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，您不能开除自己"));
				}
			}
			MessageUtil.tell_player_message(player, sendMessage);
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
		}
	}

	/**
	 * 盟主修改自动同意申请加入设置
	 *
	 * @param message 通知世界服务器盟主修改自动同意申请加入设置
	 * @return
	 */
	public void reqInnerGuildAutoGuildArgeeAddToWorld(ReqInnerGuildAutoGuildArgeeAddToWorldMessage message) {
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
		if (player != null) {
			ResGuildAutoGuildArgeeAddToClientMessage sendMessage = new ResGuildAutoGuildArgeeAddToClientMessage();
			sendMessage.setBtErrorCode(Error_Fail);
			if (player.getGuildid() == message.getGuildId()) {
				Guild guild = getGuildMapManager().FindById(player.getGuildid());
				if (guild != null) {
					MemberInfo memberInfo = guild.findMemberInfo(player);
					if (memberInfo != null && memberInfo.getGuildPowerLevel() == 1) {
						if (message.getAutoGuildAgreeAdd() == 0) {
							guild.getGuildInfo().setAutoGuildAgreeAdd((byte) 0);//不同意
						} else {
							guild.getGuildInfo().setAutoGuildAgreeAdd((byte) 1);//同意
						}
						saveGuildData(7, guild, null);
						sendAllGuildInfo(Notify_AddOrUpdate, guild);
						sendMessage.setBtErrorCode(Error_Success);
					} else {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，只有盟主才能修改此项设置"));
					}
				} else {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，没有找到该战盟"));
				}
			}
			MessageUtil.tell_player_message(player, sendMessage);
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
		}
	}

	/**
	 * 修改公告
	 *
	 * @param message 通知世界服务器修改公告
	 * @return
	 */
	public void reqInnerGuildChangeBulletinToWorld(ReqInnerGuildChangeBulletinToWorldMessage message) {
		if (message.getGuildBulletin() == null) {
			message.setGuildBulletin("");
		}
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
		if (player != null) {
			ResGuildChangeBulletinToClientMessage sendMessage = new ResGuildChangeBulletinToClientMessage();
			sendMessage.setBtErrorCode(Error_Fail);
			if (player.getGuildid() == message.getGuildId()) {
				Guild guild = getGuildMapManager().FindById(player.getGuildid());
				if (guild != null) {
					MemberInfo memberInfo = guild.findMemberInfo(player);
					if (memberInfo != null && (memberInfo.getGuildPowerLevel() == 1 || memberInfo.getGuildPowerLevel() == 2)) {
						guild.getGuildInfo().setGuildBulletin(message.getGuildBulletin());
						guild.getGuildInfo().setLastGuildBulletinTime((int) (System.currentTimeMillis() / 1000));
						saveGuildData(7, guild, null);

						sendAllGuildInfo(Notify_AddOrUpdate, guild);
						MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("恭喜您，战盟公告修改成功"));
						guild.sendAllMemberNotifyNoMe(player, Notifys.SUCCESS, ResManager.getInstance().getString("盟主更新了战盟公告，欢迎查阅"), 0);
						sendMessage.setBtErrorCode(Error_Success);
					} else {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，只有盟主才可以修改公告"));
					}
				} else {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，没有找到该战盟"));
				}
			}
			MessageUtil.tell_player_message(player, sendMessage);
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
		}
	}

	/**
	 * 提交战盟贡献物品
	 *
	 * @param message 通知世界服务器提交战盟贡献物品
	 * @return
	 */
	public void reqInnerGuildSubmitItemToWorld(ReqInnerGuildSubmitItemToWorldMessage message) {
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
		if (player != null) {
			ResGuildSubmitItemToClientMessage sendMessage = new ResGuildSubmitItemToClientMessage();
			sendMessage.setBtErrorCode(Error_Fail);
			if (player.getGuildid() == message.getGuildId()) {
				Guild guild = getGuildMapManager().FindById(player.getGuildid());
				if (guild != null) {
					MemberInfo memberInfo = guild.findMemberInfo(player);
					if (memberInfo != null) {
						int addnum = 0;
						if (message.getItemType() == 5) {//金币
							addnum = message.getItemNum() / 500000;
						} else {
							addnum = message.getItemNum();
						}
						if (addnum != 0) {
							player.setContributionPoint(player.getContributionPoint() + addnum);
							memberInfo.setContributionPoint(memberInfo.getContributionPoint() + addnum);
							memberInfo.setContributionPointHistory(player.getContributionPoint());
							switch (message.getItemType()) {
								case 1: {//忠诚徽章
									guild.getGuildInfo().setDragon(guild.getGuildInfo().getDragon() + message.getItemNum());
									memberInfo.setDragonHistory(memberInfo.getDragonHistory() + message.getItemNum());
									guild.addParseEvent(ResManager.getInstance().getString("盟贡捐献"), getEventString(ResManager.getInstance().getString("盟贡捐献"), ResManager.getInstance().getString("忠诚徽章"), message.getItemNum(), addnum), new ParseUtil.PlayerParm(player.getId(), player.getName(), "[" + message.getItemNum() + ",0,0,0,0]"));
									saveGuildData(7, guild, memberInfo);

//									MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("感谢您，增加盟贡库存[忠诚徽章]：{1}，您获得盟贡点：{2}"), String.valueOf(message.getItemNum()), String.valueOf(addnum));
									guild.sendAllMemberNotify(Notifys.SUCCESS, getParseString(String.format("感谢<font color='#FFDE00'>{@}</font>向盟贡仓库提交了：忠诚徽章*%s，获得盟贡点：%s", String.valueOf(message.getItemNum()), String.valueOf(addnum)), player));
									guild.sendAllMemberNotify(Notifys.CHAT_GROUP, getParseString(String.format("感谢<font color='#FFDE00'>{@}</font>向盟贡仓库提交了：忠诚徽章*%s，获得盟贡点：%s", String.valueOf(message.getItemNum()), String.valueOf(addnum)), player));
								}
								break;
								case 2: {//荣誉徽章
									guild.getGuildInfo().setWhiteTiger(guild.getGuildInfo().getWhiteTiger() + message.getItemNum());
									memberInfo.setWhiteTigerHistory(memberInfo.getWhiteTigerHistory() + message.getItemNum());
									guild.addParseEvent(ResManager.getInstance().getString("盟贡捐献"), getEventString(ResManager.getInstance().getString("盟贡捐献"), ResManager.getInstance().getString("荣誉徽章"), message.getItemNum(), addnum), new ParseUtil.PlayerParm(player.getId(), player.getName(), "[0," + message.getItemNum() + ",0,0,0]"));
									saveGuildData(7, guild, memberInfo);

//									MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("感谢您，增加盟贡库存[荣誉徽章]：{1}，您获得盟贡点：{2}"), String.valueOf(message.getItemNum()), String.valueOf(addnum));
									guild.sendAllMemberNotify(Notifys.SUCCESS, getParseString(String.format("感谢<font color='#FFDE00'>{@}</font>向盟贡仓库提交了：荣誉徽章*%s，获得盟贡点：%s", String.valueOf(message.getItemNum()), String.valueOf(addnum)), player));
									guild.sendAllMemberNotify(Notifys.CHAT_GROUP, getParseString(String.format("感谢<font color='#FFDE00'>{@}</font>向盟贡仓库提交了：荣誉徽章*%s，获得盟贡点：%s", String.valueOf(message.getItemNum()), String.valueOf(addnum)), player));
								}
								break;
								case 3: {//守护徽章
									guild.getGuildInfo().setSuzaku(guild.getGuildInfo().getSuzaku() + message.getItemNum());
									memberInfo.setSuzakuHistory(memberInfo.getSuzakuHistory() + message.getItemNum());
									guild.addParseEvent(ResManager.getInstance().getString("盟贡捐献"), getEventString(ResManager.getInstance().getString("盟贡捐献"), ResManager.getInstance().getString("守护徽章"), message.getItemNum(), addnum), new ParseUtil.PlayerParm(player.getId(), player.getName(), "[0,0," + message.getItemNum() + ",0,0]"));
									saveGuildData(7, guild, memberInfo);

//									MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("感谢您，增加盟贡库存[守护徽章]：{1}，您获得盟贡点：{2}"), String.valueOf(message.getItemNum()), String.valueOf(addnum));
									guild.sendAllMemberNotify(Notifys.SUCCESS, getParseString(String.format("感谢<font color='#FFDE00'>{@}</font>向盟贡仓库提交了：守护徽章*%s，获得盟贡点：%s", String.valueOf(message.getItemNum()), String.valueOf(addnum)), player));
									guild.sendAllMemberNotify(Notifys.CHAT_GROUP, getParseString(String.format("感谢<font color='#FFDE00'>{@}</font>向盟贡仓库提交了：守护徽章*%s，获得盟贡点：%s", String.valueOf(message.getItemNum()), String.valueOf(addnum)), player));
								}
								break;
								case 4: {//勇敢徽章
									guild.getGuildInfo().setBasaltic(guild.getGuildInfo().getBasaltic() + message.getItemNum());
									memberInfo.setBasalticHistory(memberInfo.getBasalticHistory() + message.getItemNum());
									guild.addParseEvent(ResManager.getInstance().getString("盟贡捐献"), getEventString(ResManager.getInstance().getString("盟贡捐献"), ResManager.getInstance().getString("勇敢徽章"), message.getItemNum(), addnum), new ParseUtil.PlayerParm(player.getId(), player.getName(), "[0,0,0," + message.getItemNum() + ",0]"));
									saveGuildData(7, guild, memberInfo);

//									MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("感谢您，增加盟贡库存[勇敢徽章]：{1}，您获得盟贡点：{2}"), String.valueOf(message.getItemNum()), String.valueOf(addnum));
									guild.sendAllMemberNotify(Notifys.SUCCESS, getParseString(String.format("感谢<font color='#FFDE00'>{@}</font>向盟贡仓库提交了：勇敢徽章*%s，获得盟贡点：%s", String.valueOf(message.getItemNum()), String.valueOf(addnum)), player));
									guild.sendAllMemberNotify(Notifys.CHAT_GROUP, getParseString(String.format("感谢<font color='#FFDE00'>{@}</font>向盟贡仓库提交了：勇敢徽章*%s，获得盟贡点：%s", String.valueOf(message.getItemNum()), String.valueOf(addnum)), player));
								}
								break;
								case 5: {//金币
									guild.getGuildInfo().setStockGold(guild.getGuildInfo().getStockGold() + message.getItemNum());
									memberInfo.setStockGoldHistory(memberInfo.getStockGoldHistory() + message.getItemNum());
									guild.addParseEvent(ResManager.getInstance().getString("盟贡捐献"), getEventString(ResManager.getInstance().getString("盟贡捐献"), ResManager.getInstance().getString("金币"), message.getItemNum(), addnum), new ParseUtil.PlayerParm(player.getId(), player.getName(), "[0,0,0,0," + message.getItemNum() + "]"));
									saveGuildData(7, guild, memberInfo);

//									MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("感谢您，增加盟贡库存[金币]：{1}，您获得盟贡点：{2}"), String.valueOf(message.getItemNum()), String.valueOf(addnum));
									guild.sendAllMemberNotify(Notifys.SUCCESS, getParseString(String.format("感谢<font color='#FFDE00'>{@}</font>向盟贡仓库提交了：金币*%s，获得盟贡点：%s", String.valueOf(message.getItemNum()), String.valueOf(addnum)), player));
									guild.sendAllMemberNotify(Notifys.CHAT_GROUP, getParseString(String.format("感谢<font color='#FFDE00'>{@}</font>向盟贡仓库提交了：金币*%s，获得盟贡点：%s", String.valueOf(message.getItemNum()), String.valueOf(addnum)), player));
								}
								break;
							}
							sendGuildAndMemberInfo(null, player, Notify_AddOrUpdate, guild);
							guild.boardcastInfo(Notify_AddOrUpdate);
							sendMessage.setBtErrorCode(Error_Success);
						}
					} else {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，您不是该战盟成员"));
					}
				} else {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，没有找到该战盟"));
				}
			}
			MessageUtil.tell_player_message(player, sendMessage);
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
		}
	}

	public void reqInnerKingCityEventToWorld(ReqInnerKingCityEventToWorldMessage message) {
		switch (message.getEventtype()) {
			case 1: {
				Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
				if (player != null) {
					Guild guild = getGuildMapManager().FindById(player.getGuildid());
					if (guild != null) {
						MemberInfo memberInfo = guild.findMemberInfo(player);
						if (memberInfo != null) {
							if (memberInfo.getGuildPowerLevel() == 1) {
								int money = Integer.valueOf(message.getGuildevent());
								if (money != 0 && guild.getGuildInfo().getStockGold() >= money) {
									guild.getGuildInfo().setStockGold(guild.getGuildInfo().getStockGold() - money);
									//guild.addParseEvent("盟贡捐献", getEventString("盟贡捐献", "金币", message.getItemNum(), addnum), new ParseUtil.PlayerParm(player.getId(), player.getName()));
									saveGuildData(7, guild, null);
									//MessageUtil.notify_player(player, Notifys.SUCCESS, "感谢您，增加盟贡库存[金币]：{1}，您获得盟贡点：{2}", String.valueOf(message.getItemNum()), String.valueOf(addnum));
									//guild.sendAllMemberNotifyNoMe(player, Notifys.SUCCESS, "感谢{1}向盟贡仓库提交了：金币*{2}，获得盟贡点：{3}", player.getName(), String.valueOf(message.getItemNum()), String.valueOf(addnum));
									//sendGuildAndMemberInfo(null, player, Notify_AddOrUpdate, guild);
									guild.boardcastInfo(Notify_AddOrUpdate);
								}
							}
						}
					}
				} else {
					log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
				}
			}
				break;
			case 2: {
				long guildid = Long.valueOf(message.getGuildevent());
				if (guildid != 0) {
					Guild guild = getGuildMapManager().FindById(guildid);
					if (guild != null) {
						guild.boardcastInfo(Notity_KingCity);
					}
				}
			}
				break;
			case 3:
				Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
				if (player != null) {
					Guild guild = getGuildMapManager().FindById(player.getGuildid());
					if (guild != null) {
						MemberInfo memberInfo = guild.findMemberInfo(player);
						if (memberInfo != null) {
							int contributionPoint = Integer.valueOf(message.getGuildevent());
							if (contributionPoint != 0 && memberInfo.getContributionPoint() >= contributionPoint) {
								memberInfo.setContributionPoint(memberInfo.getContributionPoint() - contributionPoint);
								saveGuildData(7, guild, null);
								sendGuildAndMemberInfo(null, player, Notify_AddOrUpdate, guild);
								guild.boardcastInfo(Notify_AddOrUpdate);
							}
						}
					}
				} else {
					log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
				}
				break;
			default:
				log.error("message eventtype error! message eventtype = " + message.getEventtype());
				break;
		}
	}

	/**
	 * 更换盟旗造型
	 *
	 * @param message 通知世界服务器更换盟旗造型
	 * @return
	 */
	public void reqInnerGuildChangeBannerIconToWorld(ReqInnerGuildChangeBannerIconToWorldMessage message) {
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
		if (player != null) {
			ResGuildChangeBannerIconToClientMessage sendMessage = new ResGuildChangeBannerIconToClientMessage();
			sendMessage.setBtErrorCode(Error_Fail);
			if (player.getGuildid() == message.getGuildId()) {
				Guild guild = getGuildMapManager().FindById(player.getGuildid());
				if (guild != null) {
					MemberInfo memberInfo = guild.findMemberInfo(player);
					if (memberInfo != null && memberInfo.getGuildPowerLevel() == 1) {
						if (message.getBannerIcon() != guild.getGuildInfo().getBannerIcon()) {
							GuildBanner guildBanner = getGuildBannerMap().get((int) guild.getGuildInfo().getBannerLevel());
							if (guildBanner != null) {
//								if (guildBanner.getMonsterList().contains(message.getBannerIcon())) {
									if (guildBanner.getChangicondragon() <= guild.getGuildInfo().getDragon()
										&& guildBanner.getChangiconwhitetiger() <= guild.getGuildInfo().getWhiteTiger()
										&& guildBanner.getChangiconsuzaku() <= guild.getGuildInfo().getSuzaku()
										&& guildBanner.getChangiconbasaltic() <= guild.getGuildInfo().getBasaltic()
										&& guildBanner.getChangicongold() <= guild.getGuildInfo().getStockGold()) {
										guild.getGuildInfo().setBannerIcon(message.getBannerIcon());
										guild.getGuildInfo().setDragon(guild.getGuildInfo().getDragon() - guildBanner.getChangicondragon());
										guild.getGuildInfo().setWhiteTiger(guild.getGuildInfo().getWhiteTiger() - guildBanner.getChangiconwhitetiger());
										guild.getGuildInfo().setSuzaku(guild.getGuildInfo().getSuzaku() - guildBanner.getChangiconsuzaku());
										guild.getGuildInfo().setBasaltic(guild.getGuildInfo().getBasaltic() - guildBanner.getChangiconbasaltic());
										guild.getGuildInfo().setStockGold(guild.getGuildInfo().getStockGold() - guildBanner.getChangicongold());
										guild.addParseEvent(ResManager.getInstance().getString("盟旗更换造型"), getEventString(ResManager.getInstance().getString("盟旗更换造型"), ResManager.getInstance().getString("忠诚徽章"), guildBanner.getChangicondragon(), ResManager.getInstance().getString("荣誉徽章"), guildBanner.getChangiconwhitetiger(),
											ResManager.getInstance().getString("守护徽章"), guildBanner.getChangiconsuzaku(), ResManager.getInstance().getString("勇敢徽章"), guildBanner.getChangiconbasaltic(), ResManager.getInstance().getString("金币"), guildBanner.getChangicongold()));
										saveGuildData(3, guild, null);

										sendAllGuildInfo(Notity_ChangeBanner, guild);
//										MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("恭喜您，盟旗造型修改成功"));
										guild.sendAllMemberNotify(Notifys.SUCCESS, ResManager.getInstance().getString("本盟盟主已启用了新的盟旗造型"));
										guild.boardcastInfo(Notity_BannerBuff);
										sendMessage.setBtErrorCode(Error_Success);
									} else {
										MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，更换造型所需消耗的盟贡仓库资源不足"));
									}
//								} else {
//									MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，您的盟旗造型选择错误"));
//								}
							}
						} else {
							MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("请选择一个新的盟旗造型"));
						}
					} else {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，只有盟主才可以修改盟旗"));
					}
				} else {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，没有找到该战盟"));
				}
			}
			MessageUtil.tell_player_message(player, sendMessage);
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
		}
	}

	/**
	 * 更换盟旗名字
	 *
	 * @param message 通知世界服务器更换盟旗名字
	 * @return
	 */
	public void reqInnerGuildChangeBannerNameToWorld(ReqInnerGuildChangeBannerNameToWorldMessage message) {
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
		if (player != null) {
			ResGuildChangeBannerNameToClientMessage sendMessage = new ResGuildChangeBannerNameToClientMessage();
			sendMessage.setBtErrorCode(Error_Fail);
			if (player.getGuildid() == message.getGuildId()) {
				Guild guild = getGuildMapManager().FindById(player.getGuildid());
				if (guild != null) {
					MemberInfo memberInfo = guild.findMemberInfo(player);
					if (memberInfo != null && memberInfo.getGuildPowerLevel() == 1) {
						if (getGuildMapManager().FindByBannerName(message.getBannerName()) == null) {
							if (!guild.getGuildInfo().getGuildBanner().equals(message.getBannerName())) {
								GuildBanner guildBanner = getGuildBannerMap().get((int) guild.getGuildInfo().getBannerLevel());
								if (guildBanner != null) {
									if (guildBanner.getChangenamedragon() <= guild.getGuildInfo().getDragon()
										&& guildBanner.getChangenamewhitetiger() <= guild.getGuildInfo().getWhiteTiger()
										&& guildBanner.getChangenamesuzaku() <= guild.getGuildInfo().getSuzaku()
										&& guildBanner.getChangenamebasaltic() <= guild.getGuildInfo().getBasaltic()
										&& guildBanner.getChangenamegold() <= guild.getGuildInfo().getStockGold()) {
										getGuildMapManager().remove(guild);
										guild.getGuildInfo().setGuildBanner(message.getBannerName());
										getGuildMapManager().put(guild);
										guild.getGuildInfo().setDragon(guild.getGuildInfo().getDragon() - guildBanner.getChangenamedragon());
										guild.getGuildInfo().setWhiteTiger(guild.getGuildInfo().getWhiteTiger() - guildBanner.getChangenamewhitetiger());
										guild.getGuildInfo().setSuzaku(guild.getGuildInfo().getSuzaku() - guildBanner.getChangenamesuzaku());
										guild.getGuildInfo().setBasaltic(guild.getGuildInfo().getBasaltic() - guildBanner.getChangenamebasaltic());
										guild.getGuildInfo().setStockGold(guild.getGuildInfo().getStockGold() - guildBanner.getChangenamegold());
										guild.addParseEvent(ResManager.getInstance().getString("盟旗改名"), getEventString(ResManager.getInstance().getString("盟旗改名"), guild.getGuildInfo().getGuildBanner(), ResManager.getInstance().getString("忠诚徽章"), guildBanner.getChangenamedragon(),
											ResManager.getInstance().getString("荣誉徽章"), guildBanner.getChangenamewhitetiger(), ResManager.getInstance().getString("守护徽章"), guildBanner.getChangenamesuzaku(), ResManager.getInstance().getString("勇敢徽章"), guildBanner.getChangenamebasaltic(), ResManager.getInstance().getString("金币"), guildBanner.getChangenamegold()));
										saveGuildData(3, guild, null);

										sendAllGuildInfo(Notity_ChangeBanner, guild);
//										MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("恭喜您，盟旗改名成功"));
										guild.sendAllMemberNotify(Notifys.SUCCESS, ResManager.getInstance().getString("本盟盟主启用了新的盟旗名称：{1}"), message.getBannerName());
										guild.boardcastInfo(Notity_BannerBuff);
										sendMessage.setBtErrorCode(Error_Success);
									} else {
										MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，改名所需消耗的盟贡仓库资源不足"));
									}
								}
							} else {
								MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("请输入一个新的盟旗名字"));
							}
						} else {
							MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，盟旗名字已被其他战盟占用"));
						}
					} else {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，只有盟主才可以修改盟旗"));
					}
				} else {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，没有找到该战盟"));
				}
			}
			MessageUtil.tell_player_message(player, sendMessage);
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
		}
	}

	/**
	 * 盟旗升级
	 *
	 * @param message 通知世界服务器盟旗升级
	 * @return
	 */
	public void reqInnerGuildBannerLevelUpToWorld(ReqInnerGuildBannerLevelUpToWorldMessage message) {
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
		if (player != null) {
			ResGuildBannerLevelUpToClientMessage sendMessage = new ResGuildBannerLevelUpToClientMessage();
			sendMessage.setBtErrorCode(Error_Fail);
			if (player.getGuildid() == message.getGuildId()) {
				Guild guild = getGuildMapManager().FindById(player.getGuildid());
				if (guild != null) {
					MemberInfo memberInfo = guild.findMemberInfo(player);
					if (memberInfo != null && (memberInfo.getGuildPowerLevel() == 1 || memberInfo.getGuildPowerLevel() == 2)) {
						if (guild.getGuildInfo().getBannerLevel() < getGuildBannerMap().size()) {
							GuildBanner nextGuildBanner = getGuildBannerMap().get((int) guild.getGuildInfo().getBannerLevel() + 1);
							if (nextGuildBanner != null) {
								if (nextGuildBanner.getLevelupdragon() <= guild.getGuildInfo().getDragon()
									&& nextGuildBanner.getLevelupwhitetiger() <= guild.getGuildInfo().getWhiteTiger()
									&& nextGuildBanner.getLevelupsuzaku() <= guild.getGuildInfo().getSuzaku()
									&& nextGuildBanner.getLevelupbasaltic() <= guild.getGuildInfo().getBasaltic()
									&& nextGuildBanner.getLevelupgold() <= guild.getGuildInfo().getStockGold()) {

									guild.getGuildInfo().setDragon(guild.getGuildInfo().getDragon() - nextGuildBanner.getLevelupdragon());
									guild.getGuildInfo().setWhiteTiger(guild.getGuildInfo().getWhiteTiger() - nextGuildBanner.getLevelupwhitetiger());
									guild.getGuildInfo().setSuzaku(guild.getGuildInfo().getSuzaku() - nextGuildBanner.getLevelupsuzaku());
									guild.getGuildInfo().setBasaltic(guild.getGuildInfo().getBasaltic() - nextGuildBanner.getLevelupbasaltic());
									guild.getGuildInfo().setStockGold(guild.getGuildInfo().getStockGold() - nextGuildBanner.getLevelupgold());

									if (nextGuildBanner != null && RandomUtils.nextInt(10000) <= nextGuildBanner.getSuccesscof()) {
										guild.getGuildInfo().setBannerLevel((byte) (guild.getGuildInfo().getBannerLevel() + 1));
//										guild.getGuildInfo().setBannerIcon(guild.getGuildInfo().getBannerIcon() + 100);
										guild.addParseEvent(ResManager.getInstance().getString("盟旗升级"), getEventString(ResManager.getInstance().getString("盟旗升级"), guild.getGuildInfo().getBannerLevel(), ResManager.getInstance().getString("忠诚徽章"), nextGuildBanner.getLevelupdragon(),
											ResManager.getInstance().getString("荣誉徽章"), nextGuildBanner.getLevelupwhitetiger(), ResManager.getInstance().getString("守护徽章"), nextGuildBanner.getLevelupsuzaku(), ResManager.getInstance().getString("勇敢徽章"), nextGuildBanner.getLevelupbasaltic(), ResManager.getInstance().getString("金币"), nextGuildBanner.getLevelupgold()));
										saveGuildData(3, guild, null);//"本盟盟旗已升级到[%d]级，消耗盟贡仓库：%s*%d；%s*%d；%s*%d；%s*%d；%s*%d"

										sendAllGuildInfo(Notity_ChangeBanner, guild);
//										MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("恭喜您，盟旗升级成功"));
										guild.sendAllMemberNotify(Notifys.SUCCESS, ResManager.getInstance().getString("<font color='#FFDE00'>{1}</font>战盟的盟旗升级至<font color='#FF0000'>{2}</font>级，他们变得更强大了"), guild.getGuildInfo().getGuildName(), String.valueOf(guild.getGuildInfo().getBannerLevel()));
										guild.sendAllMemberNotify(Notifys.CHAT_GROUP, ResManager.getInstance().getString("<font color='#FFDE00'>{1}</font>战盟盟旗升级至<font color='#FF0000'>{2}</font>级，他们变得更加强大了"), guild.getGuildInfo().getGuildName(), String.valueOf(guild.getGuildInfo().getBannerLevel()));
										guild.boardcastInfo(Notity_BannerBuff);
										sendMessage.setBtErrorCode(Error_Success);
										MessageUtil.notify_All_player(Notifys.SROLL, ResManager.getInstance().getString("<font color='#FFDE00'>{1}</font>战盟的盟旗升级至<font color='#FF0000'>{2}</font>级，他们变得更强大了"), guild.getGuildInfo().getGuildName(), String.valueOf(guild.getGuildInfo().getBannerLevel()));
									} else {
										guild.addParseEvent(ResManager.getInstance().getString("盟旗升级"), getEventString(ResManager.getInstance().getString("盟旗升级"), guild.getGuildInfo().getBannerLevel(), ResManager.getInstance().getString("忠诚徽章"), nextGuildBanner.getLevelupdragon(),
											ResManager.getInstance().getString("荣誉徽章"), nextGuildBanner.getLevelupwhitetiger(), ResManager.getInstance().getString("守护徽章"), nextGuildBanner.getLevelupsuzaku(), ResManager.getInstance().getString("勇敢徽章"), nextGuildBanner.getLevelupbasaltic(), ResManager.getInstance().getString("金币"), nextGuildBanner.getLevelupgold()));
										saveGuildData(3, guild, null);//"本盟盟旗已升级到[%d]级，消耗盟贡仓库：%s*%d；%s*%d；%s*%d；%s*%d；%s*%d"

										sendGuildInfo(player, Notify_AddOrUpdate, guild);
										MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，盟旗升级失败"));
										sendMessage.setBtErrorCode(Error_Fail);
									}
								} else {
									MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，升级所需消耗的盟贡仓库资源不足"));
								}
							}
						} else {
							MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您的盟旗等级已达顶级"));
						}
					} else {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，只有盟主才可以修改盟旗"));
					}
				} else {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，没有找到该战盟"));
				}
			}
			MessageUtil.tell_player_message(player, sendMessage);
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
		}
	}

	/**
	 * 添加外交关系
	 *
	 * @param message 通知世界服务器添加外交关系
	 * @return
	 */
	public void reqInnerGuildAddDiplomaticToWorld(ReqInnerGuildAddDiplomaticToWorldMessage message) {
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
		if (player != null) {
			ResGuildAddDiplomaticToClientMessage sendMessage = new ResGuildAddDiplomaticToClientMessage();
			sendMessage.setBtErrorCode(Error_Fail);
			if (player.getGuildid() == message.getGuildId()) {
				Guild guild = getGuildMapManager().FindById(player.getGuildid());
				Guild otherGuild = getGuildMapManager().FindById(message.getOtherGuildId());
				if (guild != null && otherGuild != null) {
					MemberInfo memberInfo = guild.findMemberInfo(player);
					if (memberInfo != null && memberInfo.getGuildPowerLevel() == 1) {
						if (guild.getGuildInfo().getGuildId() != otherGuild.getGuildInfo().getGuildId()) {
							if (message.getDiplomaticType() == ApplyAndInvite.Diplomatic_Friend_Type) {
								if (!findfriendDiplomaticInfo(guild.getGuildInfo(), otherGuild.getGuildInfo().getGuildId())) {
									if (!findenemyDiplomaticInfo(guild.getGuildInfo(), otherGuild.getGuildInfo().getGuildId())) {
										if (!findenemyDiplomaticInfo(otherGuild.getGuildInfo(), guild.getGuildInfo().getGuildId())) {
											Player otherPlayer = otherGuild.getPowerLevelPlayer(1);
											if (otherPlayer != null) {
												ApplyAndInvite applyAndInvite = otherGuild.findApplyAndInvite(guild.getGuildInfo().getGuildId());
												if (applyAndInvite == null && message.getArgee() == 1) {
													sendAllGuildInfoToSelf(otherPlayer);

													sendMessage.setDiplomaticType(message.getDiplomaticType());
													sendMessage.setApplyGuildId(guild.getGuildInfo().getGuildId());
													sendMessage.setBtErrorCode(Error_Success);
													MessageUtil.tell_player_message(otherPlayer, sendMessage);
													/*xiaozhuoming: 暂时没有用到
													MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("您的结盟邀请已发送，请等待对方盟主：{1} 的同意"), otherPlayer.getName());
													*/

													if (guild.findApplyAndInvite(otherGuild.getGuildInfo().getGuildId()) == null) {
														applyAndInvite = new ApplyAndInvite();
														applyAndInvite.setGuildid(otherGuild.getGuildInfo().getGuildId());
														applyAndInvite.setType(ApplyAndInvite.Diplomatic_Friend_Type);
														applyAndInvite.setSrcid(guild.getGuildInfo().getGuildId());
														applyAndInvite.setDestid(otherGuild.getGuildInfo().getGuildId());
														guild.getGuildapplyAndInviteList().add(applyAndInvite);
													}
												} else {
													if (applyAndInvite != null && applyAndInvite.getDestid() == guild.getGuildInfo().getGuildId() && applyAndInvite.getSrcid() == otherGuild.getGuildInfo().getGuildId()) {
														if (message.getArgee() == 1) {
															DiplomaticInfo diplomaticInfo = new DiplomaticInfo();
															diplomaticInfo.setDiplomaticTime((int) (System.currentTimeMillis() / 1000));
															diplomaticInfo.setGuildId(guild.getGuildInfo().getGuildId());
															otherGuild.getGuildInfo().getFriendGuildList().add(diplomaticInfo);
															sendGuildInfo(otherPlayer, Notify_AddOrUpdate, otherGuild);
															otherGuild.addParseEvent(ResManager.getInstance().getString("行会结盟"), getEventString(ResManager.getInstance().getString("行会结盟"), guild.getGuildInfo().getGuildName()));
															saveGuildData(3, otherGuild, null);
															/*xiaozhuoming: 暂时没有用到
															MessageUtil.notify_player(otherPlayer, Notifys.SUCCESS, ResManager.getInstance().getString("对方盟主：{1}已经同意了您的结盟邀请"), player.getName());
															otherGuild.sendAllMemberNotify(Notifys.SUCCESS, ResManager.getInstance().getString("恭喜恭喜，您的战盟与{1}战盟结成联盟，并肩战斗"), guild.getGuildInfo().getGuildName());
															*/
															otherGuild.boardcastInfo(Notity_DiplomaticChange);

															DiplomaticInfo otherDiplomaticInfo = new DiplomaticInfo();
															otherDiplomaticInfo.setDiplomaticTime((int) (System.currentTimeMillis() / 1000));
															otherDiplomaticInfo.setGuildId(otherGuild.getGuildInfo().getGuildId());
															guild.getGuildInfo().getFriendGuildList().add(otherDiplomaticInfo);
															sendGuildInfo(player, Notify_AddOrUpdate, guild);
															guild.addParseEvent(ResManager.getInstance().getString("行会结盟"), getEventString(ResManager.getInstance().getString("行会结盟"), otherGuild.getGuildInfo().getGuildName()));
															saveGuildData(3, guild, null);
															/*xiaozhuoming: 暂时没有用到
															guild.sendAllMemberNotify(Notifys.SUCCESS, ResManager.getInstance().getString("恭喜恭喜，您的战盟与{1}战盟结成联盟，并肩战斗"), otherGuild.getGuildInfo().getGuildName());
															*/
															guild.boardcastInfo(Notity_DiplomaticChange);
															/*xiaozhuoming: 暂时没有用到
															MessageUtil.notify_All_player(Notifys.SROLL, ResManager.getInstance().getString("[战盟]{1}与[战盟]{2}结成联盟，江湖风起云涌"), guild.getGuildInfo().getGuildName(), otherGuild.getGuildInfo().getGuildName());
															*/
														} else {
															/*xiaozhuoming: 暂时没有用到
															MessageUtil.notify_player(otherPlayer, Notifys.ERROR, ResManager.getInstance().getString("很遗憾，对方盟主：{1}拒绝了您的结盟邀请"), player.getName());
															MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您拒绝了{1}战盟的结盟请求"), otherGuild.getGuildInfo().getGuildName());
															*/
														}
														otherGuild.deleteApplyAndInvite(guild.getGuildInfo().getGuildId());
													}
												}
											} else {
												/*xiaozhuoming: 暂时没有用到
												MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("战盟结盟需对方盟主同意，但他目前不在线"));
												*/
											}
										} else {
											/*xiaozhuoming: 暂时没有用到
											MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您是该战盟的敌对战盟，如需结盟请先联系其盟主：{1} 解除敌对关系"), otherGuild.getGuildInfo().getBangZhuName());
											*/
										}
									} else {
										/*xiaozhuoming: 暂时没有用到
										MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("该战盟是您的敌对战盟，如需结盟请先解除外交关系"));
										*/
									}
								} else {
									/*xiaozhuoming: 暂时没有用到
									MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("该战盟已经是您的联盟战盟了"));
									*/
								}
							} else if (message.getDiplomaticType() == ApplyAndInvite.Diplomatic_Enemy_Type) {
								if (!findfriendDiplomaticInfo(guild.getGuildInfo(), otherGuild.getGuildInfo().getGuildId())) {
									if (!findenemyDiplomaticInfo(guild.getGuildInfo(), otherGuild.getGuildInfo().getGuildId())) {
										DiplomaticInfo diplomaticInfo = new DiplomaticInfo();
										diplomaticInfo.setGuildId(otherGuild.getGuildInfo().getGuildId());
										diplomaticInfo.setDiplomaticTime((int) (System.currentTimeMillis() / 1000));
										guild.getGuildInfo().getEnemyGuildList().add(diplomaticInfo);
										sendGuildInfo(player, Notify_AddOrUpdate, guild);
										guild.addParseEvent(ResManager.getInstance().getString("本盟添加其他行会敌对"), getEventString(ResManager.getInstance().getString("本盟添加其他行会敌对"), otherGuild.getGuildInfo().getGuildName()));
										saveGuildData(3, guild, null);
										/*xiaozhuoming: 暂时没有用到
										MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("成功添加战盟：{1}为敌对战盟"), otherGuild.getGuildInfo().getGuildName());
										guild.sendAllMemberNotify(Notifys.SUCCESS, ResManager.getInstance().getString("本战盟已将{1}战盟列为敌对战盟"), otherGuild.getGuildInfo().getGuildName());
										*/
										guild.boardcastInfo(Notity_DiplomaticChange);
										/*xiaozhuoming: 暂时没有用到
										otherGuild.sendAllMemberNotify(Notifys.SUCCESS, ResManager.getInstance().getString("{1}战盟已将本盟列为敌对战盟"), guild.getGuildInfo().getGuildName());
										*/
										otherGuild.addParseEvent(ResManager.getInstance().getString("本盟被其他行会加为敌对"), getEventString(ResManager.getInstance().getString("本盟被其他行会加为敌对"), guild.getGuildInfo().getGuildName()));
										/*xiaozhuoming: 暂时没有用到
										MessageUtil.notify_All_player(Notifys.SROLL, ResManager.getInstance().getString("[战盟]{1}将[战盟]{2}列入了敌对战盟名单"), guild.getGuildInfo().getGuildName(), otherGuild.getGuildInfo().getGuildName());
										*/
										Player otherPlayer = otherGuild.getPowerLevelPlayer(1);
										if (otherPlayer != null) {
											sendAllGuildInfoToSelf(otherPlayer);

											sendMessage.setDiplomaticType(message.getDiplomaticType());
											sendMessage.setApplyGuildId(guild.getGuildInfo().getGuildId());
											sendMessage.setBtErrorCode(Error_Success);
											MessageUtil.tell_player_message(otherPlayer, sendMessage);
										} else {
											//弹出ICO提示
											ApplyAndInvite applyAndInvite = new ApplyAndInvite();
											applyAndInvite.setGuildid(guild.getGuildInfo().getGuildId());
											applyAndInvite.setType(message.getDiplomaticType());
											applyAndInvite.setSrcid(1);
											otherGuild.getSaveSendMsgList().add(applyAndInvite);
										}
									} else {
										/*xiaozhuoming: 暂时没有用到
										MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("该战盟已经是您的敌对战盟"));
										*/
									}
								} else {
									/*xiaozhuoming: 暂时没有用到
									MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("该战盟是您的联盟战盟，如需敌对请先解除外交关系"));
									*/
								}
							}
						} else {
							if (message.getDiplomaticType() == ApplyAndInvite.Diplomatic_Friend_Type) {
								/*xiaozhuoming: 暂时没有用到
								MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，您无法将自己的战盟列为联盟"));
								*/
							} else if (message.getDiplomaticType() == ApplyAndInvite.Diplomatic_Enemy_Type) {
								/*xiaozhuoming: 暂时没有用到
								MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，您无法将自己的战盟列为敌对"));
								*/
							}
						}
					} else {
						/*xiaozhuoming: 暂时没有用到
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，只有盟主才能设立外交关系"));
						*/
					}
				} else {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，没有找到该战盟"));
				}
			}
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
		}
	}

	/**
	 * 删除外交关系
	 *
	 * @param message 通知世界服务器删除外交关系
	 * @return
	 */
	public void reqInnerGuildDeleteDiplomaticToWorld(ReqInnerGuildDeleteDiplomaticToWorldMessage message) {
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
		if (player != null) {
			ResGuildDeleteDiplomaticToClientMessage sendMessage = new ResGuildDeleteDiplomaticToClientMessage();
			sendMessage.setBtErrorCode(Error_Fail);
			if (player.getGuildid() == message.getGuildId()) {
				Guild guild = getGuildMapManager().FindById(player.getGuildid());
				Guild otherGuild = getGuildMapManager().FindById(message.getOtherGuildId());
				if (guild != null && otherGuild != null) {
					MemberInfo memberInfo = guild.findMemberInfo(player);
					if (memberInfo != null && memberInfo.getGuildPowerLevel() == 1) {
						if (guild.getGuildInfo().getGuildId() != otherGuild.getGuildInfo().getGuildId()) {
							if (message.getDiplomaticType() == ApplyAndInvite.Diplomatic_Friend_Type) {
								deletefriendDiplomaticInfo(otherGuild.getGuildInfo(), guild.getGuildInfo().getGuildId());
								saveGuildData(3, otherGuild, null);
								deletefriendDiplomaticInfo(guild.getGuildInfo(), otherGuild.getGuildInfo().getGuildId());
								sendGuildInfo(player, Notify_AddOrUpdate, guild);
								saveGuildData(3, guild, null);
								/*xiaozhuoming: 暂时没有用到
								MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("与[战盟]{1}的联盟关系解除成功"), otherGuild.getGuildInfo().getGuildName());
								guild.sendAllMemberNotify(Notifys.SUCCESS, ResManager.getInstance().getString("本盟与战盟：[{1}]之间的联盟关系解除了"), otherGuild.getGuildInfo().getGuildName());
								*/
								guild.boardcastInfo(Notity_DiplomaticChange);
								/*xiaozhuoming: 暂时没有用到
								otherGuild.sendAllMemberNotify(Notifys.SUCCESS, ResManager.getInstance().getString("战盟：[{1}]解除了与本盟之间的联盟关系"), guild.getGuildInfo().getGuildName());
								*/
								otherGuild.boardcastInfo(Notity_DiplomaticChange);
								/*xiaozhuoming: 暂时没有用到
								MessageUtil.notify_All_player(Notifys.SROLL, ResManager.getInstance().getString("[战盟]{1}与[战盟]{2}解除了联盟关系，江湖风起云涌"), guild.getGuildInfo().getGuildName(), otherGuild.getGuildInfo().getGuildName());
								*/
								Player otherPlayer = otherGuild.getPowerLevelPlayer(1);
								if (otherPlayer != null) {
									sendMessage.setDiplomaticType(message.getDiplomaticType());
									sendMessage.setApplyGuildId(guild.getGuildInfo().getGuildId());
									sendMessage.setBtErrorCode(Error_Success);
									MessageUtil.tell_player_message(otherPlayer, sendMessage);
								} else {
									//弹出ICO提示
									ApplyAndInvite applyAndInvite = new ApplyAndInvite();
									applyAndInvite.setGuildid(guild.getGuildInfo().getGuildId());
									applyAndInvite.setType(message.getDiplomaticType());
									applyAndInvite.setSrcid(2);
									otherGuild.getSaveSendMsgList().add(applyAndInvite);
								}
							} else if (message.getDiplomaticType() == ApplyAndInvite.Diplomatic_Enemy_Type) {
								deleteenemyDiplomaticInfo(guild.getGuildInfo(), otherGuild.getGuildInfo().getGuildId());
								sendGuildInfo(player, Notify_AddOrUpdate, guild);
								saveGuildData(3, guild, null);
								/*xiaozhuoming: 暂时没有用到
								MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("您解除了对[战盟]{1}的敌对状态"), otherGuild.getGuildInfo().getGuildName());
								guild.sendAllMemberNotify(Notifys.SUCCESS, ResManager.getInstance().getString("本盟解除了对战盟：[{1}]的敌对状态"), otherGuild.getGuildInfo().getGuildName());
								*/
								guild.boardcastInfo(Notity_DiplomaticChange);
								/*xiaozhuoming: 暂时没有用到
								otherGuild.sendAllMemberNotify(Notifys.SUCCESS, ResManager.getInstance().getString("战盟：[{1}]解除了对本盟的敌对状态"), guild.getGuildInfo().getGuildName());

								MessageUtil.notify_All_player(Notifys.SROLL, ResManager.getInstance().getString("[战盟]{1}解除了对[战盟]{2}的敌对状态，江湖风起云涌"), guild.getGuildInfo().getGuildName(), otherGuild.getGuildInfo().getGuildName());
								*/
								Player otherPlayer = otherGuild.getPowerLevelPlayer(1);
								if (otherPlayer != null) {
									sendMessage.setDiplomaticType(message.getDiplomaticType());
									sendMessage.setApplyGuildId(guild.getGuildInfo().getGuildId());
									sendMessage.setBtErrorCode(Error_Success);
									MessageUtil.tell_player_message(otherPlayer, sendMessage);
								} else {
									//弹出ICO提示
									ApplyAndInvite applyAndInvite = new ApplyAndInvite();
									applyAndInvite.setGuildid(guild.getGuildInfo().getGuildId());
									applyAndInvite.setType(message.getDiplomaticType());
									applyAndInvite.setSrcid(2);
									otherGuild.getSaveSendMsgList().add(applyAndInvite);
								}
							}
						} else {
							if (message.getDiplomaticType() == ApplyAndInvite.Diplomatic_Friend_Type) {
								MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，您无法将自己的战盟进行此操作"));
							} else if (message.getDiplomaticType() == ApplyAndInvite.Diplomatic_Enemy_Type) {
								MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，您无法将自己的战盟进行此操作"));
							}
						}
					} else {
						/*xiaozhuoming: 暂时没有用到
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，只有盟主才能设立外交关系"));
						*/
					}
				} else {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，没有找到该战盟"));
				}
			}
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
		}
	}

	/**
	 * 执行解散战盟
	 *
	 * @param guild 战盟
	 * @param player 玩家
	 * @param boNotify 是否发送消息
	 * @return boolean
	 * @return title
	 * @return content
	 */
	public boolean deleteGuildFromAll(Guild guild, Player player, boolean boNotify, String title, String content) {
		if (getGuildMapManager().remove(guild)) {//数据库
			if (deleteGuild(guild)) {
				List<Player> players = new ArrayList<Player>();
				List<String> playerNameList = new ArrayList<String>();
				for (Map.Entry<Long, MemberInfo> entry : guild.getMemberinfoHashMap().entrySet()) {
					MemberInfo deletememberInfo = entry.getValue();
					if (deletememberInfo != null) {
						Player deletePlayer = PlayerManager.getInstance().getPlayer(deletememberInfo.getUserId());
						if (deletePlayer != null) {
							players.add(deletePlayer);
						}
						guild.deleteMember(deletememberInfo);
					}
					playerNameList.add(deletememberInfo.getUserName());
				}
				for (int i = 0; i < guild.getEventInfoList().size(); i++) {
					EventInfo eventInfo = guild.getEventInfoList().get(i);
					if (eventInfo != null) {
						guild.deleteEvent(eventInfo);
					}
				}
				for (Map.Entry<Long, Guild> entry : getGuildMapManager().entrySet()) {
					Guild allguild = entry.getValue();
					if (allguild != null) {
						boolean bofind = false;
						if (deletefriendDiplomaticInfo(allguild.getGuildInfo(), guild.getGuildInfo().getGuildId())) {
							bofind = true;
						}
						if (deleteenemyDiplomaticInfo(allguild.getGuildInfo(), guild.getGuildInfo().getGuildId())) {
							bofind = true;
						}
						if (bofind) {
							saveGuildData(3, allguild, null);
							allguild.boardcastInfo(Notity_DiplomaticChange);
						}
					}
				}
				//消息
				if (boNotify && player != null) {
					guild.sendAllMemberNotify(Notifys.SUCCESS, getParseString(String.format("您的战盟：<font color='#FFDE00'>%s</font>，被[盟主]<font color='#FFDE00'>{@}</font>解散了。", guild.getGuildInfo().getGuildName()), player));
					guild.sendAllMemberNotify(Notifys.CHAT_GROUP, getParseString(String.format("您的战盟：<font color='#FFDE00'>%s</font>，被[盟主]<font color='#FFDE00'>{@}</font>解散了。", guild.getGuildInfo().getGuildName()), player));
				}
				for (int i = 0; i < players.size(); i++) {
					Player deletePlayer = players.get(i);
					if (deletePlayer != null) {
						if (i == players.size() - 1) {
							sendGuildAndMemberInfo(null, deletePlayer, Notity_DeleteGuild, guild);
						} else {
							sendGuildAndMemberInfo(null, deletePlayer, Notify_Delete, guild);
						}
						guild.removeMember(deletePlayer);
					}
				}
				//发邮件通知战盟解散
				for(String playerName : playerNameList) {
					MailWorldManager.getInstance().sendSystemMail(playerName, title, content, (byte) 1, 0, null);
				}
				setGuildLog(guild, player == null ? "系统删除" : player.getName() + "玩家删除", "成员数量=" + guild.getMemberinfoHashMap().size() + "=公会信息=" + JSON.toJSONString(guild.getGuildInfo()));
				return true;
			}
		}
		return false;
	}

	/**
	 * 解散战盟
	 *
	 * @param message 通知世界服务器解散战盟
	 * @return
	 */
	public void reqInnerGuildDeleteGuildToWorld(ReqInnerGuildDeleteGuildToWorldMessage message) {
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
		if (player != null) {
			ResGuildDeleteGuildToClientMessage sendMessage = new ResGuildDeleteGuildToClientMessage();
			sendMessage.setBtErrorCode(Error_Fail);
			if (player.getGuildid() == message.getGuildId()) {
				Guild guild = getGuildMapManager().FindById(player.getGuildid());
				if (guild != null) {
					MemberInfo memberInfo = guild.findMemberInfo(player);
					if (memberInfo != null && memberInfo.getGuildPowerLevel() == 1) {
						String title = "战盟解散";
						String content = "您的战盟于"+ TimeUtil.getNowStringDateForMail() + "被" + player.getName() + "(盟主)解散。";
						if (deleteGuildFromAll(guild, player, true, title, content)) {
							sendMessage.setBtErrorCode(Error_Success);
						}
					} else {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉、只有盟主才有权限解散战盟"));
					}
				} else {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，没有找到该战盟"));
				}
			}
			MessageUtil.tell_player_message(player, sendMessage);
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
		}
	}

	/**
	 * 获取战盟事件列表
	 *
	 * @param message 通知世界服务器获取战盟事件列表
	 * @return
	 */
	public void reqInnerGuildGetEventListToWorld(ReqInnerGuildGetEventListToWorldMessage message) {
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
		if (player != null) {
			ResGuildGetEventListToClientMessage sendMessage = new ResGuildGetEventListToClientMessage();
			sendMessage.setBtErrorCode(Error_Fail);
			if (player.getGuildid() == message.getGuildId()) {
				Guild guild = getGuildMapManager().FindById(player.getGuildid());
				if (guild != null) {
					for (int i = 0; i < guild.getEventInfoList().size(); i++) {
						EventInfo eventInfo = guild.getEventInfoList().get(i);
						if (eventInfo != null) {
							sendMessage.getEventList().add(eventInfo);
						}
					}
					sendMessage.setBtErrorCode(Error_Success);
				} else {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，没有找到该战盟"));
				}
			}
			MessageUtil.tell_player_message(player, sendMessage);
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
		}
	}

	/**
	 * 内部战盟通知消息
	 *
	 * @param message 消息
	 * @return
	 */
	public void reqInnerGuildNotifyToWorld(ReqInnerGuildNotifyToWorldMessage message) {
		Player player = PlayerManager.getInstance().getPlayer(message.getPlayerId());
		if (player != null) {
			if (player.getGuildid() != 0) {
				Guild guild = getGuild(player.getGuildid());
				if (guild != null) {
					switch (message.getNotifytype()) {
						case 0: {
							guild.sendAllMemberNotifyNoMe(player, message.getNotify(), message.getGuildNotify(), message.getSubType());
							guild.addEvent(ResManager.getInstance().getString("成员被其他玩家击败"), message.getGuildNotify());
						}
						break;
						case 1: {
							//guild.sendAllMemberNotifyNoMe(player, Notifys.CUTOUT, message.getGuildNotify());
							guild.addEvent(ResManager.getInstance().getString("成员击败其他玩家"), message.getGuildNotify());
						}
						break;
						case 2: {
							guild.addEvent(ResManager.getInstance().getString("成员击败BOSS"), message.getGuildNotify());
						}
						break;
						case 3: {
							guild.addEvent(ResManager.getInstance().getString("成员被BOSS击败"), message.getGuildNotify());
						}
						break;
					}
				}
			}
		} else {
			log.error(String.format("没有找到玩家 玩家ID[%s]", String.valueOf(message.getPlayerId())));
		}
	}

	public boolean findfriendDiplomaticInfo(GuildInfo guildInfo, long findguildid) {
		for (int i = 0; i < guildInfo.getFriendGuildList().size(); i++) {
			DiplomaticInfo diplomaticInfo = guildInfo.getFriendGuildList().get(i);
			if (diplomaticInfo != null && diplomaticInfo.getGuildId() == findguildid) {
				return true;
			}
		}
		return false;
	}

	public boolean findenemyDiplomaticInfo(GuildInfo guildInfo, long findguildid) {
		for (int i = 0; i < guildInfo.getEnemyGuildList().size(); i++) {
			DiplomaticInfo diplomaticInfo = guildInfo.getEnemyGuildList().get(i);
			if (diplomaticInfo != null && diplomaticInfo.getGuildId() == findguildid) {
				return true;
			}
		}
		return false;
	}

	public boolean deletefriendDiplomaticInfo(GuildInfo guildInfo, long deleteguildid) {
		for (int i = 0; i < guildInfo.getFriendGuildList().size(); i++) {
			DiplomaticInfo diplomaticInfo = guildInfo.getFriendGuildList().get(i);
			if (diplomaticInfo != null && diplomaticInfo.getGuildId() == deleteguildid) {
				guildInfo.getFriendGuildList().remove(diplomaticInfo);
				return true;
			}
		}
		return false;
	}

	public boolean deleteenemyDiplomaticInfo(GuildInfo guildInfo, long deleteguildid) {
		for (int i = 0; i < guildInfo.getEnemyGuildList().size(); i++) {
			DiplomaticInfo diplomaticInfo = guildInfo.getEnemyGuildList().get(i);
			if (diplomaticInfo != null && diplomaticInfo.getGuildId() == deleteguildid) {
				guildInfo.getEnemyGuildList().remove(diplomaticInfo);
				return true;
			}
		}
		return false;
	}

	public void setGuildLog(Guild guild, String runName, String logString) {
		GuildLog guildLog = new GuildLog();
		guildLog.setGuildId(guild.getGuildInfo().getGuildId());
		guildLog.setGuildName(guild.getGuildInfo().getGuildName());
		guildLog.setRunName(runName);
		guildLog.setLogString(logString);
		LogService.getInstance().execute(guildLog);
	}

	/**
	 * 发给所有成员的文字消息
	 *
	 * @param tid
	 * @param type
	 * @param message
	 * @param values
	 */
	public void notify_guild_all_player(long tid, String message) {
		Guild guild = getGuild(tid);
		if (guild != null) {
			ArrayList<Long> list = new ArrayList<Long>();
			Iterator<MemberInfo> it = guild.getMemberinfoHashMap().values().iterator();
			while (it.hasNext()) {
				list.add(it.next().getUserId());
			}
			MessageUtil.notify_player(list, Notifys.CHAT_GROUP, message);
		}
	}
	
	/**
	 * 战斗力更新
	 * @param msg
	 */
	public void reqInnerGuildUpdateFightPower(ReqInnerGuildUpdateFightPowerToWorldMessage msg) {
		Player player = ManagerPool.playerManager.getPlayer(msg.getPlayerId());
		if(player != null) {
			Guild guild = getGuild(player.getGuildid());
			if(guild != null) {
				player.setFightPower(msg.getFightPower());
				guild.Update();
				saveGuildData(7, guild, guild.findMemberInfo(player));
				sendGuildAndMemberInfo(null, player, Notify_AddOrUpdate, guild);
				sendOtherGuildInfo(Notify_AddOrUpdate, guild, player);
				sendOtherMemberInfo(Notify_AddOrUpdate, guild, player);		
				
				
			}
		}
	}
	
	/**
	 * 玩家切换地图更新战盟成员信息
	 * @param player
	 */
	public void enterMapGuildUpdate(Player player) {
		if(player != null) {
			Guild guild = this.getGuild(player.getGuildid());
			if(guild != null) {
				MemberInfo memberInfo = guild.findMemberInfo(player);
				if(memberInfo != null) {
					memberInfo.setMapId(player.getMap());
					sendMemberInfo(null, player, Notify_AddOrUpdate, guild);
					sendOtherMemberInfo(Notify_AddOrUpdate, guild, player);
				}
			}
		}
	}
	
	private String getParseString(String parseString, Player player) {
		ParseUtil parse = new ParseUtil();
		parse.setValue(ResManager.getInstance().getString(parseString), new ParseUtil.PlayerParm(player.getId(), player.getName()));
		return parse.toString();
	}
}
