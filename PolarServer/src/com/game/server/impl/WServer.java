package com.game.server.impl;

import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.quartz.SchedulerException;

import com.game.attribute.ActivateAttributeManager;
import com.game.cache.AbstractWork;
import com.game.cache.executor.NonOrderedQueuePoolExecutor;
import com.game.cache.executor.OrderedQueuePoolExecutor;
import com.game.cache.impl.MemoryCache;
import com.game.command.Handler;
import com.game.data.bean.Q_mapBean;
import com.game.data.manager.DataManager;
import com.game.db.bean.Gold;
import com.game.dblog.LogService;
import com.game.json.JSONserializable;
import com.game.login.message.ReqQuitToGameMessage;
import com.game.manager.ManagerPool;
import com.game.message.Message;
import com.game.message.pool.MessagePool;
import com.game.mina.IServer;
import com.game.mina.impl.ClientServer;
import com.game.newactivity.NewActivityManager;
import com.game.newactivity.PlayerActivityInfoManager;
import com.game.pet.struts.Pet;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.script.structs.ScriptEnum;
import com.game.server.config.CheckConfig;
import com.game.server.config.GameConfig;
import com.game.server.config.MailConfig;
import com.game.server.config.MapConfig;
import com.game.server.config.ServerHeartConfig;
import com.game.server.config.ServerType;
import com.game.server.loader.CheckConfigXmlLoader;
import com.game.server.loader.GameConfigXmlLoader;
import com.game.server.loader.MailConfigXmlloader;
import com.game.server.loader.ServerHeartConfigXmlLoader;
import com.game.server.log.ServerFinancialLog;
import com.game.server.log.ServerOnlineCountLog;
import com.game.server.log.ServerStartAndStopLog;
import com.game.server.message.ReqRegisterGameForPublicMessage;
import com.game.server.message.ReqRegisterGateMessage;
import com.game.server.message.ReqRegisterWorldMessage;
import com.game.server.message.ResDiscardMsgMessage;
import com.game.server.script.IServerStartScript;
import com.game.server.thread.SaveGoldExpendThread;
import com.game.server.thread.SaveGoldRaffleEventThread;
import com.game.server.thread.SaveGoldRaffleThread;
import com.game.server.thread.SaveGoldThread;
import com.game.server.thread.SaveMailThread;
import com.game.server.thread.SaveMarriageThread;
import com.game.server.thread.SavePlayerThread;
import com.game.server.thread.SaveProtectMailThread;
import com.game.server.thread.SaveProtectThread;
import com.game.server.thread.SaveServerParamThread;
import com.game.server.thread.SaveThread;
import com.game.server.thread.SaveWeddingThread;
import com.game.server.thread.SchedularThread;
import com.game.server.thread.ServerThread;
import com.game.server.timer.ServerHeartTimer;
import com.game.systemgrant.manager.SystemgrantManager;
import com.game.timer.SchedulerBean;
import com.game.timer.SchedulerManager;
import com.game.timer.SchedulerParser;
import com.game.utils.FileUtil;
import com.game.utils.Global;
import com.game.utils.HttpUtil;
import com.game.utils.MessageUtil;
import com.game.utils.ServerParamUtil;
import com.game.utils.Symbol;
import com.game.utils.TimeUtil;
import com.game.zones.timer.ZoneTeamTimer;

/**
 * 
 *  游戏服务器
 */
public class WServer extends ClientServer {
	/**
	 * 自身日志
	 */
	private static Logger log = Logger.getLogger(WServer.class);
	/**
	 * 流量日志
	 */
	private static Logger flowlog = Logger.getLogger("SERVERFLOW");
	/**
	 * 步骤日志
	 */
	private static Logger steplog = Logger.getLogger("SERVERSTEP");
	/**
	 * session关闭日志
	 */
	private static Logger closelog = Logger.getLogger("SERVERSESSIONCLOSE");
	/**
	 * 降落命令日志
	 */
	private static Logger droplog = Logger.getLogger("DROPCOMMAND");
	/**
	 * 用于同步使用的的obj
	 */
	private static Object obj = new Object();
	/**
	 * 服务器实例
	 */
	private static WServer server;

	/**
	 * 消息池
	 */
	private static MessagePool message_pool = new MessagePool();

	/**
	 * 服务线程
	 */
	private ServerThread wServerThread;
	/**
	 * 计划任务线程
	 */
	private SchedularThread wSchedularThread;
	/**
	 * 保存玩家线程
	 */
	private SavePlayerThread wSavePlayerThread;
	/**
	 * 保存线程
	 */
	private SaveThread wSaveThread;
	/**
	 * 保存邮件线程
	 */
	private SaveMailThread wSaveMailThread;
	/**
	 * 保存服务参数线程
	 */
	private SaveServerParamThread wSaveServerParamThread;
	/**
	 * 保存金币线程
	 */
	private SaveGoldThread wSaveGoldThread;
	/**
	 * 保存消耗金币线程
	 */
	private SaveGoldExpendThread wSaveGoldExpendThread;

	/**
	 * 保存钻石抽奖线程
	 */
	private SaveGoldRaffleThread wSaveGoldRaffleThread;
	
	/**
	 * 保存钻石抽奖日志线程
	 */
	private SaveGoldRaffleEventThread wSaveGoldRaffleEventThread;
	
	/**
	 * 组队线程
	 */
	private ServerThread wTeamZoneThread;
	
