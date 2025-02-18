package szlicht.daniel.calendar.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import szlicht.daniel.calendar.common.spring.WarningLogger;

@Configuration //comment to disable
public class SpringSecurityDisabler {
    private int counter = 0;
    private WarningLogger warningLogger;

    public SpringSecurityDisabler(WarningLogger warningLogger) {
        this.warningLogger = warningLogger;
    }

    @Bean
    SecurityFilterChain configureChain(HttpSecurity http) throws Exception {
        counter++;
        if (counter > 20) {
            warningLogger.notifyOwner("so many new students? Probably attack. Disabling app", "", true);
        }
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(customizer -> customizer.anyRequest().permitAll())
                .build();
    }
}
