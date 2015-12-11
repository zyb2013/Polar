package com.game.data.dao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_longyuan_exp Dao
 */
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_equip_compose_appendBean;

public class Q_equip_compose_appendDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_equip_compose_appendBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_equip_compose_appendBean> list = (List<Q_equip_compose_appendBean>)session.selectList("q_equip_compose_append.select");
            return list;
    	}finally{
			session.close();
		}
    }
}