package com.game.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

import com.game.db.DBServer;
import com.game.db.bean.ProtectBean;
import com.game.utils.LoggerProvider;
import com.game.utils.TimeUtil;

public class ProtectDao {

	private Logger log = Logger.getLogger(ProtectDao.class);
	private SqlSessionFactory sqlMapper = DBServer.getInstance().getSqlMapper();

    public int insert(ProtectBean spbean) throws SQLException {
        SqlSession session = sqlMapper.openSession();
    	try{
    		long currentTimeMillis = System.currentTimeMillis();
    		int rows = session.insert("protect.insert", spbean);
    		session.commit();
    		LoggerProvider.getDbconsuminglog().info("protect.insert "+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
    		return rows;
    	}catch (Exception e) {
    		log.error(e);
		}finally{
			session.close();
		}
    	return 0;
    }

    @SuppressWarnings("unchecked")
	public List<ProtectBean> select() throws SQLException {
        SqlSession session = sqlMapper.openSession();
        try{
        	long currentTimeMillis = System.currentTimeMillis();
        	List<ProtectBean> list = (List<ProtectBean>)session.selectList("protect.select");
        	LoggerProvider.getDbconsuminglog().info("protect.select "+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
            return list;
    	}catch (Exception e) {
    		log.error(e);
		}finally{
			session.close();
		}
    	return new ArrayList<ProtectBean>();
    }

    
    /**读取单个玩家的摆摊信息
     * 
     * @return
     * @throws SQLException
     */
	public ProtectBean selectsingle(String id)  {
        SqlSession session = sqlMapper.openSession();
        try{
        	long currentTimeMillis = System.currentTimeMillis();
        	ProtectBean protectBean = (ProtectBean)session.selectOne("protect.selectsingle",id);
        	LoggerProvider.getDbconsuminglog().info("protect.selectsingle "+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
            return protectBean;
		}finally{
			session.close();
		}
    }
    
    
    public int update(ProtectBean protectBean) throws SQLException {
        SqlSession session = sqlMapper.openSession();
    	try{
    		long currentTimeMillis = System.currentTimeMillis();
    		int rows = session.update("protect.update", protectBean);
    		session.commit();
    		LoggerProvider.getDbconsuminglog().info("protect.update "+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
            return rows;
    	}catch (Exception e) {
			log.error(e);
		}finally{
			session.close();
		}
    	return 0;
    }
    
	public int delete(String id) {
		SqlSession session = sqlMapper.openSession();
		try {
			long currentTimeMillis = System.currentTimeMillis();
			int rows = session.delete("protect.delete", id);
			session.commit();
			LoggerProvider.getDbconsuminglog().info("protect.delete "+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
			return rows;
		} catch (Exception e) {
			log.error(e);
		} finally {
			session.close();
		}
		return 0;
	}
	
	

	
}







