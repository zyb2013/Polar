package com.game.drop.timer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.game.csys.manager.CsysManger;
import com.game.drop.script.IDropClearScript;
import com.game.drop.structs.MapDropInfo;
import com.game.manager.ManagerPool;
import com.game.map.bean.DropGoodsInfo;
import com.game.map.message.ResRoundGoodsDisappearMessage;
import com.game.map.structs.Area;
import com.game.map.structs.Map;
import com.game.npc.struts.NPC;
import com.game.script.structs.ScriptEnum;
import com.game.timer.TimerEvent;
import com.game.utils.MessageUtil;

public class DropClearTimer extends TimerEvent {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(DropClearTimer.class);
	private static final int CLEAR_TIME = 60 * 1000;// 保留60秒
	private int serverId;
	private int lineId;
	private int mapId;

	public DropClearTimer(int serverId, int lineId, int mapId) {
		super(-1, 1000);
		this.serverId = serverId;
		this.lineId = lineId;
		this.mapId = mapId;
	}

	@Override
	public void action() {
		//获取地图
		Map map = ManagerPool.mapManager.getMap(serverId, lineId, mapId);
		List<MapDropInfo> mapDropInfos = new ArrayList<MapDropInfo>();
		Iterator<Area> areaIter = map.getAreas().values().iterator();
		while (areaIter.hasNext()) {
			Area area = (Area) areaIter.next();
			
			if(map.getMapModelid()==CsysManger.CSYS_MAPID){
				for (NPC npc : area.getNpcs().values()) {
					if(npc.getParameters().get("retime")!=null){
						if(System.currentTimeMillis()>(Long)npc.getParameters().get("retime")){
							ManagerPool.npcManager.showNpc(npc);
							npc.getParameters().remove("retime");
						}
					}
				}
			}
			
			if (area.getDropGoods().isEmpty()) {
				continue;
			}
			
			
			Iterator<MapDropInfo> dropGoodsIterator = area.getDropGoods().values().iterator();
			while (dropGoodsIterator.hasNext()) {
				MapDropInfo next = dropGoodsIterator.next();
				if (next.getCleartimepoint() == 0) {
					boolean isRemove = false;
					DropGoodsInfo dropinfo = next.getDropinfo();
					long time = System.currentTimeMillis() - dropinfo.getDropTime();
					if(map.getZoneModelId() >= 7001 && map.getZoneModelId() <= 7008 && 
					  (dropinfo.getItemModelId() == 720100 || dropinfo.getItemModelId() == 720101 || dropinfo.getItemModelId() == 720102)) {
						if(time >= 5 * CLEAR_TIME) {
							isRemove = true;
						}
					} else if (time >= CLEAR_TIME) {
						isRemove = true;
					}
					
					if(isRemove) {
						dropGoodsIterator.remove();
//						Position position = MapUtils.buildPosition((short) dropinfo.getX(), (short) dropinfo.getY());
						ResRoundGoodsDisappearMessage msg = new ResRoundGoodsDisappearMessage();
						msg.getGoodsIds().add(dropinfo.getDropGoodsId());
						MessageUtil.tell_round_message(next, msg);
					}
				} else {
					if (System.currentTimeMillis() > next.getCleartimepoint()) {
						DropGoodsInfo dropinfo = next.getDropinfo();
						dropGoodsIterator.remove();
//						Position position = MapUtils.buildPosition((short) dropinfo.getX(), (short) dropinfo.getY());
						ResRoundGoodsDisappearMessage msg = new ResRoundGoodsDisappearMessage();
						msg.getGoodsIds().add(dropinfo.getDropGoodsId());
						MessageUtil.tell_round_message(next, msg);
						mapDropInfos.add(next);
					}
				}
			}
		}
		for (int i = 0; i < mapDropInfos.size(); i++) {
			MapDropInfo mapDropInfo = mapDropInfos.get(i);
			if (mapDropInfo != null) {
				IDropClearScript script = (IDropClearScript) ManagerPool.scriptManager.getScript(ScriptEnum.DROP_CLEAR);
				if (script != null) {
					try {
						script.dropClear(mapDropInfo);
					} catch (Exception e) {
						logger.error(e, e);
					}
				} else {
					logger.error("物品清除脚本不存在！");
				}
			}
		}
	}
}
