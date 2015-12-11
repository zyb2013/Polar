package com.game.bank.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 返回 玩家钱庄 领取信息
 */
public class ResQueryBankMessage extends Message{

	//类型 0 月卡  大于1 对应 升级钱庄相应的 钱庄档次  -1代表 未购买 
	private int buyLv;
	//该玩家的领取记录    用逗号分隔   0,1,2,3   0代表即时奖励   1 2 3 代表已经领取的 天数/级数 
	private String nums;
	//当前第几天
	private int curryday;
	/**
 	 *set 类型 0 月卡  大于1 对应 升级钱庄相应的 钱庄档次  -1代表 未购买 
	 *@return
	 */
	public void setBuyLv(int buyLv){
		this.buyLv = buyLv;
	}

	/**
 	 *get 类型 0 月卡  大于1 对应 升级钱庄相应的 钱庄档次  -1代表 未购买 
	 *@return
	 */
	public int getBuyLv(){
		return this.buyLv;
	}

	/**
 	 *set 该玩家的领取记录    用逗号分隔   0,1,2,3   0代表即时奖励   1 2 3 代表已经领取的 天数/级数 
	 *@return
	 */
	public void setNums(String nums){
		this.nums = nums;
	}

	/**
 	 *get 该玩家的领取记录    用逗号分隔   0,1,2,3   0代表即时奖励   1 2 3 代表已经领取的 天数/级数 
	 *@return
	 */
	public String getNums(){
		return this.nums;
	}

	/**
 	 *set 当前第几天
	 *@return
	 */
	public void setCurryday(int curryday){
		this.curryday = curryday;
	}

	/**
 	 *get 当前第几天
	 *@return
	 */
	public int getCurryday(){
		return this.curryday;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//类型 0 月卡  大于1 对应 升级钱庄相应的 钱庄档次  -1代表 未购买 
		writeInt(buf,this.buyLv);
		//该玩家的领取记录    用逗号分隔   0,1,2,3   0代表即时奖励   1 2 3 代表已经领取的 天数/级数 
		writeString(buf,this.nums);
		//当前第几天
		writeInt(buf,this.curryday);
		return true;
	}

	@Override
	public int getId() {
		return 510200;
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
		//类型 0 月卡  大于1 对应 升级钱庄相应的 钱庄档次  -1代表 未购买 
		this.buyLv = readInt(buf);
		//该玩家的领取记录    用逗号分隔   0,1,2,3   0代表即时奖励   1 2 3 代表已经领取的 天数/级数 
		this.nums = readString(buf);
		//当前第几天
		this.curryday = readInt(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//类型 0 月卡  大于1 对应 升级钱庄相应的 钱庄档次  -1代表 未购买 
		buf.append("buyLv:"+buyLv+",");
		//该玩家的领取记录    用逗号分隔   0,1,2,3   0代表即时奖励   1 2 3 代表已经领取的 天数/级数 
		buf.append("nums:"+nums+",");
		//当前第几天
		buf.append("curryday:"+curryday+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}