package com.game.server;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.game.attribute.ActivateAttributeManager;
import com.game.cache.executor.NonOrderedQueuePoolExecutor;
import com.game.chat.timer.BulletinTimer;
import com.game.command.Handler;
import com.game.command.ICommand;
import com.game.dblog.LogService;
import com.game.guild.timer.GuildTimer;
import com.game.manager.ManagerPool;
import com.game.message.Message;
import com.game.message.pool.MessagePool;
import com.game.mina.impl.InnerServer;
import com.game.newactivity.NewActivityManager;
import com.game.player.config.NonageConfig;
import com.game.player.loader.NonageConfigXmlLoader;
import com.game.player.timer.OnlineCountTimmer;
import com.game.player.timer.UserOnlineTimer;
import com.game.question.timer.QuestionTimer;
import com.game.server.config.GameConfig;
import com.game.server.gmchat.config.GMChatConfig;
import com.game.server.gmchat.loader.GMChatConfigXmlLoader;
import com.game.server.http.HttpServerProtocolHandler;
import com.game.server.http.config.HttpServerConfig;
import com.game.server.http.loader.HttpConfigXmlLoader;
import com.game.server.loader.GameConfigXmlLoader;
import com.game.server.log.ServerStartAndStopLog;
import com.game.server.thread.SaveFriendThread;
import com.game.server.thread.SaveFruitThread;
import com.game.server.thread.SaveThread;
import com.game.server.thread.SchedularThread;
import com.game.server.thread.ServerThread;
import com.game.server.timer.CloseTimer;
import com.game.server.timer.WserverHeartTimer;
import com.game.timer.ITimerEvent;
import com.game.toplist.timer.TopListTimer;
import com.game.utils.ServerParamUtil;
import com.game.utils.TimeUtil;

public class WorldServer extends InnerServer {
	
	private static Logger log = Logger.getLogger(WorldServer.class);
	
	private static Logger flowlog = Logger.getLogger("WORLDFLOW");
	
	private static Object obj = new Object();
	
	private static MessagePool message_pool = new MessagePool();
	//服务器实例
	private static WorldServer server;
	//GameServer通信列表
	private static ConcurrentHashMap<Integer, Vector<IoSession>> gameSessions = new ConcurrentHashMap<Integer, Vector<IoSession>>();
	//GateServer通信列表
	private static ConcurrentHashMap<Integer, Vector<IoSession>> gateSessions = new ConcurrentHashMap<Integer, Vector<IoSession>>();
	//服务器线程
	private ServerThread wServerThread;
	//服务器新手卡认证线程
	private ServerThread wCardThread;
	//定时线程
	private SchedularThread wSchedularThread;
	//好友保存线程
	private SaveFriendThread wSaveFriendThread;
	//果实保存线程
	private SaveFruitThread wSaveFruitThread;
	//服务器启动线程组
	private ThreadGroup thread_group;
	//默认服务器配置文件
	private static final String defaultGameConfig="world-config/game-config.xml";
	//默认内部服务器配置文件
	private static final String defaultInnerServerConfig="world-config/inner-server-config.xml";
	//默认HTTP服务器配置文件
	private static final String defaultHttpServerConfig="world-config/http-server-config.xml";
	
	//默认HTTP服务器配置文件
	private static final String defaultGmchatServerConfig="world-config/gmchat-server-config.xml";
	
	//默认防沉迷配置文件
	private static final String defaultNonageConfig="world-config/nonage-server.xml";
	
	private static String SERVER_ID = "serverId";
	
	private static GameConfig config;
	
	private static HttpServerConfig httpConfig;
	
	private static GMChatConfig gmchatConfig;
	
	private static NonageConfig nonageConfig;
	
	public  static String startidentity=""; //启动唯一标识
	
	private long closeTime;
	
	private NonOrderedQueuePoolExecutor commandExcutor = new NonOrderedQueuePoolExecutor(100);
	
	public WorldServer(){
		super(defaultInnerServerConfig);
	}
	
	public WorldServer(String innerServerConfig){
		super(innerServerConfig);
	}
	
	public static WorldServer getInstance(){
		synchronized (obj) {
			if(server == null){
				config = new GameConfigXmlLoader().load(defaultGameConfig);
				httpConfig = new HttpConfigXmlLoader().load(defaultHttpServerConfig);
				gmchatConfig = new GMChatConfigXmlLoader().load(defaultGmchatServerConfig);
				nonageConfig = new NonageConfigXmlLoader().load(defaultNonageConfig);
				server = new WorldServer();
			}
		}
		return server;
	}
	
