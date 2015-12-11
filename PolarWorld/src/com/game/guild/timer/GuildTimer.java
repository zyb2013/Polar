package com.game.guild.timer;

import java.util.Date;
import java.util.Map;

import com.game.guild.manager.GuildWorldManager;
import com.game.guild.structs.Guild;
import com.game.languageres.manager.ResManager;
import com.game.prompt.structs.Notifys;
import com.game.timer.TimerEvent;

import java.util.*;

/**
 *
 * @author xiaozhuoming 公会时间循环类
 */
public class GuildTimer extends TimerEvent {

	//private Logger log = Logger.getLogger(MonsterAiTimer.class);
	public GuildTimer() {
		super(-1, 1000 * 30);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void action() {
		Date date = new Date();
		Date oldDate = new Date();
		List<Guild> deleteguilds = new ArrayList<Guild>();
		for (Map.Entry<Long, Guild> entry : GuildWorldManager.getInstance().entrySet()) {
			Guild guild = entry.getValue();
			if (guild != null) {
				oldDate.setTime(guild.getCalActiveValueTime());
				if (oldDate.getDay() != date.getDay()) {
					guild.setCalActiveValueTime(System.currentTimeMillis());
					if (guild.getGuildInfo().getActiveValue() < 1) {
						guild.getGuildInfo().setWarningValue((byte) (guild.getGuildInfo().getWarningValue() + 1));
					} else {
						guild.getGuildInfo().setWarningValue((byte) 0);
						/*xiaozhuoming: 如果有战盟成员登录解散警告值就清零
						guild.getGuildInfo().setWarningValue((byte) (guild.getGuildInfo().getWarningValue() - 1));
						if (guild.getGuildInfo().getWarningValue() < 0) {
							guild.getGuildInfo().setWarningValue((byte) 0);
						}*/
					}
					guild.calActiveValue(null);
					if (guild.getGuildInfo().getWarningValue() > 5 && guild.getGuildInfo().getWarningValue() < 7) {
						if (!GuildWorldManager.getInstance().checkKingCityGuild(guild)) {
							guild.sendAllMemberNotify(Notifys.NORMAL, ResManager.getInstance().getString("您所在的战盟每日活跃玩家不足，请多招募成员，并鼓励成员每日上线，否则您的战盟将会于（{1}）日内解散。"), String.valueOf(7 - guild.getGuildInfo().getWarningValue()));
						}else{
							guild.sendAllMemberNotify(Notifys.NORMAL, ResManager.getInstance().getString("您所在的战盟每日活跃玩家不足，请多招募成员，并鼓励成员每日上线，否则您的战盟将在下次圣盟争夺战失去王盟资格，并在第二天被强行解散。"));
						}
						GuildWorldManager.getInstance().saveGuild(guild);
					} else if (guild.getGuildInfo().getWarningValue() >= 7) {
						if (!GuildWorldManager.getInstance().checkKingCityGuild(guild)) {
							deleteguilds.add(guild);
						}else{
							guild.sendAllMemberNotify(Notifys.NORMAL, ResManager.getInstance().getString("您所在的战盟每日活跃玩家不足，请多招募成员，并鼓励成员每日上线，否则您的战盟将在下次圣盟争夺战失去王盟资格，并在第二天被强行解散。"));
							GuildWorldManager.getInstance().saveGuild(guild);
						}
					} else {
						GuildWorldManager.getInstance().saveGuild(guild);
					}
				}
			}
		}
		
		String title = "战盟解散";
		String content = "您所在的战盟由于连续7天内没有人登陆在线已经被强制解散。";
		for (int i = 0; i < deleteguilds.size(); i++) {
			Guild guild = deleteguilds.get(i);
			if (guild != null) {
//				guild.sendAllMemberNotify(Notifys.ERROR, ResManager.getInstance().getString("您所在的战盟：{1}，因为活跃玩家不足而自动解散了。"), guild.getGuildInfo().getGuildName());
				GuildWorldManager.getInstance().deleteGuildFromAll(guild, null, false, title, content);
			}
		}
	}
}
