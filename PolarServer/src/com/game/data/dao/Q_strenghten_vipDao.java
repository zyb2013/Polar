package com.game.data.dao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_arrow_bow Dao
 */
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_strenghten_vipBean;

public class Q_strenghten_vipDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_strenghten_vipBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_strenghten_vipBean> list = (List<Q_strenghten_vipBean>)session.selectList("q_strenghten_vip.select");
            return list;
    	}finally{
			session.close();
		}
    }
}