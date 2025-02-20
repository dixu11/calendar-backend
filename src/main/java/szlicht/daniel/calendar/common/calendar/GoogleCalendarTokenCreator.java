package szlicht.daniel.calendar.common.calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.*;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

@Configuration
public class GoogleCalendarTokenCreator {
    private static final String APPLICATION_NAME = "calendar-spring";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

    @Value("${spring.calendar.credentials}")
    private String credentials;
    @Value("${spring.calendar.account-mail}")
    private String accountMail;

    private OauthTokenRepository oauthTokenRepository;

    public GoogleCalendarTokenCreator(OauthTokenRepository oauthTokenRepository) {
        this.oauthTokenRepository = oauthTokenRepository;
    }

    //configuration -----
    private Credential getCredentials(NetHttpTransport httpTransport) throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new StringReader(credentials));

        GoogleAuthorizationCodeFlow flow =  new GoogleAuthorizationCodeFlow.Builder(
                httpTransport,JSON_FACTORY,clientSecrets,SCOPES)
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(8080)
                .setCallbackPath("/oauth2/callback")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        OauthToken token = new OauthToken();
        token.setUserId(accountMail);
        token.setRefreshToken(credential.getRefreshToken());
        token.setAccessToken(credential.getAccessToken());
        token.setAccessTokenExpiresAt(Instant.now().getEpochSecond() + credential.getExpiresInSeconds());
        System.out.println("expires in:");
        System.out.println(Instant.now().until(Instant.ofEpochSecond(token.getAccessTokenExpiresAt()), ChronoUnit.MINUTES));
        System.out.println("minutes");
        oauthTokenRepository.deleteAll();
        oauthTokenRepository.save(token);
        return credential;
    }

    @Bean
    public  Calendar getCalendarService() throws IOException, GeneralSecurityException {
        NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = getCredentials(transport);
        return new Calendar.Builder(transport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
