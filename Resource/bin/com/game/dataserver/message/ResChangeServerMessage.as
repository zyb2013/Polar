package com.game.dataserver.message{
	import com.game.utils.long;
	import net.Message;
	
	/** 
	 * @author Commuication Auto Maker
	 * 
	 * @version 1.0.0
	 * 
	 * @since 2011-5-8
	 * 
	 * 切换服务器
	 */
	public class ResChangeServerMessage extends Message {
	
		//服务器Id
		private var _serverId: int;
		
		//服务器平台
		private var _web: String;
		
		//账号id
		private var _userId: String;
		
		//角色id
		private var _dataServerPlayerId: long;
		
		
		/**
		 * 写入字节缓存
		 */
		override protected function writing(): Boolean{
			//服务器Id
			writeInt(_serverId);
			//服务器平台
			writeString(_web);
			//账号id
			writeString(_userId);
			//角色id
			writeLong(_dataServerPlayerId);
			return true;
		}
		
		/**
		 * 读取字节缓存
		 */
		override protected function reading(): Boolean{
			//服务器Id
			_serverId = readInt();
			//服务器平台
			_web = readString();
			//账号id
			_userId = readString();
			//角色id
			_dataServerPlayerId = readLong();
			return true;
		}
		
		/**
		 * get id
		 * @return 
		 */
		override public function getId(): int {
			return 203101;
		}
		
		/**
		 * get 服务器Id
		 * @return 
		 */
		public function get serverId(): int{
			return _serverId;
		}
		
		/**
		 * set 服务器Id
		 */
		public function set serverId(value: int): void{
			this._serverId = value;
		}
		
		/**
		 * get 服务器平台
		 * @return 
		 */
		public function get web(): String{
			return _web;
		}
		
		/**
		 * set 服务器平台
		 */
		public function set web(value: String): void{
			this._web = value;
		}
		
		/**
		 * get 账号id
		 * @return 
		 */
		public function get userId(): String{
			return _userId;
		}
		
		/**
		 * set 账号id
		 */
		public function set userId(value: String): void{
			this._userId = value;
		}
		
		/**
		 * get 角色id
		 * @return 
		 */
		public function get dataServerPlayerId(): long{
			return _dataServerPlayerId;
		}
		
		/**
		 * set 角色id
		 */
		public function set dataServerPlayerId(value: long): void{
			this._dataServerPlayerId = value;
		}
		
	}
}