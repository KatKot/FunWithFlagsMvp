package com.kotwicka.funwithflagsmvp.presenter;

import android.content.res.AssetManager;

import com.kotwicka.funwithflagsmvp.contracts.QuizContract;
import com.kotwicka.funwithflagsmvp.model.Quiz;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyListOf;
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
    public void shouldInitializeQuiz() throws IOException {
        // given
        final String africa = "Africa";
        final String africaPath = "Africa-Algeria.png";
        final String africaPath2 = "Africa-Egypt.png";
        final String asia = "Asia";
        final String asiaPath = "Asia-Japan.png";
        final Set<String> regions = new HashSet<>();
        regions.add(africa);
        regions.add(asia);
        final int choices = 6;
        final int coutriesCount = 3;
        final int questionsCount = 2;
        final int questionNumber = 1;

        // when
        when(assetManager.list(africa)).thenReturn(new String [] {africaPath, africaPath2});
        when(assetManager.list(asia)).thenReturn(new String [] {asiaPath});
        when(quiz.getCountriesSize()).thenReturn(coutriesCount);
        when(quiz.getNumberOfQuestions()).thenReturn(questionsCount);
        when(quiz.selectCountry()).thenReturn(asiaPath);
        when(quiz.getQuestionNumber()).thenReturn(questionNumber);
        mainPresenter.initQuiz(regions, choices, assetManager);

        // then
        verify(quiz).resetQuiz();
        final ArgumentCaptor<String> countryPathCaptor = ArgumentCaptor.forClass(String.class);
        verify(quiz, times(coutriesCount)).addCountry(countryPathCaptor.capture());
        final List<String> actualAddedCountries = countryPathCaptor.getAllValues();
        assertThat(actualAddedCountries).containsOnly(africaPath, africaPath2, asiaPath);
        verify(quiz).setNumberOfChoices(choices);
        verify(quiz, times(questionsCount)).addCountryToQuiz(anyString());
        verify(quiz).selectCountry();
        verify(view).setQuestionNumber(questionNumber, questionsCount);
        verify(view).setFlag(anyString());
        verify(view).initializeChoices(anyListOf(String.class));
    }

    @Test
    public void shouldLoadNextFlag() {
        // given
        final String nextCountry = "Africa-Algeria.png";
        final String expectedFlagPath = "Africa/Africa-Algeria.png";
        final String correctCountryName = "Algeria";
        final String wrongCountryName = "Egypt";
        final int questionNumber = 5;
        final int numberOfQuestions = 7;
        final int numberOfChoices = 2;

        // when
        when(quiz.selectCountry()).thenReturn(nextCountry);
        when(quiz.getQuestionNumber()).thenReturn(questionNumber);
        when(quiz.getNumberOfQuestions()).thenReturn(numberOfQuestions);
        when(quiz.getNumberOfChoices()).thenReturn(numberOfChoices);
        when(quiz.getCountryName(anyString())).thenReturn(correctCountryName).thenReturn(wrongCountryName);
        when(quiz.getCorrectCountryName()).thenReturn(correctCountryName);

        mainPresenter.loadNextFlag();

        // then
        verify(view).setQuestionNumber(questionNumber, numberOfQuestions);
        verify(view).setFlag(expectedFlagPath);
        final ArgumentCaptor<List> choicesCaptor = ArgumentCaptor.forClass(List.class);
        verify(view).initializeChoices(choicesCaptor.capture());
        assertThat(choicesCaptor.getValue()).containsOnly(correctCountryName, wrongCountryName);
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
