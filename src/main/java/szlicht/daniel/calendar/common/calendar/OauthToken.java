package szlicht.daniel.calendar.common.calendar;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
@Entity
public class OauthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    //    @Column(unique = true,nullable = false)
    private String userId;
    //    @Column(columnDefinition = "TEXT",nullable = false)
    private String refreshToken;
    //    @Column
    private String accessToken;
    //    @Column
    private long accessTokenExpiresAt;

    OauthToken() {
    }

    public OauthToken(String userId, String refreshToken, String accessToken, long accessTokenExpiresAt) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
    }

    public int getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getAccessTokenExpiresAt() {
        return accessTokenExpiresAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setAccessTokenExpiresAt(long accessTokenExpiresAt) {
        this.accessTokenExpiresAt = accessTokenExpiresAt;
    }

    @Override
    public String toString() {
        return "OauthToken{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", accessTokenExpiresAt=" + accessTokenExpiresAt +
                '}';
    }
}
