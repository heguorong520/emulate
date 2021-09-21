package com.emulate.im.handler;

import com.emulate.im.packet.CustomizePacket;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;
import org.tio.core.TioConfig;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.AioHandler;
import org.tio.core.intf.Packet;

import java.nio.ByteBuffer;

@Component
public  abstract class AbsCommonAioHandler implements AioHandler {

    /**
     * 解码：把接收到的ByteBuffer，解码成应用可以识别的业务消息包
     * 消息头：bodyLength
     * 消息体：byte[]
     */
    @Override
    public CustomizePacket decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext) throws AioDecodeException {
        //可读数据，小于头部的固定长度，直接返回null，这样tio框架会自动把本次收到的数据暂存起来，并和下次收到的数据组合起来
        if (readableLength < CustomizePacket.HEADER_LENGTH) {
            return null;
        }

        //position的值不一定是0，但是
        int bodyLength = buffer.limit();
        CustomizePacket imPacket = new CustomizePacket();
        if (bodyLength > 0) {
            byte[] dst = new byte[bodyLength];
            buffer.get(dst);
            imPacket.setBody(dst);
        }
        return imPacket;

    }

    /**
     * 编码：把业务消息包编码为可以发送的ByteBuffer
     * 消息头：bodyLength
     * 消息体：byte[]
     */
    @Override
    public ByteBuffer encode(Packet packet, TioConfig tioConfig, ChannelContext channelContext) {
        CustomizePacket spPacket = (CustomizePacket) packet;
        byte[] body = spPacket.getBody();
        int bodyLen = 0;
        if (body != null) {
            bodyLen = body.length;
        }

        ByteBuffer buffer = ByteBuffer.allocate(bodyLen);

        //写入消息体
        if (body != null) {
            buffer.put(body);
        }
        return buffer;
    }


}
