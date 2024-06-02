package eva.bots.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requests", schema = "cms_entity")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_chat_id")
    private Long tgChatId;

    @Column(name = "telegram_user_name")
    private String userName;

    @Column(name = "telegram_user_pronouns")
    private String userPronouns;

    @Column(name = "request_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime requestDate;

    @Column(name = "request_text")
    private String requestText;

    @Column(name = "is_urgent")
    private boolean isUrgent;

    @Column(name = "related_to")
    private Long relatedAdminId;

    @Column(name = "in_work")
    private boolean inWork;

    @Column(name = "in_the_archive")
    private boolean inTheArchive;
}