	public static WorldServer getInstance(String innerServerConfig, String gameConfig){
		synchronized (obj) {
			if(server == null){
				config = new GameConfigXmlLoader().load(gameConfig);
				httpConfig = new HttpConfigXmlLoader().load(defaultHttpServerConfig);
				gmchatConfig = new GMChatConfigXmlLoader().load(defaultGmchatServerConfig);
				nonageConfig = new NonageConfigXmlLoader().load(defaultNonageConfig);
				server = new WorldServer(innerServerConfig);
			}
		}
		return server;
	}
	
	@Override
	protected void init(){
		super.init();

		thread_group = new ThreadGroup(this.getServerName());
		wServerThread = new ServerThread(thread_group, this.getServerName(), 1000);
		
		wCardThread = new ServerThread(thread_group, this.getServerName() + "新手卡", 0);
		//初始化定时线程
		wSchedularThread = new SchedularThread();
		
		/*panic god
		wSaveFriendThread = new SaveFriendThread("Save-Friend-Thread");

		wSaveFruitThread = new SaveFruitThread("Save-Fruit-Thread");
		*/
		//数据加载
		//加载服务器参数
		ServerParamUtil.loadServerParam(getServerId());
		
		
		ManagerPool.guildWorldManager.loadBannerData();
		ManagerPool.guildWorldManager.loadallGuild();
		ManagerPool.topListManager.initZoneData();
		/*panic god
		ManagerPool.friendManager.loadData();
		
		//副本排行 暂不启动
		ManagerPool.countryManager.loadkingcity();
		ManagerPool.txconsumeManager.initTxConfirmExecutor(); //初始化腾讯发放道具的确认队列
		*/
		//添加 排行榜 数据加载
		ManagerPool.stallsManager.loadAllStalls();
		ManagerPool.bankManager.loadAllBankLogs();
		ManagerPool.topListManager.initSortMap();
		//加载活动列表
		NewActivityManager.getInstance().init();
		ActivateAttributeManager.getInstance().init();
		
	}
	
