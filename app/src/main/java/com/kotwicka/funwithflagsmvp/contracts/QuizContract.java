package com.kotwicka.funwithflagsmvp.contracts;

import android.content.res.AssetManager;

import java.util.Set;

public interface QuizContract {
    interface View {

    }

    interface Presenter {
        void  initQuiz(final Set<String> regions, final AssetManager assets);
    }
}