	/**
	 * 结婚信息保存线程
	 */
	private SaveMarriageThread wSaveMarriageThread;
	/**
	 * 婚宴列表保存线程
	 */
	private SaveWeddingThread wSaveWeddingThread;
	/**
	 * 2级密码信息保存线程
	 */
	private SaveProtectThread wSaveProtectThread;
	/**
	 * 邮件保存线程
	 */
	private SaveProtectMailThread wSaveProtectMailThread;
	/**
	 * 服务器启动线程组
	 */
	private ThreadGroup thread_group;
	/**
	 * 默认的游戏配置文件
	 */
	private static final String defaultGameConfig = "server-config/game-config.xml";
	/**
	 * 默认内部客户服务器配置文件
	 */
	private static final String defaultClientServerConfig = "server-config/client-server-config.xml";
	/**
	 * 默认公共服务器配置文件
	 */
	private static final String defaultPublicServerConfig = "server-config/public-server-config.xml";
	/**
	 * 默认定时调度配置文件
	 */
	private static final String DefaultSchedulerConfig = "server-config/quartz.txt";
	/**
	 * 线服务线程组
	 */
	private static ConcurrentHashMap<Integer, MServer> mServers = new ConcurrentHashMap<Integer, MServer>();
	/**
	 * 服务器配置
	 */
	private static GameConfig config;
	/**
	 * 邮件服务器参数
	 */
	private static MailConfig mailConfig;

	/**
	 * 玩家检查参数配置
	 */
	public static CheckConfig checkconfig;
	/**
	 * 服务器心跳参数配置
	 */
	public static ServerHeartConfig serverheartconfig;
	/**
	 * 连接公共服务器
	 */
	private static ClientServer publicServer = null;
	/**
	 * 连接公共服务器Session
	 */
	private static IoSession publicSession = null;
	/**
	 * 启动唯一标识
	 */
	public static String startidentity = "";
	/**
	 * 累积消耗绑钻
	 */
	public static AtomicLong sconsumebindgold = new AtomicLong(0L);
	/**
	 * 累积消耗金币
	 */
	public static AtomicLong sconsumemoney = new AtomicLong(0L);
	
	/**
	 * 累计消耗精魄
	 */
	public static AtomicLong sconsumespirit = new AtomicLong(0L);
	
	/**
	 * 累积生成绑钻
	 */
	public static AtomicLong sgeneratebindgold = new AtomicLong(0L);
	/**
	 * 累积生成金币
	 */
	public static AtomicLong sgeneratemoney = new AtomicLong(0L);
	
	/**
	 * 累计生成精魄
	 */
	public static AtomicLong sgeneratespirit = new AtomicLong(0L);
	
	/**
	 * 延迟同步HashMap
	 */
	public static ConcurrentHashMap<String, Integer> delay = new ConcurrentHashMap<String, Integer>();
	/**
	 * 网关消息解析队列(排序)
	 */
	private OrderedQueuePoolExecutor decodeExcutor = new OrderedQueuePoolExecutor(
			"网关消息解析队列", 100, 10000);
	/**
	 * 世界消息解析队列(排序)
	 */
	private OrderedQueuePoolExecutor worldExcutor = new OrderedQueuePoolExecutor(
			"世界消息解析队列", 1, -1);
	/**
	 * 命令执行池(无序)
	 */
	private NonOrderedQueuePoolExecutor commandExcutor = new NonOrderedQueuePoolExecutor(
			20);//panic god 暂时改为20个
	/**
	 * 世界命令执行池(无序)
	 */
	private NonOrderedQueuePoolExecutor worldCommandExcutor = new NonOrderedQueuePoolExecutor(
			10);
	/**
	 * 连接是否成功
	 */
	private boolean connnetSuccess = false;
	// //副本数量
	// private int zones_number = 0;
	// //副本总创建数量
	// private int zones_top = 0;

	/**
	 * 启动完成
	 */
	public static boolean STARTFINISH = false;
	/**
	 * 版本号
	 */
	public static int VERSION = 10000;
	/**
	 * 开始启动的时间
	 */
	private static long startTime = 0;
	/**
	 * 是否开启跨服
	 */
	private static boolean openPublic = false;

	/**
	 * 服务器构造函数(传入参数文件)
	 * 
	 * @param serverConfig
	 */
	public WServer(String serverConfig) {
		super(serverConfig);
	}

	/**
	 * 服务器构造函数(采用默认参数文件)
	 */
	public WServer() {
		this(defaultClientServerConfig);
	}

	/**
	 * 取得服务器实例(传参)
	 * 
	 * @param serverConfig
	 *            服务器配置文件路径
	 * @param gameConfig
	 *            游戏配置文件路径
	 * @return
	 */
	public static WServer getInstance(String serverConfig, String gameConfig) {
		synchronized (obj) {
			if (server == null) {
				config = new GameConfigXmlLoader().load(gameConfig);
				server = new WServer(serverConfig);
				startTime = System.currentTimeMillis();
			}
		}
		return server;
	}

	/**
	 * 取得服务器实例(默认参数)
	 * 
	 * @return
	 */
	public static WServer getInstance() {
		synchronized (obj) {
			if (server == null) {
				config = new GameConfigXmlLoader().load(defaultGameConfig);
				server = new WServer();
				startTime = System.currentTimeMillis();
			}
		}
		return server;
	}

