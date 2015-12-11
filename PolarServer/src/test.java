
import com.game.backpack.bean.GoodsAttribute;
import com.game.backpack.bean.ItemInfo;
import com.game.json.JSONserializable;
import com.game.utils.StringUtil;

public class test {
	public static void main(String args[]){
		String json_string = "{\"addAttributLevel\":0,\"attributs\":1,\"fightPower\":828,\"attributeCount\":11,\"goodAttributes\":[{\"type\":45,\"value\":1000}],\"gridId\":0,\"intensify\":0,\"isFullAppend\":0,\"isbind\":0,\"itemId\":91845904414496,\"itemModelId\":1423004,\"lostTime\":0,\"num\":1,\"parameters\":\"\"}";
		
		try{
			ItemInfo info = (ItemInfo) JSONserializable.toObject(StringUtil.formatToJson(json_string),ItemInfo.class);
			System.out.println(info);
			json_string ="{\"type\":45,\"value\":1000}";
			GoodsAttribute info2 = (GoodsAttribute) JSONserializable.toObject(json_string,GoodsAttribute.class);
			System.out.println(info2);
		}catch(Exception ex){
			System.out.println();
		}
	}
}
