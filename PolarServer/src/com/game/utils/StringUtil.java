package com.game.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.game.backpack.structs.AnalysisItemInfo;

public class StringUtil extends StringUtils{
	public static final String EMPTY_STRING = "";
	public static final int ZERO = 0;
	
	/**
	  * 查询一个字符串在另一个字符串中的出现频率 
	  * @param selectStr 查询字符
	  * @param targetStr 目标字符串
	  * @return
	  */
	public static int findStrIndexOfCount(String selectStr, String targetStr) {		
		return countMatches(selectStr, targetStr);
	}
	
	/**
	  * 查询一个字符串在另一个字符串中的出现频率忽略大小写 
	  * @param selectStr 查询字符
	  * @param targetStr 目标字符串
	  * @return
	  */
	public static int findStrIndexOfCountIgnoreCase(String selectStr, String targetStr) {
		selectStr=selectStr.toUpperCase();
		targetStr=targetStr.toUpperCase();
		return countMatches(selectStr,targetStr);
	}
	
	/**
	 * 判断是否空字符串 忽略空格
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s){
		if(s==null){
			return true;
		}
		if("".equals(s.trim())){
			return true;
		}
		return false;
	}
	
	/**
	 * equals比较
	 * @param objs
	 * @param obj
	 * @return
	 */
	public static <T> boolean hasObject(T[] objs, T obj) {
		if(obj==null){
			for (Object object : objs) {
				if(object==obj){
					return true;
				}
			}
		}else{
			for (Object object : objs) {
				if(obj.equals(object)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 获得一个JSON的数据 
	 * <br>注意：这里的格式是没有最外层的括号的如下:
	 * <br>“x:123, y:123, name:"张三" ”
	 * @param txt
	 * @return 
	 * 
	 */		
	public static String formatToJson(String txt)
	{
		txt = txt.substring(1, txt.length() - 1);
		txt = "{" + txt + "}"; 
		txt = txt.replaceAll(Symbol.DOUHAO_REG, Symbol.DOUHAO);
		String parse = txt.replaceAll("([{,，])\\s*([0-9a-zA-Z一-龟]+)\\s*:", "$1\"$2\":" );
		return parse;
	}
	
	/**
	 * 特殊的道具配置解析
	 * @param rewardStr
	 * @return
	 * @create	hongxiao.z      2014-3-12 下午5:15:20
	 */
	public static List<AnalysisItemInfo> analysisItems(String rewardStr)
	{
		//道具列表信息
		List<AnalysisItemInfo> analys = new ArrayList<>();
		
		if(rewardStr == null || rewardStr.isEmpty()) return analys;
		
		//解析出来的道具列表	[道具ID_道具数量_有效时长_强化等级_追加等级_卓越类型|值;卓越类型|值_会心一击率_无视一击率],[道具ID_道具数量_有效时长]
		String[] temp = rewardStr.split(",");
		
		//将[道具ID_道具数量_有效时长_强化等级_追加等级_卓越类型|值;卓越类型|值_会心一击率_无视一击率]
		for (String string : temp) 
		{
			string = string.substring(1, string.length() - 1);
			//元素属性集合
			String[] elems = string.split("_");
			AnalysisItemInfo info = new AnalysisItemInfo();
			info.setItemId(Integer.parseInt(elems[0]));
			info.setNum(Integer.parseInt(elems[1]));
			info.setDuration(Integer.parseInt(elems[2]));
			
			//如果是装备
			if(elems.length > 3)
			{
				info.setStrongLv(Integer.parseInt(elems[3]));
				info.setAddLv(Integer.parseInt(elems[4]));
				//解析卓越属性
				info.setZhuoyue(elems[5]);
				info.setHuixin(Integer.parseInt(elems[6]));
				info.setWushi(Integer.parseInt(elems[7]));
			}
			
			analys.add(info);
		}
		
		return analys;
	}
	
//	public static void main(String[] args) {
//		String data = "[攻击:100，防御:100，生命上限:100，暴击:100]";
//		System.out.println(StringUtil.formatToJson(data));
//	}
}
