package com.emulate.im.processor;

import com.emulate.im.packet.CustomizePacket;
import org.tio.core.ChannelContext;

public interface CommonProcessor {
    void process(CustomizePacket packet, ChannelContext channelContext) throws Exception;
}
