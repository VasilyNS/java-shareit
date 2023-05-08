package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.tools.Validator;
import ru.practicum.shareit.tools.exception.ObjectNotFoundException;
import ru.practicum.shareit.tools.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private List<User> users = new ArrayList<>();
    private int count = 1;

    public User create(User user) {
        userDubValidation(user);
        User newUser = new User(count, user.getName(), user.getEmail());
        users.add(newUser);
        count++;
        log.info("End of User creation: " + newUser.toString());
        return newUser;
    }

    public User update(int id, User user) {
        for (User u : users) {
            if (u.getId() == id) {
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
            throw new ObjectNotFoundException("No User with id=" + id);
        }
        return user;
    }

    public User get(int id) {
        for (User u : users) {
            if (u.getId() == id) {
                log.info("End of User getting: " + u.toString());
                return u;
            }
        }
        throw new ObjectNotFoundException("No User with id=" + id);
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    public void del(int id) {
        if (get(id) != null) {
            User delItm = null;
            for (User u : users) {
                if (u.getId() == id) {
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
            if (u.getEmail().equals(user.getEmail()) && (u.getId() != user.getId())) {
                throw new ValidationException("Duplicate email! user_id=" +
                        u.getId() + ", email= " + user.getEmail());
            }
        }
    }

}
