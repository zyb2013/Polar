package com.game.newactivity.impl;

import java.util.LinkedList;

import com.game.newactivity.AbstractActivity;
import com.game.newactivity.PlayerActivityInfoManager;
import com.game.newactivity.model.DetailActivityInfo.Row;
import com.game.newactivity.model.PlayerActivityInfo;
import com.game.player.structs.Player;
import com.game.utils.CollectionUtil;

/**
 * @author luminghua
 *
 * @date   2014年2月24日 下午3:22:47
 */
public class Pay4Gift extends AbstractActivity {

		//充值送好礼
		public void trigger(Player player,Object...objects) {
			if(CollectionUtil.isBlank(objects)) {
				return;
			}
			//单笔充值
			int gold = (Integer)objects[0];
			LinkedList<Row> rows = (LinkedList<Row>) detailInfo.getRows();
			Row last = rows.getLast();
			if(gold < Integer.parseInt(last.getCond())){
				return;
			}
			PlayerActivityInfo playerActivityInfo = PlayerActivityInfoManager.getInstance().getPlayerActivityInfo(player.getId(), this.getActivityBean().getQ_id());
			//从小到大
			for(int i=rows.size()-1; i>=0; i--) {
				if(gold >= Integer.parseInt(rows.get(i).getCond())){
					if(playerActivityInfo == null) {
						playerActivityInfo = new PlayerActivityInfo();
						playerActivityInfo.setPlayerId(player.getId());
						playerActivityInfo.setJob(player.getJob());
						playerActivityInfo.setActivityId(this.getActivityBean().getQ_id());
						playerActivityInfo.addAward(i);
						playerActivityInfo.setInfo(String.valueOf(gold));
						PlayerActivityInfoManager.getInstance().addPlayerActivityInfo(playerActivityInfo);
						PlayerActivityInfoManager.getInstance().insert(playerActivityInfo);
					}else {
						playerActivityInfo.setInfo(String.valueOf(gold));
						playerActivityInfo.addAward(i);
					}
				}else {
					break;
				}
			}
			if(playerActivityInfo != null) {
				PlayerActivityInfoManager.getInstance().update(playerActivityInfo);
				sendDetailActivityInfo(player);
				sendAwardCountMessage(player,playerActivityInfo);
			}
		}
}
