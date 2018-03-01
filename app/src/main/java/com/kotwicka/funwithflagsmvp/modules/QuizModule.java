package com.kotwicka.funwithflagsmvp.modules;

import com.kotwicka.funwithflagsmvp.model.Quiz;
import com.kotwicka.funwithflagsmvp.view.MainActivity;

import java.security.SecureRandom;

import dagger.Module;
import dagger.Provides;

@Module
public class QuizModule {

    private final MainActivity mainActivity;

    public QuizModule(final MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Provides
    SecureRandom secureRandom() {
        return new SecureRandom();
    }

    @Provides
    MainActivity activity() {
        return mainActivity;
    }
}
