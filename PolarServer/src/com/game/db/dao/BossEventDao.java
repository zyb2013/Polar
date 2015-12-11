package com.game.db.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.db.DBServer;
import com.game.utils.TimeUtil;
import com.game.utils.LoggerProvider;
import com.game.db.bean.BossEventBean;
import com.game.db.bean.WeddingBean;
/**
 * BOSS掉落事件
 * @author leaon
 * @date   2013-12-26  下午8:22:04
 */

public class BossEventDao{
	protected Logger log = Logger.getLogger(BossEventDao.class);
	
	private SqlSessionFactory sqlMapper = DBServer.getInstance().getSqlMapper();
	
	//! 插入数据
	public int insert(BossEventBean bossevent){
        SqlSession session = sqlMapper.openSession();
    	try{
    		long currentTimeMillis = System.currentTimeMillis();
    		int rows = session.insert("boss_event.insert", bossevent);
    		session.commit();
    		LoggerProvider.getDbconsuminglog().info("boss_event.insert "+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
    		return rows;
    	}catch (Exception e) {
			log.error(e);
			return 0;
		}
    	finally{
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<BossEventBean> select(){
        SqlSession session = sqlMapper.openSession();
        try{
        	long currentTimeMillis = System.currentTimeMillis();
        	List<BossEventBean> bossdrops = (List<BossEventBean>)session.selectList("boss_event.select");
        	LoggerProvider.getDbconsuminglog().info("boss_event.select "+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
            return bossdrops;
    	}catch (Exception e) {
			log.error(e);
			return null;
		}finally{
			session.close();
		}
	}
	
	public int delete(long eventId) {
		SqlSession session = sqlMapper.openSession();
		try{
			long currentTimeMillis = System.currentTimeMillis();
			int rows = session.delete("boss_event.delete", eventId);
			session.commit();
			LoggerProvider.getDbconsuminglog().info("boss_event.delete"+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
			return rows;
		}finally{
			session.close();
		}
	}	
}
