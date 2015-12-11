package com.game.toplist.message{
	import com.game.toplist.bean.TopInfo;
	import net.Message;
	
	/** 
	 * @author Commuication Auto Maker
	 * 
	 * @version 1.0.0
	 * 
	 * @since 2011-5-8
	 * 
	 * 发送给客户端获取排行榜信息
	 */
	public class ResGetTopListToClientMessage extends Message {
	
		//错误代码
		private var _errorcode: int;
		
		//自己今天膜拜次数
		private var _worshipnum: int;
		
		//排行类型 1等级 2坐骑 3武功 4龙元 5 连斩
		private var _toptype: int;
		
		//前5排行榜信息列表
		private var _top5infolist: Vector.<TopInfo> = new Vector.<TopInfo>();
		//自己排行榜信息列表
		private var _topselfinfolist: Vector.<TopInfo> = new Vector.<TopInfo>();
		
		/**
		 * 写入字节缓存
		 */
		override protected function writing(): Boolean{
			var i: int = 0;
			//错误代码
			writeByte(_errorcode);
			//自己今天膜拜次数
			writeByte(_worshipnum);
			//排行类型 1等级 2坐骑 3武功 4龙元 5 连斩
			writeByte(_toptype);
			//前5排行榜信息列表
			writeShort(_top5infolist.length);
			for (i = 0; i < _top5infolist.length; i++) {
				writeBean(_top5infolist[i]);
			}
			//自己排行榜信息列表
			writeShort(_topselfinfolist.length);
			for (i = 0; i < _topselfinfolist.length; i++) {
				writeBean(_topselfinfolist[i]);
			}
			return true;
		}
		
		/**
		 * 读取字节缓存
		 */
		override protected function reading(): Boolean{
			var i: int = 0;
			//错误代码
			_errorcode = readByte();
			//自己今天膜拜次数
			_worshipnum = readByte();
			//排行类型 1等级 2坐骑 3武功 4龙元 5 连斩
			_toptype = readByte();
			//前5排行榜信息列表
			var top5infolist_length : int = readShort();
			for (i = 0; i < top5infolist_length; i++) {
				_top5infolist[i] = readBean(TopInfo) as TopInfo;
			}
			//自己排行榜信息列表
			var topselfinfolist_length : int = readShort();
			for (i = 0; i < topselfinfolist_length; i++) {
				_topselfinfolist[i] = readBean(TopInfo) as TopInfo;
			}
			return true;
		}
		
		/**
		 * get id
		 * @return 
		 */
		override public function getId(): int {
			return 142101;
		}
		
		/**
		 * get 错误代码
		 * @return 
		 */
		public function get errorcode(): int{
			return _errorcode;
		}
		
		/**
		 * set 错误代码
		 */
		public function set errorcode(value: int): void{
			this._errorcode = value;
		}
		
		/**
		 * get 自己今天膜拜次数
		 * @return 
		 */
		public function get worshipnum(): int{
			return _worshipnum;
		}
		
		/**
		 * set 自己今天膜拜次数
		 */
		public function set worshipnum(value: int): void{
			this._worshipnum = value;
		}
		
		/**
		 * get 排行类型 1等级 2坐骑 3武功 4龙元 5 连斩
		 * @return 
		 */
		public function get toptype(): int{
			return _toptype;
		}
		
		/**
		 * set 排行类型 1等级 2坐骑 3武功 4龙元 5 连斩
		 */
		public function set toptype(value: int): void{
			this._toptype = value;
		}
		
		/**
		 * get 前5排行榜信息列表
		 * @return 
		 */
		public function get top5infolist(): Vector.<TopInfo>{
			return _top5infolist;
		}
		
		/**
		 * set 前5排行榜信息列表
		 */
		public function set top5infolist(value: Vector.<TopInfo>): void{
			this._top5infolist = value;
		}
		
		/**
		 * get 自己排行榜信息列表
		 * @return 
		 */
		public function get topselfinfolist(): Vector.<TopInfo>{
			return _topselfinfolist;
		}
		
		/**
		 * set 自己排行榜信息列表
		 */
		public function set topselfinfolist(value: Vector.<TopInfo>): void{
			this._topselfinfolist = value;
		}
		
	}
}