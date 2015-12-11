package scripts.backend;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

import com.game.activities.script.IBackendWorldScript;
import com.game.backend.manager.BackendManager;
import com.game.backend.struct.ServerRequest;
import com.game.backpack.structs.Item;
import com.game.chat.message.ReqLoadBlackListWSMessage;
import com.game.data.bean.Q_cardBean;
import com.game.data.bean.Q_itemBean;
import com.game.data.manager.DataManager;
import com.game.db.DBServer;
import com.game.db.bean.BlackListBean;
import com.game.db.bean.GameUser;
import com.game.db.bean.Gold;
import com.game.db.bean.GoldRechargeLog;
import com.game.db.dao.BlackListDao;
import com.game.db.dao.GoldDao;
import com.game.db.dao.GoldRechargeLogDAO;
import com.game.db.dao.UserDao;
import com.game.dblog.LogService;
import com.game.guild.manager.GuildWorldManager;
import com.game.json.JSONserializable;
import com.game.mail.manager.MailWorldManager;
import com.game.manager.ManagerPool;
import com.game.monster.manager.MonsterManager;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerWorldInfo;
import com.game.prompt.structs.Notifys;
import com.game.recharge.RechargeEntry;
import com.game.recharge.RechargeHistory;
import com.game.recharge.RechargeManager;
import com.game.recharge.RechargelogLog;
import com.game.recharge.message.RechargeMessage;
import com.game.script.manager.ScriptManager;
import com.game.script.structs.ScriptEnum;
import com.game.scripts.message.ReqScriptToGameMessage;
import com.game.server.WorldServer;
import com.game.server.message.ReqCloseForGameMessage;
import com.game.server.message.ReqCloseForGateMessage;
import com.game.toplist.manager.TopListManager;
import com.game.toplist.structs.TopData;
import com.game.txconsume.script.ITxAddItemsScript;
import com.game.utils.MessageUtil;
import com.game.utils.ScriptsUtils;
import com.game.utils.ServerParamUtil;
import com.game.utils.Symbol;

public class BackendWorldScript implements IBackendWorldScript {

	private static final Logger logger = Logger.getLogger(BackendWorldScript.class);
	ExecutorService service;
	private static Logger log = Logger.getLogger(BackendWorldScript.class);

	@Override
	public int getId() {
		return ScriptEnum.BACKEND;
	}

	private BlackListDao blacklistdao = new BlackListDao();
	
