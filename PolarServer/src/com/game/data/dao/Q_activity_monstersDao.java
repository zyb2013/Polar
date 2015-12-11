package com.game.data.dao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * q_activity_monsters Dao
 */
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_activity_monstersBean;;

public class Q_activity_monstersDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_activity_monstersBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_activity_monstersBean> list = (List<Q_activity_monstersBean>)session.selectList("q_activity_monsters.select");
            return list;
    	}finally{
			session.close();
		}
    }
	

}