package com.game.data.dao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_hold_reward Dao
 */
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_hold_rewardBean;

public class Q_hold_rewardDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_hold_rewardBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_hold_rewardBean> list = (List<Q_hold_rewardBean>)session.selectList("q_hold_reward.select");
            return list;
    	}finally{
			session.close();
		}
    }
	

}