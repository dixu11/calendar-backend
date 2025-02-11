package szlicht.daniel.calendar.common.calendar;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthTokenRepository extends JpaRepository<OauthToken, Integer> {
    OauthToken findByUserId(String userId);
}
