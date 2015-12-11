package com.game.shop.bean;

import com.game.message.Bean;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 *          商品动态信息
 */
public class ShopItemInfo extends Bean {

	// 商品ID
	private int sellId;

	// 物品模型ID
	private int modelId;

	// 排序号
	private int index;

	// 货币类型 1游戏币，2钻石，4绑定钻石，6钻石与绑定钻石可购买，7三种货币均可购买
	private String moneyType;

	// 金币价格
	private int coin;

	// 钻石价格
	private int gold;

	// 绑定钻石价格
	private int bindgold;

	// 原金币价格
	private int originalCoin;

	// 原钻石价格
	private int originalGold;

	// 原绑定钻石价格
	private int originalBindGold;

	// 热销标识 0无热销，1热销中，2折扣，3热销+折扣
	private byte hot;

	// 物品强化等级定义
	private int strengthen;

	// 物品附加属性定义（类型|百分比的分子;类型|百分比的分子
	private String append;

	// 过期时间 点（格式 yyyy-mm-dd hh:mm:ss）
	private String lostTime;

	// 购买后离自动失效前的存在时间（单位：秒）
	private int duration;

	// 购买时是否立刻绑定（0不是，1是立刻绑定）
	private byte buybind;

	// 追几
	private int q_extra_attribute;

	// 是否幸运
	private int q_lucky;

	// 适应等级推荐（最小
	private int q_suitable_level_min;


	// 适应等级推荐（最大）
	private int q_suitable_level_max;
	
	

	// 是否卓越
	private int q_zhuoyue;

	//显示所需人物等级
	private int q_show_level;
	
	
	//灵魂宝石
	private int q_souldiamond;
	
	//灵魂宝石显示原价
	private int q_show_souldiamond;
	
	//已经购买的数量
	private int buy_count;
	
	//限购数量
	private int limit_count;
	
	public int getBuy_count() {
		return buy_count;
	}

