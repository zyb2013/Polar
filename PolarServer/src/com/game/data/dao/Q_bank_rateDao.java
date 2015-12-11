package com.game.data.dao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_bank_rate Dao
 */
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_bank_rateBean;

public class Q_bank_rateDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_bank_rateBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_bank_rateBean> list = (List<Q_bank_rateBean>)session.selectList("q_bank_rate.select");
            return list;
    	}finally{
			session.close();
		}
    }
	

}