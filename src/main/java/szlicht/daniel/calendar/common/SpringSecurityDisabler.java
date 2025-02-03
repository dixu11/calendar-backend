package szlicht.daniel.calendar.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration //comment to disable
public class SpringSecurityDisabler {

    @Bean
    SecurityFilterChain configureChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(customizer -> customizer.anyRequest().permitAll())
                .build();
    }
}
