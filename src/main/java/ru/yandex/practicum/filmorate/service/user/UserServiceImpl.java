package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.dao.event.EventDao;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final FriendshipDao friendshipDao;
    private final LikeDao likeDao;
    private final FilmStorage filmStorage;
    private final EventDao eventDao;

    @Autowired
    public UserServiceImpl(@Qualifier("H2UserDb") UserStorage userStorage,
                           @Qualifier("H2FriendDb") FriendshipDao friendshipDao,
                           @Qualifier("H2LikeDb") LikeDao likeDao,
                           @Qualifier("H2FilmDb") FilmStorage filmStorage,
                           @Qualifier("H2EventDb") EventDao eventDao) {
        this.userStorage = userStorage;
        this.friendshipDao = friendshipDao;
        this.likeDao = likeDao;
        this.filmStorage = filmStorage;
        this.eventDao = eventDao;
    }

    @Override
    public void addFriend(Long userId, Long userFriendId) {
        try {
            friendshipDao.addFriend(userId, userFriendId);

        } catch (Exception ex) {
            throw new NotFoundException("Ошибка поиска пользователя");
        }
        eventDao.create(userId, "FRIEND", "ADD", userFriendId);
    }

    @Override
    public void deleteFriend(Long userId, Long userFriendId) {
        getUserById(userId);
        getUserById(userFriendId);

        friendshipDao.deleteFriend(userId, userFriendId);
        eventDao.create(userId, "FRIEND", "REMOVE", userFriendId);
    }

    @Override
    public Collection<User> getMutualFriends(Long userId, Long userFriendId) {
        return friendshipDao.getMutualFriends(userId, userFriendId);
    }

    @Override
    public User create(User user) {
        validate(user);
        return userStorage.create(user);
    }

    @Override
    public User update(User user) {
        User updateUser = userStorage.update(user);
        if (isNull(updateUser)) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        validate(user);
        return updateUser;
    }

    @Override
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    @Override
    public Collection<User> getAllUserFriends(Long id) {
        getUserById(id);
        return friendshipDao.getAllUserFriends(id);
    }

    @Override
    public Collection<Film> getRecommendation(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }

        Map<Long, Set<Long>> userWithLikes = likeDao.getAllUsersWithLikes();
        Set<Long> userLikeFilms = userWithLikes.getOrDefault(userId, Collections.emptySet());

        if (userLikeFilms.isEmpty()) {
            return Collections.emptyList();
        }

        userWithLikes.remove(userId);

        if (userWithLikes.isEmpty()) {
            return Collections.emptyList();
        }

        Optional<Map.Entry<Long, Set<Long>>> mostSimilarUser = userWithLikes.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .max(Comparator.comparingLong(entry -> intersectionSize(userLikeFilms, entry.getValue())));

        if (mostSimilarUser.isEmpty()) {
            return Collections.emptyList();
        }

        if (intersectionSize(userLikeFilms, mostSimilarUser.get().getValue()) == 0) {
            return Collections.emptyList();
        }

        Set<Long> similarUserLikes = mostSimilarUser.get().getValue();
        similarUserLikes.removeAll(userLikeFilms);

        return similarUserLikes.stream()
                .map(filmStorage::getFilm)
                .collect(Collectors.toList());
    }

    private long intersectionSize(Set<Long> set1, Set<Long> set2) {
        return set1.stream().filter(set2::contains).count();
    }

    private void validate(User user) {
        Collection<User> users = userStorage.findAll();

        if (users.stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()) && !Objects.equals(u.getId(), user.getId()))) {
            log.error("Email {} уже используется", user.getEmail());
            throw new DuplicatedDataException("Этот email уже используется");
        }

        if (users.stream()
                .anyMatch(u -> u.getLogin().equals(user.getLogin()) && !Objects.equals(u.getId(), user.getId()))) {
            log.error("Логин {} уже используется", user.getLogin());
            throw new DuplicatedDataException("Этот логин уже используется");
        }

        if (isNull(user.getLogin()) || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Login пуст, либо содержит в себе пробелы {}", user.getLogin());
            throw new ValidationException("Поле login должен быть заполнен и не должен содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения позже сегодняшней даты {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (isNull(user.getName()) || user.getName().isEmpty()) {
            log.warn("Вместо пустого имени присваивается логин");
            user.setName(user.getLogin());
        }
    }

    @Override
    public void deleteUserById(Long id) {
        userStorage.getUserById(id); // проверка, если не найдет, выкинет ошибку

        userStorage.deleteUserById(id);
    }

    @Override
    public List<Event> getUserEvents(Long userId) {
        userStorage.getUserById(userId);
        return eventDao.getUserEvents(userId);
    }
}