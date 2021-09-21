package com.emulate.im.processor;

import com.emulate.im.packet.CustomizePacket;
import org.tio.core.ChannelContext;

public class CustomizeReqProcessor implements CommonProcessor {
    CustomizeReqProcessor(CustomizePacket packet, ChannelContext channelContext) throws Exception {
        this.process(packet, channelContext);
    }

    /**
     * 处理消息
     */
    @Override
    public void process(CustomizePacket packet, ChannelContext channelContext) throws Exception {
        String msg = new String(packet.getBody(), "utf-8");
        System.out.println("服务端收到消息:" + msg);
    }

}
