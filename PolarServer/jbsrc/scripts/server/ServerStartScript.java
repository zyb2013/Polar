package scripts.server;

import com.game.manager.ManagerPool;
import com.game.script.structs.ScriptEnum;
import com.game.server.impl.WServer;
import com.game.server.script.IServerStartScript;
/**服务器启动调用
 * 
 * @author zhangrong
 *
 */
public class ServerStartScript implements IServerStartScript {

	@Override
	public int getId() {
		return ScriptEnum.SERVER_START;
	}

	@Override
	public void onStart(String web, int serverId) {
		setswitch();
	}
	
	//设定功能开关
	public void setswitch(){
		if(WServer.getInstance().getServerWeb().equals("hgpupugame")) {	
			//韩国版-关闭境界系统
			ManagerPool.realmManager.setIsopen(false);
			//韩国版-关闭出售紫装给熔炼石
			ManagerPool.shopManager.setIsgivestone(false);
			//韩国测试期间关闭临时钻石
			ManagerPool.backpackManager.setIsopentmpgold(false);
		}
		
		
	}
	
	
	

}
