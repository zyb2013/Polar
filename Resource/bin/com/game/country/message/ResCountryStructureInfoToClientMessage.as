package com.game.country.message{
	import com.game.country.bean.CountryStructureInfo;
	import net.Message;
	
	/** 
	 * @author Commuication Auto Maker
	 * 
	 * @version 1.0.0
	 * 
	 * @since 2011-5-8
	 * 
	 * 请求王城结构信息（打开面板）
	 */
	public class ResCountryStructureInfoToClientMessage extends Message {
	
		//王城结构信息
		private var _countrystructureInfo: CountryStructureInfo;
		
		
		/**
		 * 写入字节缓存
		 */
		override protected function writing(): Boolean{
			//王城结构信息
			writeBean(_countrystructureInfo);
			return true;
		}
		
		/**
		 * 读取字节缓存
		 */
		override protected function reading(): Boolean{
			//王城结构信息
			_countrystructureInfo = readBean(CountryStructureInfo) as CountryStructureInfo;
			return true;
		}
		
		/**
		 * get id
		 * @return 
		 */
		override public function getId(): int {
			return 146105;
		}
		
		/**
		 * get 王城结构信息
		 * @return 
		 */
		public function get countrystructureInfo(): CountryStructureInfo{
			return _countrystructureInfo;
		}
		
		/**
		 * set 王城结构信息
		 */
		public function set countrystructureInfo(value: CountryStructureInfo): void{
			this._countrystructureInfo = value;
		}
		
	}
}