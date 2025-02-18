package szlicht.daniel.calendar.mail_dialog.infrastructure;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "start_messages")
public class StartMessageEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String nick;
    private String name;
    @Column(unique = true)
    private String email;
    @Lob
    private String story;
    private LocalDateTime created;

    public StartMessageEntity() {
    }

    public StartMessageEntity(String nick, String name, String email, String story, LocalDateTime created) {
        this.nick = nick;
        this.name = name;
        this.email = email;
        this.story = story;
        this.created = created;
    }
    
    
}
