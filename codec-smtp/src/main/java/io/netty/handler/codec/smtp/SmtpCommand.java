package io.netty.handler.codec.smtp;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

/**
 * @author <a href="mailto:hoffmann@apache.org">Juergen Hoffmann</a>.
 *         Date: 21-Aug-2014
 */
public class SmtpCommand {

    public static final SmtpCommand HELO = new SmtpCommand("HELO", true);

    public static final SmtpCommand EHLO = new SmtpCommand("EHLO", true);

    public static final SmtpCommand MAIL = new SmtpCommand("MAIL", true);

    public static final SmtpCommand RCPT = new SmtpCommand("RCPT", true);

    public static final SmtpCommand DATA = new SmtpCommand("DATA", true);

    public static final SmtpCommand EXPN = new SmtpCommand("EXPN", true);

    public static final SmtpCommand VRFY = new SmtpCommand("VRFY", true);

    public static final SmtpCommand SEND = new SmtpCommand("SEND", true);

    public static final SmtpCommand SOML = new SmtpCommand("SOML", true);

    public static final SmtpCommand SAML = new SmtpCommand("SAML", true);

    public static final SmtpCommand QUIT = new SmtpCommand("QUIT", true);

    public static final SmtpCommand RSET = new SmtpCommand("RSET", true);

    public static final SmtpCommand HELP = new SmtpCommand("HELP", true);

    public static final SmtpCommand NOOP = new SmtpCommand("NOOP", true);

    public static final SmtpCommand TURN = new SmtpCommand("TURN", true);


    private final String command;
    private final byte[] bytes;

    private SmtpCommand(String command, boolean bytes) {
        this.command = command;
        if (bytes) {
            this.bytes = command.getBytes(CharsetUtil.US_ASCII);
        } else {
            this.bytes = null;
        }
    }

    void encode(ByteBuf buf) {
        buf.writeBytes(bytes);
    }
}
