package com.emulate.im.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tio.common.starter.annotation.TioServerIpStatListener;
import org.tio.core.ChannelContext;
import org.tio.core.TioConfig;
import org.tio.core.intf.Packet;
import org.tio.core.stat.IpStat;
import org.tio.core.stat.IpStatListener;
import org.tio.utils.json.Json;

@Slf4j
@Component
@TioServerIpStatListener
public class CustomizeServerIpStatListener implements IpStatListener {
    @Override
    public void onExpired(TioConfig tioConfig, IpStat ipStat) {
    }
    @Override
    public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect, IpStat ipStat) throws Exception {
    }
    @Override
    public void onDecodeError(ChannelContext channelContext, IpStat ipStat) {
        log.info("onDecodeError channelContext:{}, ipStat:{}", channelContext, Json.toJson(ipStat));
    }
    @Override
    public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess, IpStat ipStat) throws Exception {
    }
    @Override
    public void onAfterDecoded(ChannelContext channelContext, Packet packet, int packetSize, IpStat ipStat) throws Exception {
    }
    @Override
    public void onAfterReceivedBytes(ChannelContext channelContext, int receivedBytes, IpStat ipStat) throws Exception {
        log.info("onAfterReceivedBytes channelContext:{}, receivedBytes:{}, ipStat:{}", channelContext, receivedBytes, Json.toJson(ipStat));
    }
    @Override
    public void onAfterHandled(ChannelContext channelContext, Packet packet, IpStat ipStat, long cost) throws Exception {

    }

}
