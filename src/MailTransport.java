
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailTransport {

    public static final String USE_SSL = "1";
    public static final int REPLY_SUCCESSFUL = 0;
    public static final int REPLY_FAILED = 1;

    // get mail gw configuration
    mailgw mail = mailgw.loadConfig();

    private Session session;
    private Transport transport;
    private Logger log ;

    public MailTransport(Logger log) throws Exception {
        log.info("Starting create mail transport ... ");
        Long currentTimeInMilis = System.currentTimeMillis();

        this.log = log;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtps");
        props.setProperty("mail.smtps.host", mail.getIp());
        props.setProperty("mail.smtps.auth", "true");
        //props.setProperty("mail.debug", "true");
        props.setProperty("mail.smtps.port", mail.getPort());
        props.setProperty("mail.smtps.socketFactory.port", mail.getPort());
        props.setProperty("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtps.socketFactory.fallback", "false");

        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {

                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                mail.getUsername(), mail.getPassword());
                    }
                }
        );

        transport = session.getTransport();
        transport.connect();
        log.info("Finished creating mail transport in " + (System.currentTimeMillis() - currentTimeInMilis) + "ms");
    }

    public int sendMail(Long sendId, String[] receiver, String subject, String content, File[] fileList, Date nowDate) {
        log.info("Send mail with send_id = " + sendId);
        Long currentTimeInMilis = System.currentTimeMillis();
        int result;
        try {
            // Create new mail
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mail.getUsername()));
            message.setSubject(subject);
            message.setHeader("X-Mailer", "ViettelMailer");
            message.setHeader("Content-type", "text/html; charset=utf-8");
            //message.setContent(mailContent, "text/html;charset=\"utf-8\"");
            message.setSentDate(nowDate);

            // Set content
            Multipart multipart = new MimeMultipart();

            MimeBodyPart mbp = new MimeBodyPart();
            //mbp.setText(mailContent, "utf-8");
            mbp.setContent(content, "text/html;charset=\"utf-8\"");
            multipart.addBodyPart(mbp);

            if (fileList != null) {
                for (int i = 0; i < fileList.length; i++) {
                    if (fileList[i] != null && fileList[i].exists()) {
                        mbp = new MimeBodyPart();
                        DataSource dataSource = new FileDataSource(fileList[i]);
                        mbp.setDataHandler(new DataHandler(dataSource));
                        mbp.setFileName(fileList[i].getName());
                        multipart.addBodyPart(mbp);
                    }
                }
            }

            //message.setContent(multipart, "text/html;charset=\"utf-8\"");
            message.setContent(multipart);
            // End set content

            // Set recipient
            if (receiver != null) {
                for (int i = 0; i < receiver.length; i++) {
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver[i]));
                }
            }

            // Send message
            transport.sendMessage(message, message.getAllRecipients());
            result = REPLY_SUCCESSFUL;

        } catch (Exception ex) {
            log.error("Exception when send transport: ", ex);
            result = REPLY_FAILED;
        }
        log.info("Send mail with send_id = " + sendId + " result = " + result + " in " + (System.currentTimeMillis() - currentTimeInMilis) + "ms");
        return result;
    }

    public void close() {
        log.info("Close mail transport ");
        Long currentTimeInMilis = System.currentTimeMillis();
        try {
            if (transport != null && transport.isConnected()) {
                transport.close();
                transport = null;
            }
        } catch (Exception ex) {
            log.error("Exception when close mail transport: ", ex);
        }
        log.info("Close mail transport in " + (System.currentTimeMillis() - currentTimeInMilis) + "ms");
    }
}
