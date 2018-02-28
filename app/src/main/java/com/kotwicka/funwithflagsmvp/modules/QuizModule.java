package com.kotwicka.funwithflagsmvp.modules;

import com.kotwicka.funwithflagsmvp.model.Quiz;

import java.security.SecureRandom;

import dagger.Module;
import dagger.Provides;

@Module
public class QuizModule {

    @Provides
    SecureRandom secureRandom() {
        return new SecureRandom();
    }
}