	public void setBuy_count(int buy_count) {
		this.buy_count = buy_count;
	}

	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf) {
		// 商品ID
		writeInt(buf, this.sellId);
		// 物品模型ID
		writeInt(buf, this.modelId);
		// 排序号
		writeInt(buf, this.index);
		// 货币类型 1游戏币，2钻石，4绑定钻石，6钻石与绑定钻石可购买，7三种货币均可购买
		writeString(buf, this.moneyType);
		// 金币价格
		writeInt(buf, this.coin);
		// 钻石价格
		writeInt(buf, this.gold);
		// 绑定钻石价格
		writeInt(buf, this.bindgold);
		// 原金币价格
		writeInt(buf, this.originalCoin);
		// 原钻石价格
		writeInt(buf, this.originalGold);
		// 原绑定钻石价格
		writeInt(buf, this.originalBindGold);
		// 热销标识 0无热销，1热销中，2折扣，3热销+折扣
		writeByte(buf, this.hot);
		// 物品强化等级定义
		writeInt(buf, this.strengthen);
		// 物品附加属性定义（类型|百分比的分子;类型|百分比的分子
		writeString(buf, this.append);
		// 过期时间 点（格式 yyyy-mm-dd hh:mm:ss）
		writeString(buf, this.lostTime);
		// 购买后离自动失效前的存在时间（单位：秒）
		writeInt(buf, this.duration);
		// 购买时是否立刻绑定（0不是，1是立刻绑定）
		writeByte(buf, this.buybind);
		writeInt(buf, this.q_show_level);
		writeInt(buf, this.q_souldiamond);
		writeInt(buf, this.q_show_souldiamond);
		writeInt(buf, this.q_suitable_level_min);
		writeInt(buf, this.q_suitable_level_max);
		writeInt(buf, this.q_extra_attribute);
		writeInt(buf, this.buy_count);
		writeInt(buf, this.limit_count);
		return true;
	}

	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf) {
		// 商品ID
		this.sellId = readInt(buf);
		// 物品模型ID
		this.modelId = readInt(buf);
		// 排序号
		this.index = readInt(buf);
		// 货币类型 1游戏币，2钻石，4绑定钻石，6钻石与绑定钻石可购买，7三种货币均可购买
		this.moneyType = readString(buf);
		// 金币价格
		this.coin = readInt(buf);
		// 钻石价格
		this.gold = readInt(buf);
		// 绑定钻石价格
		this.bindgold = readInt(buf);
		// 原金币价格
		this.originalCoin = readInt(buf);
		// 原钻石价格
		this.originalGold = readInt(buf);
		// 原绑定钻石价格
		this.originalBindGold = readInt(buf);
		// 热销标识 0无热销，1热销中，2折扣，3热销+折扣
		this.hot = readByte(buf);
		// 物品强化等级定义
		this.strengthen = readInt(buf);
		// 物品附加属性定义（类型|百分比的分子;类型|百分比的分子
		this.append = readString(buf);
		// 过期时间 点（格式 yyyy-mm-dd hh:mm:ss）
		this.lostTime = readString(buf);
		// 购买后离自动失效前的存在时间（单位：秒）
		this.duration = readInt(buf);
		// 购买时是否立刻绑定（0不是，1是立刻绑定）
		this.buybind = readByte(buf);
		this.q_show_level = readInt(buf);
		this.q_souldiamond = readInt(buf);
		this.q_show_souldiamond = readInt(buf);
		this.q_suitable_level_min = readInt(buf);
		this.q_suitable_level_max = readInt(buf);
		this.q_extra_attribute = readInt(buf);
		this.buy_count = readInt(buf);
		this.limit_count = readInt(buf);
		return true;
	}
	

	public int getLimit_count() {
		return limit_count;
	}

	public void setLimit_count(int limit_count) {
		this.limit_count = limit_count;
	}

	public int getQ_souldiamond() {
		return q_souldiamond;
	}

	public void setQ_souldiamond(int q_souldiamond) {
		this.q_souldiamond = q_souldiamond;
	}

	public int getQ_show_souldiamond() {
		return q_show_souldiamond;
	}

	public void setQ_show_souldiamond(int q_show_souldiamond) {
		this.q_show_souldiamond = q_show_souldiamond;
	}


	
	
	public int getQ_show_level() {
		return q_show_level;
	}

	public void setQ_show_level(int q_show_level) {
		this.q_show_level = q_show_level;
	}

	public int getQ_zhuoyue() {
		return q_zhuoyue;
	}

	public void setQ_zhuoyue(int q_zhuoyue) {
		this.q_zhuoyue = q_zhuoyue;
	}

	public int getQ_extra_attribute() {
		return q_extra_attribute;
	}

	public void setQ_extra_attribute(int q_extra_attribute) {
		this.q_extra_attribute = q_extra_attribute;
	}

	public int getQ_lucky() {
		return q_lucky;
	}

	public void setQ_lucky(int q_lucky) {
		this.q_lucky = q_lucky;
	}

	public int getQ_suitable_level_min() {
		return q_suitable_level_min;
	}

	public void setQ_suitable_level_min(int q_suitable_level_min) {
		this.q_suitable_level_min = q_suitable_level_min;
	}

	public int getQ_suitable_level_max() {
		return q_suitable_level_max;
	}

	public void setQ_suitable_level_max(int q_suitable_level_max) {
		this.q_suitable_level_max = q_suitable_level_max;
	}

	

	/**
	 * get 商品ID
	 * 
	 * @return
	 */
	public int getSellId() {
		return sellId;
	}

	/**
	 * set 商品ID
	 */
	public void setSellId(int sellId) {
		this.sellId = sellId;
	}

	/**
	 * get 物品模型ID
	 * 
	 * @return
	 */
	public int getModelId() {
		return modelId;
	}

	/**
	 * set 物品模型ID
	 */
	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	/**
	 * get 排序号
	 * 
	 * @return
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * set 排序号
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * get 货币类型 1游戏币，2钻石，4绑定钻石，6钻石与绑定钻石可购买，7三种货币均可购买
	 * 
	 * @return
	 */
	public String getMoneyType() {
		return moneyType;
	}

	/**
	 * set 货币类型 1游戏币，2钻石，4绑定钻石，6钻石与绑定钻石可购买，7三种货币均可购买
	 */
	public void setMoneyType(String moneyType) {
		this.moneyType = moneyType;
	}

	/**
	 * get 金币价格
	 * 
	 * @return
	 */
	public int getCoin() {
		return coin;
	}

	/**
	 * set 金币价格
	 */
	public void setCoin(int coin) {
		this.coin = coin;
	}

	/**
	 * get 钻石价格
	 * 
	 * @return
	 */
	public int getGold() {
		return gold;
	}

	/**
	 * set 钻石价格
	 */
	public void setGold(int gold) {
		this.gold = gold;
	}

	/**
	 * get 绑定钻石价格
	 * 
	 * @return
	 */
	public int getBindgold() {
		return bindgold;
	}

	/**
	 * set 绑定钻石价格
	 */
	public void setBindgold(int bindgold) {
		this.bindgold = bindgold;
	}

	/**
	 * get 原金币价格
	 * 
	 * @return
	 */
	public int getOriginalCoin() {
		return originalCoin;
	}

	/**
	 * set 原金币价格
	 */
	public void setOriginalCoin(int originalCoin) {
		this.originalCoin = originalCoin;
	}

	/**
	 * get 原钻石价格
	 * 
	 * @return
	 */
	public int getOriginalGold() {
		return originalGold;
	}

	/**
	 * set 原钻石价格
	 */
	public void setOriginalGold(int originalGold) {
		this.originalGold = originalGold;
	}

	/**
	 * get 原绑定钻石价格
	 * 
	 * @return
	 */
	public int getOriginalBindGold() {
		return originalBindGold;
	}

	/**
	 * set 原绑定钻石价格
	 */
	public void setOriginalBindGold(int originalBindGold) {
		this.originalBindGold = originalBindGold;
	}

	/**
	 * get 热销标识 0无热销，1热销中，2折扣，3热销+折扣
	 * 
	 * @return
	 */
	public byte getHot() {
		return hot;
	}

	/**
	 * set 热销标识 0无热销，1热销中，2折扣，3热销+折扣
	 */
	public void setHot(byte hot) {
		this.hot = hot;
	}

	/**
	 * get 物品强化等级定义
	 * 
	 * @return
	 */
	public int getStrengthen() {
		return strengthen;
	}

	/**
	 * set 物品强化等级定义
	 */
	public void setStrengthen(int strengthen) {
		this.strengthen = strengthen;
	}

	/**
	 * get 物品附加属性定义（类型|百分比的分子;类型|百分比的分子
	 * 
	 * @return
	 */
	public String getAppend() {
		return append;
	}

	/**
	 * set 物品附加属性定义（类型|百分比的分子;类型|百分比的分子
	 */
	public void setAppend(String append) {
		this.append = append;
	}

	/**
	 * get 过期时间 点（格式 yyyy-mm-dd hh:mm:ss）
	 * 
	 * @return
	 */
	public String getLostTime() {
		return lostTime;
	}

	/**
	 * set 过期时间 点（格式 yyyy-mm-dd hh:mm:ss）
	 */
	public void setLostTime(String lostTime) {
		this.lostTime = lostTime;
	}

	/**
	 * get 购买后离自动失效前的存在时间（单位：秒）
	 * 
	 * @return
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * set 购买后离自动失效前的存在时间（单位：秒）
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * get 购买时是否立刻绑定（0不是，1是立刻绑定）
	 * 
	 * @return
	 */
	public byte getBuybind() {
		return buybind;
	}

	/**
	 * set 购买时是否立刻绑定（0不是，1是立刻绑定）
	 */
	public void setBuybind(byte buybind) {
		this.buybind = buybind;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("[");
		// 商品ID
		buf.append("sellId:" + sellId + ",");
		// 物品模型ID
		buf.append("modelId:" + modelId + ",");
		// 排序号
		buf.append("index:" + index + ",");
		// 货币类型 1游戏币，2钻石，4绑定钻石，6钻石与绑定钻石可购买，7三种货币均可购买
		buf.append("moneyType:" + moneyType + ",");
		// 金币价格
		buf.append("coin:" + coin + ",");
		// 钻石价格
		buf.append("gold:" + gold + ",");
		// 绑定钻石价格
		buf.append("bindgold:" + bindgold + ",");
		// 原金币价格
		buf.append("originalCoin:" + originalCoin + ",");
		// 原钻石价格
		buf.append("originalGold:" + originalGold + ",");
		// 原绑定钻石价格
		buf.append("originalBindGold:" + originalBindGold + ",");
		// 热销标识 0无热销，1热销中，2折扣，3热销+折扣
		buf.append("hot:" + hot + ",");
		// 物品强化等级定义
		buf.append("strengthen:" + strengthen + ",");
		// 物品附加属性定义（类型|百分比的分子;类型|百分比的分子
		if (this.append != null)
			buf.append("append:" + append.toString() + ",");
		// 过期时间 点（格式 yyyy-mm-dd hh:mm:ss）
		if (this.lostTime != null)
			buf.append("lostTime:" + lostTime.toString() + ",");
		// 购买后离自动失效前的存在时间（单位：秒）
		buf.append("duration:" + duration + ",");
		// 购买时是否立刻绑定（0不是，1是立刻绑定）
		buf.append("buybind:" + buybind + ",");
		buf.append("q_show_level:" + q_show_level + ",");
		if (buf.charAt(buf.length() - 1) == ',')
			buf.deleteCharAt(buf.length() - 1);
		buf.append("]");
		return buf.toString();
	}
}