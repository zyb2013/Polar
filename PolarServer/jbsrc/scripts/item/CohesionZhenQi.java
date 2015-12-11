package scripts.item;

import java.util.HashMap;

import com.game.backpack.script.IItemScript;
import com.game.backpack.structs.Item;
import com.game.config.Config;
import com.game.count.structs.CountTypes;
import com.game.dazuo.message.ResCohesionZhenQiInadequateMessage;
import com.game.dazuo.message.ResUsedsuccessfullyMessage;
//import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.player.structs.AttributeChangeReason;
import com.game.player.structs.Player;
//import com.game.prompt.structs.Notifys;
import com.game.structs.Reasons;
import com.game.utils.CommonConfig;
import com.game.utils.MessageUtil;


/**
 * 真气凝丹 9224
 * @author zhangrong
 *
 */

public class CohesionZhenQi implements IItemScript{
	private int itemmodelid= 9224;
	@Override
	public int getId() {
		return 1009224;
	}
	
	private HashMap<Integer , Integer > nummap=new HashMap<Integer, Integer>();
	
	public CohesionZhenQi(){
		nummap.put(1, 1);
		nummap.put(2, 2);
		nummap.put(3, 3);
		nummap.put(4, 3);
		nummap.put(5, 4);
		nummap.put(6, 4);
		nummap.put(7, 5);
		nummap.put(8, 5);
		nummap.put(9, 6);
		nummap.put(10, 6);
		nummap.put(11, 7);
		nummap.put(12, 7);
		nummap.put(13, 8);
		nummap.put(14, 8);
		nummap.put(15, 9);
		nummap.put(16, 9);
		nummap.put(17, 10);
		nummap.put(18, 10);
		nummap.put(19, 11);
		nummap.put(20, 11);
	}
	
	
	private int MAX=20;
	//玩家ID
	private String PLAYER_ID= "PLAYER_ID";
	//玩家名字
	private String PLAYER_NAME= "PLAYER_NAME";
	//真气数量
	private String ZHENQI_NUM= "ZHENQI_NUM";

	@Override
	public boolean use(Item item, Player player, String... parameters) {
//		String name = item.acqItemModel().getQ_name();
		if (item.getItemModelId() == itemmodelid ) {
			long id = Config.getId();
			if (item.getParameters().containsKey(PLAYER_ID)) {
				int usenum = 0 ;
				String strid = item.getParameters().get(PLAYER_ID);
				String playername = item.getParameters().get(PLAYER_NAME);
				int zhenqinum  = Integer.valueOf(item.getParameters().get(ZHENQI_NUM));
				long playerid= Long.valueOf(strid);
				int max = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.ZHENGQI_MAX.getValue()).getQ_int_value();
				if (player.getZhenqi() + zhenqinum > max ) {
					/*xiaozhuoming: 暂时没有用到
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您的真气值已经达到上限"));
					*/
					return false;
				}
				int maxnum = (int) ManagerPool.countManager.getCount(player, CountTypes.USE_NINGDAN_MAX, null);
				if (maxnum == 0) {	//每天使用计数
					ManagerPool.countManager.addCount(player, CountTypes.USE_NINGDAN_MAX, null,1, 0,0);
				}
				
				if (maxnum >= MAX) {
					/*xiaozhuoming: 暂时没有用到
					MessageUtil.notify_player(player, Notifys.MOUSEPOS, ResManager.getInstance().getString("今日使用{1}已经达到{2}次上限"),name,MAX+"");
					*/
					return false;
				}
				
				boolean isothers = false;	//是否使用他人凝丹
				
				if (playerid == player.getId()) {
					
				}else {//使用他人凝丹
					usenum = (int) ManagerPool.countManager.getCount(player, CountTypes.USE_NINGDAN, null);
					if (usenum == 0) {	//每天使用计数
						ManagerPool.countManager.addCount(player, CountTypes.USE_NINGDAN, null,1, 0,0);
					}
					usenum = usenum +1;
					if (!nummap.containsKey(usenum)) {
						/*xiaozhuoming: 暂时没有用到
						MessageUtil.notify_player(player, Notifys.MOUSEPOS, ResManager.getInstance().getString("没有配置第{1}次使用数据"),usenum+"");
						*/
						return  false;
					}
					
					int itemnnum = nummap.get(usenum);
					isothers = true;//是否他人的凝丹
					if(!ManagerPool.backpackManager.removeItem(player, 18174,itemnnum, Reasons.NINGZDAN_ZHENQI, id)){
						//数量不足，发消息给前端弹出面板
						int itemnum = ManagerPool.backpackManager.getItemNum(player, 18174);
						ResCohesionZhenQiInadequateMessage msg = new ResCohesionZhenQiInadequateMessage();
						msg.setLacknum(itemnnum - itemnum);	//缺少的数量
						msg.setPlayername(playername);
						msg.setUsenum(itemnnum);
						MessageUtil.tell_player_message(player, msg);
						return false;
					}
				}
	
				
				if(ManagerPool.backpackManager.removeItem(player, item, 1, Reasons.NINGZDAN_ZHENQI, id)){
					ResUsedsuccessfullyMessage msg= new ResUsedsuccessfullyMessage();
					if (isothers) {//他人的凝丹
						ManagerPool.countManager.addCount(player, CountTypes.USE_NINGDAN, null,1);//每天使用他人凝丹+1
						msg.setType( 1);
						ManagerPool.playerManager.addZhenqi(player, zhenqinum, AttributeChangeReason.NINGDAN_OTHERS);
						/*xiaozhuoming: 暂时没有用到
						String itemstrname = ManagerPool.backpackManager.getName(18174);
						int itemnnum = nummap.get(usenum);
						MessageUtil.notify_player(player, Notifys.CHAT_ROLE, ResManager.getInstance().getString("使用他人的{1}获得真气{2}，消耗{3}个{4}"),name,zhenqinum+"",itemnnum+"",itemstrname);
						*/
					}else {//自己的凝丹，原因不一样，
						ManagerPool.playerManager.addZhenqi(player, zhenqinum, AttributeChangeReason.NINGDAN_SELF);
						/*xiaozhuoming: 暂时没有用到
						MessageUtil.notify_player(player, Notifys.CHAT_ROLE, ResManager.getInstance().getString("使用自己的{1}获得真气{2}"),name,zhenqinum+"");
						*/
					}
					ManagerPool.countManager.addCount(player, CountTypes.USE_NINGDAN_MAX, null,1);//凝丹使用次数+1
					MessageUtil.tell_player_message(player, msg);
					return true;
				}	
			}
		}
		return false;
	}

	
	
	
}