	/**
	 * 覆盖启动方法
	 */
	@Override
	protected void init() {
		super.init();
		// 如果开启跨服则连接公共服务器
		if (openPublic && FileUtil.isExists(defaultPublicServerConfig)) {
			publicServer = new PublicConnectServer(defaultPublicServerConfig);
		}
		// 数据加载开始
		log.info("-->数据加载开始");
		DataManager.getInstance();
		log.info("-->数据加载结束");
		// 数据加载结束
		/* 按地图初始化线程 */
		// 1. 加载管理类，2.同时加载管理类里面的数据，获取map列表
		List<Q_mapBean> maps = ManagerPool.dataManager.q_mapContainer.getList();
		Iterator<Q_mapBean> iter = maps.iterator();
		while (iter.hasNext()) {
			Q_mapBean q_mapBean = (Q_mapBean) iter.next();
			// 副本地图
			if (q_mapBean.getQ_map_zones() == 1)
				continue;
			// 公共地图
			if (q_mapBean.getQ_map_public() == 1
					&& config.getServerByCountry(ServerType.PUBLIC.getValue()) != this
							.getServerId())
				continue;
			// 普通地图
			if (q_mapBean.getQ_map_public() != 1
					&& config.getServerByCountry(ServerType.PUBLIC.getValue()) == this
							.getServerId())
				continue;
			if (q_mapBean.getQ_map_lines() == null || ("").equals(q_mapBean.getQ_map_lines()))
				continue;

			String[] lines = q_mapBean.getQ_map_lines().split(Symbol.SHUXIAN_REG);
			for (int i = 0; i < lines.length; i++) {
				List<MapConfig> configs = new ArrayList<MapConfig>();
				MapConfig config = new MapConfig();
				int line = Integer.parseInt(lines[i]);
				config.setServerId(this.getServerId());
				config.setLineId(line);
				config.setMapModelId(q_mapBean.getQ_map_id());
				configs.add(config);
				MServer m = new MServer(q_mapBean.getQ_map_name() + "(" + line + "线)", 0, 0, configs);
				for (int j = 0; j < m.getMapConfigs().size(); j++) {
					config = m.getMapConfigs().get(j);
					mServers.put(getKey(config.getLineId(), config.getMapId()), m);
				}
			}
		}

		// 加载服务器参数
		ServerParamUtil.loadServerParam(getServerId());
		ManagerPool.countryManager.loadkingcity(getServerId());
		/*
		 * //panic god //载入本国王城信息
		 *
		 * //载入本国盟旗争夺战领地信息
		 * ManagerPool.guildFlagManager.loadguildfiag(getServerId());
		 * //设置攻城和领地战距离开区天数
		 * ManagerPool.countryManager.stSiegeIntervalDay(getServerId());
		 * //加载服务器禁言列表 ManagerPool.chatManager.loadChatBlackList();
		 * 
		 * //加载结婚信息列表 ManagerPool.marriageManager.loadAllMarriage(); //加载婚宴列表
		 * ManagerPool.marriageManager.loadAllWedding();
		 */
		// 初始化服务器线程
		
		//加载全服邮件列表 
		SystemgrantManager.getInstance().system_GrantBean_load();
		thread_group = new ThreadGroup(this.getServerName());

		wServerThread = new ServerThread(thread_group, this.getServerName(), 1000);

		wSavePlayerThread = new SavePlayerThread("Save-Player-Thread");

		// panic god 暂时屏蔽
		wSaveThread = new SaveThread("Save-Timer");

		wSaveGoldThread = new SaveGoldThread("Save-Gold-Thread");

		wSaveGoldExpendThread = new SaveGoldExpendThread("Save-Gold-Expend-Thread");

		wSaveGoldRaffleThread = new SaveGoldRaffleThread("Save-Gold-Raffle-Thread");
		
		wSaveGoldRaffleEventThread = new SaveGoldRaffleEventThread("Save-Gold-Raffle-Event-Thread");
		
		wSaveMailThread = new SaveMailThread("Save-Mail-Thread");

		wSaveServerParamThread = new SaveServerParamThread("Save-ServerParam-Thread");
		
		// 初始化定时线程
		wSchedularThread = new SchedularThread();

		wTeamZoneThread = new ServerThread(thread_group, this.getServerName()
				+ "-TeamZone", 1000);

		//活动线程 hongxiao.z 2014.2.10
		wSaveMarriageThread = new SaveMarriageThread("Save-Marriage-Thread");

		wSaveWeddingThread = new SaveWeddingThread("Save-Wedding-Thread");

		wSaveProtectThread = new SaveProtectThread("Save-Protect-Thread");

		wSaveProtectMailThread = new SaveProtectMailThread("Save-ProtectMail-Thread");
		try {
			// 初始化角色加速检查参数
			checkconfig = new CheckConfigXmlLoader()
					.load("server-config/check-config.xml");
			mailConfig = new MailConfigXmlloader().load();
			Global.CHECK_BETWEEN = checkconfig.getCheckbetween();
			Global.DISTANCE = checkconfig.getDistance();
			Global.BASE_SPEED = checkconfig.getBasespeed();
			// 初始化服务器心跳参数
			serverheartconfig = new ServerHeartConfigXmlLoader()
					.load("server-config/server-heart-config.xml");
			Global.HEART_PARA = serverheartconfig.getHeart_para();
			Global.HEART_WEB = serverheartconfig.getHeart_web();
		} catch (Exception e) {
			log.error(e, e);
		}
		// */
		// //副本测试使用
		// new Timer("Zones-Timer").schedule(new TimerTask(){
		//
		// @Override
		// public void run() {
		// Thread[] threads = new Thread[4000];
		// Thread.enumerate(threads);
		// int count = 0;
		// for (int i = 0; i < threads.length; i++) {
		// if(threads[i]!=null && threads[i].getName()!=null &&
		// threads[i].getName().indexOf("藏娇")!=-1){
		// count++;
		// }
		// }
		// log.error("运行线程数量:" +
		// Thread.activeCount());//executor.getPoolSize());
		// log.error("运行副本线程数量:" + count);//executor.getPoolSize());
		// log.error("副本总数量:" + zones_top);
		// log.error("副本运行数量:" + zones_number);
		// }
		// }, 60* 1000, 60 * 1000);

		//
		// //设置系统多倍经验等。。。(转移到Timer)
		// new Timer("Double-Timer").schedule(new TimerTask(){
		// @Override
		// public void run() {
		// //打坐双倍
		// if(ManagerPool.dazuoManager.isDaZuoDouble() != null){
		// ManagerPool.dazuoManager.setDaZuoDoubleStatus((byte) 1);
		// }else {
		// ManagerPool.dazuoManager.setDaZuoDoubleStatus((byte) 0);
		// }
		//
		// //打普通怪双倍
		// String douString = ManagerPool.monsterManager.isDaguaiDouble();
		// if(douString != null){
		// if (ManagerPool.monsterManager.getDaguaiDoubleStatus() == 0) {
		// ReqMonsterDoubleNoticeMessage wmsg = new
		// ReqMonsterDoubleNoticeMessage();
		// wmsg.setContent(douString);
		// wmsg.setStatus((byte) 1);
		// wmsg.setType((byte) 1);
		// MessageUtil.send_to_world(wmsg);
		// }
		// ManagerPool.monsterManager.setDaguaiDoubleStatus((byte) 1);
		// }else {
		// if (ManagerPool.monsterManager.getDaguaiDoubleStatus() == 1) {
		// ReqMonsterDoubleNoticeMessage wmsg = new
		// ReqMonsterDoubleNoticeMessage();
		// wmsg.setStatus((byte) 0);
		// wmsg.setType((byte) 1);
		// MessageUtil.send_to_world(wmsg);
		// }
		// ManagerPool.monsterManager.setDaguaiDoubleStatus((byte) 0);
		// }
		// }
		// }, 60* 1000, 60 * 1000);

		// 帧间隔测试使用
		new Timer("Step-Timer").schedule(new TimerTask() {
			@Override
			public void run() {
				Object[] objs = delay.values().toArray();
				Arrays.sort(objs, new Comparator<Object>() {
					@Override
					public int compare(Object o1, Object o2) {
						int entry1 = (Integer) o1;
						int entry2 = (Integer) o1;
						return entry1 - entry2 > 0 ? -1 : 1;
					}
				});
				if (objs.length > 0)
					steplog.error("帧间隔最大值:" + ((Integer) objs[0]));
			}
		}, 60 * 1000, 60 * 1000);

		// 流量测试使用
		/*
		 * "Total read bytes: %d, read throughtput: %f (b/s)", new Object[] { Long.valueOf(statistics.getReadBytes()), Double.valueOf(statistics.getReadBytesThroughput()) })
		 */
		new Timer("Quantity-Timer").schedule(new TimerTask() {

			@Override
			public void run() {
				if (socket == null || socket.getStatistics() == null)
					return;

				socket.getStatistics().updateThroughput(
						System.currentTimeMillis());

				StringBuffer buf = new StringBuffer();
				buf.append("WR:" + socket.getScheduledWriteBytes()).append(",");
				buf.append("MWR:" + socket.getScheduledWriteMessages()).append(
						",");
				buf.append("WT:" + socket.getStatistics().getWrittenBytes())
						.append(",");
				buf.append("RT:" + socket.getStatistics().getReadBytes())
						.append(",");
				buf.append("MWT:" + socket.getStatistics().getWrittenMessages())
						.append(",");
				buf.append("MRT:" + socket.getStatistics().getReadMessages())
						.append(",");
				buf.append(
						"WS:"+ socket.getStatistics()
										.getWrittenBytesThroughput()).append(
						",");
				buf.append("RS:"
						+ socket.getStatistics().getReadBytesThroughput());

				flowlog.error(buf.toString());
			}
		}, 5 * 1000, 5 * 1000);

		// 等待分配线程命令
		new Timer("WAIT-COMMAND-Timer").schedule(new TimerTask() {
			@Override
			public void run() {
				flowlog.error("等待解码命令：" + decodeExcutor.getTaskCounts());
				flowlog.error("等待执行并行命令：" + commandExcutor.getActiveCount());
				flowlog.error("等待执行世界并行命令："
						+ worldCommandExcutor.getActiveCount());
			}
		}, 5 * 1000, 5 * 1000);

		// 计算在线玩家数
		new Timer("CountOnlinePlayer-Timmer").schedule(new TimerTask() {
			@Override
			public void run() {
				Long[] onlineRolesId = PlayerManager.getInstance()
						.getOnlineRolesId();
				int male = 0;
				int famale = 0;
				String countrycount = "";
				int teamrolecount = 0;
				int petcount = 0;
				int rechargeer = 0;// 有过充值的玩家
				HashMap<String, Integer> guojia = new HashMap<String, Integer>();
				for (Long roleId : onlineRolesId) {
					Player player = PlayerManager.getInstance().getPlayer(
							roleId);
					if (player != null) {
						if (!PlayerManager.isArcher(player.getJob())) {
							male++;
						}else {
							famale++;
						}
						int country = player.getCountry();
						Integer gjcount = guojia.get(String.valueOf(country));
						gjcount = gjcount == null ? 0 : gjcount;
						gjcount++;
						guojia.put(String.valueOf(country), gjcount);
						long teamid = player.getTeamid();
						if (teamid != 0) {
							teamrolecount++;
						}
						List<Pet> petList = player.getPetList();
						if (petList != null) {
							for (Pet pet : petList) {
								if (pet.isShow()) {
									petcount++;
								}
							}
						}
						Gold gold = player.getGold();
						if (gold != null && gold.getTotalGold() > 0) {
							rechargeer++;
						}
					}
				}
				countrycount = JSONserializable.toString(guojia);
				ServerOnlineCountLog log = new ServerOnlineCountLog();
				log.setCountrycount(countrycount);
				log.setDatetimes(TimeUtil.getNowStringDate());
				log.setFemale(famale);
				log.setMale(male);
				log.setNowcount(onlineRolesId.length);
				log.setPetcount(petcount);
				log.setRechargeer(rechargeer);
				log.setTeamrolecount(teamrolecount);
				LogService.getInstance().execute(log);

				// 服务器 金币绑定钻石获得和消费日志
				ServerFinancialLog financiallog = new ServerFinancialLog();
				financiallog.setDatetimes(TimeUtil.getNowStringDate());
				financiallog
						.setSgeneratebindgold(sgeneratebindgold.longValue());
				financiallog.setSgeneratemoney(sgeneratemoney.longValue());
				financiallog.setSconsumebindgold(sconsumebindgold.longValue());
				financiallog.setSconsumemoney(sconsumemoney.longValue());
				LogService.getInstance().execute(financiallog);
			}
		}, 2 * 60 * 1000, 2 * 60 * 1000);

		// 加载所有玩家名字
		PlayerManager.getInstance().loadNames();
		
		// 加载玩家钻石抽奖事件列表
		ManagerPool.goldRaffleManager.loadGoldRaffleEventList();
		
		//! 加载BOSS事件
		ManagerPool.challengeManager.loadAll();
		
		//初始化激活属性模块
		ActivateAttributeManager.getInstance().init();
		//启动活动信息更新线程
		PlayerActivityInfoManager.getInstance().start();
		//初始化定时器
		try {
			SchedulerManager.getInstance().init();
		} catch (SchedulerException e) {
			log.error("定时器报错", e);
		}
		
		//加载活动
		try {
			NewActivityManager.getInstance().init();
		} catch (Exception e1) {
			log.error("加载活动报错", e1);
		}
		
		//加载定时配置文件
		try {
			LinkedList<SchedulerBean> parseFile = SchedulerParser.parseFile(DefaultSchedulerConfig);
			SchedulerManager.getInstance().addSchedulerBean(parseFile);
		} catch (Exception e) {
			log.error("加载定时配置文件报错", e);
		}
	}

