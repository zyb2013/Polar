package com.game.utils;

import com.alibaba.fastjson.JSON;
import com.game.backpack.bean.GoodsAttribute;
import com.game.backpack.structs.Attribute;
import com.game.backpack.structs.Equip;
import com.game.backpack.structs.Item;
import com.game.json.JSONserializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 客户端解析字符串
 *
 * @author  
 */
public class ParseUtil {

	private String t;
	private List<Parm> ps = new ArrayList<Parm>();

	public List<Parm> getPs() {
		return ps;
	}

	public void setPs(List<Parm> ps) {
		this.ps = ps;
	}

	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}

	public void setFormatParseString(String parseString, Object... values) {
		this.t = String.format(parseString, values);
	}

	public void setValue(String parseString, Parm... parms) {
		setT(parseString);
		if (parms != null) {
			for (int i = 0; i < parms.length; i++) {
				Parm parm = parms[i];
				if (parm != null) {
					getPs().add(parm);
				}
			}
		}
	}

	public void setValue(Parm... parms) {
		if (parms != null) {
			for (int i = 0; i < parms.length; i++) {
				Parm parm = parms[i];
				if (parm != null) {
					getPs().add(parm);
				}
			}
		}
	}

	public void setPlayerParm(long playerid, String playerName) {
		getPs().add(new PlayerParm(playerid, playerName));
	}

	public void setMapParm(int mapid, byte line, int x, int y) {
		getPs().add(new MapParm(mapid, line, x, y));
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	public static String toString(ParseUtil parseUtil) {
		return JSON.toJSONString(parseUtil);
	}

	public static ParseUtil toObject(String jsonString) {
		return (ParseUtil) JSON.parse(jsonString);
	}

	//------------------------------参数类-------------------------//
	public static class Parm {

		private byte type;

		public Parm(byte type) {
			this.type = type;
		}

		public byte getType() {
			return type;
		}

		public void setType(byte type) {
			this.type = type;
		}
	}

	public static class PlayerParm extends Parm {

		private String pid;
		private String name;

		public PlayerParm(long playerid, String playerName) {
			super((byte) 1);
			this.pid = String.valueOf(playerid);// Long.toString(playerid, 16);
			this.name = playerName;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPid() {
			return pid;
		}

		public void setPid(String pid) {
			this.pid = pid;
		}
	}

	/**扩展名字
	 * 
	 * @author zhangrong
	 *
	 */
	public static class PlayerExParm extends Parm {

		private String pid;
		private String name;
		private String cname;
		private String gname;
		
		public PlayerExParm(long playerid, String playerName  ,String cname , String gname) {
			super((byte) 1);
			this.pid = Long.toString(playerid, 16);
			this.name = playerName;
			this.cname = cname;
			this.gname =gname;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPid() {
			return pid;
		}

		public void setPid(String pid) {
			this.pid = pid;
		}

		public String getCname() {
			return cname;
		}

		public void setCname(String cname) {
			this.cname = cname;
		}

		public String getGname() {
			return gname;
		}

		public void setGname(String gname) {
			this.gname = gname;
		}
	}
	
	public static class MapParm extends Parm {

		private int mapid;
		private byte line;
		private int x;
		private int y;
		private String mapName;

		public MapParm(int mapid, int line, int x, int y) {
			super((byte) 2);
			this.mapid = mapid;
			this.line = (byte) line;
			this.x = x;
			this.y = y;
		}
		
		public MapParm(int mapid, int line, int x, int y, String mapName) {
			super((byte) 2);
			this.mapid = mapid;
			this.line = (byte) line;
			this.x = x;
			this.y = y;
			this.mapName = mapName;
		}

		public byte getLine() {
			return line;
		}

		public void setLine(byte line) {
			this.line = line;
		}

		public int getMapid() {
			return mapid;
		}

		public void setMapid(int mapid) {
			this.mapid = mapid;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}
		
		public String getMapName(){
			return this.mapName;
		}
		
		public void setMapName(String mapName){
			this.mapName = mapName;
		}
	}
	
	public static String getMapString(Map<String, String> map, String param){
		String value = "";
		if(map.containsKey(param)){
			value = map.get(param);
		}
		return value;
	}
	public static int getMapInt(Map<String, String> map, String param){
		return getMapInt(map, param, 0);
	}
	public static int getMapInt(Map<String, String> map, String param, int defaultvalue){
		int value = defaultvalue;
		if (map.containsKey(param)) {
			String v = map.get(param);
			if (!StringUtils.isBlank(v) 
					&& (StringUtils.isNumeric(v) || (v.length()>1 && v.startsWith("-")&&StringUtils.isNumeric(v.substring(1)))))
				value = Integer.parseInt(v);
		}
		return value;
	}
	
	public static class VipParm extends Parm {
		
		private int viplv;
		private int idx;

		public VipParm(int viplv, int idx) {
			super((byte) 3);
			this.viplv = viplv;
			this.idx = idx;
		}

		public int getIdx() {
			return idx;
		}

		public void setIdx(int idx) {
			this.idx = idx;
		}

		public int getViplv() {
			return viplv;
		}

		public void setViplv(int viplv) {
			this.viplv = viplv;
		}
	}
	

	public static class WebParm extends Parm {
		private String name;
		private String url;

		public WebParm(String name, String url) {
			super((byte) 4);
			this.setName(name);
			this.setUrl(url);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		
	}
	
	
	public static class ItemParm extends Parm {
		
		private long itemId;
		private int itemModelId;
		private int num;
		private int gridId;
		private byte isbind;
		private byte intensify;
		private byte attributs;
		private byte isFullAppend;
		private int lostTime;
		private List<GoodsAttribute> goodAttributes = new ArrayList<GoodsAttribute>();
		private String parameters;
		private byte addAttributLevel;
		private int fightPower;

		public ItemParm(Item item) {
			super((byte)5);
			if(item != null) {
				this.itemId = item.getId();
				this.itemModelId = item.getItemModelId();
				this.num = item.getNum();
				this.gridId = item.getGridId();
				this.isbind = (byte) (item.isBind() ? 1 : 0);
				if(item instanceof Equip){
					Equip equip = (Equip) item;
					if(equip.getAttributes() != null) {
						this.attributs = (byte) equip.getAttributes().size();
						for (Attribute attribute : equip.getAttributes()) {
							this.goodAttributes.add(attribute.buildGoodsAttribute());		
						}
					}else{
						this.attributs = (byte) 0;
					}
					this.addAttributLevel = (byte) equip.getAddAttributeLevel();
					this.intensify = (byte) equip.getGradeNum();
					this.fightPower = equip.getFightPower();
				}
				this.lostTime = item.getLosttime();
				if (item.getParameters().size() > 0) {
					this.parameters = JSONserializable.toString(item.getParameters());
				}
			}
		}

		public long getItemId() {
			return itemId;
		}

		public void setItemId(long itemId) {
			this.itemId = itemId;
		}

		public int getItemModelId() {
			return itemModelId;
		}

		public void setItemModelId(int itemModelId) {
			this.itemModelId = itemModelId;
		}

		public int getNum() {
			return num;
		}

		public void setNum(int num) {
			this.num = num;
		}

		public int getGridId() {
			return gridId;
		}

		public void setGridId(int gridId) {
			this.gridId = gridId;
		}

		public byte getIsbind() {
			return isbind;
		}

		public void setIsbind(byte isbind) {
			this.isbind = isbind;
		}

		public byte getIntensify() {
			return intensify;
		}

		public void setIntensify(byte intensify) {
			this.intensify = intensify;
		}

		public byte getAttributs() {
			return attributs;
		}

		public void setAttributs(byte attributs) {
			this.attributs = attributs;
		}

		public byte getIsFullAppend() {
			return isFullAppend;
		}

		public void setIsFullAppend(byte isFullAppend) {
			this.isFullAppend = isFullAppend;
		}

		public int getLostTime() {
			return lostTime;
		}

		public void setLostTime(int lostTime) {
			this.lostTime = lostTime;
		}

		public List<GoodsAttribute> getGoodAttributes() {
			return goodAttributes;
		}

		public void setGoodAttributes(List<GoodsAttribute> goodAttributes) {
			this.goodAttributes = goodAttributes;
		}

		public String getParameters() {
			return parameters;
		}

		public void setParameters(String parameters) {
			this.parameters = parameters;
		}

		public byte getAddAttributLevel() {
			return addAttributLevel;
		}

		public void setAddAttributLevel(byte addAttributLevel) {
			this.addAttributLevel = addAttributLevel;
		}

		public int getFightPower() {
			return fightPower;
		}

		public void setFightPower(int fightPower) {
			this.fightPower = fightPower;
		}
	}
}
