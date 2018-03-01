package com.kotwicka.funwithflagsmvp.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QuizTest {

    @Test
    public void shouldResetQuiz() {
        // given
        final String country = "Poland";
        final String country2 = "France";
        final Quiz quiz = new Quiz();
        quiz.setNumberOfChoices(5);
        quiz.isCorrectAnswer(country2);
        quiz.addCountry(country);
        quiz.addCountry(country2);
        quiz.addCountryToQuiz(country);

        // when
        quiz.resetQuiz();

        // then
        assertThat(quiz.getCountriesSize()).isZero();
        assertThat(quiz.getQuestionNumber()).isZero();
        assertThat(quiz.getNumberOfChoices()).isZero();
        assertThat(quiz.getNumberOfGuesses()).isZero();
        assertThat(quiz.getCorrectCountryName()).isEmpty();
    }

    @Test
    public void shouldSelectTheOnlyCountryAvailable() {
        // given
        final String country = "Africa-Burkina_Faso.png";
        final String countryName = "Burkina Faso";
        final Quiz quiz = new Quiz();
        quiz.addCountryToQuiz(country);

        // when
        final String selectedCountry = quiz.selectCountry();

        // then
        assertThat(selectedCountry).isEqualTo(country);
        assertThat(quiz.getCorrectCountryName()).isEqualTo(countryName);
        assertThat(quiz.getQuestionNumber()).isEqualTo(1);
        assertThat(quiz.alreadySelectedCountry(country)).isFalse();
    }

    @Test
    public void shouldSelectFirstCountryAvailable() {
        // given
        final String country = "Africa-Angola.png";
        final String countryName = "Angola";
        final String country2 = "Africa-Burkina_Faso.png";
        final Quiz quiz = new Quiz();
        quiz.addCountryToQuiz(country);
        quiz.addCountryToQuiz(country2);

        // when
        final String selectedCountry = quiz.selectCountry();

        // then
        assertThat(selectedCountry).isEqualTo(country);
        assertThat(quiz.getCorrectCountryName()).isEqualTo(countryName);
        assertThat(quiz.getQuestionNumber()).isEqualTo(1);
        assertThat(quiz.alreadySelectedCountry(country)).isFalse();
        assertThat(quiz.alreadySelectedCountry(country2)).isTrue();
    }

    @Test
    public void shouldAddCountryToQuiz() {
        // given
        final String country = "Africa-Angola.png";
        final Quiz quiz = new Quiz();

        // when
        quiz.addCountryToQuiz(country);

        // then
        assertThat(quiz.alreadySelectedCountry(country)).isTrue();
    }

    @Test
    public void shouldReturnTrueWhenCountryAlreadySelected() {
        // given
        final String country = "Africa-Angola.png";
        final Quiz quiz = new Quiz();
        quiz.addCountryToQuiz(country);

        // when
        final boolean isAlreadySelected = quiz.alreadySelectedCountry(country);

        // then
        assertThat(isAlreadySelected).isTrue();
    }

    @Test
    public void shouldReturnFalseWhenCountryAlreadySelected() {
        // given
        final String country = "Africa-Angola.png";
        final String country2 = "Africa-Egypt.png";
        final Quiz quiz = new Quiz();
        quiz.addCountryToQuiz(country);

        // when
        final boolean isAlreadySelected = quiz.alreadySelectedCountry(country2);

        // then
        assertThat(isAlreadySelected).isFalse();
    }

    @Test
    public void shouldAddCountry() {
        // given
        final String country = "Africa-Angola.png";
        final Quiz quiz = new Quiz();

        // when
        quiz.addCountry(country);

        // then
        assertThat(quiz.getCountriesSize()).isEqualTo(1);
        assertThat(quiz.getCountryAt(0)).isEqualTo(country);
    }

    @Test
    public void shouldGetCountry() {
        // given
        final String country = "Africa-Angola.png";
        final String country2 = "Africa-Egypt.png";
        final String country3 = "Africa-Algeria.png";

        final Quiz quiz = new Quiz();
        quiz.addCountry(country);
        quiz.addCountry(country2);
        quiz.addCountry(country3);

        // when
        final String selectedCountry = quiz.getCountryAt(1);

        // then
        assertThat(selectedCountry).isEqualTo(country2);
    }

    @Test
    public void shouldReturnCountrySize() {
        // given
        final String country = "Africa-Angola.png";
        final String country2 = "Africa-Egypt.png";
        final String country3 = "Africa-Algeria.png";

        final Quiz quiz = new Quiz();
        quiz.addCountry(country);
        quiz.addCountry(country2);
        quiz.addCountry(country3);

        // when
        final int countriesSize = quiz.getCountriesSize();

        // then
        assertThat(countriesSize).isEqualTo(3);
    }

    @Test
    public void shouldReturnTrueForLastAnswer() {
        // given
        final Quiz quiz = new Quiz();
        addCountries(10, quiz);
        selectCountries(10, quiz);

        // when
        final boolean isLastAnswer = quiz.isLastAnswer();

        // then
        assertThat(isLastAnswer).isTrue();
    }

    @Test
    public void shouldReturnFalseForNotLastAnswer() {
        // given
        final Quiz quiz = new Quiz();
        addCountries(10, quiz);
        selectCountries(9, quiz);

        // when
        final boolean isLastAnswer = quiz.isLastAnswer();

        // then
        assertThat(isLastAnswer).isFalse();
    }

    @Test
    public void shouldReturnTrueForCorrectAnswer() {
        // given
        final String country = "Africa-Angola.png";
        final String countryName = "Angola";
        final Quiz quiz = new Quiz();
        quiz.addCountryToQuiz(country);
        quiz.selectCountry();

        // when
        final boolean isCorrectAnswer = quiz.isCorrectAnswer(countryName);

        // then
        assertThat(isCorrectAnswer).isTrue();
    }

    @Test
    public void shouldReturnTrueForInCorrectAnswer() {
        // given
        final String country = "Africa-Angola.png";
        final String countryName = "Algeria";
        final Quiz quiz = new Quiz();
        quiz.addCountryToQuiz(country);
        quiz.selectCountry();

        // when
        final boolean isCorrectAnswer = quiz.isCorrectAnswer(countryName);

        // then
        assertThat(isCorrectAnswer).isFalse();
    }

    @Test
    public void shouldGetCountryNameWithUnderscore() {
        // given
        final String country = "Africa-Burkina_Faso.png";
        final String countryName = "Burkina Faso";
        final Quiz quiz = new Quiz();

        // when
        final String actualCountryName = quiz.getCountryName(country);

        // then
        assertThat(actualCountryName).isEqualTo(countryName);
    }

    @Test
    public void shouldGetCountryNameWithoutUnderscore() {
        // given
        final String country = "Africa-Benin.png";
        final String countryName = "Benin";
        final Quiz quiz = new Quiz();

        // when
        final String actualCountryName = quiz.getCountryName(country);

        // then
        assertThat(actualCountryName).isEqualTo(countryName);
    }

    private void selectCountries(int count, Quiz quiz) {
        for (int i = 0; i < count; i++) {
            quiz.selectCountry();
        }
    }

    private void addCountries(int count, Quiz quiz) {
        for (int i = 0; i < count; i++) {
            quiz.addCountryToQuiz("country" + i);
        }
    }
}
