package com.game.data.dao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_lotteryBean Dao
 */
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_lotteryBean;

public class Q_lotteryDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_lotteryBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_lotteryBean> list = (List<Q_lotteryBean>)session.selectList("q_lottery.select");
            return list;
    	}finally{
			session.close();
		}
    }
}