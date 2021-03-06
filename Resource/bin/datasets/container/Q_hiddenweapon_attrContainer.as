package datasets.container {
	import flash.utils.Dictionary;
	import datasets.bean.Q_hiddenweapon_attr;

	import flash.utils.ByteArray;
	/** 
	 * @author ExcelUtil Auto Maker
	 * 
	 * @version 1.0.0
	 * 
	 * @since 2011-5-8
	 * 
	 * Q_hiddenweapon_attr
	 */
	public class Q_hiddenweapon_attrContainer {
		
		private var _list:Vector.<Q_hiddenweapon_attr> = new Vector.<Q_hiddenweapon_attr>();
		
		private var _dict:Dictionary = new Dictionary();
		
		private var _version:int;
		
		public function Q_hiddenweapon_attrContainer(bytes: ByteArray){
			_version = bytes.readInt();
			var num:int = bytes.readInt();
			for (var i : int = 0; i < num; i++) {
				var bean:Q_hiddenweapon_attr = new Q_hiddenweapon_attr();
				bean.read(bytes);
				_list.push(bean);
				_dict[String(bean.q_id)] = bean;
			}
		}
		
		public function get list(): Vector.<Q_hiddenweapon_attr>{
			return _list;
		}
		
		public function get dict(): Dictionary{
			return _dict;
		}
		
		public function get version(): int{
			return _version;
		}
	}
}
