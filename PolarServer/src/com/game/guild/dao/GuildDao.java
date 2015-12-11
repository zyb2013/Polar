package com.game.guild.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

import com.game.db.DBServer;
import com.game.db.bean.GuildBean;
import com.game.utils.LoggerProvider;
import com.game.utils.TimeUtil;

public class GuildDao {

	protected Logger log = Logger.getLogger(GuildDao.class);

	private SqlSessionFactory sqlMapper = DBServer.getInstance().getSqlMapper();

	 public GuildBean selectById(long id) {
		 SqlSession session = sqlMapper.openSession();
		 try{
			 long currentTimeMillis = System.currentTimeMillis();
			 GuildBean guildBean = (GuildBean)session.selectOne("guild.selectById", id);
			 LoggerProvider.getDbconsuminglog().info("guild.selectById "+TimeUtil.getDurationToNow(currentTimeMillis)+"ms");
			 return guildBean;
		 }finally{
			 session.close();
		 }
	 }

}