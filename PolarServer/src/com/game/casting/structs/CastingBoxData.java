package com.game.casting.structs;

import java.util.ArrayList;
import java.util.List;

import com.game.object.GameObject;

/**
 * 铸造工厂奖励仓库数据
 *
 * @author xiaozhuoming 
 */
public class CastingBoxData extends GameObject {
	
	private static final long serialVersionUID = -7979255268920438499L;

	//铸造工厂奖励仓库的物品
	private List<CastingGridData> castingGridDataList = new ArrayList<CastingGridData>();

	public List<CastingGridData> getCastingGridDataList() {
		return castingGridDataList;
	}

	public void setCastingGridDataList(List<CastingGridData> castingGridDataList) {
		this.castingGridDataList = castingGridDataList;
	}
	
}
