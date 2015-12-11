package com.game.data.dao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_collect Dao
 */
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_pt_reward_consumeBean;

public class Q_ptRewardConsumeDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_pt_reward_consumeBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_pt_reward_consumeBean> list = (List<Q_pt_reward_consumeBean>)session.selectList("q_pt_reward_consume.select");
            return list;
    	}finally{
			session.close();
		}
    }
}