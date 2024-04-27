package cbc.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @Author Cbc
 * @DateTime 2024/4/24 19:24
 * @Description LTC解码器
 */
public class ProtocolFrameDecoder extends LengthFieldBasedFrameDecoder {

    public ProtocolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    public ProtocolFrameDecoder(){
        super(1024, 0, 4, 0, 0);

    }
}
