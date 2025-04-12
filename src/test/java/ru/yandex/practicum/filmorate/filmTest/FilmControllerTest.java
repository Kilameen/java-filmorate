package ru.yandex.practicum.filmorate.filmTest;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.yandex.practicum.filmorate.utils.Reader;

@SpringBootTest
@AutoConfigureTestDatabase
class FilmControllerTest {

	private static Validator validator;
	@Autowired
	private FilmController filmController;
	@Autowired
	private UserController userController;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	User user;
	Film film;

	private static final LocalDate STARTED_REALISE_DATE = LocalDate.of(1895, 12, 28);

	@BeforeEach
	void setUp() {
		jdbcTemplate.update(Reader.readString("src/test/resources/drop.sql"));
		jdbcTemplate.update(Reader.readString("src/main/resources/schema.sql"));
		jdbcTemplate.update(Reader.readString("src/main/resources/data.sql"));
		validator = Validation.buildDefaultValidatorFactory().getValidator();
		film = new Film();
		film.setId(1L);
		film.setName("Test Film");
		film.setDescription("Test Description");
		film.setReleaseDate(LocalDate.of(2025, 1, 1));
		film.setDuration(120);

		Rating rating = new Rating(1L, "G");
		film.setMpa(rating);

		user = new User();
		user.setId(1L);
		user.setName("TestName");
		user.setLogin("TestLogin");
		user.setEmail("test@yandex.ru");
		user.setBirthday(LocalDate.of(1993, 1, 25));
	}

	@Test
	void filmControllerCreatesCorrectFilm() {
		filmController.create(film);

		Collection<Film> films = filmController.findAll();
		assertEquals(1, films.size(), "Контроллер не создал фильм");
		assertEquals("Test Film", films.iterator().next().getName(), "Контроллер создал некорректный фильм");
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
		film.setDuration(-1);
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

		assertEquals(1L, findFilm.getLikes(), "Контроллер не поставил лайк пользователя");
	}

	@Test
	void userDeleteLikesTheFilm() {
		filmController.create(film);
		userController.create(user);
		filmController.addLike(film.getId(), user.getId());
		filmController.deleteLike(film.getId(), user.getId());
		Film findFilm = filmController.getFilmById(film.getId());
		assertEquals(0L, findFilm.getLikes(), "Контроллер не удалил лайк пользователя");
	}

	@Test
	void testPopularFilm() {
		filmController.create(film);
		Rating rating1 = new Rating(2L, "PG");
		Film film1 = new Film();
		film1.setId(2L);
		film1.setName("Test Film1");
		film1.setDescription("Test Description1");
		film1.setReleaseDate(LocalDate.of(2024, 1, 1));
		film1.setDuration(120);
		film1.setMpa(rating1);
		filmController.create(film1);

		Film film2 = new Film();
		Rating rating2 = new Rating(3L, "PG-13");
		film2.setId(3L);
		film2.setName("Test Film2");
		film2.setDescription("Test Description2");
		film2.setReleaseDate(LocalDate.of(2023, 1, 1));
		film2.setDuration(120);
		film2.setMpa(rating2);
		filmController.create(film2);

		userController.create(user);

		User user1 = new User();
		user1.setId(2L);
		user1.setName("TestName1");
		user1.setLogin("TestLogin1");
		user1.setEmail("tests@yandex.ru");
		user1.setBirthday(LocalDate.of(1992, 1, 25));
		userController.create(user1);

		User user2 = new User();
		user2.setId(3L);
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