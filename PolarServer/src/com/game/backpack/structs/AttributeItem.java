package com.game.backpack.structs;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.game.backpack.manager.BackpackManager;
import com.game.backpack.message.ResUseItemSuccessMessage;
import com.game.config.Config;
import com.game.data.bean.Q_itemBean;
import com.game.data.bean.Q_jobBean;
import com.game.data.manager.DataManager;
import com.game.dblog.LogService;
import com.game.manager.ManagerPool;
import com.game.player.log.RoleAttributeChangeLog;
import com.game.player.manager.PlayerManager;
import com.game.player.message.ResChangeOneAttributeMessage;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerAttributeType;
import com.game.structs.Reasons;
import com.game.utils.MessageUtil;

public class AttributeItem extends Item {

	private static final long serialVersionUID = -3295718130510094878L;

	private Logger logger = Logger.getLogger(AttributeItem.class);
	/**
	 * 使用属性物品
	 */
	@Override
	public void use(Player player, String... parameters) {
		//数量判断
		if (this.getNum() == 0)
			return;
		// 获得物品模型
		Q_itemBean model = ManagerPool.dataManager.q_itemContainer.getMap().get(this.getItemModelId());
		if (model == null)
			return;
		int num=1;
		
		if(parameters!=null && parameters.length>=1){
			num = Integer.parseInt(parameters[0]);
			if(num > getNum()){
				return;
			}
		}
		//q_attack	q_defence	q_crit	q_dodge	q_max_hp	q_max_mp	q_max_sp	q_attackspeed	q_speed	q_luck

		// 使用成功
		if (BackpackManager.getInstance().removeItem(player, this, num,Reasons.GOODUSE,Config.getId())){
			if(model.getQ_attack()>0) player.getOtherAttribute().setAttack(player.getOtherAttribute().getAttack() + model.getQ_attack() * num);
			if(model.getQ_defence()>0) player.getOtherAttribute().setDefense(player.getOtherAttribute().getDefense() + model.getQ_defence() * num);
			if(model.getQ_crit()>0) player.getOtherAttribute().setCrit(player.getOtherAttribute().getCrit() + model.getQ_crit() * num);
			if(model.getQ_dodge()>0) player.getOtherAttribute().setDodge(player.getOtherAttribute().getDodge() + model.getQ_dodge() * num);
			if(model.getQ_max_hp()>0) player.getOtherAttribute().setMaxHp(player.getOtherAttribute().getMaxHp() + model.getQ_max_hp() * num);
			if(model.getQ_max_mp()>0) player.getOtherAttribute().setMaxMp(player.getOtherAttribute().getMaxMp() + model.getQ_max_mp() * num);
			if(model.getQ_max_sp()>0) player.getOtherAttribute().setMaxSp(player.getOtherAttribute().getMaxSp() + model.getQ_max_sp() * num);
			if(model.getQ_attackspeed()>0) player.getOtherAttribute().setAttackSpeed(player.getOtherAttribute().getAttackSpeed() + model.getQ_attackspeed() * num);
			if(model.getQ_speed()>0) player.getOtherAttribute().setSpeed(player.getOtherAttribute().getSpeed() + model.getQ_speed() * num);
			if(model.getQ_luck()>0) player.getOtherAttribute().setLuck(player.getOtherAttribute().getLuck() + model.getQ_luck() * num);
			
			if (!StringUtils.isBlank(model.getQ_effict_type())) {
				// 19力量加成 20敏捷加成 21体力加成 22智力加成 23分配点增加 24全属性洗点格式示例[1,2,4]
				JSONArray typeArray = JSONArray.fromObject(model.getQ_effict_type());
				// 对应属性点
				JSONArray valueArray = JSONArray.fromObject(model.parseEffict_value());
				if (typeArray.size() != valueArray.size()) {
					logger.error("人物属性点道具配置错误： modelId[" + model.getQ_id() + "]," + typeArray.toString() + "," + valueArray.toString());
					return;
				}
				Q_jobBean q_jobBean = DataManager.getInstance().q_jobContainer.getMap().get((int) player.getJob());
				// 一级初始化属性【力量，体力，敏捷，智力，预留属性】
				RoleAttributeChangeLog log = new RoleAttributeChangeLog();
				log.setPreStrength(player.getAttibute_one_base()[0]);
				log.setPreVitality(player.getAttibute_one_base()[1]);
				log.setPreAgile(player.getAttibute_one_base()[2]);
				log.setPreIntelligence(player.getAttibute_one_base()[3]);
				log.setPreRemain(player.getAttibute_one_base()[4]);
				
				for (int i = 0; i < typeArray.size(); i++) {
					Integer type = typeArray.getInt(i);
					Integer value = valueArray.getInt(i) * num;
					switch (type) {
					case 19:
						if (value < 0) {
							// 最低不能低于初始属性值
							if (player.getAttibute_one_base()[0] + value < q_jobBean.getStrength()) {
								value = q_jobBean.getStrength() - player.getAttibute_one_base()[0];
							}
						}
						player.getAttibute_one_base()[0] += value;
						break;
					case 20:
						if (value < 0) {
							// 最低不能低于初始属性值
							if (player.getAttibute_one_base()[2] + value < q_jobBean.getAgile()) {
								value = q_jobBean.getAgile() - player.getAttibute_one_base()[2];
							}
						}
						player.getAttibute_one_base()[2] += value;
						break;
					case 21:
						if (value < 0) {
							// 最低不能低于初始属性值
							if (player.getAttibute_one_base()[1] + value < q_jobBean.getVitality()) {
								value = q_jobBean.getVitality() - player.getAttibute_one_base()[1];
							}
						}
						player.getAttibute_one_base()[1] += value;
						break;
					case 22:
						if (value < 0) {
							// 最低不能低于初始属性值
							if (player.getAttibute_one_base()[3] + value < q_jobBean.getIntelligence()) {
								value = q_jobBean.getIntelligence() - player.getAttibute_one_base()[3];
							}
						}
						player.getAttibute_one_base()[3] += value;
						break;
					case 23:
						if (value < 0) {
							logger.error("可分配点不可为负数");
							break;
						}
						player.getAttibute_one_base()[4] += value;
						break;
					case 24:
						int allot = 0;
						allot += (player.getAttibute_one_base()[0] - q_jobBean.getStrength());
						allot += (player.getAttibute_one_base()[1] - q_jobBean.getVitality());
						allot += (player.getAttibute_one_base()[2] - q_jobBean.getAgile());
						allot += (player.getAttibute_one_base()[3] - q_jobBean.getIntelligence());
						player.getAttibute_one_base()[4] += allot;
						player.getAttibute_one_base()[0] = q_jobBean.getStrength();
						player.getAttibute_one_base()[1] = q_jobBean.getVitality();
						player.getAttibute_one_base()[2] = q_jobBean.getAgile();
						player.getAttibute_one_base()[3] = q_jobBean.getIntelligence();
						break;
					default:
						continue;
					}
					// 如果是减少点数的，则把减掉的加到可分配点那里；type=23，value不能<0
					if (value < 0 && type != 23 && type != 24) {
						player.getAttibute_one_base()[4] += Math.abs(value);
					}
				}
				
				//! xuliang 使用物品成功消息
				ResUseItemSuccessMessage successMsg = new ResUseItemSuccessMessage();
				successMsg.setItemId(this.getItemModelId());
				MessageUtil.tell_player_message(player, successMsg);
				
				ManagerPool.playerAttributeManager.countPlayerAttribute(player, PlayerAttributeType.BASE);
				log.setAfStrength(player.getAttibute_one_base()[0]);
				log.setAfVitality(player.getAttibute_one_base()[1]);
				log.setAfAgile(player.getAttibute_one_base()[2]);
				log.setAfIntelligence(player.getAttibute_one_base()[3]);
				log.setAfRemain(player.getAttibute_one_base()[4]);
				log.setType(1);
				LogService.getInstance().execute(log);
				PlayerManager.getInstance().savePlayer(player);
				// ResChangeOneAttributeMessage endmsg = new ResChangeOneAttributeMessage();
				// endmsg.setEndValue(1);
				// endmsg.setStrength(player.getAttibute_one()[0]);
				// endmsg.setVitality(player.getAttibute_one()[1]);
				// endmsg.setAgile(player.getAttibute_one()[2]);
				// endmsg.setIntelligence(player.getAttibute_one()[3]);
				// endmsg.setRestPlusPoint(player.getAttibute_one()[4]);
				// MessageUtil.tell_player_message(player, endmsg);
			}
			ManagerPool.playerAttributeManager.countPlayerAttribute(player, PlayerAttributeType.OTHER);
		}
	}


	@Override
	public void unuse(Player player, String... parameters) {
		
	}

}