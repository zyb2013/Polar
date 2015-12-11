package com.game.dblog.task;

import com.game.dblog.bean.BaseLogBean;

public class FileLogTask implements Runnable {
	BaseLogBean log;
	public FileLogTask(BaseLogBean log) {
		this.log=log;
	}

	@Override
	public void run() {
		log.logToFile();

	}
}
