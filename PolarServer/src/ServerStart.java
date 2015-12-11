import com.game.server.impl.WServer;

public class ServerStart {

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			new Thread(WServer.getInstance()).start();
			return;
		}
		
		if (args.length == 1) {
			new Thread(WServer.getInstance()).start();
			if (args[0].startsWith("stid"))
				WServer.startidentity = args[0]; // 记录启动标示
			return;
		}
		
		if (args.length == 2) {
			new Thread(WServer.getInstance(args[0], args[1])).start();
			return;
		}
		
		if (args.length > 2) {
			new Thread(WServer.getInstance(args[0], args[1])).start();
			if (args[3].startsWith("stid"))
				WServer.startidentity = args[0]; // 记录启动标示
		}
	}
}
