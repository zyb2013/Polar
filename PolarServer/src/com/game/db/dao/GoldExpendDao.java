package com.game.db.dao;

import java.util.HashMap;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

import com.game.db.DBServer;
import com.game.db.bean.GoldExpend;
import com.game.utils.LoggerProvider;
import com.game.utils.TimeUtil;

public class GoldExpendDao {

	protected Logger log = Logger.getLogger(GoldExpendDao.class);
	
	private SqlSessionFactory sqlMapper = DBServer.getInstance().getSqlMapper();


    public int insert(GoldExpend expend) {
        SqlSession session = sqlMapper.openSession();
    	try {
    		long currentTimeMillis = System.currentTimeMillis();
    		int rows = session.insert("gold_expend.insert", expend);
    		session.commit();
    		LoggerProvider.getDbconsuminglog().info("gold_expend.insert "+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
    		return rows;
    	} catch (Exception e) {
    		log.error(e, e);
    		return 0;
    	} finally {
			session.close();
		}
    }

	public int select(long roleid, long startTime, long endTime) {
		int ret = 0;
		SqlSession session = sqlMapper.openSession();
    	try {
    		long currentTimeMillis = System.currentTimeMillis();
    		HashMap<String, Object> map = new HashMap<String, Object>();
    		map.put("roleid", roleid);
    		map.put("start_time", startTime);
    		map.put("end_time", endTime);
    		ret = (Integer)session.selectOne("gold_expend.select", map);
    		LoggerProvider.getDbconsuminglog().info("gold_expend.select "+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
    	} finally {
			session.close();
		}
    	return ret;
	}
}