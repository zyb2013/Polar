package com.game.newactivity.impl;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.game.country.structs.CountryData;
import com.game.newactivity.AbstractOpenServerActivity;
import com.game.newactivity.PlayerActivityInfoManager;
import com.game.newactivity.model.PlayerActivityInfo;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.utils.CollectionUtil;
import com.game.utils.ServerParamUtil;
import com.game.utils.StringUtil;

/**
 * @author luminghua
 *
 * @date   2014年2月24日 下午3:21:37
 * 
 * 攻城战
 */
public class Siegecraft extends AbstractOpenServerActivity {

	@Override
	public void stop() {
		_logger.info(this.getClass().getName() +" execute end() method.");
		String dataString = ServerParamUtil.getImportantParamMap().get(ServerParamUtil.COUNTRYDATA );
		if(StringUtil.isNotBlank(dataString)) {
			CountryData data = JSON.parseObject(dataString,CountryData.class);
			long kingId = data.getKingId();
			if(kingId != 0) {
				addPlayerInfo(kingId,0);
			}
			List<Long> kingGuildList = data.getKingGuildList();
			if(CollectionUtil.isNotBlank(kingGuildList)) {
				for(Long playerId : kingGuildList) {
					if(playerId == kingId) {
						continue;
					}
					addPlayerInfo(playerId,1);
				}
			}
			List<Long> playerList = data.getPlayerList();
			if(CollectionUtil.isNotBlank(playerList)) {
				for(Long playerId : playerList) {
					addPlayerInfo(playerId,2);
				}
			}
		}
	}
	
	private void addPlayerInfo(long playerId,int order) {
		PlayerActivityInfo info = new PlayerActivityInfo();
		info.setPlayerId(playerId);
		info.setActivityId(this.getActivityBean().getQ_id());
		info.addAward(order);
		PlayerActivityInfoManager.getInstance().addPlayerActivityInfo(info);
		PlayerActivityInfoManager.getInstance().insert(info);
		Player player = PlayerManager.getInstance().getOnLinePlayer(playerId);
		if(player !=null) {
			sendAwardCountMessage(player,info);
			sendDetailActivityInfo(player);
		}
	}
}
