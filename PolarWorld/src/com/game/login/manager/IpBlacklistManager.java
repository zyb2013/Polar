package com.game.login.manager;

import com.game.db.dao.BlackListDao;
import com.game.login.message.IpBlacklistChangeForGateMessage;
import com.game.utils.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by guowenjie on 14-3-10.
 */
public class IpBlacklistManager {
    private static final Logger logger = LoggerFactory.getLogger(IpBlacklistManager.class);
    private static AtomicBoolean initialized = new AtomicBoolean(false);

    private static IpBlacklistManager instance;

    public static synchronized IpBlacklistManager getInstance() {
        if (instance == null) instance = new IpBlacklistManager();
        return instance;
    }

    private BlackListDao blacklistdao = new BlackListDao();

    /**
     * 封禁ip地址列表
     */
    private Set<String> blackIPs = Collections.synchronizedSet(new HashSet<String>());

    public Set<String> getBlackIPs() {
        return blackIPs;
    }

    /**
     * 重新加载
     *
     * @param ips 重新加载的ip列表
     */
    public synchronized void reloadAll(List<String> ips) {
        blacklistdao.reloadAllIps(ips);
        blackIPs.clear();
        blackIPs.addAll(ips);

        IpBlacklistChangeForGateMessage message = new IpBlacklistChangeForGateMessage();
        message.setActionType((byte) 1);
        message.setIps(ips);
        MessageUtil.send_to_gate(message);
    }

    /**
     * 添加
     *
     * @param ips 待添加的ip地址列表
     */
    public void addAll(List<String> ips) {
        blacklistdao.addAllIps(ips);
        blackIPs.addAll(ips);

        IpBlacklistChangeForGateMessage message = new IpBlacklistChangeForGateMessage();
        message.setActionType((byte) 2);
        message.setIps(ips);
        logger.debug("add ip to black list: {}", ips);
        MessageUtil.send_to_gate(message);
    }

    /**
     * 删除
     *
     * @param ips 待删除的ip列表
     */
    public void removeAll(List<String> ips) {
        blacklistdao.removeIps(ips);
        blackIPs.removeAll(ips);

        IpBlacklistChangeForGateMessage message = new IpBlacklistChangeForGateMessage();
        message.setActionType((byte) 3);
        message.setIps(ips);
        MessageUtil.send_to_gate(message);
    }


    /**
     * 清空（删除所有）
     */
    public void removeAll() {
        blacklistdao.removeAllIps();
        blackIPs.clear();

        IpBlacklistChangeForGateMessage message = new IpBlacklistChangeForGateMessage();
        message.setActionType((byte) 4);
        message.setIps(new ArrayList<String>());
        MessageUtil.send_to_gate(message);
    }

    public boolean contains(String ip) {
        return getBlackIPs().contains(ip);
    }

    /**
     * 发消息给Gate重新加载
     */
    public void refreshGate(int gateId) {
        reloadAll();

        List<String> ips = new ArrayList<>();
        ips.addAll(blackIPs);
        IpBlacklistChangeForGateMessage message = new IpBlacklistChangeForGateMessage();
        message.setActionType((byte) 1);
        message.setIps(ips);
        MessageUtil.send_to_gate(gateId, message);
    }

    /**
     * 从数据库中加载，仅调用一次
     */
    public void reloadAll() {
        if (initialized.compareAndSet(false, true)) {
            logger.debug("加载ip封禁列表");
            List<String> ips = blacklistdao.getAllIps();
            blackIPs.clear();
            blackIPs.addAll(ips);
        }
    }
}
