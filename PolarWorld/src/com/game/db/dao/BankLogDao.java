package com.game.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

import com.game.db.BaseDao;
import com.game.db.bean.BankLogBean;
import com.game.db.bean.StallsBean;
import com.game.utils.LoggerProvider;
import com.game.utils.TimeUtil;

public class BankLogDao extends BaseDao {

	private Logger log = Logger.getLogger(BankLogDao.class);
	private SqlSessionFactory sqlMapper = getSqlMapper();

    public int insert(BankLogBean bankLogBean) throws SQLException {
        SqlSession session = sqlMapper.openSession();
    	try{
    		long currentTimeMillis = System.currentTimeMillis();
    		int rows = session.insert("bank_log.insert", bankLogBean);
    		session.commit();
    		LoggerProvider.getDbconsuminglog().info("bank_log.insert "+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
    		return rows;
    	}catch (Exception e) {
    		log.error(e);
		}finally{
			session.close();
		}
    	return 0;
    }

    @SuppressWarnings("unchecked")
	public List<BankLogBean> select() throws SQLException {
        SqlSession session = sqlMapper.openSession();
        try{
        	long currentTimeMillis = System.currentTimeMillis();
        	List<BankLogBean> list = (List<BankLogBean>)session.selectList("bank_log.select");
        	LoggerProvider.getDbconsuminglog().info("bank_log.select "+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
            return list;
    	}catch (Exception e) {
    		log.error(e);
		}finally{
			session.close();
		}
    	return new ArrayList<BankLogBean>();
    }

    
	
	
	public static void main(String[] args) {
		BankLogDao stallsDao = new BankLogDao();
		
		try {
			System.err.println(stallsDao.select().size());
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}
//	
	
	
	

}







