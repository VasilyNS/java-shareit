package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.tools.Validator;
import ru.practicum.shareit.tools.exception.UserNotFoundException;
import ru.practicum.shareit.tools.exception.ValidationException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    public User saveUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        Validator.userEmailValidation(user);

        User userForReturn = new User();
        try {
            userForReturn = userRepository.save(user);
        } catch (Exception e) {
            // Пример обработки ошибок записи в SQL, таких, как дубль вызывающий primary key violation
            throw new ValidationException("Duplicate email! email= " + user.getEmail());
        }
        return userForReturn;

        /* В этом блоке была бы нормальная проверка на дубль email, но мы делаем это через БД, чтобы id крутился дальше
        User u = userRepository.findByEmail(user.getEmail());
        if (u == null) {
            return userRepository.save(user);
        } else {
            throw new ValidationException("Duplicate email! user_id=" +
                    user.getId() + ", email= " + user.getEmail());
        } */
    }

    @Transactional
    public User updateUser(Long id, UserDto userDto) {

        // Проверка email на корректность и не дубликат
        User userForCheck = UserMapper.toUser(userDto);
        if (StringUtils.hasText(userForCheck.getEmail())) {
            Validator.userEmailValidation(userForCheck);
            User userForEmailCheck = userRepository.findByEmail(userForCheck.getEmail());
            if (userForEmailCheck != null) {
                if (!userForEmailCheck.getId().equals(id)) {
                    throw new ValidationException("Trying to update with duplicate email! user_id=" +
                            id + ", email= " + userForCheck.getEmail());
                }
            }
        }

        // Проверка что пользователь c таким id есть
        User user = checkUserExist(id);

        // Обновляем имя, если оно есть
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        // Обновляем эл. почту, если она есть
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return checkUserExist(id);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUser(Long id) {
        checkUserExist(id);
        userRepository.deleteById(id);
    }

    /**
     * Проверка, что пользователь существует,
     * если нет - исключение, если да - возврат его самого
     */
    public User checkUserExist(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

}
