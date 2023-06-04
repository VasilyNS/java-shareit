package ru.practicum.shareit.user;

import lombok.*;

import javax.persistence.*;

/**
 * Сущность пользователя
 * Использование тут '@Data' - не рекомендуется
 * без @NoArgsConstructor JPA не работает
 * '@ToString' также можно использовать на усмотрение
 */
@Entity
@Table(name = "users", schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof User)) return false;
//        return id != null && id.equals(((User) o).getId());
//    }
//
//    @Override
//    public int hashCode() {
//        return getClass().hashCode();
//    }

}
