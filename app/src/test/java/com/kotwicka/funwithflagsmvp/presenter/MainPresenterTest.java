package com.kotwicka.funwithflagsmvp.presenter;

import android.content.res.AssetManager;

import com.google.common.collect.Sets;
import com.kotwicka.funwithflagsmvp.contracts.QuizContract;
import com.kotwicka.funwithflagsmvp.model.Quiz;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {

    @Mock
    private QuizContract.View view;

    @Mock
    private Quiz quiz;

    @Mock
    private SecureRandom secureRandom;

    @Mock
    private AssetManager assetManager;

    @InjectMocks
    private MainPresenter mainPresenter;

    @Test
    public void shouldResetQuizAndLoadCountries() throws IOException {
        // given
        final String africa = "Africa";
        final String africaPath = "Africa-Algeria.png";
        final String africaPath2 = "Africa-Egypt.png";
        final String asia = "Asia";
        final String asiaPath = "Asia-Japan.png";
        final Set<String> regions = Sets.newHashSet(africa, asia);
        final int choices = 2;
        final int countriesCount = 3;

        // when
        when(assetManager.list(africa)).thenReturn(new String[]{africaPath, africaPath2});
        when(assetManager.list(asia)).thenReturn(new String[]{asiaPath});

        mainPresenter.loadCountries(regions, choices, assetManager);

        // then
        verify(quiz).resetQuiz();
        verify(quiz).setNumberOfChoices(choices);
        final ArgumentCaptor<String> countryPathCaptor = ArgumentCaptor.forClass(String.class);
        verify(quiz, times(countriesCount)).addCountry(countryPathCaptor.capture());
        final List<String> actualAddedCountries = countryPathCaptor.getAllValues();
        assertThat(actualAddedCountries).containsOnly(africaPath, africaPath2, asiaPath);
    }

    @Test
    public void shouldAddGivenNumberOfCountriesToQuiz() {
        // given
        final int countrySize = 4;
        final int questionSize = 3;
        final String country = "Europe-Poland.png";
        final String country2 = "Europe-France.png";
        final String country3 = "Europe-Italy.png";

        // when
        when(quiz.getCountriesSize()).thenReturn(countrySize);
        when(quiz.getNumberOfQuestions()).thenReturn(questionSize);
        when(quiz.getCountryAt(anyInt())).thenReturn(country).thenReturn(country2).thenReturn(country3);

        mainPresenter.selectCountriesForQuiz();

        // then
        final ArgumentCaptor<String> countryArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(quiz, times(questionSize)).alreadySelectedCountry(anyString());
        verify(quiz, times(questionSize)).addCountryToQuiz(countryArgumentCaptor.capture());
        final List<String> actualAddedCountries = countryArgumentCaptor.getAllValues();
        assertThat(actualAddedCountries).containsOnly(country, country2, country3);
        verify(secureRandom, times(questionSize)).nextInt(countrySize);
    }

    @Test
    public void shouldLoadNextQuestion() {
        // given
        final String nextCountry = "Africa-Algeria.png";
        final String expectedFlagPath = "Africa/Africa-Algeria.png";
        final int questionNumber = 5;
        final int numberOfQuestions = 7;

        // when
        when(quiz.selectCountry()).thenReturn(nextCountry);
        when(quiz.getQuestionNumber()).thenReturn(questionNumber);
        when(quiz.getNumberOfQuestions()).thenReturn(numberOfQuestions);

        mainPresenter.loadNextQuestion();

        // then
        verify(quiz).selectCountry();
        verify(view).setFlag(expectedFlagPath);
        verify(view).setQuestionNumber(questionNumber, numberOfQuestions);
    }

    @Test
    public void shouldSelectGivenNumberOfChoicesIncludingCorerctOne() {
        // given
        final int numberOfChoices = 4;
        final int countriesSize = 20;
        final String correctCountry = "Europe-Poland.png";
        final String incorrectCountry = "Europe-Italy.png";
        final String incorrectCountry2 = "Europe-France.png";
        final String incorrectCountry3 = "Europe-Greece.png";
        final String incorrectCountry4 = "Europe-Spain.png";
        final String correctCountryName = "Poland";
        final String incorrectCountryName = "Italy";
        final String incorrectCountry2Name = "France";
        final String incorrectCountry3Name = "Greece";
        final String incorrectCountry4Name = "Spain";

        // when
        when(quiz.getNumberOfChoices()).thenReturn(numberOfChoices);
        when(quiz.getCountriesSize()).thenReturn(countriesSize);
        when(quiz.getCountryAt(anyInt())).thenReturn(incorrectCountry).thenReturn(correctCountry).thenReturn(incorrectCountry2).thenReturn(incorrectCountry3).thenReturn(incorrectCountry4);
        when(quiz.getCountryName(correctCountry)).thenReturn(correctCountryName);
        when(quiz.getCountryName(incorrectCountry)).thenReturn(incorrectCountryName);
        when(quiz.getCountryName(incorrectCountry2)).thenReturn(incorrectCountry2Name);
        when(quiz.getCountryName(incorrectCountry3)).thenReturn(incorrectCountry3Name);
        when(quiz.getCountryName(incorrectCountry4)).thenReturn(incorrectCountry4Name);
        when(quiz.getCorrectCountryName()).thenReturn(correctCountryName);

        final List<String> countries = mainPresenter.selectPossibleAnswers();

        // then
        assertThat(countries).containsOnly(correctCountryName, incorrectCountryName, incorrectCountry2Name, incorrectCountry3Name);
    }

    @Test
    public void shouldSelectGivenNumberOfChoicesReplacingOneWithCorrectOne() {
        // given
        final int numberOfChoices = 4;
        final int countriesSize = 20;
        final String correctCountry = "Europe-Poland.png";
        final String incorrectCountry = "Europe-Italy.png";
        final String incorrectCountry2 = "Europe-France.png";
        final String incorrectCountry3 = "Europe-Greece.png";
        final String incorrectCountry4 = "Europe-Spain.png";
        final String correctCountryName = "Poland";
        final String incorrectCountryName = "Italy";
        final String incorrectCountry2Name = "France";
        final String incorrectCountry3Name = "Greece";
        final String incorrectCountry4Name = "Spain";

        // when
        when(quiz.getNumberOfChoices()).thenReturn(numberOfChoices);
        when(quiz.getCountriesSize()).thenReturn(countriesSize);
        when(quiz.getCountryAt(anyInt())).thenReturn(incorrectCountry).thenReturn(incorrectCountry2).thenReturn(incorrectCountry3).thenReturn(incorrectCountry4);
        when(quiz.getCountryName(correctCountry)).thenReturn(correctCountryName);
        when(quiz.getCountryName(incorrectCountry)).thenReturn(incorrectCountryName);
        when(quiz.getCountryName(incorrectCountry2)).thenReturn(incorrectCountry2Name);
        when(quiz.getCountryName(incorrectCountry3)).thenReturn(incorrectCountry3Name);
        when(quiz.getCountryName(incorrectCountry4)).thenReturn(incorrectCountry4Name);
        when(quiz.getCorrectCountryName()).thenReturn(correctCountryName);

        final List<String> countries = mainPresenter.selectPossibleAnswers();

        // then
        assertThat(countries).contains(correctCountryName).hasSize(4);
    }

    @Test
    public void shouldValidateChoice() {
        // given
        final String choice = "Poland";

        // when
        when(quiz.isCorrectAnswer(choice)).thenReturn(true);
        final boolean isValidChoice = mainPresenter.validateChoice(choice);

        // then
        verify(quiz).isCorrectAnswer(choice);
        assertThat(isValidChoice).isTrue();
    }

    @Test
    public void shouldCheckNumberOfGuesses() {
        // given
        final int totalNumberOfGuesses = 5;

        // when
        when(quiz.getNumberOfGuesses()).thenReturn(totalNumberOfGuesses);
        final int actualTotalNumberOfGuesses = mainPresenter.getTotalNumberOfGuesses();

        // then
        assertThat(actualTotalNumberOfGuesses).isEqualTo(totalNumberOfGuesses);
        verify(quiz).getNumberOfGuesses();
    }

    @Test
    public void shouldCheckIfIsLastAnswer() {
        // given
        final boolean isLastAnswer = true;

        // when
        when(quiz.isLastAnswer()).thenReturn(true);
        final boolean actualIsLastAnswer = mainPresenter.isLastAnswer();

        // then
        assertThat(actualIsLastAnswer).isEqualTo(isLastAnswer);
        verify(quiz).isLastAnswer();
    }
}
