package com.game.timer;

/**
 * @author luminghua
 *
 * @date   2014年2月21日 下午9:00:53
 * 
 * 继承这个接口的子类，所有需要定时执行的方法都必须有且仅有一个String参数
 */
interface Schedulerable {

	public void initBean(SchedulerBean bean);
}