	@Override
	public void run(){
		long begin=System.currentTimeMillis();
		super.run();
		wServerThread.start();
		wCardThread.start();
		
		///*panic god
		//防沉迷状态
		wServerThread.addTimerEvent(new UserOnlineTimer());
		//公会时间
		wServerThread.addTimerEvent(new GuildTimer());
		/*panic god
		//排行榜崇拜次数更新
		wServerThread.addTimerEvent(new TopListTimer());
		//国家
		wServerThread.addTimerEvent(new BulletinTimer());
		*/
		//关服剩余时间
		wServerThread.addTimerEvent(new CloseTimer());
		//玩家计算
		wServerThread.addTimerEvent(new OnlineCountTimmer());
		//基础活动脚本定时调用
		wServerThread.addTimerEvent(new WserverHeartTimer());
		/*panic god
		//答题
		wServerThread.addTimerEvent(new QuestionTimer());
		*/
		//定时调用
		wSchedularThread.start();
		//调度管理
		ManagerPool.schedularManager.init();
		//*/
		
		//流量测试使用
		new Timer("Quantity-Timer").schedule(new TimerTask(){
			
			@Override
			public void run() {
				acceptor.getStatistics().updateThroughput(System.currentTimeMillis());
				
				StringBuffer buf = new StringBuffer();
				buf.append("WR:" + acceptor.getScheduledWriteBytes()).append(",");
				buf.append("MWR:" + acceptor.getScheduledWriteMessages()).append(",");
				buf.append("WT:" + acceptor.getStatistics().getWrittenBytes()).append(",");
				buf.append("RT:" + acceptor.getStatistics().getReadBytes()).append(",");
				buf.append("MWT:" + acceptor.getStatistics().getWrittenMessages()).append(",");
				buf.append("MRT:" + acceptor.getStatistics().getReadMessages()).append(",");
				buf.append("WS:" + acceptor.getStatistics().getWrittenBytesThroughput()).append(",");
				buf.append("RS:" + acceptor.getStatistics().getReadBytesThroughput());
				
				flowlog.error(buf.toString());
			}
		}, 5 * 1000, 5 * 1000);
		
		//内网消息定时发送
		new Timer("Inner-Send-Timer").schedule(new TimerTask(){
			@Override
			public void run() {
				List<IoSession> sessions = new ArrayList<IoSession>();
				synchronized (gateSessions) {
					Iterator<Vector<IoSession>> iter = gateSessions.values().iterator();
					while (iter.hasNext()) {
						Vector<IoSession> list = (Vector<IoSession>) iter.next();
						sessions.addAll(list);
					}
				}
				synchronized (gameSessions) {
					Iterator<Vector<IoSession>> iter = gameSessions.values().iterator();
					while (iter.hasNext()) {
						Vector<IoSession> list = (Vector<IoSession>) iter.next();
						sessions.addAll(list);
					}
				}
				for (IoSession ioSession : sessions) {
					IoBuffer sendbuf = null;
					synchronized (ioSession) {
						if(ioSession.containsAttribute("SEND_BUF")){
							sendbuf = (IoBuffer)ioSession.getAttribute("SEND_BUF");
							ioSession.removeAttribute("SEND_BUF");
						}
					}
					try{
						if (sendbuf != null && sendbuf.position() > 0) {
							sendbuf.flip();
							WriteFuture wf = ioSession.write(sendbuf);
							wf.await();
						}
					}catch (Exception e) {
						continue;
					}
				}
			}
		}, 1, 1);
				
		/*panic god
		wSaveFriendThread.start();
		wSaveFruitThread.start();
		*/
		
		new SaveThread("World-Save-Timer").start();
		
		new HttpServer("World-Http").start();
		
//		new ChatServer().start();
		
		try{
			URL location = getClass().getProtectionDomain().getCodeSource().getLocation();
			String runname = ManagementFactory.getRuntimeMXBean().getName();
	        String pid = runname.substring(0, runname.indexOf("@")); 
			ServerStartAndStopLog log=new ServerStartAndStopLog();
			log.setAction("start");
			log.setDatatime(TimeUtil.getNowStringDate());
			log.setLocal(location.toString());
			log.setServerId(String.valueOf(server_id));
			log.setConsuming((int) (System.currentTimeMillis()-begin));
			log.setAppName("worldserver");
			log.setIdentity(WorldServer.startidentity);
			log.setPid(pid);
			//LogService.getInstance().execute(log);
		}catch (Exception e) {
			log.error(e,e);
		}
	}

