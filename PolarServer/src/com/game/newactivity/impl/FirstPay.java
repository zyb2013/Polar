package com.game.newactivity.impl;

import com.game.newactivity.AbstractActivity;
import com.game.newactivity.PlayerActivityInfoManager;
import com.game.newactivity.message.ResRemoveActivityMessage;
import com.game.newactivity.model.PlayerActivityInfo;
import com.game.player.structs.Player;
import com.game.utils.CollectionUtil;
import com.game.utils.MessageUtil;

/**
 * @author luminghua
 *
 * @date   2014年2月24日 下午3:22:36
 * 
 */
public class FirstPay extends AbstractActivity {

	//首充
	public void trigger(Player player,Object...objects) {
		if(CollectionUtil.isBlank(objects)) {
			return;
		}
		int gold = (Integer)objects[0];
		PlayerActivityInfo info = new PlayerActivityInfo();
		info.setPlayerId(player.getId());
		info.setJob(player.getJob());
		info.setActivityId(this.getActivityBean().getQ_id());
		info.setInfo(String.valueOf(gold));
		info.addAward(0);
		PlayerActivityInfoManager.getInstance().addPlayerActivityInfo(info);
		PlayerActivityInfoManager.getInstance().insert(info);
		this.sendDetailActivityInfo(player);
		sendAwardCountMessage(player,info);
	}
	

	public boolean isCanVisible(Player player) {
		if(!this.isBetweenStartAndEnd()) {
			return false;
		}
		if(player == null) {
			return false;
		}
		if(player.getRechargeGold() == 0) {
			return true;
		}
		PlayerActivityInfo playerActivityInfo = PlayerActivityInfoManager.getInstance().getPlayerActivityInfo(player.getId(), this.getActivityBean().getQ_id());
		if(playerActivityInfo!=null && playerActivityInfo.getCanAward() != 0) {
			return true;
		}
		return false;
	}
	
	public GetAwardResult getAward(Player player,int order) {
		GetAwardResult award = super.getAward(player, order);
		if(award.result == GetAwardResult.ResultType.succeed) {
			ResRemoveActivityMessage msg = new ResRemoveActivityMessage();
			msg.setActivityId(this.getActivityBean().getQ_id());
			MessageUtil.tell_player_message(player, msg);
		}
		return award;
	}
}
