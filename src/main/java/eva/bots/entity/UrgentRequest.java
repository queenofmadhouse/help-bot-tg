package eva.bots.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "urgent_requests", schema = "cms_entity")
public class UrgentRequest {

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

    @Column(name = "is_solved")
    private boolean isSolved;
}
