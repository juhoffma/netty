package io.netty.handler.codec.smtp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.smtp.SmtpConstants.CR;
import static io.netty.handler.codec.smtp.SmtpConstants.LF;
import static io.netty.handler.codec.smtp.SmtpConstants.SP;

/**
 * @author <a href="mailto:hoffmann@apache.org">Juergen Hoffmann</a>.
 *         Date: 24-Aug-2014
 */
public class SmtpClientEncoder extends MessageToByteEncoder<SmtpClientMessage> {

    /**
     * Encode a message into a {@link io.netty.buffer.ByteBuf}. This method will be called for each written message that can be handled
     * by this encoder.
     *
     * @param ctx the {@link io.netty.channel.ChannelHandlerContext} which this {@link io.netty.handler.codec.MessageToByteEncoder} belongs to
     * @param msg the message to encode
     * @param out the {@link io.netty.buffer.ByteBuf} into which the encoded message will be written
     * @throws Exception is thrown if an error accour
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, SmtpClientMessage msg, ByteBuf out) throws Exception {
        msg.getSmtpCommand().encode(out);
        out.writeByte(SP);
        out.writeBytes(msg.getMessage().getBytes(CharsetUtil.ISO_8859_1));
        out.writeByte(CR);
        out.writeByte(LF);
    }
}
