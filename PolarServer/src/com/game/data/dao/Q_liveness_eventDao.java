package com.game.data.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_liveness_eventBean;

/**
 * 活跃度事件原型dao
 * @author hongxiao.z
 * @date   2013-12-26  下午6:44:49
 */
public class Q_liveness_eventDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_liveness_eventBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_liveness_eventBean> list = (List<Q_liveness_eventBean>)session.selectList("q_liveness_event.select");
            return list;
    	}finally{
			session.close();
		}
    }
}