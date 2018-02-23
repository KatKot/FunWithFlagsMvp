package com.kotwicka.funwithflagsmvp.contracts;

import android.content.res.AssetManager;

import java.util.Set;

public interface QuizContract {
    interface View {
        void setFlag(final String flagPath);
        void setQuestionNumber(final int currentQuestionNumber, final int questionsNumber);
        void setCountryNameChoices(final Set<String> countryNames);
    }

    interface Presenter {
        void  initQuiz(final Set<String> regions, final AssetManager assets);
    }
}
