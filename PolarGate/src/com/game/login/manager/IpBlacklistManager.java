package com.game.login.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by guowenjie on 14-3-10.
 */
public class IpBlacklistManager {

    private static final Logger logger = LoggerFactory.getLogger(IpBlacklistManager.class);

    private static IpBlacklistManager instance;
    public static synchronized IpBlacklistManager getInstance() {
        if (instance == null) instance = new IpBlacklistManager();
        return instance;
    }

    /**
     * 封禁ip地址列表
     */
    private Set<String> blackIPs = Collections.synchronizedSet(new HashSet<String>());
    public Set<String> getBlackIPs() {
        return blackIPs;
    }

    /**
     * 添加
     * @param ips 待添加的ip地址列表
     */
    public void addAll(List<String> ips) {
        logger.debug("add ip to black list: {}", ips);
        blackIPs.addAll(ips);
    }

    /**
     * 删除
     * @param ips 待删除的ip列表
     */
    public void removeAll(List<String> ips) {
        blackIPs.removeAll(ips);
    }

    /**
     * 清空
     */
    public void removeAll() {
        blackIPs.clear();
    }

    /**
     * 重新加载
     * @param ips 重新加载的ip列表
     */
    public void reloadAll(List<String> ips) {
        blackIPs.clear();
        blackIPs.addAll(ips);
    }

    public boolean contains(String ip) {
        return getBlackIPs().contains(ip);
    }
}
