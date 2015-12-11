package scripts.item;

import com.game.backpack.manager.BackpackManager;
import com.game.backpack.script.IItemScript;
import com.game.backpack.structs.Item;
import com.game.config.Config;
import com.game.data.bean.Q_realm_basicBean;
import com.game.data.bean.Q_realm_breakBean;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.realm.manager.RealmManager;
import com.game.realm.message.ResRealmInfoToClientMessage;
import com.game.realm.structs.Realm;
import com.game.structs.Reasons;
import com.game.utils.MessageUtil;

/**
 * 境界祝福丹
 * @author Administrator
 *
 */
public class RealmBlessDan implements IItemScript {
	
	@Override
	public int getId() {
		return 1009247;
	}

	@Override
	public boolean use(Item item, Player player, String... parameters) {
		int levelLimit = 3;
		Realm realm = player.getRealm();
		if (realm.getRealmlevel() < levelLimit) {
			Q_realm_basicBean basicBean = ManagerPool.dataManager.q_realm_basicContainer.getMap().get(levelLimit);
			if (basicBean != null) {
				/*xiaozhuoming: 暂时没有用到
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("{1}以上才可使用该物品"), basicBean.getQ_name());
				*/
			}
			return false;
		}
		
		Q_realm_basicBean basicBean = ManagerPool.dataManager.q_realm_basicContainer.getMap().get(realm.getRealmlevel());
		if (basicBean == null) {
			return false;
		}
		
		if (!RealmManager.getInstance().checkallowBreak(player, basicBean)) {
			/*xiaozhuoming: 暂时没有用到
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您的属性值未满,无法使用该道具"));
			*/
			return false;
		}
		
		Q_realm_breakBean breakdata = ManagerPool.dataManager.q_realm_breakContainer.getMap().get(realm.getRealmlevel());
		if (breakdata == null) {
			return false;
		}
		
		if (realm.getBlessingnum() >= breakdata.getQ_fail_blessing_limit()) {
			/*xiaozhuoming: 暂时没有用到
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您的祝福值满了,无法使用该物品"));
			*/
			return false;
		}
		
		if(BackpackManager.getInstance().removeItem(player, item, 1, Reasons.GOODUSE, Config.getId())){
			int biess = 100;
			if (biess + realm.getBlessingnum() > breakdata.getQ_fail_blessing_limit()) {
				realm.setBlessingnum(breakdata.getQ_fail_blessing_limit());
			}else {
				realm.setBlessingnum(biess + realm.getBlessingnum());
			}
			ResRealmInfoToClientMessage ret = new ResRealmInfoToClientMessage();
			ret.setBealmInfo(realm.getRealmInfo());
			MessageUtil.tell_player_message(player, ret);
			return true;
		}else{
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("扣除物品失败"));
			return false;
		}
	}

}
