package com.game.data.dao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_horse_addition Dao
 */
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_prayBean;

public class Q_prayDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_prayBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_prayBean> list = (List<Q_prayBean>)session.selectList("q_pray.select");
            return list;
    	}finally{
			session.close();
		}
    }
}