	@Override
	public void doCommand(IoSession iosession, IoBuffer buf) {
		//消息处理
		try{
			int id = buf.getInt();
			//获取消息体
			Message msg = message_pool.getMessage(id);
			
			msg.setSendId(buf.getLong());
			int roleNum = buf.getInt();
			for (int i = 0; i < roleNum; i++) {
				msg.getRoleId().add(buf.getLong());
			}
			msg.read(buf);
			msg.setSession(iosession);
			
			log.debug("收到消息" + msg.getId() + "-->" + msg.getClass().getSimpleName() + ":" + msg.toString());
			
			//生成处理函数
			Handler handler = message_pool.getHandler(id);
			handler.setMessage(msg);

			if("Local".equalsIgnoreCase(msg.getQueue())){
				commandExcutor.execute(handler);
			}else{
				wServerThread.addCommand(handler);
			}
		}catch (IllegalAccessException e) {
			log.error(e,e);
		}catch (InstantiationException e) {
			log.error(e,e);
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		log.error("InnerServer error " + session, cause);
//		session.close(true);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sessionClosed(IoSession iosession) {
		log.error("InnerServer " + iosession + " closed!");
		
			
		Object[] objs = gameSessions.values().toArray();
		for (Object obj : objs) {
			Vector<IoSession> sessions = (Vector<IoSession>)obj;
			sessions.remove(iosession);
		}
		objs = gateSessions.values().toArray();
		for (Object obj : objs) {
			Vector<IoSession> sessions = (Vector<IoSession>)obj;
			sessions.remove(iosession);
		}
	}

	@Override
	protected void stop() {
		long begin = System.currentTimeMillis();
		/*
		 * 	luminghua hide 因为没有初始化
		 * wSaveFriendThread.stop(true); 
		wSaveFruitThread.stop(true);*/
		wSchedularThread.stop(true);
		//保存服务器参数(暂时不要现在没同步)
		//ServerParamUtil.saveServerParam();
		try{
			URL location = getClass().getProtectionDomain().getCodeSource().getLocation();
			String runname = ManagementFactory.getRuntimeMXBean().getName();
	        String pid = runname.substring(0, runname.indexOf("@")); 
			ServerStartAndStopLog log=new ServerStartAndStopLog();
			log.setAction("stop");
			log.setDatatime(TimeUtil.getNowStringDate());
			log.setLocal(location.toString());
			log.setServerId(String.valueOf(server_id));
			log.setConsuming((int) (System.currentTimeMillis()-begin));
			log.setAppName("worldserver");
			log.setIdentity(WorldServer.startidentity);
			log.setPid(pid);
			LogService.getInstance().execute(log);
		}catch (Exception e) {
			log.error(e,e);
		}
		LogService.getInstance().shutdown();
	}
	
	/**
	 * 游戏服务器注册
	 * @param id 游戏服务器编号
	 * @param session 通讯接口
	 */
	public synchronized void registerGameServer(int id, IoSession session){
		session.setAttribute(SERVER_ID, id);
		synchronized (gameSessions) {
			Vector<IoSession> sessions = null;
			if(gameSessions.containsKey(id)){
				sessions = gameSessions.get(id);
			}else{
				sessions = new Vector<IoSession>();
				gameSessions.put(id, sessions);
			}
			sessions.add(session);
		}
	}
	
	/**
	 * 网关服务器注册
	 * @param id 网关服务器编号
	 * @param session 通讯接口
	 */
	public synchronized void registerGateServer(int id, IoSession session){
		session.setAttribute(SERVER_ID, id);
		synchronized (gateSessions) {
			Vector<IoSession> sessions = null;
			if(gateSessions.containsKey(id)){
				sessions = gateSessions.get(id);
			}else{
				sessions = new Vector<IoSession>();
				gateSessions.put(id, sessions);
			}
			sessions.add(session);
		}
	}

	public static ConcurrentHashMap<Integer, Vector<IoSession>> getGameSessions() {
		return gameSessions;
	}

	public static ConcurrentHashMap<Integer, Vector<IoSession>> getGateSessions() {
		return gateSessions;
	}

	@Override
	public void sessionIdle(IoSession iosession, IdleStatus idlestatus) {
		log.error("Innerserver session " + iosession + " idle " + idlestatus);
	}
	
	public void addTimerEvent(ITimerEvent event){
		wServerThread.addTimerEvent(event);
	}
	
	public void addSchedularTask(String cron, String className){
		wSchedularThread.addSchedulerTask(cron, className);
	}
	
	
	public void addCommand(ICommand doth){		
		wServerThread.addCommand(doth);
	}
	
	public static GameConfig getGameConfig(){
		return config;
	}
	
	public static HttpServerConfig getHttpConfig(){
		return httpConfig;
	}
	
	public static GMChatConfig getGmchatConfig() {
		return gmchatConfig;
	}

	public static NonageConfig getNonageConfig() {
		return nonageConfig;
	}

	public  ServerThread getServerThread(){
		return wServerThread;
	}
	
	public  ServerThread getCardThread(){
		return wCardThread;
	}
	
	public SchedularThread getwSchedularThread() {
		return wSchedularThread;
	}
	
	public SaveFriendThread getSaveFriendThread() {
		return wSaveFriendThread;
	}

	public SaveFruitThread getwSaveFruitThread() {
		return wSaveFruitThread;
	}

	public long getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(long closeTime) {
		this.closeTime = closeTime;
	}

	private class HttpServer extends Thread{

		public HttpServer(String name){
			super(name);
		}
		
		@Override
		public void run() {
			//注册Mina Nio端口接收
			NioSocketAcceptor acceptor = new NioSocketAcceptor();
			//绑定Mina安全服务器管理模块
	        acceptor.setHandler(new HttpServerProtocolHandler());
	        try{
	        	//绑定服务器数据监听端口，启动服务器
	        	acceptor.bind(new InetSocketAddress(httpConfig.getPort()));
	        	log.info("Http Server " + server.getServerName() + " Start At Port " + httpConfig.getPort());
	        }catch (IOException e) {
	        	log.error("Http Server " + server.getServerName() + " Port " + httpConfig.getPort() + "Already Use:" + e.getMessage());
	        	System.exit(1);
			}
		}
	}
	
	
	
	@Override
	public void sessionCreate(IoSession iosession) {
	}

	@Override
	public void sessionOpened(IoSession iosession) {
	}
}