	@SuppressWarnings("unchecked")
	@Override
	public String doServerRequest(ServerRequest sr, String requeststr) {
		String msg = "-56";
		if (sr.getActionid().equals("100")) {
			String roleid = sr.getRoleid();
			Player p = PlayerManager.getInstance().getPlayer(Long.valueOf(roleid));
			Map<String, String> playerinfo = new HashMap<String, String>();
			if (p != null) {
				playerinfo = new HashMap<String, String>();
				playerinfo.put("id", "" + p.getId());
				playerinfo.put("userid", "" + p.getUserId());
				playerinfo.put("name", p.getName());
				playerinfo.put("level", "" + p.getLevel());
				String adult = p.getIsAdult() == 1 ? "已成年" : "未成年";
				playerinfo.put("isAdult", adult);
				playerinfo.put("server", "" + p.getServer());
				playerinfo.put("location", "" + p.getMap() + "[" + p.getLine() + "线]");
				playerinfo.put("ipString", p.getIpString());
				String sex = p.getJob() !=  3 && p.getJob()!=6 && p.getJob() !=9 ? "男" : "女";
				playerinfo.put("sex", sex);
				msg = JSONserializable.toString(playerinfo);
			}
		} else if (sr.getActionid().equals("101")) { //封禁和解封账号 //通过username
			String username = sr.getContent();
			int state = sr.getIsDeleted();
			int result = -5;
			try {
				result = forbidUser(state, username);
			} catch (SQLException e) {
				log.error(e, e);
			}
			msg = ""+result;
		}else if(sr.getActionid().equals("102")){ //发送邮件
			return ""+sendmailrole(sr.getServerid(), sr.getRolename(), sr.getTitle(), sr.getContent(), sr.getSitem(), sr.getItems());
		}else if(sr.getActionid().equals("103")){ //账号禁言接口
			String content = sr.getContent(); //账号列表 逗号隔开
			String[] usernames = content.split(Symbol.DOUHAO_REG);
			String time = sr.getTime();
			long now = System.currentTimeMillis();
			long endtime = now+365*24*3600000;
			//插入数据库 或者 修改数据库  默认禁言一年
			Set<String> chatblackusernames = PlayerManager.getInstance().getChatBlackUsernames();
			List<BlackListBean> updateBeans = new ArrayList<BlackListBean>();
			List<BlackListBean> insertBeans = new ArrayList<BlackListBean>();
			if(!StringUtils.isBlank(time) && StringUtils.isNumeric(time)){
				endtime = now+Integer.parseInt(time)*60000; //有时间则为时间，没时间就默认一年
			}
			if(usernames!=null && usernames.length>0 && usernames.length<=30){
				for(String username: usernames){
					BlackListBean bean = new BlackListBean();
					bean.setUsername(username); bean.setType(1); bean.setState(0); bean.setEndtime(endtime);
					if(chatblackusernames.contains(username)){ //已经有的 修改
						updateBeans.add(bean);
					}else{ //没有的 1加入 set 2插入数据库 
						chatblackusernames.add(username);
						insertBeans.add(bean);
					}
				}
				//该插入插入 该修改修改 
				if(insertBeans.size()>0){
					for(BlackListBean b: insertBeans){
						blacklistdao.insert(b);
					}
				}
				if(updateBeans.size()>0){
					for(BlackListBean b: updateBeans){
						blacklistdao.updateByUsernameType(b);
					}
				}
				msg = "1"; //成功执行
				//通知游戏服重加载禁言列表
				ReqLoadBlackListWSMessage reqmsg = new ReqLoadBlackListWSMessage();
				reqmsg.setBlacktype(1);
				MessageUtil.send_to_game(reqmsg);
			}else if(usernames.length>=30){
				msg = "2"; //传入账号超过一次处理上线 30个 //TODO定一个上限
			}else{
				msg = "3"; //传入账号格式错误或账号列表为空
			}
		} else if(sr.getActionid().equals("104")){ //IP禁言
			String content = sr.getContent();
			String[] ips = content.split(Symbol.DOUHAO_REG);
			String time = sr.getTime();
			long now = System.currentTimeMillis();
			long endtime = now+365*24*3600000;
			Set<String> chatblackips = PlayerManager.getInstance().getChatBlackIPs();
			List<BlackListBean> updateBeans = new ArrayList<BlackListBean>();
			List<BlackListBean> insertBeans = new ArrayList<BlackListBean>();
			if(!StringUtils.isBlank(time) && StringUtils.isNumeric(time)){
				endtime = now+Integer.parseInt(time)*60000; //有时间则为时间，没时间就默认一年
			}
			if(ips!=null && ips.length>0 && ips.length<=30){
				for(String ip: ips){
					BlackListBean bean = new BlackListBean();
					bean.setUsername(ip); bean.setType(2); bean.setState(0); bean.setEndtime(endtime);
					if(chatblackips.contains(ip)){ //已经有的 修改
						updateBeans.add(bean);
					}else{ //没有的 1加入 set 2插入数据库 
						chatblackips.add(ip);
						insertBeans.add(bean);
					}
				}
				if(insertBeans.size()>0){
					for(BlackListBean b: insertBeans){
						blacklistdao.insert(b);
					}
				}
				if(updateBeans.size()>0){
					for(BlackListBean b: updateBeans){
						blacklistdao.updateByUsernameType(b);
					}
				}
				msg = "1"; //成功执行
				//通知游戏服重加载禁言列表
				ReqLoadBlackListWSMessage reqmsg = new ReqLoadBlackListWSMessage();
				reqmsg.setBlacktype(2);
				MessageUtil.send_to_game(reqmsg);
			}else if(ips.length>30){
				msg = "2"; //传入ip超过一次处理上线 30个 
			}else{
				msg = "3"; //传入ip格式错误或ip列表为空
			}
		} else if(sr.getActionid().equals("106")){ //设置服务器双倍时间
			String dateString = sr.getContent();
			int serverid = Integer.parseInt(sr.getServerid());
			MonsterManager.getInstance().setsysDouble(dateString, serverid);
			return "2";
		} else if(sr.getActionid().equals("107")){ //离线发钻石
			String username = sr.getUsername(), num = sr.getNum(), sid=sr.getServerid(), oid=sr.getOid();
			String ret = "-4";
			try {
				int re = innergoldrecharge(username, num, sid, oid);
				ret = String.valueOf(re);
			} catch (NumberFormatException e) {
				log.error(e, e);
			} catch (SQLException e) {
				log.error(e, e);
			}
			msg = ret;
		} else if(sr.getActionid().equals("108")){ //离线查询剩余钻石
			int goldnum = -1;
			try {
				String userid = String.valueOf(sr.getUserid());
				String serverid = sr.getServerid();
				Gold gold = dao.select(userid, Integer.parseInt(serverid));
				if(gold!=null) goldnum = gold.getGold();
			} catch (NumberFormatException e) {
				log.error(e, e);
			} catch (Exception e) {
				log.error(e, e);
			}
			msg = String.valueOf(goldnum);
		} else if (sr.getActionid().equals("998")) { //根据userid查看 userPlayers里面的内容
			String userid = "" + sr.getUserid();
			StringBuffer sb = new StringBuffer();
			if (!StringUtils.isBlank(userid)) {
				List<Player> rolelist = PlayerManager.getInstance().getPlayersByUser(userid);
				if (rolelist != null) {
					sb.append("[" + rolelist.size() + "]");
					for (Player p : rolelist) {
						sb.append("[" + p.getId() + "\t" + p.getName() + "]");
					}
				} else {
					sb.append("rolelist=空");
				}
			} else {
				sb.append("userid不存在" + userid);
			}
			msg = sb.toString();
		} else if (sr.getActionid().equals("999")) { //根据userid 移除 userPlayers 里的角色
			String userid = "" + sr.getUserid();
			long roleid = Long.valueOf(sr.getRoleid());
			StringBuffer sb = new StringBuffer();
			if (StringUtils.isBlank(userid) || StringUtils.isBlank("" + roleid)) {
				sb.append("userid" + userid + "或者roleid" + roleid + " 为空");
			} else {
				sb.append("开始移除玩家==");
				List<Player> rolelist = PlayerManager.getInstance().getPlayersByUser(userid);
				Iterator<Player> it = rolelist.iterator();
				while (it.hasNext()) {
					Player p = it.next();
					if (p.getId() == roleid) {
						sb.append("移除角色[" + roleid + "=" + p.getName() + "]成功 ==");
						it.remove();
					}
				}
				sb.append("移除玩家结束==");
			}
			msg = sb.toString();
		} else if(sr.getActionid().equals("1000")){
			String content = sr.getContent(); //传入的充值json字符串
			if(StringUtils.isBlank(content)){ msg = "-2"; }
			HashMap<String, String> parammap = new HashMap<String, String>();
			parammap = (HashMap<String, String>) JSONserializable.toObject(content, HashMap.class);
			//构造充值参数map
			if(parammap==null){ msg = "-2";}
			RechargeEntry entry = new RechargeEntry(parammap, content);
			//调用充值方法
			int reacharge = reacharge(entry);
			msg = String.valueOf(reacharge); //返回充值结果
		} else if(sr.getActionid().equals("1001")){ //清理服务器双倍
			setDaguaiDoubleTime();
			msg = "1001";
		} else if(sr.getActionid().equals("1002")){
			ReqScriptToGameMessage callmsg = new ReqScriptToGameMessage();
			callmsg.setScriptId(13);
			callmsg.setMethod("clearCollect");
			MessageUtil.send_to_game(callmsg);
			System.out.println("in 1002 actionid"+sr.getActionid());
			msg = "1002";
		}else if(sr.getActionid().equals("1003")) { //倒计时关服
			//单位 秒
			int secend = Integer.parseInt(sr.getNum());
			if(secend<0){ msg = "-1003"; }
			else{
				try {
					Thread.sleep(secend*1000); //
				} catch (InterruptedException e) {
					log.error(e, e);
				}
				//
				//立刻关掉网关 睡眠一段时间时间 再关游戏服和世界服
				ReqCloseForGateMessage reqgmsg= new ReqCloseForGateMessage();
				MessageUtil.send_to_gate(reqgmsg); //关掉网关
				msg = "1003";
				//停倒计时时间
				try {
					Thread.sleep(5*60*1000); //5分钟
				} catch (InterruptedException e) {
					log.error(e, e);
				}
				//关游戏服
				ReqCloseForGameMessage reqgamemsg = new ReqCloseForGameMessage();
				MessageUtil.send_to_game(reqgamemsg);
				try {
					Thread.sleep(10*1000); //10秒 让世界服把关闭消息发出去
				} catch (InterruptedException e) {
					log.error(e, e);
				}
				//关世界服
				System.exit(0);
			}
		} else if(sr.getActionid().equals("1004")) { //查看内存里的card数据是否正确
			Q_cardBean q_cardBean = DataManager.getInstance().q_cardContainer.getMap().get("24_2");
			if(q_cardBean!=null){
				int q_item_id = q_cardBean.getQ_item_id();
				msg = ""+q_item_id;
			}else{
				msg = "q_cardBean.get 24_2 为空";
			}
		} else if(sr.getActionid().equals("1005")){ //changeres
			ReqScriptToGameMessage callmsg = new ReqScriptToGameMessage();
			callmsg.setScriptId(13);
			callmsg.setMethod("changeres");
			MessageUtil.send_to_game(callmsg);
			System.out.println("in 1005 actionid"+sr.getActionid());
			msg = "1005";
		} else if(sr.getActionid().equals("1000001")){ //腾讯道具发放
			String resstring = "";
			Map<String, String> resmap = new HashMap<String, String>();
			resmap.put("ret", "-8");
			resmap.put("msg", "道具发放脚本没找到");
			ITxAddItemsScript script = (ITxAddItemsScript) ScriptManager.getInstance().getScript(ScriptEnum.TXADDITEMS);
			if(script==null){
				log.error("腾讯发放道具脚本["+ScriptEnum.TXADDITEMS+"]不存在");
				resstring = JSONserializable.toString(resmap);
			}else{
				resstring = script.reqAddItems(requeststr);
			}
			msg = resstring;
		} else if(sr.getActionid().equals("1006")){ //关监狱
			String roleid = sr.getContent();
			Player player = PlayerManager.getInstance().getPlayer(Long.valueOf(roleid));
			if(player!=null){
				ScriptsUtils.callGame(player, ScriptEnum.BACKENDSERVER, "inprison", roleid);
				msg = "1006";
			}else{
				msg = "0";
			}
		} else if(sr.getActionid().equals("1007")){ //放出监狱
			String roleid = sr.getContent();
			Player player = PlayerManager.getInstance().getPlayer(Long.valueOf(roleid));
			if(player!=null){
				ScriptsUtils.callGame(player, ScriptEnum.BACKENDSERVER, "outprison", roleid);
				msg = "1007";
			}else{
				msg = "0";
			}
		} else if(sr.getActionid().equals("1008")){ //全部放出监狱
			ConcurrentHashMap<Long,Player> players = PlayerManager.getPlayers();
			if(players.size()>0){
				Object[] playerz = players.values().toArray();
				Player p = (Player)playerz[0];
				if(p!=null){
					ScriptsUtils.callGame(p, ScriptEnum.BACKENDSERVER, "alloutprison", String.valueOf(p.getServer()));
					msg = "1008";
				}else{
					msg = "--1008";
				}
			}else{
				msg = "操作失败 没有角色在线";
			}
		}
		return msg;
	}
	
	
	
	
	private static Pattern pattern = Pattern.compile("(-?\\d+)\\_(\\d+)");
	private static Pattern pattern1 = Pattern.compile("(-?\\d+)\\_(\\d+)\\_([01])\\_(\\d+)"); //是否绑定 过期时间(小时)
	private static Pattern pattern2 = Pattern.compile("(-?\\d+)\\_(\\d+)\\_([01])\\_(\\d+)\\_(\\d{1,2})\\_(.+)"); // id+num+bind+losttime+grade+append(取出来再检测)
	public int sendmailrole(String serverid, String rolename, String title, String content, String sitem, String items){
		String zone = "["+serverid+"区]";
		String name = zone+rolename;
		PlayerWorldInfo playerWorldInfo = PlayerManager.getInstance().getPlayerWorldInfo(name);
 		if(playerWorldInfo==null) return 2; //角色不存在
		List<Item> itemlist = new ArrayList<Item>();
		int itemid=0, itemnum=0, grade=0, groupcount=0;
		boolean isbind = false; long losttime = 0L;
		String append="";
		if(!StringUtils.isBlank(sitem) && "checked".equals(sitem) && !StringUtils.isBlank(items)){
			String[] itemss = items.split("&");
			for(String s: itemss){
				if(StringUtils.isBlank(s)) continue;
				Matcher m2 = pattern2.matcher(s);
				Matcher m1 = pattern1.matcher(s);
				Matcher m = pattern.matcher(s);
				if(m.matches()){
					groupcount = m.groupCount();
					itemid = Integer.parseInt(m.group(1));
					itemnum = Integer.parseInt(m.group(2));
				}else if(m1.matches()){
					groupcount = m1.groupCount();
					itemid = Integer.parseInt(m1.group(1));
					itemnum = Integer.parseInt(m1.group(2));
					isbind = Integer.parseInt(m1.group(3))>0? true:false;
					int losthour = Integer.parseInt(m1.group(4)); 
					losttime = losthour==0?0: System.currentTimeMillis()+losthour*3600000;
				}else if(m2.matches()){
					groupcount = m2.groupCount();
					itemid = Integer.parseInt(m2.group(1));
					itemnum = Integer.parseInt(m2.group(2));
					isbind = Integer.parseInt(m2.group(3))>0? true:false;
					int losthour = Integer.parseInt(m2.group(4));
					losttime = losthour==0?0: System.currentTimeMillis()+losthour*3600000;
					grade = Integer.parseInt(m2.group(5));
					append = m2.group(6);
				}else{
					return -40;
				}
				Q_itemBean q_itemBean = DataManager.getInstance().q_itemContainer.getMap().get(itemid);
				if(q_itemBean==null){ return itemid; } //物品不存在
				if(itemid>0){
					if(groupcount==6){
						itemlist.addAll(Item.createItems( itemid, itemnum, isbind, losttime, grade, append));
					}else if(groupcount==4){
						itemlist.addAll(Item.createItems( itemid, itemnum, isbind, losttime));
					}else if(groupcount==2){
						itemlist.addAll(Item.createItems( itemid, itemnum, false , 0));
					}
				}else if(itemid==-1){
					itemlist.add(Item.createMoney(itemnum));
				}else if(itemid==-2){
					return -2;  //暂不支持钻石
				}else if(itemid==-3){
					itemlist.add(Item.createZhenQi(itemnum));
				}else if(itemid==-4){
					itemlist.add(Item.createExp(itemnum));
				}else if(itemid==-5){
					itemlist.add(Item.createBindGold(itemnum));
				}else if(itemid==-6){ //战魂
					itemlist.add(Item.createFightSpirit(itemnum));
				}else if(itemid==-7){ //军功
					itemlist.add(Item.createRank(itemnum));
				}
			}
		}
		long sendresult = MailWorldManager.getInstance().sendSystemMail(name, title, content, (byte)1, 0, itemlist);
		return sendresult>0?-50: sendresult==-2?-50: (int)sendresult;
	}
	
	
	
