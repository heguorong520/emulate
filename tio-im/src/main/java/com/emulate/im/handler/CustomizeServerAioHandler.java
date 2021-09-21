package com.emulate.im.handler;

import com.emulate.im.processor.AbsServerProcessorFactory;
import com.emulate.im.processor.CommonProcessor;
import org.springframework.stereotype.Component;
import org.tio.common.starter.annotation.TioServerAioListener;
import org.tio.common.starter.annotation.TioServerMsgHandler;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioHandler;

@Component
@TioServerMsgHandler
public class CustomizeServerAioHandler extends AbsCommonAioHandler implements ServerAioHandler {
    private final
    AbsServerProcessorFactory serverAioFactory;

    public CustomizeServerAioHandler(AbsServerProcessorFactory serverAioFactory) {
        this.serverAioFactory = serverAioFactory;
    }

    @Override
    public void handler(Packet packet, ChannelContext channelContext) throws Exception {
        CommonProcessor processor = serverAioFactory.process(packet, channelContext);
    }
}
