package com.game.db.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

import com.game.db.DBServer;
import com.game.goldraffle.structs.GoldRaffleEventData;
import com.game.utils.LoggerProvider;
import com.game.utils.TimeUtil;

public class GoldRaffleEventDao {

	protected Logger log = Logger.getLogger(GoldRaffleEventDao.class);

	private SqlSessionFactory sqlMapper = DBServer.getInstance().getSqlMapper();

	public int insert(GoldRaffleEventData goldRaffleEventData) {
		SqlSession session = sqlMapper.openSession();
		try{
			long currentTimeMillis = System.currentTimeMillis();
			int rows = session.insert("gold_raffle_event.insert", goldRaffleEventData);
			session.commit();
			LoggerProvider.getDbconsuminglog().info("gold_raffle_event.insert "+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
			return rows;
		}catch (Exception e) {
			log.error(e);
			return -1;
		}finally{
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	public List<GoldRaffleEventData> select() {
		SqlSession session = sqlMapper.openSession();
		try{
			long currentTimeMillis = System.currentTimeMillis();
			List<GoldRaffleEventData> goldRaffleEventDataList = (List<GoldRaffleEventData>)session.selectList("gold_raffle_event.select");
			LoggerProvider.getDbconsuminglog().info("gold_raffle_event.select "+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
			return goldRaffleEventDataList;
		}catch (Exception e) {
			log.error(e);
			return null;
		}finally{
			session.close();
		}
	}

	public int delete(long eventId) {
		SqlSession session = sqlMapper.openSession();
		try{
			long currentTimeMillis = System.currentTimeMillis();
			int rows = session.delete("gold_raffle_event.delete", eventId);
			session.commit();
			LoggerProvider.getDbconsuminglog().info("gold_raffle_event.delete"+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
			return rows;
		}catch (Exception e) {
			log.error(e);
			return -1;
		}finally{
			session.close();
		}
	}
}