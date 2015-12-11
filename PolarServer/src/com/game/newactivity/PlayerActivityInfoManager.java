package com.game.newactivity;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.game.db.dao.PlayerActivityInfoDao;
import com.game.newactivity.model.PlayerActivityInfo;

/**
 * @author luminghua
 *
 * @date   2014年2月25日 下午9:32:13
 * 
 * 做更新队列
 */
public class PlayerActivityInfoManager {


	private static PlayerActivityInfoManager instance;
	private final static Object obj = new Object();
	
	private Logger logger = Logger.getLogger(PlayerActivityInfoManager.class);
	
	private PlayerActivityInfoDao infoDao = new PlayerActivityInfoDao();
	
	private ConcurrentHashMap<Long,Map<Integer,PlayerActivityInfo>> playerInfoMap = new ConcurrentHashMap<Long,Map<Integer,PlayerActivityInfo>>();
	
	private LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
	
	private Thread thread;
	
	public static PlayerActivityInfoManager getInstance() {
		if(instance == null) {
			synchronized(obj) {
				if(instance == null) {
					instance = new PlayerActivityInfoManager();
				}
			}
		}
		return instance;
	}
	
	public void start() {
		Worker worker = new Worker();
		thread = new Thread(worker);
		thread.start();
	}
	
	public void stop() {
		thread.interrupt();
	}
	
	Map<Long, Map<Integer, PlayerActivityInfo>> getPlayerInfoMap() {
		return playerInfoMap;
	}


	public void removePlayerActivityInfo(long playerId,int activityId) {
		Map<Integer, PlayerActivityInfo> map = playerInfoMap.get(playerId);
		if(map != null)
			map.remove(activityId);
	}
	public void removePlayerActivityInfo(int activityId) {
		Iterator<Map<Integer, PlayerActivityInfo>> iterator = playerInfoMap.values().iterator();
		while(iterator.hasNext()) {
			Map<Integer, PlayerActivityInfo> next = iterator.next();
			next.remove(activityId);
		}
	}
	public void addPlayerActivityInfo(PlayerActivityInfo info) {
		Map<Integer, PlayerActivityInfo> map = playerInfoMap.get(info.getPlayerId());
		if(map != null) {
			map.put(info.getActivityId(), info);
		}
	}
	
	public PlayerActivityInfo getPlayerActivityInfo(long playerId,int activityId) {
		Map<Integer, PlayerActivityInfo> map = playerInfoMap.get(playerId);
		if(map == null)
			return null;
		return map.get(activityId);
	}
	
    public PlayerActivityInfo selectById(int id) {
    	return infoDao.selectById(id);
    }
    
    public List<PlayerActivityInfo> selectByPlayerId(long playerId) {
    	return infoDao.selectByPlayerId(playerId);
    }
    
    public List<PlayerActivityInfo> selectByActivityId(int activityId) {
    	return infoDao.selectByActivityId(activityId);
    }
    
    public List<PlayerActivityInfo> selectByActivityIdAndAward(int activityId) {
    	return infoDao.selectByActivityIdAndAward(activityId);
    }
    
    public void insert(final PlayerActivityInfo playerActivityInfo) {
    	queue.add(new Runnable() {

			@Override
			public void run() {
				infoDao.insert(playerActivityInfo);				
			}
    		
    	});
    }

    public void update(final PlayerActivityInfo playerActivityInfo) {
    	queue.add(new Runnable() {

			@Override
			public void run() {
				infoDao.update(playerActivityInfo);
			}
    		
    	});
    }
    
    public void delete(final int activityId) {
    	queue.add(new Runnable() {

			@Override
			public void run() {
				infoDao.delete(activityId);			
			}
    		
    	});
    }
    
    private class Worker implements Runnable{

		@Override
		public void run() {
			while(true) {
				try {
					Runnable task = queue.take();
					task.run();
				}catch(InterruptedException i) {
					logger.error("stop worker");
					return;
				}catch (Exception e) {
					logger.error("", e);
				}
			}
		}
    	
    	
    }
}
