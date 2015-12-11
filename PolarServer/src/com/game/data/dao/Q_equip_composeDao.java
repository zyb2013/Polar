package com.game.data.dao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_item_strength Dao
 */
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_equip_composeBean;

public class Q_equip_composeDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_equip_composeBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_equip_composeBean> list = (List<Q_equip_composeBean>)session.selectList("q_equip_compose.select");
            return list;
    	}finally{
			session.close();
		}
    }
}