package com.game.task.struts;

/**
 * @author luminghua.ko@gmail.com
 *
 * @date   2014年3月13日 下午5:02:41
 * 
 * 主线任务大类型(1、对话 2、杀怪 3、杀怪掉落物品 4、采集 5、NPC间道具传递 6、小电影 7、强化装备 8、其他多种类型 9、走到某点播放剧情10、杀一定等级范围的怪11、收集物品12穿戴强化装备13穿戴普通装备)
 */
public class TaskType {

	public static final int CHAT1 = 1;
	public static final int KILL_MONSTER2 = 2;
	public static final int DROP_ITEM3 = 3;
	public static final int GATHER_ITEM4 = 4;
	public static final int TRANSFER_ITEM5 = 5;
	public static final int STORY6 = 6;
	public static final int INTENSIFY7 = 7;//只要强化装备就行
	public static final int OTHERS8 = 8;
	public static final int POINT_STORY9 = 9;
	public static final int KILL_LEVEL_SCOPE_MONSTER10 = 10;
	public static final int COLLECT_ITEM11 = 11;
	public static final int WEAR_INTENSIFY_EQUIP12 = 12;//强化了装备还要穿上
	public static final int WEAR_NORMAL_EQUIP13 = 13;
}
