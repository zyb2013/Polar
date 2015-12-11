package com.game.data.dao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_casting_reward Dao
 */
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_casting_rewardBean;

public class Q_casting_rewardDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_casting_rewardBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_casting_rewardBean> list = (List<Q_casting_rewardBean>)session.selectList("q_casting_reward.select");
            return list;
    	}finally{
			session.close();
		}
    }
}