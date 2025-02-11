package szlicht.daniel.calendar.common.calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Configuration
public class GoogleCalendarTokenLoader {
    private static final String APPLICATION_NAME = "calendar-spring";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${spring.calendar.credentials}")
    private String credentials;
    @Value("${spring.calendar.account-mail}")
    private String accountMail;

    private OauthTokenRepository oauthTokenRepository;

    public GoogleCalendarTokenLoader(OauthTokenRepository oauthTokenRepository) {
        this.oauthTokenRepository = oauthTokenRepository;
    }

    private Credential getCredential() throws IOException, GeneralSecurityException {
        OauthToken oauthToken = oauthTokenRepository.findByUserId(accountMail);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new StringReader(credentials));
        NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(transport)
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(clientSecrets)
                .build()
                .setRefreshToken(oauthToken.getRefreshToken())
                .setAccessToken(oauthToken.getAccessToken());
        if (oauthToken.getAccessTokenExpiresAt() == null ||
                oauthToken.getAccessTokenExpiresAt() < Instant.now().getEpochSecond()) {
            if (credential.refreshToken()) {
                String newAccessToken = credential.getAccessToken();
                oauthToken.setAccessToken(newAccessToken);
                oauthToken.setAccessTokenExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS).getEpochSecond());
                oauthTokenRepository.save(oauthToken);
            } else {
                throw new RuntimeException("Nie udało się odświeżyć tokena!");
            }
        }
        return credential;
    }

    @Bean
    public Calendar getCalendarService() throws IOException, GeneralSecurityException {
        NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = getCredential();
        return new Calendar.Builder(transport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
