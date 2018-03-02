package com.kotwicka.funwithflagsmvp.contracts;

import android.content.Context;
import android.content.res.AssetManager;

import java.util.List;
import java.util.Set;

public interface QuizContract {
    interface View {
        void setFlag(final String flagPath);

        void setQuestionNumber(final int currentQuestionNumber, final int questionsNumber);

        Context getContext();
    }

    interface Presenter {
        void loadCountries(final Set<String> regions, final int choices, final AssetManager assets);

        void selectCountriesForQuiz();

        void loadNextQuestion();

        List<String> selectPossibleAnswers();

        boolean validateChoice(final String choice);

        boolean isLastAnswer();

        int getTotalNumberOfGuesses();
    }
}
