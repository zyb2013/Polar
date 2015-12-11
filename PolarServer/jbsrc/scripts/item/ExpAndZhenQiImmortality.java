package scripts.item;

import java.util.ArrayList;
//import java.util.HashMap;

import com.game.backpack.script.IItemScript;
import com.game.backpack.structs.Item;
import com.game.chat.bean.GoodsInfoRes;
import com.game.config.Config;
import com.game.count.structs.CountTypes;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.player.structs.AttributeChangeReason;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.structs.Reasons;
import com.game.utils.CommonConfig;
import com.game.utils.MessageUtil;
import com.game.utils.ParseUtil;
//import com.game.utils.TimeUtil;
import com.game.vip.manager.VipManager;
import com.game.vip.struts.GuideType;
/**经验丹和真气丹根据人物等级给数值
 * 
 * @author zhangrong
 * 海量经验丹真气丹，每天2次，其他只能每天1次
 */

public class ExpAndZhenQiImmortality  implements IItemScript {
	@Override
	public int getId() {
		return 1016017;	
	}
	
	private int[][]  exp_a = {
			{60,10348160},
			{61,12779979},
			{62,15739552},
			{63,19324673},
			{64,23644305},
			{65,28816498},
			{66,34964017},
			{67,42206564},
			{68,50647876},
			{69,60355386},
			{70,64882040},
			{71,69710471},
			{72,74855768},
			{73,80333019},
			{74,86157163},
			{75,92342805},
			{76,98904005},
			{77,105854016},
			{78,113204989},
			{79,120967617},
			{80,129150720},
			{81,137760769},
			{82,146801319},
			{83,156272372},
			{84,166169622},
			{85,176483599},
			{86,187198674},
			{87,198291929},
			{88,209731848},
			{89,221476832},
			{90,222861062},
			{91,223830023},
			{92,224338727},
			{93,224338727},
			{94,223777880},
			{95,222600102},
			{96,220745101},
			{97,218148100},
			{98,214739536},
			{99,210444745},
			{100,272727272},
			{101,272727272},
			{102,272727272},
			{103,272727272},
			{104,272727272},
			{105,272727272},
			{106,272727272},
			{107,272727272},
			{108,272727272},
			{109,272727272},
			{110,272727272},
			{111,272727272},
			{112,272727272},
			{113,272727272},
			{114,272727272},
			{115,272727272},
			{116,272727272},
			{117,272727272},
			{118,272727272},
			{119,272727272},
			{120,272727272}};
	
	
	private int[][]  exp_b={
			{60,22765954},
			{61,28115954},
			{62,34627016},
			{63,42514281},
			{64,52017473},
			{65,63396296},
			{66,76920839},
			{67,92854441},
			{68,111425329},
			{69,132781851},
			{70,142740489},
			{71,153363037},
			{72,164682690},
			{73,176732643},
			{74,189545759},
			{75,203154173},
			{76,217588811},
			{77,232878836},
			{78,249050977},
			{79,266128759},
			{80,284131586},
			{81,303073692},
			{82,322962903},
			{83,343799219},
			{84,365573170},
			{85,388263918},
			{86,411837084},
			{87,436242245},
			{88,461410067},
			{89,487249031},
			{90,490294337},
			{91,492426051},
			{92,493545201},
			{93,493545201},
			{94,492311338},
			{95,489720226},
			{96,485639224},
			{97,479925821},
			{98,472426980},
			{99,462978441},
			{100,600000000},
			{101,600000000},
			{102,600000000},
			{103,600000000},
			{104,600000000},
			{105,600000000},
			{106,600000000},
			{107,600000000},
			{108,600000000},
			{109,600000000},
			{110,600000000},
			{111,600000000},
			{112,600000000},
			{113,600000000},
			{114,600000000},
			{115,600000000},
			{116,600000000},
			{117,600000000},
			{118,600000000},
			{119,600000000},
			{120,600000000}};
	

	
	@Override
	public boolean use(Item item, Player player, String... parameters) {
		String name = item.acqItemModel().getQ_name();
		int exp = 0;
		String strtype="";
		Reasons reasons = null;
		int zhenqi = 0;
		int guideType = 0;
		CountTypes countTypes = null;
		if (item.getItemModelId() == 16017) {	//经验丹A  霸者经验丹
			exp = getexpnum(player ,exp_a);
			strtype = "DANYAO_ADDEXP";
			reasons = Reasons.def2;
			countTypes = CountTypes.DANYAO_ADDEXP;
			guideType = GuideType.EXP_16017.getValue();
		}else if (item.getItemModelId() == 16018) {//经验丹B 王威经验丹
			exp = getexpnum(player ,exp_b);
			strtype = "DANYAO_ADDEXP";
			reasons = Reasons.def3;
			guideType = GuideType.EXP_16018.getValue();
			countTypes = CountTypes.DANYAO_ADDEXP;
		}else if (item.getItemModelId() == 16023) {//经验丹C 海量经验丹
			exp = (int)(getexpnum(player ,exp_a)*1.2);	// 霸者经验丹*1.2
			strtype = "DANYAO_ADDEXP";
			reasons = Reasons.def8;
			guideType = GuideType.EXP_16023.getValue();
			countTypes = CountTypes.DANYAO_ADDEXP;
		}else if (item.getItemModelId() == 16019) {//真气丹A 霸者真气丹
			strtype = "DANYAO_ADDZHENQI";
			reasons = Reasons.def4;
			zhenqi = 100000;
			guideType = GuideType.ZHENQI_16019.getValue();
			countTypes = CountTypes.DANYAO_ADDZHENQI;
		}else if (item.getItemModelId() == 16020) {//真气丹B 王威真气丹
			strtype = "DANYAO_ADDZHENQI";
			reasons = Reasons.def5;
			zhenqi = 200000;
			guideType = GuideType.ZHENQI_16020.getValue();
			countTypes = CountTypes.DANYAO_ADDZHENQI;
		}else if (item.getItemModelId() == 16024) {//真气丹C 海量真气丹
			strtype = "DANYAO_ADDZHENQI";
			reasons = Reasons.def9;
			zhenqi = 120000;
			guideType = GuideType.ZHENQI_16024.getValue();
			countTypes = CountTypes.DANYAO_ADDZHENQI;
		}
		
		if (exp == 0 && zhenqi == 0) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("道具使用出错了"));
			return false;
		}
		
		if (player.getLevel() < 60 && strtype.equals("DANYAO_ADDEXP")) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，60级以上玩家才能使用{1}。"),name);
			return false;
		}
		
		if(strtype.equals("DANYAO_ADDZHENQI") ){
			if (player.getLevel() < 40 ) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，40级以上玩家才能使用{1}。"),name);
				return false;
			}
			
			int max = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.ZHENGQI_MAX.getValue()).getQ_int_value();
			if (player.getZhenqi() + zhenqi >  max) {
				/* xuliang
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您在使用后真气将会溢出，请使用掉一些真气后再使用该道具（真气上限为{1}）"),max+"");
				*/
				return false;
			}
		}

		
		long num = ManagerPool.countManager.getCount(player, countTypes,null);

		if (((item.getItemModelId() == 16024 || item.getItemModelId() == 16023) && num >= 2) || ((item.getItemModelId() != 16024 && item.getItemModelId() != 16023) && num >= 1)) {
			if (strtype.equals("DANYAO_ADDZHENQI")) {
				/*xiaozhuoming: 暂时没有用到
				MessageUtil.notify_player(player, Notifys.ERROR, "霸者,海量,王威真气丹三个道具，您每天只能选择使用其中一样，并且这个道具每天只能使用一个");
				MessageUtil.notify_player(player, Notifys.MOUSEPOS, ResManager.getInstance().getString("海量真气丹您每天只能使用2个,明天再用吧"));
				*/
			}else {
				/*xiaozhuoming: 暂时没有用到
				MessageUtil.notify_player(player, Notifys.ERROR, "霸者,海量,王威经验丹三个道具，您每天只能选择使用其中一样，并且这个道具每天只能使用一个");
				MessageUtil.notify_player(player, Notifys.MOUSEPOS, ResManager.getInstance().getString("海量经验丹您每天只能使用2个,明天再用吧"));
				*/
			}
			return false;
		}
		
		
		
		if( ManagerPool.backpackManager.removeItem(player, item, 1, reasons,Config.getId()) ){
			
			ManagerPool.countManager.addCount(player,  countTypes, null, 1,1,0);//记录次数
			
			if (strtype.equals("DANYAO_ADDZHENQI")) {
				ManagerPool.playerManager.addZhenqi(player, zhenqi,AttributeChangeReason.ZHENQI_BAZHE);
				MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("恭喜！使用{1}获得真气{2}。"),name,zhenqi+"");
				
				ParseUtil parseUtil = new ParseUtil();
				parseUtil.setValue(String.format(ResManager.getInstance().getString("恭喜玩家【%s】使用【%s】获取海量真气!{@}"), player.getName(), name), new ParseUtil.VipParm(VipManager
						.getInstance().getVIPLevel(player), guideType));
				MessageUtil.notify_All_player(Notifys.CHAT_BULL, parseUtil.toString(),new ArrayList<GoodsInfoRes>(),guideType);
				
			}else if (strtype.equals("DANYAO_ADDEXP")) {
				
				ManagerPool.playerManager.addExp(player, exp,AttributeChangeReason.EXP_BAZHE);
				MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM, ResManager.getInstance().getString("恭喜！使用{1}获得经验{2}，您的等级越高获得的经验越多。"),name,exp+"");
				
				ParseUtil parseUtil = new ParseUtil();
				parseUtil.setValue(String.format(ResManager.getInstance().getString("恭喜玩家【%s】使用【%s】获取海量经验!{@}"), player.getName(), name), new ParseUtil.VipParm(VipManager
						.getInstance().getVIPLevel(player), guideType));
				MessageUtil.notify_All_player(Notifys.CHAT_BULL, parseUtil.toString(),new ArrayList<GoodsInfoRes>(),guideType);
			}
			return true;
		}
		return false;
	}
	
	
	
	public int getexpnum( Player player,int[][] exptab){
		int lv = player.getLevel();
		if (lv > 120) {
			lv = 120;
		}
		
		for (int[] tab : exptab) {
			if (tab[0] == lv) {
				return tab[1];
			}
		}
		return 0;
	}
	
	
	
	
}
