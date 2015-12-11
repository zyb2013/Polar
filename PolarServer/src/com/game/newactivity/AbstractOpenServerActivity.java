package com.game.newactivity;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import net.sf.json.JSONObject;

import com.game.newactivity.message.ResRemoveActivityMessage;
import com.game.newactivity.model.DetailActivityInfo.Row;
import com.game.newactivity.model.PlayerActivityInfo;
import com.game.newactivity.model.SimpleRankInfo;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.timer.SchedulerManager;
import com.game.timer.SchedulerParser;
import com.game.utils.CollectionUtil;
import com.game.utils.MessageUtil;
import com.game.utils.StringUtil;
import com.game.utils.Symbol;

/**
 * @author luminghua
 *
 * @date   2014年2月27日 下午4:33:20
 * 
 * 七日排行榜开服活动类型
 */
public abstract class AbstractOpenServerActivity extends AbstractActivity {

	protected Date closePanel;//关闭活动面板时间
	
	@Override
	public void initActivity() {
		super.initActivity();
		String q_expend = this.getActivityBean().getQ_expend();
		if(StringUtil.isNotBlank(q_expend)) {
			try {
				JSONObject json = JSONObject.fromObject(q_expend);
				String string = json.getString("closePanel");
				if(StringUtil.isNotBlank(string)) {
					closePanel = SchedulerParser.parse2Date(string);
					if(closePanel != null && closePanel.getTime() > System.currentTimeMillis()) {
						//清除活动
						SchedulerManager.getInstance().timerschedule(this.getClass().getSimpleName(), new TimerTask() {

							@Override
							public void run() {
								ResRemoveActivityMessage msg = new ResRemoveActivityMessage();
								msg.setActivityId(activityBean.getQ_id());
								MessageUtil.tell_world_message(msg);
							}
							
						}, closePanel);
					}
				}
			} catch (ParseException e) {
				_logger.error(this.getClass().getName() +"解析关闭活动面板时间出错："+this.getActivityBean().getQ_id(), e);
			}
		}
	}
	
	public boolean isCanVisible(Player player) {
		 boolean betweenStartAndEnd = isBetweenStartAndEnd();
		 long now = System.currentTimeMillis();
		 return betweenStartAndEnd?betweenStartAndEnd:(closePanel == null?true:((now < closePanel.getTime()) && (startDate==null?true:now>startDate.getTime())));
	}
	
	protected boolean payMent;//是否在结算中
	
	@Override
	public void stop() {
		_logger.info(this.getClass().getName() +" execute end() method.");
		payMent = true;
		ActivityRankManager.getInstance().playerAskRank(null, this.getActivityBean().getQ_id());
	}
	
	public void payMent(List<SimpleRankInfo> list) {
		if(payMent) {
			payMent = false;
			if(CollectionUtil.isBlank(list)) {
				return;
			}
			list.remove(list.size()-1);//将最后一个myself去掉
			List<Row> rows = this.detailInfo.getRows();
			for(int i=0; i<rows.size(); i++) {
				Row row = rows.get(i);
				String[] split = row.getCond().split(Symbol.SHUXIAN_REG);
				String[] split2 = split[0].split("-");
				int limit = Integer.parseInt(split[1]);
				if(split2.length == 1) {
					int rank = Integer.parseInt(split2[0]);
					if(rank > 0) {
						if(list.size()>=rank) {
							SimpleRankInfo simpleRankInfo = list.get(rank-1);
							int limit2 = Integer.parseInt(simpleRankInfo.getData());
							if(limit2 >= limit) {
								addPlayerAward(simpleRankInfo,i);
							}
						}
					}
				}else {
					int min = Integer.parseInt(split2[0]);
					int max = Integer.parseInt(split2[1]);
					for(int k=min; k<=max; k++) {
						if(list.size() < k) {
							break;
						}
						SimpleRankInfo simpleRankInfo = list.get(k-1);
						int limit2 = Integer.parseInt(simpleRankInfo.getData());
						if(limit2 >= limit) {
							addPlayerAward(simpleRankInfo,i);
						}else {
							break;
						}
					}
				}
			}
			//把前20的存起来
			ActivityRankManager.getInstance().storeRankInfo(this.getActivityBean().getQ_id(), list);
		}
	}
	
	protected void addPlayerAward(SimpleRankInfo rankInfo,int order) {
		PlayerActivityInfo playerInfo = new PlayerActivityInfo();
		playerInfo.setPlayerId(rankInfo.getPlayerId());
		playerInfo.setJob(rankInfo.getJob());
		playerInfo.setActivityId(this.getActivityBean().getQ_id());
		playerInfo.setInfo(rankInfo.getData());
		playerInfo.addAward(order);
		PlayerActivityInfoManager.getInstance().addPlayerActivityInfo(playerInfo);
		PlayerActivityInfoManager.getInstance().insert(playerInfo);
		Player player = PlayerManager.getInstance().getOnLinePlayer(rankInfo.getPlayerId());
		if(player != null) {
			sendAwardCountMessage(player,playerInfo);
			sendDetailActivityInfo(player);
		}
	}
}
