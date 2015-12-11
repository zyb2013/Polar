package com.game.player.scheduler;

import java.util.Calendar;

import org.apache.log4j.Logger;

import com.game.drop.manager.DropManager;
import com.game.timer.SchedulerEvent;

/**
 * 需要定点执行的任务
 * 
 * @author  
 * 
 */
public class Midnight1ClockEvent extends SchedulerEvent {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(Midnight1ClockEvent.class);

	@Override
	public void action() {
		logger.info("开始执行定点任务");
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		DropManager.clearItemCount(hour);
		logger.info("结束执行定点任务");
	}
	
}