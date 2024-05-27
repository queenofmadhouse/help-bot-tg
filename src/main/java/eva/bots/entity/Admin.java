package eva.bots.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.Table;

@SuperBuilder
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admins", schema = "cms_entity")
public class Admin extends User {

    @Column(name = "telegram_user_id")
    protected String telegramUserId;
}
