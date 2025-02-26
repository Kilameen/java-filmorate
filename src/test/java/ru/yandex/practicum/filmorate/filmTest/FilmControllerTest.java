package ru.yandex.practicum.filmorate.filmTest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Marker;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {

	private static Validator validator;
	@Autowired
	private FilmController filmController;
	@Autowired
	private UserController userController;
	User user;
	Film film;
	private static final LocalDate STARTED_REALISE_DATE = LocalDate.of(1895, 12, 28);

	@BeforeEach
	void setUp() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
		film = new Film();
		film.setName("Test Film");
		film.setDescription("Test Description");
		film.setReleaseDate(LocalDate.of(2025, 1, 1));
		film.setDuration(120);

		user = new User();
		user.setName("TestName");
		user.setLogin("TestLogin");
		user.setEmail("test@yandex.ru");
		user.setBirthday(LocalDate.of(1993, 1, 25));
	}

	@AfterEach
	void setDown() {
		filmController.deleteAllFilm(film);
		userController.deleteAllUser(user);
	}

	@Test
	void filmControllerCreatesCorrectFilm() {
		filmController.create(film);
		Collection<Film> films = filmController.findAll();
		assertEquals(1, films.size(), "Контроллер не создал фильм");
		assertEquals("Test Film", films.iterator().next().getName(), "Контроллер создал некорректный фильм");
	}

	@Test
	void filmControllerRejectsDuplicateFilms() {
		filmController.create(film);

		Film film2 = new Film();
		film2.setName("Test Film");
		film2.setDescription("Test Description_2");
		film2.setReleaseDate(LocalDate.of(2025, 1, 1));
		film2.setDuration(120);


		Collection<Film> films = filmController.findAll();
		assertEquals(1, films.size(), "Контроллер не создал фильм");
		DuplicatedDataException thrown = assertThrows(
				DuplicatedDataException.class,
				() -> filmController.create(film2),
				"Контроллер не выкинул исключение о дубликате фильма"
		);
		assertTrue(thrown.getMessage().contains("Фильм с таким названием и датой релиза уже существует"));
	}

	@Test
	void filmValidatesBlankName() {
		film.setName("");
		Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnCreate.class);
		assertEquals(1, violations.size(), "Не пройдена валидация на пустое название");
	}

	@Test
	void filmValidatesNullName() {
		Film film = new Film();
		film.setDescription("Test Description");
		film.setReleaseDate(LocalDate.of(2025, 1, 1));
		film.setDuration(120);
		Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnCreate.class);
		assertEquals(1, violations.size(), "Не пройдена валидация на null название");
	}

	@Test
	void filmValidatesLongDescription() {
		film.setDescription("a".repeat(201));
		Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnCreate.class);
		assertEquals(1, violations.size(), "Не пройдена валидация на слишком длинное описание");
	}

	@Test
	void filmValidatesNegativeDuration() {
		film.setDuration(-1);;
		Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnCreate.class);
		assertEquals(1, violations.size(), "Не пройдена валидация на отрицательную продолжительность");
	}

	@Test
	void filmValidatesReleaseDate() {
		film.setReleaseDate(LocalDate.of(1890, 1, 1));
		assertThrows(ValidationException.class, () -> filmController.create(film), "Дата релиза фильма не может быть раньше: " + STARTED_REALISE_DATE);
	}

	@Test
	void userLikesTheFilm() {
		filmController.create(film);
		userController.create(user);
		filmController.addLike(film.getId(), user.getId());
		Film findFilm = filmController.getFilmById(film.getId());
		assertTrue(findFilm.getLikes().contains(user.getId()), "Контроллер не поставил лайк пользователя");
	}

	@Test
	void userDeleteLikesTheFilm() {
		filmController.create(film);
		userController.create(user);
		filmController.addLike(film.getId(), user.getId());
		filmController.deleteLike(film.getId(), user.getId());
		Film findFilm = filmController.getFilmById(film.getId());
		assertTrue(findFilm.getLikes().isEmpty(), "Контроллер не удалил лайк пользователя");
	}

	@Test
	void testPopularFilm() {
		filmController.create(film);

		Film film1 = new Film();
		film1.setName("Test Film1");
		film1.setDescription("Test Description1");
		film1.setReleaseDate(LocalDate.of(2024, 1, 1));
		film1.setDuration(120);
		filmController.create(film1);

		Film film2 = new Film();
		film2.setName("Test Film2");
		film2.setDescription("Test Description2");
		film2.setReleaseDate(LocalDate.of(2023, 1, 1));
		film2.setDuration(120);
		filmController.create(film2);

		userController.create(user);
		User user1 = new User();
		user1.setName("TestName1");
		user1.setLogin("TestLogin1");
		user1.setEmail("tests@yandex.ru");
		user1.setBirthday(LocalDate.of(1992, 1, 25));
		userController.create(user1);

		User user2 = new User();
		user2.setName("TestName2");
		user2.setLogin("TestLogin2");
		user2.setEmail("testy@yandex.ru");
		user2.setBirthday(LocalDate.of(1991, 1, 25));
		userController.create(user2);

		filmController.addLike(film.getId(), user.getId());
		filmController.addLike(film.getId(), user1.getId());
		filmController.addLike(film1.getId(), user2.getId());

		Collection<Film> films = filmController.getPopularFilms(10L);
		assertEquals(3, films.size(), "Неверное колличество популярных фильмов");
		assertEquals("Test Film", films.stream().findFirst().get().getName(), "Первый фильм - Test Film");
		Optional<Film> secondFilm = films.stream().skip(1).findFirst();
		assertTrue(secondFilm.isPresent(), "Второй фильм отсутствует");
		assertEquals("Test Film1", secondFilm.get().getName(), "Второй фильм - Test Film1");
	}
}