	private SqlSessionFactory sqlMapper = DBServer.getInstance().getSqlMapper();

	public int delUserByUseridServer(String userid, String serverid){
		SqlSession session = sqlMapper.openSession();
		try {
			int rows = 0;
			Connection conn = session.getConnection();
			PreparedStatement pstmt = conn.prepareStatement("DELETE FROM user WHERE userid=?");
			pstmt.setString(1, userid);
			rows = pstmt.executeUpdate();
			return rows;
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			session.close();
		}
		return -1;
	}
	
	public Map<String, Integer> getUseridLevelMap(String serverid){
		Map<String, Integer> useridlevelmap = new HashMap<String, Integer>();
		SqlSession session = sqlMapper.openSession();
		try {
			Connection conn = session.getConnection();
			PreparedStatement pstmt = conn.prepareStatement("SELECT r.userid, r.level FROM role r WHERE r.deleted=0");
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				String userid = rs.getString("userid");
				int level = rs.getInt("level");
				if(useridlevelmap.containsKey(userid)){
					int curlevel = useridlevelmap.get(userid);
					if(level>curlevel){
						useridlevelmap.put(userid, level);
					}
				}else{
					useridlevelmap.put(userid, level);
				}
			}rs.close();
			return useridlevelmap;
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			session.close();
		}
		return useridlevelmap;
	}
	
	public List<String> getUseridsByUsernameServer(String username, String serverid){
		List<String> userids = new ArrayList<String>();
		SqlSession session = sqlMapper.openSession();
		try {
			Connection conn = session.getConnection();
			PreparedStatement pstmt = conn.prepareStatement("SELECT u.userid FROM user u WHERE u.userName=? AND u.server=?");
			pstmt.setString(1, username);
			pstmt.setString(2, serverid);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				String userid = rs.getString("userid");
				userids.add(userid);
			}rs.close();
			return userids;
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			session.close();
		}
		return userids;
	}
	
	public int forbidUser(int state, String userid) throws SQLException {
		SqlSession session = sqlMapper.openSession();
		try {
			int rows = 0;
			Connection conn = session.getConnection();
			PreparedStatement pstmt = conn.prepareStatement("UPDATE user SET isforbid=? WHERE userName=?");
			pstmt.setInt(1, state);
			pstmt.setString(2, userid);
			rows = pstmt.executeUpdate();
			return rows;
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			session.close();
		}
		return -1;
	}

	public void deleteTopMap(List<Object> param) {
		int toptype = Integer.valueOf(param.get(0).toString());
		int topidx = 0;
		if (param.size() >= 2) {
			topidx = Integer.valueOf(param.get(1).toString());
		}
		TreeMap<TopData, Long> treeMap = TopListManager.getInstance().getTreeMap(toptype);
		if (!treeMap.isEmpty()) {
			TopData topData = (TopData) treeMap.keySet().toArray()[topidx];
			if (topData != null) {
				treeMap.remove(topData);
			}
		}
	}
	
	public int innergoldrecharge(String username, String num, String sid, String oid) throws NumberFormatException, SQLException{
		int result = -3;
		GameUser u = userDao.selectGameUser(username, Integer.parseInt(sid)); //根据账号取
		if(u!=null){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("UID", u.getUsername());  //UID
			map.put("OID", oid);   //OID
			map.put("SID", sid);   //SID
			map.put("GOLD", num);  //GOLD
			map.put("IP", "-1");   //IP
			map.put("TYPE", "9");  //TYPE
			map.put("REMARK", ""); //REMARK
			map.put("TIME", "");   //TIME 
			map.put("SIGN", "");   //SIGN 
			String msg=JSONserializable.toString(map);
			RechargeEntry entry = new RechargeEntry(map, msg);
			result = RechargeManager.getInstance().reacharge(entry);
		}
		return result;
	}
	

	public void worldreload(List<Object> param) {
		final long roleID = Long.parseLong(param.get(0).toString());
		final String tableName = param.get(1).toString();
		logger.info("接到重新load" + tableName + "指令");
		service.execute(new Runnable() {

			@Override
			public void run() {
				logger.info("reload " + tableName + " is starting");
				try {
					Player player = PlayerManager.getInstance().getPlayer(roleID);
					if (tableName != null && !tableName.equals("")) {
						Field declaredField = DataManager.getInstance().getClass().getDeclaredField(tableName + "Container");
						int hashCode = declaredField.get(DataManager.getInstance()).hashCode();
						Class<?> cls = declaredField.getType();
						Object newInstance = cls.newInstance();
						Method method = cls.getMethod("load");
						method.invoke(newInstance);
						declaredField.set(DataManager.getInstance(), newInstance);
						int hashCode2 = declaredField.get(DataManager.getInstance()).hashCode();
						if (hashCode != hashCode2) {
							logger.info("reload " + tableName + " end");
						} else {
							logger.info("reload " + tableName + " is faild" + hashCode + " " + hashCode2);
						}
						if (player != null) {
							MessageUtil.notify_player(player, Notifys.SUCCESS, "重加载表{1}成功", tableName);
						}

					} else {
						Field[] declaredFields = DataManager.getInstance().getClass().getDeclaredFields();
						for (Field field : declaredFields) {
							int hashCode = field.get(DataManager.getInstance()).hashCode();
							Class<?> cls = field.getType();
							Object newInstance = cls.newInstance();
							Method method = cls.getMethod("load");
							method.invoke(newInstance);
							field.set(DataManager.getInstance(), newInstance);
							int hashCode2 = field.get(DataManager.getInstance()).hashCode();
							if (hashCode != hashCode2) {
								logger.info("reload " + tableName + " end");
							} else {
								logger.info("reload " + tableName + " is faild" + hashCode + " " + hashCode2);
							}
							if (player != null) {
								MessageUtil.notify_player(player, Notifys.SUCCESS, "重加载所有表成功", tableName);
							}
						}
					}

				} catch (SecurityException e) {
					logger.error("run()", e);
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					logger.error("run()", e);
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					logger.error("run()", e);
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					logger.error("run()", e);
					e.printStackTrace();
				} catch (InstantiationException e) {
					logger.error("run()", e);
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					logger.error("run()", e);
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					logger.error("run()", e);
					e.printStackTrace();
				}

				if (logger.isDebugEnabled()) {
					logger.debug("run() - end");
				}
			}
		});

		if (logger.isDebugEnabled()) {
			logger.debug("reLoad(String) - end");
		}
	}

	public void loadserverparam(List<Object> param) {
		log.error("开始执行loadserverparam");
		ServerParamUtil.loadServerParam(WorldServer.getInstance().getServerId());
		log.error("执行loadserverparam成功");
		if (!param.isEmpty()) {
			try {
				long roleID = Long.parseLong(param.get(0).toString());
				Player player = PlayerManager.getInstance().getPlayer(roleID);
				if (player != null) {
					MessageUtil.notify_player(player, Notifys.SUCCESS, "执行loadserverparam成功");
				}
			} catch (Exception e) {
				log.error("没有玩家数据");
			}
		}
	}

	public void saveallguild(List<Object> param) {
		log.error("开始执行saveallguild");
		GuildWorldManager.getInstance().saveAllGuild();
		log.error("执行saveallguild成功");
		if (!param.isEmpty()) {
			try {
				long roleID = Long.parseLong(param.get(0).toString());
				Player player = PlayerManager.getInstance().getPlayer(roleID);
				if (player != null) {
					MessageUtil.notify_player(player, Notifys.SUCCESS, "执行saveallguild成功");
				}
			} catch (Exception e) {
				log.error("没有玩家数据");
			}
		}
	}
	
	
	private GoldDao dao = new GoldDao();
	private GoldRechargeLogDAO rechargelog = new GoldRechargeLogDAO();
	private UserDao userDao = new UserDao();
	private static final Logger recharge = Logger.getLogger("RECHARGELOG");
	//充值方法
	public int reacharge(RechargeEntry entry) {
		recharge.info(entry.toString());
		log.info(entry.toString());
		try {
			if (StringUtils.isBlank(entry.getOid())) {
				//参数错误 订单号不能为空
				return 1;
			}

			if (StringUtils.isBlank(entry.getGold()) || Integer.parseInt(entry.getGold()) == 0) {
				//参数错误 充值数不能为空或者为零
				return 2;
			}

			if (StringUtils.isBlank(entry.getSid())) {
				//参数错误 sid不能为空
				return 3;
			}

			if (StringUtils.isBlank(entry.getUid())) {
				//参数错误 uid不能为空
				return 4;
			}
			GoldRechargeLog selectById = rechargelog.selectById(entry.getOid());
			if (selectById != null) {
				//参数错误 订单号重复
				return 5;
			}
			int serverId = Integer.parseInt(entry.getSid());
			GameUser user = userDao.selectGameUser(entry.getUid(), serverId);
			if (user == null) {
				//参数错误 找不到账户
				return 6;
			}
//			List<Player> result = PlayerManager.getInstance().getPlayerByUserServerId(String.valueOf(user.getUserid()), serverId);
			//订单号验证
			Gold gold = dao.select(String.valueOf(user.getUserid()), serverId);
			int rechargeNum = Integer.parseInt(entry.getGold());
			if (gold == null) {
				gold = new Gold();
				gold.setUserId(String.valueOf(user.getUserid()));
				gold.setServerId(serverId);
				gold.setGold(rechargeNum);
				gold.setTotalGold(rechargeNum > 0 ? rechargeNum : 0);
				gold.setCostGold(0);
				gold.setBuyitemresume(0);
				gold.setFaildrollBackadd(0);
				gold.setGettempybadd(0);
				gold.setHuokuanAdd(0);
				gold.setJiaoyiresume(0);
				gold.setJiaoyiybadd(0);
				gold.setShangjiaresume(0);
				gold.setTwgmadd(0);
				gold.setYbxiajiaadd(0);
//				gold.setIsinner(0);
				dao.insert(gold);
			} else {
				Gold savegold = new Gold();
				savegold.setGold(rechargeNum);
				savegold.setTotalGold(rechargeNum > 0 ? rechargeNum : 0);
				savegold.setUserId(String.valueOf(user.getUserid()));
				savegold.setServerId(serverId);
//				gold.setGold(rechargeNum);
//				gold.setTotalGold(rechargeNum>0?rechargeNum:0);
				dao.update(savegold);
			}

			GoldRechargeLog rechargeLog = new GoldRechargeLog();
			rechargeLog.setGold(rechargeNum);
			rechargeLog.setOid(entry.getOid());
			rechargeLog.setServerid(entry.getSid());
			rechargeLog.setTime(System.currentTimeMillis());
			rechargeLog.setType(Integer.parseInt(entry.getType()));
			rechargeLog.setUid(entry.getUid());
			rechargeLog.setUserid(user.getUserid());
			rechargeLog.setRmb(entry.getRMB());
			rechargeLog.setRechargeContent(entry.toString());
			rechargelog.insert(rechargeLog);

			//添加gamelog库日志
			RechargelogLog logLog = new RechargelogLog(rechargeLog);
			LogService.getInstance().execute(logLog);
			
			List<Player> result = PlayerManager.getInstance().getPlayersByUser(String.valueOf(user.getUserid()));
			if (result!=null && result.size() > 1) {
				logger.error("userId=" + user.getUserid() + ",serverId" + serverId + "出现了同一个服同一个账号登录两个角色的情况,忽略充值请求" + entry.toString(), new Exception());
				for (Player player : result) {
					BackendManager.getInstance().kickonlineplayer(String.valueOf(player.getId()));	
				}
				return 0;
			}
			if (result!=null && result.size() > 0) {   // 通知到Server
				for (Player player : result) {
					RechargeMessage msg = new RechargeMessage();
					msg.setPlayerId(player.getId());
					msg.setRechargeParam(rechargeNum);
					msg.setOid(entry.getOid());
					MessageUtil.send_to_game(player, msg);
					if (player.getRechargeHistorys() != null) {
						RechargeHistory rechargeHistory = new RechargeHistory();
						rechargeHistory.setGold(rechargeLog.getGold());
						rechargeHistory.setTime(rechargeLog.getTime());
						player.getRechargeHistorys().add(rechargeHistory);
					}
				}
			}
		} catch (Exception e) {
			logger.error("充值出现异常\t" + entry.toString(), e);
			return -2;
		}
		return 0;
	}
	
	//
	/**清理双倍字段
	 * 
	 */
	public void setDaguaiDoubleTime(){
		ManagerPool.monsterManager.setDaguaiDoubleTime(null);
	}
	
	public static void main(String[] args){
//		String items = "[{\"aaa\":-2,\"bbb\":3},{\"aaa\":1000,\"bbb\":5}]"; 
//		List<HashMap<String, String>> listmap = (List<HashMap<String, String>>) JSONserializable.toList(items, HashMap.class);
//		System.out.println(listmap.size());
	}
	
}
