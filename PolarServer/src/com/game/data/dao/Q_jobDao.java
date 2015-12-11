package com.game.data.dao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_job Dao
 */
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_jobBean;

public class Q_jobDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_jobBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_jobBean> list = (List<Q_jobBean>)session.selectList("q_job.select");
            return list;
    	}finally{
			session.close();
		}
    }
}