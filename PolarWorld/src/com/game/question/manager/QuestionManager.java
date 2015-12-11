package com.game.question.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.game.data.bean.Q_questionBean;
import com.game.data.manager.DataManager;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.question.message.ResSendQuestionInfoMessage;
import com.game.question.message.ResSendQuestionJoinMessage;
import com.game.question.message.ResSendQuestionResultMessage;
import com.game.question.message.ResSendQuestionScoreMessage;
import com.game.utils.MessageUtil;

public class QuestionManager {
	
	static QuestionManager me = null;
	private static Object obj = new Object();
	
	private HashMap<Long, Integer> player2score = new HashMap<Long, Integer>(); // 参加答题的玩家
	private ArrayList<Player> players = new ArrayList<Player>();
	private TreeMap<Integer, Q_questionBean> questions = new TreeMap<Integer, Q_questionBean>();
	private boolean isAnswering = false; // 是否正在答题
	private Logger log = Logger.getLogger(this.getClass());
	private long nextSendQuestionTime;
	private final long questionInteval = 10 * 1000;
	private final short questionNum = 10;
	private final Integer rightScore = new Integer(5);
	
	private QuestionManager() {}
	
	static public QuestionManager getInstance() {
		synchronized (obj) {
			if (me == null) {
				me = new QuestionManager();
			}
		}
		return me;
	}

	public void chooseMsg(Player player, byte choice) {
		if (!canChoose(player)) {
			return ;
		}
		
		ResSendQuestionResultMessage send = new ResSendQuestionResultMessage();
		if (isRight(choice)) {
			send.setIsRight((byte) 0);
			if (player2score.containsKey(Long.valueOf(player.getId()))) {
				player2score.put(Long.valueOf(player.getId()), rightScore);
			}
			else {
				player2score.put(Long.valueOf(player.getId()), rightScore + player2score.get(Long.valueOf(player.getId())));
			}
		}
		else {
			send.setIsRight((byte) 1);
		}
		
		MessageUtil.tell_player_message(player, send);
	}

	private boolean isRight(byte choice) {
		Iterator<Q_questionBean> it = questions.values().iterator();
		if (!it.hasNext()) return false;
		Q_questionBean questionBean = it.next();
		return questionBean.getQ_right_answer_id() == choice;
	}

	private boolean canChoose(Player player) {
		if (!player2score.containsKey(Long.valueOf(player.getId()))) return false;
		if (!players.contains(player)) return false;
		
		if (!isAnswering) return false;
		return true;
	}

	public void join(Player player) {
		if (!player2score.containsKey(Long.valueOf(player.getId())))
			player2score.put(Long.valueOf(player.getId()), Integer.valueOf(0));
		if (!players.contains(player))
			players.add(player);
	}

	public boolean canStart() {
		if (isAnswering) return false;
		// TODO 这里看策划咋个定时间
		return true;
	}

	public void doWhenStart() {
		isAnswering = true;
		
		short times = 0;
		int size = DataManager.getInstance().q_questionContainer.getMap().size();
		while (questions.size() < questionNum) {
			if (++times > questionNum * 10) break; // 防止死循环
			Q_questionBean questionBean = DataManager.getInstance().q_questionContainer.getMap().get(Integer.valueOf((int)(Math.random() * size + 1)));
			if (questionBean != null) questions.put(Integer.valueOf(questionBean.getQ_id()), questionBean);
		}
		
		// TODO 这里需要等待一段时间
		nextSendQuestionTime = System.currentTimeMillis() + questionInteval;
		
		ResSendQuestionJoinMessage msg = new ResSendQuestionJoinMessage();
		MessageUtil.tell_world_message(msg);
	}

	public boolean canSendQuestion() {
		if (!isAnswering) return false;
		if (questions.size() < 1) return false;
		return System.currentTimeMillis() > nextSendQuestionTime;
	}

	public void sendQuestion() {
		// TODO
		nextSendQuestionTime += questionInteval;
		
		Iterator<Q_questionBean> it = questions.values().iterator();
		Q_questionBean questionBean = it.next();
		
		ResSendQuestionInfoMessage msg = new ResSendQuestionInfoMessage();
		msg.setIndex((short) (questionNum - questions.size()));
		msg.setQ_id(questionBean.getQ_id());
		MessageUtil.tell_player_message(players, msg);
		
		// TODO to remove
		log.fatal("question" + questionBean.getQ_id() + ": " + questionBean.getQ_question_content());
		log.fatal("A: " + questionBean.getQ_answer_content_1());
		log.fatal("B: " + questionBean.getQ_answer_content_2());
		log.fatal("C: " + questionBean.getQ_answer_content_3());
		log.fatal("D: " + questionBean.getQ_answer_content_4());
		log.fatal("right answer: " + questionBean.getQ_right_answer_id());
		
		questions.remove(Integer.valueOf(questionBean.getQ_id()));
	}

	public boolean canEnd() {
		if (!isAnswering) return false;
		if (questions.size() > 0) return false;
		// TODO 这里也需要策划定
		if (nextSendQuestionTime > System.currentTimeMillis()) return false;
		return true;
	}

	public void doWhenEnd() {
		Iterator<Long> it = player2score.keySet().iterator();
		while (it.hasNext()) {
			Player player = PlayerManager.getInstance().getPlayer(it.next());
			if (player == null) continue;
			ResSendQuestionScoreMessage msg = new ResSendQuestionScoreMessage();
			msg.setScore(player2score.get(Long.valueOf(player.getId())));
			MessageUtil.tell_player_message(player, msg);
		}
		clear();
	}

	private void clear() {
		log.fatal("答题结束！");
		player2score.clear();
		questions.clear();
		isAnswering = false;
		nextSendQuestionTime = 0;
		players.clear();
	}
}
