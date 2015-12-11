package scripts.player;

import com.game.config.Config;
import com.game.manager.ManagerPool;
import com.game.player.script.IPlayerLevelUPScript;
import com.game.player.structs.Player;
//import com.game.prompt.structs.Notifys;
import com.game.script.structs.ScriptEnum;
import com.game.server.impl.WServer;
import com.game.structs.Reasons;
//import com.game.utils.MessageUtil;
import com.game.utils.TimeUtil;

public class PlayerLevelUPScript implements IPlayerLevelUPScript {

	@Override
	public int getId() {
		return ScriptEnum.LEVELUP;
	}

	@Override
	public void onLevelUP(Player player) {
		if(WServer.getInstance().getServerWeb().equals("hgpupugame")) {
			if(TimeUtil.GetSeriesDay() <= 20130328){//韩国测试期间给钻石，28号后就不给了，，以防忘记
				int yb = 0;
				if (player.getLevel() == 20) {
					yb = 500;
				}else if (player.getLevel() == 30 || player.getLevel() == 40 || player.getLevel() == 50) {
					yb = 1500;
				}
				if (yb > 0) {
					ManagerPool.backpackManager.addGold(player, yb, Reasons.def27,Config.getId() );
					/*xiaozhuoming: 暂时没有用到
					MessageUtil.notify_player(player, Notifys.CHAT_ROLE, "축하합니다. {1}레벨이 되어 {1}원보를 획득했습니다.", player.getLevel()+"",yb+"");
					*/
				}
			}
		}
	}

}
