package io.netty.handler.codec.smtp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:hoffmann@apache.org">Juergen Hoffmann</a>.
 *         Date: 23-Aug-2014
 */
public class SmtpClientEncoderTest {

    @Test
    public void testHeloEncoding() {
        SmtpClientEncoder encoder = new SmtpClientEncoder();

        SmtpClientMessage message = new SmtpClientMessage(SmtpCommand.HELO, "mail.example.com");
        EmbeddedChannel ch = new EmbeddedChannel(new SmtpClientEncoder());

        ByteBuf buffer = Unpooled.buffer(64);
        try {
            encoder.encode(null, message, buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String msg = buffer.toString(Charset.forName("US-ASCII"));
        assertEquals("HELO mail.example.com\r\n", msg);    }
}
