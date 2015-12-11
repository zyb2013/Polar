package com.game.data.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_activation_attributeBean;

/**
 * @author luminghua.ko@gmail.com
 *
 * @date   2014年3月18日 上午11:41:23
 */

public class Q_activation_attributeDao extends BaseDao {
	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_activation_attributeBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_activation_attributeBean> list = (List<Q_activation_attributeBean>)session.selectList("q_activation_attribute.select");
            return list;
    	}finally{
			session.close();
		}
    }
}
