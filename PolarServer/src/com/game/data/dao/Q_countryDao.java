package com.game.data.dao;

/** 
 * @author ExcelUtil Auto Maker
 * 
 * @version 1.0.0
 * 
 * Q_country Dao
 */
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.game.data.BaseDao;
import com.game.data.bean.Q_countryBean;

public class Q_countryDao extends BaseDao {

	private SqlSessionFactory sqlMapper = getSqlMapper();

    @SuppressWarnings("unchecked")
	public List<Q_countryBean> select() {
        SqlSession session = sqlMapper.openSession();
        try{
        	List<Q_countryBean> list = (List<Q_countryBean>)session.selectList("q_country.select");
            return list;
    	}finally{
			session.close();
		}
    }
	
//	public void insert() {
//		
//		 
//		 for (int i = 10001; i <= 10400; i++) {
//			 SqlSession session = sqlMapper.openSession();
//			 Q_countryBean q = new Q_countryBean();
//			 q.setQ_lv(i);
//			 q.setQ_rewards("100017_10;100030_10;");
//			 int rows = session.insert("q_country.insert", q);
//			 session.commit();
//			 session.close();
//		}
//		 for (int i = 20001; i <= 20400; i++) {
//			 SqlSession session = sqlMapper.openSession();
//			 Q_countryBean q = new Q_countryBean();
//			 q.setQ_lv(i);
//			 q.setQ_rewards("100017_10;100030_10;");
//			 int rows = session.insert("q_country.insert", q);
//			 session.commit();
//			 session.close();
//		}
//		
//		
//	}
	public static void main(String[] args) {
		Q_countryDao countryDao = new Q_countryDao();
		List<Q_countryBean> li = countryDao.select();
		System.out.println(li);
	}
	

}