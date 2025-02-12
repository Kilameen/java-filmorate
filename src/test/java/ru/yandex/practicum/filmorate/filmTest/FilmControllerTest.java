package ru.yandex.practicum.filmorate.filmTest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = FilmController.class)
class FilmControllerTest {
	private static Validator validator;
	private FilmController filmController;
	private Film film;

	@BeforeEach
	void setUp() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
		filmController = new FilmController();
		film = new Film();
		film.setName("Test Film");
		film.setDescription("Test Description");
		film.setReleaseDate(LocalDate.of(2025, 1, 1));
		film.setDuration(120);
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
		Set<ConstraintViolation<Film>> violations = validator.validate(film);
		assertEquals(1, violations.size(), "Не пройдена валидация на пустое название");
	}

	@Test
	void filmValidatesNullName() {
		Film film = new Film();
		film.setDescription("Test Description");
		film.setReleaseDate(LocalDate.of(2025, 1, 1));
		film.setDuration(120);
		Set<ConstraintViolation<Film>> violations = validator.validate(film);
		assertEquals(2, violations.size(), "Не пройдена валидация на null название");
	}

	@Test
	void filmValidatesLongDescription() {
		film.setDescription("a".repeat(201));
		Set<ConstraintViolation<Film>> violations = validator.validate(film);
		assertEquals(1, violations.size(), "Не пройдена валидация на слишком длинное описание");
	}

	@Test
	void filmValidatesNegativeDuration() {
		film.setDuration(-1);
		Set<ConstraintViolation<Film>> violations = validator.validate(film);
		assertEquals(1, violations.size(), "Не пройдена валидация на отрицательную продолжительность");
	}

	@Test
	void filmValidatesReleaseDate() {
		film.setReleaseDate(LocalDate.of(1890, 1, 1));
		assertThrows(ValidationException.class, () -> filmController.create(film), "Не пройдена валидация, дата релиза не может быть раньше 28.12.1895");
	}
}