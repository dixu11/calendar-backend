package szlicht.daniel.calendar.common;

import jakarta.mail.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.util.Properties;

@Configuration
@EnableIntegration
public class ReadingMailIntegration {
    @Bean
    public Session mailSession(){
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        return Session.getInstance(props);
    }


   /* @Bean
    public IMAPIdleChannelAdapter mailAdapter(Session session) {
        IMAPIdleChannelAdapter adapter = new IMAPIdleChannelAdapter();
        adapter.setStoreUri("imaps://<username>:<app_password>@imap.gmail.com/INBOX");
        adapter.setShouldMarkMessagesAsRead(false);
        adapter.setShouldDeleteMessages(false);
        adapter.setAutoCloseFolder(false);
        // Domyślnie adapter sprawdza folder w trybie IDLE,
        // więc "w locie" wykrywa nowe wiadomości.
        adapter.setOutputChannel(mailChannel());
        return adapter;
    }*/

    @Bean
    public MessageChannel mailChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mailChannel")
    public MessageHandler mailHandler() {
        return message -> {
            // Tu odbierasz wiadomości; "payload" to np. MimeMessage
            Object payload = message.getPayload();
            System.out.println("New mail received! " + payload);
        };
    }
}
