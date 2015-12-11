package com.game.db.dao;


import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

import com.game.db.DBServer;
import com.game.goldraffle.structs.GoldRaffleData;
import com.game.utils.LoggerProvider;
import com.game.utils.TimeUtil;

public class GoldRaffleDao {

	protected Logger log = Logger.getLogger(GoldRaffleDao.class);

	private SqlSessionFactory sqlMapper = DBServer.getInstance().getSqlMapper();

	public int save(GoldRaffleData goldRaffleData) {
		SqlSession session = sqlMapper.openSession();
		try{
			long currentTimeMillis = System.currentTimeMillis();
			int rows = session.insert("gold_raffle.save", goldRaffleData);
			session.commit();
			LoggerProvider.getDbconsuminglog().info("gold_raffle.save"+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
			return rows;
		}catch (Exception e) {
			log.error(e);
			return 0;
		}finally{
			session.close();
		}
	}

	public GoldRaffleData load(long playerId) {
		SqlSession session = sqlMapper.openSession();
		try{
			long currentTimeMillis = System.currentTimeMillis();
			GoldRaffleData goldRaffleData = (GoldRaffleData)session.selectOne("gold_raffle.load", playerId);
			LoggerProvider.getDbconsuminglog().info("gold_raffle.select "+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
			return goldRaffleData;
		}catch (Exception e) {
			log.error(e);
			return null;
		}finally{
			session.close();
		}
	}
	
}