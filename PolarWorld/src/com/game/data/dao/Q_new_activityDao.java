package com.game.data.dao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_activities Dao
 */
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_newActivityBean;

public class Q_new_activityDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_newActivityBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_newActivityBean> list = (List<Q_newActivityBean>)session.selectList("q_new_activity.select");
            return list;
    	}finally{
			session.close();
		}
    }
	
	public int update(Q_newActivityBean bean) {
        SqlSession session = sqlMapper.openSession();
    	try{
    		int rows = session.update("q_new_activity.update", bean);
    		session.commit();
            return rows;
    	}finally{
			session.close();
		}
    }
}