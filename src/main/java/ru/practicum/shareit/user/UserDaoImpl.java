package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.tools.Validator;
import ru.practicum.shareit.tools.exception.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private List<User> users = new ArrayList<>();
    private Long count = 1L;

    public User create(User user) {
        userDubValidation(user);
        User newUser = new User(count, user.getName(), user.getEmail());
        users.add(newUser);
        count++;
        log.info("End of User creation: " + newUser.toString());
        return newUser;
    }

    public User update(Long id, User user) {
        for (User u : users) {
            if (u.getId().equals(id)) {
                if (user.getName() != null) {
                    u.setName(user.getName());
                }
                if (user.getEmail() != null) {
                    if (!u.getEmail().equals(user.getEmail())) {
                        userDubValidation(user);
                    }
                    u.setEmail(user.getEmail());
                }
                log.info("End of User updating: " + u.toString());
                return u;
            }
            throw new UserNotFoundException(id);
        }
        return user;
    }

    public User get(Long id) {
        for (User u : users) {
            if (u.getId().equals(id)) {
                log.info("End of User getting: " + u.toString());
                return u;
            }
        }
        throw new UserNotFoundException(id);
    }

    public List<User> getAll() {
        log.info("End of User getAll");
        return new ArrayList<>(users);
    }

    public void del(Long id) {
        if (get(id) != null) {
            User delItm = null;
            for (User u : users) {
                if (u.getId().equals(id)) {
                    delItm = u;
                }
            }
            if (delItm != null) {
                users.remove(delItm);
            }
            log.info("End of User deleting, id=" + id);
        }
    }

    /**
     * Этот метод еще здесь, чтобы обращаться локально к users
     * В потом будет хранение данных в БД и метод будет перемещен в валидацию
     */
    private void userDubValidation(User user) {
        Validator.userEmailValidation(user);
        for (User u : users) {
            if (u.getEmail().equals(user.getEmail()) && (!u.getId().equals(user.getId()))) {
                throw new ValidationException("Duplicate email! user_id=" +
                        u.getId() + ", email= " + user.getEmail());
            }
        }
    }

}
