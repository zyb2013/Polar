package com.game.data.dao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_evencut Dao
 */
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_lost_skillBean;

public class Q_lostSkillDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_lost_skillBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_lost_skillBean> list = (List<Q_lost_skillBean>)session.selectList("q_lostskill.select");
            return list;
    	}finally{
			session.close();
		}
    }
}