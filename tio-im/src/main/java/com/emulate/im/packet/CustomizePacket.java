package com.emulate.im.packet;

import lombok.Data;
import org.tio.core.intf.Packet;

@Data
public class CustomizePacket extends Packet {

    private byte[] body;

    public static final int HEADER_LENGTH = 4;
}
