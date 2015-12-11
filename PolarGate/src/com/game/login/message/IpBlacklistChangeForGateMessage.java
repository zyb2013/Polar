package com.game.login.message;

import com.game.message.Message;
import org.apache.mina.core.buffer.IoBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guowenjie on 14-3-10.
 */
public class IpBlacklistChangeForGateMessage extends Message {

    @Override
    public int getId() {
        return 300315;
    }

    /**
     * 更新类型，1 全部覆盖 2 添加 3 删除 4 清空
     */
    private byte actionType;
    /**
     * ip 地址列表
     */
    private List<String> ips;

    @Override
    public String getQueue() {
        return null;
    }

    @Override
    public String getServer() {
        return null;
    }

    @Override
    public boolean write(IoBuffer buf) {
        writeByte(buf, actionType);
        writeInt(buf, ips.size());
        for (String ip : ips) writeString(buf, ip);
        return true;
    }

    @Override
    public boolean read(IoBuffer buf) {
        this.actionType = readByte(buf);
        int ipCount = readInt(buf);
        List<String> ips = new ArrayList<>();
        for (int i = 0; i < ipCount; i++) {
            ips.add(readString(buf));
        }
        setIps(ips);
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IpBlacklistChangeForGateMessage{");
        sb.append("actionType=").append(actionType);
        sb.append(", ips=").append(ips);
        sb.append('}');
        return sb.toString();
    }

    public byte getActionType() {
        return actionType;
    }

    public void setActionType(byte actionType) {
        this.actionType = actionType;
    }

    public List<String> getIps() {
        return ips;
    }

    public void setIps(List<String> ips) {
        this.ips = ips;
    }
}
