package com.game.data.dao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_question Dao
 */
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_questionBean;

public class Q_questionDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_questionBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_questionBean> list = (List<Q_questionBean>)session.selectList("q_question.select");
            return list;
    	}finally{
			session.close();
		}
    }
}