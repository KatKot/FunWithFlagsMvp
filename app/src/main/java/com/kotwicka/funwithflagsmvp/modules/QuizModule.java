package com.kotwicka.funwithflagsmvp.modules;

import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.kotwicka.funwithflagsmvp.R;
import com.kotwicka.funwithflagsmvp.contracts.QuizContract;
import com.kotwicka.funwithflagsmvp.model.Quiz;
import com.kotwicka.funwithflagsmvp.presenter.MainPresenter;

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

    @Provides
    Handler handler() {
        return new Handler();
    }

    @Provides
    Animation shakeAnimation(QuizContract.View view) {
        return AnimationUtils.loadAnimation(view.getContext(), R.anim.incorrect_shake);
    }

    @Provides
    SharedPreferences sharedPreferences(QuizContract.View view) {
        return PreferenceManager.getDefaultSharedPreferences(view.getContext());
    }

}
