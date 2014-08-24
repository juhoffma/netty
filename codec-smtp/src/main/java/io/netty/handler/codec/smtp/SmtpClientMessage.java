package io.netty.handler.codec.smtp;

/**
 * @author <a href="mailto:hoffmann@apache.org">Juergen Hoffmann</a>.
 *         Date: 23-Aug-2014
 */
public class SmtpClientMessage {

    private SmtpCommand smtpCommand;

    private String message;

    public SmtpCommand getSmtpCommand() {
        return smtpCommand;
    }

    public String getMessage() {
        return message;
    }

    public SmtpClientMessage(SmtpCommand smtpCommand, String message)
    {
        if(smtpCommand == null)
        {
            throw new IllegalArgumentException("smtp command must not be null");
        }

        if(message == null)
        {
            throw new IllegalArgumentException("smtp message must not be null");
        }

        this.smtpCommand = smtpCommand;
        this.message = message;

    }
}