	/**
	 * 创建副本地图线程服务
	 * 
	 * @param name
	 *            服务名
	 * @param zoneId
	 *            副本ID
	 * @param zoneModelId
	 *            副本模板ID
	 * @param mapConfigs
	 *            地图配置
	 * @return
	 */
	public MServer createMapServer(String name, long zoneId, int zoneModelId,
			List<MapConfig> mapConfigs) {
		try {
			MServer m = new MServer(name, zoneId, zoneModelId, mapConfigs);
			for (int i = 0; i < m.getMapConfigs().size(); i++) {
				MapConfig config = m.getMapConfigs().get(i);
				mServers.put(getKey(config.getLineId(), config.getMapId()), m);
			}

			// synchronized (obj) {
			// zones_number++;
			// zones_top++;
			// }

			log.error("map threads " + mServers.size());
			new Thread(m).start();

			return m;
		} catch (Exception e) {
			log.error(e, e);
		}
		return null;
	}

	/**
	 * 移除副本地图线程服务器
	 * 
	 * @param mapModelId
	 * @return
	 */
	public void removeMapServer(MServer server) {
		// 销毁副本
		for (int i = 0; i < server.getMapConfigs().size(); i++) {
			MapConfig config = server.getMapConfigs().get(i);
			mServers.remove(getKey(config.getLineId(), config.getMapId()));
		}
		server.stop(true);
		// synchronized (obj) {
		// zones_number--;
		// }
		log.error("销毁副本，副本总数量:" + mServers.size());

		// log.error("map threads " + mServers.size());
	}

