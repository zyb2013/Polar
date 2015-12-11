package com.game.db.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

import com.game.db.DBServer;
import com.game.newactivity.model.PlayerActivityInfo;
import com.game.utils.CollectionUtil;

public class PlayerActivityInfoDao {

	protected Logger log = Logger.getLogger(PlayerActivityInfoDao.class);
	
	private SqlSessionFactory sqlMapper = DBServer.getInstance().getSqlMapper();


    public int insert(PlayerActivityInfo playerActivityInfo) {
        SqlSession session = sqlMapper.openSession();
    	try{
    		playerActivityInfo.setTime(System.currentTimeMillis());
    		int rows = session.insert("playerActivityInfo.insert", playerActivityInfo);
    		session.commit();
    		return rows;
    	}finally{
			session.close();
		}
    }


    public PlayerActivityInfo selectById(int id) {
    	SqlSession session = sqlMapper.openSession();
    	try{
    		Object selectOne = session.selectOne("playerActivityInfo.selectById",id);
    		if(selectOne != null) {
    			return (PlayerActivityInfo)selectOne;
    		}
            return null;
    	}finally{
			session.close();
		}
    }
    
    public List<PlayerActivityInfo> selectByPlayerId(long playerId) {
    	SqlSession session = sqlMapper.openSession();
    	try{
    		List<PlayerActivityInfo> selectList = session.selectList("playerActivityInfo.selectByPlayerId", playerId);
    		if(CollectionUtil.isNotBlank(selectList)) {
    			return selectList;
    		}
            return null;
    	}finally{
			session.close();
		}
    }
    
    public List<PlayerActivityInfo> selectByActivityId(int activityId) {
    	SqlSession session = sqlMapper.openSession();
    	try{
    		List<PlayerActivityInfo> selectList = session.selectList("playerActivityInfo.selectByActivityId", activityId);
    		if(CollectionUtil.isNotBlank(selectList)) {
    			return selectList;
    		}
            return null;
    	}finally{
			session.close();
		}
    }
    
    public List<PlayerActivityInfo> selectByActivityIdAndAward(int activityId) {
    	SqlSession session = sqlMapper.openSession();
    	try{
    		List<PlayerActivityInfo> selectList = session.selectList("playerActivityInfo.selectByActivityIdAndAward", activityId);
    		if(CollectionUtil.isNotBlank(selectList)) {
    			return selectList;
    		}
            return null;
    	}finally{
			session.close();
		}
    }

    public int update(PlayerActivityInfo playerActivityInfo) {
        SqlSession session = sqlMapper.openSession();
    	try{
    		int rows = session.update("playerActivityInfo.update", playerActivityInfo);
    		session.commit();
            return rows;
    	}finally{
			session.close();
		}
    }
    
    public int delete(int activityId) {
        SqlSession session = sqlMapper.openSession();
    	try{
    		int rows = session.delete("playerActivityInfo.deleteByActivityId", activityId);
    		session.commit();
            return rows;
    	}finally{
			session.close();
		}
    }
}