package com.game.question.timer;

import com.game.question.manager.QuestionManager;
import com.game.timer.TimerEvent;

public class QuestionTimer extends TimerEvent {
	
	public QuestionTimer() {
		super(-1, 1 * 1000);
	}

	@Override
	public void action() {
		if (QuestionManager.getInstance().canStart()) {
			QuestionManager.getInstance().doWhenStart();
		}
		
		if (QuestionManager.getInstance().canSendQuestion()) {
			QuestionManager.getInstance().sendQuestion();
		}
		
		if (QuestionManager.getInstance().canEnd()) {
			QuestionManager.getInstance().doWhenEnd();
		}
	}

}
