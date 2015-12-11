package com.game.data.dao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_csys Dao
 */
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_csysBean;

public class Q_csysDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_csysBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_csysBean> list = (List<Q_csysBean>)session.selectList("q_csys.select");
            return list;
    	}finally{
			session.close();
		}
    }
	

}