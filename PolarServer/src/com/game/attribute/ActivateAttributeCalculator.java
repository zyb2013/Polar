/**
 * 
 */
package com.game.attribute;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.game.data.bean.Q_activation_attributeBean;
import com.game.json.JSONserializable;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerAttribute;
import com.game.player.structs.PlayerAttributeCalculator;
import com.game.player.structs.PlayerAttributeType;
import com.game.utils.CommonString;
import com.game.utils.StringUtil;

/**
 * @author luminghua
 *
 * @date   2013年12月27日 下午12:12:27
 */
public class ActivateAttributeCalculator implements PlayerAttributeCalculator {

	@Override
	public int getType() {
		return PlayerAttributeType.ACTIVATION;
	}

	@Override
	public PlayerAttribute getPlayerAttribute(Player player) {
		PlayerAttribute att = new PlayerAttribute();
		Q_activation_attributeBean beanByCount = ActivateAttributeManager.getInstance().getBeanByCount(player);
		if(beanByCount == null) {
			return att;
		}
		String attributeAddition = beanByCount.getQ_attribute();
		if (!StringUtils.isBlank(attributeAddition)) {
			@SuppressWarnings("unchecked")
			HashMap<String, Integer> attHashMap = (HashMap<String, Integer>) JSONserializable.toObject(StringUtil.formatToJson(attributeAddition), HashMap.class);
			if (attHashMap != null && !attHashMap.isEmpty()) {
				for (Map.Entry<String, Integer> entry : attHashMap.entrySet()) {
					String attStr = entry.getKey();
					Integer attInt = entry.getValue();
					if (attStr.equalsIgnoreCase(CommonString.ATTACK)) {
						att.setPhysic_attacklower(attInt);
						att.setPhysic_attackupper(attInt);
						att.setMagic_attacklower(attInt);
						att.setMagic_attackupper(attInt);
					} else if (attStr.equalsIgnoreCase(CommonString.ATTACKPERCENT)) {
						att.setPhysic_attackPercent(attInt);
						att.setMagic_attackPercent(attInt);
					} else if (attStr.equalsIgnoreCase(CommonString.DEFENSE)) {
						att.setDefense(attInt);
					} else if (attStr.equalsIgnoreCase(CommonString.DEFENSEPERCENT)) {
						att.setDefensePercent(attInt);
					} else if (attStr.equalsIgnoreCase(CommonString.CRIT)) {
						att.setCrit(attInt);
					} else if (attStr.equalsIgnoreCase(CommonString.CRITPERCENT)) {
						att.setCritPercent(attInt);
					} else if (attStr.equalsIgnoreCase(CommonString.DODGE)) {
						att.setDodge(attInt);
					} else if (attStr.equalsIgnoreCase(CommonString.DODGEPERCENT)) {
						att.setDodgePercent(attInt);
					} else if (attStr.equalsIgnoreCase(CommonString.MAXHP)) {
						att.setMaxHp(attInt);
					} else if (attStr.equalsIgnoreCase(CommonString.MAXHPPERCENT)) {
						att.setMaxHpPercent(attInt);
					} else if (attStr.equalsIgnoreCase(CommonString.MAXMP)) {
						att.setMaxMp(attInt);
					} else if (attStr.equalsIgnoreCase(CommonString.MAXMPPERCENT)) {
						att.setMaxMpPercent(attInt);
					} else if (attStr.equalsIgnoreCase(CommonString.MAXSP)) {
						att.setMaxSp(attInt);
					} else if (attStr.equalsIgnoreCase(CommonString.MAXSPPERCENT)) {
						att.setMaxSpPercent(attInt);
					} else if (attStr.equalsIgnoreCase(CommonString.ATTACKSPEED)) {
						att.setAttackSpeed(attInt);
					} else if (attStr.equalsIgnoreCase(CommonString.ATTACKSPEEDPERCENT)) {
						att.setAttackSpeedPercent(attInt);
					} else if (attStr.equalsIgnoreCase(CommonString.SPEED)) {
						att.setSpeed(attInt);
					} else if (attStr.equalsIgnoreCase(CommonString.SPEEDPERCENT)) {
						att.setSpeedPercent(attInt);
					}
				}
			}
		}
		return att;
	}

}
