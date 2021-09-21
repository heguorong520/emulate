package com.emulate.im.processor;

import com.emulate.im.packet.CustomizePacket;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;

@Component
public class AbsServerProcessorFactory {
    public CommonProcessor process(Packet packet, ChannelContext channelContext) throws Exception {
        //可以在在這裏返回不同的業務的處理器
        CustomizePacket spPacket = (CustomizePacket) packet;
        return new CustomizeReqProcessor(spPacket,channelContext);
    }
}