package szlicht.daniel.calendar.common.mail;

import org.eclipse.angus.mail.imap.IMAPMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.mail.ImapIdleChannelAdapter;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Configuration
@EnableIntegration
public class ReadingMailIntegration {

    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    private MailResponder responder;

    public ReadingMailIntegration(MailResponder responder) {
        this.responder = responder;
    }

    @Bean
    public ImapMailReceiver imapMailReceiver() {
        String encodedUser = URLEncoder.encode(username, StandardCharsets.UTF_8);
        String encodedPass = URLEncoder.encode(password, StandardCharsets.UTF_8);
        String storeUri = String.format("imaps://%s:%s@imap.gmail.com/INBOX", encodedUser, encodedPass);
        ImapMailReceiver receiver = new ImapMailReceiver(storeUri);
        receiver.setShouldMarkMessagesAsRead(false);
        receiver.setShouldDeleteMessages(false);
        receiver.setAutoCloseFolder(false);
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
//        props.setProperty("mail.debug", "true");
        props.setProperty("mail.imaps.ssl.trust", "*");
        receiver.setJavaMailProperties(props);
        return receiver;
    }

    @Bean
    public ImapIdleChannelAdapter mailAdapter(ImapMailReceiver imapMailReceiver) {
        ImapIdleChannelAdapter adapter = new ImapIdleChannelAdapter(imapMailReceiver);
        adapter.setAutoStartup(true);
        adapter.setOutputChannel(mailChannel());
        adapter.setReconnectDelay(10000L); //10 sec reconnect instead of 1 min for debug
        return adapter;
    }

    @Bean
    public MessageChannel mailChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mailChannel")
    public MessageHandler mailHandler() {
        return message -> {
            IMAPMessage payload = (IMAPMessage) message.getPayload();
            responder.respondToMail(payload);
        };
    }
}
