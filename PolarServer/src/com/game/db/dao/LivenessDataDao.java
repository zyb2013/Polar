package com.game.db.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

import com.game.db.DBServer;
import com.game.db.bean.LivenessBean;
import com.game.utils.LoggerProvider;
import com.game.utils.TimeUtil;
/**
 * 活跃度DB记录操作类
 * @author hongxiao.z
 * @date   2013-12-26  下午8:22:04
 */
public class LivenessDataDao 
{

	protected Logger log = Logger.getLogger(LivenessDataDao.class);
	
	private SqlSessionFactory sqlMapper = DBServer.getInstance().getSqlMapper();

	/**
	 * 将数据插入到数据库
	 * @param liveness		活跃度数据
	 * @return				
	 * @create	hongxiao.z      2013-12-26 下午8:23:19
	 */
    public int insert(LivenessBean liveness) {
        SqlSession session = sqlMapper.openSession();
    	try{
    		long currentTimeMillis = System.currentTimeMillis();
    		int rows = session.insert("liveness_data.insert", liveness);
    		session.commit();
    		LoggerProvider.getDbconsuminglog().info("liveness_data.insert "+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
    		return rows;
    	}catch (Exception e) {
		log.error(e);
		return 0;
		}
    	finally{
			session.close();
		}
    }

    /**
     * 从DB读取一条活跃度数据
     * @param roleid		角色ID
     * @return
     * @create	hongxiao.z      2013-12-26 下午8:25:45
     */
    public LivenessBean select(long roleid) 
    {
        SqlSession session = sqlMapper.openSession();
        try{
        	long currentTimeMillis = System.currentTimeMillis();
        	LivenessBean liveness = (LivenessBean)session.selectOne("liveness_data.select", roleid);
        	LoggerProvider.getDbconsuminglog().info("game_role.selectNames "+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
            return liveness;
    	}finally{
			session.close();
		}
    }
    

    /**
     * 更新领取状态数据
     * @param liveness
     * @return
     * @create	hongxiao.z      2013-12-26 下午8:26:22
     */
    public int updateGain(LivenessBean liveness) 
    {
        SqlSession session = sqlMapper.openSession();
    	try{
    		long currentTimeMillis = System.currentTimeMillis();
    		int rows = session.update("liveness_data.updateGain", liveness);
    		session.commit();
    		LoggerProvider.getDbconsuminglog().info("liveness_data.updateGain "+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
            return rows;
    	}finally{
			session.close();
		}
    }
    
    /**
     * 更新活跃度数据
     * @param liveness
     * @return
     * @create	hongxiao.z      2013-12-26 下午8:26:22
     */
    public int updateLiveness(LivenessBean liveness) 
    {
        SqlSession session = sqlMapper.openSession();
    	try{
    		long currentTimeMillis = System.currentTimeMillis();
    		int rows = session.update("liveness_data.updateLiveness", liveness);
    		session.commit();
    		LoggerProvider.getDbconsuminglog().info("liveness_data.updateLiveness "+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
            return rows;
    	}finally{
			session.close();
		}
    }
    
    /**
     * 更新事件进度数据
     * @param liveness
     * @return
     * @create	hongxiao.z      2013-12-26 下午8:26:22
     */
    public int updateEvent(LivenessBean liveness) 
    {
    	SqlSession session = sqlMapper.openSession();
    	try{
    		long currentTimeMillis = System.currentTimeMillis();
    		int rows = session.update("liveness_data.updateEvent", liveness);
    		session.commit();
    		LoggerProvider.getDbconsuminglog().info("liveness_data.updateEvent "+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
    		return rows;
    	}finally{
    		session.close();
    	}
    }
    
    /**
     * 更新事件进度数据
     * @param liveness
     * @return
     * @create	hongxiao.z      2013-12-26 下午8:26:22
     */
    public int updateAll(LivenessBean liveness) 
    {
    	SqlSession session = sqlMapper.openSession();
    	try{
    		long currentTimeMillis = System.currentTimeMillis();
    		int rows = session.update("liveness_data.updateAll", liveness);
    		session.commit();
    		LoggerProvider.getDbconsuminglog().info("liveness_data.updateAll "+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
    		return rows;
    	}finally{
    		session.close();
    	}
    }
}