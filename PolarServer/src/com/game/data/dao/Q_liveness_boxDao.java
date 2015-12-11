package com.game.data.dao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_activities_drop Dao
 */
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_liveness_boxBean;

/**
 * 活跃度模块宝箱dao
 * @author hongxiao.z
 * @date   2013-12-26  下午6:45:08
 */
public class Q_liveness_boxDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_liveness_boxBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_liveness_boxBean> list = (List<Q_liveness_boxBean>)session.selectList("q_liveness_box.select");
            return list;
    	}finally{
			session.close();
		}
    }
}