	@Override
	public void run() {
		long begin = System.currentTimeMillis();
		super.run();
		Iterator<MServer> iter = mServers.values().iterator();
		HashSet<MServer> already = new HashSet<MServer>();
		while (iter.hasNext()) {
			MServer mServer = (MServer) iter.next();
			if (!already.contains(mServer)) {
				new Thread(mServer).start();
				already.add(mServer);
			}
		}
		wServerThread.start();
		wServerThread.addTimerEvent(new ServerHeartTimer(this.getServerId(),
				this.getServerWeb()));

		wTeamZoneThread.start();
		wTeamZoneThread.addTimerEvent(new ZoneTeamTimer(this.getServerId()));

		wSchedularThread.start();

		wSavePlayerThread.start();
		wSaveThread.start();
		
		wSaveGoldThread.start();
		// wSaveGoldExpendThread.start();
		wSaveGoldRaffleThread.start();
		wSaveGoldRaffleEventThread.start();
		
		ManagerPool.schedularManager.init();
		wSaveServerParamThread.start();
		// panic god 暂时屏蔽
		/*

		 * 
		 *  wSaveWeddingThread.start();
		 * wSaveProtectThread.start(); wSaveProtectMailThread.start();
		 * ManagerPool.schedularManager.init();
		 *  wSaveMarriageThread.start();
		 */
		
		wSaveMailThread.start();
		ManagerPool.mailServerManager.startClearMail();

		// 内网消息定时发送
		new Timer("Inner-Send-Timer").schedule(new TimerTask() {
			@Override
			public void run() {
				List<IoSession> sessions = new ArrayList<IoSession>();
				synchronized (gateSessions) {
					Iterator<List<IoSession>> iter = gateSessions.values()
							.iterator();
					while (iter.hasNext()) {
						List<IoSession> list = (List<IoSession>) iter.next();
						sessions.addAll(list);
					}
				}
				synchronized (worldSessions) {
					sessions.addAll(worldSessions);
				}
				for (IoSession ioSession : sessions) {
					IoBuffer sendbuf = null;
					synchronized (ioSession) {
						if (ioSession.containsAttribute("SEND_BUF")) {
							sendbuf = (IoBuffer) ioSession
									.getAttribute("SEND_BUF");
							ioSession.removeAttribute("SEND_BUF");
						}
					}
					try {
						if (sendbuf != null && sendbuf.position() > 0) {
							sendbuf.flip();
							WriteFuture wf = ioSession.write(sendbuf);
							wf.await();
						}
					} catch (Exception e) {
						continue;
					}
				}
			}
		}, 1, 1);

		if (publicServer != null) {
			new Thread(publicServer, "公共服连接").start();
		}

		while (!connnetSuccess) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				log.error(e, e);
			}
		}

		try {
			 HttpUtil.post(Global.HEART_WEB, String.format(Global.HEART_PARA,
			 WServer.getInstance().getServerWeb(),
			 WServer.getInstance().getServerId(), 1));
		} catch (Exception e) {
			log.error(e, e);
		}

		IServerStartScript script = (IServerStartScript) ManagerPool.scriptManager
				.getScript(ScriptEnum.SERVER_START);
		if (script != null) {
			try {
				script.onStart(this.getServerWeb(), this.getServerId());
			} catch (Exception e) {
				log.error(e, e);
			}
		} else {
			log.error("找不到服务器启动脚本");
		}

		// 内网消息定时发送
		new Timer("Server-Alive-Timer").schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					 HttpUtil.post(Global.HEART_WEB, String.format(
					 Global.HEART_PARA, WServer.getInstance()
					 .getServerWeb(), WServer.getInstance()
					 .getServerId(), 2));
				} catch (Exception e) {
					log.error(e, e);
				}
			}
		}, 60 * 1000, 60 * 1000);

		
				
				
		STARTFINISH = true;

		log.info("server startup in "
				+ (System.currentTimeMillis() - startTime) + " ms");
		try {
			URL location = getClass().getProtectionDomain().getCodeSource()
					.getLocation();
			String runname = ManagementFactory.getRuntimeMXBean().getName();
			String pid = runname.substring(0, runname.indexOf("@"));
			ServerStartAndStopLog log = new ServerStartAndStopLog();
			log.setAction("start");
			log.setDatatime(TimeUtil.getNowStringDate());
			log.setLocal(location.toString());
			log.setServerId(String.valueOf(server_id));
			log.setConsuming((int) (System.currentTimeMillis() - begin));
			log.setAppName("gameserver");
			log.setIdentity(WServer.startidentity);
			log.setPid(pid);
//			LogService.getInstance().execute(log);
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	@Override
	protected void stop() {
		long begin = System.currentTimeMillis();

		// 服务器停止操作
		MServer[] servers = mServers.values().toArray(new MServer[0]);
		for (int i = 0; i < servers.length; i++) {
			servers[i].stop(false);
		}
		wServerThread.stop(true);
		wTeamZoneThread.stop(true);
		wSchedularThread.stop(true);

		wSaveThread.stop(true);

		wSavePlayerThread.stop(true);
		wSaveGoldThread.stop(true);
		wSaveGoldExpendThread.stop(true);
		wSaveGoldRaffleThread.stop(true);
		wSaveGoldRaffleEventThread.stop(true);
		wSaveMailThread.stop(true);
		wSaveServerParamThread.stop(true);
		wSaveMarriageThread.stop(true);
		wSaveWeddingThread.stop(true);
		wSaveProtectThread.stop(true);
		wSaveProtectMailThread.stop(true);

		try {
			Thread.sleep(10000);
		} catch (Exception e) {
			log.error(e, e);
		}

		MemoryCache<Long, Player> players = ManagerPool.playerManager
				.getPlayers();
		// 事件迭代器
		Player[] saveplayers = players.getCache().values()
				.toArray(new Player[0]);

		log.error("保存玩家开始，保存数量：" + saveplayers);

		int count = 0;
		// 派发事件
		for (Player player : saveplayers) {
			// Player player = saves.get(i);
			count++;
			try {
				ManagerPool.playerManager.quit(player);
				ManagerPool.playerManager.updatePlayerSync(player);
			} catch (Exception ex) {
				log.error(ex, ex);
			}
			if (count % 100 == 0)
				log.error("已经保存数量：" + count);
		}
		// 保存服务器参数(暂时不要现在没同步)
		// ServerParamUtil.saveServerParam();

		try {
			URL location = getClass().getProtectionDomain().getCodeSource()
					.getLocation();
			ServerStartAndStopLog log = new ServerStartAndStopLog();
			String runname = ManagementFactory.getRuntimeMXBean().getName();
			String pid = runname.substring(0, runname.indexOf("@"));
			log.setAction("stop");
			log.setDatatime(TimeUtil.getNowStringDate());
			log.setLocal(location.toString());
			log.setServerId(String.valueOf(server_id));
			log.setConsuming((int) (System.currentTimeMillis() - begin));
			log.setAppName("gameserver");
			log.setIdentity(WServer.startidentity);
			log.setPid(pid);
			LogService.getInstance().execute(log);
		} catch (Exception e) {
			log.error(e, e);
		}
		LogService.getInstance().shutdown();

		try {
			HttpUtil.post(Global.HEART_WEB, String.format(Global.HEART_PARA,
					WServer.getInstance().getServerWeb(), WServer.getInstance()
							.getServerId(), 3));
		} catch (Exception e) {
			log.error(e, e);
		}
		SchedulerManager.getInstance().stop();
		PlayerActivityInfoManager.getInstance().stop();;
		log.error("游戏服务器" + server_id + "停止成功！");
	}

	/**
	 * 添加计划任务
	 * 
	 * @param cron
	 *            Cron 表达式
	 * @param className
	 *            加载类名
	 */
	public void addSchedularTask(String cron, String className) {
		wSchedularThread.addSchedulerTask(cron, className);
	}

	@Override
	public void sessionClosed(IoSession session) {
		closelog.error(session + " closed!");
		// 发生错误关闭连接
		int id = (Integer) session.getAttribute("connect-server");
		if (id != 0) {
			removeSession(session, id, IServer.GATE_SERVER);
		} else {
			removeSession(session, id, IServer.WORLD_SERVER);
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		closelog.error(session + " cause " + cause, cause);
		// 发生错误关闭连接
		// session.close(true);
	}

	@Override
	public void doCommand(IoSession iosession, IoBuffer buf) {
		// 消息处理
		try {
			int id = buf.getInt();
			long sessionId = buf.getLong();
			if (sessionId > 0) {
				// 网关消息解析队列
				decodeExcutor.addTask(sessionId, new Work(id, iosession, buf));
			} else {
				// 世界消息解析队列
				worldExcutor.addTask(0l, new Work(id, iosession, buf));
			}
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	private class Work extends AbstractWork {

		private int id;

		private IoSession iosession;

		private IoBuffer buf;

		public Work(int id, IoSession iosession, IoBuffer buf) {
			this.id = id;
			this.iosession = iosession;
			this.buf = buf;
		}

		@Override
		public void run() {
			try {
				// 获取消息体
				Message msg = message_pool.getMessage(id);
				if (msg == null) {
					log.error("收到了不存在的消息：" + id);
					return;
				}
				int roleNum = buf.getInt();
				for (int i = 0; i < roleNum; i++) {
					msg.getRoleId().add(buf.getLong());
				}
				msg.read(buf);
				msg.setSession(iosession);

				// if(msg.getRoleId().size()>0 &&
				// msg.getRoleId().get(0)==504365526429977l)
				// log.debug(iosession.toString() + "收到消息" + msg.getId() + "_" +
				// msg.getClass().getSimpleName() + ":" + msg.toString());

				// 生成处理函数
				Handler handler = message_pool.getHandler(id);
				handler.setMessage(msg);
				handler.setCreateTime(System.currentTimeMillis());

				if ("Local".equalsIgnoreCase(msg.getQueue())) {
					// handler.action();
					commandExcutor.execute(handler);
				} else if ("Server".equalsIgnoreCase(msg.getQueue())) {
					Player player = null;
					if (msg.getRoleId().size() > 0)
						player = ManagerPool.playerManager.getPlayer(msg
								.getRoleId().get(0));
					if (player == null) {
						// 服务器之间消息直接执行
						if ((msg.getId() % 1000) >= 300) {
							// handler.action();
							worldCommandExcutor.execute(handler);
						} else {
							if (msg.getRoleId().size() > 0) {
								droplog.error("丢弃玩家(" + msg.getRoleId().get(0)
										+ ")Server指令(" + msg.getId() + ")："
										+ msg.toString());
							} else {
								droplog.error("丢弃Server指令：" + msg.toString());
							}
						}
						return;
					}

					handler.setParameter(player);
					wServerThread.addCommand(handler);
				} else {
					Player player = null;
					if (msg.getRoleId().size() > 0)
						player = ManagerPool.playerManager.getPlayer(msg.getRoleId().get(0));
					if (player == null) {
						// 服务器之间消息直接执行
						if ((msg.getId() % 1000) >= 300) {
							// handler.action();
							worldCommandExcutor.execute(handler);
						} else {
							if (msg.getRoleId().size() > 0) {
								droplog.error("丢弃玩家(" + msg.getRoleId().get(0)
										+ ")Map指令(" + msg.getId() + ")："
										+ msg.toString());
							} else {
								droplog.error("丢弃Map指令：" + msg.toString());
							}
						}
						return;
					}
					handler.setParameter(player);

					MServer mServer = getMServer(player.getLine(), player.getMap());
					if (mServer == null) {
						// 服务器之间消息直接执行
						if ((msg.getId() % 1000) >= 300) {
							// handler.action();
							worldCommandExcutor.execute(handler);
						} else if (msg instanceof ReqQuitToGameMessage) {
							// 退出特殊处理（在线人数问题）
							log.error("人物不在地图中时移除");
							commandExcutor.execute(handler);
						} else {
							droplog.error("丢弃玩家(" + player.getId() + ",line="
									+ player.getLine() + ",map="
									+ player.getMap() + ",mapModel="
									+ player.getMapModelId() + ")Map指令：("
									+ msg.getId() + ")" + msg.toString());
							ResDiscardMsgMessage dropmsg = new ResDiscardMsgMessage();
							dropmsg.setMsgid(msg.getId());
							dropmsg.setMsgcont(msg.toString());
							MessageUtil.tell_player_message(player, dropmsg);
						}
						return;
					}
					mServer.addCommand(handler);
				}
			} catch (Exception e) {
				log.error(e, e);
			}
		}
	}

	public MServer[] getMServers() {
		return mServers.values().toArray(new MServer[0]);
	}

	public MServer getMServer(int lineId, int mapId) {
		return mServers.get(getKey(lineId, mapId));
	}

	public static GameConfig getGameConfig() {
		return config;
	}

	@Override
	public void register(IoSession session, int type) {
		switch (type) {
		case IServer.GATE_SERVER:
			ReqRegisterGateMessage msg = new ReqRegisterGateMessage();
			msg.setServerId(this.getServerId());
			msg.setServerName(this.getServerName());
			session.write(msg);
			break;
		case IServer.WORLD_SERVER:
			ReqRegisterWorldMessage mesg = new ReqRegisterWorldMessage();
			mesg.setServerId(this.getServerId());
			mesg.setServerName(this.getServerName());
			session.write(mesg);
			break;
		}
	}

	@Override
	public void sessionIdle(IoSession iosession, IdleStatus idlestatus) {
		log.error("Clientserver session " + iosession + " idle " + idlestatus);
	}

	/**
	 * 获得服务器Key
	 * 
	 * @param lineId
	 * @param mapId
	 * @return 线路ID*10000000 + 地图ID
	 */
	private int getKey(int lineId, int mapId) {
		return lineId * 10000000 + mapId;
	}

	public static MessagePool getMessage_pool() {
		return message_pool;
	}

	public boolean isConnectWorld() {
		if (worldSessions == null || worldSessions.size() == 0)
			return false;
		return worldSessions.get(0).isConnected();
	}

	public SavePlayerThread getSavePlayerThread() {
		return wSavePlayerThread;
	}

	public SaveGoldThread getSaveGoldThread() {
		return wSaveGoldThread;
	}

	public SaveGoldExpendThread getSaveGoldExpendThread() {
		return wSaveGoldExpendThread;
	}

	public SaveGoldRaffleThread getwSaveGoldRaffleThread() {
		return wSaveGoldRaffleThread;
	}

	public SaveGoldRaffleEventThread getwSaveGoldRaffleEventThread() {
		return wSaveGoldRaffleEventThread;
	}

	public SaveMailThread getwSaveMailThread() {
		return wSaveMailThread;
	}

	public SaveServerParamThread getwSaveServerParamThread() {
		return wSaveServerParamThread;
	}

	public ServerThread getServerThread() {
		return wServerThread;
	}

	@Override
	public void sessionCreate(IoSession iosession) {
	}

	@Override
	public void sessionOpened(IoSession iosession) {
	}

	@Override
	protected void connectComplete() {
		connnetSuccess = true;
	}

	public boolean isConnectPublic() {
		if (publicSession == null) {
			return false;
		}
		if (!publicSession.isConnected()) {
			return false;
		}
		return true;
	}

	public IoSession getPublicSession() {
		return publicSession;
	}

	public SaveMarriageThread getwSaveMarriageThread() {
		return wSaveMarriageThread;
	}

	public void setwSaveMarriageThread(SaveMarriageThread wSaveMarriageThread) {
		this.wSaveMarriageThread = wSaveMarriageThread;
	}

	public SaveWeddingThread getwSaveWeddingThread() {
		return wSaveWeddingThread;
	}

	public void setwSaveWeddingThread(SaveWeddingThread wSaveWeddingThread) {
		this.wSaveWeddingThread = wSaveWeddingThread;
	}

	public SaveProtectThread getwSaveProtectThread() {
		return wSaveProtectThread;
	}

	public void setwSaveProtectThread(SaveProtectThread wSaveProtectThread) {
		this.wSaveProtectThread = wSaveProtectThread;
	}

	public SaveProtectMailThread getwSaveProtectMailThread() {
		return wSaveProtectMailThread;
	}

	public void setwSaveProtectMailThread(
			SaveProtectMailThread wSaveProtectMailThread) {
		this.wSaveProtectMailThread = wSaveProtectMailThread;
	}

	public static MailConfig getMailConfig() {
		return mailConfig;
	}

	public static void setMailConfig(MailConfig mailConfig) {
		WServer.mailConfig = mailConfig;
	}

	/**
	 * 连接公共服务器服务类
	 * 
	 */
	private class PublicConnectServer extends ClientServer {
		/**
		 * 连接公共服务器构造函数
		 * 
		 * @param serverConfig
		 */
		public PublicConnectServer(String serverConfig) {
			super(serverConfig);// 调用父类构造函数
		}

		/**
		 * 线程关闭时(实现接口方法)
		 */
		@Override
		public void sessionClosed(IoSession session) {
			// publiccloselog.error("world " + iosession + " closed!");
			publicSession = null;
			if (session.containsAttribute("connect-server-id")) {
				int id = (Integer) session.getAttribute("connect-server-id");
				String ip = (String) session.getAttribute("connect-server-ip");
				int port = (Integer) session
						.getAttribute("connect-server-port");
				// panic god 暂时屏蔽kill
				// reconnectPublic(id, ip, port);
			}
		}

		/**
		 * 产生异常
		 */
		@Override
		public void exceptionCaught(IoSession session, Throwable cause) {
			// publiccloselog.error("world " + session + " cause " + cause,
			// cause);
			// session.close(true);
		}

		/**
		 * 消息处理
		 */
		@Override
		public void doCommand(IoSession iosession, IoBuffer buf) {
			try {
				int id = buf.getInt();

				long sessionId = buf.getLong();

				if (sessionId > 0) {
					decodeExcutor.addTask(sessionId, new Work(id, iosession,
							buf));
				} else {
					worldExcutor.addTask(0l, new Work(id, iosession, buf));
				}
				//
				// int roleNum = buf.getInt();
				// List<Long> roles = new ArrayList<Long>();
				// for (int i = 0; i < roleNum; i++) {
				// roles.add(buf.getLong());
				// }
				//
				// //生成处理函数
				// Handler handler = message_pool.getHandler(id);
				//
				// if(handler!=null){
				// //获取消息体
				// Message msg = message_pool.getMessage(id);
				// msg.read(buf);
				// msg.setSession(iosession);
				// //自身处理消息
				// handler.setMessage(msg);
				//
				// }
			} catch (Exception e) {
				log.error(e, e);
			}
		}

		@Override
		protected void stop() {
		}

		/**
		 * 注册公共服务器
		 */
		@Override
		public void register(IoSession session, int type) {
			ReqRegisterGameForPublicMessage msg = new ReqRegisterGameForPublicMessage();
			msg.setServerId(this.getServerId());
			msg.setServerName(this.getServerName());
			msg.setWebName(this.getServerWeb());
			session.write(msg);
			publicSession = session;
		}

		@Override
		public void sessionIdle(IoSession iosession, IdleStatus idlestatus) {

		}

		@Override
		public void sessionCreate(IoSession iosession) {
		}

		@Override
		public void sessionOpened(IoSession iosession) {

		}

		@Override
		protected void connectComplete() {

		}
	}
}
