package com.kotwicka.funwithflagsmvp.modules;

import com.kotwicka.funwithflagsmvp.contracts.QuizContract;
import com.kotwicka.funwithflagsmvp.model.Quiz;
import com.kotwicka.funwithflagsmvp.presenter.MainPresenter;
import com.kotwicka.funwithflagsmvp.view.MainActivity;

import java.security.SecureRandom;

import dagger.Module;
import dagger.Provides;

@Module
public class QuizModule {

    private final QuizContract.View quizView;

    public QuizModule(final QuizContract.View quizView) {
        this.quizView = quizView;
    }

    @Provides
    SecureRandom secureRandom() {
        return new SecureRandom();
    }

    @Provides
    QuizContract.View activity() {
        return quizView;
    }

    @Provides
    QuizContract.Presenter presenter(QuizContract.View view, Quiz quiz, SecureRandom secureRandom) {
        return new MainPresenter(view, quiz, secureRandom);
    }

}
