package com.game.stalls.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 摊位搜索消息
 */
public class ReqStallsSearchMessage extends Message{

	//道具名称
	private String goodsname;
	//玩家名字
	private String playername;
	//搜索金币或者钻石，0不搜索，1金币，2钻石，3两个都搜索
	private byte goldyuanbao;
	//职业类型
	private int q_job_limit;
	//强化等级
	private byte intensify;
	//佩戴部位
	private int q_kind;
	//物品类型  宝石     分类1_分类2_分类3
	private int q_type;
	//可用物品  0 默认  1 可用
	private byte can_use;
	//追加物品
	private byte addAttribut;
	//卓越物品
	private byte zhuoyue;
	//左侧选中灵魂宝石之类的时候加入的 物品名搜索
	private String hidden;
	/**
 	 *set 道具名称
	 *@return
	 */
	public void setGoodsname(String goodsname){
		this.goodsname = goodsname;
	}

	/**
 	 *get 道具名称
	 *@return
	 */
	public String getGoodsname(){
		return this.goodsname;
	}

	/**
 	 *set 玩家名字
	 *@return
	 */
	public void setPlayername(String playername){
		this.playername = playername;
	}

	/**
 	 *get 玩家名字
	 *@return
	 */
	public String getPlayername(){
		return this.playername;
	}

	/**
 	 *set 搜索金币或者钻石，0不搜索，1金币，2钻石，3两个都搜索
	 *@return
	 */
	public void setGoldyuanbao(byte goldyuanbao){
		this.goldyuanbao = goldyuanbao;
	}

	/**
 	 *get 搜索金币或者钻石，0不搜索，1金币，2钻石，3两个都搜索
	 *@return
	 */
	public byte getGoldyuanbao(){
		return this.goldyuanbao;
	}

	/**
 	 *set 职业类型
	 *@return
	 */
	public void setQ_job_limit(int q_job_limit){
		this.q_job_limit = q_job_limit;
	}

	/**
 	 *get 职业类型
	 *@return
	 */
	public int getQ_job_limit(){
		return this.q_job_limit;
	}

	/**
 	 *set 强化等级
	 *@return
	 */
	public void setIntensify(byte intensify){
		this.intensify = intensify;
	}

	/**
 	 *get 强化等级
	 *@return
	 */
	public byte getIntensify(){
		return this.intensify;
	}

	/**
 	 *set 佩戴部位
	 *@return
	 */
	public void setQ_kind(int q_kind){
		this.q_kind = q_kind;
	}

	/**
 	 *get 佩戴部位
	 *@return
	 */
	public int getQ_kind(){
		return this.q_kind;
	}

	/**
 	 *set 物品类型  宝石     分类1_分类2_分类3
	 *@return
	 */
	public void setQ_type(int q_type){
		this.q_type = q_type;
	}

	/**
 	 *get 物品类型  宝石     分类1_分类2_分类3
	 *@return
	 */
	public int getQ_type(){
		return this.q_type;
	}

	/**
 	 *set 可用物品  0 默认  1 可用
	 *@return
	 */
	public void setCan_use(byte can_use){
		this.can_use = can_use;
	}

	/**
 	 *get 可用物品  0 默认  1 可用
	 *@return
	 */
	public byte getCan_use(){
		return this.can_use;
	}

	/**
 	 *set 追加物品
	 *@return
	 */
	public void setAddAttribut(byte addAttribut){
		this.addAttribut = addAttribut;
	}

	/**
 	 *get 追加物品
	 *@return
	 */
	public byte getAddAttribut(){
		return this.addAttribut;
	}

	/**
 	 *set 卓越物品
	 *@return
	 */
	public void setZhuoyue(byte zhuoyue){
		this.zhuoyue = zhuoyue;
	}

	/**
 	 *get 卓越物品
	 *@return
	 */
	public byte getZhuoyue(){
		return this.zhuoyue;
	}

	/**
 	 *set 左侧选中灵魂宝石之类的时候加入的 物品名搜索
	 *@return
	 */
	public void setHidden(String hidden){
		this.hidden = hidden;
	}

	/**
 	 *get 左侧选中灵魂宝石之类的时候加入的 物品名搜索
	 *@return
	 */
	public String getHidden(){
		return this.hidden;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//道具名称
		writeString(buf,this.goodsname);
		//玩家名字
		writeString(buf,this.playername);
		//搜索金币或者钻石，0不搜索，1金币，2钻石，3两个都搜索
		writeByte(buf,this.goldyuanbao);
		//职业类型
		writeInt(buf,this.q_job_limit);
		//强化等级
		writeByte(buf,this.intensify);
		//佩戴部位
		writeInt(buf,this.q_kind);
		//物品类型  宝石     分类1_分类2_分类3
		writeInt(buf,this.q_type);
		//可用物品  0 默认  1 可用
		writeByte(buf,this.can_use);
		//追加物品
		writeByte(buf,this.addAttribut);
		//卓越物品
		writeByte(buf,this.zhuoyue);
		//左侧选中灵魂宝石之类的时候加入的 物品名搜索
		writeString(buf,this.hidden);
		return true;
	}

	@Override
	public int getId() {
		return 123208;
	}
	@Override
	public String getQueue() {
		return null;
	}
	@Override
	public String getServer(){
		return null;
	} 
	/**
 	 *读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//道具名称
		this.goodsname = readString(buf);
		//玩家名字
		this.playername = readString(buf);
		//搜索金币或者钻石，0不搜索，1金币，2钻石，3两个都搜索
		this.goldyuanbao = readByte(buf);
		//职业类型
		this.q_job_limit = readInt(buf);
		//强化等级
		this.intensify = readByte(buf);
		//佩戴部位
		this.q_kind = readInt(buf);
		//物品类型  宝石     分类1_分类2_分类3
		this.q_type = readInt(buf);
		//可用物品  0 默认  1 可用
		this.can_use = readByte(buf);
		//追加物品
		this.addAttribut = readByte(buf);
		//卓越物品
		this.zhuoyue = readByte(buf);
		//左侧选中灵魂宝石之类的时候加入的 物品名搜索
		this.hidden = readString(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//道具名称
		buf.append("goodsname:"+goodsname+",");
		//玩家名字
		buf.append("playername:"+playername+",");
		//搜索金币或者钻石，0不搜索，1金币，2钻石，3两个都搜索
		buf.append("goldyuanbao:"+goldyuanbao+",");
		//职业类型
		buf.append("q_job_limit:"+q_job_limit+",");
		//强化等级
		buf.append("intensify:"+intensify+",");
		//佩戴部位
		buf.append("q_kind:"+q_kind+",");
		//物品类型  宝石     分类1_分类2_分类3
		buf.append("q_type:"+q_type+",");
		//可用物品  0 默认  1 可用
		buf.append("can_use:"+can_use+",");
		//追加物品
		buf.append("addAttribut:"+addAttribut+",");
		//卓越物品
		buf.append("zhuoyue:"+zhuoyue+",");
		//左侧选中灵魂宝石之类的时候加入的 物品名搜索
		buf.append("hidden:"+hidden+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}