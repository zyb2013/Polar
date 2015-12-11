package com.game.data.dao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_casting_costBean Dao
 */
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_casting_costBean;

public class Q_casting_costDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_casting_costBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_casting_costBean> list = (List<Q_casting_costBean>)session.selectList("q_casting_cost.select");
            return list;
    	}finally{
			session.close();
		}
    }
}