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
import com.game.data.bean.Q_item_add_attributeBean;

public class Q_item_add_attributeDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_item_add_attributeBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_item_add_attributeBean> list = (List<Q_item_add_attributeBean>)session.selectList("q_item_add_attribute.select");
            return list;
    	}finally{
			session.close();
		}
    }
}