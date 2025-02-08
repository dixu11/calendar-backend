package szlicht.daniel.calendar.common.calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

@Configuration
public class GoogleCalendarClient {
    private static final String APPLICATION_NAME = "calendar-spring";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

    @Value("${meeting.mail.bot}")
    private String fromEmail;
    @Value("${spring.calendar.credentials}")
    private String credentials;

    //configuration -----
    private Credential getCredentials(NetHttpTransport httpTransport) throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new StringReader(credentials));

        GoogleAuthorizationCodeFlow flow =  new GoogleAuthorizationCodeFlow.Builder(
                httpTransport,JSON_FACTORY,clientSecrets,SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(8080)
                .setCallbackPath("/oauth2/callback")
                .build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    @Bean
    public  Calendar getCalendarService() throws IOException, GeneralSecurityException {
        NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = getCredentials(transport);
        return new Calendar.Builder(transport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    //some utility methods  --------

    public static DateTime toDateTime(LocalDateTime localDateTime) {
        return new DateTime(localDateTime.toEpochSecond(ZoneOffset.UTC)*1000);
    }

    public static EventDateTime toEventDateTime(LocalDateTime localDateTime) {
        return new EventDateTime()
                .setDateTime(toDateTime(localDateTime.minusHours(1)))
                .setTimeZone("Europe/Warsaw");
    }

    public static LocalDateTime toLocalDateTime(DateTime dateTime) {
        return LocalDateTime.ofEpochSecond(dateTime.getValue() / 1000, 0, ZoneOffset.ofHours(1));
    }


}
