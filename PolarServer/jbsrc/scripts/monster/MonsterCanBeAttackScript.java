package scripts.monster;

import com.game.fight.structs.Fighter;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.monster.script.IMonsterCanBeAttackScript;
import com.game.monster.structs.Monster;
import com.game.pet.struts.Pet;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.script.structs.ScriptEnum;
import com.game.utils.MessageUtil;

/**怪物是否可被攻击
 * 
 * @author zhangrong
 *
 */
public class MonsterCanBeAttackScript implements IMonsterCanBeAttackScript {

	//阿斗
	private int ADOU = 13501;
	
	@Override
	public int getId() {
		return ScriptEnum.MONSTER_BEATTACK;
	}

	@Override
	public boolean canbeattack(Fighter fighter, Monster monster) {
		/*panic god 暂时屏蔽
		if(monster.getModelId()==ADOU){
			if(fighter instanceof Player || fighter instanceof Pet){
				return false;
			}else if(fighter instanceof Monster){
				return true;
			}
		}*/
		if(fighter instanceof Monster){
			return false;
		}
		
		//---------------------------战盟领地旗帜------------（和AttackCheckScript脚本一致）------
		if (ManagerPool.guildFlagManager.getflagmonidlist().contains(monster.getModelId())) {//检测是否对应怪物
			Player player = null;
			if (fighter instanceof Player) {
				player = (Player)fighter;

			}else if(fighter instanceof Pet){
				Pet attackPet = (Pet) fighter;
				long attackplayerid = attackPet.getOwnerId();
				player = ManagerPool.playerManager.getOnLinePlayer(attackplayerid);
			}
			if (player != null) {
				if (player.getLevel() < 30) {
					//MessageUtil.notify_player(player, Notifys.ERROR, "您的等级低于30级，不能攻击盟旗。");
					return false;
				}
				
				
				if (monster.getParameters().containsKey("guildid")) {
					long guildid = (Long)(monster.getParameters().get("guildid"));
					if (player.getGuildId() == guildid) {
						//MessageUtil.notify_player(player, Notifys.ERROR, "不能攻击本盟盟旗。");
						return false;
					}
				}
				
				if(ManagerPool.guildFlagManager.getFlagwarstatus() != 1 ){
					//MessageUtil.notify_player(player, Notifys.ERROR, "现在不是领地争夺战时间，不能攻击盟旗。");
					return false;
				}
				
				if (player.getLine() != 1) {
					//MessageUtil.notify_player(player, Notifys.ERROR, "请到1线去攻击盟旗真身。");
					return false;
				}
			}
		}
		
		
		if (fighter instanceof Player) {	//相同阵营不能攻击
			Player player = (Player)fighter;
			if (player.getGroupmark() > 0 && player.getGroupmark() == monster.getGroupmark() ) {
				MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("相同阵营不能攻击."));
				return false;
			}
		}
		


		
		return true;
	}

}
