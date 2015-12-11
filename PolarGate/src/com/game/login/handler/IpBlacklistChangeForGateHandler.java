package com.game.login.handler;

import com.game.command.Handler;
import com.game.login.message.IpBlacklistChangeForGateMessage;
import com.game.manager.ManagerPool;
import org.apache.log4j.Logger;

/**
 * 改动封禁ip列表
 * Created by guowenjie on 14-3-10.
 */
public class IpBlacklistChangeForGateHandler extends Handler{
    Logger log = Logger.getLogger(IpBlacklistChangeForGateHandler.class);

    @Override
    public void action() {
        IpBlacklistChangeForGateMessage message = (IpBlacklistChangeForGateMessage) this.getMessage();

        log.debug("receive: " + message);

        switch (message.getActionType()) {
            case 1: // 全部替换
                ManagerPool.blacklistManager.reloadAll(message.getIps());
                break;
            case 2: // 增加
                ManagerPool.blacklistManager.addAll(message.getIps());
                break;
            case 3: // 删除
                ManagerPool.blacklistManager.removeAll(message.getIps());
                break;
            case 4: // 清空
                ManagerPool.blacklistManager.removeAll();
                break;
            default:
                log.error("Unknown action type: " + message.getActionType());
        }
    }
}
