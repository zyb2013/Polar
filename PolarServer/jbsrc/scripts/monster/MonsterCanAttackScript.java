package scripts.monster;

import com.game.fight.structs.Fighter;
import com.game.languageres.manager.ResManager;
import com.game.monster.script.IMonsterCanAttackScript;
import com.game.monster.structs.Monster;
import com.game.pet.struts.Pet;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.script.structs.ScriptEnum;
import com.game.utils.MessageUtil;
/**怪物是否可攻击目标
 * 
 * @author zhangrong
 *
 */
public class MonsterCanAttackScript implements IMonsterCanAttackScript {

	@Override
	public int getId() {
		return ScriptEnum.MONSTER_ATTACK;
	}

	@Override
	public boolean canattack(Fighter fighter, Monster monster) {
		
		if(fighter instanceof Pet){
			return false;
		}
		
		if (fighter instanceof Player) {	//相同阵营不能攻击
			Player player = (Player)fighter;
			if (player.getGroupmark() > 0  && player.getGroupmark() == monster.getGroupmark() ) {
				MessageUtil.notify_player(player, Notifys.SUCCESS, ResManager.getInstance().getString("相同阵营不能攻击"));
				return false;
			}
		}
		
		
		return true;
	}

}
