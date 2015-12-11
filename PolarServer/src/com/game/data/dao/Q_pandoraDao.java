package com.game.data.dao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_gift Dao
 */
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_PandoraBean;

public class Q_pandoraDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_PandoraBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
			List<Q_PandoraBean> list = (List<Q_PandoraBean>) session.selectList("q_pandora.select");
            return list;
    	}finally{
			session.close();
		}
    }
}