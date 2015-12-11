package com.game.data.dao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_collect Dao
 */
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_pt_awardBean;

public class Q_ptAwardDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_pt_awardBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_pt_awardBean> list = (List<Q_pt_awardBean>)session.selectList("q_pt_award.select");
            return list;
    	}finally{
			session.close();
		}
    }
}