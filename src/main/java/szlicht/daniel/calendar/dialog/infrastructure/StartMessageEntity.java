package szlicht.daniel.calendar.dialog.infrastructure;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "start_messages")
public class StartMessageEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true)
    private String email;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String story;
    private LocalDateTime created;

    public StartMessageEntity() {
    }

    public StartMessageEntity(String name, String email, String story, LocalDateTime created) {
        this.name = name;
        this.email = email;
        this.story = story;
        this.created = created;
    }
    
    
}
