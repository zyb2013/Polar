package com.game.utils;

import com.game.server.thread.ServerThread;
import com.game.timer.ITimerEvent;

/** 
 * @author  
 * 
 * @version 1.0.0
 * 
 * @since 2011-8-31
 * 
 * 计时事件工具
 */
public class TimerEventUtil {

	public static void addTimerEvent(ITimerEvent event){
		((ServerThread)Thread.currentThread()).addTimerEvent(event);
	}
	
	public static void removeTimerEvent(ITimerEvent event){
		((ServerThread)Thread.currentThread()).removeTimerEvent(event);
	}
}
