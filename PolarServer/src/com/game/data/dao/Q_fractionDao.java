package com.game.data.dao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_fractionBean Dao
 */
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_fractionBean;

public class Q_fractionDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_fractionBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_fractionBean> list = (List<Q_fractionBean>)session.selectList("q_fraction.select");
            return list;
    	}finally{
			session.close();
		}
    }
}