package com.game.player.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
import java.util.List;
import java.util.ArrayList;
import com.game.backpack.bean.ItemInfo;
/**
  * 点击收藏获得奖励结果
 */
public class ResCollectionRewardMessage extends Message{

	//奖励道具列表
	private List<ItemInfo> itemlist = new ArrayList<ItemInfo>();
	/**
 	 *set 奖励道具列表
	 *@return
	 */
	public void setItemlist(List<ItemInfo> itemlist){
		this.itemlist = itemlist;
	}

	/**
 	 *get 奖励道具列表
	 *@return
	 */
	public List<ItemInfo> getItemlist(){
		return this.itemlist;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//奖励道具列表
		writeShort(buf,itemlist.size());
		for(int i = 0;i < itemlist.size();i++){
			writeBean(buf,itemlist.get(i));
		}
		return true;
	}

	@Override
	public int getId() {
		return 55001;
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
		//奖励道具列表
		int itemlist_length = readShort(buf);
		for(int i = 0;i < itemlist_length;i++){
			itemlist.add((ItemInfo)readBean(buf,ItemInfo.class));
		}
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//奖励道具列表
		buf.append("itemlist:{");
		for(int i=0;i<itemlist.size();i++){
			buf.append(itemlist.get(i).toString()+",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}