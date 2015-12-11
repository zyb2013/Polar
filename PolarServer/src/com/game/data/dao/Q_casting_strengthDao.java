package com.game.data.dao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_casting_strength Dao
 */
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_casting_strengthBean;

public class Q_casting_strengthDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_casting_strengthBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_casting_strengthBean> list = (List<Q_casting_strengthBean>)session.selectList("q_casting_strength.select");
            return list;
    	}finally{
			session.close();
		}
    }
}