package com.game.timer;

import org.apache.log4j.Logger;

/**
 * @author luminghua
 *
 * @date   2014年2月21日 下午9:06:29
 */
public abstract class AbstractSchedulerTask implements Schedulerable {

	protected SchedulerBean _bean;
	protected Logger _logger;
	
	@Override
	public void initBean(SchedulerBean bean) {
		this._bean = bean;
		this._logger = Logger.getLogger(getClass());
	}
	
	public void trigger() {
		_logger.info(this.getClass().getName() +" execute default trigger() method.");
	}

}
